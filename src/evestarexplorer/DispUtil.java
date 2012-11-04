/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer;

import com.mytdev.javafx.scene.control.AutoCompleteTextField;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

/**
 *
 * @author gyv
 */

public class DispUtil {

    static void SStatus(Label l, double ss) {
        final Color clr;
        clr = ssColor(ss);
        
        l.setText(String.format("%1$.2f", ss));
        l.setTextFill(clr);
    }

    static Color ssColor(Double ss) {
        
        Color clr = (ss <= 0) ? Color.RED : (ss < 0.45) ? Color.ORANGE : Color.GREEN;
        return clr;
    }
    
    public static Valid starNameInputValidate(AutoCompleteTextField t) {
        
        String name = t.getText();
        Valid res = EveStarExplorer.world.isStarValid(name);
        
        switch (res) {
            case FULL:
                t.setStyle("-fx-text-fill: green");
                break;
            case PARTIAL:
                t.setStyle("-fx-text-fill: black");
                break;
            case BAD:
                t.setStyle("-fx-text-fill: red");
                break;
        }
        
        return res;
    }
    
    
    
    /**
     * 149 597 870 700 - 1AU
     * 149 597 870 - 149M km
     * 149597 - 149K km
     * 149 - 149 km
     * @param dist дистанция в метрах
     * @return читабельное значение
     */
    public static String dist2HumanReadeable(double dist) {
        
        long v = Math.round(dist / Const.AU);
        if (v >= 1) {
            return "" + v + "AU";
        } 
        
        v = Math.round(dist / 1000000000 );
        if (v >= 1) {
            return "" + v + "M km";
        }
        
        v = Math.round(dist / 1000000 );
        if (v >= 1) {
            return "" + v + "K km";
        }
        
        v = Math.round(dist / 1000 );
        return "" + v + " km";
    }
    
}
