package com.radixdlt.client.examples;

import com.radixdlt.client.application.translate.tokens.TokenUnitConversions;
import java.math.BigDecimal;

import com.radixdlt.client.RadixApplicationAPI;
import com.radixdlt.client.application.identity.RadixIdentities;
import com.radixdlt.client.application.identity.RadixIdentity;
import com.radixdlt.client.application.translate.tokens.CreateTokenAction.TokenSupplyType;
import com.radixdlt.client.atommodel.accounts.RadixAddress;
import com.radixdlt.client.application.translate.tokens.TokenDefinitionReference;
import com.radixdlt.client.core.Bootstrap;

public class RadixWalletExample {

	private static String TO_ADDRESS_BASE58 = "JFgcgRKq6GbQqP8mZzDRhtr7K7YQM1vZiYopZLRpAeVxcnePRXX";
	//private static String TO_ADDRESS_BASE58 = null;
	private static String MESSAGE = "A gift for you!";
	private static BigDecimal AMOUNT = new BigDecimal("0.01");

	public static void main(String[] args) throws Exception {
		// Identity Manager which manages user's keys, signing, encrypting and decrypting
		final RadixIdentity radixIdentity;
		if (args.length > 0) {
			radixIdentity = RadixIdentities.loadOrCreateFile(args[0]);
		} else {
			radixIdentity = RadixIdentities.loadOrCreateFile("my.key");
		}

		RadixApplicationAPI api = RadixApplicationAPI.create(Bootstrap.LOCALHOST, radixIdentity);
		api.pull();

		System.out.println("My address: " + api.getMyAddress());
		System.out.println("My public key: " + api.getMyPublicKey());

		// Print out all past and future transactions
		api.getMyTokenTransfers()
			.subscribe(System.out::println);

		// Subscribe to current and future total balance
		api.getBalance(api.getMyAddress())
			.subscribe(balance -> System.out.println("My Balance:\n" + balance));

		api.createToken(
			"Joshy Token",
			"JOSH",
			"The Best Coin Ever",
			BigDecimal.valueOf(10000),
			TokenUnitConversions.getMinimumGranularity(),
			TokenSupplyType.MUTABLE
		).toObservable().subscribe(System.out::println);

		api.getTokenClass(TokenDefinitionReference.of(api.getMyAddress(), "JOSH"))
			.subscribe(System.out::println);

		// If specified, send money to another address
		if (TO_ADDRESS_BASE58 != null) {
			RadixAddress toAddress = RadixAddress.from(TO_ADDRESS_BASE58);
			api.transferTokens(toAddress, AMOUNT, api.getNativeTokenRef(), "Test Message").toObservable()
				.subscribe(System.out::println, Throwable::printStackTrace);
		}
	}
}
