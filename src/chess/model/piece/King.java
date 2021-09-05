package chess.model.piece;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import chess.model.Board;
import chess.model.Direction;
import chess.model.move.CastleMove;
import chess.model.move.Move;

/**
 * Representation of a King chess piece
 */
public class King extends Piece {
    /**
     * King constructor
     */
    public King(PieceColor color, Point position) {
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

        // The king can move one square in any direction
        for (Direction direction : Direction.values()) {
            this.addMove(board, this.position.x + direction.x, this.position.y + direction.y, moves);
        }

        // The king can castle under certain conditions
        CastleMove.addMoves(this, board, moves);

        return moves;
    }

    @Override
    /**
     * Returns a deep copy of this piece
     */
    public Piece copy() {
        return new King(this.color, this.copyPosition(), this.moveCount);
    }

    /**
     * King constructor
     */
    private King(PieceColor color, Point position, int moveCount) {
        super(color, position, moveCount);
        this.id = PieceId.KING;
    }
}
