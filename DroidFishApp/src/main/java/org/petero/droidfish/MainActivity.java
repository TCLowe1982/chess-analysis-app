package org.petero.droidfish;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.FrameLayout;
import org.petero.droidfish.gamelogic.Position;
import org.petero.droidfish.gamelogic.Piece;
import org.petero.droidfish.gamelogic.Move;
import java.util.ArrayList;
import android.text.TextUtils;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

public class MainActivity extends AppCompatActivity {
    
    // Home screen elements
    private LinearLayout homeLayout;
    private Button btnNewGame;
    private Button btnContinueGame;
    private Button btnExit;
    
    // Game screen elements
    private GridLayout chessBoard;
    private Button btnExit2;
    private TextView tvInstruction;
    private Button btnAttack;
    private Button btnDefense;
    private Button btnTrap;
    private Button btnCounter;
    
    // Game state
    private String currentStrategy = "";
    private Position position;
    private static final String STARTING_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    private ArrayList<ImageView> pieceViews = new ArrayList<>();
    private int selectedRow = -1;
    private int selectedCol = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize UI elements
        initializeViews();
        
        // Set up click listeners
        setupClickListeners();
    }
    
    private void initializeViews() {
        // Home screen elements
        homeLayout = findViewById(R.id.home);
        btnNewGame = findViewById(R.id.btnNewGame);
        btnContinueGame = findViewById(R.id.btnContinueGame);
        btnExit = findViewById(R.id.btnExit);
        
        // Game screen elements
        chessBoard = findViewById(R.id.chessBoard);
        btnExit2 = findViewById(R.id.btnExit2);
        tvInstruction = findViewById(R.id.tvInstruction);
        btnAttack = findViewById(R.id.btnAttack);
        btnDefense = findViewById(R.id.btnDefense);
        btnTrap = findViewById(R.id.btnTrap);
        btnCounter = findViewById(R.id.btnCounter);
    }
    
    private void setupClickListeners() {
        // Home screen buttons
        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewGame();
            }
        });
        
        btnContinueGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueGame();
            }
        });
        
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        // Game screen buttons
        btnExit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToHomeScreen();
            }
        });
        
        // Strategy buttons
        btnAttack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectStrategy("attack");
            }
        });
        
        btnDefense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectStrategy("defense");
            }
        });
        
        btnTrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectStrategy("trap");
            }
        });
        
        btnCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectStrategy("counter");
            }
        });
    }
    
    private void startNewGame() {
        // Hide home screen and show game screen
        homeLayout.setVisibility(View.GONE);
        showGameScreen();
        
        // Initialize new chess board
        initializeChessBoard();
    }

    private void continueGame() {
        // Hide home screen and show game screen
        homeLayout.setVisibility(View.GONE);
        showGameScreen();
        
        // Load saved game state
        loadGameState();
    }

    private void returnToHomeScreen() {
        // Hide game screen elements
        chessBoard.setVisibility(View.INVISIBLE);
        btnExit2.setVisibility(View.INVISIBLE);
        tvInstruction.setVisibility(View.INVISIBLE);
        btnAttack.setVisibility(View.INVISIBLE);
        btnDefense.setVisibility(View.INVISIBLE);
        btnTrap.setVisibility(View.INVISIBLE);
        btnCounter.setVisibility(View.INVISIBLE);
        
        // Show home screen
        homeLayout.setVisibility(View.VISIBLE);
    }

    private void showGameScreen() {
        // Show game screen elements
        chessBoard.setVisibility(View.VISIBLE);
        btnExit2.setVisibility(View.VISIBLE);
        tvInstruction.setVisibility(View.VISIBLE);
        btnAttack.setVisibility(View.VISIBLE);
        btnDefense.setVisibility(View.VISIBLE);
        btnTrap.setVisibility(View.VISIBLE);
        btnCounter.setVisibility(View.VISIBLE);
    }

    private void selectStrategy(String strategy) {
        currentStrategy = strategy;
        
        // Apply the selected strategy to the game
        switch(strategy) {
            case "attack":
                applyAttackStrategy();
                break;
            case "defense":
                applyDefenseStrategy();
                break;
            case "trap":
                applyTrapStrategy();
                break;
            case "counter":
                applyCounterStrategy();
                break;
        }
    }

    private void initializeChessBoard() {
        // Clear the chessboard
        chessBoard.removeAllViews();
        
        // Create chess board squares and pieces
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                boolean isWhiteSquare = (row + col) % 2 == 0;
                createBoardSquare(row, col, isWhiteSquare);
            }
        }
        
        // Set up initial piece positions
        setupInitialPieces();
    }

    private void createBoardSquare(int row, int col, boolean isWhite) {
        // Create a square view for the chess board
        View squareView = new View(this);
        int squareSize = chessBoard.getWidth() / 8;
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = squareSize;
        params.height = squareSize;
        params.rowSpec = GridLayout.spec(row);
        params.columnSpec = GridLayout.spec(col);
        
        // Set background color based on square color
        if (isWhite) {
            squareView.setBackgroundColor(getResources().getColor(android.R.color.white));
        } else {
            squareView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        }
        
        squareView.setTag(row + "," + col); // Store position for later reference
        chessBoard.addView(squareView, params);
    }

    private void setupInitialPieces() {
        // Initialize Position with starting position
        position = new Position();
        position.fromFEN(STARTING_FEN);
        
        // Clear any existing piece views
        for (ImageView pieceView : pieceViews) {
            if (pieceView.getParent() != null) {
                ((FrameLayout)pieceView.getParent()).removeView(pieceView);
            }
        }
        pieceViews.clear();
        
        // Place pieces on the board according to the position
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int square = Position.getSquare(col, 7-row); // Convert to internal position format
                int piece = position.getPiece(square);
                
                if (piece != Piece.EMPTY) {
                    addPiece(row, col, getPieceDrawableId(piece));
                }
            }
        }
    }

    private void addPiece(int row, int col, int pieceDrawableId) {
        // Find the square at the specified position
        String tag = row + "," + col;
        for (int i = 0; i < chessBoard.getChildCount(); i++) {
            View square = chessBoard.getChildAt(i);
            if (tag.equals(square.getTag())) {
                // Create a FrameLayout to hold both the square and the piece
                FrameLayout container = new FrameLayout(this);
                int squareSize = square.getLayoutParams().width;
                GridLayout.LayoutParams squareParams = (GridLayout.LayoutParams) square.getLayoutParams();
                
                // Create piece ImageView
                ImageView pieceView = new ImageView(this);
                pieceView.setImageResource(pieceDrawableId);
                pieceView.setLayoutParams(new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT));
                
                // Replace the square with the container
                chessBoard.removeView(square);
                container.addView(square);
                container.addView(pieceView);
                
                // Set the same layout params for the container
                container.setLayoutParams(squareParams);
                container.setTag(tag);
                
                // Add click listener for piece movement
                container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleSquareClick(row, col);
                    }
                });
                
                chessBoard.addView(container);
                pieceViews.add(pieceView);
                break;
            }
        }
    }

    private void loadGameState() {
        SharedPreferences prefs = getSharedPreferences("ChessAnalyzerPrefs", MODE_PRIVATE);
        String savedFen = prefs.getString("currentPosition", "");
        
        if (!TextUtils.isEmpty(savedFen)) {
            // Initialize the position with the saved FEN
            position = new Position();
            position.fromFEN(savedFen);
        } else {
            // If no saved position, use the starting position
            position = new Position();
            position.fromFEN(STARTING_FEN);
        }
        
        // Update the board display
        updateBoardFromPosition();
        
        // Also load the current strategy if available
        currentStrategy = prefs.getString("currentStrategy", "");
        if (!TextUtils.isEmpty(currentStrategy)) {
            applyCurrentStrategy();
        }
    }

    private void applyCurrentStrategy() {
        switch(currentStrategy) {
            case "attack":
                applyAttackStrategy();
                break;
            case "defense":
                applyDefenseStrategy();
                break;
            case "trap":
                applyTrapStrategy();
                break;
            case "counter":
                applyCounterStrategy();
                break;
        }
    }

    private void applyAttackStrategy() {
        // Implement attack strategy logic
        // For example, highlight attacking moves or show suggestions
        
        tvInstruction.setText("Attack Strategy: Look for opportunities to capture opponent pieces or threaten key squares");
        
        // You could use DroidFish's engine to analyze attacking moves
        // and highlight them on the board
        
        // Example highlighting key squares for attack
        highlightSquaresForStrategy("attack");
    }

    private void applyDefenseStrategy() {
        // Implement defense strategy logic
        
        tvInstruction.setText("Defense Strategy: Focus on protecting your pieces and strengthening your position");
        
        // You could use DroidFish's engine to analyze defensive moves
        
        // Example highlighting key squares for defense
        highlightSquaresForStrategy("defense");
    }

    private void applyTrapStrategy() {
        // Implement trap strategy logic
        
        tvInstruction.setText("Trap Strategy: Set up tactical traps to win material or gain positional advantage");
        
        // You could use DroidFish's engine to identify potential traps
        
        // Example highlighting key squares for traps
        highlightSquaresForStrategy("trap");
    }

    private void applyCounterStrategy() {
        // Implement counter strategy logic
        
        tvInstruction.setText("Counter Strategy: Respond to opponent threats with effective counter-moves");
        
        // You could use DroidFish's engine to identify counter moves
        
        // Example highlighting key squares for counter moves
        highlightSquaresForStrategy("counter");
    }

    private void highlightSquaresForStrategy(String strategy) {
        // This would highlight key squares based on the selected strategy
        // In a complete implementation, you would use DroidFish's engine analysis
        
        // For now, just a placeholder method
        // You could add colored overlays to specific squares on the board
    }

    private int getPieceDrawableId(int piece) {
        // Map DroidFish's piece constants to drawables
        switch (piece) {
            case Piece.WKING:   return R.drawable.wk;
            case Piece.WQUEEN:  return R.drawable.wq;
            case Piece.WROOK:   return R.drawable.wr;
            case Piece.WBISHOP: return R.drawable.wb;
            case Piece.WKNIGHT: return R.drawable.wn;
            case Piece.WPAWN:   return R.drawable.wp;
            case Piece.BKING:   return R.drawable.bk;
            case Piece.BQUEEN:  return R.drawable.bq;
            case Piece.BROOK:   return R.drawable.br;
            case Piece.BBISHOP: return R.drawable.bb;
            case Piece.BKNIGHT: return R.drawable.bn;
            case Piece.BPAWN:   return R.drawable.bp;
            default:            return 0; // Empty square
        }
    }

    private void handleSquareClick(int row, int col) {
        int square = Position.getSquare(col, 7-row);
        int piece = position.getPiece(square);
        
        if (selectedRow == -1 && selectedCol == -1) {
            // No piece selected yet, select this square if it has a piece
            if (piece != Piece.EMPTY) {
                selectedRow = row;
                selectedCol = col;
                highlightSquare(row, col, true);
            }
        } else {
            // A piece is already selected, try to move it
            int fromSquare = Position.getSquare(selectedCol, 7-selectedRow);
            int toSquare = Position.getSquare(col, 7-row);
            
            // Create a move and check if it's legal
            Move move = new Move(fromSquare, toSquare, Piece.EMPTY);
            if (position.legalMove(move)) {
                // Make the move
                position.makeMove(move);
                
                // Update the board display
                updateBoardFromPosition();
                
                // Apply current strategy after the move
                applyCurrentStrategy();
            }
            
            // Unhighlight the previously selected square
            highlightSquare(selectedRow, selectedCol, false);
            
            // Reset selection
            selectedRow = -1;
            selectedCol = -1;
        }
    }

    private void updateBoardFromPosition() {
        // Clear the board
        for (ImageView pieceView : pieceViews) {
            if (pieceView.getParent() != null) {
                ((FrameLayout)pieceView.getParent()).removeView(pieceView);
            }
        }
        pieceViews.clear();
        
        // Add pieces according to the current position
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int square = Position.getSquare(col, 7-row);
                int piece = position.getPiece(square);
                
                if (piece != Piece.EMPTY) {
                    addPiece(row, col, getPieceDrawableId(piece));
                }
            }
        }
    }

    private void highlightSquare(int row, int col, boolean highlight) {
        String tag = row + "," + col;
        for (int i = 0; i < chessBoard.getChildCount(); i++) {
            View view = chessBoard.getChildAt(i);
            if (tag.equals(view.getTag())) {
                if (view instanceof FrameLayout) {
                    View square = ((FrameLayout) view).getChildAt(0);
                    if (highlight) {
                        square.setBackgroundColor(Color.rgb(255, 255, 0)); // Yellow highlight
                    } else {
                        // Restore original color
                        boolean isWhiteSquare = (row + col) % 2 == 0;
                        if (isWhiteSquare) {
                            square.setBackgroundColor(getResources().getColor(android.R.color.white));
                        } else {
                            square.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                        }
                    }
                }
                break;
            }
        }
    }

    // You may need additional methods based on how you integrate with DroidFish
}