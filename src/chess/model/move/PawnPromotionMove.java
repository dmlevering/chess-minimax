package chess.model.move;

import java.awt.Point;

import chess.model.Game;
import chess.model.Player;
import chess.model.piece.Pawn;
import chess.model.piece.Piece;
import chess.model.piece.Queen;

/**
 * Represents a pawn promotion, where the active player selects a new piece to
 * replace the pawn
 */
public class PawnPromotionMove extends Move {
    /**
     * PawnPromotionMove constructor
     */
    public PawnPromotionMove(Point from, Point to, Piece fromPiece, Piece toPiece) {
        super(from, to, fromPiece, toPiece);
    }

    @Override
    /**
     * Executes this move for the specified game
     */
    public void execute(Game game) {
        Player activePlayer = game.getActivePlayer();

        // For now, just default to a new queen to avoid a dialog
        this.fromPiece = new Queen(activePlayer.getColor(), this.from);
        super.execute(game);
    }

    @Override
    /**
     * Undo this move for the specified game
     */
    public void undo(Game game) {
        Player activePlayer = game.getActivePlayer();

        this.fromPiece = new Pawn(activePlayer.getColor(), from);
        super.undo(game);
    }

    @Override
    /**
     * Override toString()
     */
    public String toString() {
        return super.toString() + " (pawn promotion)";
    }
}
