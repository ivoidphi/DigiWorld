import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import javax.sound.sampled.*;

public class ScreenTitle extends JFrame {

    // ─────────────────────────────────────────────
    //  PASTE YOUR GIF FILE NAME HERE
    //  Make sure the .gif is in the same folder
    //  as this .java file
    // ─────────────────────────────────────────────
    private static final String GIF_NAME = "DIGIWORLD.gif";
    // ─────────────────────────────────────────────

    public ScreenTitle() {
        setTitle("Game Title Screen");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setResizable(false);

        // ── Load the GIF ──────────────────────────
        ImageIcon gifIcon = new ImageIcon(GIF_NAME);
        JLabel gifBackground = new JLabel(gifIcon) {
            @Override
            protected void paintComponent(Graphics g) {
                // Stretch GIF to fill the entire screen
                g.drawImage(gifIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        gifBackground.setLayout(new GridBagLayout());

        // ── Buttons ───────────────────────────────
        JButton playButton    = createMenuButton("PLAY");
        JButton creditsButton = createMenuButton("CREDITS");
        JButton exitButton    = createMenuButton("EXIT");

        // ── Button actions (empty — fill in later) ──
        playButton.addActionListener(e -> {
            // TODO: start game
        });

        creditsButton.addActionListener(e -> {
            // TODO: show credits screen
        });

        exitButton.addActionListener(e -> {
            // TODO: exit application
        });

        // ── Stack buttons vertically in center ────
        JPanel menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx  = 0;
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        gbc.gridy = 0; menuPanel.add(playButton,    gbc);
        gbc.gridy = 1; menuPanel.add(creditsButton, gbc);
        gbc.gridy = 2; menuPanel.add(exitButton,    gbc);

        gifBackground.add(menuPanel);
        setContentPane(gifBackground);
        pack();

        // ── Start background music ──────────────────
        playBackgroundMusic("bgmusic.wav");
    }

    // ── Styled button factory ─────────────────────
    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;

            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    @Override public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background
                g2.setColor(hovered ? new Color(255, 221, 87, 60) : new Color(0, 0, 0, 160));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                // Border
                g2.setColor(hovered ? new Color(255, 221, 87) : Color.WHITE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 8, 8);

                // Label
                g2.setColor(hovered ? new Color(255, 221, 87) : Color.WHITE);
                g2.setFont(new Font("Arial Black", Font.PLAIN, 18));
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth()  - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };

        btn.setPreferredSize(new Dimension(220, 55));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Background Music ──────────────────────────
    private void playBackgroundMusic(String audioFileName) {
        try {
            URL audioURL = getClass().getResource("/" + audioFileName);
            if (audioURL == null) {
                System.err.println("Audio file not found: " + audioFileName);
                return;
            }
            
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioURL);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
            
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    clip.stop();
                    clip.close();
                }
            });
        } catch (Exception e) {
            System.err.println("Error playing audio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ── Entry point ───────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ScreenTitle().setVisible(true));
    }
}