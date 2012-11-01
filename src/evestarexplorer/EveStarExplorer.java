/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer;

import evestarexplorer.gui.AlliStandLoaderController;
import evestarexplorer.gui.ControlPanelController;
import evestarexplorer.gui.LoadingSplashController;
import evestarexplorer.gui.SettingsPanelController;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author gyv
 */
public class EveStarExplorer extends Application {
    
    //Cam ctrlCam = new Cam();
    Cam rootCam = new Cam();
    
    double mousePosX;
    double mousePosY;
    double mouseOldX;
    double mouseOldY;
    double mouseDeltaX;
    double mouseDeltaY;
    
    double scale = 1;
    double scaleMin = 1;
    double scaleMax = 10;
    double scaleStep = 1.2;
    
    static public LoadingSplashController splashPanel;
    static public ControlPanelController ctrlPanel;
    static public SettingsPanelController setPanel;
    static public AlliStandLoaderController standLoaderPanel;
    static Stage primaryStage;
    
    public static World world;

    double getWorldX (double sceneX) {
        return (sceneX - rootCam.x) / scale;
    }
    
    double getWorldY (double sceneY) {
        return (sceneY - rootCam.y) / scale;
    }
    
    double getSceneX (double worldX) {
        return worldX * scale + rootCam.x;
    }
    
    double getSceneY (double worldY) {
        return worldY * scale + rootCam.y;
    }
    
    private void resetCamera(Stage stage) {
        
        double h = stage.getScene().getHeight();
        double w = stage.getScene().getWidth();
        
        double wldW = world.maxX - world.minX;
        double wldH = world.maxY - world.minY;
        
        scaleMin = Math.min(w / wldW, h / wldH);
        scale = scaleMin;
        
        world.setScale(scale);
        
        double x = -(world.maxX + world.minX) / 2 * scale;
        double y = -(world.maxY + world.minY) / 2 * scale;
        
        x += w / 2;
        y += h / 2;
        
        rootCam.setCamPos(x, y);
    }
    
    class Cam extends Group {
        Translate t  = new Translate();
        Translate p  = new Translate();
        Translate ip = new Translate();
        Rotate rx = new Rotate();
        Rotate ry = new Rotate();
        Rotate rz = new Rotate();
        Scale s = new Scale();
        
        double x = 0;
        double y = 0;

        { 
            rx.setAxis(Rotate.X_AXIS);
            ry.setAxis(Rotate.Y_AXIS);
            rz.setAxis(Rotate.Z_AXIS); 
        }
        
        public Cam() { 
            super(); 
            getTransforms().addAll(t, p, rx, rz, ry, s, ip); 
        }

        void setCamPos(double posX, double posY) {
            
            x = posX;
            y = posY;
            
            setTranslateX(posX);
            setTranslateY(posY);

            ctrlPanel.dbgMouseX.setText(Double.toString(posX));
            ctrlPanel.dbgMouseY.setText(Double.toString(posY));
            
        }

