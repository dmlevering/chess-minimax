package chess.model;

import java.awt.Point;

import chess.model.piece.Piece;

/**
 * Represents a square on a chess board
 */
public class Square {
    /** This square's (x,y) position */
    private final Point position;
    public Point getPosition() {
        return this.position;
    }

    /** The piece on this square, if any */
    private Piece piece;
    public Piece getPiece() {
        return piece;
    }
    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    /** This square's color */
    private final SquareColor color;
    public SquareColor getColor() {
        return color;
    }

    /**
     * Square constructor
     */
    public Square(SquareColor color, int x, int y) {
        this.position = new Point(x, y);
        this.color = color;
        this.piece = null;
    }

    /**
     * Returns a deep copy of this square
     */
    public Square copy() {
        return new Square(this);
    }

    @Override
    /**
     * Override toString()
     */
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Square [" + this.position.x + "," + this.position.y + "], ");
        if (this.piece != null) {
            str.append(this.piece.toString());
        } else {
            str.append("empty");
        }
        return str.toString();
    }

    /**
     * Square copy constructor
     */
    private Square(Square other) {
        this.position = new Point(other.position);
        this.color = other.color;
        this.piece = other.piece != null ? other.piece.copy() : null;
    }

    @Override
    /**
     * Auto-generated hashCode()
     */
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((color == null) ? 0 : color.hashCode());
        result = prime * result + ((piece == null) ? 0 : piece.hashCode());
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
        Square other = (Square) obj;
        if (color != other.color)
            return false;
        if (piece == null) {
            if (other.piece != null)
                return false;
        } else if (!piece.equals(other.piece))
            return false;
        if (position == null) {
            if (other.position != null)
                return false;
        } else if (!position.equals(other.position))
            return false;
        return true;
    }
}
