package chess.model.engine;

import java.util.HashMap;
import java.util.Map;

import chess.model.Board;
import chess.model.Player;
import chess.model.piece.Piece;
import chess.model.piece.PieceColor;
import chess.model.piece.PieceId;

/**
 * Evaluates boards based on piece locations and values
 * 
 * https://github.com/lhartikk/simple-chess-ai
 */
public class BoardEvaluator {
    private static double whiteKing[][];
    static {
        whiteKing = new double[][] { { 2.0, 2.0, -1.0, -2.0, -3.0, -3.0, -3.0, -3.0 },
                { 2.0, 2.0, -2.0, -3.0, -4.0, -4.0, -4.0, -4.0 }, { 5.0, 0.0, -2.0, -3.0, -4.0, -4.0, -4.0, -4.0 },
                { 0.0, 0.0, -2.0, -4.0, -5.0, -5.0, -5.0, -5.0 }, { 0.0, 0.0, -2.0, -4.0, -5.0, -5.0, -5.0, -5.0 },
                { 1.0, 0.0, -2.0, -3.0, -4.0, -4.0, -4.0, -4.0 }, { 5.0, 2.0, -2.0, -3.0, -4.0, -4.0, -4.0, -4.0 },
                { 2.0, 2.0, -1.0, -2.0, -3.0, -3.0, -3.0, -3.0 }, };
    }

    private static double blackKing[][];
    static {
        blackKing = reverseInteriorArrays(whiteKing);
    }

    private static double whitePawn[][];
    static {
        whitePawn = new double[][] { { 0.0, 0.5, 0.5, 0.0, 0.5, 1.0, 5.0, 0.0 },
                { 0.0, 1.0, -0.5, 0.0, 0.5, 1.0, 5.0, 0.0 }, { 0.0, 1.0, -1.0, 0.0, 1.0, 2.0, 5.0, 0.0 },
                { 0.0, -2.0, 0.0, 2.0, 2.5, 3.0, 5.0, 0.0 }, { 0.0, -2.0, 0.0, 2.0, 2.5, 3.0, 5.0, 0.0 },
                { 0.0, 1.0, -1.0, 0.0, 1.0, 2.0, 5.0, 0.0 }, { 0.0, 1.0, -0.5, 0.0, 0.5, 1.0, 5.0, 0.0 },
                { 0.0, 0.5, 0.5, 0.0, 0.5, 1.0, 5.0, 0.0 }, };
    }

    private static double blackPawn[][];
    static {
        blackPawn = reverseInteriorArrays(whitePawn);
    }

    private static double knight[][];
    static {
        knight = new double[][] { { -5, -4, -3, -3, -3, -3, -4, -5 }, { -4, -2, .5, 0, .5, 0, -2, -4 },
                { -3, 0, 1, 1.5, 1.5, 1, 0, -3 }, { -3, .5, 1.5, 2, 2, 1.5, 0, -3 }, { -3, .5, 1.5, 2, 2, 1.5, 0, -3 },
                { -3, 0, 1, 1.5, 1.5, 1, 0, -3 }, { -4, -2, .5, 0, .5, 0, -2, -4 },
                { -5, -4, -3, -3, -3, -3, -4, -5 }, };
    }

    private static double whiteBishop[][];
    static {
        whiteBishop = new double[][] { { -2, -1, -1, -1, -1, -1, -1, -2 }, { -1, .5, 1, 0, .5, 0, 0, -1 },
                { -1, 0, 1, 1, .5, .5, 0, -1 }, { -1, 0, 1, 1, 1, 1, 0, -1 }, { -1, 0, 1, 1, 1, 1, 0, -1 },
                { -1, 0, 1, 1, .5, .5, 0, -1 }, { -1, .5, 1, 0, .5, 0, 0, -1 }, { -2, -1, -1, -1, -1, -1, -1, -2 }, };
    }

    private static double blackBishop[][];
    static {
        blackBishop = reverseInteriorArrays(whiteBishop);
    }

    private static double whiteRook[][];
    static {
        whiteRook = new double[][] { { 0, -.5, -.5, -.5, -.5, -.5, .5, 0 }, { 0, 0, 0, 0, 0, 0, 1, 0 },
                { 0, 0, 0, 0, 0, 0, 1, 0 }, { .5, 0, 0, 0, 0, 0, 1, 0 }, { .5, 0, 0, 0, 0, 0, 1, 0 },
                { 0, 0, 0, 0, 0, 0, 1, 0 }, { 0, 0, 0, 0, 0, 0, 1, 0 }, { 0, -.5, -.5, -.5, -.5, -.5, .5, 0 }, };
    }

    private static double blackRook[][];
    static {
        blackRook = reverseInteriorArrays(whiteRook);
    }

    private static double whiteQueen[][];
    static {
        whiteQueen = new double[][] { { -2, -1, -1, 0, -.5, -1, -1, -2 }, { -1, 0, .5, 0, 0, 0, 0, -1 },
                { -1, .5, .5, .5, .5, .5, 0, -.5 }, { -.5, 0, .5, .5, .5, .5, 0, -.5 },
                { -.5, 0, .5, .5, .5, .5, 0, -.5 }, { -1, .5, .5, .5, .5, .5, 0, -.5 }, { -1, 0, .5, 0, 0, 0, 0, -1 },
                { -2, -1, -1, 0, -.5, -1, -1, -2 }, };
    }

    private static double blackQueen[][];
    static {
        blackQueen = reverseInteriorArrays(whiteQueen);
    }

    private static Map<PieceId, double[][]> whiteLUT;
    static {
        whiteLUT = new HashMap<PieceId, double[][]>();
        whiteLUT.put(PieceId.KING, whiteKing);
        whiteLUT.put(PieceId.QUEEN, whiteQueen);
        whiteLUT.put(PieceId.ROOK, whiteRook);
        whiteLUT.put(PieceId.BISHOP, whiteBishop);
        whiteLUT.put(PieceId.KNIGHT, knight);
        whiteLUT.put(PieceId.PAWN, whitePawn);
    }

    private static Map<PieceId, double[][]> blackLUT;
    static {
        blackLUT = new HashMap<PieceId, double[][]>();
        blackLUT.put(PieceId.KING, blackKing);
        blackLUT.put(PieceId.QUEEN, blackQueen);
        blackLUT.put(PieceId.ROOK, blackRook);
        blackLUT.put(PieceId.BISHOP, blackBishop);
        blackLUT.put(PieceId.KNIGHT, knight);
        blackLUT.put(PieceId.PAWN, blackPawn);
    }

    public static double evaluate(Player enginePlayer, Board board) {
        double result = 0.0;
        Map<PieceId, double[][]> LUT = enginePlayer.getColor() == PieceColor.WHITE ? whiteLUT : blackLUT;
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                Piece piece = board.getSquare(i, j).getPiece();
                if (piece != null) {
                    PieceId id = piece.getId();
                    double value = id.getValue() + LUT.get(id)[i][j];
                    if (piece.getColor() == enginePlayer.getColor()) {
                        value = -value;
                    }
                    result += value;
                }
            }
        }
        return result;
    }

    private static double[][] reverseInteriorArrays(double[][] arr) {
        double[][] ret = new double[arr.length][];
        for (int i = 0; i < arr.length; i++) {
            ret[i] = reverse(arr[i]);
        }
        return ret;
    }

    private static double[] reverse(double[] arr) {
        double[] ret = new double[arr.length];
        int idx = 0;
        for (int i = arr.length - 1; i >= 0; i--) {
            ret[idx] = arr[i];
            idx += 1;
        }
        return ret;
    }
}
