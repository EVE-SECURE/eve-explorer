/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer.gui;

import evestarexplorer.EveStarExplorer;
import evestarexplorer.SolarSystemObject;
import evestarexplorer.StarInfo;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CircleBuilder;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author g_yaltchik
 */
public class SolarMapController implements Initializable {

    @FXML ToggleButton showMoons;
    @FXML ToggleButton showBelts;
    
    @FXML Group displayGroup;
    Group fieldGroup = new Group();
    Group labelGroup = new Group();
    
    private double maxDistance = 0;
    
    private Map<SolarSystemObject, MapObject> objects = new HashMap<>();
    
    private double mouseX = 0;
    private double mouseY = 0;
    
    private final Translate t = new Translate();
    private final Rotate rX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rY = new Rotate(0, Rotate.Y_AXIS);
    private final Rotate rZ = new Rotate(0, Rotate.Z_AXIS);
    private SimpleDoubleProperty  scale = new SimpleDoubleProperty(1);
    
    SizeUpdateListener listener = new SizeUpdateListener();
    
    public void setTranslate(Translate t) {
        this.t.setX(t.getX());
        this.t.setY(t.getY());
        this.t.setZ(t.getZ());
    }
    
    public void setX(double x) { t.setX(x); }
    public void setY(double y) { t.setY(y); }
    public void setZ(double z) { t.setZ(z); }
    
    public void setAngleX(double angle) { rX.setAngle(angle); }
    public void setAngleY(double angle) { rY.setAngle(angle); }
    public void setAngleZ(double angle) { rZ.setAngle(angle); }
    
    private double getMaxDistance(Iterable<SolarSystemObject> starObjects) {
        double distance = 0;
        for (SolarSystemObject sso : starObjects) {
            distance = Math.max(distance, Math.sqrt(sso.getX() * sso.getX() + sso.getY() * sso.getY() + sso.getZ() * sso.getZ()));
        }
        return distance;
    }
    
    
    abstract class MapObject extends Group {
        abstract public void deregister();
    }
    
    class Orbit extends MapObject {

        private final Circle orbit;
        double radius = 0;
        
        public Orbit(SolarSystemObject sso) {
            super();
            radius = Math.sqrt(
                      sso.getX() * sso.getX() 
                    + sso.getY() * sso.getY() 
                    + sso.getZ() * sso.getZ());
            
            orbit = CircleBuilder.create()
                    .radius(radius*scale.get())
                    .stroke(new Color(0, 0, 0, 0.2))
                    .strokeWidth(2)
                    .fill(Color.TRANSPARENT)
                    .smooth(true)
                    .build();
            
            final Rotate rz = new Rotate(0, Rotate.Z_AXIS);
            final Rotate ry = new Rotate(0, Rotate.Y_AXIS);
            
            double xz = Math.sqrt(sso.getX()*sso.getX() + sso.getZ()*sso.getZ());
            double angle = Math.toDegrees(Math.asin(sso.getZ()/xz));
            if (sso.getX() < 0) { angle = 180 - angle; }
            rz.setAngle(angle);
            
            angle = Math.toDegrees(Math.asin(-sso.getY()/Math.sqrt(sso.getZ()*sso.getZ() + xz*xz)));
            ry.setAngle(angle);
            
            this.getTransforms().addAll(rz, ry);
            this.getChildren().addAll(orbit);
            
            orbit.radiusProperty().bind(scale.multiply(radius));
            
        }

        @Override
        public void deregister() {
            orbit.radiusProperty().unbind();
        }
        
    }
    
    class StarBody extends MapObject {
        
        protected final SolarSystemObject sso;
        protected final ImageView image;
        protected final Group imgTrans = new Group();
        
        public StarBody(SolarSystemObject s, Image img) {
            super();
            this.sso = s;
            
            image = ImageViewBuilder
                    .create()
                    .image(img)
                    .x(0)
                    .y(0)
                    .layoutX(-Images.planetIcon.getWidth() / 2)
                    .layoutY(-Images.planetIcon.getHeight() / 2)
                    .build();
            
            imgTrans.getChildren().add(image);
            imgTrans.translateXProperty().bind(scale.multiply(this.sso.getX()));
            imgTrans.translateYProperty().bind(scale.multiply(this.sso.getZ()));
            imgTrans.translateZProperty().bind(scale.multiply(this.sso.getY()));
            this.getChildren().add(imgTrans);
        }

