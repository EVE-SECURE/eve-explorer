/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer;

/**
 *
 * @author g_yaltchik
 */
final class LaneInfo {
    
    public enum Type {NORMAL, CONSTELLATION, REGION;}
    
    final String gate1;
    final String gate2;
    
    final String id;
    final Type type;
    
    final SolarSystemObject so1;
    final SolarSystemObject so2;
    
    final StarInfo si1;
    final StarInfo si2;
    
    static String getKey(String gate1, String gate2) {
        
        return ((gate1.compareTo(gate2) > 0) 
                ? gate1 + " " + gate2 
                : gate2 + " " + gate1).toUpperCase();

    }

    LaneInfo(String s) {
        String[] data;
        data = s.split("\\t");
        gate1 = data[0];
        gate2 = data[1];
        id = getKey(gate1, gate2);

        // ищем информацию о системе
        // учитываем, что может быть джамп у Джовов - пропускаем
        si1 = EveStarExplorer.world.findStarInfo(gate1);
        so1 = (si1 != null) ? si1.findGate(gate2) : null;

        si2 = EveStarExplorer.world.findStarInfo(gate2);
        so2 = (si2 != null) ? si2.findGate(gate1) : null;

        if (si1 != null && si2 != null) {
            if (!si1.region.equals(si2.region)) { type = Type.REGION; }
            else if (!si1.constellation.equals(si2.constellation)) { type = Type.CONSTELLATION; }
            else { type = Type.NORMAL; }
        }
        else {
            type = Type.NORMAL;
        }
    }

}
