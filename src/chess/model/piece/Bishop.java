package chess.model.piece;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import chess.model.Board;
import chess.model.move.Move;

/**
 * Representation of a Bishop chess piece
 */
public class Bishop extends Piece {
    /**
     * Bishop constructor
     */
    public Bishop(PieceColor color, Point position) {
        this(color, position, 0);
    }

    @Override
    /**
     * Returns this piece's possible moves given the current board
     * 
     * Note: This function may return moves that would put the player in check.
     * These moves are filtered elsewhere.
     */
    public List<Move> getMoves(Board board) {
        List<Move> moves = new ArrayList<Move>();
        if (this.position == null) {
            return moves;
        }

        // The bishop can move diagonally
        this.addDiagonalMoves(board, moves);

        return moves;
    }

    @Override
    /**
     * Returns a deep copy of this piece
     */
    public Piece copy() {
        return new Bishop(this.color, this.copyPosition(), this.moveCount);
    }

    /**
     * Bishop constructor
     */
    private Bishop(PieceColor color, Point position, int moveCount) {
        super(color, position, moveCount);
        this.id = PieceId.BISHOP;
    }
}
