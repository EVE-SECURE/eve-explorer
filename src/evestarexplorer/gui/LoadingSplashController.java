/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer.gui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author gyv
 */
public class LoadingSplashController implements Initializable {

    @FXML Label taskName;
    @FXML TextArea info;
    @FXML ProgressBar progress;
    
    private Stage stage;
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    public void show() {
        stage.show();
    }
    
    public void hide() {
        stage.hide();
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        evestarexplorer.EveStarExplorer.splashPanel = this;
    }
}
