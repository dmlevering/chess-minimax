package chess.model.move;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import chess.model.Board;
import chess.model.Game;
import chess.model.Player;
import chess.model.piece.Piece;

/**
 * Manages move execution, undo/redo, history
 */
public class MoveManager {
    /** The game model */
    private Game game;

    /** Undo and redo moves in pairs */
    public boolean canUndo() {
        return this.undoStack.size() >= 2 && this.undoStack.size() % 2 == 0;
    }
    public boolean canRedo() {
        return this.redoStack.size() >= 2 && this.redoStack.size() % 2 == 0;
    }
    private ArrayDeque<Move> undoStack;
    private ArrayDeque<Move> redoStack;

    /** Move history (most recent move last) */
    private ArrayDeque<Move> history;

    /**
     * MoveManager constructor
     */
    public MoveManager(Game game) {
        this.game = game;
        this.undoStack = new ArrayDeque<Move>();
        this.redoStack = new ArrayDeque<Move>();
        this.history = new ArrayDeque<Move>();
    }

    /**
     * Returns a deep copy of this move manager
     */
    public MoveManager copy(Game game) {
        return new MoveManager(this, game);
    }
    
    /**
     * Resets this move manager
     */
    public void reset() {
        this.undoStack = new ArrayDeque<Move>();
        this.redoStack = new ArrayDeque<Move>();
        this.history = new ArrayDeque<Move>();
    }

    /**
     * Executes a move
     */
    public void execute(Move move) {
        this.execute(move, true);
        this.redoStack.clear();
    }

    /**
     * Executes a move
     */
    public void execute(Move move, boolean history) {
        move.execute(this.game);
        if (history) {
            this.undoStack.push(move);
            this.history.addLast(move);
        }
    }

    /**
     * Undo a move
     */
    public void undo(Move move, boolean history) {
        move.undo(this.game);
        if (history) {
            this.redoStack.push(move);
            this.history.removeLast();
        }
    }

    /**
     * Returns all of the specified player's valid moves
     */
    public List<Move> getValidMoves(Player player) {
        List<Move> moves = new ArrayList<Move>();
        for (Move move : this.getMoves(player)) {
            if (move.isValid(this.game)) {
                moves.add(move);
            }
        }
        return moves;
    }

    /**
     * Returns all of the specified piece's valid moves
     */
    public List<Move> getValidMoves(Piece piece) {
        List<Move> moves = new ArrayList<Move>();
        for (Move move : this.getMoves(piece)) {
            if (move.isValid(this.game)) {
                moves.add(move);
            }
        }
        return moves;
    }

    /**
     * Returns all of the specified player's possible moves
     * 
     * Note: This function may return moves that would put this player in check.
     * These moves are filtered elsewhere.
     */
    public List<Move> getMoves(Player player) {
        List<Move> moves = new ArrayList<Move>();
        Board board = this.game.getBoard();
        for (Piece piece : board.getPieces(player.getColor())) {
            moves.addAll(piece.getMoves(board));
        }
        return moves;
    }

    /**
     * Redo a pair of moves
     */
    public boolean redo() {
        if (!canRedo()) {
            return false;
        }

        Move first = this.redoStack.pop();
        Move second = this.redoStack.pop();
        this.execute(first, true);
        this.execute(second, true);
        return true;
    }

    /**
     * Undo a pair of moves
     */
    public boolean undo() {
        if (!canUndo()) {
            return false;
        }

        Move first = this.undoStack.pop();
        Move second = this.undoStack.pop();
        this.undo(first, true);
        this.undo(second, true);
        return true;
    }

    /**
     * Returns the move history
     */
    public List<Move> getHistory() {
        return new ArrayList<Move>(this.history);
    }

    /**
     * Returns all of the specified piece's possible moves
     * 
     * Note: This function may return moves that would put this player in check.
     * These moves are filtered elsewhere.
     */
    public List<Move> getMoves(Piece piece) {
        return piece.getMoves(this.game.getBoard());
    }

    /**
     * MoveManager copy constructor
     */
    private MoveManager(MoveManager other, Game game) {
        this.game = game;

        // TODO:DML
        this.undoStack = other.undoStack.clone();
        this.redoStack = other.redoStack.clone();
        this.history = other.history.clone();
    }
}
