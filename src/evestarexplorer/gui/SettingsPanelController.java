/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer.gui;

import evestarexplorer.ApiInfoLoader;
import evestarexplorer.EveStarExplorer;
import evestarexplorer.api.AllianceInfo;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author gyv
 */
public class SettingsPanelController implements Initializable {

    @FXML TableView allianceTbl;
    @FXML TableColumn<AllianceInfo, String> allyNameCol;
    @FXML TableColumn<AllianceInfo, String> allySNameCol;
    @FXML TableColumn<AllianceInfo, Long> allyMembersCol;
    @FXML TableColumn<AllianceInfo, Integer> allyStandingCol;
    
    public void apiUpdated() {
        final ObservableList<AllianceInfo> data;
        data = FXCollections.observableArrayList(ApiInfoLoader.getInstance().alliances.getList());
        allianceTbl.setItems(data);
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
        allyStandingCol.setCellValueFactory(new PropertyValueFactory("standing"));
        
        allyNameCol.setComparator(String.CASE_INSENSITIVE_ORDER);
        allySNameCol.setComparator(String.CASE_INSENSITIVE_ORDER);
        
    }    
}
