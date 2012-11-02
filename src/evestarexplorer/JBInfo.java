/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author gyv
 */
public class JBInfo {
    
    private final StarInfo fromSystem;
    private final StarInfo toSystem;
    
    private SolarSystemObject fromObj;
    private SolarSystemObject toObj;
    
    private SimpleBooleanProperty isActive;
    private SimpleStringProperty from;
    private SimpleStringProperty to;
    private SimpleStringProperty shortName;
    private SimpleBooleanProperty hasCynogen;
    private SimpleBooleanProperty hasCynojammer;
    
    public BooleanProperty isActiveProperty() { return isActive; }
    public StringProperty fromProperty() { return from; }
    public StringProperty toProperty() { return to; }
    public StringProperty shortNameProperty() { return shortName; }
    public BooleanProperty hasCynogenProperty() { return hasCynogen; }
    public BooleanProperty hasCynojummerProperty() { return hasCynojammer; }
    
    private void updateProps() {
        isActive.set(true);
        from.set((fromObj != null) ? fromObj.name : fromSystem.name);
        to.set(toObj != null ? toObj.name : toSystem.name);
        
        SovInfo sFrom = fromSystem.getSovInfo();
        SovInfo sTo = toSystem.getSovInfo();
        
        String s;
        if (sFrom.isClaimable() && sTo.isClaimable()) {
            if (sFrom.isClaimed() && sTo.isClaimed()) {
                if (sFrom.getOwnerID() == sTo.getOwnerID()) {
                    s = sFrom.getOwnerShort();
                }
                else {
                    // разные альянсы владеют системами
                    s = "-DIFF-";
                }
            }
            else {
                s = sFrom.getOwnerShort() + "--";
            }
        }
        else {
            s = "-N/A-";
        }
        shortName.set(s);
    }

    public JBInfo(StarInfo from, StarInfo to) {
        
        if (from.compareBySov(to) >= 0) {
            fromSystem = from;
            toSystem = to;
        }
        else {
            fromSystem = to;
            toSystem = from;
        }
        
        updateProps();
    }
    
    
}
