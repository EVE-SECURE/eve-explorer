/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer;

import com.beimin.eveapi.eve.conquerablestationlist.ApiStation;
import java.util.Random;

/**
 *
 * @author gyv
 */
public class ConquerableObject extends SolarSystemObject {

    private final static double PLANET_DISTANCE = 5_000_000.0;
    
    public ConquerableObject(ApiStation station) throws Exception {
        super();
        
        setId(station.getStationID());
        setSystemId(station.getSolarSystemID());
        setType(Type.STATION);
        setName(station.getStationName());
        
        String delimiter = " - ";
        String name = getName().split(delimiter)[0];
//        // если не добавить разделитель, то можно перепутать ABC I и ABC IV
//        name += delimiter;
        
        StarInfo si = EveStarExplorer.world.starsIndexById.get(getSystemId());
        if (si == null) {
            throw new Exception("Jove system found");
        }
        
        SolarSystemObject planet = null;
        for (SolarSystemObject so : si.getStarObjects()) {
            
            if (so.getType() == SolarSystemObject.Type.PLANET) { 
                
                if (so.getName().compareTo(name) == 0) {
                    planet = so;
                    break;
                }
                
            }
                
        }
        assert planet != null : name + " in " + getSystemId();
        
        /*
         * Разместим станцию в псевдослучайной координате неподалеку от планеты
         * на расстоянии PLANET_DISTANCE - 2*PLANET_DISTANCE
         */
        Random rnd = new Random(getSystemId());
        setX(planet.getX() + PLANET_DISTANCE * (rnd.nextDouble() + 1));
        setY(planet.getY() + PLANET_DISTANCE * (rnd.nextDouble() + 1));
        setZ(planet.getZ() + PLANET_DISTANCE * (rnd.nextDouble() + 1));
    }
    
}
