package com.radixdlt.client.application.translate;

import com.radixdlt.client.assets.Asset;
import com.radixdlt.client.assets.Amount;

public class InsufficientFundsException extends Exception {
	private final Asset asset;
	private final long available;
	private final long requestedAmount;

	public InsufficientFundsException(Asset asset, long available, long requestedAmount) {
		super("Requested " + Amount.subUnitsOf(requestedAmount, asset)
			+ " but only " + Amount.subUnitsOf(available, asset) + " available.");
		this.asset = asset;
		this.available = available;
		this.requestedAmount = requestedAmount;
	}

	public long getAvailable() {
		return available;
	}

	public long getRequestedAmount() {
		return requestedAmount;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof InsufficientFundsException)) {
			return false;
		}

		InsufficientFundsException o = (InsufficientFundsException) obj;
		return this.asset.equals(o.asset) && this.available == o.available && this.requestedAmount == o.requestedAmount;
	}

	@Override
	public int hashCode() {
		return this.getMessage().hashCode();
	}
}
