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
    
    private long podKills = -1;
    private long shipKills = -1;
    private long npcKills = -1;

    private SovInfo sov;

    public long getPodKills() {
        return podKills;
    }

    public void setPodKills(long podKills) {
        this.podKills = podKills;
    }

    public long getShlipKills() {
        return shipKills;
    }

    public void setShipKills(long shipKills) {
        this.shipKills = shipKills;
    }

    public long getNpcKills() {
        return npcKills;
    }

    public void setNpcKills(long npcKills) {
        this.npcKills = npcKills;
    }
    
    public void setKillsInfo(long shipKills, long podKills, long npcKills) {
        this.shipKills = shipKills;
        this.podKills = podKills;
        this.npcKills = npcKills;
    }
    
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
        assert id == so.getSystemId(); 
        
        if (so.getType() == SolarSystemObject.Type.STATION) {
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
        return Math.sqrt(dx * dx + dy * dy + dz * dz) * 1e14;
    }

    public SolarSystemObject findGate(String dest) {
        
        // TODO: возможно нужна оптиизация поиска гейта
        String rS = "(?i).*\\(" + Pattern.quote(dest) + "\\).*";
        Pattern p = Pattern.compile(rS);
        
        for (SolarSystemObject so : starObjects) {
            if (so.getType() == SolarSystemObject.Type.GATE) {
                if (p.matcher(so.getName()).matches()) {
                    return so;
                }
            }
        }
        return null;
        
    }
    
    /**
     *
     * @param object объект, наличие которого проверяется в солнечной системе
     * если объект не принадлежит этой солнечной системе или не определен, то возвращается
     * объект SUN текущей системы (считается, что оно есть всегда)
     * @return object или Sun
     */
    public SolarSystemObject findSolarObj(SolarSystemObject object) {
        
        if (object == null || this.id != object.getSystemId()) {
            for (SolarSystemObject so : this.starObjects) {
                if (so.getType() == SolarSystemObject.Type.SUN) {
                    return so;
                }
            }
            throw new AssertionError("System without sun:" + this.id);
        }
        else {
            return object;
        }
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
