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
                        if (phase < 0.5) {
                            return 1.0;
                        } else {
                            return -1.0;
                        }
                        
                        /*if (phase < 0.05) {
                            return 20*phase;
                        } else if (phase < 0.45) {
                            return 1.0;
                        } else if (phase < 0.55) {
                            return 20*(phase-0.45) - 1.0;
                        } else if (phase < 0.95) {
                            return -1.0;
                        } else {
                            return 20*(1.0-phase);
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
                        if (phase <= 0.5) {
                            return phase*2;
                        } else {
                            return phase*2 - 2.0;
                        }
                        
                        /*if (phase < 0.45) {
                            return phase*1.8;
                        } else if (phase < 0.55) {
                            return (phase-0.45)*-200 + 1.0;
                        } else {
                            return phase*1.8 - 1.8;
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
