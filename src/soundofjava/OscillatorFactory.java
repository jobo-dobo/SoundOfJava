/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soundofjava;

/**
 * Factory class which creates Oscillators with different waveforms
 * 
 * @author Jonathon Zeitler
 */
public class OscillatorFactory {
    /**
     * Default constructor, no initialization currently
     */
    OscillatorFactory() {
    }
    
    /**
     * Returns an Oscillator with the given type of waveform (or sine if no
     * match to type identifier string) and starting frequency, with default
     * amplitude of 1.0
     * 
     * @param type  identifier string for type of waveform
     * @param freq  starting frequency
     * @return Oscillator fitting the providing specifications
     */
    public Oscillator getOscillator(String type, double freq) {
        return getOscillator(type, freq, 1.0);
    }
    
    /**
     * Returns an Oscillator with the given type of waveform (or sine if no
     * match to type identifier string), starting frequency, and starting
     * amplitude
     * 
     * @param type  identifier string for type of waveform
     * @param freq  starting frequency
     * @param amp   starting amplitude
     * @return Oscillator fitting the providing specifications
     */
    public Oscillator getOscillator(String type, double freq, double amp) {
        type = type.toUpperCase();
        switch (type) {
            // Oscillator with square waveform
            case "SQUARE" :
                return new Oscillator(freq, amp) {
                    // anonymous class initialization
                    {
                        setCacheSize(10000);
                    }
                    // square wave function
                    @Override
                    protected double waveFunction() {
                        double samp = 0.0;
                        for (int i=1; i<=41; i+=2) {
                            samp += 
                                  Math.sin((double)i*phase*2.0*Math.PI)
                                  /(double)i;
                        }
                        return samp;
                        
                        /*if (phase < 0.5) {
                            return 1.0;
                        } else {
                            return -1.0;
                        }*/
                    }
                    
                };
            
            // Oscillator with triangle waveform
            case "TRIANGLE" :
                return new Oscillator(freq,amp) {
                    // anonymous class initialization
                    {
                        setCacheSize(4);
                    }
                    // triangle wave function
                    @Override
                    protected double waveFunction() {
                        if (phase <= 0.25) {
                            return phase*4;
                        } else if (phase <= 0.75) {
                            return (phase - 0.25)*-4 + 1.0;
                        } else {
                            return 4*(phase -0.75) - 1.0;
                        }
                    }
                };
            
            // Oscillator with sawtooth waveform
            case "SAWTOOTH" :
                return new Oscillator(freq,amp) {
                    // anonymous class initialization
                    {
                        setCacheSize(10000);
                    }
                    // sawtooth wave function
                    @Override
                    protected double waveFunction() {
                        double samp = 0.5;
                        for (int i=1; i<=41; i++) {
                            double sign;
                            if (i%2 == 0) { sign = 1.0; } else { sign = -1.0; }
                            samp -= 
                                  sign*Math.sin((double)i*phase*2.0*Math.PI)
                                  /(double)i;
                        }
                        return samp;
                        
                        /*if (phase <= 0.5) {
                            return phase*2;
                        } else {
                            return phase*2 - 2.0;
                        }*/
                    }
                    
                };
            
            // Oscillator with sine waveform (default if no type match)
            case "SINE" :
            default :
                return new Oscillator(freq, amp) {
                    // anonymous class initialization
                    {
                        setCacheSize(10000);
                    }
                    // sine wave function
                    @Override
                    protected double waveFunction() {
                        return Math.sin(phase*2.0*Math.PI);
                    }
                };
        }
    }
}
