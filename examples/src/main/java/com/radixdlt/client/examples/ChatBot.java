package com.radixdlt.client.examples;

import com.radixdlt.client.RadixApplicationAPI;
import com.radixdlt.client.application.identity.RadixIdentities;
import com.radixdlt.client.application.translate.data.DecryptedMessage;
import com.radixdlt.client.core.Bootstrap;
import com.radixdlt.client.application.identity.RadixIdentity;
import com.radixdlt.client.dapps.messaging.RadixMessaging;
import io.reactivex.Completable;
import java.sql.Timestamp;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This is an example of a ChatBot service which uses the RadixMessaging module
 * to chat with users in a Radix Universe.
 */
public class ChatBot {

	private final RadixApplicationAPI api;

	private final RadixMessaging messaging;

	/**
	 * The chat algorithm to run on each new conversation
	 *
	 * TODO: make this asynchronous via Observers/Observables
	 */
	private final Supplier<Function<String,String>> chatBotAlgorithmSupplier;

	public ChatBot(RadixApplicationAPI api, Supplier<Function<String,String>> chatBotAlgorithmSupplier) {
		this.api = api;
		this.messaging = new RadixMessaging(api);
		this.chatBotAlgorithmSupplier = chatBotAlgorithmSupplier;
	}

	/**
	 * Connect to the network and begin running the service
	 */
	public void run() {
		System.out.println("Chatbot address: " + api.getMyAddress());

		// Subscribe/Decrypt messages
		messaging
			.getAllMessagesGroupedByParticipants()
			.flatMapCompletable(convo -> convo
				.doOnNext(message -> System.out.println("Received at " + new Timestamp(System.currentTimeMillis()) + ": " + message)) // Print messages
				.filter(message -> !message.getFrom().equals(api.getMyAddress())) // Don't reply to ourselves!
				.filter(message -> Math.abs(message.getTimestamp() - System.currentTimeMillis()) < 60000) // Only reply to recent messages
				.flatMapCompletable(new io.reactivex.functions.Function<DecryptedMessage, Completable>() {
					Function<String,String> chatBotAlgorithm = chatBotAlgorithmSupplier.get();

					@Override
					public Completable apply(DecryptedMessage message) {
						return messaging.sendMessage(chatBotAlgorithm.apply(new String(message.getData())), message.getFrom()).toCompletable();
					}
				})
			).subscribe(
				System.out::println,
				Throwable::printStackTrace
			);
	}

	public static void main(String[] args) throws Exception {
		// Setup Identity of Chatbot
		RadixIdentity radixIdentity = RadixIdentities.loadOrCreateFile("chatbot.key");

		RadixApplicationAPI api = RadixApplicationAPI.create(Bootstrap.LOCALHOST, radixIdentity);

		ChatBot chatBot = new ChatBot(api, () -> new Function<String, String>() {
			int messageCount = 0;

			@Override
			public String apply(String s) {
				switch(messageCount++) {
					case 0: return "Who dis?";
					case 1: return "Howdy " + s;
					case 5: return "Chillz out yo";
					default: return "I got nothing more to say";
				}
			}
		});
		chatBot.run();
	}
}
