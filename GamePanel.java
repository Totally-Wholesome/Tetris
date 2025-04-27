import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class GamePanel extends JPanel implements KeyListener, ActionListener {
    private final int squareSize = 30;
    private final int gridColumns = 10;
    private final int gridRows = 20;
    private final Color[][] grid = new Color[gridRows][gridColumns]; // Now stores Color objects
    
    private Timer timer;
    private int currentX, currentY;
    private int[][] currentShape;
    private Color currentColor;
    private boolean gameOver = false;
    
    private final int[][][] shapes = {
        {{1, 1, 1, 1}}, // I
        {{1, 1}, {1, 1}}, // O
        {{1, 1, 1}, {0, 1, 0}}, // T
        {{1, 1, 1}, {1, 0, 0}}, // L
        {{1, 1, 1}, {0, 0, 1}}, // J
        {{0, 1, 1}, {1, 1, 0}}, // S
        {{1, 1, 0}, {0, 1, 1}}  // Z
    };
    
    private final Color[] colors = {
        Color.CYAN, Color.YELLOW, Color.MAGENTA, 
        Color.ORANGE, Color.BLUE, Color.GREEN, Color.RED
    };
    
    // Tracks which shape index is current
    private int currentShapeIndex;
    
    public GamePanel() {
        this.setPreferredSize(new Dimension(gridColumns * squareSize, gridRows * squareSize));
        this.setBackground(Color.BLACK);
        this.addKeyListener(this);
        this.setFocusable(true);
        
        timer = new Timer(500, this);
        spawnNewShape();
        timer.start();
    }
    
    private void spawnNewShape() {
        Random random = new Random();
        currentShapeIndex = random.nextInt(shapes.length);
        currentShape = shapes[currentShapeIndex];
        currentColor = colors[currentShapeIndex];
        
        currentX = gridColumns / 2 - currentShape[0].length / 2;
        currentY = 0;
        
        if (checkCollision()) {
            gameOver = true;
            timer.stop();
        }
    }
    
    private boolean checkCollision() {
        for (int row = 0; row < currentShape.length; row++) {
            for (int col = 0; col < currentShape[row].length; col++) {
                if (currentShape[row][col] != 0) {
                    int newX = currentX + col;
                    int newY = currentY + row;
                    
                    if (newX < 0 || newX >= gridColumns || 
                        newY >= gridRows || 
                        (newY >= 0 && grid[newY][newX] != null)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private void mergeShapeToGrid() {
        for (int row = 0; row < currentShape.length; row++) {
            for (int col = 0; col < currentShape[row].length; col++) {
                if (currentShape[row][col] != 0) {
                    grid[currentY + row][currentX + col] = currentColor;
                }
            }
        }
    }
    
    private void clearLines() {
        for (int row = gridRows - 1; row >= 0; row--) {
            boolean lineComplete = true;
            for (int col = 0; col < gridColumns; col++) {
                if (grid[row][col] == null) {
                    lineComplete = false;
                    break;
                }
            }
            
            if (lineComplete) {
                for (int r = row; r > 0; r--) {
                    System.arraycopy(grid[r - 1], 0, grid[r], 0, gridColumns);
                }
                // Clear top line
                for (int col = 0; col < gridColumns; col++) {
                    grid[0][col] = null;
                }
                row++; // Check the same row again
            }
        }
    }
    
    private void rotateShape() {
        int[][] rotated = new int[currentShape[0].length][currentShape.length];
        
        for (int row = 0; row < currentShape.length; row++) {
            for (int col = 0; col < currentShape[row].length; col++) {
                rotated[col][currentShape.length - 1 - row] = currentShape[row][col];
            }
        }
        
        int[][] oldShape = currentShape;
        currentShape = rotated;
        
        if (checkCollision()) {
            currentShape = oldShape;
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw the grid pattern
        g.setColor(new Color(50, 50, 50));
        for (int row = 0; row <= gridRows; row++) {
            g.drawLine(0, row * squareSize, 
                      gridColumns * squareSize, row * squareSize);
        }
        for (int col = 0; col <= gridColumns; col++) {
            g.drawLine(col * squareSize, 0, 
                      col * squareSize, gridRows * squareSize);
        }
        
        // Draw filled cells (existing blocks)
        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridColumns; col++) {
                if (grid[row][col] != null) {
                    g.setColor(grid[row][col]);
                    g.fillRect(col * squareSize, row * squareSize, 
                              squareSize, squareSize);
                    g.setColor(Color.WHITE);
                    g.drawRect(col * squareSize, row * squareSize, 
                              squareSize, squareSize);
                }
            }
        }
        
        // Draw current shape
        if (currentShape != null) {
            for (int row = 0; row < currentShape.length; row++) {
                for (int col = 0; col < currentShape[row].length; col++) {
                    if (currentShape[row][col] != 0) {
                        g.setColor(currentColor);
                        g.fillRect((currentX + col) * squareSize, 
                                  (currentY + row) * squareSize, 
                                  squareSize, squareSize);
                        g.setColor(Color.WHITE);
                        g.drawRect((currentX + col) * squareSize, 
                                    (currentY + row) * squareSize, 
                                    squareSize, squareSize);
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
    
    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameOver) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    currentX--;
                    if (checkCollision()) currentX++;
                    break;
                case KeyEvent.VK_RIGHT:
                    currentX++;
                    if (checkCollision()) currentX--;
                    break;
                case KeyEvent.VK_DOWN:
                    currentY+=0.1;
                    if (checkCollision()) currentY--;
                    break;
                case KeyEvent.VK_UP:
                    rotateShape();
                    break;
                case KeyEvent.VK_SPACE:
                    // Hard drop
                    while (!checkCollision()) {
                        currentY++;
                    }
                    currentY--;
                    mergeShapeToGrid();
                    clearLines();
                    spawnNewShape();
                    break;
            }
            repaint();
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {}
    
    @Override
    public void keyTyped(KeyEvent e) {}
}