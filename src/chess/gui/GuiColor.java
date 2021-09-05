package chess.gui;

import java.awt.Color;

/**
 * Chess GUI colors
 */
public class GuiColor {
    /** Dark squares color */
    public static final Color DARK = new Color(110, 155, 88);

    /** Light squares color */
    public static final Color LIGHT = new Color(238, 240, 212);

    /** Default square border color */
    public static final Color BORDER_DEFAULT = new Color(0, 0, 0, 0);

    /** Selected square border color */
    public static final Color BORDER_SELECTED = new Color(180, 205, 62);

    /** Moved from square color */
    public static final Color MOVE_FROM = new Color(180, 205, 62);

    /** Moved to square color */
    public static final Color MOVE_TO = new Color(246, 246, 130);

    /** Possible move circle color */
    public static final Color MOVE_POSSIBLE = new Color(89, 89, 89, 120);
}
