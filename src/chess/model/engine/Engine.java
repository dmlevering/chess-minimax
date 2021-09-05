package chess.model.engine;

import java.util.ArrayList;
import java.util.List;

import chess.model.Game;
import chess.model.Player;
import chess.model.move.Move;

public abstract class Engine {
    protected Game game;
    public void setGame(Game game) {
        this.game = game;
    }

    protected Player player;
    public void setPlayer(Player player) {
        this.player = player;
    }

    /** Maximum number of worker threads */
    protected int threadCount;
    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    /** Is the engine thread running? */
    private volatile boolean isRunning = false;
    public boolean getIsRunning() {
        return this.isRunning;
    }

    /** Classes subscribed to this engine's events */
    private List<EngineListener> listeners = new ArrayList<EngineListener>();

    /**
     * Engine constructor
     */
    public Engine(int threadCount) {
        this.threadCount = threadCount;
    }

    /**
     * Subscribe to engine events
     */
    public void addEngineListener(EngineListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Unsubscribe from engine events
     */
    public void removeEngineListener(EngineListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * Selects a move based on the current game state
     */
    public abstract Move selectMove();

    /**
     * Runs the engine on a new thread
     */
    public void run() {
        Engine engine = this;
        class EngineThread extends Thread {
            @Override
            public void run() {
                engine.isRunning = true;
                Move move = engine.selectMove();
                engine.isRunning = false;
                for (EngineListener listener : engine.listeners) {
                    listener.engineMoveSelected(move);
                }
            }
        }

        EngineThread thread = new EngineThread();
        thread.start();
    }

    /**
     * Reports progress towards selecting a move
     */
    protected void setProgress(double progress, long moveCount, long hashMapHits) {
        for (EngineListener listener : this.listeners) {
            listener.engineProgressUpdated(progress, moveCount, hashMapHits);
        }
    }
}
