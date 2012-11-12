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
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CircleBuilder;
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
//    @FXML AnchorPane starFieldPane;
    
    @FXML Group fieldGroup = new Group();
    
    private double maxDistance = 0;
    
    private Map<SolarSystemObject, Node> objects = new HashMap<>();
    
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
            distance = Math.max(distance, Math.sqrt(sso.x * sso.x + sso.y * sso.y + sso.z * sso.z));
        }
        return distance;
    }
    
    class Orbit extends Group {

        private final Circle orbit;
        double radius = 0;
        
        public Orbit(SolarSystemObject sso) {
            super();
            radius = Math.sqrt(
                      sso.x * sso.x 
                    + sso.y * sso.y 
                    + sso.z * sso.z);
            
            orbit = CircleBuilder.create()
                    .radius(radius*scale.get())
                    .stroke(new Color(0, 0, 0, 0.2))
                    .strokeWidth(2)
                    .fill(Color.TRANSPARENT)
                    .smooth(true)
                    .build();
            
            final Rotate rz = new Rotate(0, Rotate.Z_AXIS);
            final Rotate ry = new Rotate(0, Rotate.Y_AXIS);
            
            double xz = Math.sqrt(sso.x*sso.x + sso.z*sso.z);
            double angle = Math.toDegrees(Math.asin(sso.z/xz));
            if (sso.x < 0) { angle = 180 - angle; }
            rz.setAngle(angle);
            
            angle = Math.toDegrees(Math.asin(-sso.y/Math.sqrt(sso.z*sso.z + xz*xz)));
            ry.setAngle(angle);
            
            this.getTransforms().addAll(rz, ry);
            this.getChildren().addAll(orbit);
            
            orbit.radiusProperty().bind(scale.multiply(radius));
            
        }
        
    }
    
    class StarBody extends Group {
        
        private final SolarSystemObject s;
        private final ImageView image;
        
        public StarBody(SolarSystemObject s, Image img) {
            super();
            this.s = s;
            
            image = ImageViewBuilder
                    .create()
                    .image(img)
                    .x(0)
                    .y(0)
//                    .x(this.s.x * scale.doubleValue())
//                    .y(this.s.z * scale.doubleValue())
                    .layoutX(-Images.planetIcon.getWidth() / 2)
                    .layoutY(-Images.planetIcon.getHeight() / 2)
                    .build();
            
//            image.xProperty().bind(scale.multiply(this.s.x));
//            image.yProperty().bind(scale.multiply(this.s.z));
            
//            Translate _t = new Translate();
//            _t.xProperty().bind(scale.multiply(this.s.x));
//            _t.yProperty().bind(scale.multiply(this.s.z));
//            _t.zProperty().bind(scale.multiply(this.s.y));
            
            Group imgTrans = new Group(image);
            imgTrans.translateXProperty().bind(scale.multiply(this.s.x));
            imgTrans.translateYProperty().bind(scale.multiply(this.s.z));
            imgTrans.translateZProperty().bind(scale.multiply(this.s.y));
            
            
//            Rotate _rx = new Rotate(0, Rotate.X_AXIS);
//            Rotate _ry = new Rotate(0, Rotate.Y_AXIS);
//            Rotate _rz = new Rotate(0, Rotate.Z_AXIS);
//
//            _rx.angleProperty().bind(rX.angleProperty().multiply(-1.0));
//            _ry.angleProperty().bind(rY.angleProperty().multiply(-1.0));
//            _rz.angleProperty().bind(rZ.angleProperty().multiply(-1.0));
//            
//            Group iG1 = new Group();
//            Group iG2 = new Group();
//            
//            iG1.getTransforms().add(_t);
//            iG1.getTransforms().add(_rx);
//            iG2.getTransforms().add(_ry);
//            
//            iG1.getChildren().add(image);
//            iG2.getChildren().add(iG1);
            
            //iG1.getTransforms().addAll(_t, _rz, _ry, _rz);
            
//            this.getChildren().add(iG2);
            this.getChildren().add(imgTrans);
        }
        
    }
    
    class Planet extends StarBody {

        private final SolarSystemObject sso;
        private final Orbit orbit;

        public Planet(SolarSystemObject s) {
            super(s, Images.planetIcon);
            this.sso = s;
            
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

        public Gate(SolarSystemObject s) {
            super(s, Images.gateIcon);
        }
        
    }
    
    private void updateScale() {
        double sceneSize = Math.min(fieldGroup.getScene().getWidth(), fieldGroup.getScene().getHeight());
        scale.set( sceneSize / (2 * maxDistance) );
        System.out.println("new scale:" + scale);
    }

    private Node createObject(SolarSystemObject s) {
    
        switch (s.type) {
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

            Node o = createObject(sso);
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
        
        fieldGroup.getTransforms().addAll(t, rX, rY, rZ);

        showMoons.setGraphic(new ImageView(Images.moonIcon));
        showBelts.setGraphic(new ImageView(Images.beltIcon));
        
        fieldGroup.getParent().setOnMousePressed(new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent event) {
                mouseX = event.getX();
                mouseY = event.getY();
            }
        });
        
        fieldGroup.getParent().setOnMouseDragged(new EventHandler<MouseEvent>(){

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
