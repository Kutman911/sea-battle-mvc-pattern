import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Viewer {
    private final Canvas canvas;
    private final JFrame frame;
    private final AudioPlayer audioPlayer;
    private final Controller controller;
    private final JButton startButton;

    public Viewer() {
        audioPlayer = new AudioPlayer();
        audioPlayer.playBackgroundMusic("src/sounds/background_music.wav");

        controller = new Controller(this);

        canvas = new Canvas(controller.getModel());
        canvas.addMouseListener(controller);
        canvas.addMouseMotionListener(controller);
        canvas.getModel().setCanvas(canvas);

        frame = new JFrame("Sea Battle MVC Pattern");
        frame.setIconImage(new ImageIcon(Viewer.class.getResource("/images/appIcon.jpg")).getImage());
        frame.setSize(1500, 900);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Top control panel with centered, styled START button
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(new Color(15, 35, 60));
        topPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        startButton = new JButton("START");
        startButton.setToolTipText("Start battle");
        startButton.setFocusPainted(false);
        startButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        startButton.setForeground(Color.WHITE);
        Color btnGreen = new Color(28, 150, 90);
        Color borderYellow = new Color(237, 176, 36);
        startButton.setBackground(btnGreen);
        startButton.setOpaque(true);
        startButton.setPreferredSize(new Dimension(160, 46));
        startButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderYellow, 3),
                BorderFactory.createEmptyBorder(6, 18, 6, 18)
        ));
        startButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (startButton.isEnabled()) startButton.setBackground(btnGreen.darker());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (startButton.isEnabled()) startButton.setBackground(btnGreen);
            }
        });

        startButton.addActionListener(e -> {
            if (audioPlayer != null) {
                audioPlayer.playSound("src/sounds/buttonClick.wav");
            }
            canvas.attemptStart(this);
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        topPanel.add(startButton, gbc);

        frame.add("North", topPanel);
        frame.add("Center", canvas);
        frame.setLocationRelativeTo(null);

        // Keyboard shortcuts to start (Ctrl+Enter, or S)
        InputMap im = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = frame.getRootPane().getActionMap();
        im.put(KeyStroke.getKeyStroke("control ENTER"), "startBattle");
        im.put(KeyStroke.getKeyStroke('S'), "startBattle");
        am.put("startBattle", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (startButton.isEnabled()) {
                    canvas.attemptStart(Viewer.this);
                }
            }
        });
    }

    public void update() {
        canvas.repaint();
    }

    public Canvas getCanvas() {
        return canvas;
    }

    // Transient UI hint for better UX
    public void showHint(String message, int durationMs) {
        if (canvas != null) {
            canvas.showHint(message, durationMs);
        }
    }

    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }

    public void showMainMenuFromCanvas() {
        canvas.showMainMenu(this);
    }

    public JFrame getFrame() {
        return frame;
    }

    public JButton getStartButton() {
        return startButton;
    }
}
