/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soundofjava;

/* Imports */
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.LineUnavailableException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author Jonathon Zeitler
 */
public class SoundPrinter {
    private static final float DEFAULT_RATE = 44100;
    private static final int BUFFER_SIZE = 1024;
    private AudioFormat audioF;
    private SourceDataLine dataLine;
    private float sampleRate;
    private byte[] inBuffer;
    private int inPos;
    private byte[] outBuffer;
    private boolean buffOverride;
    private Thread outThread;
    private LinkedBlockingQueue<byte[]> sharedQueue;
    private boolean stopFlag;
    
    
    public SoundPrinter() {
        this(DEFAULT_RATE);
    }
    
    public SoundPrinter(float rate) {
        sampleRate = rate;
        audioF = new AudioFormat(rate,8,1,true,false);
        try {
            dataLine = AudioSystem.getSourceDataLine(audioF);
            dataLine.open(audioF);
        } catch(LineUnavailableException lue) {
            System.out.println(lue);
        }
        buffOverride = false;
        sharedQueue = new LinkedBlockingQueue<byte[]>();
        inBuffer = new byte[BUFFER_SIZE];
        inPos = 0;
        stopFlag = true;
    }
    
    public void put(double[] samples, int len)
    {
        int putPos = 0;
        int toCopy;
        byte[] bSamples = new byte[len];
        for (int i=0; i<len; i++) bSamples[i] = (byte)samples[i];
        while (putPos<len) {
            if (inPos + len - putPos > BUFFER_SIZE) {
                toCopy = BUFFER_SIZE - inPos;
            } else {
                toCopy = len - putPos;
            }
            System.arraycopy(bSamples,putPos,inBuffer,inPos,toCopy);
            inPos = (inPos+toCopy)%BUFFER_SIZE;
            putPos += toCopy;
            if (inPos==0) {
                // Buffer is full, add to queue
                sharedQueue.add(inBuffer);
                inBuffer = new byte[BUFFER_SIZE];
            }
        }
    }
    
    // Puts whatever is in the buffer into output queue
    public void putEnd()
    {
        // Do nothing if there's nothing to add
        if (inPos == 0) return;
        sharedQueue.add(inBuffer);
        inBuffer = new byte[BUFFER_SIZE];
        inPos = 0;
    }
    
    public void play() {
        // Return if already playing
        if (stopFlag = false || outThread != null && !outThread.isAlive()) return;
        // Otherwise create writer and kick off thread
        outThread = new Thread(new Writer(sharedQueue,dataLine, this));
        outThread.start();
    }
    
    public void stop() {
        stopFlag = true;
    }
    
    public boolean isStopped() {
        return stopFlag;
    }
    
    public void close() {
        dataLine.close();
    }
    
    private class Writer implements Runnable {
        private LinkedBlockingQueue<byte[]> queue;
        private SourceDataLine sourceDataLine;
        private SoundPrinter sPrinter;
        private byte[] outBuff;
        
        public Writer(LinkedBlockingQueue<byte[]> lbq, SourceDataLine sdl, SoundPrinter sp) {
            queue = lbq;
            sourceDataLine = sdl;
            sPrinter = sp;
        }
        
        @Override
        public void run() {
            sourceDataLine.start();
            // While there is more to read and stopFlag is false
            while (!sPrinter.isStopped() && queue.peek() != null) {
                outBuff = queue.remove();
                for (int opos = 0; opos<BUFFER_SIZE && !sPrinter.isStopped(); opos++) {
                    sourceDataLine.write(outBuff, opos, 1);
                }
            }
            sourceDataLine.drain();
            sourceDataLine.stop();
            sPrinter.stop();
        }
    }
}

