/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer.gui;

import java.util.prefs.Preferences;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Расширение класса Stage
 * Отличается тем, что сохраняет геометрию окна
 * @author g_yaltchik
 */
public class GeometryPersistentStage extends Stage {

    private final Preferences prefs;
    private static final String prefs_key_x = "geom_X";
    private static final String prefs_key_y = "geom_Y";
    private static final String prefs_key_width = "geom_W";
    private static final String prefs_key_height = "geom_H";

    /**
     *
     * @param root
     */
    public final void setMonitoredScene(Scene root) {
        setScene(root);
        
        try {
            double x,y,h,w;
            x = prefs.getDouble(prefs_key_x, -1);
            y = prefs.getDouble(prefs_key_y, -1);
            h = prefs.getDouble(prefs_key_height, -1);
            w = prefs.getDouble(prefs_key_width, -1);

//            System.out.println("Loaded: " + x + "," + y + " " + h + "," + w);
            
            if (x != -1) { setX(x); }
            if (y != -1) { setY(y); }
            if (h != -1) { setHeight(h); }
            if (w != -1) { setWidth(w); }
        }
        catch (Exception ex) {
        }
        
        xProperty().addListener(new PersistingNumberListener(prefs, prefs_key_x));
        yProperty().addListener(new PersistingNumberListener(prefs, prefs_key_y));
        heightProperty().addListener(new PersistingNumberListener(prefs, prefs_key_height));
        widthProperty().addListener(new PersistingNumberListener(prefs, prefs_key_width));
    }
    
    public GeometryPersistentStage(Object owner) {
        super();
        prefs =  Preferences.userNodeForPackage(owner.getClass()).node(owner.getClass().getSimpleName());
    }
}
