package com.soundspectrogram;

import com.musicg.graphic.GraphicRender;
import com.musicg.wave.Wave;
import com.musicg.wave.extension.Spectrogram;

import java.io.*;

/**
 * Created by vlad on 15.09.15.
 */
public class DemoWave {

    private String filename;
    private String outFolder;
    private Wave wave;
    private Spectrogram spectrogram;
    private File fileHeader;
    private File fileSpectrogram;
    private File fileAmplitudes;
    private FileWriter fileWriterHeader;
    private FileWriter fileWriterSpectrogram;
    private FileWriter fileWriterAmplitudes;

    public DemoWave(String filename, String outFolder) throws IOException {

        this.filename = filename;
        this.outFolder = outFolder;
        this.wave = new Wave(this.filename);
        this.spectrogram = new Spectrogram(wave);
        this.fileSpectrogram = new File("out/spectrogram.txt");
        this.fileHeader = new File("out/header.txt");
        this.fileAmplitudes = new File("out/amplitudes.txt");
        fileSpectrogram.createNewFile();
        fileHeader.createNewFile();
        fileAmplitudes.createNewFile();
        this.fileWriterHeader = new FileWriter(fileHeader);
        this.fileWriterSpectrogram = new FileWriter(fileSpectrogram);
        this.fileWriterAmplitudes = new FileWriter(fileAmplitudes);
        this.spectrogram = new Spectrogram(wave);

    }

    public void getWavHeader() throws IOException {
        fileWriterHeader.write(wave.toString() + "\n\n");
        fileWriterHeader.flush();
        fileWriterHeader.close();
    }

    public void getSpectrogramData() throws IOException {
        double[] [] pairAmlitudeFrequency = spectrogram.getNormalizedSpectrogramData();
        for (double[] column : pairAmlitudeFrequency ){
            for (double row : column){
                fileWriterSpectrogram.write(row + " ");
            }
            fileWriterSpectrogram.write("\n");
        }

        double[] normalizedAmplitudes = wave.getNormalizedAmplitudes();

        for (double row : normalizedAmplitudes) {
            fileWriterAmplitudes.write(row + "\n");
        }

        fileWriterSpectrogram.flush();
        fileWriterAmplitudes.flush();
        fileWriterSpectrogram.close();
        fileWriterAmplitudes.close();
    }

    public void pictureSpectrogram(){

        GraphicRender render = new GraphicRender();
        render.renderSpectrogramData(spectrogram.getNormalizedSpectrogramData(), outFolder + "/spectrogram.jpg");


    }

    public void pictureWave() {
        GraphicRender graphicRender = new GraphicRender();
        graphicRender.renderWaveform(wave, outFolder+"/waveform.jpg");
    }
}
