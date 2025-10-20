/*import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

class SudokuGame extends JFrame { private final JTextField[][] cells = new JTextField[9][9]; private int[][] puzzle = new int[9][9]; private int[][] solution = new int[9][9];

private int errorCount = 0;
private JLabel errorLabel = new JLabel("Mistakes: 0");
private JLabel timerLabel = new JLabel("Time: 0s");
private int secondsElapsed = 0;
private Timer timer;

private JComboBox<String> difficultyBox = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
private JButton newGameButton = new JButton("New Game");

public SudokuGame() {
    setTitle("Sudoku Game — Random Puzzle");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    JPanel mainPanel = new JPanel(new GridLayout(3, 3, 4, 4));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    Font cellFont = new Font("Times New Roman", Font.BOLD, 20);

    for (int blockRow = 0; blockRow < 3; blockRow++) {
        for (int blockCol = 0; blockCol < 3; blockCol++) {
            JPanel subGrid = new JPanel(new GridLayout(3, 3));
            subGrid.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    int row = blockRow * 3 + r;
                    int col = blockCol * 3 + c;

                    JTextField cell = new JTextField();
                    cell.setHorizontalAlignment(JTextField.CENTER);
                    cell.setFont(cellFont);
                    cell.setDocument(new JTextFieldLimit(1));

                    cells[row][col] = cell;
                    subGrid.add(cell);
                }
            }
            mainPanel.add(subGrid);
        }
    }

    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
    JButton checkButton = new JButton("Check Sudoku");
    checkButton.setFont(new Font("Arial", Font.BOLD, 16));
    checkButton.addActionListener(e -> checkSudoku());

    newGameButton.addActionListener(e -> loadNewPuzzle((String) difficultyBox.getSelectedItem()));

    bottomPanel.add(errorLabel);
    bottomPanel.add(timerLabel);
    bottomPanel.add(difficultyBox);
    bottomPanel.add(newGameButton);
    bottomPanel.add(checkButton);

    add(mainPanel, BorderLayout.CENTER);
    add(bottomPanel, BorderLayout.SOUTH);

    loadNewPuzzle("Easy");
    setSize(600, 650);
    setLocationRelativeTo(null);
    setVisible(true);
}

private void loadNewPuzzle(String difficulty) {
    generateRandomPuzzle(difficulty);

    errorCount = 0;
    secondsElapsed = 0;
    errorLabel.setText("Mistakes: 0");
    timerLabel.setText("Time: 0s");

    if (timer != null) timer.stop();
    timer = new Timer(1000, e -> {
        secondsElapsed++;
        timerLabel.setText("Time: " + secondsElapsed + "s");
    });
    timer.start();

    for (int r = 0; r < 9; r++) {
        for (int c = 0; c < 9; c++) {
            JTextField cell = cells[r][c];
            cell.setText("");
            cell.setEditable(true);
            cell.setBackground(Color.blue);
            for (KeyListener kl : cell.getKeyListeners()) {
                cell.removeKeyListener(kl);
            }

            if (puzzle[r][c] != 0) {
                cell.setText(String.valueOf(puzzle[r][c]));
                cell.setEditable(false);
                cell.setBackground(new Color(220, 220, 220));
            } else {
                final int rr = r, cc = c;
                cell.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyReleased(KeyEvent e) {
                        String text = cell.getText().trim();
                        cell.setBackground(Color.white);
                        if (text.isEmpty()) return;

                        char ch = e.getKeyChar();
                        if (Character.isDigit(ch) && ch != '0') {
                            int entered = Integer.parseInt(text);
                            if (entered == solution[rr][cc]) {
                                cell.setBackground(new Color(144, 238, 144));
                            } else {
                                cell.setBackground(new Color(255, 182, 193));
                                errorCount++;
                                errorLabel.setText("Mistakes: " + errorCount);
                            }
                        }
                    }
                });
            }
        }
    }
}

private void checkSudoku() {
    for (int r = 0; r < 9; r++) {
        for (int c = 0; c < 9; c++) {
            if (!cells[r][c].isEditable()) continue;
            String text = cells[r][c].getText().trim();
            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all cells.", "Incomplete Grid", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int entered = Integer.parseInt(text);
            if (entered != solution[r][c]) {
                JOptionPane.showMessageDialog(this, "❌ Incorrect number at (" + (r + 1) + ", " + (c + 1) + ")", "Invalid Sudoku", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    }
    timer.stop();
    JOptionPane.showMessageDialog(this, "✅ Sudoku solved correctly!", "Success", JOptionPane.INFORMATION_MESSAGE);
}

static class JTextFieldLimit extends javax.swing.text.PlainDocument {
    private final int limit;
    JTextFieldLimit(int limit) { this.limit = limit; }

    @Override
    public void insertString(int offset, String str, javax.swing.text.AttributeSet attr)
            throws javax.swing.text.BadLocationException {
        if (str == null) return;
        if ((getLength() + str.length()) <= limit) {
            super.insertString(offset, str, attr);
        }
    }
}

// Generate a random valid Sudoku puzzle and its solution
private void generateRandomPuzzle(String difficulty) {
    // Start with a solved grid
    solution = generateSolvedGrid();

    // Copy solution to puzzle
    for (int i = 0; i < 9; i++) {
        System.arraycopy(solution[i], 0, puzzle[i], 0, 9);
    }

    // Remove cells based on difficulty
    int cellsToRemove;
    switch (difficulty) {
        case "Medium":
            cellsToRemove = 40;
            break;
        case "Hard":
            cellsToRemove = 50;
            break;
        default: // Easy
            cellsToRemove = 30;
    }

    Random rand = new Random();
    while (cellsToRemove > 0) {
        int r = rand.nextInt(9);
        int c = rand.nextInt(9);
        if (puzzle[r][c] != 0) {
            puzzle[r][c] = 0;
            cellsToRemove--;
        }
    }
}

// Generate a fully solved Sudoku grid using backtracking
private int[][] generateSolvedGrid() {
    int[][] grid = new int[9][9];
    fillGrid(grid, 0, 0);
    return grid;
}

private boolean fillGrid(int[][] grid, int row, int col) {
    if (row == 9) return true;
    if (col == 9) return fillGrid(grid, row + 1, 0);

    Random rand = new Random();
    int[] numbers = new int[9];
    for (int i = 0; i < 9; i++) numbers[i] = i + 1;
    // Shuffle numbers
    for (int i = 8; i > 0; i--) {
        int j = rand.nextInt(i + 1);
        int temp = numbers[i];
        numbers[i] = numbers[j];
        numbers[j] = temp;
    }

    for (int num : numbers) {
        if (isSafe(grid, row, col, num)) {
            grid[row][col] = num;
            if (fillGrid(grid, row, col + 1)) return true;
            grid[row][col] = 0;
        }
    }
    return false;
}

private boolean isSafe(int[][] grid, int row, int col, int num) {
    for (int i = 0; i < 9; i++) {
        if (grid[row][i] == num) return false;
        if (grid[i][col] == num) return false;
    }

    int boxRowStart = (row / 3) * 3;
    int boxColStart = (col / 3) * 3;
    for (int r = boxRowStart; r < boxRowStart + 3; r++) {
        for (int c = boxColStart; c < boxColStart + 3; c++) {
            if (grid[r][c] == num) return false;
        }
    }

    return true;
}

public static void main(String[] args) {
    SwingUtilities.invokeLater(SudokuGame::new);
}

}
*/
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

