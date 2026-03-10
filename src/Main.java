import javax.swing.*;

void main() {
    JFrame window = new JFrame();
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setResizable(false);
    window.setTitle("DigiWorld");

    GamePanel gamePanel = new GamePanel();
    window.add(gamePanel);

    window.pack(); // fits window to GamePanel size
    window.setLocationRelativeTo(null); // center on screen
    window.setVisible(true);

    gamePanel.startGameLoop();
}