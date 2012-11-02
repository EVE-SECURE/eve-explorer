/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer.api;

import com.beimin.eveapi.eve.alliancelist.ApiAlliance;
import evestarexplorer.StarInfoList;
import java.util.Comparator;
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

    private int standing = 0;
    private StarInfoList sovList = new StarInfoList();


    private StringProperty nameProp;
    private StringProperty shortNameProp;
    private LongProperty membersProp;
    private IntegerProperty standProp;
    private IntegerProperty claimedProp;

    private void initProps() {
        nameProp = new SimpleStringProperty(name);
        shortNameProp = new SimpleStringProperty(shortName);
        membersProp = new SimpleLongProperty(memberCount);
        standProp = new SimpleIntegerProperty(standing);
        claimedProp = new SimpleIntegerProperty(sovList.size());
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
    
    public int getStanding() { return standing; }
    public void setStanding(int stand) {
        assert (stand >= -10 && stand <= 10);
        standing = stand;
        standProp.setValue(stand);
    }

    public int getClaimed() { return sovList.size(); }
    public void setSovList(StarInfoList sov) {
        sovList = sov;
        claimedProp.setValue(getClaimed());
    }

    public StringProperty fullNameProperty() { return nameProp; }
    public StringProperty shortNameProperty() { return shortNameProp; }
    public LongProperty membersCountProperty() { return membersProp; }
    public IntegerProperty standingProperty() { return standProp; }
    public IntegerProperty claimedProperty() { return claimedProp; }

    public static class AllianceInfoCompareBySize implements Comparator<AllianceInfo>{

        @Override
        public int compare(AllianceInfo t1, AllianceInfo t2) {
            long m1 = t1.memberCount;
            long m2 = t2.memberCount;
            if (m1 > m2) {
                return -1;
            }
            else if (m1 < m2) {
                return 1;
            }
            else {
                return Long.compare(System.identityHashCode(t1),System.identityHashCode(t2));
            }
        }
    }

    public static class AllianceInfoCompareByStanding implements Comparator<AllianceInfo>{

        @Override
        public int compare(AllianceInfo t1, AllianceInfo t2) {
            long m1 = t1.standing;
            long m2 = t2.standing;
            if (m1 > m2) {
                return -1;
            }
            else if (m1 < m2) {
                return 1;
            }
            else {
                int res = t1.name.compareToIgnoreCase(t2.name);
                return (res != 0) ? res : Long.compare(System.identityHashCode(t1),System.identityHashCode(t2));
            }
        }
    }

}
