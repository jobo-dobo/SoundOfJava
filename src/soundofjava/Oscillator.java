/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soundofjava;

/**
 * Oscillator class providing implementation for generating a waveform at
 * varying frequency and amplitude. Function for the waveform to generate
 * should be provided as an override of waveFunction - a function of an input
 * ranging from 0.0 to 1.0 (right exclusive)
 * 
 * @author Jonathon Zeitler
 */
public class Oscillator extends Component {
    protected double phase;
    protected double[] waveCache;
    protected int cacheLength;
    private double frequency;
    private double amplitude;
    private int freqPort;
    private int ampPort;
    private int fmPort;
    private int amPort;
    private int outPort;
    private boolean cacheEnabled;

    /**
     * Constructor taking starting frequency, amplitude, sample rate,
     * and flag for cache option
     * 
     * @param frq       starting frequency at which to generate waveform
     * @param amp       starting amplitude at which to generate waveform
     * @param cacheOn   true enables caching of the wave function for improved 
     *                  performance
     */
    public Oscillator(double frq, double amp, boolean cacheOn) {
        super();
        
        frequency = frq;
        amplitude = amp;
        phase = 0.0;
        
        freqPort = addInputPort("Frequency");
        ampPort = addInputPort("Amplitude");
        fmPort = addInputPort("Frequency Modulator");
        amPort = addInputPort("Amplitude Modulator");
        
        outPort = addOutputPort("Primary");
        
        cacheEnabled = cacheOn;
        cacheLength = 10000;
        if (cacheEnabled) {
            waveCache = null;
        }
    }
    
    /**
     * Constructor taking starting frequency and amplitude. Rate defaults to the
     * SoundPrinter default sampling rate and cacheEnabled defaults to true
     * 
     * @param frq   starting frequency at which to generate waveform
     * @param amp   starting amplitude at which to generate waveform
     */
    public Oscillator(double frq, double amp) {
        this(frq,amp,true);
    }
    
    /**
     * Constructor taking starting frequency. Amplitude defaults to 1.0, rate
     * defaults to the SoundPrinter default sampling rate, and cacheEnabled
     * defaults to true
     * 
     * @param frq   starting frequency at which to generate waveform
     */
    public Oscillator(double frq) {
        this(frq,1,true);
    }
    
    /**
     * Sets frequency at which to generate waveform
     * @param frq   frequency at which to generate waveform
     */
    public void setFrequency(double frq) { frequency = frq; }
    /**
     * Sets amplitude at which to generate waveform
     * @param amp   amplitude at which to generate waveform
     */
    public void setAmplitude(double amp) { amplitude = amp; }
    /**
     * Sets relative position in the waveform period, ranging from 0.0 to 1.0 
     * (right exclusive). Will be wrapped according to decimal portion
     * @param p phase (0.0 to 1.0) to move generation position to
     */
    public void setPhase(double p) { phase = wrapPhase(p); }
    /**
     * Returns current frequency
     * @return frequency at which waveform is generating
     */
    public double getFrequency() { return frequency; }
    /**
     * Returns current amplitude
     * @return amplitude at which waveform is generating
     */
    public double getAmplitude() { return amplitude; }
    /**
     * Returns current relative position in waveform period, 0.0 to 1.0 (right
     * exclusive)
     * @return current phase
     */
    public double getPhase() { return phase; }
    /**
     * Returns frequency input port number
     * @return frequency input port number
     */
    public int getFrequencyPort() { return freqPort; }
    /**
     * Returns amplitude input port number
     * @return amplitude input port number
     */
    public int getAmplitudePort() { return ampPort; }
    /**
     * Returns frequency modulation input port number
     * @return frequency modulation input port number
     */
    public int getFMPort() { return fmPort; }
    /**
     * Returns amplitude modulation input port number
     * @return amplitude modulation input port number
     */
    public int getAMPort() { return amPort; }
    /**
     * Returns default output port number
     * @return default output port number
     */
    public int getOutputPort() { return outPort; }
    
