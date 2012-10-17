/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer.api;

import com.beimin.eveapi.core.ApiException;
import com.beimin.eveapi.eve.alliancelist.AllianceListParser;
import com.beimin.eveapi.eve.alliancelist.AllianceListResponse;
import com.beimin.eveapi.eve.alliancelist.ApiAlliance;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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

}
