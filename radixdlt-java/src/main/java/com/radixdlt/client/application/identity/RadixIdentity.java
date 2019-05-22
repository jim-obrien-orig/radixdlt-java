package com.radixdlt.client.application.identity;

import com.radixdlt.client.core.atoms.Atom;
import com.radixdlt.client.core.crypto.ECPublicKey;
import io.reactivex.Single;

public interface RadixIdentity {
	Single<Atom> addSignature(Atom atom);

	/**
	 * Transforms a possibly encrypted bytes object into an unencrypted one.
	 * If decryption fails then return an empty Maybe.
	 * @param data bytes to transform
	 * @return either the unencrypted version of the bytes or an error
	 */
	Single<UnencryptedData> decrypt(Data data);
	ECPublicKey getPublicKey();
}
