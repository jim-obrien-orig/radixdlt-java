package com.radixdlt.client.application.translate.tokenclasses;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.radix.utils.UInt256;
import org.radix.utils.UInt256s;

import com.radixdlt.client.application.translate.Action;
import com.radixdlt.client.application.translate.ApplicationState;
import com.radixdlt.client.application.translate.StatefulActionToParticlesMapper;
import com.radixdlt.client.application.translate.tokens.InsufficientFundsException;
import com.radixdlt.client.application.translate.tokens.TokenBalanceState;
import com.radixdlt.client.application.translate.tokens.TokenBalanceState.Balance;
import com.radixdlt.client.atommodel.accounts.RadixAddress;
import com.radixdlt.client.atommodel.quarks.FungibleQuark.FungibleType;
import com.radixdlt.client.atommodel.tokens.OwnedTokensParticle;
import com.radixdlt.client.atommodel.tokens.TokenClassReference;
import com.radixdlt.client.core.RadixUniverse;
import com.radixdlt.client.core.atoms.particles.SpunParticle;
import com.radixdlt.client.core.crypto.ECKeyPair;

import io.reactivex.Observable;

public class BurnTokensActionMapper implements StatefulActionToParticlesMapper {
	private final RadixUniverse universe;

	public BurnTokensActionMapper(RadixUniverse universe) {
		this.universe = universe;
	}

	@Override
	public Observable<RequiredShardState> requiredState(Action action) {
		if (!(action instanceof BurnTokensAction)) {
			return Observable.empty();
		}

		BurnTokensAction burnTokensAction = (BurnTokensAction) action;

		RadixAddress tokenClassAddress = burnTokensAction.getTokenClassReference().getAddress();

		return Observable.just(new RequiredShardState(TokenBalanceState.class, tokenClassAddress));
	}

	@Override
	public Observable<SpunParticle> mapToParticles(Action action, Observable<Observable<? extends ApplicationState>> store) {
		if (!(action instanceof BurnTokensAction)) {
			return Observable.empty();
		}

		BurnTokensAction burnTokensAction = (BurnTokensAction) action;
		return store.firstOrError()
			.flatMapObservable(s -> s)
			.map(TokenBalanceState.class::cast)
			.firstOrError()
			.toObservable()
			.flatMapIterable(state -> map(burnTokensAction, state));
	}

	private List<SpunParticle> map(BurnTokensAction burnTokensAction, TokenBalanceState curState) {
		final Map<TokenClassReference, Balance> allConsumables = curState.getBalance();

		final TokenClassReference tokenRef = burnTokensAction.getTokenClassReference();
		final Balance bal = allConsumables.get(burnTokensAction.getTokenClassReference());
		final BigDecimal balance = bal == null ? BigDecimal.ZERO : bal.getAmount();
		final BigDecimal burnUnits = TokenClassReference.subunitsToUnits(burnTokensAction.getAmount());
		if (bal == null || balance.compareTo(burnUnits) < 0) {
			throw new InsufficientFundsException(tokenRef, balance, burnUnits);
		}
		final UInt256 granularity = TokenClassReference.unitsToSubunits(bal.getGranularity());

		final List<OwnedTokensParticle> unconsumedOwnedTokensParticles =
			Optional.ofNullable(allConsumables.get(burnTokensAction.getTokenClassReference()))
				.map(b -> b.unconsumedConsumables().collect(Collectors.toList()))
				.orElse(Collections.emptyList());

		List<SpunParticle> particles = new ArrayList<>();

		UInt256 consumerTotal = UInt256.ZERO;
		final UInt256 subunitAmount = burnTokensAction.getAmount();
		Iterator<OwnedTokensParticle> iterator = unconsumedOwnedTokensParticles.iterator();
		Map<ECKeyPair, UInt256> newUpQuantities = new HashMap<>();

		// HACK for now
		// TODO: remove this, create a ConsumersCreator
		// TODO: randomize this to decrease probability of collision
		while (consumerTotal.compareTo(subunitAmount) < 0 && iterator.hasNext()) {
			final UInt256 left = subunitAmount.subtract(consumerTotal);

			OwnedTokensParticle particle = iterator.next();
			UInt256 particleAmount = particle.getAmount();
			consumerTotal = consumerTotal.add(particleAmount);

			final UInt256 amount = UInt256s.min(left, particleAmount);
			particle.addConsumerQuantities(amount, null, newUpQuantities);

			SpunParticle<OwnedTokensParticle> down = SpunParticle.down(particle);
			particles.add(down);
		}

		newUpQuantities.entrySet().stream()
			.map(e -> new OwnedTokensParticle(
				e.getValue(),
				granularity,
				e.getKey() == null ? FungibleType.BURNED : FungibleType.TRANSFERRED,
				e.getKey() == null ? burnTokensAction.getTokenClassReference().getAddress()
					: universe.getAddressFrom(e.getKey().getPublicKey()),
				System.nanoTime(),
				burnTokensAction.getTokenClassReference(),
				System.currentTimeMillis() / 60000L + 60000L
			))
			.map(SpunParticle::up)
			.forEach(particles::add);
		return particles;
	}
}