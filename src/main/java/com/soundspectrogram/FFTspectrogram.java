package com.soundspectrogram;

import com.musicg.dsp.FastFourierTransform;
import com.musicg.dsp.WindowFunction;
import com.musicg.wave.Wave;

/**
 * Created by vlad on 16.09.15.
 */
public class FFTspectrogram {
    public static final int SPECTROGRAM_DEFAULT_FFT_SAMPLE_SIZE = 1024;
    public static final int SPECTROGRAM_DEFAULT_OVERLAP_FACTOR = 0;
    private Wave wave;
    private double[][] spectrogram;
    private double[][] absoluteSpectrogram;
    private int fftSampleSize;
    private int overlapFactor;
    private int numFrames;
    private int framesPerSecond;
    private int numFrequencyUnit;
    private double unitFrequency;

    public FFTspectrogram(Wave wave) {
        this.wave = wave;
        this.fftSampleSize = 1024;
        this.overlapFactor = 0;
        this.buildSpectrogram();
    }

    public FFTspectrogram(Wave wave, int fftSampleSize, int overlapFactor) {
        this.wave = wave;
        if(Integer.bitCount(fftSampleSize) == 1) {
            this.fftSampleSize = fftSampleSize;
        } else {
            System.err.print("The input number must be a power of 2");
            this.fftSampleSize = 1024;
        }

        this.overlapFactor = overlapFactor;
        this.buildSpectrogram();
    }

    private void buildSpectrogram() {
        short[] amplitudes = this.wave.getSampleAmplitudes();
        int numSamples = amplitudes.length;
        boolean pointer = false;
        int maxAmp;
        if(this.overlapFactor > 1) {
            int window = numSamples * this.overlapFactor;
            int win = this.fftSampleSize * (this.overlapFactor - 1) / this.overlapFactor;
            int signals = this.fftSampleSize - 1;
            short[] fft = new short[window];
            int var18 = 0;

            for(maxAmp = 0; maxAmp < amplitudes.length; ++maxAmp) {
                fft[var18++] = amplitudes[maxAmp];
                if(var18 % this.fftSampleSize == signals) {
                    maxAmp -= win;
                }
            }

            numSamples = window;
            amplitudes = fft;
        }

        this.numFrames = numSamples / this.fftSampleSize;
        this.framesPerSecond = (int)((float)this.numFrames / this.wave.length());
        WindowFunction var19 = new WindowFunction();
        var19.setWindowType("Hamming");
        double[] var20 = var19.generate(this.fftSampleSize);
        double[][] var21 = new double[this.numFrames][];

        for(int var22 = 0; var22 < this.numFrames; ++var22) {
            var21[var22] = new double[this.fftSampleSize];
            maxAmp = var22 * this.fftSampleSize;

            for(int n = 0; n < this.fftSampleSize; ++n) {
                var21[var22][n] = (double)amplitudes[maxAmp + n] * var20[n];
            }
        }

        this.absoluteSpectrogram = new double[this.numFrames][];
        FastFourierTransform var23 = new FastFourierTransform();

        for(maxAmp = 0; maxAmp < this.numFrames; ++maxAmp) {
            this.absoluteSpectrogram[maxAmp] = var23.getMagnitudes(var21[maxAmp]);
        }

        if(this.absoluteSpectrogram.length > 0) {
            this.numFrequencyUnit = this.absoluteSpectrogram[0].length;
            this.unitFrequency = (double)this.wave.getWaveHeader().getSampleRate() / 2.0D / (double)this.numFrequencyUnit;
            this.spectrogram = new double[this.numFrames][this.numFrequencyUnit];
            double var24 = 4.9E-324D;
            double minAmp = 1.7976931348623157E308D;

            for(int minValidAmp = 0; minValidAmp < this.numFrames; ++minValidAmp) {
                for(int j = 0; j < this.numFrequencyUnit; ++j) {
                    if(this.absoluteSpectrogram[minValidAmp][j] > var24) {
                        var24 = this.absoluteSpectrogram[minValidAmp][j];
                    } else if(this.absoluteSpectrogram[minValidAmp][j] < minAmp) {
                        minAmp = this.absoluteSpectrogram[minValidAmp][j];
                    }
                }
            }

            double var25 = 9.999999960041972E-12D;
            if(minAmp == 0.0D) {
                minAmp = var25;
            }

            double diff = Math.log10(var24 / minAmp);

            for(int i = 0; i < this.numFrames; ++i) {
                for(int j1 = 0; j1 < this.numFrequencyUnit; ++j1) {
                    if(this.absoluteSpectrogram[i][j1] < var25) {
                        this.spectrogram[i][j1] = 0.0D;
                    } else {
                        this.spectrogram[i][j1] = Math.log10(this.absoluteSpectrogram[i][j1] / minAmp) / diff;
                    }
                }
            }
        }

    }

    public double[][] getNormalizedSpectrogramData() {
        return this.spectrogram;
    }

    public double[][] getAbsoluteSpectrogramData() {
        return this.absoluteSpectrogram;
    }

    public int getNumFrames() {
        return this.numFrames;
    }

    public int getFramesPerSecond() {
        return this.framesPerSecond;
    }

    public int getNumFrequencyUnit() {
        return this.numFrequencyUnit;
    }

    public double getUnitFrequency() {
        return this.unitFrequency;
    }

    public int getFftSampleSize() {
        return this.fftSampleSize;
    }

    public int getOverlapFactor() {
        return this.overlapFactor;
    }
}
