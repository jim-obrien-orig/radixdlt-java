package com.radixdlt.client.atommodel.chess;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.radixdlt.client.atommodel.accounts.RadixAddress;
import com.radixdlt.client.core.atoms.particles.Particle;
import org.radix.common.ID.EUID;
import org.radix.serialization2.DsonOutput;
import org.radix.serialization2.SerializerId2;

@SerializerId2("CHESSMOVEPARTICLE")
public final class ChessMoveParticle extends Particle {
    @JsonProperty("move")
    @DsonOutput(DsonOutput.Output.ALL)
    private String move;

    @JsonProperty("gameAddress")
    @DsonOutput(DsonOutput.Output.ALL)
    private RadixAddress gameAddress;

    @JsonProperty("gameUID")
    @DsonOutput(DsonOutput.Output.ALL)
    private EUID gameUID;

<<<<<<< HEAD
    private ChessMoveParticle(RadixAddress gameAddress, EUID gameUID, String move) {
=======
    private ChessMoveParticle() {
    }

    public ChessMoveParticle(RadixAddress gameAddress, EUID gameUID, String move) {
>>>>>>> de2d9eb... Add initial state to board
        this.gameAddress = gameAddress;
        this.gameUID = gameUID;
        this.move = move;
    }

    public static ChessMoveParticle move(ChessBoardParticle board, String move) {
        return new ChessMoveParticle(board.getGameAddress(), board.getGameUID(), move);
    }
}