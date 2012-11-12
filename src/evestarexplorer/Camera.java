/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer;

import javafx.scene.Group;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

/**
 *
 * @author gyv
 */
class Camera extends Group {
    Translate t = new Translate();
    Translate p = new Translate();
    Translate ip = new Translate();
    Rotate rx = new Rotate();
    Rotate ry = new Rotate();
    Rotate rz = new Rotate();
    Scale s = new Scale();
    double x = 0;
    double y = 0;
    {
        rx.setAxis(Rotate.X_AXIS);
        ry.setAxis(Rotate.Y_AXIS);
        rz.setAxis(Rotate.Z_AXIS);
    }

    public Camera() {
        super();
        getTransforms().addAll(t, p, rx, rz, ry, s, ip);
    }

    void setCamPos(double posX, double posY) {
        x = posX;
        y = posY;
        //setTranslateX(posX);
        //setTranslateY(posY);
        t.setX(posX);
        t.setY(posY);
        EveStarExplorer.ctrlPanel.dbgMouseX.setText(Double.toString(posX));
        EveStarExplorer.ctrlPanel.dbgMouseY.setText(Double.toString(posY));
    }

    void moveCamPos(double deltaX, double deltaY) {
        setCamPos(x + deltaX, y + deltaY);
    }
    
}
