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
public class Oscillator extends Component {
    private double rate;
    protected double phase;
    private double frequency;
    private double amplitude;
    private int freqPort;
    private int ampPort;
    private int outPort;

    public Oscillator(double frq, double amp, double rte) {
        frequency = frq;
        amplitude = amp;
        rate = rte;
        phase = 0.0;
        
        freqPort = AddInputPort("Frequency");
        ampPort = AddInputPort("Amplitude");
        outPort = AddOutputPort("Primary");
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
        if (phase>=1) phase--;
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
}
