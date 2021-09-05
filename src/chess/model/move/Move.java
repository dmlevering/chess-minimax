package chess.model.move;

import java.awt.Point;

import chess.model.Board;
import chess.model.Game;
import chess.model.piece.King;
import chess.model.piece.Piece;

/**
 * Represents a move from one (x,y) position to another
 */
public class Move {
    /** The position to move from */
    public final Point from;

    /** The position to move to */
    public final Point to;

    /** The moving piece */
    public Piece fromPiece;

    /** The piece at the position we're moving to, if any */
    public Piece toPiece;

    /**
     * Move constructor
     */
    public Move(Point from, Point to, Piece fromPiece, Piece toPiece) {
        this.from = from;
        this.to = to;
        this.fromPiece = fromPiece != null ? fromPiece.copy() : null;
        this.toPiece = toPiece != null ? toPiece.copy() : null;
    }

    /**
     * Move constructor
     */
    public Move(Point to) {
        this.from = null;
        this.to = to;
        this.fromPiece = null;
        this.toPiece = null;
    }

    /**
     * Returns a deep copy of this move
     */
    public Move copy() {
        return new Move(this);
    }

    /**
     * Executes this move for the specified game
     */
    public void execute(Game game) {
        // Move the piece
        Board board = game.getBoard();
        board.getSquare(this.to).setPiece(this.fromPiece);
        board.getSquare(this.from).setPiece(null);
        this.fromPiece.setPosition(this.to);
        if (this.toPiece != null) {
            this.toPiece.setPosition(null);
        }

        // Track the king's position
        if (this.fromPiece instanceof King) {
            board.setKingPosition(this.fromPiece.getColor(), this.to);
        }

        // Track movement counts
        this.fromPiece.moveCount += 1;
    }

    /**
     * Undo this move for the specified game
     */
    public void undo(Game game) {
        // Move the pieces back to where they were
        Board board = game.getBoard();
        board.getSquare(this.from).setPiece(this.fromPiece);
        board.getSquare(this.to).setPiece(this.toPiece);
        this.fromPiece.setPosition(this.from);
        if (this.toPiece != null) {
            this.toPiece.setPosition(this.to);
        }

        // Track the king's position
        if (this.fromPiece instanceof King) {
            board.setKingPosition(this.fromPiece.getColor(), this.from);
        }

        // Track movement counts
        this.fromPiece.moveCount -= 1;
    }

    /**
     * Returns whether this move is valid for the specified game
     */
    public boolean isValid(Game game) {
        // This move is valid as long as it doesn't result in check
        game.executeMove(this, false);
        boolean valid = !game.isCheck();
        game.undoMove(this, false);
        return valid;
    }

    @Override
    /**
     * Override toString()
     */
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(this.fromPiece.getDescription() + " ");
        String from = Character.toString('a' + this.from.x) + (this.from.y + 1);
        String to = Character.toString('a' + this.to.x) + (this.to.y + 1);
        if (this.toPiece != null) {
            str.append(String.format("takes %s (%s to %s)", toPiece.getDescription(), from, to));
        } else {

            str.append(String.format("moves from %s to %s", from, to));
        }

        return str.toString();
    }

    @Override
    /**
     * Override equals()
     */
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if (!(object instanceof Move)) {
            return false;
        }

        // We don't care about the square to move from
        Move move = (Move) object;
        return move.to.equals(this.to);
    }

    /**
     * Move copy constructor
     */
    private Move(Move other) {
        this.from = new Point(other.from);
        this.to = new Point(other.to);
        this.fromPiece = other.fromPiece != null ? other.fromPiece.copy() : null;
        this.toPiece = other.toPiece != null ? other.toPiece.copy() : null;
    }
}
