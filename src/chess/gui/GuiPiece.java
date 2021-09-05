package chess.gui;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import chess.model.piece.Piece;
import chess.model.piece.PieceId;

/**
 * Manages chess piece icons
 */
public class GuiPiece {
    /** Piece icons */
    private static final Map<String, BufferedImage> pieceIcons = new HashMap<String, BufferedImage>();

    /** Piece icons, scaled cache */
    private static final Map<String, ImageIcon> pieceIconsScaled = new HashMap<String, ImageIcon>();

    /**
     * Loads piece icons from the assets folder
     */
    public static void loadPieceIcons() {
        String[] colors = { "w", "b" };
        for (String color : colors) {
            for (PieceId id : PieceId.values()) {
                try {
                    // "wK.png" -> white king
                    String name = color + id.getAbrvName();
                    BufferedImage icon = ImageIO.read(new File("assets/" + name + ".png"));
                    pieceIcons.put(name, icon);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Returns the piece's icon scaled as specified
     */
    public static ImageIcon getPieceIcon(Piece piece, int height, int width) {
        if (height == 0 || width == 0) {
            return new ImageIcon(pieceIcons.get(piece.toString()));
        }
        
        String key = piece.toString();
        if (pieceIconsScaled.containsKey(key)) {
            ImageIcon icon = pieceIconsScaled.get(key);
            if (icon.getIconHeight() == height && icon.getIconWidth() == width) {
                return icon;
            }
        }

        ImageIcon scaled = new ImageIcon(pieceIcons.get(key).getScaledInstance(width, height, Image.SCALE_SMOOTH));
        pieceIconsScaled.put(key, scaled);
        return scaled;
    }
}
