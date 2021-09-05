package chess.model.move;

/**
 * Move selection type
 */
public enum MoveType {
    /** Move to empty square only */
    MOVE_ONLY,

    /** Move to square with an enemy piece only */
    ATTACK_ONLY,

    /** Move to any square */
    ANY;
}
