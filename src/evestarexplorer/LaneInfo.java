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
    String gate1;
    String gate2;
    String id;
    
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
    }

}
