/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer.api;

import com.beimin.eveapi.core.ApiException;
import com.beimin.eveapi.eve.alliancelist.AllianceListParser;
import com.beimin.eveapi.eve.alliancelist.AllianceListResponse;
import com.beimin.eveapi.eve.alliancelist.ApiAlliance;
import evestarexplorer.SovInfo;
import evestarexplorer.StarInfo;
import evestarexplorer.StarInfoList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.prefs.Preferences;

/**
 *
 * @author gyv
 */
public class AllianceList {
    
    private static AllianceListParser parser = AllianceListParser.getInstance();
    
    private TreeSet<AllianceInfo> list;
    private TreeMap<String, AllianceInfo> index;
    private HashMap<Long, AllianceInfo> indexById;
    private Date timestamp = null;
    
    private final Preferences prefs = Preferences.userNodeForPackage(this.getClass());
    private static final String prefs_key_standing = "standings";
    
    public void updateSovList(Map<String, StarInfo> stars) {
        
        HashMap<Long, StarInfoList> sovList = new HashMap<>();
        
        for (StarInfo si : stars.values()) {
            
            SovInfo sov = si.getSovInfo();
            if (sov.isClaimed()) {
                
                long id = sov.getOwnerID();
                StarInfoList sl = sovList.get(id);

                if (sl == null) {
                    sl = new StarInfoList();
                    sovList.put(id, sl);
                }

                sl.add(si);
            }
        }
        
        for (Long l : sovList.keySet()) {
            
            AllianceInfo ai = indexById.get(l);
            ai.setSovList(sovList.get(l));
            
        }
        
        loadStandingsFromPrefs();
        
    }
    
    public boolean isUpdated() { return list != null; }
    public Date updatedAt() { return timestamp; }
    
    public AllianceInfo get(String tiker) {
        AllianceInfo a = index.get(tiker);
        return (a != null) ? a : new AllianceInfo();
    }
    
    public AllianceInfo get(long id) {
        if (id == 0) { return new AllianceInfo(); }
        AllianceInfo a = indexById.get(id);
        return (a != null) ? a : new AllianceInfo();
    }
    
    public TreeSet<AllianceInfo> update() throws ApiException {

            AllianceListResponse response = parser.getResponse();
            assert response != null;

            timestamp = new Date();

            list = new TreeSet<>(new AllianceInfo.AllianceInfoCompareBySize());
            index = new TreeMap<>();
            indexById = new HashMap<>();
            
            for (ApiAlliance aa : response.getAll()) {
                AllianceInfo ai = new AllianceInfo(aa);
                list.add(ai);
                index.put(ai.shortName, ai);
                indexById.put(ai.id, ai);
            }
            
            return list;
    }
    
    public Set<AllianceInfo> getList() { 
        return (list != null) ? list : new TreeSet<AllianceInfo>(); 
    }
    
    public void persistStandings() {
        
        String value = "";
        
        for (AllianceInfo ai : list) {
            if (ai.getStanding() != 0) {
                value += ai.id + " " + ai.getStanding() + "\n";
            }
        }
        
        prefs.put(prefs_key_standing, value);
        
    }

    private void loadStandingsFromPrefs() {
        
        String value = prefs.get(prefs_key_standing, "");
        
        String[] strings = value.split("\\n+");
        for (String s : strings) {
            String[] data = s.split("\\s+");
            if (data.length == 2) {
                long id;
                int stand;
                try {
                    id = Long.parseLong(data[0]);
                    stand = Integer.parseInt(data[1]);
                    
                    assert id > 0;
                    assert stand >= -10 && stand <= 10;

                    AllianceInfo ai = indexById.get(id);
                    if (ai != null) {
                        ai.setStanding(stand);
                    }
                } catch (NumberFormatException numberFormatException) {
                    // TODO: обработать исключение
                }
            }
        }
    }

}
