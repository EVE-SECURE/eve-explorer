/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer.api;

import com.beimin.eveapi.core.ApiException;
import com.beimin.eveapi.eve.conquerablestationlist.ApiStation;
import com.beimin.eveapi.eve.conquerablestationlist.ConquerableStationListParser;
import com.beimin.eveapi.eve.conquerablestationlist.StationListResponse;
import evestarexplorer.ConquerableObject;
import evestarexplorer.EveStarExplorer;
import evestarexplorer.SolarSystemObject;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author gyv
 */
public class ConquerableList {

    private static ConquerableStationListParser parser = ConquerableStationListParser.getInstance();
    private Date timestamp = null;
    private Map<Long, SolarSystemObject> list = null;
    
    public void update() throws ApiException {
        
        StationListResponse response = parser.getResponse();
        assert response != null;
        
        timestamp = new Date();
        list = new HashMap<>();
        
        Map<Long, SolarSystemObject> objects = EveStarExplorer.world.solarObjIndex;
        Map<Integer, ApiStation> st = response.getStations();
        
        for (ApiStation s : st.values()) {
            long id = s.getStationID();
            
            if (!objects.containsKey(id)) {
                ConquerableObject obj;
                try {
                    obj = new ConquerableObject(s);
                } catch (Exception ex) {
                    continue;
                }
                assert id == obj.getId() : id + " == " + obj.getId();
                list.put(id, obj);
            }
            else {
            }
            
        }
    }
    
    public Date updatedAt() { return timestamp; }
    public boolean isUpdated() { return list != null; }
    public Map<Long,SolarSystemObject> getList() { return list; }

}
