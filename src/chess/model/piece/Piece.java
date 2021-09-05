package chess.model.piece;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import chess.model.Board;
import chess.model.Direction;
import chess.model.Square;
import chess.model.move.Move;
import chess.model.move.MoveType;

/**
 * Abstract representation of a chess piece
 */
public abstract class Piece {
    /** The piece's type */
    protected PieceId id;
    public PieceId getId() { return this.id; }
    
    /** The piece's color */
    protected PieceColor color;
    public PieceColor getColor() { return this.color; }
    
    /** This piece's current (x,y) position on the board, if any */
    protected Point position;
    public Point getPosition() { return this.position; }
    public void setPosition(Point position) { this.position = position; }
    
    /** Has this piece been moved? */
    public int moveCount;
    public boolean hasMoved() { return this.moveCount != 0; }

    /**
     * Piece constructor
     */
    public Piece(PieceColor color, Point position) {
        this(color, position, 0);
    }

    /**
     * Piece constructor
     */
    public Piece(PieceColor color, Point position, int moveCount) {
        this.color = color;
        this.position = position;
        this.moveCount = moveCount;
    }

    /**
     * Returns a deep copy of this piece
     */
    public abstract Piece copy();

    /**
     * Returns this piece's possible moves given the current board
     * 
     * Note: This function may return moves that would put the player in check.
     * These moves are filtered elsewhere.
     */
    public abstract List<Move> getMoves(Board board);

    @Override
    /**
     * Returns a string representation of this piece
     */
    public String toString() {
        return this.color.getAbrvName() + this.id.getAbrvName();
    }

    /**
     * Returns a more human-readable string description of this piece
     */
    public String getDescription() {
        return String.format("%s %s", this.color.getFullName(), this.id.getFullName().toLowerCase());
    }

    /**
     * Adds this piece's possible horizontal and vertical moves
     */
    protected void addOrthogonalMoves(Board board, List<Move> moves) {
        for (Direction direction : Direction.Orthogonals) {
            moves.addAll(this.getMoves(this.position.x, this.position.y, direction, board));
        }
    }

    /**
     * Adds this piece's possible diagonal moves
     */
    protected void addDiagonalMoves(Board board, List<Move> moves) {
        for (Direction direction : Direction.Diagonals) {
            moves.addAll(this.getMoves(this.position.x, this.position.y, direction, board));
        }
    }

    /**
     * Adds a move to the specified square if possible (i.e., if the square is empty
     * or contains an enemy piece). Returns true if the square is empty, false
     * otherwise.
     */
    protected boolean addMove(Board board, int x, int y, List<Move> moves) {
        return this.addMove(board, x, y, moves, MoveType.ANY);
    }

    /**
     * Adds a move to the specified position if possible. Returns true if the square
     * is valid and empty, false otherwise.
     */
    protected boolean addMove(Board board, int x, int y, List<Move> moves, MoveType moveType) {
        Square to = board.getSquare(x, y);

        if (to == null) {
            return false;
        }

        boolean move = false;
        boolean empty = true;
        Piece toPiece = to.getPiece();

        // There is a piece on this square
        if (toPiece != null) {
            empty = false;
            if (moveType != MoveType.MOVE_ONLY && this.color != toPiece.getColor()) {
                move = true;
            }
        }

        // This square is empty, and we are allowed to move without attacking
        else if (moveType != MoveType.ATTACK_ONLY) {
            move = true;
        }

        if (move) {
            moves.add(new Move(this.position, to.getPosition(), this, toPiece));
        }

        return empty;
    }

    /**
     * Returns a deep copy of this piece's position
     */
    protected Point copyPosition() {
        return this.position != null ? new Point(this.position) : null;
    }

    /**
     * Returns this piece's possible moves in a given direction
     */
    private List<Move> getMoves(int x, int y, Direction direction, Board board) {
        List<Move> moves = new ArrayList<Move>();
        while (this.addMove(board, x + direction.x, y + direction.y, moves)) {
            x += direction.x;
            y += direction.y;
        }

        return moves;
    }

    @Override
    /**
     * Auto-generated hashCode()
     */
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((color == null) ? 0 : color.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + moveCount;
        result = prime * result + ((position == null) ? 0 : position.hashCode());
        return result;
    }

    @Override
    /**
     * Auto-generated equals()
     */
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Piece other = (Piece) obj;
        if (color != other.color)
            return false;
        if (id != other.id)
            return false;
        if (moveCount != other.moveCount)
            return false;
        if (position == null) {
            if (other.position != null)
                return false;
        } else if (!position.equals(other.position))
            return false;
        return true;
    }
}