class SudokuGame extends JFrame {
    // GUI and Game State Components
    private final JTextField[][] cells = new JTextField[9][9];
    private int[][] puzzle = new int[9][9];
    private int[][] solution = new int[9][9];

    private int errorCount = 0;
    private JLabel errorLabel = new JLabel("Mistakes: 0");
    private JLabel timerLabel = new JLabel("Time: 0s");
    private int secondsElapsed = 0;
    private Timer timer;

    private JComboBox<String> difficultyBox = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
    private JButton newGameButton = new JButton("New Game");
    private JButton solveButton = new JButton("Solve");

    /**
     * Constructor for the SudokuGame GUI.
     */
    public SudokuGame() {
        setTitle("Sudoku Game — Random Puzzle");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- Main Sudoku Grid Panel ---
        JPanel mainPanel = new JPanel(new GridLayout(3, 3, 4, 4));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Font cellFont = new Font("Times New Roman", Font.BOLD, 20);

        for (int blockRow = 0; blockRow < 3; blockRow++) {
            for (int blockCol = 0; blockCol < 3; blockCol++) {
                JPanel subGrid = new JPanel(new GridLayout(3, 3));
                // 3x3 blocks get a thicker black border
                subGrid.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

                for (int r = 0; r < 3; r++) {
                    for (int c = 0; c < 3; c++) {
                        int row = blockRow * 3 + r;
                        int col = blockCol * 3 + c;

                        JTextField cell = new JTextField();
                        cell.setHorizontalAlignment(JTextField.CENTER);
                        cell.setFont(cellFont);
                        // Limit input to a single character
                        cell.setDocument(new JTextFieldLimit(1));

                        cells[row][col] = cell;
                        subGrid.add(cell);
                    }
                }
                mainPanel.add(subGrid);
            }
        }

        // --------------------------------------------------------------------------------------------------
        // Bottom Control Panel: MODIFIED LAYOUT
        // Using a combination of JPanel and GridLayout/FlowLayout to ensure all controls fit.
        // --------------------------------------------------------------------------------------------------
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1)); // Top for status, bottom for buttons
        
        // Panel for Status Labels (Timer and Mistakes)
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 5));
        statusPanel.add(errorLabel);
        statusPanel.add(timerLabel);

        // Panel for Controls (Difficulty and Buttons)
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton checkButton = new JButton("Check Sudoku");
        checkButton.setFont(new Font("Arial", Font.BOLD, 14));
        newGameButton.setFont(new Font("Arial", Font.BOLD, 14));
        solveButton.setFont(new Font("Arial", Font.BOLD, 14));

        // Action Listeners
        checkButton.addActionListener(e -> checkSudoku());
        newGameButton.addActionListener(e -> loadNewPuzzle((String) difficultyBox.getSelectedItem()));
        solveButton.addActionListener(e -> solvePuzzle());

        controlPanel.add(new JLabel("Difficulty:"));
        controlPanel.add(difficultyBox);
        controlPanel.add(newGameButton);
        controlPanel.add(solveButton); 
        controlPanel.add(checkButton);

        bottomPanel.add(statusPanel);
        bottomPanel.add(controlPanel);

        // Add panels to JFrame
        add(mainPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Start the first game
        loadNewPuzzle("Easy");
        // Increased the width slightly for better fit, but maintained the requested ratio.
        setSize(650, 700); 
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Resets the game state and loads a new puzzle based on the selected difficulty.
     * @param difficulty The difficulty level ("Easy", "Medium", "Hard").
     */
    private void loadNewPuzzle(String difficulty) {
        generateRandomPuzzle(difficulty);

        errorCount = 0;
        secondsElapsed = 0;
        errorLabel.setText("Mistakes: 0");
        timerLabel.setText("Time: 0s");

        // Start/Restart Timer
        if (timer != null) timer.stop();
        timer = new Timer(1000, e -> {
            secondsElapsed++;
            timerLabel.setText("Time: " + secondsElapsed + "s");
        });
        timer.start();

        // Enable solve button for a new game
        solveButton.setEnabled(true);

        // Populate the grid and set up listeners
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                JTextField cell = cells[r][c];
                cell.setText("");
                cell.setEditable(true);
                cell.setBackground(Color.white); // Reset background
                cell.setForeground(Color.BLUE);

                // Remove previous listeners to prevent duplicates
                for (KeyListener kl : cell.getKeyListeners()) {
                    cell.removeKeyListener(kl);
                }

                if (puzzle[r][c] != 0) {
                    // Pre-filled cells
                    cell.setText(String.valueOf(puzzle[r][c]));
                    cell.setEditable(false);
                    cell.setForeground(Color.BLACK);
                    cell.setBackground(new Color(220, 220, 220)); // Gray background for fixed cells
                } else {
                    // Editable cells
                    final int rr = r, cc = c;
                    cell.addKeyListener(new KeyAdapter() {
                        @Override
                        public void keyReleased(KeyEvent e) {
                            String text = cell.getText().trim();
                            
                            // Input validation to ensure only 1-9 is entered
                            if (text.length() > 0) {
                                char ch = text.charAt(0);
                                if (!Character.isDigit(ch) || ch == '0') {
                                    cell.setText("");
                                    cell.setBackground(Color.white);
                                    return;
                                }
                            }
                            
                            if (text.isEmpty()) {
                                cell.setBackground(Color.white);
                                return;
                            }

                            try {
                                int entered = Integer.parseInt(text);
                                Color currentBg = cell.getBackground();
                                
                                if (entered == solution[rr][cc]) {
                                    // Correct entry: Light Green
                                    cell.setBackground(new Color(144, 238, 144)); 
                                } else {
                                    // Incorrect entry: Light Red
                                    // Only increment error count if changing from a non-red state
                                    if (!currentBg.equals(new Color(255, 182, 193)) && !currentBg.equals(Color.white)) {
                                        errorCount++;
                                        errorLabel.setText("Mistakes: " + errorCount);
                                    } else if (currentBg.equals(Color.white)) {
                                        // This handles the first incorrect guess in a cell
                                        errorCount++;
                                        errorLabel.setText("Mistakes: " + errorCount);
                                    }
                                    cell.setBackground(new Color(255, 182, 193)); 
                                }
                            } catch (NumberFormatException ignored) {
                                // Handled by KeyListener logic
                            }
                        }
                    });
                }
            }
        }
    }

    /**
     * Checks if the user's current input in all editable cells matches the solution.
     */
    private void checkSudoku() {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (!cells[r][c].isEditable()) continue;
                String text = cells[r][c].getText().trim();
                
                if (text.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all empty cells.", "Incomplete Grid", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                try {
                    int entered = Integer.parseInt(text);
                    if (entered != solution[r][c]) {
                        JOptionPane.showMessageDialog(this, "❌ The grid contains one or more incorrect numbers. Keep trying!", "Invalid Sudoku", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid entry found. Please use numbers 1-9.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }
        
        // Solved correctly
        timer.stop();
        solveButton.setEnabled(false);
        JOptionPane.showMessageDialog(this, 
            String.format("✅ Sudoku solved correctly!\nTime: %d seconds\nMistakes: %d", secondsElapsed, errorCount), 
            "Success!", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Solves the current puzzle by filling in the correct numbers and locks the grid.
     */
    private void solvePuzzle() {
        if (timer != null) {
            timer.stop();
        }
        solveButton.setEnabled(false);

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                JTextField cell = cells[r][c];
                
                // Only fill and lock editable cells
                if (cell.isEditable()) {
                    cell.setText(String.valueOf(solution[r][c]));
                    cell.setEditable(false); 
                    cell.setBackground(new Color(144, 238, 144)); // Light Green (Solved)
                    // Remove key listener since it's now solved and locked
                    for (KeyListener kl : cell.getKeyListeners()) {
                        cell.removeKeyListener(kl);
                    }
                }
            }
        }
        JOptionPane.showMessageDialog(this, "The puzzle has been solved!", "Solution Revealed", JOptionPane.INFORMATION_MESSAGE);
    }

    // --------------------------------------------------------------------------------------------------
    // Sudoku Generation Logic (No change)
    // --------------------------------------------------------------------------------------------------

    private void generateRandomPuzzle(String difficulty) {
        solution = generateSolvedGrid();

        for (int i = 0; i < 9; i++) {
            System.arraycopy(solution[i], 0, puzzle[i], 0, 9);
        }

        int cellsToRemove;
        switch (difficulty) {
            case "Medium":
                cellsToRemove = 40;
                break;
            case "Hard":
                cellsToRemove = 50;
                break;
            default: // Easy
                cellsToRemove = 30;
        }

        Random rand = new Random();
        while (cellsToRemove > 0) {
            int r = rand.nextInt(9);
            int c = rand.nextInt(9);
            
            if (puzzle[r][c] != 0) {
                puzzle[r][c] = 0;
                cellsToRemove--;
            }
        }
    }

    private int[][] generateSolvedGrid() {
        int[][] grid = new int[9][9];
        fillGrid(grid, 0, 0);
        return grid;
    }

    private boolean fillGrid(int[][] grid, int row, int col) {
        if (row == 9) return true;
        if (col == 9) return fillGrid(grid, row + 1, 0);

        Random rand = new Random();
        int[] numbers = new int[9];
        for (int i = 0; i < 9; i++) numbers[i] = i + 1;
        
        for (int i = 8; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int temp = numbers[i];
            numbers[i] = numbers[j];
            numbers[j] = temp;
        }

        for (int num : numbers) {
            if (isSafe(grid, row, col, num)) {
                grid[row][col] = num;
                if (fillGrid(grid, row, col + 1)) return true;
                grid[row][col] = 0;
            }
        }
        return false;
    }

    private boolean isSafe(int[][] grid, int row, int col, int num) {
        for (int i = 0; i < 9; i++) {
            if (grid[row][i] == num) return false;
            if (grid[i][col] == num) return false;
        }

        int boxRowStart = (row / 3) * 3;
        int boxColStart = (col / 3) * 3;
        for (int r = boxRowStart; r < boxRowStart + 3; r++) {
            for (int c = boxColStart; c < boxColStart + 3; c++) {
                if (grid[r][c] == num) return false;
            }
        }

        return true;
    }
    
    // --------------------------------------------------------------------------------------------------
    // Helper Class: JTextFieldLimit (No change)
    // --------------------------------------------------------------------------------------------------

    static class JTextFieldLimit extends javax.swing.text.PlainDocument {
        private final int limit;
        
        JTextFieldLimit(int limit) { 
            this.limit = limit; 
        }

        @Override
        public void insertString(int offset, String str, javax.swing.text.AttributeSet attr)
                throws javax.swing.text.BadLocationException {
            if (str == null) return;
            
            if (str.length() == 1 && Character.isDigit(str.charAt(0)) && str.charAt(0) != '0') {
                 if ((getLength() + str.length()) <= limit) {
                    super.insertString(offset, str, attr);
                }
            }
        }
    }
    
    // --------------------------------------------------------------------------------------------------
    // Main Method (No change)
    // --------------------------------------------------------------------------------------------------

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SudokuGame::new);
    }
}