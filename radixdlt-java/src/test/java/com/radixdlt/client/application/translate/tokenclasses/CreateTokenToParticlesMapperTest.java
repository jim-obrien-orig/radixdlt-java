package com.radixdlt.client.application.translate.tokenclasses;

import java.util.List;

import org.junit.Test;
import org.radix.utils.UInt256;

import com.radixdlt.client.application.translate.tokenclasses.CreateTokenAction.TokenSupplyType;
import com.radixdlt.client.atommodel.accounts.RadixAddress;
import com.radixdlt.client.atommodel.tokens.OwnedTokensParticle;
import com.radixdlt.client.atommodel.tokens.TokenParticle;
import com.radixdlt.client.core.atoms.particles.SpunParticle;
import com.radixdlt.client.core.crypto.ECPublicKey;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.reactivex.observers.TestObserver;

public class CreateTokenToParticlesMapperTest {
	@Test
	public void testNormalConstruction() {
		CreateTokenAction tokenCreation = mock(CreateTokenAction.class);
		RadixAddress address = mock(RadixAddress.class);
		ECPublicKey key = mock(ECPublicKey.class);
		when(address.getPublicKey()).thenReturn(key);
		when(tokenCreation.getAddress()).thenReturn(address);
		when(tokenCreation.getIso()).thenReturn("ISO");
		when(tokenCreation.getInitialSupply()).thenReturn(UInt256.ONE);
		when(tokenCreation.getTokenSupplyType()).thenReturn(TokenSupplyType.MUTABLE);

		CreateTokenToParticlesMapper createTokenToParticlesMapper = new CreateTokenToParticlesMapper();
		TestObserver<List<SpunParticle>> testObserver = TestObserver.create();
		createTokenToParticlesMapper.mapToParticles(tokenCreation).toList().subscribe(testObserver);
		testObserver.assertValue(particles ->
			particles.stream().anyMatch(s -> s.getParticle() instanceof TokenParticle)
				&& particles.stream().anyMatch(s -> s.getParticle() instanceof OwnedTokensParticle)
				&& particles.size() == 2
		);
	}
}