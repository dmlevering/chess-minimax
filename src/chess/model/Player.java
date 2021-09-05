package chess.model;

import chess.model.engine.Engine;
import chess.model.piece.PieceColor;

/**
 * Represents a chess player (human or engine)
 */
public class Player {
    /** This player's color */
    private final PieceColor color;
    public PieceColor getColor() {
        return this.color;
    }

    /** This player's opponent */
    private Player opponent;
    public Player getOpponent() {
        return this.opponent;
    }
    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }

    /** This player's engine, if any */
    private Engine engine;
    public Engine getEngine() {
        return this.engine;
    }
    public void setEngine(Engine engine) {
        this.engine = engine;
        if (engine != null) {
            engine.setPlayer(this);
        }
    }

    /**
     * Player constructor
     */
    public Player(PieceColor color, Engine engine) {
        this.color = color;
        this.engine = engine;
        if (this.engine != null) {
            this.engine.setPlayer(this);
        }
    }

    /**
     * Returns a deep copy of this player
     */
    public Player copy() {
        return new Player(this);
    }

    /**
     * Takes a turn (no action for human player)
     */
    public void takeTurn() {
        if (this.engine != null) {
            // Run the engine on a new thread, non-blocking
            this.engine.run();
        }
    }

    /**
     * Player copy constructor
     */
    private Player(Player other) {
        this.color = other.color;
        this.engine = other.engine; // TODO:DML
    }

    @Override
    /**
     * Auto-generated hashCode()
     */
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((color == null) ? 0 : color.hashCode());
        return result;
    }

    @Override
    /**
     * Auto-generated equals()
     */
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Player other = (Player) obj;
        if (color != other.color)
            return false;
        return true;
    }
}
