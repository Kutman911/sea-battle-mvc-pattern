import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Viewer {
    private Canvas canvas;
    private JFrame frame;
    private Model model;
    private AudioPlayer audioPlayer;
    private Controller controller;
    private JButton startButton;

    public Viewer() {
        audioPlayer = new AudioPlayer();
        audioPlayer.playBackgroundMusic("src/sounds/background_music.wav");

        controller = new Controller(this);
        model = controller.getModel();

        canvas = new Canvas(model);
        canvas.addMouseListener(controller);
        canvas.addMouseMotionListener(controller);
        model.setCanvas(canvas);

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
        startButton.addActionListener(e -> attemptStart());

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
                    attemptStart();
                }
            }
        });
    }

    public void update() {
        canvas.repaint();
    }

    public void setVisibleFrame() {
        frame.setVisible(true);
        SwingUtilities.invokeLater(() -> model.getLevelWindow().showLevelStartWindow());
    }

    public void showResult(boolean isWin) {
        if (audioPlayer != null) {
            audioPlayer.stop();
        }
        if (!isWin) {
            audioPlayer.playSound("src/sounds/loseSound.wav");
        }

        SwingUtilities.invokeLater(() -> {
            ResultDialog dialog = new ResultDialog(
                    frame,
                    isWin,
                    model.getLevelWindow().getCurrentLevel(),
                    () -> {
                        if (isWin) {
                            if (model.getLevelWindow().getCurrentLevel() >= 3) {
                                model.getLevelWindow().resetToLevelOne();
                            }
                        } else {
                            model.getLevelWindow().resetToLevelOne();
                        }
                        model.resetGame();
                        if (startButton != null) {
                            startButton.setEnabled(true);
                        }
                        SwingUtilities.invokeLater(() -> model.getLevelWindow().showLevelStartWindow());
                        if (audioPlayer != null) {
                            audioPlayer.playBackgroundMusic("src/sounds/background_music.wav");
                        }
                    },
                    () -> {
                        model.getLevelWindow().nextLevel();
                        if (startButton != null) {
                            startButton.setEnabled(true);
                        }
                        if (audioPlayer != null) {
                            audioPlayer.playBackgroundMusic("src/sounds/background_music.wav");
                        }
                    },
                    () -> System.exit(0)
            );
            dialog.setVisible(true);
        });
    }
    private void attemptStart() {
        if (!model.areAllShipsPlaced()) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Please place all ships first",
                    "Reminder",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        if (!model.isBattleStarted()) {
            model.startBattlePhase();
            startButton.setEnabled(false);
        }
        update();
    }
   public void scheduleComputerTurn() {
        int delay = 400;

        Timer timer = new Timer(delay, e -> {
            model.computerTurn();
            update();
            ((Timer) e.getSource()).stop();
        });

        timer.setRepeats(false);
        timer.start();
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }
}
