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
public class SquareOscillator extends Oscillator{
    SquareOscillator(double freq) {
        super(freq);
    }
    
    SquareOscillator(double freq, double amp)  {
        super(freq, amp);
        System.out.println(outputPorts);
    }
    
    @Override
    protected double waveFunction() {
        if (phase < 0.5) {
            return 1.0;
        } else {
            return -1.0;
        }
    }
}
