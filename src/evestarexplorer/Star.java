/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MenuItemBuilder;
import javafx.scene.input.MouseButton;

/**
 *
 * @author g_yaltchik
 */
public class Star extends Group {

    final StarInfo info;
    final World world;
    
    private static final double STAR_SIZE = 5;
    
    private static StarContextMenu menu = null;
    
    static private Star currentStar = null;
    static private boolean alwaysShowText = false;

    private long selectCounter = 0;
    private boolean selected = false;
    private boolean inactive = false;
    private boolean highlighted = false;
    
    private final Rectangle body = new Rectangle();
    private final Text text = new Text();
    
    /**
     * Включает режим в котором звезды всегда отображают свое имя на карте.
     * Используется при масштабировании.
     * 
     * @param alwaysShowText
     */
    public static void setAlwaysShowText(boolean alwaysShowText) {
        Star.alwaysShowText = alwaysShowText;
    }
    
    public static boolean getAlwaysShowText() {
        return Star.alwaysShowText;
    }
    
    private String createLabel() {
        if (info.getShlipKills() != -1) {
            return info.name + "\n(" + info.getShlipKills() + "/" +
                    info.getPodKills() + "/" + info.getNpcKills() + ")";
        }
        else {
            return info.name;
        }
    }
    
    final public void updateLabel() {
        text.setText(createLabel());
        text.setTextAlignment(TextAlignment.CENTER);
    }
    
    final public void repaintText() {
        text.setVisible(selected || highlighted || alwaysShowText);
        text.setFill(selected ? Color.BLACK : Color.GRAY);
        text.toFront();
    }
    
    private void paintBody() {
        
        Color color = DispUtil.ssColor(info.ss);
        
        if (inactive) {
            color = color.grayscale();
        }
        
        body.setFill(color);
    }
    
    final public void reshapeBody() {

        double size = STAR_SIZE;
        
        body.setHeight(size);
        body.setWidth(size);
        body.setStrokeType(StrokeType.OUTSIDE);
        body.setSmooth(true);
        
        body.setArcHeight(info.hasStation() ? 0 : size);
        body.setArcWidth(info.hasStation() ? 0 : size);
        
        setLayoutX(-size/2);
        setLayoutY(-size/2);
        
    }
    
    private void repaintMe() {
        
        paintBody();
        
        if (selectCounter != 0 || highlighted) {
            //if (selected) { return; }
            selected = true;
            body.setStroke(highlighted ? body.getFill() : Color.BLACK);
            body.setStrokeType(StrokeType.OUTSIDE);
            body.setStrokeWidth(highlighted ? 2.0 : 1.0);
            
            repaintText();
        }
        else {
            //if (!selected) { return; }
            selected = false;
            body.setStroke(Color.TRANSPARENT);
            body.setStrokeWidth(0);
            
            repaintText();
        }
    }
    
    void highlightStar(boolean sel) {
        selectCounter += sel ? +1 : -1;
        repaintMe();
    }
    
    void highlightNeigbors(boolean sel) {
        
        highlightStar(sel);
        for (StarInfo si : info.getNeigbors()) {
            world.worldLanes.get(LaneInfo.getKey(si.name, this.info.name)).highlightLane(sel);
        }
        
    }
    
    void setAsCurrent() {
        
        if (currentStar == this) { return; }
        if (currentStar != null) {
            currentStar.highlightNeigbors(false);
        }

        currentStar = this;
        highlightNeigbors(true);
    }

    /**
     * @return the highlighted
     */
    public boolean isHighlighted() {
        return highlighted;
    }

    /**
     * @param highlighted the highlighted to set
     */
    public void setHighlighted(boolean highlighted) {
        if (this.highlighted != highlighted) {
            this.highlighted = highlighted;
            repaintMe();
        }
    }

    /**
     * @return the inactive
     */
    public boolean isInactive() {
        return inactive;
    }

    /**
     * Устанавливает состояние активности.
     * Неактивная звезда рисуется без цвета и не принимает никаких событий.
     * @param inactive the inactive to set
     */
    public void setInactive(boolean inactive) {
        if (this.inactive != inactive) {
            this.inactive = inactive;
            paintBody();
        }
    }

