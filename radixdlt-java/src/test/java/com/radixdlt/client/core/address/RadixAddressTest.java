package com.radixdlt.client.core.address;

import com.radixdlt.client.atommodel.accounts.RadixAddress;
import java.util.Arrays;
import java.util.List;

import org.bouncycastle.util.encoders.Base64;
import org.junit.Test;
import org.radix.common.ID.EUID;

import com.radixdlt.client.core.crypto.ECPublicKey;

import static org.junit.Assert.assertEquals;

public class RadixAddressTest {

	@Test
	public void createAddressFromPublicKey() {
		ECPublicKey publicKey = new ECPublicKey(Base64.decode("A455PdOZNwyRWaSWFXyYYkbj7Wv9jtgCCqUYhuOHiPLC"));
		RadixAddress address = new RadixAddress(RadixUniverseConfigs.getBetanet(), publicKey);
		assertEquals("JHB89drvftPj6zVCNjnaijURk8D8AMFw4mVja19aoBGmRYwsmGW", address.toString());
		assertEquals(address, RadixAddress.from("JHB89drvftPj6zVCNjnaijURk8D8AMFw4mVja19aoBGmRYwsmGW"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void createAddressFromBadPublicKey() {
		ECPublicKey publicKey = new ECPublicKey(Base64.decode("BADKEY"));
		new RadixAddress(RadixUniverseConfigs.getBetanet(), publicKey);
	}

	@Test
	public void createAddressAndCheckUID() {
		RadixAddress address = new RadixAddress("JHB89drvftPj6zVCNjnaijURk8D8AMFw4mVja19aoBGmRYwsmGW");
		assertEquals(new EUID("8022a3d56d67d35da4150b036cedfa6c"), address.getUID());
	}

	@Test
	public void generateAddress() {
		new RadixAddress(RadixUniverseConfigs.getBetanet(), new ECPublicKey(new byte[33]));
	}

	@Test
	public void testAddresses() {
		List<String> addresses = Arrays.asList(
			"JHB89drvftPj6zVCNjnaijURk8D8AMFw4mVja19aoBGmRYwsmGW"
		);

		addresses.forEach(address -> {
			RadixAddress.from(address);
		});
	}
}