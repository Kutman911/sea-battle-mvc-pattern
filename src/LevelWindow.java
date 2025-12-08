import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class LevelWindow {
    private int currentLevel = 1;
    //  0 = hidden, 1 = start, 2 = completed, 3 = final
    private int windowState = 0;
    private final Viewer viewer;
    private final Canvas canvas;
    private final Model model;
    private float alpha = 0f;

    private final AudioPlayer audioPlayer;

    public LevelWindow(Viewer viewer, Canvas canvas, Model model) {
        this.viewer = viewer;
        this.canvas = canvas;
        this.model = model;
        this.audioPlayer = viewer.getAudioPlayer();
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getWindowState() {
        return windowState;
    }

    public float getAlpha() {
        return alpha;
    }

    public void showLevelStartWindow() {
        windowState = 1;

        audioPlayer.playLevelSound("src/sounds/levelSound.wav");

        fadeIn();

        new Timer().schedule(new TimerTask() {
            public void run() {
                fadeOut();

                new Timer().schedule(new TimerTask() {
                    public void run() {
                        windowState = 0;
                        SwingUtilities.invokeLater(viewer::update);
                    }
                }, 400);

            }
        }, 2400);
    }

    public void showLevelCompletedWindow() {
        windowState = 2;
        fadeIn();
        viewer.update();

        new Timer().schedule(new TimerTask() {
            public void run() {
                fadeOut();

                new Timer().schedule(new TimerTask() {
                    public void run() {
                        SwingUtilities.invokeLater(() -> showResultDialog(true));
                    }
                }, 400);

            }
        }, 2400);
    }

    private void showFinalWindow() {
        windowState = 3;
        fadeIn();
        viewer.update();
    }

    public void nextLevel() {
        currentLevel = currentLevel + 1;

        if (currentLevel > 3) {
            showFinalWindow();

            new Timer().schedule(new TimerTask() {
                public void run() {
                    model.resetGame();
                }
            }, 3000);
            return;
        }

        model.resetGame();

        showLevelStartWindow();
    }

    private void fadeIn() {
        alpha = 0f;
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                alpha += 0.03f;
                if (alpha >= 1f) {
                    alpha = 1f;
                    timer.cancel();
                }
                canvas.repaint();
            }
        }, 0, 15);
    }

    private void fadeOut() {
        alpha = 1f;
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                alpha -= 0.03f;
                if (alpha <= 0f) {
                    alpha = 0f;
                    timer.cancel();
                }
                canvas.repaint();
            }
        }, 0, 15);
    }

    public void resetToLevelOne() {
        currentLevel = 1;
        windowState = 0;
        alpha = 0f;
    }

    public void showResultDialog(boolean playerWon) {
        // Выполняем в EDT
        SwingUtilities.invokeLater(() -> {
            Window parent = SwingUtilities.getWindowAncestor(canvas);
            String title = playerWon ? "Уровень пройден" : "Результат";
            String message = playerWon ? "Поздравляем! Вы прошли уровень." : "Компьютер выиграл. Что делать дальше?";
            String[] options = {"Играть снова", "Следующий уровень", "Выход"};

            int choice = JOptionPane.showOptionDialog(
                    parent,
                    message,
                    title,
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice == 0) {
                model.resetGame();
                showLevelStartWindow();
            } else if (choice == 1) {
                nextLevel();
            } else {
                System.exit(0);
            }
        });
    }
}
