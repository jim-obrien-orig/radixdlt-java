package com.radixdlt.client.core.atoms.particles;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.radix.common.ID.EUID;
import org.radix.serialization2.DsonOutput;
import com.radixdlt.client.atommodel.Accountable;
import com.radixdlt.client.atommodel.Identifiable;
import com.radixdlt.client.atommodel.accounts.RadixAddress;

import org.radix.serialization2.DsonOutput.Output;
import org.radix.serialization2.SerializerId2;
import org.radix.serialization2.client.NonVersionedSerializableObject;
import org.radix.serialization2.client.Serialize;

import com.radixdlt.client.core.atoms.RadixHash;

/**
 * A logical action on the ledger
 */
@SerializerId2("radix.particle")
public abstract class Particle extends NonVersionedSerializableObject {

	@JsonProperty("destinations")
	@DsonOutput(Output.ALL)
	private ImmutableSet<EUID> destinations;

	public Particle() {
		this.destinations = ImmutableSet.of();
	}

	public Particle(EUID destination) {
		Objects.requireNonNull(destination);
		this.destinations = ImmutableSet.of(destination);
	}

	public Particle(Set<EUID> destinations) {
		Objects.requireNonNull(destinations);
		this.destinations = ImmutableSet.copyOf(destinations);
	}

	public final Set<RadixAddress> getShardables() {
		Set<RadixAddress> addresses = new HashSet<>();

		if (this instanceof Accountable) {
			Accountable a = (Accountable) this;
			addresses.addAll(a.getAddresses());
		}

		if (this instanceof Identifiable) {
			Identifiable i = (Identifiable) this;
			addresses.add(i.getRRI().getAddress());
		}

		return new HashSet<>(addresses);
	}

	public final byte[] toDson() {
		return Serialize.getInstance().toDson(this, DsonOutput.Output.HASH);
	}

	public final RadixHash getHash() {
		return RadixHash.of(toDson());
	}

	public final EUID getHid() {
		return this.getHash().toEUID();
	}

	public Set<EUID> getDestinations() {
		return destinations;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Particle)) {
			return false;
		}

		Particle particle = (Particle) o;
		return this.getHash().equals(particle.getHash());
	}

	@Override
	public int hashCode() {
		return this.getHash().hashCode();
	}
}