    /**
     * Generates the next sample
     * @return  sample value generated
     */
    @Override
    public double generate() {
        // Return a 0.0 if not generating
        if (!generateActive) { return 0.0; }
        
        // Read frequency port to frequency if exists and active
        if (inputs.get(freqPort) != null &&
                inputs.get(freqPort).chainable.isGenerating()) {
            frequency = generateInput(freqPort);
        }
        
        // Read amplitude port to amplitude if exists and active
        if (inputs.get(ampPort) != null &&
                inputs.get(ampPort).chainable.isGenerating()) {
            amplitude = generateInput(ampPort);
        }
        
        double realFreq = frequency;
        double realAmp = amplitude;
        
        // Add in frequency modulation from input if exists
        if (inputs.get(fmPort) != null) {
            realFreq += generateInput(fmPort);
        }
        
        // Add in amplitude modulation from input if exists
        if (inputs.get(amPort) != null) {
            realAmp += generateInput(amPort);
        }
        
        // Negative amplitude to zero
        if (realAmp < 0) { realAmp = 0.0; }
        
        // Generate final sample
        double samp;
        if (cacheEnabled) {
            if (waveCache == null) { fillCache(); }
            samp = realAmp*cacheFetch(phase);
        } else {
            samp = realAmp*waveFunction();
        }
        
        // Step phase according to final frequency
        phase += realFreq/rate;
        phase = wrapPhase(phase);
        
        return samp;
    }
    
    /**
     * Generates sample for given output. Only default output is valid for the
     * basic Oscillator
     * 
     * @param outputPort    output port number to generate
     * @return              sample value generated
     */
    @Override
    public double generate(int outputPort) {
        if (outputPort != outPort) { return 0.0; }
        return generate();
    }
    
    /**
     * Changes the cache length and rebuilds the wave cache. Higher cache length
     * requires more memory, but reduces risk of aliasing
     * 
     * @param len   new cache length
     */
    public void setCacheSize(int len) {
        cacheLength = len;
        waveCache = new double[cacheLength];
        fillCache();
    }
    
    /**
     * Enables cached waveform to improve performance (in some  circumstances,
     * a small or inappropriate cache length may result in aliasing)
     */
    public void enableCache() { cacheEnabled = true; }
    
    /**
     * Disables waveform cache and generates it in realtime. May improve
     * accuracy and reduce aliasing, but disabling caching for computationally
     * expensive wave functions can cause considerable degradation in 
     * performance.
     */
    public void disableCache() { cacheEnabled = false; }
    
    /**
     * Rebuilds waveform cache
     */
    private void fillCache() {
        if (waveCache == null) {
            waveCache = new double[cacheLength];
        }
        if (cacheEnabled) {
            for (int i=0; i<cacheLength; i++) {
                waveCache[i] = waveFunction();
                phase += (1.0/(double)cacheLength);
            }
            phase = 0.0;
        }
    }
    
    /**
     * Generates a value -1.0 to 1.0 based on current phase (ranging from 0.0
     * to 1.0, right exclusive). Intended to be overridden by child classes,
     * this function provides the waveform the oscillator will generate.
     * 
     * @return  generated sample according to current phase
     */
    protected double waveFunction() {
        return Math.sin(phase*2.0*Math.PI);
    }
    
    /**
     * Returns a linearly interpolated sample from the waveform cache based on
     * a phase position from 0.0 to 1.0 (right exclusive)
     * 
     * @param pos   phase position, based on 0.0 to 1.0 period
     * @return      sample from the given phase position
     */
    protected double cacheFetch(double pos) {
        double realPos = wrapPhase(pos)*(double)cacheLength;
        int index1 = (int)Math.floor(realPos);
        int index2 = index1 + 1;
        if (index2 >= cacheLength) { index2 = 0; }
        realPos -= (double)index1;
        return (1.0 - realPos)*waveCache[index1] + 
                realPos*waveCache[index2];
    }
    
    /**
     * Wraps any double value to boundaries of 0.0 to 1.0 (right exclusive)
     * 
     * @param p phase to wrap
     * @return  wrapped phase from 0.0 to 1.0 (right exclusive)
     */
    private double wrapPhase(double p) {
        if (p<0) { p = 1.0 - Math.floor(-1*p); }
        if (p>=1) { p -= Math.floor(p); }
        return p;
    }
}
