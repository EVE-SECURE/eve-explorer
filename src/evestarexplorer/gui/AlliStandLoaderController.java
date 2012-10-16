/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer.gui;

import evestarexplorer.ApiInfoLoader;
import evestarexplorer.EveStarExplorer;
import evestarexplorer.api.AllianceInfo;
import evestarexplorer.api.AllianceList;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    @FXML public Button fillWCurrentButton;
    @FXML public Button resetTo0Button;
    
    @FXML protected void updateClicked() {
        String data = text.getText();
        if (data.length() == 0) {return;}
        
        String unprocessed = "";
        Pattern lPattern = Pattern.compile(".*\\[(.*?)\\]\\s+(-?\\d+)");
        for (String line: data.split("\n+")) {
            Matcher m = lPattern.matcher(line);
            
            if (m.find()) {
                String ticker = m.group(1);
                int stand = Integer.parseInt(m.group(2));
                
                System.out.println(ticker + " : " + stand);
            }
            else {
                unprocessed += line;
            }
        }
        
        text.setText(unprocessed);
    }
    
    @FXML protected void fillClicked() {
        AllianceList list = ApiInfoLoader.getInstance().alliances;
        updateText(list, false);
    }
    
    @FXML protected void resetClicked() {
        AllianceList list = ApiInfoLoader.getInstance().alliances;
        updateText(list, false);
    }

    @FXML protected void cancelClicked() {
        pane.getScene().getWindow().hide();
    }

    public void updateText(AllianceList list, boolean setToZero) {
        
        SortedSet<AllianceInfo> l = new TreeSet<>(new AllianceInfo.AllianceInfoCompareByStanding());
        l.addAll(list.getList());
        
        String txt = "";
        
        for (AllianceInfo i : l) {
            txt += i.name + "["+ i.shortName +"]" + "             " + ((setToZero) ? 0 : i.standing) + '\n';
        }
        
        text.setText(txt);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        EveStarExplorer.standLoaderPanel = this;
        
    }
    
}
