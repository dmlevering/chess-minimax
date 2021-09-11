package chess.model.move;

import java.awt.Point;

import chess.model.Game;
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
     * Returns a deep copy of this move
     */
    public Move copy() {
        return new PawnPromotionMove(this);
    }

    @Override
    /**
     * Executes this move for the specified game
     */
    public void execute(Game game) {
        // For now, just default to a new queen to avoid a dialog
        this.fromPiece = new Queen(this.fromPiece.getColor(), this.from);
        super.execute(game);
    }

    @Override
    /**
     * Undo this move for the specified game
     */
    public void undo(Game game) {
        this.fromPiece = new Pawn(this.fromPiece.getColor(), from);
        super.undo(game);
    }

    @Override
    /**
     * Override toString()
     */
    public String toString() {
        return super.toString() + " (pawn promotion)";
    }
    
    /**
     * PawnPromotionMove copy constructor
     */
    private PawnPromotionMove(PawnPromotionMove other) {
        this(other.from, other.to, other.fromPiece, other.toPiece);
    }
}
