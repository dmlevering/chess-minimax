package chess.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import chess.model.Square;
import chess.model.SquareColor;
import chess.model.piece.Piece;

/**
 * Represents a square on a chess board
 */
public class GuiSquare extends JPanel {
    /** Generated UID */
    private static final long serialVersionUID = 6625641002334128430L;

    /** The square model */
    private Square square;
    public Square getSquare() {
        return this.square;
    }

    /** Is this square a possible move? */
    private boolean isPossibleMove;
    public boolean getIsPossibleMove() {
        return this.isPossibleMove;
    }
    public void setPossibleMove(boolean isPossibleMove) {
        this.isPossibleMove = isPossibleMove;
    }

    /** Is this square selected? */
    private boolean isSelected;
    public boolean getIsSelected() {
        return this.isSelected;
    }
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    /** This square's original background color (light or dark) */
    private Color backColor;

    /** This square's alternate background color */
    private Color backColorAlt;
    public void setBackColorAlt(Color backColorAlt) {
        this.backColorAlt = backColorAlt;
    }

    /**
     * GuiSquare constructor
     */
    public GuiSquare(Square square) {
        super();
        this.square = square;
        this.backColor = square.getColor() == SquareColor.DARK ? GuiColor.DARK : GuiColor.LIGHT;
        this.backColorAlt = null;
        this.setBackground(this.backColor);
        this.isPossibleMove = false;
        this.isSelected = false;
    }

    /**
     * Redraws this panel
     */
    public void redraw() {
        // Clear existing icon
        this.removeAll();

        // Draw an icon if there is a piece on this square
        Piece piece = this.square.getPiece();
        if (piece != null) {
            int height = (int) (this.getHeight() * 0.75);
            int width = (int) (this.getWidth() * 0.75);
            this.add(new JLabel(GuiPiece.getPieceIcon(piece, height, width)));
        }

        // Update the background color
        if (this.backColorAlt != null) {
            this.setBackground(this.backColorAlt);
        } else {
            this.setBackground(this.backColor);
        }

        // Highlight this square's border when selected
        this.setBorder(new LineBorder(this.isSelected ? GuiColor.BORDER_SELECTED : GuiColor.BORDER_DEFAULT,
                (int) (this.getHeight() * 0.05)));
        
        // Repaint
        this.repaint();
    }

    @Override
    /**
     * Draws a circle on this square when it is a possible move
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.isPossibleMove) {
            Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(GuiColor.MOVE_POSSIBLE);
            g2.fillOval(this.getWidth() / 4, this.getHeight() / 4, this.getWidth() / 2, this.getHeight() / 2);
        }
    }
}