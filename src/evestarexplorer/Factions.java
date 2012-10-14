/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer;

import evestarexplorer.Factions.FactionInfo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gyv
 */
public class Factions extends HashMap<Integer, FactionInfo>{

    public class FactionInfo {
        final Integer id;
        final String name;
        final Integer corpId;
        
        FactionInfo(String s) {
            String[] data;
            data = s.split("\\t");
        
            id = Integer.parseInt(data[0]);
            name = data[1];
            corpId = Integer.parseInt(data[2]);
        }
    }
    
    private void addFaction(String s) {
        FactionInfo fi = new FactionInfo(s);
        this.put(fi.id, fi);
    }
    
    private Factions() {
        String line;
        InputStream s = EveStarExplorer.class.getResourceAsStream("data/factions.txt"); 
        BufferedReader br = new BufferedReader( new InputStreamReader(s) );
        
        try {
            while ((line = br.readLine()) != null) {
                addFaction(line);
            }
        }
        catch (IOException ex) {
            Logger.getLogger(EveStarExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }
}
    
    public static Factions getInstance() {
        return FactionsHolder.INSTANCE;
    }
    
    private static class FactionsHolder {
        private static final Factions INSTANCE = new Factions();
    }
    
    static long getAmarrID() { return 500003; }
    static long getCaldariID() { return 500001; }
    static long getGallenteID() { return 500004; }
    static long getMimatarID() { return 500002; }
}
