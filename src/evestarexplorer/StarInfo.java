/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer;

/**
 *
 * @author g_yaltchik
 */
final public class StarInfo {
    Integer id;
    
    public String name;
    public String constellation;
    public String region;
    
    public Double ss;
    
    public Double x;
    public Double y;
    public Double z;
    
    boolean isSeenFlag = false;
    long currentDistance = Long.MAX_VALUE;
    
    StarInfoList gates = new StarInfoList();

    private SovInfo sov;
    
    void updateSovInfo(SovInfo sov) { this.sov = sov; }
    final SovInfo getSovInfo() {return sov;}
    
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

    double distanceTo(StarInfo si) {
        double dx = x - si.x;
        double dy = y - si.y;
        double dz = z - si.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
}
