package chess.model.engine;

import chess.model.move.Move;

/**
 * Implement EngineListener and call
 * chess.model.engine.Engine.addEngineListener() to listen for engine events
 */
public interface EngineListener {
    /**
     * Invoked when the engine has selected a move
     */
    void engineMoveSelected(Move move);

    /**
     * Invoked when the engine has made progress towards selecting a move
     */
    void engineProgressUpdated(double progress, long moveCount, long hashMapHits);
}
