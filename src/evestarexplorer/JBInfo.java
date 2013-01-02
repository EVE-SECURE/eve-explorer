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
    
    public static double JB_REACH = Const.LY * 5;
    
    private StarInfo fromSystem;
    private StarInfo toSystem;
    private String id;

    private SolarSystemObject fromObj;
    private SolarSystemObject toObj;
    
    private SimpleBooleanProperty isActive;
    private SimpleBooleanProperty isValid;
    private SimpleStringProperty from;
    private SimpleStringProperty to;
    private SimpleStringProperty shortName;
    
    public BooleanProperty isActiveProperty() { return isActive; }
    public BooleanProperty isValidProperty() { return isValid; }
    public StringProperty fromProperty() { return from; }
    public StringProperty toProperty() { return to; }
    public StringProperty shortNameProperty() { return shortName; }
    
    private void updateProps() {
        isActive.set(true);
        from.set((fromObj != null) ? fromObj.getName() : fromSystem.name);
        to.set(toObj != null ? toObj.getName() : toSystem.name);
        
        SovInfo sFrom = fromSystem.getSovInfo();
        SovInfo sTo = toSystem.getSovInfo();
        
        String s;
        boolean valid = false;
        if (sFrom.isClaimable() && sTo.isClaimable()) {
            if (sFrom.isClaimed() && sTo.isClaimed()) {
                if (sFrom.getOwnerID() == sTo.getOwnerID()) {
                    s = "[" + sFrom.getOwnerShort() + "]";
                    valid = true;
                }
                else {
                    // разные альянсы владеют системами
                    s = "-DIFF-";
                }
            }
            else {
                s = (sFrom.isClaimed() ? sFrom.getOwnerShort() : (sTo.isClaimed() ? sTo.getOwnerShort() : "")) + "--";
            }
        }
        else {
            s = "-N/A-";
        }
        isValid.set(valid);
        shortName.set(s);
    }
    
    public String getId() {
        return id;
    }
    public String serialize() {
        return "" + fromSystem.id
                + "\t" + toSystem.id
                + "\t" + (fromObj != null ? fromObj.getId() : -1)
                + "\t" + (toObj != null ? toObj.getId() : -1)
                + "\t" + (isActive.get() ? 1 : 0)
                ;
    }
    
    private void normalizeId() {

        if (fromSystem.id > toSystem.id) {
            StarInfo f1;
            f1 = fromSystem;
            fromSystem = toSystem;
            toSystem = f1;
        }
        
        id = fromSystem.id + "_" + toSystem.id;
    }
    
    public JBInfo(String serial) throws Exception {
        
        String[] data = serial.split("\t");
        if (data.length != 5) { throw new Exception("Invalid string");}
        
        long val;
        val = Long.parseLong(data[0]);
        fromSystem = EveStarExplorer.world.starsIndexById.get(val);
        if (fromSystem == null) { throw new Exception("Invalid string");}

        val = Long.parseLong(data[1]);
        toSystem = EveStarExplorer.world.starsIndexById.get(val);
        if (toSystem == null) { throw new Exception("Invalid string");}

        normalizeId();
        
        // Это нормально получить тут null
        val = Long.parseLong(data[2]);
        fromObj = EveStarExplorer.world.solarObjIndex.get(val);

        val = Long.parseLong(data[3]);
        toObj = EveStarExplorer.world.solarObjIndex.get(val);
        
        val = Long.parseLong(data[4]);
        isActive.set(val == 1);
        
        updateProps();

    }
    
    public JBInfo(StarInfo fromSys, SolarSystemObject fromObj, 
                  StarInfo toSys, SolarSystemObject toObj) {
        
        fromSystem = fromSys;
        toSystem = toSys;
        normalizeId();

        this.fromObj = fromSys.findSolarObj(fromObj);
        this.toObj = fromSys.findSolarObj(toObj);
        
        updateProps();
    }
    
    
    public JBInfo(StarInfo from, StarInfo to) {
        
        fromSystem = from;
        toSystem = to;
        normalizeId();
        
        updateProps();
    }
    
}
