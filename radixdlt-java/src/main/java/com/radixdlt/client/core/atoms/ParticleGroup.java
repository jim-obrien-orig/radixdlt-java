package com.radixdlt.client.core.atoms;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.radixdlt.client.core.atoms.particles.Particle;
import com.radixdlt.client.core.atoms.particles.Spin;
import com.radixdlt.client.core.atoms.particles.SpunParticle;
import org.radix.serialization2.DsonOutput;
import org.radix.serialization2.SerializerId2;
import org.radix.serialization2.client.SerializableObject;

import java.util.*;
import java.util.stream.Stream;

/**
 * A group of particles representing one intent, e.g. a transfer.
 * <p>
 * * @author flotothemoon
 */
@SerializerId2("PARTICLEGROUP")
public class ParticleGroup extends SerializableObject {
	@JsonProperty("particles")
	@DsonOutput(DsonOutput.Output.ALL)
	private final ImmutableList<SpunParticle> particles;


	@JsonProperty("metaData")
	@DsonOutput(DsonOutput.Output.ALL)
	private Map<String, String> metaData = new HashMap<>();


	private ParticleGroup() {
		this.particles = ImmutableList.of();
	}

	public ParticleGroup(Iterable<SpunParticle> particles) {
		Objects.requireNonNull(particles, "particles is required");

		this.particles = ImmutableList.copyOf(particles);
	}

	public ParticleGroup(Iterable<SpunParticle> particles, Map<String, String> metaData) {
		Objects.requireNonNull(particles, "particles is required");
		Objects.requireNonNull(metaData, "metaData is required");

		this.particles = ImmutableList.copyOf(particles);
		this.metaData.putAll(metaData);
	}



	/**
	 * Get a stream of the spun particles in this group
	 */
	public final Stream<SpunParticle> spunParticles() {
		return this.particles.stream();
	}

	/**
	 * Get a stream of particles of a certain spin in this group
	 *
	 * @param spin The spin to filter by
	 * @return The particles in this group with that spin
	 */
	public final Stream<Particle> particles(Spin spin) {
		return this.spunParticles().filter(p -> p.getSpin() == spin).map(SpunParticle::getParticle);
	}

	/**
	 * Get a {@link ParticleGroup} consisting of the given particles
	 */
	public static ParticleGroup of(Iterable<SpunParticle> particles) {
		Objects.requireNonNull(particles, "particles is required");

		return new ParticleGroup(ImmutableList.copyOf(particles));
	}

	/**
	 * Get a {@link ParticleGroup} consisting of the given particles
	 */
	public static ParticleGroup of(SpunParticle<?>... particles) {
		Objects.requireNonNull(particles, "particles is required");

		return new ParticleGroup(ImmutableList.copyOf(particles));
	}

	/**
	 * Whether this {@link ParticleGroup} contains any particles
	 */
	public boolean hasParticles() {
		return !this.particles.isEmpty();
	}

	/** Get the metadata associated with the particle group
	 * @return a map of the metadata
	 */
	public Map<String, String> getMetaData() {
		return this.metaData;
	}

	/**
	 * Get a build for a single {@link ParticleGroup}
	 * @return The {@link ParticleGroupBuilder}
	 */
	public static ParticleGroupBuilder builder() {
		return new ParticleGroupBuilder();
	}

	/**
	 * A builder for immutable {@link ParticleGroup}s
	 */
	public static class ParticleGroupBuilder {
		private List<SpunParticle> particles = new ArrayList<>();
		private Map<String, String> metaData = new HashMap<>();

		private ParticleGroupBuilder() {
		}

		public final ParticleGroupBuilder addParticle(SpunParticle<?> spunParticle) {
			Objects.requireNonNull(spunParticle, "spunParticle is required");

			this.particles.add(spunParticle);

			return this;
		}

		public final ParticleGroupBuilder addParticle(Particle particle, Spin spin) {
			Objects.requireNonNull(particle, "particle is required");
			Objects.requireNonNull(spin, "spin is required");

			SpunParticle<?> spunParticle = SpunParticle.of(particle, spin);
			this.particles.add(spunParticle);

			return this;
		}

		public final ParticleGroupBuilder addMetaData(String key, String value) {
			Objects.requireNonNull(key, "key is required");
			Objects.requireNonNull(value, "value is required");

			this.metaData.put(key, value);

			return this;
		}


		public ParticleGroup build() {
			return new ParticleGroup(ImmutableList.copyOf(this.particles), metaData);
		}
	}
}
