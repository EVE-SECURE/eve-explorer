/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer.api;

import com.beimin.eveapi.core.ApiException;
import com.beimin.eveapi.map.kills.KillsParser;
import com.beimin.eveapi.map.kills.KillsResponse;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author gyv
 */
public class KillsInfo {
    
    private KillsParser parser = KillsParser.getInstance();
    private Date updated = null;
    private KillsResponse response = null;
    
    public Date updatedAt() { return updated; }
    public boolean isUpdated() { return updated != null; }
    
    public void update() throws ApiException {
        updated = null;
        response = null;
        response = parser.getResponse();
        updated = new Date();
    }
    
    public Map<Integer, Integer> getPodKills() { return response.getPodKills(); }
    public Map<Integer, Integer> getNpcKills() { return response.getFactionKills(); }
    public Map<Integer, Integer> getShipKills() { return response.getShipKills(); }
    
    
    private long getKills(Map<Integer, Integer> kills, long starId) {
        if (isUpdated()) {
            Integer res = kills.get(new Integer((int)starId));
            return res != null ? res : -1;
        }
        return -1;
    }
    
    public long getPodKills(long starId) {
        return getKills(getPodKills(), starId);
    }
    
    public long getNpcKills(long starId) {
        return getKills(getNpcKills(), starId);
    }
    
    public long getShipKills(long starId) {
        return getKills(getShipKills(), starId);
    }
}
