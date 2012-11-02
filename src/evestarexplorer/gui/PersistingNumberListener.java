/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer.gui;

import java.util.prefs.Preferences;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author g_yaltchik
 */
class PersistingNumberListener implements ChangeListener<Number> {
    private final Preferences pref;
    private final String key;

    public PersistingNumberListener(Preferences pref, String key) {
        this.pref = pref;
        this.key = key;
    }

    @Override
    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        pref.put(key, newValue.toString());
//        System.out.println(key + ": " + newValue);
    }
    
}