    Star(StarInfo si, final World world) {
        super();

        this.world = world;
        info = si;
        
        double size = STAR_SIZE;
        
//        body.setHeight(size);
//        body.setWidth(size);
//        body.setStrokeType(StrokeType.OUTSIDE);
//        
//        if (info.hasStation() == false) {
//            body.setArcHeight(size);
//            body.setArcWidth(size);
//        }
        
        reshapeBody();
        repaintMe();
        
        setTranslateX(info.x);
        setTranslateY(info.y);
        setTranslateZ(0);
        
        body.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                
                //if (Star.this.inactive) { return; }
                if (!me.isStillSincePress()) { return; }
                
                if (me.getButton() == MouseButton.PRIMARY) {

                    if (me.getClickCount() == 2) {
                        EveStarExplorer.centerAtStar(Star.this);
                        me.consume();
                        return;
                    }

                    if (me.isControlDown()) {
                        EveStarExplorer.ssysObjPanel.setupSystem(info);
                        EveStarExplorer.ssysPanelStage.setTitle(info.name + " (" + info.region + ")");
                        EveStarExplorer.ssysPanelStage.show();
                    }
                    else if (me.isAltDown()) {
                        EveStarExplorer.solarMapPanel.setupMap(info);
                        EveStarExplorer.solarMapStage.setTitle(info.name + " (" + info.region + ")");
                        EveStarExplorer.solarMapStage.show();
                    }
                    else {
    //                    System.out.println(info.region + " > " + info.name);
    //                    System.out.println("star: " + info.x + ", " + info.y);
    //                    System.out.println("scene: " + me.getSceneX() + ", " + me.getSceneY());
    //                    System.out.println("screen: " + me.getScreenX() + ", " + me.getScreenY());
    //                    System.out.println("cam: " + EveStarExplorer.rootCam.x + ", " + EveStarExplorer.rootCam.y);

                        world.getCPanel().gateStarClicked(info);
                        Star.this.setAsCurrent();
                    }
                    
                }
                else if (me.getButton() == MouseButton.SECONDARY) {
                    
                    if (menu == null) { menu = new StarContextMenu(); }
                    
                    menu.attachTo(Star.this);
                    menu.show(Star.this.body, Side.RIGHT, 4, 4);
                    
                    me.consume();
                }
            }
        });
        
        body.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                world.getCPanel().infoStarName.setText(info.name);
                world.getCPanel().infoConst.setText(info.constellation);
                world.getCPanel().infoRegion.setText(info.region);
                DispUtil.SStatus(world.getCPanel().infoSS, info.ss);
                world.getCPanel().infoSovOwner.setText(info.getSovInfo().getOwnerName());
                highlightNeigbors(true);
                Star.this.getScene().getRoot().fireEvent(new StarExplorerEvent(Star.this, StarExplorerEvent.MAP_STAR_ENTERED));
            }
        });

        body.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                highlightNeigbors(false);
                Star.this.getScene().getRoot().fireEvent(new StarExplorerEvent(Star.this, StarExplorerEvent.MAP_STAR_LEAVED));
            }
        });

        text.setFont(new Font(10));
        text.setFill(Color.GRAY);
        text.setLayoutX(4);
        text.setLayoutY(-4);
        text.setVisible(false);
        text.setMouseTransparent(true);
        updateLabel();
        
        getChildren().addAll(body, text);
        
    }

    void addGate(StarInfo si) {
        info.addNeigbor(si);
    }

    StarInfoList getGates() {
        return info.getNeigbors();
    }

    static class StarContextMenu extends ContextMenu {

        private Star current = null;
        
        public void attachTo(Star star) {
            current = star;
            
            newJB.setDisable(!star.info.getSovInfo().isClaimable() || star.isInactive());
        }
        
        private final MenuItem center;
        private final MenuItem newJB;
        
        public StarContextMenu() {
            super();
            
            setConsumeAutoHidingEvents(false);
            
            center = MenuItemBuilder.create()
                        .text("Center")
                        .id(null)
                        .onAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent t) {
                                EveStarExplorer.centerAtStar(current);
                                current.setAsCurrent();
                            }
                        })
                        .build();

            newJB = MenuItemBuilder.create()
                        .text("Add JB")
                        .onAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent t) {
                                current.getScene().getRoot().fireEvent(new StarExplorerEvent(current, StarExplorerEvent.JBMAP_ADD_STAR));
                            }
                        })
                        .build();
            
            getItems().addAll(center, newJB);

        }
        
    }
    
}
