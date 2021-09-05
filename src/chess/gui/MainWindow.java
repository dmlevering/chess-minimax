package chess.gui;

import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import chess.model.Game;
import chess.model.GameListener;
import chess.model.GameResult;
import chess.model.Player;
import chess.model.engine.Engine;
import chess.model.engine.EngineListener;
import chess.model.engine.MinimaxEngine;
import chess.model.move.Move;
import chess.model.piece.PieceColor;

public class MainWindow implements ActionListener, ItemListener, GameListener, EngineListener
{
    /** Chess game */
    private GuiBoard board;
    private Game game;
    private Engine minimaxEngine;
    
    /** Default engine options */
    private int engineThreadCount = Math.max(1, Runtime.getRuntime().availableProcessors() / 2);
    private int engineDepth = 3;
    
    /** Toolstrip menu */
    private JMenuBar mbMain;
    private JMenuItem miNewGame, miEngineOptions, miMoveHistory;
    
    /** Displays current game state */
    private JTextField tfInfo;
    
    /** Engine move selection progress bar */
    private JProgressBar pbEngine;
    
    /** Engine selection */
    private JRadioButtonMenuItem rbNoEngine, rbMinimaxEngine;

    /** Undo and redo buttons */
    private JButton bUndo, bRedo;

    /** Show possible moves checkbox */
    private JCheckBoxMenuItem cbmiShowPossibleMoves;
    
    /** The main window */
    private JFrame window;
    
    /**
     * Start the GUI
     */
    public void start()
    {
        // Run GUI on the event-dispatching thread
        MainWindow mainWindow = this;
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                try
                {
                    // Set the system look and feel
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    
                    window = new JFrame("Chess");
                    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    Dimension size = new Dimension(800, 600);
                    Dimension minSize = new Dimension(800, 600);
                    window.setSize(size);
                    window.setMinimumSize(minSize);
                    window.setBackground(Color.black);
                    window.setVisible(true);
                    GuiPiece.loadPieceIcons();
                    createMenu();
                    
                    minimaxEngine = new MinimaxEngine(engineThreadCount);
                    
                    Player player1 = new Player(PieceColor.WHITE, null);
                    Player player2 = new Player(PieceColor.BLACK, minimaxEngine);

                    game = new Game(player1, player2);
                    game.addGameListener(mainWindow);
                    board = new GuiBoard(game, window.getSize(), true);
                    
                    minimaxEngine.setGame(game);
                    minimaxEngine.addEngineListener(mainWindow);
                    setEngineOptions(engineDepth, engineThreadCount);
                    
                    // Wrap the board in another JPanel to lock 1:1 aspect ratio
                    JPanel squarePanel = new JPanel(new GridBagLayout());
                    squarePanel.setBackground(Color.black);
                    squarePanel.add(board);
                    window.add(squarePanel);
                    window.setContentPane(squarePanel);
                    
                    // Redraw the board when the window is resized
                    window.addComponentListener(new ComponentAdapter()
                    {
                        public void componentResized(ComponentEvent componentEvent)
                        {
                            board.redraw();
                        }
                    });
                    
                    // Start a new game
                    newGame();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    /**
     * Handle button clicks
     */
    public void itemStateChanged(ItemEvent e)
    {
        if(this.board == null)
        {
            return;
        }
        
        Object source = e.getSource();
        if(source == cbmiShowPossibleMoves)
        {
            this.board.setShowPossibleMoves(cbmiShowPossibleMoves.isSelected());
        }
        else if(source == rbNoEngine || source == rbMinimaxEngine)
        {
            Player player2 = this.game.getPlayer2();
            if(rbNoEngine.isSelected())
            {
                player2.setEngine(null);
            }
            else if(rbMinimaxEngine.isSelected())
            {
                player2.setEngine(this.minimaxEngine);
            }
        }
    }

    @Override
    /**
     * Handle button clicks
     */
    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();
        if(source == bUndo)
        {
            this.board.undo();
        }
        else if(source == bRedo)
        {
            this.board.redo();
        }
        else if(source == miMoveHistory)
        {
            this.showMoveHistory();
        }
        else if(source == miNewGame)
        {
            this.newGame();
        }
        else if(source == miEngineOptions)
        {
            this.showEngineOptions();
        }
    }
    
    /**
     * Create the menu
     */
    private void createMenu()
    {
        mbMain = new JMenuBar();
        
        JMenu menu = new JMenu("Game");
        mbMain.add(menu);
        
        miNewGame = new JMenuItem("New Game");
        miNewGame.addActionListener(this);
        menu.add(miNewGame);
        
        cbmiShowPossibleMoves = new JCheckBoxMenuItem("Show Possible Moves");
        cbmiShowPossibleMoves.addItemListener(this);
        cbmiShowPossibleMoves.setSelected(true);
        menu.add(cbmiShowPossibleMoves);
        
        JMenu submenuEngine = new JMenu("Engine");
        ButtonGroup groupEngine = new ButtonGroup();
        rbNoEngine = new JRadioButtonMenuItem("No Engine");
        rbNoEngine.setSelected(false);
        rbNoEngine.addItemListener(this);
        groupEngine.add(rbNoEngine);
        submenuEngine.add(rbNoEngine);
        
        rbMinimaxEngine = new JRadioButtonMenuItem("Minimax Engine");
        rbMinimaxEngine.setSelected(true);
        rbMinimaxEngine.addItemListener(this);
        groupEngine.add(rbMinimaxEngine);
        submenuEngine.add(rbMinimaxEngine);
        menu.add(submenuEngine);
        
        miEngineOptions = new JMenuItem("Engine Options");
        miEngineOptions.addActionListener(this);
        menu.add(miEngineOptions);
        
        miMoveHistory = new JMenuItem("Move History");
        miMoveHistory.addActionListener(this);
        menu.add(miMoveHistory);
        
        JPanel pInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        tfInfo = new JTextField();
        tfInfo.setColumns(20);
        tfInfo.setEditable(false);
        tfInfo.setMaximumSize(tfInfo.getPreferredSize());
        
        pbEngine = new JProgressBar(0, 100);
        pbEngine.setPreferredSize(new Dimension(300, tfInfo.getPreferredSize().height + 2));
        pbEngine.setStringPainted(true);
        
        pInfo.add(tfInfo);
        pInfo.add(pbEngine);
        pInfo.setPreferredSize(new Dimension(400, 30));
        pInfo.setOpaque(false);
        mbMain.add(pInfo);
        
        // Right-align subsequent menu items
        mbMain.add(Box.createHorizontalGlue());
        
        JPanel pHistory = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        bUndo = new JButton("Undo");
        bUndo.addActionListener(this);
        bUndo.setEnabled(false);
        
        bRedo = new JButton("Redo");
        bRedo.addActionListener(this);
        bRedo.setEnabled(false);
        
        pHistory.add(bUndo);
        pHistory.add(bRedo);
        pHistory.setPreferredSize(new Dimension(30, 30));
        pHistory.setOpaque(false);
        mbMain.add(pHistory);
        
        window.add(mbMain);
        window.setJMenuBar(mbMain);
    }

    @Override
    /**
     * Called when a turn ends
     */
    public void turnCompleted()
    {
        this.refreshUndoRedo();
        tfInfo.setText("Turn: " + this.game.getActivePlayer().getColor().toString());
        pbEngine.setValue(0);
        
        this.board.redraw();
    }

    @Override
    /**
     * Unused
     */
    public void engineMoveSelected(Move move)
    {

    }

    @Override
    /**
     * Handles engine progress reports
     */
    public void engineProgressUpdated(double progress, long moveCount, long hashMapHits)
    {
        if(progress < 100.0)
        {
            pbEngine.setValue((int)(progress * 100));
            pbEngine.setString(String.format("%,d moves evaluated, %,d hashmap hits", moveCount, hashMapHits));
        }
    }

    @Override
    /**
     * Undo operation completed
     */
    public void undoCompleted()
    {
        this.refreshUndoRedo();
    }

    @Override
    /**
     * Redo operation completed
     */
    public void redoCompleted()
    {
        this.refreshUndoRedo();
    }
    
    /**
     * Refresh the undo/redo buttons
     */
    private void refreshUndoRedo()
    {
        bUndo.setEnabled(this.game.isUndoAvailable());
        bRedo.setEnabled(this.game.isRedoAvailable());
    }

    @Override
    /**
     * Called when the game is over
     */
    public void gameCompleted(GameResult result)
    {
        switch(result.result)
        {
        case GameResult.CHECKMATE:
            tfInfo.setText("Checkmate - " + result.winner.getColor().toString() + " wins!");
            break;
            
        case GameResult.DRAW:
            tfInfo.setText("Game over: Draw");
            break;
            
        default:
            break;
        }
    }
    
    /**
     * Shows move history dialog
     */
    private void showMoveHistory()
    {
        List<Move> moves = this.game.getMoveHistory();
        StringBuilder str = new StringBuilder();
        for( int i = 0; i < moves.size(); i++)
        {
            str.append((i + 1) + ". " + moves.get(i).toString() + "\n");
        }
        
        JTextArea display = new JTextArea(16, 58);
        display.setText(str.toString());
        display.setEditable(false);
        JScrollPane scroll = new JScrollPane(display);
        JPanel panel = new JPanel();
        panel.add(scroll);

        JDialog dialog = new JDialog(this.window, "Move History");
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this.window);
        dialog.setModalityType(ModalityType.APPLICATION_MODAL);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }
    
