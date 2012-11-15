/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
    
    boolean isSeenFlag = false;
    long currentDistance = Long.MAX_VALUE;

    final private StarInfoList neigbors = new StarInfoList();
    final private List<SolarSystemObject> starObjects = new ArrayList<>();
    private boolean _hasStation = false;

    private SovInfo sov;
    
    public boolean hasStation() {
        return _hasStation;
    }
    void updateSovInfo(SovInfo sov) { this.sov = sov; }
    
    /**
     * Возвращает информацию о суверенитете текущей системы.
     * Если суверенитет не загружен, то вернет объект по умолчанию.
     * @return
     */
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
    
    public List<SolarSystemObject> getStarObjects() { return starObjects; }
    public void addSolarObject(SolarSystemObject so) { 
        assert id == so.systemId; 
        
        if (so.type == SolarSystemObject.Type.STATION) {
            _hasStation = true;
        }
        
        double dist = Double.MAX_VALUE;
        for (SolarSystemObject i : starObjects) {
            dist = Math.min(dist, i.updateNearestDistance(so));
        }
        so.setNearestObjectDistance(dist);
        starObjects.add(so);
    }
    
    public StarInfoList getNeigbors() { return neigbors; }
    public void addNeigbor(StarInfo si) { neigbors.add(si); }

    double distanceTo(StarInfo si) {
        double dx = x - si.x;
        double dy = y - si.y;
        double dz = z - si.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public SolarSystemObject findGate(String dest) {
        
        // TODO: возможно нужна оптиизация поиска гейта
        String rS = "(?i).*\\(" + Pattern.quote(dest) + "\\).*";
        Pattern p = Pattern.compile(rS);
        
        for (SolarSystemObject so : starObjects) {
            if (so.type == SolarSystemObject.Type.GATE) {
                if (p.matcher(so.name).matches()) {
                    return so;
                }
            }
        }
        return null;
        
    }
    
    /**
     * Система с клаймом всегда "больше" системы без клайма
     * Если обе системы с клаймом, или обе без, то сравниваются их id
     * @param s1
     * @param s2
     * @return
     */
    public int compareBySov(StarInfo si) {

        if (si.getSovInfo().isClaimed() && this.getSovInfo().isClaimed()) { return Long.compare(this.id, si.id); }
        if (this.getSovInfo().isClaimed() && !si.getSovInfo().isClaimed()) { return 1; }
        if (si.getSovInfo().isClaimed() && !this.getSovInfo().isClaimed()) { return -1; }
        return Long.compare(this.id, si.id);

    }
    
}
