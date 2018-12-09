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
        SoundPrinter soundPrinter2 = new SoundPrinter();
        SoundPrinter soundPrinter3 = new SoundPrinter();
        SoundPrinter soundPrinter4 = new SoundPrinter();
        SoundPrinter soundPrinter5 = new SoundPrinter();
        SoundPrinter soundPrinter6 = new SoundPrinter();
        SoundPrinter soundPrinter7 = new SoundPrinter();
        float rate = 44100;
        double Hertz = 734.162;
        double Hertz2 = 873.0705;
        double Hertz3 = 1100;
        
        OscillatorFactory of = new OscillatorFactory();
        
        SquareOscillator sineOscillator = new SquareOscillator(Hertz,10);
        Oscillator sineOscillator2 = new Oscillator(150,10);
        SquareOscillator sineOscillator3 = new SquareOscillator(330,10);
        SquareOscillator sineOscillator4 = new SquareOscillator(223,10);
        Oscillator sineOscillator5 = of.getOscillator("SAWTOOTH",Hertz/2,0.5);
        Oscillator sineOscillator6 = of.getOscillator("TRIANGLE",Hertz2/2,0.8);
        Oscillator sineOscillator7 = of.getOscillator("SINE",Hertz3/2,0.8);
        Oscillator fmOscillator = new Oscillator(5,5);
        Oscillator fmOscillator2 = new Oscillator(7,7);
        Oscillator amOscillator = new Oscillator (3,0.05);
        fmOscillator2.connectTo(sineOscillator5,
                fmOscillator2.getOutputPort(),
                sineOscillator5.getFMPort());
        fmOscillator.connectTo(sineOscillator7,
                fmOscillator.getOutputPort(),
                sineOscillator7.getFMPort());
        amOscillator.connectTo(sineOscillator6,
                amOscillator.getOutputPort(),
                sineOscillator6.getAMPort());
        
        int next = (int)(Math.random()*40000);
        
        
                //soundPrinter3.play();
                //soundPrinter4.play();
                /*soundPrinter5.play();
                soundPrinter6.play();
                soundPrinter7.play();
        soundPrinter.putEnd();
        soundPrinter2.putEnd();*/
                
        Mixer mixer = new Mixer(3);
        mixer.add(sineOscillator5);
        mixer.add(sineOscillator6);
        mixer.add(sineOscillator7);
        mixer.toggleSolo(0);
        
        for (int i = 0; i<rate*15; i++) {
            //sineOscillator.setFrequency(Hertz+freqOscillator.generate());
            /*soundPrinter.put(sineOscillator.generate());
            soundPrinter2.put(sineOscillator2.generate());
            soundPrinter3.put(sineOscillator3.generate());
            soundPrinter4.put(sineOscillator4.generate());
            soundPrinter5.put(sineOscillator5.generate());
            soundPrinter6.put(sineOscillator6.generate());
            soundPrinter7.put(sineOscillator7.generate());*/
            
            Hertz -= 0.00086;
            Hertz2 -= 0.00093;
            Hertz3 -= 0.001;
            //sineOscillator5.setFrequency(Hertz);
            //sineOscillator6.setFrequency(Hertz2);
            //sineOscillator7.setFrequency(Hertz3);
            mixer.generate();
            if (i==rate*5) {
                mixer.toggleSolo(1);
            } else if (i==rate*10) {
                mixer.toggleSolo(2);
            }
            
            //if (i>0 && i==next) {
            //    sineOscillator.setFrequency(Hertz+(Math.random()-0.5)*600);
            //    next += (int)(Math.random()*6000);
            //}
            
            /*if (i==2048) {
                //soundPrinter.play();
            }
            
            if (i==2034) {
                //soundPrinter2.play();
            }*/
        }
        mixer.play();

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
        mixer.stop();
        //soundPrinter.stop();
    }
    
}
