/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer;

import com.beimin.eveapi.map.sovereignty.ApiSystemSovereignty;
import evestarexplorer.api.AllianceList;
import evestarexplorer.api.Sovereignty;
import evestarexplorer.gui.ControlPanelController;
import evestarexplorer.gui.SettingsPanelController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;

/**
 *
 * @author g_yaltchik
 */
public class World {
    
    final private ControlPanelController cPanel;
    final private SettingsPanelController sPanel;
    final private Group rootScene;
    
    private PathHighlighter ph = null;
    
    private double currentScale = 1.0;
    
    Map<String, Star> worldStars = new HashMap<>();
    Map<String, Lane> worldLanes = new HashMap<>();
    Map<String, StarInfo> starsIndexByName = new HashMap<>();
    Map<Long, StarInfo> starsIndexById = new HashMap<>();
    Map<String, LaneInfo> lanesIndex = new HashMap<>();
    
    double minX = Double.MAX_VALUE;
    double minY = Double.MAX_VALUE;
    double maxX = Double.MIN_VALUE;
    double maxY = Double.MIN_VALUE;
    
    World(final Group root, ControlPanelController cp, SettingsPanelController sp) {
        rootScene = root;
        cPanel = cp;
        sPanel = sp;
        
        root.addEventHandler(StarExplorerEvent.PATH_FOUND, new EventHandler<StarExplorerEvent>() {

            @Override
            public void handle(StarExplorerEvent event) {
                System.out.println("Path found event:");
                System.out.println("[" + event.path.size() + "] " + event.path.toString());
                event.consume();
                
                if (ph != null) {
                    ph.stop();
                    rootScene.getChildren().remove(ph);
                    ph = null;
                }
                
                ph = new PathHighlighter(event.path);
                doSetScale(ph, currentScale);
                rootScene.getChildren().add(ph);
                raiseStars();
                ph.play();
            }
            
        });
        
        root.addEventHandler(StarExplorerEvent.STAR_LOOKUP_ON, new EventHandler<StarExplorerEvent>() {

            @Override
            public void handle(StarExplorerEvent event) {
                root.getScene().setCursor(Cursor.CROSSHAIR);
            }
            
        });

        root.addEventHandler(StarExplorerEvent.STAR_LOOKUP_OFF, new EventHandler<StarExplorerEvent>() {

            @Override
            public void handle(StarExplorerEvent event) {
                root.getScene().setCursor(Cursor.DEFAULT);
            }
            
        });

        root.addEventHandler(StarExplorerEvent.API_UPDATED, new EventHandler<StarExplorerEvent>() {

            @Override
            public void handle(StarExplorerEvent event) {
                updateApiInfo();
            }
            
        });
    }
    
    void updateApiInfo() {
        
        ApiInfoLoader loader = ApiInfoLoader.getInstance();
        for (StarInfo si : starsIndexByName.values()) {
            
            ApiSystemSovereignty sov = loader.sovereignty.getInfo().get((int)si.id);
            si.updateSovInfo(new SovInfo(sov));
            
        }
        
        Sovereignty sov = ApiInfoLoader.getInstance().sovereignty;
        AllianceList al = ApiInfoLoader.getInstance().alliances;
        assert sov != null;
        assert al != null;
        
        al.updateSovList(starsIndexByName);
        
        sPanel.apiUpdated();
        
    }
    
    public ControlPanelController getCPanel() {
        return cPanel;
    }
    
    SettingsPanelController getSPanel() {
        return sPanel;
    }

    int starsCount() {
        return worldStars.size();
    }
    
    final Collection<Star> getStars() {
        return worldStars.values();
    }

    final Collection<Lane> getLanes() {
        return worldLanes.values();
    }
    
    Star findStar(String name) {
        return worldStars.get(name.toUpperCase());
    }
    
    Valid isStarValid(String name) {
        
        Valid res = Valid.BAD;
        String n = name.toUpperCase();
        for (Star s : getStars()) {
            
            if (s.info.name.toUpperCase().compareTo(n) == 0) {
                return Valid.FULL;
            }
            
            if (s.info.name.toUpperCase().startsWith(n)) {
                res = Valid.PARTIAL;
            }
        }
        
        return res;
    }
    
    boolean starExists(String name) {
        return starsIndexByName.containsKey(name.toUpperCase());
    }
    
