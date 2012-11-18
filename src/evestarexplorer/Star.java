/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author g_yaltchik
 */
public class Star extends Group {

    final StarInfo info;
    
    static private Star currentStar = null;
    static private boolean alwaysShowText = false;

    private long selectCounter = 0;
    private boolean selected = false;
    private final double STAR_SIZE = 5;
    final World world;
    
    private final Rectangle body = new Rectangle();
    private final Text text = new Text();
    
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
        text.setVisible(selected || alwaysShowText);
        text.setFill(selected ? Color.BLACK : Color.GRAY);
        text.toFront();
    }
    
    final public void repaintBody() {

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
        
        Color color = DispUtil.ssColor(info.ss);
        body.setFill(color);
        
        if (selectCounter != 0) {
            if (selected) { return; }
            selected = true;
            body.setStroke(Color.BLACK);
            body.setStrokeType(StrokeType.OUTSIDE);
            body.setStrokeWidth(1.0);
            
            repaintText();
        }
        else {
            if (!selected) { return; }
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
        
        repaintBody();
        repaintMe();
        
        setTranslateX(info.x);
        setTranslateY(info.y);
        setTranslateZ(0);
        

        final Star self = this;
        body.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                
                if (!me.isStillSincePress()) { return; }
                
                if (me.getClickCount() == 2) {
                    EveStarExplorer.centerAtStar(self);
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
                    self.setAsCurrent();
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
            }
        });
        body.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                highlightNeigbors(false);
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
    
}
