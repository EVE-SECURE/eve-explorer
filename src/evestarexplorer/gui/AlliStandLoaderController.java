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
    @FXML public Button fillWClaimButton;
    @FXML public Button fillWAllButton;
    @FXML public Button resetTo0Button;
    
    @FXML protected void updateClicked() {

        AllianceList list = ApiInfoLoader.getInstance().alliances;
        
        String data = text.getText();
        if (data.length() == 0) {return;}
        
        String unprocessed = "";
        Pattern lPattern = Pattern.compile(".*\\[(.*?)\\]\\s+(-?\\d+)");
        for (String line: data.split("\n+")) {
            Matcher m = lPattern.matcher(line);
            
            try {
                if (m.find()) {
                    String ticker = m.group(1);
                    int stand = Integer.parseInt(m.group(2));

                    if (stand <= 10 && stand >= -10) {

                        AllianceInfo ai = list.get(ticker);

                        if (ai.id != 0) {
                            ai.setStanding(stand);
                        }
                        else {
                            throw new Exception("Unknown ticker");
                        }
                    }
                    else {
                        throw new Exception("Standing out of range");
                    }

                }
                else {
                    throw new Exception("Invalid line");
                }
            }
            
            catch (Exception ex) {
                unprocessed += line + "<<<" + ex.getMessage() + "\n";
            }
            
        }
        
        text.setText(unprocessed);
    }
    
    @FXML protected void fillClicked() {
        AllianceList list = ApiInfoLoader.getInstance().alliances;
        updateText(list, SelectMode.ONLY_CHANGED, false);
    }
    
    @FXML protected void fillAllClicked() {
        AllianceList list = ApiInfoLoader.getInstance().alliances;
        updateText(list, SelectMode.SHOW_ALL, false);
    }
    
    @FXML protected void fillClaimClicked() {
        AllianceList list = ApiInfoLoader.getInstance().alliances;
        updateText(list, SelectMode.ONLY_CLAIM, false);
    }
    
    @FXML protected void resetClicked() {
        AllianceList list = ApiInfoLoader.getInstance().alliances;
        updateText(list, SelectMode.SHOW_ALL, false);
    }

    @FXML protected void cancelClicked() {
        pane.getScene().getWindow().hide();
    }

    void updateText(AllianceList list, SelectMode mode, boolean setToZero) {
        
        SortedSet<AllianceInfo> l = new TreeSet<>(new AllianceInfo.AllianceInfoCompareByStanding());
        l.addAll(list.getList());
        
        String txt = "";
        
        for (AllianceInfo i : l) {
            if ((mode == SelectMode.SHOW_ALL) || 
                (mode == SelectMode.ONLY_CHANGED &&  i.getStanding() != 0) ||
                (mode == SelectMode.ONLY_CLAIM && i.getClaimed() != 0))
            {
                txt += i.name + " ["+ i.shortName +"]" + "             " + ((setToZero) ? 0 : i.getStanding()) + '\n';
            }
        }
        
        text.setText(txt);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        EveStarExplorer.standLoaderPanel = this;
        
    }
    
    static enum SelectMode { SHOW_ALL, ONLY_CLAIM, ONLY_CHANGED; }
    
}
