/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soundofjava;

import java.util.ArrayList;

/**
 *
 * @author Jonathon Z
 */
public class Envelope extends Component {
    protected boolean isOpen;
    private EnvelopeRamp[] ramps;
    private Env envSection;
    private int outputPort;
    
    public Envelope() {
        this(0.02,0.1,0.3);
    }
    
    public Envelope(double attackDuration, double decayDuration,
            double releaseDuration) {
        this(attackDuration, 1.5, decayDuration, 0.8, releaseDuration);
    }
    
    public Envelope(double attackDuration, double attackAmp,
            double decayDuration, double decayAmp,
            double releaseDuration) {
        super();
        isOpen = false;
        ramps = new EnvelopeRamp[4];
        ramps[0] = getRamp(EnvType.LINEAR, attackDuration, 0.0, attackAmp);
        ramps[1] = getRamp(EnvType.LINEAR, decayDuration, attackAmp, decayAmp);
        ramps[2] = getRamp(EnvType.LINEAR, 0.1, decayAmp, decayAmp);
        ramps[3] = getRamp(EnvType.LINEAR, releaseDuration, decayAmp, 0.0);
        envSection = Env.ATTACK;
        outputPort = addOutputPort("Primary");
        outputSamples.set(0, 0.0);
    }
    
    public boolean isOpen() { return isOpen; }
    public void open() { isOpen = true; }
    public void close() { isOpen = false; }
    public int getOutputPort() { return outputPort; }
    
    @Override
    public double generate(int port) {
        return generate();
    }
    
    @Override
    public double generate() {
        double samp = outputSamples.get(0);
        if (isOpen) {
            switch (envSection) {
                case ATTACK :
                    //System.out.print("(Attack)");
                    if (ramps[0].isComplete()) {
                        ramps[1].setAmp(samp);
                        samp = ramps[1].generate();
                        envSection = Env.DECAY;
                        ramps[0].reset();
                    } else {
                        samp = ramps[0].generate();
                    }
                    break;
                case DECAY :
                    //System.out.print("(Decay)");
                    if (ramps[1].isComplete()) {
                        ramps[2].setAmp(samp);
                        samp = ramps[2].generate();
                        envSection = Env.SUSTAIN;
                        ramps[1].reset();
                    } else {
                        samp = ramps[1].generate();
                    }
                    break;
                case SUSTAIN :
                    //System.out.print("(Sustain)");
                    samp = ramps[2].generate();
                    break;
                case RELEASE :
                    //System.out.print("(Release)");
                    ramps[0].setAmp(samp);
                    samp = ramps[0].generate();
                    envSection = Env.ATTACK;
                    ramps[3].reset();
                    break;
            }
        } else {
            //System.out.print("(Release)");
            switch (envSection) {
                case RELEASE :
                    samp = ramps[3].generate();
                    break;
                default :
                    ramps[3].setAmp(samp);
                    samp = ramps[3].generate();
                    envSection = Env.RELEASE;
                    ramps[0].reset();
                    ramps[1].reset();
                    ramps[2].reset();
            }
        }
        outputSamples.set(0, samp);
        
        return samp;
    }
    
    /**
     * Encapsulates a section of an envelope
     */
    protected abstract class EnvelopeRamp {
        public double duration;
        public double position;
        public double startAmp;
        public double endAmp;
        public EnvType type;
        
        EnvelopeRamp(double dur, double start, double end) {
            duration = dur;
            position = 0;
            startAmp = start;
            endAmp = end;
        }
        
        abstract public double generate();
        abstract public boolean isComplete();
        abstract void setAmp(double amp);
        public void reset() { position = 0.0; }
    }
    
    public final EnvelopeRamp getRamp(EnvType type,
            double dur, double start, double end) {
        switch (type) {
            case LINEAR :
            default :
                return new EnvelopeRamp(dur, start, end) {
                    @Override
                    public double generate() {
                        double samp;
                        if (isComplete()) {
                            samp = end;
                        } else {
                            samp = position*(end - start) + start;
                        }
                        position += (1.0/(duration*rate));
                        //System.out.println(position + ": " + samp);
                        return samp;
                    }
                    
                    @Override
                    public boolean isComplete() {
                        return (position >= 1.0);
                    }
                    
                    @Override
                    public void setAmp(double amp) {
                        if (end - start == 0) {
                            position = 0.0;
                        } else {
                            position = (amp - start)/(end - start);
                        }
                    }
                };

        }
    }
    
    /**
     * Enum to keep track of which envelope is in progress
     */
    public enum Env {
        ATTACK, DECAY, SUSTAIN, RELEASE
    }
    
    /**
     * Enum to specify ramp functions
     */
    public enum EnvType {
        LINEAR
    }
}
