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
    private String ownerShort;
    private boolean claimable;
    private boolean claimed;
    
    
    private static ApiInfoLoader apiInfo = ApiInfoLoader.getInstance();
    
    private void init() {
        factionId = 0;
        allianceId = 0;
        ownerName = "- unknown -";
        ownerShort = "";
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
                    ownerShort = a.shortName;
                }
                else {
                    ownerName = "- unclaimed -";
                    ownerShort = "---";
                }
            }
            else {
                Factions.FactionInfo i = Factions.getInstance().get((int)factionId);
                ownerName = i.name;
                ownerShort = "";
            }
        }
    }

    
    public boolean isClaimable() { return claimable; }
    public boolean isClaimed() { return claimed; }
    public String getOwnerName() { return ownerName; }
    public String getOwnerShort() { return ownerShort; }
    public long getOwnerID() { return allianceId; }
    public long getFactionID() { return factionId; }
    
}
