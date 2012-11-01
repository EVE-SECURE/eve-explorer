/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer.gui;

import com.beimin.eveapi.core.ApiException;
import evestarexplorer.ApiInfoLoader;
import evestarexplorer.DispUtil;
import evestarexplorer.EveStarExplorer;
import evestarexplorer.StarExplorerEvent;
import evestarexplorer.StarInfo;
import evestarexplorer.Valid;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author g_yaltchik
 */
public class ControlPanelController implements Initializable {
    
    private Group starField = null;
    
    public void setStarField(Group field) {
        starField = field;
    }

    //@FXML private AnchorPane mainPane;
    @FXML public Label infoStarName;
    @FXML public Label infoConst;
    @FXML public Label infoRegion;
    @FXML public Label infoSS;
    
    @FXML public Label infoSovOwner;

    @FXML public Button apiLoad;
    @FXML public Label apiSovereignityStatus;
    @FXML public Label apiAllianceListStatus;
    
    @FXML public TextField gateFrom;
    @FXML public TextField gateDest;
    @FXML public Button gateStart;
    
    @FXML public CheckBox gateHS_prefs;
    @FXML public CheckBox gateLS_prefs;
    @FXML public CheckBox gate0S_prefs;
    
    @FXML public VBox gateAvoidVBox;
    @FXML public CheckBox gateAvoidAmarr;
    @FXML public CheckBox gateAvoidCaldari;
    @FXML public CheckBox gateAvoidMinmatar;
    @FXML public CheckBox gateAvoidGallente;
    
    @FXML public ToggleButton gateFindFrom;
    @FXML public ToggleButton gateFindDest;
    
    
    private void showApiStatus(final Label l, final ApiException ex, final Date at) {
        
        Platform.runLater(new Runnable() {
            @Override 
            public void run() {
                
                if (ex == null) {
                    l.setText("OK");
                    l.setStyle("-fx-text-fill: green");
                    l.setTooltip(new Tooltip("Updated at: " + at.toString()));
                }
                else {
                    l.setText("ERROR");
                    l.setStyle("-fx-text-fill: red");
                    l.setTooltip(new Tooltip("Error: " + ex.toString() + "\n" + at));
                }
                
            }
        });
        
    }
    
    @FXML protected void apiUpdateClicked() {
        
        LoadingSplashController splash = evestarexplorer.EveStarExplorer.splashPanel;
        
        ApiUpdateTask task = new ApiUpdateTask();
        splash.start(task);

    }
    
    public class ApiUpdateTask extends Task<Void> {       
        
        final LoadingSplashController splash = evestarexplorer.EveStarExplorer.splashPanel;
        
        @Override 
        @SuppressWarnings("SleepWhileInLoop")
        protected Void call() throws InterruptedException {

            String message = "";
            ApiInfoLoader loader = ApiInfoLoader.getInstance();

            updateProgress(-1, 1);
            try {
                message += "Alliance List loading ... ";
                updateMessage(message);
                loader.alliances.update();
                showApiStatus(apiAllianceListStatus, null, loader.alliances.updatedAt());
    
                gateAvoidVBox.setDisable(false);
            } catch (ApiException ex) {
                showApiStatus(apiAllianceListStatus, ex, new Date());
            }
            message += "Ok\n";
            updateMessage(message);

            try {
                message += "Sovereignty Info loading ... ";
                updateMessage(message);
                loader.sovereignty.update();
                showApiStatus(apiSovereignityStatus, null, loader.sovereignty.updatedAt());
            } catch (ApiException ex) {
                showApiStatus(apiSovereignityStatus, ex, new Date());
            }
            message += "OK\n";
            updateMessage(message);
            
            Platform.runLater(new Runnable() {
                @Override 
                public void run() {
                    starField.fireEvent(new StarExplorerEvent(StarExplorerEvent.API_UPDATED));
//                    starField.fireEvent(new StarExplorerEvent(StarExplorerEvent.LONG_ACTION_ENDED));
                    //splash.hide();
                }
            });
            return null;
        }
    }
    
    public void gateStarClicked(StarInfo si) {
        
        starField.fireEvent(new StarExplorerEvent(StarExplorerEvent.STAR_LOOKUP_OFF));
        if (gateFindFrom.isSelected()) {
            gateFrom.setText(si.name);
            gateFindFrom.setSelected(false);
            gateFindFromClicked();
        }
        if (gateFindDest.isSelected()) {
            gateDest.setText(si.name);
            gateFindDest.setSelected(false);
            gateFindDestClicked();
        }
        
        //gateCheckInput();
        
    }
    
    private void gateFindClicked(ToggleButton b1, ToggleButton b2) {
        
        if (b2.isSelected()) {
            throw new Error("CPanel: both find buttons are selected");
        }
        
        gateFrom.setDisable(b1.isSelected());
        gateDest.setDisable(b1.isSelected());
        b2.setDisable(b1.isSelected());
        
        if (b1.isSelected()) {
            starField.fireEvent(new StarExplorerEvent(StarExplorerEvent.STAR_LOOKUP_ON));
        }
        else {
            starField.fireEvent(new StarExplorerEvent(StarExplorerEvent.STAR_LOOKUP_OFF));
        }
    }
    
    @FXML protected void gateFindFromClicked() {
        
        gateFindClicked(gateFindFrom, gateFindDest);

    }
    
    @FXML protected void gateFindDestClicked() {
        
        gateFindClicked(gateFindDest, gateFindFrom);

    }
    
    @FXML protected void gateCheckInput() {
        
        Valid s1 = DispUtil.starNameInputValidate(gateFrom);
        Valid s2 = DispUtil.starNameInputValidate(gateDest);
        
        if ( s1 == Valid.FULL && s2 == Valid.FULL ) {
            gateStart.setDisable(false);
        }
        else {
            gateStart.setDisable(true);
        }
    }
    
    @FXML protected void gateStartSearch() {
        
        String s1 = gateFrom.getText().toUpperCase();
        String s2 = gateDest.getText().toUpperCase();
        List<String> path;

        path = EveStarExplorer.world.findPath(s1, s2);
        System.err.println(path.toString());
        
        if (!path.isEmpty()) {
            System.out.println("Fire!!!");
            //EveStarExplorer.primaryStage.getScene().getRoot().fireEvent(new StarExplorerEvent(path));
            starField.fireEvent(new StarExplorerEvent(path));
        }
    }

    @FXML public Label dbgScale;
    @FXML public Label dbgMouseX;
    @FXML public Label dbgMouseY;
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        EveStarExplorer.ctrlPanel = this;
        
        final Image targetIcon = new Image(ControlPanelController.class.getResourceAsStream("/evestarexplorer/images/Target.png"));
        ImageView imageView1 = new ImageView(targetIcon);
        imageView1.setPreserveRatio(true);
        imageView1.setFitHeight(16);
        imageView1.setFitWidth(16);
        ImageView imageView2 = new ImageView(targetIcon);
        imageView2.setPreserveRatio(true);
        imageView2.setFitHeight(16);
        imageView2.setFitWidth(16);
        gateFindFrom.setGraphic(imageView1);
        gateFindDest.setGraphic(imageView2);
        
        gateStart.setDisable(true);

        ChangeListener<String> inputCheck = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                gateCheckInput();
            }
        };
        
        gateDest.textProperty().addListener(inputCheck);
        gateFrom.textProperty().addListener(inputCheck);
    
    }
    
}
