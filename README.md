# radixdlt-java

[![](https://jitpack.io/v/com.radixdlt/radixdlt-java.svg)](https://jitpack.io/#com.radixdlt/radixdlt-java) [![Build Status](https://travis-ci.com/radixdlt/radixdlt-java.svg?branch=master)](https://travis-ci.com/radixdlt/radixdlt-java) [![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=com.radixdlt%3Aradixdlt-java%3Aradixdlt-java&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.radixdlt%3Aradixdlt-java%3Aradixdlt-java) [![Reliability](https://sonarcloud.io/api/project_badges/measure?project=com.radixdlt%3Aradixdlt-java%3Aradixdlt-java&metric=reliability_rating)](https://sonarcloud.io/component_measures?id=com.radixdlt%3Aradixdlt-java%3Aradixdlt-java&metric=reliability_rating) [![Security](https://sonarcloud.io/api/project_badges/measure?project=com.radixdlt%3Aradixdlt-java%3Aradixdlt-java&metric=security_rating)](https://sonarcloud.io/component_measures?id=com.radixdlt%3Aradixdlt-java%3Aradixdlt-java&metric=security_rating) [![Code Corevage](https://sonarcloud.io/api/project_badges/measure?project=com.radixdlt%3Aradixdlt-java%3Aradixdlt-java&metric=coverage)](https://sonarcloud.io/component_measures?id=com.radixdlt%3Aradixdlt-java%3Aradixdlt-java&metric=Coverage)
[![License MIT](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

radixdlt-java is a Java/Android Client library for interacting with a [Radix](https://www.radixdlt.com) Distributed Ledger.

## Table of contents

- [Features](#features)
- [Installation](#installation)
- [Getting started](#getting-started)
- [Radix dApp API](#radix-dapp-api)
- [Code examples](#code-examples)
- [Contribute](#contribute)
- [Links](#links)
- [License](#license)

## Features
* Connection to the Alphanet test network 
* Fee-less transactions for testnets
* Identity Creation
* Native token transfers
* Immutable data storage
* Instant Messaging and TEST token wallet Dapp implementation
* RXJava 2 based
* Utilizes JSON-RPC over Websockets

## Installation
Include the following gradle dependency:

### Gradle
```
repositories {
    maven { url 'https://jitpack.io' }
}

```
```
dependencies {
    implementation 'com.radixdlt:radixdlt-java:0.11.9'
}
```

## Getting started

### Identities
An Identity is the user's credentials (or more technically the manager of the
public/private key pair) into the ledger, allowing a user to own tokens and send tokens
as well as decrypt data.

To create/load an identity from a file:
```java
RadixIdentity identity = RadixIdentities.loadOrCreateEncryptedFile("filename.key", "password");
```
This will either create or load a file with a public/private key and encrypted with the given password.

### Universes
A Universe is an instance of a Radix Distributed Ledger which is defined by a genesis atom and
a dynamic set of unpermissioned nodes forming a network.

To bootstrap to the Alphanet test network:
```java
RadixUniverse.bootstrap(Bootstrap.ALPHANET);
```
**Note:** No network connections will be made yet until it is required.

## Radix dApp API
The Radix Application API is a client side API exposing high level abstractions to make
DAPP creation easier.

To initialize the API:
```java
RadixUniverse.bootstrap(Bootstrap.ALPHANET); // This must be called before RadixApplicationAPI.create()
RadixApplicationAPI api = RadixApplicationAPI.create(identity);
```


### Addresses
An address is a reference to an account and allows a user to receive tokens and/or data from other users.

You can get your own address by:
```java
RadixAddress myAddress = api.getMyAddress();
```

Or from a base58 string:
```java
RadixAddress anotherAddress = RadixAddress.fromString("JHB89drvftPj6zVCNjnaijURk8D8AMFw4mVja19aoBGmRXWchnJ");
```

## Code examples

### Storing and Retrieving Data
Immutable data can be stored on the ledger. The data can be encrypted so that only
selected identities can read the data.

To store the encrypted string `Hello` which only the user can read:
```java
ECPublicKey myPublicKey = api.getMyPublicKey();
Data data = new DataBuilder()
    .bytes("Hello".getBytes(StandardCharsets.UTF_8))
    .addReader(myPublicKey)
    .build();
Result result = api.storeData(data, <address>);
```

To store the unencrypted string `Hello`:
```java
Data data = new DataBuilder()
    .bytes("Hello".getBytes(StandardCharsets.UTF_8))
    .unencrypted()
    .build();
Result result = api.storeData(data, <address>);
```

The returned `Result` object exposes RXJava interfaces from which you can get
notified of the status of the storage action:

```java
result.toCompletable().subscribe(<on-success>, <on-error>);
```

To then read (and decrypt if necessary) all the readable data at an address:
```java
Observable<UnencryptedData> readable = api.getReadableData(<address>);
readable.subscribe(data -> { ... });
```

**Note:** data which is not decryptable by the user's key is simply ignored

### Sending and Retrieving Tokens
To send an amount of TEST (the testnet native token) from my account to another address:
```java
Result result = api.sendTokens(<to-address>, Amount.of(10, Asset.TEST));
```

To retrieve all of the token transfers which have occurred in my account:
```java
Observable<TokenTransfer> transfers = api.getMyTokenTransfers(Asset.TEST);
transfers.subscribe(tx -> { ... });
```

To get a stream of the balance of TEST tokens in my account:
```java
Observable<Amount> balance = api.getMyBalance(Asset.TEST);
balance.subscribe(bal -> { ... });
```

## Contribute

Contributions are welcome, we simply ask to:

* Fork the codebase
* Make changes
* Submit a pull request for review

When contributing to this repository, we recommend discussing with the development team the change you wish to make using a [GitHub issue](https://github.com/radixdlt/radixdlt-java/issues) before making changes.

Please follow our [Code of Conduct](CODE_OF_CONDUCT.md) in all your interactions with the project.

## Links

| Link | Description |
| :----- | :------ |
[radixdlt.com](https://radixdlt.com/) | Radix DLT Homepage
[documentation](https://docs.radixdlt.com/) | Radix Knowledge Base
[forum](https://forum.radixdlt.com/) | Radix Technical Forum
[@radixdlt](https://twitter.com/radixdlt) | Follow Radix DLT on Twitter

## License

radixdlt-java is released under the [MIT License](LICENSE).
