/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.controller;

import intCount.Global;
import intCount.model.UserPreferences;
import intCount.utility.TabContent;
import intCount.utility.Utility;

import java.util.Optional;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;



/**
 * FXML Controller class
 *
 * @author Hicham ALAOUI RIZQ
 */
public class UserPreferencesController implements TabContent {

    @FXML
    private CheckBox chkPrintOnSave;
    @FXML
    private RadioButton rdPrintPreview;
    @FXML
    private RadioButton rdPrintDirectly;
    @FXML
    private CheckBox chkPrintDialogFirst;
    @FXML
    private CheckBox chkAutoLoadLastActiveYear;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnClose;
    
    private final BooleanProperty isDirty = new SimpleBooleanProperty(false);
    private Stage mainWindow;
    private TabPane tabPane;
    
    public void initialize() {
        chkPrintDialogFirst.managedProperty().bind(
                chkPrintDialogFirst.visibleProperty());
        chkPrintDialogFirst.visibleProperty().bind(
                rdPrintDirectly.selectedProperty());
        
        btnSave.disableProperty().bind(isDirty.not());
        
        rdPrintPreview.selectedProperty().addListener((Observable observable) -> {
            isDirty.set(true);
        });
        
        rdPrintDirectly.selectedProperty().addListener((Observable observable) -> {
            isDirty.set(true);
        });
        
         chkPrintDialogFirst.selectedProperty().addListener((Observable observable) -> {
            isDirty.set(true);
        });
         
          chkPrintOnSave.selectedProperty().addListener((Observable observable) -> {
            isDirty.set(true);
        });
          
           chkAutoLoadLastActiveYear.selectedProperty().addListener((Observable observable) -> {
            isDirty.set(true);
        });
         
        
    }    

    @Override
    public boolean shouldClose() {
        if (isDirty.get()) {
            ButtonType response = shouldSaveUnsavedData();
            if (response == ButtonType.CANCEL) {
                return false;
            } else if (response == ButtonType.YES) {
                return savePreferences();
            } 
        }
        
        return true;
    }

    @Override
    public void putFocusOnNode() {
        chkPrintOnSave.requestFocus();
    }

    @Override
    public boolean loadData() {
        UserPreferences preferences = Global.getUserPreferences();
        
        if (preferences.getPrintOnSave()) {
            chkPrintOnSave.setSelected(true);
        }
        
        if (preferences.getShowPrintPreview()) {
            rdPrintPreview.setSelected(true);
        } else {
            rdPrintDirectly.setSelected(true);
            if (preferences.getShowPrintDialog()) {
                chkPrintDialogFirst.setSelected(true);
            }
        }
        
        chkAutoLoadLastActiveYear.setSelected(
                preferences.getAutoOpenLastOpenedYear());
        
        isDirty.set(false);
        return true;
    }

    @Override
    public void setMainWindow(Stage stage) {
        mainWindow = stage;
    }

    @Override
    public void setTabPane(TabPane tabPane) {
        this.tabPane = tabPane;
    }
    
    @FXML
    private void onCloseTabAction(ActionEvent event) {
        
        if (shouldClose()) {
             Tab currenTab = tabPane.getSelectionModel().getSelectedItem();
            tabPane.getTabs().remove(currenTab);
        }
        
    }
    
    @FXML
    private void onSavePreferencesAction(ActionEvent event) {
        
        if (savePreferences()) {
            isDirty.set(false);
            btnClose.fire();
        }
        
    }
    
    private boolean savePreferences() {
        
        UserPreferences preferences = collateUserPreferences();
        
        if (!Global.setUserPreferences(preferences)) {
        	String message = "An error occurred while saving user preferences." +
                    "\nPlease make sure you are using this app with " +
                    "Administrative privileges.";
            
            Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
            alert.setTitle("Erreur occured");
            alert.setHeaderText("Error saving preferences");
            alert.initOwner(mainWindow);
            Utility.beep();
              Global.styleAlertDialog(alert);
            alert.showAndWait();
            return false;
        }
        
        return true;
        
    }
    
    private UserPreferences collateUserPreferences() {
        
        UserPreferences preferences = new UserPreferences();
        
        boolean value = chkPrintOnSave.isSelected();
        preferences.setPrintOnSave(value);
        
        value = rdPrintPreview.isSelected();
        preferences.setShowPrintPreview(value);
        
        if (!value) { //if print preview is not selected
            value = chkPrintDialogFirst.isSelected(); //show print option dialog before printing the invoice
            preferences.setShowPrintDialog(value);
        }
        
        preferences.setAutoOpenLastOpenedYear(chkAutoLoadLastActiveYear.isSelected());
        
        return preferences;
        
    }
    
    private ButtonType shouldSaveUnsavedData() {
    	final String message = "Preferences have not been saved." +
                "\nSave preferences before closing this tab?";
        
        final Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message,
                ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.setTitle("Unsaved preferences");
        alert.setHeaderText("Save unsaved preferences now?");
        alert.initOwner(mainWindow);
         Global.styleAlertDialog(alert);
        Optional<ButtonType> response = alert.showAndWait();
        
        if (!response.isPresent()) {
            return ButtonType.CANCEL;
        }
        
        return response.get();
    }
    
}
