/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author g_yaltchik
 */
class StarInfoList extends ArrayList<StarInfo> {

    List<String> toStrings() {
        List<String> res = new ArrayList<>();
        for (StarInfo si : this) {
            res.add(si.name);
        }
        return res;
    }
    
}
