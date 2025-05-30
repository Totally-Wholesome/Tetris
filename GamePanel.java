import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class GamePanel extends JPanel implements KeyListener, ActionListener {
    private final int SQUARE_SIZE = 30;
    private final int GRID_COLUMNS = 10;
    private final int GRID_ROWS = 20;
    
    private Color[][] grid = new Color[GRID_ROWS][GRID_COLUMNS];
    private Timer timer = new Timer(500, this);
    private Random random = new Random();
    private int currentX;
    private int currentY;
    private int[][] currentShape;
    private int currentShapeIndex;
    private Color currentColor;
    private boolean gameOver = false;
    
    private int[][][] shapes = {
        {{1, 1, 1, 1}}, // I
        {{1, 1}, {1, 1}}, // O
        {{1, 1, 1}, {0, 1, 0}}, // T
        {{1, 1, 1}, {1, 0, 0}}, // L
        {{1, 1, 1}, {0, 0, 1}}, // J
        {{0, 1, 1}, {1, 1, 0}}, // S
        {{1, 1, 0}, {0, 1, 1}}  // Z
    };
    
    private Color[] colors = {Color.CYAN, Color.YELLOW, Color.MAGENTA, Color.ORANGE, Color.BLUE, Color.GREEN, Color.RED};
    
    public GamePanel() {
        this.setPreferredSize(new Dimension(GRID_COLUMNS * SQUARE_SIZE, GRID_ROWS * SQUARE_SIZE));
        this.setBackground(Color.BLACK);
        this.addKeyListener(this);
        this.setFocusable(true);
        
        spawnNewShape();
        timer.start();
    }
    
    // Creates a new shape at the top of the board, selected shape is random
    private void spawnNewShape() {
    	currentShapeIndex = (int) (Math.random() * shapes.length);
        currentShape = shapes[currentShapeIndex];
        currentColor = colors[currentShapeIndex];
        currentX = GRID_COLUMNS / 2 - currentShape[0].length / 2;
        currentY = 0;       
        if (checkCollision()) {
            gameOver = true;
            timer.stop();
        }
    }
    
    // Check if the shape is colliding with borders or other shapes
    private boolean checkCollision() {
        for (int row = 0; row < currentShape.length; row++) {
            for (int col = 0; col < currentShape[row].length; col++) {
                if (currentShape[row][col] != 0) {
                    int newX = currentX + col;
                    int newY = currentY + row;
                    
                    // Checks if the shape is colliding with any borders or squares that are already taken
                    if (newX < 0 || newX >= GRID_COLUMNS || 
                        newY >= GRID_ROWS || 
                        (newY >= 0 && grid[newY][newX] != null)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    // Merges the shape when it touches the bottom or another shape
    private void mergeShapeToGrid() {
        for (int row = 0; row < currentShape.length; row++) {
            for (int col = 0; col < currentShape[row].length; col++) {
                if (currentShape[row][col] != 0) {
                    grid[currentY + row][currentX + col] = currentColor;
                }
            }
        }
    }
    
    // Checks each row on the grid and clears it if no squares are null
    private void clearLines() {
        for (int row = GRID_ROWS - 1; row >= 0; row--) {
            boolean lineComplete = true;
            for (int col = 0; col < GRID_COLUMNS; col++) {
                if (grid[row][col] == null) {
                    lineComplete = false;
                    break;
                }
            }
            
            if (lineComplete) {
                for (int r = row; r > 0; r--) {
                    System.arraycopy(grid[r - 1], 0, grid[r], 0, GRID_COLUMNS);
                }
                row++;
            }
        }
    }
    
    // Rotates the shape 90 degrees clockwise
    private void rotateShape() {
        int[][] rotated = new int[currentShape[0].length][currentShape.length];
        
        for (int row = 0; row < currentShape.length; row++) {
            for (int col = 0; col < currentShape[row].length; col++) {
                rotated[col][currentShape.length - 1 - row] = currentShape[row][col];
            }
        }
        int[][] oldShape = currentShape;
        currentShape = rotated;
        
        // Makes sure the rotation doesn't cause collisions
        if (checkCollision()) {
            currentShape = oldShape;
        }
    }
    
    // Called each time the game is repainted
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw the grid pattern
        g.setColor(new Color(50, 50, 50));
        for (int row = 0; row <= GRID_ROWS; row++) {
            g.drawLine(0, row * SQUARE_SIZE, GRID_COLUMNS * SQUARE_SIZE, row * SQUARE_SIZE);
        }
        for (int col = 0; col <= GRID_COLUMNS; col++) {
            g.drawLine(col * SQUARE_SIZE, 0, col * SQUARE_SIZE, GRID_ROWS * SQUARE_SIZE);
        }
        
        // Draw filled cells (existing blocks)
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLUMNS; col++) {
                if (grid[row][col] != null) {
                    g.setColor(grid[row][col]);
                    g.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
                    g.setColor(Color.WHITE);
                    g.drawRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
                }
            }
        }
        
        // Draw current shape
        if (currentShape != null) {
            for (int row = 0; row < currentShape.length; row++) {
                for (int col = 0; col < currentShape[row].length; col++) {
                    if (currentShape[row][col] != 0) {
                        g.setColor(currentColor);
                        g.fillRect((currentX + col) * SQUARE_SIZE, (currentY + row) * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
                        g.setColor(Color.WHITE);
                        g.drawRect((currentX + col) * SQUARE_SIZE, (currentY + row) * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
                    }
                }
            }
        }
        
        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 45));
            g.drawString("GAME OVER", 10, 300);
        }
    }
    
    // restarts the game
    public void restartGame() { 	
    	for(int row = 0; row < GRID_ROWS; row++) {
    		for(int col = 0; col < GRID_COLUMNS; col++) {
    			grid[row][col] = null;
    		}
    	}  	
    	gameOver = false;
    	timer.start();
    	spawnNewShape();
    	repaint();
    }
    
    // Called each time the timer ticks
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            currentY++;
            if (checkCollision()) {
                currentY--;
                mergeShapeToGrid();
                clearLines();
                spawnNewShape();
            }
            repaint();
        }
    }
    
    // Different cased for each key
    @Override
    public void keyPressed(KeyEvent e) {
    	int keyCode = e.getKeyCode();

    	switch (keyCode) {
	        case KeyEvent.VK_LEFT:
	            currentX--;
	            if (checkCollision()) currentX++;
	            break;
	        case KeyEvent.VK_RIGHT:
	            currentX++;
	            if (checkCollision()) currentX--;
	            break;
	        case KeyEvent.VK_DOWN:
	            currentY++;
	            if (checkCollision()) currentY--;
	            break;
	        case KeyEvent.VK_UP:
	            rotateShape();
	            break;
	        case KeyEvent.VK_SPACE:
	            while (!checkCollision()) {
	                currentY++;
	            }
	            currentY--;
	            mergeShapeToGrid();
	            clearLines();
	            spawnNewShape();
	            break;
	        case KeyEvent.VK_R:
	        	if(gameOver) {
	    			restartGame();
	    		}
	        	break;
	    }
    	repaint();
    }
    
    @Override
    public void keyReleased(KeyEvent e) {}
    
    @Override
    public void keyTyped(KeyEvent e) {}
}
