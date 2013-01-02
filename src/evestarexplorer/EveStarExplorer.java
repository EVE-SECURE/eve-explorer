/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer;

import evestarexplorer.gui.AlliStandLoaderController;
import evestarexplorer.gui.ControlPanelController;
import evestarexplorer.gui.GeometryPersistentStage;
import evestarexplorer.gui.LoadingSplashController;
import evestarexplorer.gui.SSystemObjectsController;
import evestarexplorer.gui.SettingsPanelController;
import evestarexplorer.gui.SolarMapController;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author gyv
 */
public class EveStarExplorer extends Application {

    //Cam ctrlCam = new Camera();
    static Camera rootCam = new Camera();

    double mousePosX;
    double mousePosY;
    double mouseOldX;
    double mouseOldY;
    double mouseDeltaX;
    double mouseDeltaY;
    
    static double scale = 1;
    static double scaleMin = 1;
    final static double SCALE_MAX = 10;
    final static double SCALE_STEP = 1.2;

    private final static double SHOW_STAR_NAMES_SCALE = 0.9;
    
    static public LoadingSplashController splashPanel;
    static public ControlPanelController ctrlPanel;
    static public SettingsPanelController setPanel;
    static public AlliStandLoaderController standLoaderPanel;
    static public SSystemObjectsController ssysObjPanel;
    static public SolarMapController solarMapPanel;
    
    static public Stage primaryStage;
    static public Stage ctrlPanelStage;
    static public Stage setPanelStage;
    static public Stage ssysPanelStage;
    static public Stage solarMapStage;
    
    public static World world;

    static double getWorldX (double sceneX) {
        return (sceneX - rootCam.x) / scale;
    }
    
    static double getWorldY (double sceneY) {
        return (sceneY - rootCam.y) / scale;
    }
    
    static double getSceneX (double worldX) {
        return worldX * scale + rootCam.x;
    }
    
    static double getSceneY (double worldY) {
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
    
    public static void setAsCurrent(String name) {
        
        Star star = world.findStar(name);
        if (star == null) { return; }
        
        star.setAsCurrent();
        
    }
    
    static void centerAtStar(Star star) {
        if (star == null) { return; }
        
        double centerX = primaryStage.getScene().getWidth()/2;
        double centerY = primaryStage.getScene().getHeight()/2;
        
        double x = getSceneX(star.info.x);
        double y = getSceneY(star.info.y);
        
        double dX = centerX - x;
        double dY = centerY - y;
        
        rootCam.moveCamPos(dX, dY);
    }
    
    public static void centerAtStar(String starName) {
        centerAtStar(world.findStar(starName));
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
        
        GeometryPersistentStage stage = null;
        try {
            Parent ctrlPane = FXMLLoader.load(getClass().getResource("gui/ControlPanel.fxml"));
            Scene scene = new Scene(ctrlPane);
            stage = new GeometryPersistentStage(ctrlPanel);
            stage.setMonitoredScene(scene);
        } catch (IOException ex) {
            Logger.getLogger(EveStarExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return stage;

    }
    
    private Stage initSettingPanel() {

        GeometryPersistentStage stage = null;
        try {
            Parent settingsPane = FXMLLoader.load(getClass().getResource("gui/SettingsPanel.fxml"));
            Scene scene = new Scene(settingsPane);
            stage = new GeometryPersistentStage(setPanel);
            stage.setMonitoredScene(scene);
        } catch (IOException ex) {
            Logger.getLogger(EveStarExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return stage;
    
    }
    
    private Stage initSSysPanel() {
        
        GeometryPersistentStage stage = null;
        try {
            Parent root = FXMLLoader.load(getClass().getResource("gui/SSystemObjects.fxml"));
            Scene scene = new Scene(root);
            stage = new GeometryPersistentStage(ssysObjPanel);
            stage.setMonitoredScene(scene);
        } catch (IOException ex) {
            Logger.getLogger(EveStarExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return stage;

    }
        
    private Stage initSolarMap() {

        GeometryPersistentStage stage = null;
        try {
            Parent root = FXMLLoader.load(getClass().getResource("gui/SolarMap.fxml"));
            Scene scene = new Scene(root, 300, 300, true);
            stage = new GeometryPersistentStage(solarMapPanel);
            scene.setCamera(new PerspectiveCamera());
            stage.setMonitoredScene(scene);
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
        
        setHandlers(stage.getScene());
    }
    
    private void updateStarNamesDisplayStatus() {

        boolean showStatus = scale > SHOW_STAR_NAMES_SCALE;
        if (showStatus != Star.getAlwaysShowText()) {
            Star.setAlwaysShowText(showStatus);
            for (Star s : world.worldStars.values()) {
                s.repaintText();
            }
        }
    }

    void setHandlers(final Scene s) {
        
        s.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent se) {
                
                double newScale = scale;
                
                if (se.getDeltaY() > 0) { 
                    newScale = newScale * SCALE_STEP; 
                }
                else if (se.getDeltaY() < 0) {
                    newScale = newScale / SCALE_STEP; 
                }
                else {
                    return;
                }
                
                if (newScale <= SCALE_MAX && newScale >= scaleMin) {
                    
                    double sceneX = se.getSceneX();
                    double sceneY = se.getSceneY();
                    
                    double worldX = getWorldX(sceneX);
                    double worldY = getWorldY(sceneY);
                    
                    scale = newScale;
                    updateStarNamesDisplayStatus();
                    
                    world.setScale(scale);
                    
                    double newSceneX = getSceneX(worldX);
                    double newSceneY = getSceneY(worldY);
                    
                    double dX = newSceneX - sceneX;
                    double dY = newSceneY - sceneY;
                    
                    rootCam.moveCamPos(-dX, -dY);
                }
            }
        });
        
        s.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if(event.getButton().equals(MouseButton.PRIMARY) 
                   && (event.getClickCount() == 2))
                {
                    ctrlPanelStage.show();
                    ctrlPanelStage.setIconified(false);
                    setPanelStage.show();
                    setPanelStage.setIconified(false);
                }
                else if(event.getButton() == MouseButton.SECONDARY) {
                    world.menu.show(s.getWindow(), event.getScreenX(), event.getScreenY());
                }
            }

        });
        
        s.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                if (me.getButton() == MouseButton.PRIMARY) {
                    mouseOldX = me.getX();
                    mouseOldY = me.getY();
                    mousePosX = me.getX();
                    mousePosY = me.getY();
                }
            }

        });
        
