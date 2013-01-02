/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer;

import java.util.List;
import javafx.event.Event;
import javafx.event.EventType;

/**
 *
 * @author gyv
 */
public class StarExplorerEvent extends Event {

    public static final EventType<StarExplorerEvent> PATH_FOUND = new EventType<>("PATH_FOUND");
    public static final EventType<StarExplorerEvent> STAR_LOOKUP_ON = new EventType<>("STAR_LOOKUP_ON");
    public static final EventType<StarExplorerEvent> STAR_LOOKUP_OFF = new EventType<>("STAR_LOOKUP_OFF");
    public static final EventType<StarExplorerEvent> LONG_ACTION_STARTED = new EventType<>("LONG_ACTION_STARTED");
    public static final EventType<StarExplorerEvent> LONG_ACTION_ENDED = new EventType<>("LONG_ACTION_ENDED");
    public static final EventType<StarExplorerEvent> API_UPDATED = new EventType<>("API_UPDATED");
    public static final EventType<StarExplorerEvent> JBMAP_ADD_STAR = new EventType<>("JBMAP_ADD_STAR");
    public static final EventType<StarExplorerEvent> MAP_STAR_ENTERED = new EventType<>("MAP_STAR_ENTERED");
    public static final EventType<StarExplorerEvent> MAP_STAR_LEAVED = new EventType<>("MAP_STAR_LEAVED");
    
    List<String> path = null;
    Star star = null;
    
    public StarExplorerEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }
    
    public StarExplorerEvent(List<String> path) {
        super(PATH_FOUND);
        this.path = path;
    }
    
    public StarExplorerEvent(Star star, EventType<StarExplorerEvent> event) {
        super(event);
        this.star = star;
    }
    
}
