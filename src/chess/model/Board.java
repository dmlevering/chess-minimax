package chess.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import chess.model.piece.Bishop;
import chess.model.piece.King;
import chess.model.piece.Knight;
import chess.model.piece.Pawn;
import chess.model.piece.Piece;
import chess.model.piece.PieceColor;
import chess.model.piece.Queen;
import chess.model.piece.Rook;

/**
 * Represents a chess board
 */
public class Board {
    /** Number of rows on a standard chess board */
    private static final int STD_ROWS = 8;

    /** Number of columns on a standard chess board */
    private static final int STD_COLUMNS = 8;

    /** The board is a 2D matrix of squares */
    private final Square[][] squares;

    /** Number of rows on this board */
    private final int rows;
    public int getRows() {
        return this.rows;
    }

    /** Number of columns on this board */
    private final int columns;
    public int getColumns() {
        return this.columns;
    }

    /** The positions of the kings on this board */
    private final Point[] kingPositions;
    public Point getKingPosition(Player player) {
        int idx = player.getColor().ordinal();
        if (idx < this.kingPositions.length) {
            return this.kingPositions[idx];
        }
        return null;
    }
    public void setKingPosition(Player player, Point position) {
        this.setKingPosition(player.getColor(), position);
    }
    public void setKingPosition(PieceColor color, Point position) {
        int idx = color.ordinal();
        if (idx < this.kingPositions.length) {
            this.kingPositions[idx] = position;
        }
    }
    
    /** The starting pieces on this board */
    private final List<Piece> initialPieces;

    /**
     * Constructs a standard chess board
     */
    public static Board getStandard() {
        List<Piece> pieces = new ArrayList<Piece>();
        pieces.addAll(getStandardWhitePieces());
        pieces.addAll(getStandardBlackPieces());
        return new Board(STD_ROWS, STD_COLUMNS, pieces);
    }

