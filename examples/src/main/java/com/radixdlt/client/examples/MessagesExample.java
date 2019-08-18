package com.radixdlt.client.examples;

import com.radixdlt.client.application.RadixApplicationAPI;
import com.radixdlt.client.application.RadixApplicationAPI.Result;
import com.radixdlt.client.application.identity.RadixIdentities;
import com.radixdlt.client.application.identity.RadixIdentity;
import com.radixdlt.client.atommodel.accounts.RadixAddress;
import com.radixdlt.client.core.Bootstrap;
import org.radix.utils.RadixConstants;


public class MessagesExample {
	public static void main(String[] args) {
		// Create a new public key identity
		final RadixIdentity radixIdentity = RadixIdentities.createNew();

		System.out.println("Public key: " + radixIdentity.getPublicKey());

		// Initialize api layer
		RadixApplicationAPI api = RadixApplicationAPI.create(Bootstrap.LOCALHOST, radixIdentity);

		// Sync with network
		api.pull();

		System.out.println("My address: " + api.getAddress());
		System.out.println("My public key: " + api.getPublicKey());


		// Print out all past and future messages
		api.observeMessages().subscribe(System.out::println);

		// Send a message to an address
		RadixAddress toAddress = RadixAddress.from("JEbhKQzBn4qJzWJFBbaPioA2GTeaQhuUjYWkanTE6N8VvvPpvM8");
		Result result = api.sendMessage(toAddress, "Hello".getBytes(RadixConstants.STANDARD_CHARSET), true);
		result.blockUntilComplete();
	}
}
