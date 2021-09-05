package chess.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import chess.model.engine.Engine;
import chess.model.engine.EngineListener;
import chess.model.move.Move;
import chess.model.move.MoveManager;
import chess.model.piece.Piece;

/**
 * Represents a chess game
 */
public class Game implements EngineListener {
    /** This game's board */
    private final Board board;
    public Board getBoard() {
        return this.board;
    }

    /** This game's players */
    private final Player player1;
    private final Player player2;
    public Player getPlayer1() {
        return this.player1;
    }
    public Player getPlayer2() {
        return this.player2;
    }

    /** The player whose turn it is */
    private Player activePlayer;
    public Player getActivePlayer() {
        return this.activePlayer;
    }

    /** Handles move execution, undo/redo, history */
    private final MoveManager moveManager;

    /** Listeners for game events */
    private final List<GameListener> listeners = new ArrayList<GameListener>();

    /**
     * Game constructor
     */
    public Game(Player player1, Player player2) {
        // Use the standard setup
        this(player1, player2, Board.getStandard());
    }

    /**
     * Game constructor
     */
    public Game(Player player1, Player player2, Board board) {
        // Store the board
        this.board = board;

        // Initialize players
        this.player1 = player1;
        this.player2 = player2;
        this.activePlayer = this.player1;
        this.player1.setOpponent(this.player2);
        this.player2.setOpponent(this.player1);

        // Subscribe to engine events
        Engine player1Engine = this.player1.getEngine();
        if (player1Engine != null) {
            player1Engine.addEngineListener(this);
        }
        Engine player2Engine = this.player2.getEngine();
        if (player2Engine != null) {
            player2Engine.addEngineListener(this);
        }

        // Initialize the move manager
        this.moveManager = new MoveManager(this);
    }

    /**
     * Returns a deep copy of this game
     */
    public Game copy() {
        return new Game(this);
    }

    /**
     * Starts a new game
     */
    public void startNewGame() {
        this.activePlayer = this.player1;
        this.board.reset();
        this.moveManager.reset();
    }

    @Override
    /**
     * An engine has selected a move
     */
    public void engineMoveSelected(Move move) {
        this.executeMove(move);
        this.endTurn();
    }

    /**
     * Ends the current turn
     */
    public void endTurn() {
        this.endTurn(true);
    }

    /**
     * Ends the current turn
     */
    public void endTurn(boolean history) {
        // Swap the active player
        this.activePlayer = this.activePlayer.getOpponent();

        if (history) {
            // Turn completed
            for (GameListener listener : this.listeners) {
                listener.turnCompleted();
            }

            // No valid moves?
            int moves = this.getValidMoves(this.activePlayer).size();
            if (moves == 0) {
                // Checkmate if we're in check
                if (this.isCheck()) {
                    Player winner = this.activePlayer.getOpponent();
                    GameResult result = new GameResult(GameResult.CHECKMATE, winner);
                    for (GameListener listener : this.listeners) {
                        listener.gameCompleted(result);
                    }
                }

                // Stalemate otherwise
                else {
                    GameResult result = new GameResult(GameResult.DRAW, null);
                    for (GameListener listener : this.listeners) {
                        listener.gameCompleted(result);
                    }
                }
            }

            // Otherwise, there is at least one valid move, so start the next turn
            else {
                this.activePlayer.takeTurn();
            }
        }
    }

