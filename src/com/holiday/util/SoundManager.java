package com.holiday.util;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

public class SoundManager {

    private static final float SAMPLE_RATE = 44100f;
    private static boolean soundEnabled = true;

    public static boolean isSoundEnabled() {
        return soundEnabled;
    }

    public static void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
    }

    public static void playClickSound() {
        if (!soundEnabled) return;
        new Thread(() -> {
            try {
                byte[] pcm = generateTone(1200, 25, 0.25f, ToneType.SINE_DECAY);
                playPcmAudio(pcm);
            } catch (Exception e) {
                // Ignore sound errors gracefully
            }
        }, "AudioThread-Click").start();
    }

    public static void playSuccessSound() {
        if (!soundEnabled) return;
        new Thread(() -> {
            try {
                // Chord sequence: C5 (523Hz) -> E5 (659Hz) -> G5 (784Hz)
                byte[] tone1 = generateTone(523, 70, 0.35f, ToneType.SINE_DECAY);
                byte[] tone2 = generateTone(659, 70, 0.35f, ToneType.SINE_DECAY);
                byte[] tone3 = generateTone(784, 140, 0.45f, ToneType.SINE_DECAY);

                byte[] combined = new byte[tone1.length + tone2.length + tone3.length];
                System.arraycopy(tone1, 0, combined, 0, tone1.length);
                System.arraycopy(tone2, 0, combined, tone1.length, tone2.length);
                System.arraycopy(tone3, 0, combined, tone1.length + tone2.length, tone3.length);

                playPcmAudio(combined);
            } catch (Exception e) {
                // Ignore sound errors gracefully
            }
        }, "AudioThread-Success").start();
    }

    public static void playErrorSound() {
        if (!soundEnabled) return;
        new Thread(() -> {
            try {
                // Low warning buzz tone: 220Hz / 180Hz square wave
                byte[] tone1 = generateTone(220, 100, 0.4f, ToneType.SQUARE);
                byte[] tone2 = generateTone(180, 120, 0.4f, ToneType.SQUARE);

                byte[] combined = new byte[tone1.length + tone2.length];
                System.arraycopy(tone1, 0, combined, 0, tone1.length);
                System.arraycopy(tone2, 0, combined, tone1.length, tone2.length);

                playPcmAudio(combined);
            } catch (Exception e) {
                // Ignore sound errors gracefully
            }
        }, "AudioThread-Error").start();
    }

    private enum ToneType {
        SINE_DECAY,
        SQUARE
    }

    private static byte[] generateTone(int freq, int durationMs, float volume, ToneType type) {
        int samples = (int) (SAMPLE_RATE * (durationMs / 1000.0f));
        byte[] buffer = new byte[samples * 2]; // 16-bit PCM mono

        for (int i = 0; i < samples; i++) {
            double angle = 2.0 * Math.PI * i * freq / SAMPLE_RATE;
            double sampleVal;

            if (type == ToneType.SQUARE) {
                sampleVal = Math.sin(angle) >= 0 ? 1.0 : -1.0;
            } else {
                // Sine wave with exponential volume decay envelope
                double decay = 1.0 - ((double) i / samples);
                sampleVal = Math.sin(angle) * decay;
            }

            short pcmVal = (short) (sampleVal * volume * Short.MAX_VALUE);
            buffer[i * 2] = (byte) (pcmVal & 0xFF);
            buffer[i * 2 + 1] = (byte) ((pcmVal >> 8) & 0xFF);
        }
        return buffer;
    }

    private static void playPcmAudio(byte[] pcmData) throws Exception {
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
        SourceDataLine line = AudioSystem.getSourceDataLine(format);
        line.open(format, pcmData.length);
        line.start();
        line.write(pcmData, 0, pcmData.length);
        line.drain();
        line.close();
    }
}
