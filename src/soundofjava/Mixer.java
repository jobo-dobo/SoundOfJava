/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soundofjava;

import java.util.ArrayList;

/**
 *
 * @author Jonathon Zeitler
 */
public class Mixer extends Component {
    ArrayList<ChannelOption> channelOptions;
    
    public class ChannelOption {
        double volume;
    }
    
    @Override
    public double generate() {
        return 0.0;
    }
    
    @Override
    public double generate(int i) {
        return 0.0;
    }
}
