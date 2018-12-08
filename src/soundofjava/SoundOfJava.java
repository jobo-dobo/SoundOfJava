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
        Oscillator sineOscillator5 = of.getOscillator("TRIANGLE",Hertz,80);
        Oscillator sineOscillator6 = of.getOscillator("SAWTOOTH",Hertz2,0);
        Oscillator sineOscillator7 = of.getOscillator("SQUARE",Hertz3,0);
        /*Oscillator freqOscillator = new Oscillator(5,5);
        Oscillator ampOscillator = new Oscillator (3,5);
        freqOscillator.connectTo(sineOscillator,
                freqOscillator.getOutputPort(),
                sineOscillator.getFrequencyPort());
        ampOscillator.connectTo(sineOscillator,
                ampOscillator.getOutputPort(),
                sineOscillator.getAmplitudePort());*/
        
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
            sineOscillator5.setFrequency(Hertz);
            sineOscillator6.setFrequency(Hertz2);
            sineOscillator7.setFrequency(Hertz3);
            mixer.generate();
            if (i==rate*5) {
                sineOscillator5.setAmplitude(0);
                sineOscillator6.setAmplitude(80);
            } else if (i==rate*10) {
                sineOscillator6.setAmplitude(0);
                sineOscillator7.setAmplitude(80);
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