    static long searchCount = 0;
    static long tooLongPathCount = 0;
    static long pathFoundCount = 0;
    static long shortPathExistsCount = 0;

    void raiseStars() {
        for (Star s : worldStars.values()) {
            s.toFront();
        }
    }
    
    protected class CompareByDistanceToGoal implements Comparator {

        final StarInfo goal;

        public CompareByDistanceToGoal(StarInfo goal) {
            this.goal = goal;
        }
        
        
        @Override
        public int compare(Object t1, Object t2) {
            double d1 = ((StarInfo) t1).distanceTo(goal);
            double d2 = ((StarInfo) t2).distanceTo(goal);
            
            if (d1 < d2) {
                return -1;
            }
            else if (d1 > d2) {
                return 1;
            }
            else {
                return 0;
            }
        }

    }
    
    class CompareByGateDistance implements Comparator {

        @Override
        public int compare(Object t1, Object t2) {
            
            long d1 = ((StarInfo) t1).currentDistance;
            long d2 = ((StarInfo) t2).currentDistance;
            
            if (d1 > d2) {
                return 1;
            }
            else if (d1 < d2) {
                return -1;
            }
            else {
                return Long.compare(System.identityHashCode(t1),System.identityHashCode(t2));
            }
        }
        
    }
    
    public List<String> findPath(String from, String dest) {
        
        CompareByGateDistance cmp = new CompareByGateDistance();
        SortedSet<StarInfo> unchecked = new TreeSet<>(cmp);
        
        StarInfo siFrom = starsIndexByName.get(from);
        StarInfo siDest = starsIndexByName.get(dest);
        
        boolean zsIsOk = cPanel.gate0S_prefs.isSelected();
        boolean lsIsOk = cPanel.gateLS_prefs.isSelected();
        boolean hsIsOk = cPanel.gateHS_prefs.isSelected();
        
        boolean avoidAmarr = cPanel.gateAvoidAmarr.isSelected();
        boolean avoidCaldari = cPanel.gateAvoidCaldari.isSelected();
        boolean avoidGallente = cPanel.gateAvoidGallente.isSelected();
        boolean avoidMinmatar = cPanel.gateAvoidMinmatar.isSelected();
        
        for (StarInfo si: starsIndexByName.values()) {
            if (si == siFrom) {
                si.currentDistance = 0;
            }
            else {
                si.currentDistance = Long.MAX_VALUE;
            }
            si.isSeenFlag = false;
            unchecked.add(si);
        }
        
        while (!unchecked.isEmpty()) {

            StarInfo curr = unchecked.first();
            unchecked.remove(curr);
            curr.isSeenFlag = true;
            
            
            for (StarInfo neig : curr.getNeigbors()) {
                
                if (!neig.isSeenFlag) {
                    
                    long distCost;

                    if (neig.ss >= 0.45)    { distCost = (hsIsOk) ? 1 : 100000; }
                    else if (neig.ss > 0.0) { distCost = (lsIsOk) ? 1 : 100000; }
                    else                    { distCost = (zsIsOk) ? 1 : 100000; }
                    
                    if (!neig.getSovInfo().isClaimable()) {
                        long faction = neig.getSovInfo().getFactionID();
                        
                        distCost += (avoidAmarr && faction == Factions.getAmarrID()) ? 10000 : 0;
                        distCost += (avoidCaldari && faction == Factions.getCaldariID()) ? 10000 : 0;
                        distCost += (avoidGallente && faction == Factions.getGallenteID()) ? 10000 : 0;
                        distCost += (avoidMinmatar && faction == Factions.getMimatarID()) ? 10000 : 0;
                    }

                    if (neig.currentDistance > curr.currentDistance + distCost) {

                        unchecked.remove(neig);
                        neig.currentDistance = curr.currentDistance + distCost;
                        unchecked.add(neig);

                    }

                }
                
            }
        }
        
        List<String> path = new ArrayList<>();
        path.add(siDest.name);
        
        while (siDest.currentDistance != 0) {

            StarInfo siClosest = null;
            for (StarInfo si : siDest.getNeigbors()) {
                if (siClosest == null || siClosest.currentDistance > si.currentDistance) {
                    siClosest = si;
                }
            }
            
            path.add(0,siClosest.name);
            siDest = siClosest;
            
        }
        
        return path;
        
    }
    
