import javax.swing.*;

public class GameFrame extends JFrame {
	private GamePanel gamePanel = new GamePanel();
    
    public GameFrame() {
    	this.setTitle("Java Tetris");
        this.setSize(315, 635);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.add(gamePanel);
        this.setVisible(true);
    }
}