        s.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {

                if (me.getButton() != MouseButton.PRIMARY) { return; }
                
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
    
    final static long STARS_COUNT = 5432;
    final static long STAROBJECTS_COUNT = 362274;
    final static long LANES_COUNT = 14335;
    
    @SuppressWarnings("SleepWhileInLoop")
    void populateWorld() {
        
        final Task<Void> task = new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                
                String line;
                String message = "";
                
                message += "Loading stars... ";
                updateMessage(message);
                InputStream s = EveStarExplorer.class.getResourceAsStream("data/starsystems.txt"); 
                BufferedReader br = new BufferedReader( new InputStreamReader(s) );

                try {
                    long counter = 0;
                    while ((line = br.readLine()) != null) {
                        counter++;
                        world.addStarInfo(line);
                        updateProgress(counter, STARS_COUNT);
                    }
                }
                catch (IOException ex) {
                    Logger.getLogger(EveStarExplorer.class.getName()).log(Level.SEVERE, null, ex);
                }
                message += "Ok\n";
                updateMessage(message);

                message += "Loading solar systems objects... ";
                updateMessage(message);
                s = EveStarExplorer.class.getResourceAsStream("data/starobjects.txt"); 
                br = new BufferedReader( new InputStreamReader(s) );

                try {
                    long counter = 0;
                    while ((line = br.readLine()) != null) {
                        counter++;
                        world.addSolarObject(line);
                        updateProgress(counter, STAROBJECTS_COUNT);
                    }
                }
                catch (IOException ex) {
                    Logger.getLogger(EveStarExplorer.class.getName()).log(Level.SEVERE, null, ex);
                }

                world.generateStars();
                message += "Ok\n";
                updateMessage(message);

                message += "Loading jump lanes... ";
                updateMessage(message);
                s = EveStarExplorer.class.getResourceAsStream("data/jumps.txt"); 
                br = new BufferedReader( new InputStreamReader(s) );

                try {
                    long counter = 0;
                    while ((line = br.readLine()) != null) {
                        counter++;
                        world.addLane(line);
                        updateProgress(counter, LANES_COUNT);
                    }
                }
                catch (IOException ex) {
                    Logger.getLogger(EveStarExplorer.class.getName()).log(Level.SEVERE, null, ex);
                }
                message += "Ok\n";
                updateMessage(message);
                
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        Group g = new Group();
                        g.getChildren().addAll(world.getStars());
                        g.getChildren().addAll(world.getLanes());

                        world.raiseStars();
                        
                        rootCam.getChildren().add(g);
                        ctrlPanel.setStarField(rootCam);
                        ctrlPanel.setupStarsList(world.getStarInfos());
                        
                        resetCamera(primaryStage);
                        
                        //setPanelStage.show();
                        ctrlPanelStage.show();
                    }
                });

                return null;
            };

        };

        evestarexplorer.EveStarExplorer.splashPanel.start(task);
        
    }
    
    @Override
    public void start(Stage primaryStage) {
        
        EveStarExplorer.primaryStage = primaryStage;
        
        Stage spl = initSplashPanel();
        spl.initOwner(primaryStage);
        spl.initModality(Modality.APPLICATION_MODAL);
        spl.initStyle(StageStyle.UNDECORATED);
        splashPanel.setStage(spl);
        
        ctrlPanelStage = initControlPanel();
        ctrlPanelStage.initOwner(primaryStage);
        
        setPanelStage = initSettingPanel();
        setPanelStage.initOwner(primaryStage);
        
        ssysPanelStage = initSSysPanel();
        ssysPanelStage.initOwner(primaryStage);
        
        solarMapStage = initSolarMap();
        solarMapStage.initOwner(primaryStage);
        
        initMainPanel(primaryStage);

        resetCamera(primaryStage);
        primaryStage.show();
        
        populateWorld();
        
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
