/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

/**
 *
 * @author g_yaltchik
 */
public class Star extends Rectangle {

    final StarInfo info;
    
    static private Star currentStar = null;
    
    private long selectCounter = 0;
    private boolean selected = false;
    private final double STAR_SIZE = 5;
    final World world;
    
    private void repaintMe() {
        
        Color color = DispUtil.ssColor(info.ss);
        setFill(color);
        
        if (selectCounter != 0) {
            if (selected) { return; }
            selected = true;
            setStroke(Color.BLACK);
            setStrokeType(StrokeType.OUTSIDE);
            setStrokeWidth(1.0);
        }
        else {
            if (!selected) { return; }
            selected = false;
            setStroke(Color.TRANSPARENT);
            setStrokeWidth(0);
        }
    }
    
    void setSelection(boolean sel) {
        
        selectCounter += sel ? +1 : -1;
        repaintMe();
        for (StarInfo si : info.getNeigbors()) {
            world.worldLanes.get(LaneInfo.getKey(si.name, this.info.name)).setSelected(sel);
        }
        
    }
    
    void setAsCurrent() {
        
        if (currentStar == this) { return; }
        if (currentStar != null) {
            currentStar.setSelection(false);
        }

        currentStar = this;
        setSelection(true);
    }

    Star(StarInfo si, final World world) {
        super();
        
        this.world = world;
        info = si;
        
        double size = STAR_SIZE;
        
        setHeight(size);
        setWidth(size);
        setStrokeType(StrokeType.OUTSIDE);
        
        if (info.hasStation() == false) {
            setArcHeight(size);
            setArcWidth(size);
        }
        
        setSmooth(true);
        repaintMe();
        
        setLayoutX(-size/2);
        setLayoutY(-size/2);
        setTranslateX(info.x);
        setTranslateY(info.y);
        setTranslateZ(0);
        

        final Star self = this;
        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                
                if (!me.isStillSincePress()) { return; }
                
                if (me.getClickCount() == 2) {
                    EveStarExplorer.centerAtStar(self);
                    return;
                }
                
                if (me.isControlDown()) {
                    EveStarExplorer.ssysObjPanel.setupSystem(info);
                    EveStarExplorer.ssysPanelStage.setTitle(info.name + " (" + info.region + ")");
                    EveStarExplorer.ssysPanelStage.show();
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
        setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                world.getCPanel().infoStarName.setText(info.name);
                world.getCPanel().infoConst.setText(info.constellation);
                world.getCPanel().infoRegion.setText(info.region);
                DispUtil.SStatus(world.getCPanel().infoSS, info.ss);
                world.getCPanel().infoSovOwner.setText(info.getSovInfo().getOwnerName());
                setSelection(true);
            }
        });
        setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                setSelection(false);
            }
        });
    }

    void addGate(StarInfo si) {
        info.addNeigbor(si);
    }

    StarInfoList getGates() {
        return info.getNeigbors();
    }
    
}
