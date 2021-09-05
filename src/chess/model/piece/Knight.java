package chess.model.piece;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import chess.model.Board;
import chess.model.Direction;
import chess.model.move.Move;

/**
 * Representation of a Knight chess piece
 */
public class Knight extends Piece {
    /**
     * Knight constructor
     */
    public Knight(PieceColor color, Point position) {
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

        int x = this.position.x;
        int y = this.position.y;

        this.addMove(board, x + Direction.NORTHWEST.x, y + (Direction.NORTHWEST.y * 2), moves);
        this.addMove(board, x + (Direction.NORTHWEST.x * 2), y + Direction.NORTHWEST.y, moves);
        this.addMove(board, x + Direction.SOUTHWEST.x, y + (Direction.SOUTHWEST.y * 2), moves);
        this.addMove(board, x + (Direction.SOUTHWEST.x * 2), y + Direction.SOUTHWEST.y, moves);
        this.addMove(board, x + Direction.NORTHEAST.x, y + (Direction.NORTHEAST.y * 2), moves);
        this.addMove(board, x + (Direction.NORTHEAST.x * 2), y + Direction.NORTHEAST.y, moves);
        this.addMove(board, x + Direction.SOUTHEAST.x, y + (Direction.SOUTHEAST.y * 2), moves);
        this.addMove(board, x + (Direction.SOUTHEAST.x * 2), y + Direction.SOUTHEAST.y, moves);

        return moves;
    }

    @Override
    /**
     * Returns a deep copy of this piece
     */
    public Piece copy() {
        return new Knight(this.color, this.copyPosition(), this.moveCount);
    }

    /**
     * Knight constructor
     */
    private Knight(PieceColor color, Point position, int moveCount) {
        super(color, position, moveCount);
        this.id = PieceId.KNIGHT;
    }
}
