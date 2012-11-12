/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer;

import java.util.ArrayList;
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

    private final World world;
    
    private PathTransition pathTrn;
    private Path path = new Path();
    private Circle marker;
    
    private final List<Star> stars = new ArrayList<>();
    private final List<Lane> lanes = new ArrayList<>();
    
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

    public PathHighlighter(World world, List<String> route) {
        
        this.world = world;
        
        marker = CircleBuilder.create()
                .radius(10)
                .mouseTransparent(true)
                .fill(Color.ORANGE)
                .build();
        
        Star prevStar = null;
        for (String s : route) {
            Star star = world.findStar(s);
            
            if (path.getElements().isEmpty()) {
                path.getElements().add(new PMoveTo(star.info.x, star.info.y));
            }
            else {
                path.getElements().add(new PLineTo(star.info.x, star.info.y));
            }

            if (prevStar != null) {
                Lane lane = world.findLane(star, prevStar);
                lanes.add(lane);
            }
            stars.add(star);
            prevStar = star;
        }

        path.setStroke(Color.TRANSPARENT);
        //path.setStrokeWidth(3);
        
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
        for (Star s : stars) { s.highlightStar(true); }
        for (Lane l : lanes) { l.highlightLane(true); }
        pathTrn.play();
    }
    
    void stop() {
        pathTrn.stop();
        for (Star s : stars) { s.highlightStar(false); }
        for (Lane l : lanes) { l.highlightLane(false); }
    }
    
}
