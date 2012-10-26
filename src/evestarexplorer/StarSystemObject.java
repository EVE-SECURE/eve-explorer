/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer;

/**
 *
 * @author gyv
 */
public class StarSystemObject {

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

    public StarSystemObject(String s) {
        
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