        private void moveCamPos(double deltaX, double deltaY) {
            
            setCamPos(x + deltaX, y + deltaY);
            
        }

    }
    

    
    private Stage initSplashPanel() {
        final Stage stage = new Stage();
        try {
            Parent ctrlPane = FXMLLoader.load(getClass().getResource("gui/LoadingSplash.fxml"));
            Scene scene = new Scene(ctrlPane);
            stage.setScene(scene);
        } catch (IOException ex) {
            Logger.getLogger(EveStarExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return stage;
    }
    
    private Stage initControlPanel() {
        final Stage stage = new Stage();
        try {
            Parent ctrlPane = FXMLLoader.load(getClass().getResource("gui/ControlPanel.fxml"));
            Scene scene = new Scene(ctrlPane);
            stage.setScene(scene);
        } catch (IOException ex) {
            Logger.getLogger(EveStarExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return stage;
    }
    
    private Stage initSettingPanel() {
        final Stage stage = new Stage();
        try {
            Parent settingsPane = FXMLLoader.load(getClass().getResource("gui/SettingsPanel.fxml"));
            Scene scene = new Scene(settingsPane);
            stage.setScene(scene);
        } catch (IOException ex) {
            Logger.getLogger(EveStarExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return stage;
    }
        
    void initMainPanel(Stage stage) {
        
        world = new World(rootCam, ctrlPanel, setPanel);
        
        stage.setResizable(true);
        stage.setTitle("EVE Star Explorer");
        stage.setScene(new Scene(rootCam, 800, 600, false));
        
        rootCam.getChildren().add(populateWorld());
        ctrlPanel.setStarField(rootCam);
        
        setHandlers(stage.getScene());
    }

    void setHandlers(final Scene s) {
        
        s.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent se) {
                
                double newScale = scale;
                
                if (se.getDeltaY() > 0) { 
                    newScale = newScale * scaleStep; 
                }
                else if (se.getDeltaY() < 0) {
                    newScale = newScale / scaleStep; 
                }
                else {
                    return;
                }
                
                if (newScale <= scaleMax && newScale >= scaleMin) {
                    
                    double sceneX = se.getSceneX();
                    double sceneY = se.getSceneY();
                    
                    double worldX = getWorldX(sceneX);
                    double worldY = getWorldY(sceneY);
                    
                    scale = newScale;
                    world.setScale(scale);
                    
                    double newSceneX = getSceneX(worldX);
                    double newSceneY = getSceneY(worldY);
                    
                    double dX = newSceneX - sceneX;
                    double dY = newSceneY - sceneY;
                    
                    rootCam.moveCamPos(-dX, -dY);
                }
            }
        });
        
        s.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                mouseOldX = me.getX();
                mouseOldY = me.getY();
                mousePosX = me.getX();
                mousePosY = me.getY();
            }

        });
        
        s.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                mousePosX = me.getX();
                mousePosY = me.getY();

                mouseDeltaX = mousePosX - mouseOldX;
                mouseDeltaY = mousePosY - mouseOldY;
                
                if (me.isPrimaryButtonDown()) {
                    
                    rootCam.moveCamPos(mouseDeltaX, mouseDeltaY);

                }
            }

        });

        s.addEventHandler(StarExplorerEvent.LONG_ACTION_STARTED, new EventHandler<StarExplorerEvent>() {

            @Override
            public void handle(StarExplorerEvent event) {
                s.setCursor(Cursor.WAIT);
            }
            
        });

        s.addEventHandler(StarExplorerEvent.LONG_ACTION_ENDED, new EventHandler<StarExplorerEvent>() {

            @Override
            public void handle(StarExplorerEvent event) {
                s.setCursor(Cursor.DEFAULT);
            }
            
        });

    }
    
    Node populateWorld() {
        
        String line;
        InputStream s = EveStarExplorer.class.getResourceAsStream("data/starsystems.txt"); 
        BufferedReader br = new BufferedReader( new InputStreamReader(s) );
        
        try {
            while ((line = br.readLine()) != null) {
                world.addStarInfo(line);
            }
        }
        catch (IOException ex) {
            Logger.getLogger(EveStarExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }

        s = EveStarExplorer.class.getResourceAsStream("data/starobjects.txt"); 
        br = new BufferedReader( new InputStreamReader(s) );
        
        try {
            while ((line = br.readLine()) != null) {
                world.addSolarObject(line);
            }
        }
        catch (IOException ex) {
            Logger.getLogger(EveStarExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }

        world.generateStars();

        s = EveStarExplorer.class.getResourceAsStream("data/jumps.txt"); 
        br = new BufferedReader( new InputStreamReader(s) );
        
        try {
            while ((line = br.readLine()) != null) {
                world.addLane(line);
            }
        }
        catch (IOException ex) {
            Logger.getLogger(EveStarExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Group g = new Group();
        g.getChildren().addAll(world.getStars());
        g.getChildren().addAll(world.getLanes());
        
        world.raiseStars();
        return g;
    }
    
    @Override
    public void start(Stage primaryStage) {
        
        EveStarExplorer.primaryStage = primaryStage;
        
        Stage spl = initSplashPanel();
        Stage cp = initControlPanel();
        Stage sp = initSettingPanel();
        initMainPanel(primaryStage);
        
        resetCamera(primaryStage);
        
        //spl.initOwner(primaryStage);
        spl.initModality(Modality.APPLICATION_MODAL);
        spl.initStyle(StageStyle.UNDECORATED);
        splashPanel.setStage(spl);
        
        cp.initOwner(primaryStage);
        
        sp.initOwner(primaryStage);
        
        primaryStage.show();
        cp.show();
        sp.show();
    
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
