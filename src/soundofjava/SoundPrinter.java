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
 * Implements basic functions of queuing output and writing to system audio
 * 
 * @author Jonathon Zeitler
 */
public class SoundPrinter {
    
    // Default sample rate
    public static final float DEFAULT_RATE = 44100;
    
    private static final int BUFFER_SIZE = 1024;
    private AudioFormat audioF;
    private SourceDataLine dataLine;
    private float sampleRate;
    private int sampleBytes;
    private final int MAX_VALUE;
    private byte[] inBuffer;
    private int inPos;
    private byte[] outBuffer;
    private boolean buffOverride;
    private Thread outThread;
    private LinkedBlockingQueue<byte[]> sharedQueue;
    private boolean stopFlag;
    
    /**
     * Default constructor
     */
    public SoundPrinter() {
        this(DEFAULT_RATE,16);
    }
    
    /**
     * Constructor that accepts a sampling rate and the bits per sample
     *
     * @param rate  sample rate in samples per second
     * @param bits  number of bits per sample, 8 and 16 are valid values
     */
    public SoundPrinter(float rate, int bits) {
        if (bits<=8) {
            sampleBytes = 1;
        } else {
            sampleBytes = 2;
        }
        MAX_VALUE = (int)Math.pow(2, sampleBytes*8 - 1) - 1;
        sampleRate = rate;
        audioF = new AudioFormat(rate,sampleBytes*8,1,true,false);
        try {
            dataLine = AudioSystem.getSourceDataLine(audioF);
            dataLine.open(audioF);
        } catch(LineUnavailableException lue) {
            System.out.println(lue);
        }
        buffOverride = false;
        sharedQueue = new LinkedBlockingQueue<>();
        inBuffer = new byte[BUFFER_SIZE];
        inPos = 0;
        stopFlag = true;
    }
    
    /**
     * Adds a specified array of samples to output queue
     *
     * @param samples   array containing samples to add
     * @param len       length of the array to add
     */
    public void put(double[] samples, int len)
    {
        int length = len * sampleBytes;
        int putPos = 0;
        int toCopy;
        byte[] bSamples = new byte[length];
        
        // convert to byte values into bsamples array
        for (int i=0; i<len; i++) {
                System.arraycopy(
                        sampleConvert(samples[i]),0,
                        bSamples,i*2,sampleBytes
                );
        }
        
        // put byte samples into queue of buffers
        while (putPos<length) {
            if (inPos + length - putPos > BUFFER_SIZE) {
                toCopy = BUFFER_SIZE - inPos;
            } else {
                toCopy = length - putPos;
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
    
    /**
     * Adds a single double sample to the output queue
     * 
     * @param sample    sample to add to the output queue
     */
    public void put(double sample) {
        double[] buf = new double[1];
        buf[0] = sample;
        put(buf,1);
    }
    
    /**
     * Puts whatever is in the buffer into output queue
     */
    public void putEnd()
    {
        // Do nothing if there's nothing to add
        if (inPos == 0) return;
        sharedQueue.add(inBuffer);
        inBuffer = new byte[BUFFER_SIZE];
        inPos = 0;
    }
    
    /**
     * Plays output queue until no more buffers are in the queue or the stop
     * state is triggered
     */
    public void play() {
        // Return if already playing
        if (stopFlag == false || outThread != null && outThread.isAlive()) return;
        // Otherwise create writer and kick off thread
        stopFlag = false;
        outThread = new Thread(new Writer(this));
        outThread.start();
    }
    
    /**
     * Stops all output by setting flag
     */
    public void stop() {
        stopFlag = true;
    }
    
    /**
     * Provides stop state
     * 
     * @return true if SoundPrinter is in the stop state
     */
    public boolean isStopped() {
        return stopFlag;
    }
    
    /**
     * Closes the data line
     */
    public void close() {
        dataLine.close();
    }
    
    /**
     * Nested class used for thread that writes to audio output
     */
    private class Writer implements Runnable {
        private final LinkedBlockingQueue<byte[]> queue;
        private final SourceDataLine sourceDataLine;
        private final SoundPrinter sPrinter;
        private byte[] outBuff;
        
        /**
         * Constructor which takes the SoundPrinter
         * 
         * @param sp    the SoundPrinter writing to output
         */
        public Writer(SoundPrinter sp) {
            queue = sp.sharedQueue;
            sourceDataLine = sp.dataLine;
            sPrinter = sp;
        }
        
        @Override
        public void run() {
            sourceDataLine.start();
            // While there is more to read and stopFlag is false
            while (!sPrinter.isStopped() && queue.peek() != null) {
                // Take top buffer from output queue
                outBuff = queue.remove();
                
                // Output one sample at a time from buffer until end of 
                // current buffer is reached or stop state is triggered
                /*for (int opos = 0; opos<BUFFER_SIZE && !sPrinter.isStopped();
                        opos+=sampleBytes) {
                    sourceDataLine.write(outBuff, opos, sampleBytes);
                }*/
                
                sourceDataLine.write(outBuff, 0, BUFFER_SIZE);
            }
            // Stop state triggered or buffer queue emptied
            sourceDataLine.drain();
            sourceDataLine.stop();
            sPrinter.stop();
        }
    }
    
    /**
     * Converts a double sample to byte(s) for output buffer
     * 
     * @param sample    sample to convert
     * @return          sample as it should be written to buffer
     */
    private byte[] sampleConvert(double sample) {
        byte[] result = new byte[sampleBytes];
        int value = (int)Math.round((double)MAX_VALUE*(sample));
        for (int i=0; i<sampleBytes; i++) {
            result[i] = (byte)(value & 0xFF);
            value = value >> 8;
        }
         
        return result;
    }
}

