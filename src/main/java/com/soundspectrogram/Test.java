package com.soundspectrogram;

import java.io.IOException;

/**
 * Created by vlad on 15.09.15.
 */
public class Test {
    public static void main(String[] args) throws IOException {
       DemoWave demoWave = new DemoWave("audio_work/American_English/Female_1.wav", "out");
       demoWave.getWavHeader();
        demoWave.getSpectrogramData();
        demoWave.pictureSpectrogram();
        demoWave.pictureWave();
    }
}
