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
    protected double[] waveCache;
    protected int cacheLength;
    private boolean cacheEnabled;

    public Oscillator(double frq, double amp, double rte, boolean cacheOn) {
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
        
        cacheEnabled = cacheOn;
        cacheLength = 10000;
        if (cacheEnabled) {
            waveCache = null;
        }
    }
    
    public Oscillator(double frq, double amp) {
        this(frq,amp,SoundPrinter.DEFAULT_RATE,true);
    }
    
    public Oscillator(double frq) {
        this(frq,100,SoundPrinter.DEFAULT_RATE,true);
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
        
        double samp;
        if (cacheEnabled) {
            if (waveCache == null) { fillCache(); }
            samp = realAmp*cacheFetch(phase);
        } else {
            samp = realAmp*waveFunction();
        }
        
        if (inputs.get(freqPort) != null) {
            realFreq += generateInput(freqPort);
        }
        
        if (inputs.get(ampPort) != null) {
            realAmp += generateInput(ampPort);
        }
        
        phase += realFreq/rate;
        phase = wrapPhase(phase);
        return samp;
    }
    
    @Override
    public double generate(int outputPort) {
        if (outputPort != outPort) { return 0.0; }
        return generate();
    }
    
    public void setCacheSize(int len) {
        cacheLength = len;
        waveCache = new double[cacheLength];
        fillCache();
    }
    
    public void enableCache() { cacheEnabled = true; }
    public void disableCache() { cacheEnabled = false; }
    
    private void fillCache() {
        if (cacheEnabled) {
            for (int i=0; i<cacheLength; i++) {
                waveCache[i] = waveFunction();
                phase += (1.0/(double)cacheLength);
            }
            phase = 0.0;
        }
    }
    
    protected double waveFunction() {
        return Math.sin(phase*2.0*Math.PI);
    }
    
    protected double cacheFetch(double pos) {
        double realPos = pos*(double)cacheLength;
        int index1 = (int)Math.floor(realPos);
        int index2 = index1 + 1;
        if (index2 >= cacheLength) { index2 = 0; }
        realPos -= (double)index1;
        return (1.0 - realPos)*waveCache[index1] + 
                realPos*waveCache[index2];
    }
    
    private double wrapPhase(double p) {
        if (p<0) { p = 1.0 - Math.floor(-1*p); }
        if (p>=1) { p -= Math.floor(p); }
        return p;
    }
}
