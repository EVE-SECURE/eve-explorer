/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evestarexplorer.gui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author gyv
 */
public class LoadingSplashController implements Initializable {

    @FXML Label taskName;
    @FXML TextArea info;
    @FXML ProgressBar progress;
    
    private BooleanProperty taskIsRunning;
    private Stage stage;
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    public void show() {
        stage.show();
        stage.toFront();
    }
    
    public void hide() {
        stage.hide();
    }
    
    public void start(final Task<Void> task) {
        progress.progressProperty().bind(task.progressProperty());
        info.textProperty().bind(task.messageProperty());
        taskIsRunning = new SimpleBooleanProperty();
        
        taskIsRunning.bind(task.runningProperty());
        // NOTE: В документации есть странная фраза про WeakChangeListener
        // однако он не подошел. Мне кажется, что все и так должно быть нормально.
        taskIsRunning.addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue==false) {
                    stage.hide();
                }
            }
            
        });
        
        task.setOnFailed(new EventHandler<WorkerStateEvent>(){

            @Override
            @SuppressWarnings("CallToThreadDumpStack")
            public void handle(WorkerStateEvent event) {
                System.out.println("Task failed!");
                Throwable ex = task.getException();
                if (ex != null) {
                    ex.printStackTrace();
                }
            }
        });

        
        show();
        new Thread(task).start();
    }
    
    public boolean taskIsRunning() {
        return this.taskIsRunning.get();
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        evestarexplorer.EveStarExplorer.splashPanel = this;
    }
}
