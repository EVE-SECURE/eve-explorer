/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer;

import java.util.List;
import javafx.animation.PathTransition;
import javafx.animation.PathTransitionBuilder;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CircleBuilder;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

/**
 *
 * @author g_yaltchik
 */
public class PathHighlighter extends Group {

    private PathTransition pathTrn;
    private Path path = new Path();
    private Circle marker;
    private Group root = null;
    
    class PMoveTo extends MoveTo {

        private final double starX;
        private final double starY;

        public PMoveTo(double x, double y) {
            super(x, y);
            starX = x;
            starY = y;
        }
        
        double getStarX() { return starX; }
        double getStarY() { return starY; }
        
    }

    class PLineTo extends LineTo {

        private final double starX;
        private final double starY;

        public PLineTo(double x, double y) {
            super(x, y);
            starX = x;
            starY = y;
        }
        
        double getStarX() { return starX; }
        double getStarY() { return starY; }
        
    }

    public PathHighlighter(List<String> route) {
        
        marker = CircleBuilder.create()
                .radius(10)
                .mouseTransparent(true)
                .fill(Color.ORANGE)
                .build();
        
        for (String s : route) {
            StarInfo si = EveStarExplorer.world.findStar(s).info;
            
            if (path.getElements().isEmpty()) {
                path.getElements().add(new PMoveTo(si.x, si.y));
            }
            else {
                path.getElements().add(new PLineTo(si.x, si.y));
            }
        }

        path.setStroke(Color.BLUEVIOLET);
        path.setStrokeWidth(3);
        
        pathTrn = PathTransitionBuilder
                    .create()
                    .duration(Duration.seconds(2))
                    .path(path)
                    .node(marker)
                    .cycleCount(Timeline.INDEFINITE)
                    .autoReverse(false)
                    .build();
        
        this.setId(PathHighlighter.class.getName());
        this.getChildren().addAll(path, marker);
    }
    
    void play() {
        pathTrn.play();
    }
    
    void stop() {
        pathTrn.stop();
    }
    
}
