/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer.api;

import com.beimin.eveapi.eve.alliancelist.ApiAlliance;
import java.util.Date;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author g_yaltchik
 */
public class AllianceInfo {
    
    public long id = 0;
    public long executorCorpID = 0;
    public long memberCount = 0;
    public String name = "??? Unknown";
    public String shortName = "???";
    public Date startDate = new Date();

    public int standing = 0;

    private StringProperty nameProp;
    private StringProperty shortNameProp;
    private LongProperty membersProp;
    private IntegerProperty standProp;

    private void initProps() {
        nameProp = new SimpleStringProperty(name);
        shortNameProp = new SimpleStringProperty(shortName);
        membersProp = new SimpleLongProperty(memberCount);
        standProp = new SimpleIntegerProperty(standing);
    }
        
    AllianceInfo(ApiAlliance api) {
        id = api.getAllianceID();
        executorCorpID = api.getExecutorCorpID();
        memberCount = api.getMemberCount();
        name = api.getName();
        shortName = api.getShortName();
        startDate = api.getStartDate();
        
        initProps();
    }

    AllianceInfo() {
        initProps();
    }

    public StringProperty fullNameProperty() { return nameProp; }
    public StringProperty shortNameProperty() { return shortNameProp; }
    public LongProperty membersCountProperty() { return membersProp; }
    public IntegerProperty standingProperty() { return standProp; }
}