    /**
     * Subscribe to game events
     */
    public void addGameListener(GameListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Unsubscribe to game events
     */
    public void removeGameListener(GameListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * Returns whether an engine is running
     */
    public boolean isEngineRunning() {
        Engine engine = this.activePlayer.getEngine();
        if (engine != null) {
            return engine.getIsRunning();
        }
        return false;
    }

    /**
     * Returns whether the active player is in check
     */
    public boolean isCheck() {
        // Locate the active player's king
        Point kingPosition = this.board.getKingPosition(this.activePlayer);

        // Return whether the active player's opponent is threatening the king
        List<Move> moves = this.moveManager.getMoves(this.activePlayer.getOpponent());
        return moves.contains(new Move(kingPosition));
    }

    /**
     * Returns whether the specified player has been checkmated, i.e., is in check
     * and has no valid moves
     */
    public boolean isCheckmate() {
        return this.isCheck() && this.getValidMoves(this.activePlayer).size() == 0;
    }

    /**
     * Returns whether a draw has been forced by stalemate TODO:DML Threefold
     * repetition
     */
    public boolean isDraw() {
        return !this.isCheck() && this.getValidMoves(this.activePlayer).size() == 0;
    }

    /**
     * Returns the most recent move, if any
     */
    public Move getMostRecentMove() {
        List<Move> history = this.getMoveHistory();
        int size = history.size();
        if (size > 0) {
            return history.get(size - 1);
        }
        return null;
    }

    /**
     * Returns this game's full move history, in order
     */
    public List<Move> getMoveHistory() {
        return this.moveManager.getHistory();
    }

    /**
     * Returns all of the specified player's valid moves
     */
    public List<Move> getValidMoves(Player player) {
        return this.moveManager.getValidMoves(player);
    }

    /**
     * Returns all of the specified piece's valid moves
     */
    public List<Move> getValidMoves(Piece piece) {
        return this.moveManager.getValidMoves(piece);
    }

    /**
     * Returns whether the given move is valid
     */
    public boolean isValidMove(Move move) {
        for (Move m : this.getValidMoves(move.fromPiece)) {
            if (move.from.equals(m.from) && move.to.equals(m.to)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Executes the specified move
     */
    public void executeMove(Move move) {
        this.moveManager.execute(move);
    }

    /**
     * Executes the specified move
     */
    public void executeMove(Move move, boolean history) {
        this.moveManager.execute(move, history);
    }

    /**
     * Undo the specified move
     */
    public void undoMove(Move move, boolean history) {
        this.moveManager.undo(move, history);
    }

    /**
     * Undo a pair of moves
     */
    public void undo() {
        if (this.moveManager.undo()) {
            // Notify listeners
            for (GameListener listener : this.listeners) {
                listener.undoCompleted();
            }
        }
    }

    /**
     * Returns whether undo is possible
     */
    public boolean isUndoAvailable() {
        return this.moveManager.canUndo();
    }

    /**
     * Redo a pair of moves
     */
    public void redo() {
        if (this.moveManager.redo()) {
            // Notify listeners
            for (GameListener listener : this.listeners) {
                listener.redoCompleted();
            }
        }
    }

    /**
     * Returns whether redo is possible
     */
    public boolean isRedoAvailable() {
        return this.moveManager.canRedo();
    }

    @Override
    /**
     * Unused
     */
    public void engineProgressUpdated(double progress, long moveCount, long hashMapHits) {
    }

    /**
     * Game copy constructor
     */
    private Game(Game other) {
        // Perform deep copies
        this.player1 = other.player1.copy();
        this.player2 = other.player2.copy();
        this.board = other.board.copy();
        this.moveManager = other.moveManager.copy(this);

        // Initialize player relationships
        this.player1.setOpponent(this.player2);
        this.player2.setOpponent(this.player1);
        this.activePlayer = other.activePlayer == other.player1 ? this.player1 : this.player2;
    }

    @Override
    /**
     * Auto-generated hashCode()
     */
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((activePlayer == null) ? 0 : activePlayer.hashCode());
        result = prime * result + ((board == null) ? 0 : board.hashCode());
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
        Game other = (Game) obj;
        if (activePlayer == null) {
            if (other.activePlayer != null)
                return false;
        } else if (!activePlayer.equals(other.activePlayer))
            return false;
        if (board == null) {
            if (other.board != null)
                return false;
        } else if (!board.equals(other.board))
            return false;
        return true;
    }
}
