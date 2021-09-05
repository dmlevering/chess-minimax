package chess.model.engine;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import chess.model.Game;
import chess.model.move.Move;

/**
 * Minimax chess engine
 * 
 * https://www.chessprogramming.org/Minimax
 * https://github.com/lhartikk/simple-chess-ai
 */
public class MinimaxEngine extends Engine {
    /** How many moves ahead to look */
    private int depth = 3;
    public void setDepth(int depth) {
        this.depth = depth;
    }

    /** Thread-safe hashmap to avoid evaluating repeat boards */
    private Map<Integer, Double> hashMap = new ConcurrentHashMap<Integer, Double>();

    /**
     * MinimaxEngine constructor
     */
    public MinimaxEngine(int threadCount) {
        super(threadCount);
    }

    @Override
    /**
     * Selects a move based on the current game state
     */
    public Move selectMove() {
        // Get all available moves
        Game game = this.game.copy();
        List<Move> moves = game.getValidMoves(this.player);
        Collections.shuffle(moves);

        // Create a thread pool
        Executor executor = Executors.newFixedThreadPool(this.threadCount);
        CompletionService<MinimaxEngineWorker> service = new ExecutorCompletionService<MinimaxEngineWorker>(executor);

        // Determine the value of each move using the thread pool
        for (Move move : moves) {
            service.submit(new MinimaxEngineWorker(game, move, this.hashMap));
        }

        Move bestMove = null;
        double bestMoveValue = Double.NEGATIVE_INFINITY;
        int movesSize = moves.size();
        long moveCount = 0;
        long hashMapHits = 0;
        for (int i = 0; i < movesSize; i++) {
            try {
                // Block until a worker thread finishes
                MinimaxEngineWorker worker = service.take().get();

                // Report progress
                moveCount += worker.moveCount;
                hashMapHits += worker.hashMapHits;
                this.setProgress((double) i / movesSize, moveCount, hashMapHits);

                // Update the best move
                if (bestMove == null || worker.moveValue >= bestMoveValue) {
                    bestMove = worker.move;
                    bestMoveValue = worker.moveValue;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        
        // Clear the hashmap
        this.hashMap.clear();
        Runtime.getRuntime().gc();

        return bestMove;
    }

    /**
     * Thread pool worker to determine the value of a given move
     */
    class MinimaxEngineWorker implements Callable<MinimaxEngineWorker> {
        /** This worker's game instance */
        private Game game;

        /** The top-level move to test */
        public Move move;

        /** The move's determined value */
        public double moveValue;

        /** The number of moves tested */
        public long moveCount;

        /** Thread-safe hashmap to avoid evaluating repeat boards */
        private Map<Integer, Double> hashMap;
        
        /** The number of hashmap hits */
        public long hashMapHits;

        /**
         * MinimaxEngineWorker constructor
         */
        public MinimaxEngineWorker(Game game, Move move, Map<Integer, Double> hashMap) {
            // Perform a deep copy so each worker has its own instance to manipulate
            this.game = game.copy();
            this.move = move.copy();
            this.moveCount = 0;
            this.hashMap = hashMap;
            this.hashMapHits = 0;
        }

        @Override
        /**
         * Determines the move's value
         */
        public MinimaxEngineWorker call() throws Exception {
            this.game.executeMove(this.move, false);
            this.game.endTurn(false);
            this.moveValue = minimax(depth, false, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
            return this;
        }

        /**
         * Performs the minimax algorithm to determine a move's value
         */
        private double minimax(int depth, boolean isMaximizingPlayer, double alpha, double beta) {
            // Check the hashmap for this board
            if (this.hashMap.containsKey(this.game.hashCode())) {
                try {
                    double value = this.hashMap.get(this.game.hashCode());
                    this.hashMapHits += 1;
                    return value;
                } catch (NullPointerException ex) {
                    System.out.println("Minimax - hashmap error");
                }
            }

            // Base case
            if (depth == 0) {
                double value = -BoardEvaluator.evaluate(player, this.game.getBoard());
                this.hashMap.put(this.game.hashCode(), value);
                return value;
            }

            List<Move> moves = this.game.getValidMoves(this.game.getActivePlayer());

            if (isMaximizingPlayer) {
                double bestValue = Double.NEGATIVE_INFINITY;
                for (Move move : moves) {
                    this.game.executeMove(move, false);
                    this.game.endTurn(false);
                    bestValue = Math.max(bestValue, minimax(depth - 1, !isMaximizingPlayer, alpha, beta));
                    this.game.undoMove(move, false);
                    this.game.endTurn(false);
                    this.moveCount += 1;

                    alpha = Math.max(alpha, bestValue);

                    // Alpha-Beta pruning
                    if (beta <= alpha) {
                        break;
                    }
                }
                return bestValue;
            } else {
                double bestValue = Double.POSITIVE_INFINITY;
                for (Move move : moves) {
                    this.game.executeMove(move, false);
                    this.game.endTurn(false);
                    bestValue = Math.min(bestValue, minimax(depth - 1, !isMaximizingPlayer, alpha, beta));
                    this.game.undoMove(move, false);
                    this.game.endTurn(false);
                    this.moveCount += 1;

                    beta = Math.min(beta, bestValue);

                    // Alpha-Beta pruning
                    if (beta <= alpha) {
                        break;
                    }
                }
                return bestValue;
            }
        }
    }
}
