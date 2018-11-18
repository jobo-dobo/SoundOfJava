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
        int vol = 100;
        double Hertz = 440;
        //double realHertz;
        double[] sample = new double[1];
        
        // Sample
        for(int i=0; i<rate*5; i++){
            if (i%rate<rate/2) {
                Hertz +=.01;
            } else {
                Hertz -=.01;
            }
            //realHertz = 0.5*Hertz;
            double angle = ((i%rate)/rate)*Hertz*2.0*Math.PI;
            //double angle = (i/rate)*Hertz*x*i*Math.PI;

            //double angle2 = 0.5*(i/rate)*2.0*Math.PI;

            sample[0]=(Math.sin(angle)*vol);
            soundPrinter.put(sample, 1);
            if (i==2000) soundPrinter.play();
        }
        soundPrinter.putEnd();
        //soundPrinter.play();
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
        //soundPrinter.stop();
    }
    
}
