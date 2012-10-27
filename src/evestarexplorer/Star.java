/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;

/**
 *
 * @author g_yaltchik
 */
class Star extends Circle {
    final StarInfo info;
    final World world;
    boolean selected = false;
    
    private void repaintMe() {
        
        Color color = DispUtil.ssColor(info.ss);
        RadialGradient gr;
        if (selected) {
        
            gr = new RadialGradient(0, 0, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE, new Stop[] {
                        new Stop(0, color)
                    });
            
            setStroke(Color.BLACK);
            setStrokeWidth(0.5);
            
        }
        else {
            
            gr = new RadialGradient(0, 0, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE, new Stop[] {
                        new Stop(0, color),
                        new Stop(0.5, new Color(color.getRed(), color.getGreen(), color.getBlue(), 0)),
                        new Stop(1, Color.TRANSPARENT)
                    });
            
            setStroke(Color.TRANSPARENT);
            setStrokeWidth(0);
            
        }
        
        setFill(gr);
    }
    
    void setSelected(boolean sel) {
        if (selected != sel) {
            selected = sel;
            repaintMe();
            
            for (StarInfo si : info.getNeigbors()) {
                world.worldLanes.get(LaneInfo.getKey(si.name, this.info.name)).setSelected(sel);
            }
        }
    }

    Star(StarInfo si, final World world) {
        super();
        
        this.world = world;
        info = si;
        
        double size = 4;
        //final Star self = this;
        setRadius(size);
        setSmooth(true);
        repaintMe();
        
        setTranslateX(info.x);
        setTranslateY(info.y);
        setTranslateZ(0);
        
        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                System.out.println(info.region + " > " + info.name);
                System.out.println("star: " + info.x + ", " + info.y);
                System.out.println("scene: " + me.getSceneX() + ", " + me.getSceneY());
                System.out.println("screen: " + me.getScreenX() + ", " + me.getScreenY());
                
                world.getCPanel().gateStarClicked(info);
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
                setSelected(true);
            }
        });
        setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                setSelected(false);
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
