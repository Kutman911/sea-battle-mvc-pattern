package common;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class AudioPlayer {
    private Clip clip;
    private float volume = 1.0f;

    public void playBackgroundMusic(String resourcePath) {
        try {
            InputStream audioSrc = getClass().getResourceAsStream(resourcePath);
            if (audioSrc == null) {
                System.err.println("Audio file not found: " + resourcePath);
                return;
            }
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audio = AudioSystem.getAudioInputStream(bufferedIn);

            clip = AudioSystem.getClip();
            clip.open(audio);
            setVolume(volume);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playSound(String resourcePath) {
        try {
            InputStream audioSrc = getClass().getResourceAsStream(resourcePath);
            if (audioSrc == null) {
                System.err.println("Audio file not found: " + resourcePath);
                return;
            }
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audio = AudioSystem.getAudioInputStream(bufferedIn);

            Clip soundClip = AudioSystem.getClip();
            soundClip.open(audio);
            setClipVolume(soundClip, volume);
            soundClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playLevelSound(String resourcePath) {
        try {
            InputStream audioSrc = getClass().getResourceAsStream(resourcePath);
            if (audioSrc == null) {
                System.err.println("Audio file not found: " + resourcePath);
                return;
            }
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audio = AudioSystem.getAudioInputStream(bufferedIn);

            Clip levelClip = AudioSystem.getClip();
            levelClip.open(audio);
            setClipVolume(levelClip, volume);
            levelClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
        if (clip != null && clip.isOpen()) {
            setClipVolume(clip, this.volume);
        }
    }

    private void setClipVolume(Clip clip, float volume) {
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float min = gainControl.getMinimum();
            float max = gainControl.getMaximum();
            float gain = (float) (20.0 * Math.log10(Math.max(0.0001f, volume)));
            gain = Math.max(min, Math.min(max, gain));
            gainControl.setValue(gain);
        }
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
}