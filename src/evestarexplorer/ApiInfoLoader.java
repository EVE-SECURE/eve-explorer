/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer;

import evestarexplorer.api.*;

/**
 *
 * @author gyv
 */
public class ApiInfoLoader {
    
    public Sovereignty sovereignty = new Sovereignty();
    public AllianceList alliances = new AllianceList();
    public ConquerableList conquerables = new ConquerableList();
    public KillsInfo kills = new KillsInfo();
    
    private ApiInfoLoader() {
    }
    
    public static ApiInfoLoader getInstance() {
        return ApiInfoLoaderHolder.INSTANCE;
    }
    
    private static class ApiInfoLoaderHolder {

        private static final ApiInfoLoader INSTANCE = new ApiInfoLoader();
    }
}