    private void doSetScale(PathHighlighter phl, double scale) {
        
        for (Node n : phl.getChildren()) {
            
            String nClass = n.getClass().getName();
            
            if (nClass.compareTo(Path.class.getName()) == 0) {
                Path p = (Path) n;

                //System.out.println("Scale PATH!!!");
                for (PathElement e : p.getElements()) {
                    
                    String eClass = e.getClass().getName();
                    
                    if (eClass.compareTo(PathHighlighter.PMoveTo.class.getName())==0) {
                        PathHighlighter.PMoveTo m = (PathHighlighter.PMoveTo) e;
                        m.setX(m.getStarX() * scale);
                        m.setY(m.getStarY() * scale);
                    } else if (eClass.compareTo(PathHighlighter.PLineTo.class.getName())==0) {
                        PathHighlighter.PLineTo l = (PathHighlighter.PLineTo) e;
                        l.setX(l.getStarX() * scale);
                        l.setY(l.getStarY() * scale);
                    }
                    else {
                        throw new Error("Unimplemented scale for Path element: " + eClass);
                    }
                    
                }
            }
            else if (nClass.compareTo(Circle.class.getName()) == 0) {
                // skip circle
                //Circle c = (Circle) n;
            }
            else {
                throw new Error("Unknown PathHighlighte element: " + nClass);
            }
        }
        
    }
    
    private void doSetScale(Group group, double scale) {
        for (Node n : group.getChildren()) {
            
            String nClass = n.getClass().getName();
            
            if (nClass.compareTo(Group.class.getName()) == 0) {
                doSetScale((Group) n, scale);
            }
            else if (nClass.compareTo(PathHighlighter.class.getName()) == 0) {
                PathHighlighter p = (PathHighlighter) n;
                doSetScale(p, scale);
            }
            else if (nClass.compareTo(Star.class.getName()) == 0) {
                Star s = (Star) n;
                s.setTranslateX((s.info.x) * scale);
                s.setTranslateY((s.info.y) * scale);
            }
            else if (nClass.compareTo(Lane.class.getName()) == 0) {
                Lane l = (Lane) n;

                StarInfo s1 = findStar(l.info.gate1).info;
                StarInfo s2 = findStar(l.info.gate2).info;

                l.setStartX(s1.x * scale);
                l.setStartY(s1.y * scale);
                l.setEndX(s2.x * scale);
                l.setEndY(s2.y * scale);
            }
            else {
                System.out.println("Scale: " + nClass);
            }
        }
    }
    
    void setScale(double scale) {
        
        if (ph != null) {
            ph.stop();
        }
        currentScale = scale;
        doSetScale(rootScene, scale);
        if (ph != null) {
            ph.play();
        }
        
        cPanel.dbgScale.setText("" + scale);
        
    }
    
    public void addStar(String s) {
        StarInfo si = new StarInfo(s);
        
        // skip Jovian regions
        String r = "[A-Z0-9]+\\-[A-Z0-9]+";
        if (si.region.matches(r)) {
            return;
        }
        
        starsIndexByName.put(si.name.toUpperCase(), si);
        starsIndexById.put(si.id, si);
        worldStars.put(si.name.toUpperCase(), new Star(si, this));
        
        minX = Math.min(minX, si.x);
        minY = Math.min(minY, si.y);
        maxX = Math.max(maxX, si.x);
        maxY = Math.max(maxY, si.y);
    }
    
    public void addStarObject(String s) {
        SolarSystemObject so = new SolarSystemObject(s);
        
        StarInfo si = starsIndexById.get(so.systemId);
        // мы могли найти систему Джовов, пропускаем ее
        if (si != null) {
            si.addStarObject(so);
        }
        
    }
    
    public void addLane(String s) {
        LaneInfo li = new LaneInfo(s);
        
        Star s1 = findStar(li.gate1);
        Star s2 = findStar(li.gate2);

        if (s1 == null || s2 == null || lanesIndex.containsKey(li.id)) 
        {
            return;
        }
        
        Lane lane = new Lane(li, this);
        worldLanes.put(lane.getLaneId(),lane);
        lanesIndex.put(li.id, li);
        
        s1.addGate(s2.info);
        s2.addGate(s1.info);
        
    }
    
}