    /**
     * Starts a new game
     */
    private void newGame()
    {
        this.game.startNewGame();
        tfInfo.setText("Turn: " + this.game.getActivePlayer().getColor().toString());
        this.board.redraw();
        this.refreshUndoRedo();
    }
    
    /**
     * Displays engine options dialog
     */
    private void showEngineOptions() {
        // Engine thread count
        JComboBox<Integer> threads = new JComboBox<Integer>();
        int cores = Runtime.getRuntime().availableProcessors();
        for (int i = 1; i <= cores; i++) {
            threads.addItem(i);
        }
        threads.setSelectedItem(this.engineThreadCount);
        
        // Engine search depth
        JComboBox<Integer> depth = new JComboBox<Integer>();
        for (int i = 1; i <= 5; i++) {
            depth.addItem(i);
        }
        depth.setSelectedItem(this.engineDepth);
        
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Search depth:"));
        panel.add(depth);
        panel.add(new JLabel("Threads:"));
        panel.add(threads);
        
        // Show the dialog
        int result = JOptionPane.showConfirmDialog(this.window, panel, "Engine Options",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            this.setEngineOptions((int)depth.getSelectedItem(), (int)threads.getSelectedItem());
        }
    }
    
    /**
     * Sets engine options
     */
    private void setEngineOptions(int depth, int threads) {
        this.engineThreadCount = threads;
        this.engineDepth = depth;
        
        Engine engine = this.game.getPlayer2().getEngine();
        if (engine != null) {
            engine.setThreadCount(this.engineThreadCount);
            MinimaxEngine minimax = (MinimaxEngine)engine;
            if (minimax != null) {
                minimax.setDepth(this.engineDepth);
            }
        }
    }
}
