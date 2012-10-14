/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer;

import com.beimin.eveapi.map.sovereignty.ApiSystemSovereignty;
import evestarexplorer.api.AllianceInfo;

/**
 *
 * @author gyv
 */
public class SovInfo {
    
    private long factionId;
    private long allianceId;
    private String ownerName;
    private String ownerId;
    private boolean claimable;
    private boolean claimed;
    
    
    private static ApiInfoLoader apiInfo = ApiInfoLoader.getInstance();
    
    private void init() {
        factionId = 0;
        allianceId = 0;
        ownerName = "- unknown -";
        ownerId = "";
        claimable = false;
        claimed = false;
    }
    
    
    SovInfo() { init(); }
    
    SovInfo(ApiSystemSovereignty sov) {
        
        if (sov == null) { init(); }
        else {
            factionId = sov.getFactionID();
            allianceId = sov.getAllianceID();
            claimable = (factionId == 0);
            claimed = (allianceId != 0);
            
            if (claimable) {
                if (claimed) {
                    AllianceInfo a = apiInfo.alliances.get(allianceId);
                    ownerName = a.name;
                    ownerId = a.shortName;
                }
                else {
                    ownerName = "- unclaimed -";
                    ownerId = "---";
                }
            }
            else {
                Factions.FactionInfo i = Factions.getInstance().get((int)factionId);
                ownerName = i.name;
                ownerId = "";
            }
        }
    }

    
    boolean isClaimable() { return claimable; }
    boolean isClaimed() { return claimed; }
    String getOwnerName() { return ownerName; }
    String getOwnerID() { return ownerId; }
    long getFactionID() { return factionId; }
    
}
