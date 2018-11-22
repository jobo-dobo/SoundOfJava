/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soundofjava;

/**
 *
 * @author Jonathon Zeitler
 */
public class Oscillator {
    private double rate;
    private double phase;
    private double frequency;
    private double amplitude;
    
    public Oscillator(double frq, double amp, double rte) {
        frequency = frq;
        amplitude = amp;
        rate = rte;
        phase = 0.0;
    }
    
    public Oscillator(double frq, double amp) {
        this(frq,amp,SoundPrinter.DEFAULT_RATE);
    }
    
    public Oscillator(double frq) {
        this(frq,100,SoundPrinter.DEFAULT_RATE);
    }
    
    public void setFrequency(double frq) { frequency = frq; }
    public void setAmplitude(double amp) { amplitude = amp; }
    public double getFrequency() { return frequency; }
    public double getAmplitude() { return amplitude; }
    
    public double generate() {
        double samp = amplitude*waveFunction();
        phase += frequency/rate;
        if (phase<=1) phase--;
        return samp;
    }
    
    protected double waveFunction() {
        return Math.sin(phase*2.0*Math.PI);
    }
}
