package chess.gui;

import chess.model.Board;
import chess.model.Game;
import chess.model.move.Move;
import chess.model.piece.Piece;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

/**
 * Represents a chess board
 */
public class GuiBoard extends JPanel {
    /** Generated UID */
    private static final long serialVersionUID = -2969992473862409598L;

    /** The squares that make up this board */
    private GuiSquare squares[][];

    /** The game model */
    private Game game;

    /** The currently selected square, if any */
    private GuiSquare selectedSquare;

    /** Highlight possible moves? */
    private boolean showPossibleMoves;
    public void setShowPossibleMoves(boolean showPossibleMoves) {
        this.showPossibleMoves = showPossibleMoves;
        this.resetPossibleMoves();
        this.redraw();
    }

    /**
     * GuiBoard constructor
     */
    public GuiBoard(Game game, Dimension dimensions, boolean showPossibleMoves) {
        // Initialize GUI
        super();
        this.setPreferredSize(dimensions);
        this.setBackground(java.awt.Color.black);
        this.setBorder(new LineBorder(java.awt.Color.black));

        // Store initialization arguments
        this.showPossibleMoves = showPossibleMoves;
        this.game = game;

        // This panel is a grid of squares
        Board board = this.game.getBoard();
        int rows = board.getRows();
        int columns = board.getColumns();
        this.setLayout(new GridLayout(rows, columns));
        this.squares = new GuiSquare[rows][columns];
        for (int y = rows - 1; y >= 0; y--) {
            for (int x = 0; x < columns; x++) {
                GuiSquare square = new GuiSquare(board.getSquare(x, y));
                this.squares[x][y] = square;

                // Update the board when this square is clicked
                square.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        GuiSquare square = (GuiSquare) e.getComponent();
                        if (square != null) {
                            guiSquareSelected(square);
                        }
                    }
                });

                // Order matters because we're adding to a grid
                this.add(square);
            }
        }
    }

    /**
     * Called when a square is clicked
     */
    private void guiSquareSelected(GuiSquare square) {
        // No action while an engine is running
        if (this.game.isEngineRunning()) {
            return;
        }
        
        // A square is already selected
        if (this.selectedSquare != null) {
            // De-select this square
            if (square == this.selectedSquare) {
                square.setSelected(false);
                this.selectedSquare = null;
            }

            // Attempt to move or select a different piece
            else {
                // Try moving
                if (this.tryExecuteMove(this.selectedSquare.getSquare().getPiece(), square.getSquare().getPosition())) {
                    this.selectedSquare.setSelected(false);
                    this.selectedSquare = null;
                }

                // Try selecting a different piece
                else if (this.canSelect(square)) {
                    this.selectedSquare.setSelected(false);
                    square.setSelected(true);
                    this.selectedSquare = square;
                }

                // Otherwise, clear the selection
                else {
                    this.selectedSquare.setSelected(false);
                    this.selectedSquare = null;
                }
            }
        }

        // Select this square if possible
        else if (this.canSelect(square)) {
            square.setSelected(true);
            this.selectedSquare = square;
        }

        // Reset possible moves
        this.resetPossibleMoves();

        // Redraw
        this.redraw();
    }

    /**
     * Undo a move, if possible
     */
    public void undo() {
        this.game.undo();
        this.clearSelection();
    }

    /**
     * Redo a move, if possible
     */
    public void redo() {
        this.game.redo();
        this.clearSelection();
    }

    /**
     * Redraws the panel
     */
    public void redraw() {
        // Don't redraw while an engine is running
        if (this.game.isEngineRunning()) {
            return;
        }

        Move mostRecent = this.game.getMostRecentMove();
        for (int i = 0; i < this.squares.length; i++) {
            for (int j = 0; j < this.squares[i].length; j++) {
                // Set an alternate background color (null for default background)
                Color alt = null;
                if (mostRecent != null) {
                    if (i == mostRecent.from.x && j == mostRecent.from.y) {
                        alt = GuiColor.MOVE_FROM;
                    } else if (i == mostRecent.to.x && j == mostRecent.to.y) {
                        alt = GuiColor.MOVE_TO;
                    }
                }
                this.squares[i][j].setBackColorAlt(alt);

                this.squares[i][j].redraw();
            }
        }

        this.revalidate();
        this.repaint();
    }

    @Override
    /**
     * Ensures this board's aspect ratio remains 1:1
     */
    public Dimension getPreferredSize() {
        Dimension d = this.getParent().getSize();
        int newSize = (d.width > d.height) ? d.height : d.width;
        newSize = (newSize == 0) ? 100 : newSize;
        return new Dimension(newSize, newSize);
    }

    /**
     * Attempts to move the specified piece to the specified position, returns
     * whether the move was executed
     */
    private boolean tryExecuteMove(Piece piece, Point to) {
        Move m = new Move(to);
        for (Move move : this.game.getValidMoves(piece)) {
            if (move.equals(m)) {
                this.game.executeMove(move);
                this.game.endTurn();
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether the player can select this square
     */
    private boolean canSelect(GuiSquare square) {
        // The player can only select squares containing own pieces
        Piece piece = square.getSquare().getPiece();
        return piece != null && piece.getColor() == this.game.getActivePlayer().getColor();
    }

    /**
     * Resets possible moves
     */
    private void resetPossibleMoves() {
        // Clear all possible moves
        for (int i = 0; i < this.squares.length; i++) {
            for (int j = 0; j < this.squares[i].length; j++) {
                this.squares[i][j].setPossibleMove(false);
            }
        }

        // Show possible moves for the selected square
        if (this.showPossibleMoves && this.selectedSquare != null) {
            // Get this piece's possible moves
            Piece piece = this.selectedSquare.getSquare().getPiece();
            for (Move move : this.game.getValidMoves(piece)) {
                this.squares[move.to.x][move.to.y].setPossibleMove(true);
            }
        }
    }

    /**
     * Clears the selected square and possible move highlighting
     */
    private void clearSelection() {
        // Clear selected square
        if (this.selectedSquare != null) {
            this.selectedSquare.setSelected(false);
            this.selectedSquare = null;
        }

        // Reset possible moves
        this.resetPossibleMoves();

        // Redraw
        this.redraw();
    }
}
