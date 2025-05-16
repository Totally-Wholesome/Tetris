import javax.swing.*;

public class GameFrame extends JFrame {
    public GameFrame() {
    	this.add(new GamePanel());
    	this.setTitle("Java Tetris");
        this.setSize(315, 635);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
