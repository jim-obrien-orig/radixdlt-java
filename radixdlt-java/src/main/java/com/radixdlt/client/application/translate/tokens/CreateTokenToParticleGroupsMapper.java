package com.radixdlt.client.application.translate.tokens;

import com.google.common.collect.ImmutableMap;
import com.radixdlt.client.application.translate.StatelessActionToParticleGroupsMapper;
import com.radixdlt.client.application.translate.tokens.CreateTokenAction.TokenSupplyType;
import com.radixdlt.client.atommodel.rri.RRIParticle;
import com.radixdlt.client.atommodel.tokens.TokenDefinitionParticle;
import com.radixdlt.client.atommodel.tokens.TokenDefinitionParticle.TokenTransition;
import com.radixdlt.client.atommodel.tokens.TokenPermission;
import com.radixdlt.client.atommodel.tokens.TransferrableTokensParticle;
import com.radixdlt.client.atommodel.tokens.UnallocatedTokensParticle;
import com.radixdlt.client.core.atoms.ParticleGroup;
import com.radixdlt.client.core.atoms.ParticleGroup.ParticleGroupBuilder;
import com.radixdlt.client.core.atoms.particles.Spin;
import com.radixdlt.client.core.atoms.particles.SpunParticle;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.radix.utils.UInt256;

/**
 * Maps the CreateToken action into it's corresponding particles
 */
public class CreateTokenToParticleGroupsMapper implements StatelessActionToParticleGroupsMapper<CreateTokenAction> {
	@Override
	public List<ParticleGroup> mapToParticleGroups(CreateTokenAction tokenCreation) {
		final TokenPermission mintPermissions;
		final TokenPermission burnPermissions;

		if (tokenCreation.getTokenSupplyType().equals(TokenSupplyType.FIXED)) {
			mintPermissions = TokenPermission.TOKEN_CREATION_ONLY;
			burnPermissions = TokenPermission.NONE;
		} else if (tokenCreation.getTokenSupplyType().equals(TokenSupplyType.MUTABLE)) {
			mintPermissions = TokenPermission.TOKEN_OWNER_ONLY;
			burnPermissions = TokenPermission.TOKEN_OWNER_ONLY;
		} else {
			throw new IllegalStateException("Unknown supply type: " + tokenCreation.getTokenSupplyType());
		}

		TokenDefinitionParticle token = new TokenDefinitionParticle(
			tokenCreation.getTokenRRI().getAddress(),
			tokenCreation.getName(),
			tokenCreation.getTokenRRI().getName(),
			tokenCreation.getDescription(),
			TokenUnitConversions.unitsToSubunits(tokenCreation.getGranularity()),
			ImmutableMap.of(
				TokenTransition.MINT, mintPermissions,
				TokenTransition.BURN, burnPermissions
			),
			null
		);

		UnallocatedTokensParticle unallocated = new UnallocatedTokensParticle(
			UInt256.MAX_VALUE,
			TokenUnitConversions.unitsToSubunits(tokenCreation.getGranularity()),
			System.currentTimeMillis(),
			token.getRRI(),
			token.getTokenPermissions()
		);

		RRIParticle rriParticle = new RRIParticle(token.getRRI());
		ParticleGroup tokenCreationGroup = ParticleGroup.of(
			SpunParticle.down(rriParticle),
			SpunParticle.up(token),
			SpunParticle.up(unallocated)
		);

		if (tokenCreation.getInitialSupply().compareTo(BigDecimal.ZERO) == 0) {
			// No initial supply -> just the token particle
			return Collections.singletonList(
				tokenCreationGroup
			);
		}

		TransferrableTokensParticle minted = new TransferrableTokensParticle(
			TokenUnitConversions.unitsToSubunits(tokenCreation.getInitialSupply()),
			TokenUnitConversions.unitsToSubunits(tokenCreation.getGranularity()),
			tokenCreation.getTokenRRI().getAddress(),
			System.nanoTime(),
			token.getRRI(),
			System.currentTimeMillis() / 60000L + 60000,
			token.getTokenPermissions()
		);

		ParticleGroupBuilder mintGroupBuilder = ParticleGroup.builder()
			.addParticle(unallocated, Spin.DOWN)
			.addParticle(minted, Spin.UP);

		final UInt256 leftOver = UInt256.MAX_VALUE.subtract(TokenUnitConversions.unitsToSubunits(tokenCreation.getInitialSupply()));

		if (!leftOver.isZero()) {
			UnallocatedTokensParticle unallocatedLeftOver = new UnallocatedTokensParticle(
				leftOver,
				TokenUnitConversions.unitsToSubunits(tokenCreation.getGranularity()),
				System.currentTimeMillis(),
				token.getRRI(),
				token.getTokenPermissions()
			);

			mintGroupBuilder.addParticle(unallocatedLeftOver, Spin.UP);
		}

		return Arrays.asList(
			tokenCreationGroup,
			mintGroupBuilder.build()
		);
	}
}