    /**
     * Board constructor
     */
    public Board(int rows, int columns, List<Piece> pieces) {
        this.squares = new Square[rows][columns];
        this.kingPositions = new Point[PieceColor.values().length];
        this.rows = rows;
        this.columns = columns;
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                SquareColor color = (((this.rows - 1 - i) + j) % 2 == 0) ? SquareColor.LIGHT : SquareColor.DARK;
                squares[i][j] = new Square(color, i, j);
            }
        }

        // Set pieces
        this.initialPieces = new ArrayList<Piece>();
        for (Piece piece : pieces) {
            int x = piece.getPosition().x;
            int y = piece.getPosition().y;
            if (isValidPosition(x, y)) {
                this.squares[x][y].setPiece(piece);
                this.initialPieces.add(piece.copy());

                // Set king positions
                if (piece instanceof King) {
                    this.setKingPosition(piece.getColor(), piece.getPosition());
                }
            }
        }
    }

    /**
     * Returns a deep copy of this board
     */
    public Board copy() {
        return new Board(this);
    }
    
    /**
     * Resets the original pieces on this board
     */
    public void reset() {
        // Clear the remaining pieces
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                squares[i][j].setPiece(null);
            }
        }
        
        // Set the original pieces
        for (Piece piece : this.initialPieces) {
            int x = piece.getPosition().x;
            int y = piece.getPosition().y;
            this.squares[x][y].setPiece(piece);

            // Set king positions
            if (piece instanceof King) {
                this.setKingPosition(piece.getColor(), piece.getPosition());
            }
        }
    }

    /**
     * Returns all pieces of the specified color on the board
     */
    public List<Piece> getPieces(PieceColor color) {
        List<Piece> pieces = new ArrayList<Piece>();
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                Piece piece = getPiece(i, j);
                if (piece != null && piece.getColor() == color) {
                    pieces.add(piece);
                }
            }
        }
        return pieces;
    }

    /**
     * Gets the piece at the square at the specified position, if any
     */
    public Piece getPiece(Point position) {
        return this.getPiece(position.x, position.y);
    }

    /**
     * Gets the piece at the square at position (x,y), if any
     */
    public Piece getPiece(int x, int y) {
        Square square = this.getSquare(x, y);
        if (square != null) {
            return square.getPiece();
        }
        return null;
    }

    /**
     * Gets the square at position (x,y), if any
     */
    public Square getSquare(Point location) {
        return this.getSquare(location.x, location.y);
    }

    /**
     * Gets the square at position (x,y), if any
     */
    public Square getSquare(int x, int y) {
        if (isValidPosition(x, y)) {
            return this.squares[x][y];
        }
        return null;
    }

    /**
     * Returns whether the given position is valid for this board
     */
    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < this.rows && y >= 0 && y < this.columns;
    }

    /**
     * Board copy constructor
     */
    private Board(Board other) {
        this.rows = other.rows;
        this.columns = other.columns;
        this.squares = new Square[this.rows][this.columns];
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                this.squares[i][j] = other.squares[i][j].copy();
            }
        }
        this.kingPositions = new Point[other.kingPositions.length];
        for (int i = 0; i < this.kingPositions.length; i++) {
            this.kingPositions[i] = new Point(other.kingPositions[i]);
        }
        this.initialPieces = new ArrayList<Piece>();
        for (Piece piece : other.initialPieces) {
            this.initialPieces.add(piece.copy());
        }
    }
    
    /**
     * Returns the standard white pieces
     */
    private static List<Piece> getStandardWhitePieces() {
        return new ArrayList<>(Arrays.asList(
            new Rook   (PieceColor.WHITE, new Point(0, 0)),
            new Knight (PieceColor.WHITE, new Point(1, 0)),
            new Bishop (PieceColor.WHITE, new Point(2, 0)),
            new Queen  (PieceColor.WHITE, new Point(3, 0)),
            new King   (PieceColor.WHITE, new Point(4, 0)),
            new Bishop (PieceColor.WHITE, new Point(5, 0)),
            new Knight (PieceColor.WHITE, new Point(6, 0)),
            new Rook   (PieceColor.WHITE, new Point(7, 0)),
            new Pawn   (PieceColor.WHITE, new Point(0, 1)),
            new Pawn   (PieceColor.WHITE, new Point(1, 1)),
            new Pawn   (PieceColor.WHITE, new Point(2, 1)),
            new Pawn   (PieceColor.WHITE, new Point(3, 1)),
            new Pawn   (PieceColor.WHITE, new Point(4, 1)),
            new Pawn   (PieceColor.WHITE, new Point(5, 1)),
            new Pawn   (PieceColor.WHITE, new Point(6, 1)),
            new Pawn   (PieceColor.WHITE, new Point(7, 1))
            ));
    }
    
    /**
     * Returns the standard black pieces
     */
    private static List<Piece> getStandardBlackPieces() {
        return new ArrayList<>(Arrays.asList(
            new Rook   (PieceColor.BLACK, new Point(0, 7)),
            new Knight (PieceColor.BLACK, new Point(1, 7)),
            new Bishop (PieceColor.BLACK, new Point(2, 7)),
            new Queen  (PieceColor.BLACK, new Point(3, 7)),
            new King   (PieceColor.BLACK, new Point(4, 7)),
            new Bishop (PieceColor.BLACK, new Point(5, 7)),
            new Knight (PieceColor.BLACK, new Point(6, 7)),
            new Rook   (PieceColor.BLACK, new Point(7, 7)),
            new Pawn   (PieceColor.BLACK, new Point(0, 6)),
            new Pawn   (PieceColor.BLACK, new Point(1, 6)),
            new Pawn   (PieceColor.BLACK, new Point(2, 6)),
            new Pawn   (PieceColor.BLACK, new Point(3, 6)),
            new Pawn   (PieceColor.BLACK, new Point(4, 6)),
            new Pawn   (PieceColor.BLACK, new Point(5, 6)),
            new Pawn   (PieceColor.BLACK, new Point(6, 6)),
            new Pawn   (PieceColor.BLACK, new Point(7, 6))
            ));
    }

    @Override
    /**
     * Auto-generated hashCode()
     */
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + columns;
        result = prime * result + Arrays.hashCode(kingPositions);
        result = prime * result + rows;
        result = prime * result + Arrays.deepHashCode(squares);
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
        Board other = (Board) obj;
        if (columns != other.columns)
            return false;
        if (!Arrays.equals(kingPositions, other.kingPositions))
            return false;
        if (rows != other.rows)
            return false;
        if (!Arrays.deepEquals(squares, other.squares))
            return false;
        return true;
    }
}
