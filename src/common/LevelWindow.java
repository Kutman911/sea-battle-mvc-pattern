package common;
import canvas.Canvas;
import model.Model;
import viewer.Viewer;
import javax.swing.SwingUtilities;
import java.awt.Window;
import java.util.Timer;
import java.util.TimerTask;

public class LevelWindow {

    private final int LEVEL_START = 1;
    private final int MAX_LEVEL = 3;

//    private static final int STATE_HIDDEN = 0;
//    private static final int STATE_LEVEL_START = 1;
//    private static final int STATE_LEVEL_COMPLETED = 2;
//    private static final int STATE_FINAL = 3;

    public enum WindowState {
        HIDDEN,
        LEVEL_START,
        LEVEL_COMPLETED,
        FINAL
    }

    private final int SHOW_DURATION_MS = 2400;
    private final int FADE_OUT_DELAY_MS = 400;
    private final int FINAL_RESET_DELAY_MS = 3000;
    private final int CONGRATS_DURATION_MS = 2000;

    private final float FADE_STEP = 0.03f;
    private final int FADE_TIMER_PERIOD_MS = 15;

    private final String LEVEL_SOUND_PATH = "/resources/sounds/levelSound.wav";

    private int currentLevel = LEVEL_START;
    private WindowState windowState = WindowState.HIDDEN;

    private final Viewer viewer;
    private final Canvas canvas;
    private final Model model;
    private final AudioPlayer audioPlayer;

    private float alpha = 0f;

    public LevelWindow(Viewer viewer, Canvas canvas, Model model) {
        this.viewer = viewer;
        this.canvas = canvas;
        this.model = model;
        this.audioPlayer = viewer.getAudioPlayer();
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public WindowState getWindowState() {
        return windowState;
    }

    public float getAlpha() {
        return alpha;
    }

    public void showLevelStartWindow() {
        windowState = windowState.LEVEL_START;
        audioPlayer.playLevelSound(LEVEL_SOUND_PATH);
        fadeIn();

        new Timer().schedule(new TimerTask() {
            public void run() {
                fadeOut();
                new Timer().schedule(new TimerTask() {
                    public void run() {
                        windowState = WindowState.HIDDEN;
                        SwingUtilities.invokeLater(viewer::update);
                    }
                }, FADE_OUT_DELAY_MS);
            }
        }, SHOW_DURATION_MS);
    }

    public void showLevelCompletedWindow() {
        windowState = WindowState.LEVEL_COMPLETED;
        fadeIn();
        viewer.update();

        new Timer().schedule(new TimerTask() {
            public void run() {
                fadeOut();
                new Timer().schedule(new TimerTask() {
                    public void run() {
                        SwingUtilities.invokeLater(() -> {
                            if (currentLevel < MAX_LEVEL) {
                                showResultDialog(true);
                            } else {
                                showCongratulationsWindow();
                            }
                        });
                    }
                }, FADE_OUT_DELAY_MS);
            }
        }, SHOW_DURATION_MS);
    }

    private void showFinalWindow() {
        windowState = WindowState.FINAL;
        fadeIn();
        viewer.update();
    }

    public void nextLevel() {
        currentLevel++;

        if (currentLevel > MAX_LEVEL) {
            showFinalWindow();
            new Timer().schedule(new TimerTask() {
                public void run() {
                    model.resetGame();
                }
            }, FINAL_RESET_DELAY_MS);
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
                alpha += FADE_STEP;
                if (alpha >= 1f) {
                    alpha = 1f;
                    timer.cancel();
                }
                canvas.repaint();
            }
        }, 0, FADE_TIMER_PERIOD_MS);
    }

    private void fadeOut() {
        alpha = 1f;
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                alpha -= FADE_STEP;
                if (alpha <= 0f) {
                    alpha = 0f;
                    timer.cancel();
                }
                canvas.repaint();
            }
        }, 0, FADE_TIMER_PERIOD_MS);
    }

    public void resetToLevelOne() {
        currentLevel = LEVEL_START;
        windowState = WindowState.HIDDEN;
        alpha = 0f;
    }

    public void showResultDialog(boolean playerWon) {
        SwingUtilities.invokeLater(() -> {
            Window parent = SwingUtilities.getWindowAncestor(canvas);

            ResultDialog dialog = new ResultDialog(
                    parent,
                    playerWon,
                    currentLevel,
                    () -> {
                        model.resetGame();
                        enableViewerButtons();
                        showLevelStartWindow();
                    },
                    () -> {
                        nextLevel();
                        enableViewerButtons();
                    },
                    () -> System.exit(0)
            );

            dialog.setVisible(true);
        });
    }

    public void showCongratulationsWindow() {
        windowState = WindowState.FINAL;
        fadeIn();
        viewer.update();

        new Timer().schedule(new TimerTask() {
            public void run() {
                fadeOut();
                new Timer().schedule(new TimerTask() {
                    public void run() {
                        SwingUtilities.invokeLater(() -> showResultDialog(true));
                    }
                }, FADE_OUT_DELAY_MS);
            }
        }, CONGRATS_DURATION_MS);
    }

    private void enableViewerButtons() {
        if (viewer.getStartButton() != null) {
            viewer.getStartButton().setEnabled(true);
            viewer.getStartButton().setVisible(true);
        }

        if (viewer.getRandomButton() != null) {
            viewer.getRandomButton().setEnabled(true);
            viewer.getRandomButton().setVisible(true);
        }
    }
}
