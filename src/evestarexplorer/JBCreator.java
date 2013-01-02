/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer;

/**
 *
 * @author g_yaltchik
 */
public class JBCreator {
    
    private boolean inProgress = false;
    
    private StarInfo start;
    private StarInfo end;
    
    public JBInfo build() {
        if (start == null && end == null) {
            return null;
        }
        
        return new JBInfo(start, end);
    }
    
    public void reset() {
        start = null;
        end = null;
        inProgress = false;
    }
    
    boolean addStar(StarInfo s) {
        if (!inProgress) {
            start = s;
            inProgress = true;
            return false;
        }
        else {
            end = s;
            inProgress = false;
            return true;
        }
    }
}
