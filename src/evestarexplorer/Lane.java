/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 *
 * @author g_yaltchik
 */
class Lane extends Line {
    final LaneInfo info;
    final World world;
    
    private boolean selected = false;
    
    void setSelected(boolean sel) {
        if (selected != sel) {
            
            selected = sel;
            repaintMe();
            
        }
    }

    Lane(LaneInfo li, World world) {
        super();
        this.world = world;
        info = li;
        setSmooth(true);
        repaintMe();
        StarInfo s1 = world.findStar(info.gate1).info;
        StarInfo s2 = world.findStar(info.gate2).info;
        setStartX(s1.x);
        setStartY(s1.y);
        setEndX(s2.x);
        setEndY(s2.y);
        setTranslateZ(0);
    }
    
    final String getLaneId() {
        return info.id;
    }

    private void repaintMe() {
        
        if (selected) {
            
            Color color = new Color(0, 0, 0, 1);
            setStroke(color);
            setStrokeWidth(1);
            
        }
        else {
            
            Color color = new Color(0, 0, 0, 0.5);
            setStroke(color);
            setStrokeWidth(0.5);

        }
        
    }

}
