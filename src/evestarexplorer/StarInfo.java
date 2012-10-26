/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author g_yaltchik
 */
final public class StarInfo {
    
    final long id;
    
    public final String name;
    public final String constellation;
    public final String region;
    
    public final Double ss;
    
    public final Double x;
    public final Double y;
    public final Double z;
    
    final private StarInfoList gates = new StarInfoList();

    final private List<StarSystemObject> starObjects = new ArrayList<>();

    
    boolean isSeenFlag = false;
    long currentDistance = Long.MAX_VALUE;

    private SovInfo sov;
    
    void updateSovInfo(SovInfo sov) { this.sov = sov; }
    final public SovInfo getSovInfo() {return sov;}
    
    StarInfo(String s) {
        String[] data;
        data = s.split("\\t");
        id = Integer.parseInt(data[0]);
        name = data[1];
        constellation = data[2];
        region = data[3];
        ss = Double.parseDouble(data[4]);
        x = Double.parseDouble(data[5]);
        y = -Double.parseDouble(data[7]);
        z = Double.parseDouble(data[6]);
        
        sov = new SovInfo();
    }
    
    public List<StarSystemObject> getStarObjects() { return starObjects; }
    public void addStarObject(StarSystemObject so) { 
        assert id == so.systemId; 
        starObjects.add(so); 
    }
    
    public StarInfoList getGates() { return gates; }
    public void addGate(StarInfo si) { gates.add(si); }

    double distanceTo(StarInfo si) {
        double dx = x - si.x;
        double dy = y - si.y;
        double dz = z - si.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
}
