/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer;

/**
 *
 * @author gyv
 */
public class SolarSystemObject {

    public enum Type {  NONE, SUN, PLANET, MOON, BELT, GATE, STATION; 
    
        public static Type getType(String s) {
            switch (s) {
                case "Sun": return Type.SUN;
                case "Planet": return Type.PLANET;
                case "Moon": return Type.MOON;
                case "Asteroid Belt": return Type.BELT;
                case "Stargate": return Type.GATE;
                case "Station": return Type.STATION;
            }
            return Type.NONE;
        }
        
    }
    
    public final long systemId;
    public final Type type;
    public final String name;
    public final double x;
    public final double y;
    public final double z;
    
    private double nearestObjectDistance = Double.MAX_VALUE;

    public boolean isWarpSafe() {
        return (nearestObjectDistance < WarpSafety.DSCAN_RANGE);
    }
    
    public double getNearestObjectDistance() { return nearestObjectDistance; }
    public void setNearestObjectDistance(double nearestObjectDistance) {
        this.nearestObjectDistance = nearestObjectDistance;
    }
    
    /**
     * Обновляет дистанцию до ближайшего объекта, если переданный объект ближе, 
     * чем текущая дистанция. Если переданный объект дальше, то дистанция не 
     * обновляется.
     * @param so 
     * объект, дистанция до которого проверяется
     * @return 
     * новая дистанция до объекта
     */
    public double updateNearestDistance(SolarSystemObject so) {
        double dist = this.distance(so);
        nearestObjectDistance = Math.min(nearestObjectDistance, dist);
        return dist;
    }
    
    public double distance(SolarSystemObject so) {
        double dx = this.x - so.x;
        double dy = this.y - so.y;
        double dz = this.z - so.z;
        return Math.sqrt(dx*dx + dy*dy + dz*dz);
    }
    
    public SolarSystemObject(String s) {
        
        String[] data = s.split("\\t");
        assert data.length == 7;
        
        systemId = Long.parseLong(data[0]);
        type = Type.getType(data[2]);
        assert type != Type.NONE;

        name = data[3];
        x = Double.parseDouble(data[4]);
        y = Double.parseDouble(data[5]);
        z = Double.parseDouble(data[6]);
        
    }
    
}
