package common;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioPlayer {
    private Clip clip;
    private float volume = 1.0f;

    public void playBackgroundMusic(String filename) {
        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(new File(filename));
            clip = AudioSystem.getClip();
            clip.open(audio);

            setVolume(volume);

            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void playSound(String filename) {
        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(new File(filename));
            Clip soundClip = AudioSystem.getClip();
            soundClip.open(audio);

            setClipVolume(soundClip, volume);

            soundClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playLevelSound(String filename) {
        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(new File(filename));
            Clip levelClip = AudioSystem.getClip();
            levelClip.open(audio);

            setClipVolume(levelClip, volume);

            levelClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume)); // Ограничение 0.0-1.0
        if (clip != null && clip.isOpen()) {
            setClipVolume(clip, this.volume);
        }
    }

    private void setClipVolume(Clip clip, float volume) {
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            float min = gainControl.getMinimum();
            float max = gainControl.getMaximum();

            float gain = (float) (20.0 * Math.log10(volume));
            gain = Math.max(min, Math.min(max, gain));

            gainControl.setValue(gain);
        }
    }

    public float getVolume() {
        return volume;
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
}