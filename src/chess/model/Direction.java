package chess.model;

/**
 * Directions on a chess board, assuming the origin (southwest) is position (0,0)
 */
public enum Direction
{
    NORTH(0, 1), 
    SOUTH(0, -1), 
    EAST(1, 0), 
    WEST(-1, 0), 
    NORTHEAST(1, 1), 
    NORTHWEST(-1, 1), 
    SOUTHEAST(1, -1), 
    SOUTHWEST(-1, -1);
    
    public static final Direction[] Orthogonals =
        {
        NORTH,
        SOUTH,
        EAST,
        WEST
        };
    
    public static final Direction[] Diagonals =
        {
        NORTHEAST,
        NORTHWEST,
        SOUTHEAST,
        SOUTHWEST
        };
    
    public int x;
    public int y;
    
    /**
     * Direction constructor
     */
    private Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
