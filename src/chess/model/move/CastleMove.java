package chess.model.move;

import java.awt.Point;
import java.util.List;

import chess.model.Board;
import chess.model.Game;
import chess.model.piece.King;
import chess.model.piece.Piece;
import chess.model.piece.PieceColor;
import chess.model.piece.Rook;

/**
 * Represents a "castle" move where a king and a rook both move at once
 * 
 * https://en.wikipedia.org/wiki/Castling
 */
public class CastleMove extends Move {
    /** The position to move the rook from */
    public final Point rookFrom;

    /** The position to move the rook to */
    public final Point rookTo;

    /**
     * CastleMove constructor
     */
    public CastleMove(King king, Point kingFrom, Point kingTo, Point rookFrom, Point rookTo) {
        super(kingFrom, kingTo, king, null);
        this.rookFrom = rookFrom;
        this.rookTo = rookTo;
    }

    @Override
    /**
     * Returns a deep copy of this move
     */
    public Move copy() {
        return new CastleMove(this);
    }

    /**
     * Adds castle moves
     * 
     * Note: This function may return moves that would put the player in check.
     * These moves are filtered elsewhere.
     */
    public static void addMoves(King king, Board board, List<Move> moves) {
        // The king cannot castle if it has already moved
        if (king.hasMoved()) {
            return;
        }

        // Determine row
        int row;
        if (king.getColor() == PieceColor.WHITE) {
            row = 0;
        } else {
            assert (king.getColor() == PieceColor.BLACK);
            row = board.getRows() - 1;
        }

        // Try castling west
        Piece rook = board.getPiece(0, row);
        if (rook != null && rook instanceof Rook && !rook.hasMoved()) {
            if (board.getPiece(1, row) == null && board.getPiece(2, row) == null && board.getPiece(3, row) == null) {
                CastleMove move = new CastleMove(king, king.getPosition(), new Point(2, row), rook.getPosition(),
                        new Point(3, row));
                moves.add(move);
            }
        }

        // Try castling east
        rook = board.getPiece(7, row);
        if (rook != null && rook instanceof Rook && !rook.hasMoved()) {
            if (board.getPiece(5, row) == null && board.getPiece(6, row) == null) {
                CastleMove move = new CastleMove(king, king.getPosition(), new Point(6, row), rook.getPosition(),
                        new Point(5, row));
                moves.add(move);
            }
        }
    }

    @Override
    /**
     * Executes this move for the specified game
     */
    public void execute(Game game) {
        Board board = game.getBoard();
        Piece king = board.getPiece(this.from);
        Piece rook = board.getPiece(this.rookFrom);

        // Move the pieces
        board.getSquare(this.to).setPiece(king);
        board.getSquare(this.from).setPiece(null);
        board.getSquare(this.rookTo).setPiece(rook);
        board.getSquare(this.rookFrom).setPiece(null);
        king.setPosition(this.to);
        rook.setPosition(this.rookTo);

        // Track the king's position
        board.setKingPosition(king.getColor(), this.to);

        // Track movement counts
        king.moveCount += 1;
        rook.moveCount += 1;
    }

    @Override
    /**
     * Undo this move for the specified game
     */
    public void undo(Game game) {
        Board board = game.getBoard();
        Piece king = board.getPiece(this.to);
        Piece rook = board.getPiece(this.rookTo);

        // Move the pieces back to where they were
        board.getSquare(this.from).setPiece(king);
        board.getSquare(this.to).setPiece(null);
        board.getSquare(this.rookFrom).setPiece(rook);
        board.getSquare(this.rookTo).setPiece(null);
        king.setPosition(this.from);
        rook.setPosition(this.rookFrom);

        // Track the king's position
        board.setKingPosition(king.getColor(), this.from);

        // Track movement counts
        king.moveCount -= 1;
        rook.moveCount -= 1;
    }

    @Override
    /**
     * Returns whether this move is valid for the specified game
     */
    public boolean isValid(Game game) {
        // This move is invalid if the king is in check
        if (game.isCheck()) {
            return false;
        }

        // The move is invalid if the king passes through check
        int x = this.from.x;
        int xTo = this.to.x;
        int dir = xTo - x > 0 ? 1 : -1;
        boolean valid = true;
        while (x != xTo && valid) {
            x += dir;
            Move moveKing = new Move(this.from, new Point(x, this.to.y), this.fromPiece, null);
            game.executeMove(moveKing, false);
            valid = !game.isCheck();
            game.undoMove(moveKing, false);
        }

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
        str.append(String.format("castles from %s to %s", from, to));
        return str.toString();
    }

    /**
     * CastleMove copy constructor
     */
    private CastleMove(CastleMove other) {
        super(other.from, other.to, other.fromPiece, null);
        this.rookFrom = new Point(other.rookFrom);
        this.rookTo = new Point(other.rookTo);
    }
}
