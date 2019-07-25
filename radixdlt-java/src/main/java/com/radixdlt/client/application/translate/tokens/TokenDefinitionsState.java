package com.radixdlt.client.application.translate.tokens;

import com.google.common.collect.ImmutableMap;
import com.radixdlt.client.application.translate.ApplicationState;
import com.radixdlt.client.application.translate.tokens.TokenState.TokenSupplyType;

import com.radixdlt.client.atommodel.tokens.UnallocatedTokensParticle;
import com.radixdlt.client.core.atoms.particles.RRI;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.radix.utils.UInt256;

public class TokenDefinitionsState implements ApplicationState {
	private final ImmutableMap<RRI, TokenState> state;

	private TokenDefinitionsState(ImmutableMap<RRI, TokenState> state) {
		this.state = state;
	}

	public static TokenDefinitionsState init() {
		return new TokenDefinitionsState(ImmutableMap.of());
	}

	public Map<RRI, TokenState> getState() {
		return state;
	}

	public static TokenDefinitionsState combine(TokenDefinitionsState state0, TokenDefinitionsState state1) {
		HashMap<RRI, TokenState> newState = new HashMap<>(state0.getState());
		state1.getState().forEach((rri, tokenState1) ->
			newState.merge(rri, tokenState1, TokenState::combine)
		);

		return new TokenDefinitionsState(ImmutableMap.copyOf(newState));
	}

	public TokenDefinitionsState mergeUnallocated(UnallocatedTokensParticle unallocatedTokensParticle) {
		RRI ref = unallocatedTokensParticle.getTokDefRef();

		HashMap<RRI, TokenState> newState = new HashMap<>(state);
		final TokenState tokenState = state.get(ref);
		if (tokenState == null) {
			newState.put(ref, new TokenState(
				null,
				null,
				null,
				null,
				TokenUnitConversions.subunitsToUnits(UInt256.MAX_VALUE.subtract(unallocatedTokensParticle.getAmount())),
				TokenUnitConversions.subunitsToUnits(unallocatedTokensParticle.getGranularity()),
				null
			));
			return new TokenDefinitionsState(ImmutableMap.copyOf(newState));
		}

		newState.put(ref, new TokenState(
			tokenState.getName(),
			tokenState.getIso(),
			tokenState.getDescription(),
			tokenState.getIconUrl(),
			Optional.ofNullable(tokenState.getTotalSupply())
				.orElse(TokenUnitConversions.subunitsToUnits(UInt256.MAX_VALUE))
				.subtract(TokenUnitConversions.subunitsToUnits(unallocatedTokensParticle.getAmount())),
			tokenState.getGranularity(),
			tokenState.getTokenSupplyType()
		));

		return new TokenDefinitionsState(ImmutableMap.copyOf(newState));
	}

	public TokenDefinitionsState mergeTokenClass(
		RRI ref,
		String name,
		String iso,
		String description,
		String iconUrl,
		BigDecimal supply,
		BigDecimal granularity,
		TokenSupplyType tokenSupplyType
	) {
		HashMap<RRI, TokenState> newState = new HashMap<>(state);

		if (state.containsKey(ref)) {
			final TokenState curState = state.get(ref);
			final BigDecimal totalSupply = curState.getTotalSupply();

			newState.put(ref, new TokenState(name, iso, description, iconUrl, totalSupply, granularity, tokenSupplyType));
		} else {
			newState.put(ref, new TokenState(name, iso, description, iconUrl, supply, granularity, tokenSupplyType));
		}

		return new TokenDefinitionsState(ImmutableMap.copyOf(newState));
	}

	@Override
	public int hashCode() {
		return state.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof TokenDefinitionsState)) {
			return false;
		}

		TokenDefinitionsState t = (TokenDefinitionsState) o;
		return t.state.equals(this.state);
	}

	@Override
	public String toString() {
		return state.toString();
	}
}
