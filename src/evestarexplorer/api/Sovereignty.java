/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer.api;

import com.beimin.eveapi.core.ApiException;
import com.beimin.eveapi.map.sovereignty.ApiSystemSovereignty;
import com.beimin.eveapi.map.sovereignty.SovereigntyParser;
import com.beimin.eveapi.map.sovereignty.SovereigntyResponse;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author gyv
 */
public class Sovereignty {
    
    private SovereigntyParser parser = SovereigntyParser.getInstance();
    private Map<Integer, ApiSystemSovereignty> info = null;
    private Date timestamp = null;
    
    public boolean isUpdated() { return info != null; }
    public Date updatedAt() { return timestamp; }
    
    public Map<Integer, ApiSystemSovereignty> update() throws ApiException {

        SovereigntyResponse response = parser.getResponse();
        assert response != null;
        info = response.getSystemSovereignties();
        timestamp = new Date();
        return info;
    }
    
    public Map<Integer, ApiSystemSovereignty> getInfo() { return info; }
    
}