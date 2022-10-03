/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.controller;

import intCount.Global;
import intCount.model.ApplicationAttributes;
import intCount.utility.TabContent;
import intCount.utility.Utility;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Hicham ALAOUI RIZQ
 */
public class AboutController implements TabContent {

    private Stage mainWindow;
    private TabPane tabPane;

    @FXML
    private Button btnClose;
    @FXML
    private Text txtTitle;
    @FXML
    private Text txtVersion;

    @FXML
    private Label lblDeveloper;
    @FXML
    private Label lblLocation;
    @FXML
    private Label lblMobileNumber;
    @FXML
    private Hyperlink lnkEmailAddress;
    @FXML
    private Hyperlink lnkBlogURL;
    @FXML
    private Hyperlink lnkLocation;

    @Override
    public boolean shouldClose() {
        return true;
    }

    @Override
    public void putFocusOnNode() {
        btnClose.requestFocus();
    }

    @Override
    public boolean loadData() {
        ApplicationAttributes attributes = null;

        try {
            attributes = Global.getApplicationAttributes();
        } catch (Exception e) {
            final String message = "An error occurred while retrieving application information."
                    + "\n Sorry for the inconvenience!";
            final Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
            alert.setTitle("Error Occurred");
            alert.setHeaderText("Error reading application attributes ");
            alert.initOwner(mainWindow);
            Global.styleAlertDialog(alert);
            Utility.beep();
            alert.showAndWait();
            return false;
        }

        
        return true;
    }

    @Override
    public void setMainWindow(Stage stage) {
        mainWindow = stage;
    }

    @Override
    public void setTabPane(TabPane pane) {
        tabPane = pane;
    }

    private void populateControls(ApplicationAttributes attributes) {
         txtTitle.setText("intCount Facturation");
         txtVersion.setText("Version " + "1.0");
         
         lblDeveloper.setText(attributes.getDeveloperName());
         lnkLocation.setText(attributes.getDeveloperLocation());
         lblMobileNumber.setText(attributes.getDeveloperMobileNumber());
         lnkEmailAddress.setText(attributes.getDeveloperEmailAddress());
         lnkBlogURL.setText(attributes.getDeveloperBlogURL());
    }

    @FXML
    private void onCloseTabAction(ActionEvent event) {
        final Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
        tabPane.getTabs().remove(currentTab);
    }
    
    @FXML
    private void onSendEmailAction(ActionEvent event) {
        final HostServices hostServices = (HostServices)
                mainWindow.getProperties().get("hostServices");
        hostServices.showDocument("mailto:" + lnkEmailAddress.getText());
    }
    
    @FXML
    private void onVisitURLAction(ActionEvent event) {
         final HostServices hostServices = (HostServices)
                mainWindow.getProperties().get("hostServices");
        hostServices.showDocument(lnkBlogURL.getText());
    }
    
    @FXML
    private void onLocationVisitAction(ActionEvent event) {
      final String baseURL =  "https://www.google.com/maps/place/";
      final HostServices hostServices = (HostServices)
                mainWindow.getProperties().get("hostServices");
        String url = baseURL + lnkLocation.getText();
        hostServices.showDocument(url);
    }
}
