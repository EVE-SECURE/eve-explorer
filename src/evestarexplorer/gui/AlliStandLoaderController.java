/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer.gui;

import evestarexplorer.EveStarExplorer;
import evestarexplorer.api.AllianceInfo;
import evestarexplorer.api.AllianceList;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author gyv
 */

public class AlliStandLoaderController implements Initializable {

    @FXML AnchorPane pane;
    @FXML TextArea text;
    
    @FXML public Button updateButtton;
    @FXML public Button cancelButtton;
    
    @FXML protected void cancelClicked() {
        pane.getScene().getWindow().hide();
    }

    public void updateText(AllianceList list) {
        
        SortedSet<AllianceInfo> l = new TreeSet<>(new AllianceInfo.AllianceInfoCompareByStanding());
        l.addAll(list.getList());
        
        String txt = "";
        
        for (AllianceInfo i : l) {
            txt = txt + i.name + '\t' + i.standing + '\n';
        }
        
        text.setText(txt);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        EveStarExplorer.standLoaderPanel = this;
        
    }    
}
