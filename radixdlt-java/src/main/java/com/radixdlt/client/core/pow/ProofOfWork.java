package com.radixdlt.client.core.pow;

import com.radixdlt.client.core.atoms.RadixHash;

import java.nio.ByteBuffer;
import org.bouncycastle.util.encoders.Base64;
import org.radix.utils.primitives.Bytes;

public class ProofOfWork {
	private final long nonce;
	private final int magic;
	private final byte[] seed;
	private final byte[] target;
	public final byte[] work;

	public ProofOfWork(long nonce, int magic, byte[] seed, byte[] target, byte[] work) {
		this.nonce = nonce;
		this.magic = magic;
		this.seed = seed;
		this.target = target;
		this.work = work;
	}

	public String getTargetHex() {
		return Bytes.toHexString(target);
	}

	public long getNonce() {
		return nonce;
	}

	public void validate() throws ProofOfWorkException {
		String targetHex = getTargetHex();
		ByteBuffer byteBuffer = ByteBuffer.allocate(4 + 32 + Long.BYTES);
		byteBuffer.putInt(magic);
		byteBuffer.put(seed);
		byteBuffer.putLong(nonce);
		String hashHex = Bytes.toHexString(RadixHash.of(byteBuffer.array()).toByteArray());
		if (hashHex.compareTo(targetHex) > 0) {
			throw new ProofOfWorkException(hashHex, targetHex);
		}
	}

	@Override
	public String toString() {
		return "POW: nonce(" + nonce + ") magic(" + magic + ") seed(" + Base64.toBase64String(seed) + ") target(" + getTargetHex() + ")";
	}
}
