package chess.model.piece;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import chess.model.Board;
import chess.model.move.Move;

/**
 * Representation of a Queen chess piece
 */
public class Queen extends Piece {
    /**
     * Queen constructor
     */
    public Queen(PieceColor color, Point position) {
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

        // The queen can move diagonally and orthogonally
        this.addDiagonalMoves(board, moves);
        this.addOrthogonalMoves(board, moves);

        return moves;
    }

    @Override
    /**
     * Returns a deep copy of this piece
     */
    public Piece copy() {
        return new Queen(this.color, this.copyPosition(), this.moveCount);
    }

    /**
     * Queen constructor
     */
    private Queen(PieceColor color, Point position, int moveCount) {
        super(color, position, moveCount);
        this.id = PieceId.QUEEN;
    }
}
