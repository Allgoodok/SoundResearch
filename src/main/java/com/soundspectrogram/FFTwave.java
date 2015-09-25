package com.soundspectrogram;

import com.musicg.fingerprint.FingerprintManager;
import com.musicg.fingerprint.FingerprintSimilarity;
import com.musicg.fingerprint.FingerprintSimilarityComputer;
import com.musicg.wave.Wave;
import com.musicg.wave.WaveHeader;
import com.musicg.wave.extension.NormalizedSampleAmplitudes;
import com.musicg.wave.extension.Spectrogram;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by vlad on 16.09.15.
 */
public class FFTwave {
    private static final long serialVersionUID = 1L;
    private WaveHeader waveHeader;
    private byte[] data;
    private byte[] fingerprint;

    public FFTwave() {
        this.waveHeader = new WaveHeader();
        this.data = new byte[0];
    }

    public FFTwave(String filename) {
        try {
            FileInputStream e = new FileInputStream(filename);
            this.initWaveWithInputStream(e);
            e.close();
        } catch (FileNotFoundException var3) {
            var3.printStackTrace();
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    public FFTwave(InputStream inputStream) {
        this.initWaveWithInputStream(inputStream);
    }

    public FFTwave(WaveHeader waveHeader, byte[] data) {
        this.waveHeader = waveHeader;
        this.data = data;
    }

    private void initWaveWithInputStream(InputStream inputStream) {
        this.waveHeader = new WaveHeader(inputStream);
        if(this.waveHeader.isValid()) {
            try {
                this.data = new byte[inputStream.available()];
                inputStream.read(this.data);
            } catch (IOException var3) {
                var3.printStackTrace();
            }
        } else {
            System.err.println("Invalid Wave Header");
        }

    }

    public void trim(int leftTrimNumberOfSample, int rightTrimNumberOfSample) {
        long chunkSize = this.waveHeader.getChunkSize();
        long subChunk2Size = this.waveHeader.getSubChunk2Size();
        long totalTrimmed = (long)(leftTrimNumberOfSample + rightTrimNumberOfSample);
        if(totalTrimmed > subChunk2Size) {
            leftTrimNumberOfSample = (int)subChunk2Size;
        }

        chunkSize -= totalTrimmed;
        subChunk2Size -= totalTrimmed;
        if(chunkSize >= 0L && subChunk2Size >= 0L) {
            this.waveHeader.setChunkSize(chunkSize);
            this.waveHeader.setSubChunk2Size(subChunk2Size);
            byte[] trimmedData = new byte[(int)subChunk2Size];
            System.arraycopy(this.data, leftTrimNumberOfSample, trimmedData, 0, (int)subChunk2Size);
            this.data = trimmedData;
        } else {
            System.err.println("Trim error: Negative length");
        }

    }

    public void leftTrim(int numberOfSample) {
        this.trim(numberOfSample, 0);
    }

    public void rightTrim(int numberOfSample) {
        this.trim(0, numberOfSample);
    }

    public void trim(double leftTrimSecond, double rightTrimSecond) {
        int sampleRate = this.waveHeader.getSampleRate();
        int bitsPerSample = this.waveHeader.getBitsPerSample();
        int channels = this.waveHeader.getChannels();
        int leftTrimNumberOfSample = (int)((double)(sampleRate * bitsPerSample / 8 * channels) * leftTrimSecond);
        int rightTrimNumberOfSample = (int)((double)(sampleRate * bitsPerSample / 8 * channels) * rightTrimSecond);
        this.trim(leftTrimNumberOfSample, rightTrimNumberOfSample);
    }

    public void leftTrim(double second) {
        this.trim(second, 0.0D);
    }

    public void rightTrim(double second) {
        this.trim(0.0D, second);
    }

    public WaveHeader getWaveHeader() {
        return this.waveHeader;
    }

    /*public Spectrogram getSpectrogram() {
        //return new Spectrogram(this);
    }

    public Spectrogram getSpectrogram(int fftSampleSize, int overlapFactor) {
        return new Spectrogram(this, fftSampleSize, overlapFactor);
    }*/

    public byte[] getBytes() {
        return this.data;
    }

    public int size() {
        return this.data.length;
    }

    public float length() {
        float second = (float)this.waveHeader.getSubChunk2Size() / (float)this.waveHeader.getByteRate();
        return second;
    }

    public String timestamp() {
        float totalSeconds = this.length();
        float second = totalSeconds % 60.0F;
        int minute = (int)totalSeconds / 60 % 60;
        int hour = (int)(totalSeconds / 3600.0F);
        StringBuffer sb = new StringBuffer();
        if(hour > 0) {
            sb.append(hour + ":");
        }

        if(minute > 0) {
            sb.append(minute + ":");
        }

        sb.append(second);
        return sb.toString();
    }

    public short[] getSampleAmplitudes() {
        int bytePerSample = this.waveHeader.getBitsPerSample() / 8;
        int numSamples = this.data.length / bytePerSample;
        short[] amplitudes = new short[numSamples];
        int pointer = 0;

        for(int i = 0; i < numSamples; ++i) {
            short amplitude = 0;

            for(int byteNumber = 0; byteNumber < bytePerSample; ++byteNumber) {
                amplitude |= (short)((this.data[pointer++] & 255) << byteNumber * 8);
            }

            amplitudes[i] = amplitude;
        }

        return amplitudes;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(this.waveHeader.toString());
        sb.append("\n");
        sb.append("length: " + this.timestamp());
        return sb.toString();
    }

   /* public double[] getNormalizedAmplitudes() {
        NormalizedSampleAmplitudes amplitudes = new NormalizedSampleAmplitudes(this);
        return amplitudes.getNormalizedAmplitudes();
    }*/

    /*public byte[] getFingerprint() {
        if(this.fingerprint == null) {
            FingerprintManager fingerprintManager = new FingerprintManager();
            this.fingerprint = fingerprintManager.extractFingerprint(this);
        }

        return this.fingerprint;
    }*/

    /*public FingerprintSimilarity getFingerprintSimilarity(Wave wave) {
        FingerprintSimilarityComputer fingerprintSimilarityComputer = new FingerprintSimilarityComputer(this.getFingerprint(), wave.getFingerprint());
        return fingerprintSimilarityComputer.getFingerprintsSimilarity();
    }*/
}
