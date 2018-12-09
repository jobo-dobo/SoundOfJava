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
public class OscillatorFactory {
    OscillatorFactory() {
        
    }
    
    public Oscillator getOscillator(String type, double freq) {
        return getOscillator(type, freq, 100.0);
    }
    
    public Oscillator getOscillator(String type, double freq, double amp) {
        type = type.toUpperCase();
        switch (type) {
            case "SQUARE" :
                return new Oscillator(freq, amp) {
                    @Override
                    protected double waveFunction() {
                        double samp = 0.0;
                        for (int i=1; i<=31; i+=2) {
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
                
            case "TRIANGLE" :
                return new Oscillator(freq,amp) {
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
                
            case "SAWTOOTH" :
                return new Oscillator(freq,amp) {
                    @Override
                    protected double waveFunction() {
                        double samp = 0.5;
                        for (int i=1; i<=31; i++) {
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
                
            case "SINE" :
            default :
                return new Oscillator(freq, amp) {
                    @Override
                    protected double waveFunction() {
                        return Math.sin(phase*2.0*Math.PI);
                    }
                };
        }
    }
}
