/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer.gui;

import evestarexplorer.ApiInfoLoader;
import evestarexplorer.EveStarExplorer;
import evestarexplorer.api.AllianceInfo;
import evestarexplorer.api.AllianceList;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author gyv
 */
public class SettingsPanelController implements Initializable {

    final Stage standUpdater = new Stage();

    @FXML TableView allianceTbl;
    @FXML TableColumn<AllianceInfo, String> allyNameCol;
    @FXML TableColumn<AllianceInfo, String> allySNameCol;
    @FXML TableColumn<AllianceInfo, Long> allyMembersCol;
    @FXML TableColumn<AllianceInfo, Integer> allyClaimSizeCol;
    @FXML TableColumn<AllianceInfo, Integer> allyStandingCol;
    
    @FXML protected Button setStandings;
    
    @FXML protected void setStandingClicked() {
//        AllianceList list = ApiInfoLoader.getInstance().alliances;
//        EveStarExplorer.standLoaderPanel.updateText(list);
        standUpdater.show();
    }
    
    public void apiUpdated() {
        final ObservableList<AllianceInfo> data;
        AllianceList list = ApiInfoLoader.getInstance().alliances;
        data = FXCollections.observableArrayList(list.getList());
        allianceTbl.setItems(data);
//        
//        EveStarExplorer.standLoaderPanel.updateText(list);
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        EveStarExplorer.setPanel = this;
        
        allyNameCol.setCellValueFactory(new PropertyValueFactory("fullName"));
        allySNameCol.setCellValueFactory(new PropertyValueFactory("shortName"));
        allyMembersCol.setCellValueFactory(new PropertyValueFactory("membersCount"));
        allyClaimSizeCol.setCellValueFactory(new PropertyValueFactory("claimed"));
        allyStandingCol.setCellValueFactory(new PropertyValueFactory("standing"));
        
        allyNameCol.setComparator(String.CASE_INSENSITIVE_ORDER);
        allySNameCol.setComparator(String.CASE_INSENSITIVE_ORDER);
        
        try {
            Parent settingsPane = FXMLLoader.load(getClass().getResource("AlliStandLoader.fxml"));
            Scene scene = new Scene(settingsPane);
            standUpdater.setScene(scene);
        } catch (IOException ex) {
            Logger.getLogger(EveStarExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        standUpdater.initModality(Modality.APPLICATION_MODAL);
        standUpdater.initStyle(StageStyle.UTILITY);

    }    
}