        @Override
        public void deregister() {
            imgTrans.translateXProperty().unbind();
            imgTrans.translateYProperty().unbind();
            imgTrans.translateZProperty().unbind();
        }
        
    }
    
    class Planet extends StarBody {

        private final Orbit orbit;

        public Planet(SolarSystemObject s) {
            super(s, Images.planetIcon);
            
            orbit = new Orbit(sso);
            this.getChildren().add(orbit);
        }
        
    }
    
    class Sun extends StarBody {

        public Sun(SolarSystemObject s) {
            super(s, Images.sunIcon);
        }
        
    }
    
    class Station extends StarBody {

        public Station(SolarSystemObject s) {
            super(s, Images.stationIcon);
        }
        
    }
    
    class Gate extends StarBody {
        
        private final Text label = new Text();
        private ChangeListener<Number> rxListener;

        public Gate(SolarSystemObject s) {
            super(s, Images.gateIcon);
            
            label.setText(s.getName());
            label.setFont(new Font(10));
            label.setFill(Color.GRAY);
            
            labelGroup.getChildren().add(label);
            
            rxListener = new ChangeListener<Number>() {

                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    Point2D xy = image.localToScene(new Point2D(image.getX(), image.getY()));
                    Point2D lxy = label.sceneToLocal(xy);
                    label.setX(lxy.getX());
                    label.setY(lxy.getY());
                }
            };
            
            rX.angleProperty().addListener(rxListener);
            
        }

        @Override
        public void deregister() {
            super.deregister();
            rX.angleProperty().removeListener(rxListener);
        }
        
    }
    
    private void updateScale() {
        double sceneSize = Math.min(fieldGroup.getScene().getWindow().getWidth(), 
                                    fieldGroup.getScene().getWindow().getHeight());
        scale.set( sceneSize * 0.9 / (2 * maxDistance) );
    }

    private MapObject createObject(SolarSystemObject s) {
    
        switch (s.getType()) {
            case SUN: return new Sun(s);
            case PLANET: return new Planet(s);
            case MOON: break;
            case BELT: break;
            case STATION: return new Station(s);
            case GATE: return new Gate(s);
            default:
                assert true;
        }
        return null;
    }
    
    public void setupMap(StarInfo si) {
        
        fieldGroup.getChildren().clear();
        labelGroup.getChildren().clear();
        
        for (MapObject o : objects.values()) {
            o.deregister();
        }
        objects.clear();
        
        Window w = fieldGroup.getScene().getWindow();
        w.heightProperty().addListener(listener);
        w.widthProperty().addListener(listener);
        w.setOnShowing(new EventHandler<WindowEvent>(){

            @Override
            public void handle(WindowEvent event) {
                updateScale();
            }
        });

        maxDistance = getMaxDistance(si.getStarObjects());
        updateScale();
        
        for (SolarSystemObject sso : si.getStarObjects()) {

            MapObject o = createObject(sso);
            if (o != null ) {
                objects.put(sso, o);
                fieldGroup.getChildren().add(o);
            }
            
        }
        
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        EveStarExplorer.solarMapPanel = this;
        showMoons.setGraphic(new ImageView(Images.moonIcon));
        showBelts.setGraphic(new ImageView(Images.beltIcon));
        
        Parent parent = displayGroup.getParent();
        
        displayGroup.getChildren().addAll(fieldGroup, labelGroup);
        
        fieldGroup.getTransforms().addAll(t, rX, rY, rZ);

        parent.setOnMousePressed(new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent event) {
                mouseX = event.getX();
                mouseY = event.getY();
            }
        });
        
        parent.setOnMouseDragged(new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent event) {
                double dX = mouseX - event.getX();
                double dY = mouseY - event.getY();
                
                mouseX = event.getX();
                mouseY = event.getY();
                
                rY.setAngle(rY.getAngle() + dX / 5);
                rX.setAngle(rX.getAngle() + dY / 5);
//                rZ.setAngle(rZ.getAngle() + dY / 5);
            }
            
        });
        
    }

    
    class SizeUpdateListener implements ChangeListener<Number> {

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            SolarMapController.this.updateScale();
        }
        
    }

}
