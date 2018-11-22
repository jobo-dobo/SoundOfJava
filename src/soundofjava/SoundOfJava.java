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
public class SoundOfJava {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SoundPrinter soundPrinter = new SoundPrinter();
        float rate = 44100;
        double Hertz = 440;
        
        Oscillator sineOscillator = new Oscillator(Hertz);
        Oscillator freqOscillator = new Oscillator(5,10);
        
        for (int i = 0; i<rate*5; i++) {
            sineOscillator.setFrequency(Hertz+freqOscillator.generate());
            soundPrinter.put(sineOscillator.generate());
        }
        
        soundPrinter.putEnd();
        soundPrinter.play();

        // To prove the multithreading works!
        System.out.println("Does the multithreading work?");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
        System.out.println("Yes it does!");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
        System.out.println("STOP");
        soundPrinter.stop();
    }
    
}
