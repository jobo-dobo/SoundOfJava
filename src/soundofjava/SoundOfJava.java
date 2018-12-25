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
        float rate = 44100;
        double Hertz = 734.162;
        double Hertz2 = 873.0705;
        double Hertz3 = 1100;
        
        OscillatorFactory of = new OscillatorFactory();
        
        // Create three main tone generators
        Oscillator sineOscillator = of.getOscillator("SAWTOOTH",Hertz/2,0.5);
        Oscillator sineOscillator2 = of.getOscillator("TRIANGLE",Hertz2/2,0.8);
        Oscillator sineOscillator3 = of.getOscillator("SINE",Hertz3/2,0.8);
        
        // Create fm and am oscillators
        Oscillator fmOscillator = new Oscillator(5,5);
        Oscillator fmOscillator2 = new Oscillator(7,7);
        Oscillator amOscillator = new Oscillator (3,0.05);
        
        // Hook up fm and am oscillators
        fmOscillator2.connectTo(sineOscillator,
                fmOscillator2.getOutputPort(),
                sineOscillator.getFMPort());
        fmOscillator.connectTo(sineOscillator3,
                fmOscillator.getOutputPort(),
                sineOscillator3.getFMPort());
        amOscillator.connectTo(sineOscillator2,
                amOscillator.getOutputPort(),
                sineOscillator2.getAMPort());
        
        // set up envelope
        Envelope env = new Envelope();
        Envelope env2 = new Envelope();
        Envelope env3 = new Envelope();
        env.connectTo(sineOscillator,
                env.getOutputPort(),
                sineOscillator.getAmplitudePort());
        env.close();
        env2.connectTo(sineOscillator2,
                env2.getOutputPort(),
                sineOscillator2.getAmplitudePort());
        env2.close();
        env3.connectTo(sineOscillator3,
                env3.getOutputPort(),
                sineOscillator3.getAmplitudePort());
        env3.close();
        
        //int next = (int)(Math.random()*40000);
        
        // Add our three tone generators to a mixer
        Mixer mixer = new Mixer(3);
        mixer.add(sineOscillator);
        mixer.add(sineOscillator2);
        mixer.add(sineOscillator3);
        
        // Start with first channel in solo
        mixer.toggleSolo(0);
        
        for (int i = 0; i<rate*15; i++) {
            
            Hertz -= 0.00086;
            Hertz2 -= 0.00093;
            Hertz3 -= 0.001;
            //sineOscillator.setFrequency(Hertz);
            //sineOscillator2.setFrequency(Hertz2);
            //sineOscillator3.setFrequency(Hertz3);
            
            if (i%(int)rate==(int)(rate*0.7)) {
                env.close();
                env2.close();
                env3.close();
            }
            if (i%(int)rate==0) {
                env.open();
                env2.open();
                env3.open();
            }
            
            // generate next sample
            mixer.generate();
            
            // switch solo channel after 5 seconds and 10 seconds
            if (i==rate*5) {
                mixer.toggleSolo(1);
        mixer.play();
            } else if (i==rate*10) {
                mixer.toggleSolo(2);
            }
            
            //if (i>0 && i==next) {
            //    sineOscillator.setFrequency(Hertz+(Math.random()-0.5)*600);
            //    next += (int)(Math.random()*6000);
            //}
        }
        
        // Mixer plays its generated output
        //mixer.play();

        // To prove the multithreading works!
        System.out.println("Does the multithreading work?");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
        System.out.println("Yes it does!");
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
        System.out.println("STOP");
        
        // Stop Mixer
        mixer.stop();
    }
    
}
