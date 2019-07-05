package com.radixdlt.client.application.translate.tokens;

import com.radixdlt.client.application.translate.data.receipt.Receipt;
import com.radixdlt.client.atommodel.accounts.RadixAddress;
import com.radixdlt.client.core.atoms.particles.RRI;
import java.math.BigDecimal;
import java.util.Optional;

import org.checkerframework.checker.nullness.Opt;
import org.json.JSONObject;
import org.radix.serialization2.client.Serialize;
import org.radix.utils.RadixConstants;
import org.radix.utils.primitives.Bytes;

import javax.swing.text.html.Option;

public class TokenTransfer {
	private final RadixAddress from;
	private final RadixAddress to;
	private final RRI tokenDefinition;
	private final BigDecimal amount;
	private final byte[] attachment;
	private final long timestamp;

	public TokenTransfer(
		RadixAddress from,
		RadixAddress to,
		RRI tokenDefinition,
		BigDecimal amount,
		byte[] attachment,
		long timestamp
	) {
		this.from = from;
		this.to = to;
		this.tokenDefinition = tokenDefinition;
		this.amount = amount;
		this.attachment = attachment;
		this.timestamp = timestamp;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public RadixAddress getFrom() {
		return from;
	}

	public RadixAddress getTo() {
		return to;
	}

	public RRI getTokenClass() {
		return tokenDefinition;
	}

	public Optional<byte[]> getAttachment() {
		return Optional.ofNullable(attachment);
	}

	public Optional<String> getAttachmentAsString() {
		return getAttachment().map(a -> new String(a, RadixConstants.STANDARD_CHARSET));
	}

	public long getTimestamp() {
		return timestamp;
	}

	public Optional<Receipt> getReceiptFromAttachment() {
		if (attachment != null)
		{
			Optional<Receipt> maybeReceipt = Optional.ofNullable(Receipt.fromSerializedJsonBytes(attachment));
			return maybeReceipt;
		} else {
			return Optional.empty();
		}
	}

	public boolean hasReceipt() {
		return getReceiptFromAttachment().isPresent();
	}

	@Override
	public String toString() {
		return timestamp + " " + from + " -> " + to + " " + amount
			+ (attachment == null ? "" : " " + attachment);
	}
}
