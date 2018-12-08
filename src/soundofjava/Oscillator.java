/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soundofjava;

import java.util.ArrayList;

/**
 *
 * @author Jonathon Zeitler
 */
public class Oscillator extends Component {
    private double rate;
    protected double phase;
    private double frequency;
    private double amplitude;
    private int freqPort;
    private int ampPort;
    private int outPort;

    public Oscillator(double frq, double amp, double rte) {
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
        inputSamples = new ArrayList<>();
        outputSamples = new ArrayList<>();
        inputPorts = new ArrayList<>();
        outputPorts = new ArrayList<>();
        
        frequency = frq;
        amplitude = amp/100;
        rate = rte;
        phase = 0.0;
        
        freqPort = addInputPort("Frequency");
        ampPort = addInputPort("Amplitude");
        outPort = addOutputPort("Primary");
    }
    
    public Oscillator(double frq, double amp) {
        this(frq,amp,SoundPrinter.DEFAULT_RATE);
    }
    
    public Oscillator(double frq) {
        this(frq,100,SoundPrinter.DEFAULT_RATE);
    }
    
    public void setFrequency(double frq) { frequency = frq; }
    public void setAmplitude(double amp) { amplitude = amp/100; }
    public void setPhase(double p) { phase = wrapPhase(p); }
    public double getFrequency() { return frequency; }
    public double getAmplitude() { return amplitude*100; }
    public double getPhase() { return phase; }
    public int getFrequencyPort() { return freqPort; }
    public int getAmplitudePort() { return ampPort; }
    public int getOutputPort() { return outPort; }
    
    @Override
    public double generate() {
        if (!generateActive) { return 0.0; }
        
        double realFreq = frequency;
        double realAmp = amplitude;
        
        if (inputs.get(freqPort) != null) {
            realFreq += generateInput(freqPort);
        }
        
        if (inputs.get(ampPort) != null) {
            realAmp += generateInput(ampPort);
        }
        
        double samp = realAmp*waveFunction();
        phase += realFreq/rate;
        phase = wrapPhase(phase);
        return samp;
    }
    
    @Override
    public double generate(int outputPort) {
        if (outputPort != outPort) { return 0.0; }
        return generate();
    }
    
    protected double waveFunction() {
        return Math.sin(phase*2.0*Math.PI);
    }
    
    private double wrapPhase(double p) {
        if (p<0) { p = 1.0 - Math.floor(-1*p); }
        if (p>=1) { p -= Math.floor(p); }
        return p;
    }
}
