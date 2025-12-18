package common;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class VolumeControlPanel extends JPanel {
    private final JSlider volumeSlider;
    private final JLabel volumeLabel;
    private final AudioPlayer audioPlayer;
    private int previousVolume;

    public VolumeControlPanel(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        this.previousVolume = 80;

        setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        setOpaque(false);

        volumeLabel = createVolumeLabel();
        volumeSlider = createVolumeSlider();

        add(volumeLabel);
        add(volumeSlider);

        setVolume(volumeSlider.getValue());
    }

    private JLabel createVolumeLabel() {
        JLabel label = new JLabel();
        try {
            ImageIcon volumeIcon = new ImageIcon(VolumeControlPanel.class.getResource("/images/volume_icon.png"));
            if (volumeIcon.getImage() != null) {
                label.setIcon(volumeIcon);
            } else {
                label.setText("🔊");
                label.setFont(new Font("SansSerif", Font.PLAIN, 18));
            }
        } catch (Exception e) {
            label.setText("🔊");
            label.setFont(new Font("SansSerif", Font.PLAIN, 18));
        }
        label.setForeground(Color.WHITE);
        return label;
    }

    private JSlider createVolumeSlider() {
        JSlider slider = new JSlider(0, 100, 80);
        slider.setPreferredSize(new Dimension(120, 40));
        slider.setToolTipText("Adjust volume");
        slider.setBackground(new Color(15, 35, 60));
        slider.setForeground(new Color(237, 176, 36));
        slider.setOpaque(false);

        slider.setUI(new javax.swing.plaf.basic.BasicSliderUI(slider) {
            @Override
            public void paintTrack(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Rectangle trackBounds = trackRect;
                int trackHeight = 6;
                int trackY = trackBounds.y + (trackBounds.height - trackHeight) / 2;

                g2d.setColor(new Color(50, 70, 95));
                g2d.fillRoundRect(trackBounds.x, trackY, trackBounds.width, trackHeight, 3, 3);

                int fillWidth = thumbRect.x - trackBounds.x;
                g2d.setColor(new Color(237, 176, 36));
                g2d.fillRoundRect(trackBounds.x, trackY, fillWidth, trackHeight, 3, 3);

                g2d.setColor(new Color(100, 120, 145));
                g2d.drawRoundRect(trackBounds.x, trackY, trackBounds.width, trackHeight, 3, 3);
            }

            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int thumbSize = 16;
                int thumbX = thumbRect.x + (thumbRect.width - thumbSize) / 2;
                int thumbY = thumbRect.y + (thumbRect.height - thumbSize) / 2;

                g2d.setColor(new Color(237, 176, 36));
                g2d.fillOval(thumbX, thumbY, thumbSize, thumbSize);

                g2d.setColor(Color.WHITE);
                g2d.fillOval(thumbX + 2, thumbY + 2, thumbSize - 4, thumbSize - 4);
            }
        });

        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!slider.getValueIsAdjusting()) {
                    int volume = slider.getValue();
                    setVolume(volume);

                    updateVolumeIcon(volume);
                }
            }
        });

        return slider;
    }

    public void setVolume(int volume) {
        volume = Math.max(0, Math.min(100, volume));
        volumeSlider.setValue(volume);
        float volumeFloat = volume / 100.0f;
        audioPlayer.setVolume(volumeFloat);

        if (volume > 0) {
            previousVolume = volume;
        }

        updateVolumeIcon(volume);
    }

    public int getVolume() {
        return volumeSlider.getValue();
    }

    public void toggleMute() {
        int currentVolume = getVolume();
        if (currentVolume > 0) {
            previousVolume = currentVolume;
            setVolume(0);
        } else {
            setVolume(previousVolume);
        }
    }

    private void updateVolumeIcon(int volume) {
        String iconText;
        if (volume == 0) {
            iconText = "🔇";
        } else if (volume < 33) {
            iconText = "🔈";
        } else if (volume < 66) {
            iconText = "🔉";
        } else {
            iconText = "🔊";
        }

        if (volumeLabel.getIcon() == null) {
            volumeLabel.setText(iconText);
        }

        volumeSlider.setToolTipText("Volume: " + volume + "%");
    }

    public JSlider getVolumeSlider() {
        return volumeSlider;
    }

    public JLabel getVolumeLabel() {
        return volumeLabel;
    }
}