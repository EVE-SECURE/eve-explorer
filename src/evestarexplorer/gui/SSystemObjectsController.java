/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer.gui;

import evestarexplorer.DispUtil;
import evestarexplorer.EveStarExplorer;
import evestarexplorer.SolarSystemObject;
import evestarexplorer.StarInfo;
import evestarexplorer.WarpSafety;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author gyv
 */
public class SSystemObjectsController implements Initializable {

    @FXML TableView objectsTbl;
    @FXML TableColumn<SolarObject, Boolean> currentCol;
    @FXML TableColumn<SolarObject, Double> distCol;
    @FXML TableColumn<SolarObject, String> nameCol;
    
    final static Image sunIcon = new Image(ControlPanelController.class.getResourceAsStream("/evestarexplorer/images/sun_16x16.png"));
    final static Image planetIcon = new Image(ControlPanelController.class.getResourceAsStream("/evestarexplorer/images/planet_16x16.png"));
    final static Image moonIcon = new Image(ControlPanelController.class.getResourceAsStream("/evestarexplorer/images/moon_16x16.png"));
    final static Image beltIcon = new Image(ControlPanelController.class.getResourceAsStream("/evestarexplorer/images/belt_16x16.png"));
    final static Image gateIcon = new Image(ControlPanelController.class.getResourceAsStream("/evestarexplorer/images/gate_16x16.png"));
    final static Image stationIcon = new Image(ControlPanelController.class.getResourceAsStream("/evestarexplorer/images/station_16x16.png"));
        
    private final ObservableList<SolarObject> data = FXCollections.observableArrayList();
    
    public void setupSystem(StarInfo si) {
        
        SolarObject sun = null;
        data.clear();
        for (SolarSystemObject sso : si.getStarObjects()) {
            SolarObject so = new SolarObject(sso);
            data.add(so);
            if (sso.type == SolarSystemObject.Type.SUN) { sun = so; }
        }
        setCurrentObject(sun);
        objectsTbl.setItems(data);
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        EveStarExplorer.ssysObjPanel = this;
        currentCol.setCellValueFactory(new PropertyValueFactory("active"));
        distCol.setCellValueFactory(new PropertyValueFactory("distance"));
        distCol.setSortType(TableColumn.SortType.ASCENDING);
        nameCol.setCellValueFactory(new PropertyValueFactory("name"));
        
        distCol.setCellFactory(new Callback<TableColumn<SolarObject, Double>, TableCell<SolarObject, Double>> () {
            @Override
            public TableCell<SolarObject, Double> call(TableColumn<SolarObject, Double> param) {
                return new DistanceTableCell<>();
            }
        });
        
        currentCol.setCellFactory(new Callback<TableColumn<SolarObject, Boolean>, TableCell<SolarObject, Boolean>>() {
            @Override
            public TableCell<SolarObject, Boolean> call(TableColumn<SolarObject, Boolean> param) {
                return new ActiveTableCell<>();
            }
        });
        
        nameCol.setCellFactory(new Callback<TableColumn<SolarObject, String>, TableCell<SolarObject, String>>() {
            @Override
            public TableCell<SolarObject, String> call(TableColumn<SolarObject, String> param) {
                return new NameTableCell<>();
            }
        });
        
    }

    private void setCurrentObject(SolarObject curObj) {
        for (SolarObject obj : data) {
            obj.updateDistance(curObj.so);
            obj.active.set(obj == curObj);
        }
        objectsTbl.getSortOrder().setAll(distCol);
        objectsTbl.getSelectionModel().select(curObj);
    }

    private class NameTableCell <S, T> extends TableCell<S, T> {

        public NameTableCell() {
            super();
            
            setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {
                    if (event.isStillSincePress() && event.getClickCount()==2) {
                        SolarObject so = data.get(getIndex());
                        setCurrentObject(so);
                    }
                }
                
            });
        }
        
        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
            } else {
                setText((String)item);
                SolarObject so = data.get(getIndex());
                setTextFill(so.active.get() ? Color.RED : Color.BLACK);
            }
        }

    }

    private class DistanceTableCell<S, T> extends TableCell<S, T> {

        public DistanceTableCell() {
            super();
            setGraphic(null);
        }

        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
            } else {
                SolarObject so = data.get(getIndex());
                setTextFill(so.active.get() ? Color.RED : Color.BLACK);
                setText(DispUtil.dist2HumanReadeable((Double) item));
                setTextFill((Double) item > WarpSafety.DSCAN_RANGE ? Color.BLACK : Color.BLUE);
            }
        }
    }

    private class ActiveTableCell<S, T> extends TableCell<S, T> {

        public ActiveTableCell() {
            setGraphic(null);
        }

        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            
            if (empty) {
                setText(null);
                setGraphic(null);
            } 
            else {
                setText("");
                SolarObject so = data.get(getIndex());
                if (so != null) {
                    switch (so.so.type) {
                        case BELT: setGraphic(new ImageView(beltIcon)); break;
                        case GATE: setGraphic(new ImageView(gateIcon)); break;
                        case MOON: setGraphic(new ImageView(moonIcon)); break;
                        case PLANET: setGraphic(new ImageView(planetIcon)); break;
                        case STATION: setGraphic(new ImageView(stationIcon)); break;
                        case SUN: setGraphic(new ImageView(sunIcon)); break;
                        default:
                            setGraphic(null);
                    }
                }
            }
        }
    }
    
    public class SolarObject {
        
        private BooleanProperty active = new SimpleBooleanProperty();
        private DoubleProperty distance = new SimpleDoubleProperty();
        private StringProperty name = new SimpleStringProperty();
        public final SolarSystemObject so;

        public BooleanProperty activeProperty() {
            return active;
        }

        public DoubleProperty distanceProperty() {
            return distance;
        }

        public StringProperty nameProperty() {
            return name;
        }

        public SolarObject(SolarSystemObject so) {
            active.set(false);
            distance.set(Double.MAX_VALUE);
            name.set(so.name);
            this.so = so;
        }

        private void updateDistance(SolarSystemObject sun) {
            distance.set(so.distance(sun));
        }
        
        
    }
}
