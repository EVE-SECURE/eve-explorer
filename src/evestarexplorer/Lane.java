/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;

/**
 *
 * @author g_yaltchik
 */
class Lane extends Line {
    final LaneInfo info;
    
    private boolean selected = false;
    
    void setSelected(boolean sel) {
        if (selected != sel) {
            
            selected = sel;
            repaintMe();
            
        }
    }

    Lane(LaneInfo li) {
        super();
        
        info = li;
        
        setSmooth(true);
        
        
        switch (info.type) {
            
            case CONSTELLATION:
                getStrokeDashArray().addAll(2d,2d);
                break;
                
            case REGION:
                getStrokeDashArray().addAll(10d,2d);
                break;
                
        }
        
        
        repaintMe();
        setStartX(info.si1.x);
        setStartY(info.si1.y);
        setEndX(info.si2.x);
        setEndY(info.si2.y);
        setTranslateZ(0);
    }
    
    final String getLaneId() {
        return info.id;
    }

    private static List<Stop> unsafeStart = new ArrayList<>();
    private static List<Stop> unsafeEnd = new ArrayList<>();
    {
        unsafeStart.add(new Stop(0, Color.RED)); 
        unsafeStart.add(new Stop(0.2, Color.RED));
        unsafeStart.add(new Stop(0.3, Color.BLACK));
        unsafeEnd.add(new Stop(0.7, Color.BLACK)); 
        unsafeEnd.add(new Stop(0.8, Color.RED));
        unsafeEnd.add(new Stop(1, Color.RED));
    }
        
    private void repaintMe() {
        
        if (selected) {
            
            List<Stop> stops = new ArrayList<>();
            
            if (info.so1.isWarpSafe() == false) {
                stops.addAll(unsafeStart);
            }
            else {
                stops.add(new Stop(0, Color.BLACK));
            }
            
            if (info.so2.isWarpSafe() == false) {
                stops.addAll(unsafeEnd);
            }
            else {
                stops.add(new Stop(1, Color.BLACK));
            }
            
            LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
            setStroke(gradient);
            setStrokeWidth(1);
            
        }
        else {
            
            Color color = new Color(0, 0, 0, 0.5);
            setStroke(color);
            setStrokeWidth(0.5);

        }
        
    }

}
