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
                        viewer.update();
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
                        nextLevel();
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
}
