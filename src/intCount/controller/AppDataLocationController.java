
 
package intCount.controller;

import intCount.Global;
import intCount.database.Database;
import intCount.model.FinancialYear;
import intCount.utility.*;

import java.io.File;
import java.util.Optional;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.stage.DirectoryChooser;

/**
 * FXML Controller class
 *
 * @author Hicham ALAOUI RIZQ
 */
public class AppDataLocationController implements TabContent {

    private Stage mainWindow;
    private TabPane tabPane;
    private final BooleanProperty isDirty = new SimpleBooleanProperty(false);

    @FXML
    private TextField tfFolderPath;
    @FXML
    private Button btnBrowseForFolder;
    @FXML
    private Button btnSave;
    @FXML
    private CheckBox chkMoveDatabases;

    @Override
    public boolean shouldClose() {
       if (!isDirty.get()) {
           return true;
       }
       
       ButtonType response = shouldSaveUnsavedData();
       if (response == ButtonType.CANCEL) {
           return false;
       } else if (response == ButtonType.NO) {
           return true;
       } else return saveData();
    }

    @Override
    public void putFocusOnNode() {
        btnBrowseForFolder.requestFocus();
    }

    @Override
    public boolean loadData() {
        String path = Global.getAppDataPath();
        tfFolderPath.setText(path);
        return true;
    }

    @Override
    public void setMainWindow(Stage stage) {
        mainWindow = stage;
    }

    @Override
    public void setTabPane(TabPane pane) {
        this.tabPane = pane;
    }

    public void initialize() {
        btnSave.disableProperty().bind(isDirty.not());
        tfFolderPath.getTooltip().textProperty().bind(tfFolderPath.textProperty());
        chkMoveDatabases.managedProperty().bind(chkMoveDatabases.visibleProperty());
    }

    @FXML
    private void onBrowseForFolderAction(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select the folder to store application data");
        chooser.setInitialDirectory(new File(tfFolderPath.getText()));
        File file = chooser.showDialog(mainWindow);
        if (file != null) {
            String path = file.getAbsolutePath();
            tfFolderPath.setText(path);
            chkMoveDatabases.setVisible(true);
            isDirty.set(true);
        }
    }
    
    @FXML
    private void onSaveLocationAction(ActionEvent event) {
        boolean isSuccess = saveData();
        
        if (isSuccess) {
            isDirty.set(false);
            
            if (chkMoveDatabases.isSelected()) {
                final String message = "Existing databases, if any, " + 
                        "have been successfully moved to the new location !!";
                final Alert alert = new Alert(Alert.AlertType.INFORMATION, 
                        message, ButtonType.OK);
                alert.initOwner(mainWindow);
                alert.setTitle("Success");
                alert.setHeaderText("Databases successfully moved to new location !");
                Global.styleAlertDialog(alert);
                alert.showAndWait();
            }
        }
    }
    
    @FXML
    private void onCloseTabAction(ActionEvent event) {
        if (shouldClose()) {
             Tab tab = tabPane.getSelectionModel().getSelectedItem();
            tabPane.getTabs().remove(tab);
        }
    }
    
    private boolean saveNewDataLocation(final String location) {
         boolean saved = Global.saveAppDataFolderPath(location);
         
        if (!saved) {
            Alert alert = new Alert(Alert.AlertType.ERROR, 
                    "An error occurred while saving the new data location !",
                    ButtonType.OK);
            alert.setTitle("Error Occurred");
            alert.setHeaderText("Error saving new data location !");
            alert.initOwner(mainWindow);
            Utility.beep();
             Global.styleAlertDialog(alert);
            alert.showAndWait();
        }
        
        return saved;
    }
    
    private ButtonType shouldSaveUnsavedData() {
        String message = "The new data path of the application has not yet been saved.."  
                + System.lineSeparator() + 
                "Do you want to save it before closing this tabt?";
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message,
                ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.setTitle("New unsaved path");
        alert.setHeaderText("Save the new path before closing this tab?");
        alert.initOwner(mainWindow);
         Global.styleAlertDialog(alert);
        
        Optional<ButtonType> response = alert.showAndWait();
        if (! response.isPresent()) {
            return  ButtonType.CANCEL;
        }
        
        return response.get();
    }
    
    private boolean saveData() {
        
        //user opted to move the existing databases to the new location
        if (chkMoveDatabases.isSelected()) { 
            final FinancialYear year = Global.getActiveFinancialYear();
            if (year != null && tabPane.getTabs().size() > 1) {
                final Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
                if (!Global.closeTabs(tabPane, currentTab)) {
                    return false;
                }
            }
            if (year != null) {
                Database.shutDownActiveYearDatabase();
            }
        }
        
        final String newDataLocation = tfFolderPath.getText();
        final String currentDataLocation = Global.getAppDataPath();
        
        if (!saveNewDataLocation(newDataLocation)) {
            return false;
        }
        
        //user opted to move the existing databases to the new location
        if (chkMoveDatabases.isSelected()) { 
            if (! Global.moveDatabases(currentDataLocation, newDataLocation)) {
                final String message = "An error occurred while moving the " +
                        "databases from current location to new location !"
                        +"\n\n Try to choose another folder " +
                        "pour stocker des bases de données.";
                final Alert alert = new Alert(Alert.AlertType.ERROR, message, 
                        ButtonType.OK);
                alert.initOwner(mainWindow);
                alert.setTitle("Database move error");
                alert.setHeaderText("Error moving database(s) to new location !");
                Utility.beep();
                Global.styleAlertDialog(alert);
                alert.showAndWait();
                
                //restore the old database location
                saveNewDataLocation(currentDataLocation);
                return false;
            }
        }
        
        return true;
    }

}
