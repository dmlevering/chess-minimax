package chess.model.piece;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import chess.model.Board;
import chess.model.Direction;
import chess.model.move.Move;
import chess.model.move.MoveType;
import chess.model.move.PawnPromotionMove;

/**
 * Representation of a Pawn chess piece
 */
public class Pawn extends Piece {
    /**
     * Pawn constructor
     */
    public Pawn(PieceColor color, Point position) {
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
        Direction forward = this.color == PieceColor.WHITE ? Direction.NORTH : Direction.SOUTH;

        // Pawns can always move 1 square forward
        this.addMove(board, x, y + forward.y, moves, MoveType.MOVE_ONLY);

        // Pawns can move forward 2 squares on their first move
        if (!this.hasMoved() && moves.size() > 0) {
            this.addMove(board, x, y + (forward.y * 2), moves, MoveType.MOVE_ONLY);
        }

        // Pawns can attack diagonally 1 square forward
        switch (forward) {
        case NORTH:
            this.addMove(board, x + Direction.NORTHWEST.x, y + forward.y, moves, MoveType.ATTACK_ONLY);
            this.addMove(board, x + Direction.NORTHEAST.x, y + forward.y, moves, MoveType.ATTACK_ONLY);
            break;

        case SOUTH:
            this.addMove(board, x + Direction.SOUTHWEST.x, y + forward.y, moves, MoveType.ATTACK_ONLY);
            this.addMove(board, x + Direction.SOUTHEAST.x, y + forward.y, moves, MoveType.ATTACK_ONLY);
            break;

        default:
            break;
        }

        // Pawns can be promoted when the reach the other side of the board
        if (y + forward.y == board.getRows() - 1 || y + forward.y == 0) {
            // Convert all moves to pawn promotion moves
            List<Move> promotionMoves = new ArrayList<Move>();
            for (Move move : moves) {
                promotionMoves.add(new PawnPromotionMove(move.from, move.to, move.fromPiece, move.toPiece));
            }
            moves = promotionMoves;
        }

        return moves;
    }

    @Override
    /**
     * Returns a deep copy of this piece
     */
    public Piece copy() {
        return new Pawn(this.color, this.copyPosition(), this.moveCount);
    }

    /**
     * Pawn constructor
     */
    private Pawn(PieceColor color, Point position, int moveCount) {
        super(color, position, moveCount);
        this.id = PieceId.PAWN;
    }
}
