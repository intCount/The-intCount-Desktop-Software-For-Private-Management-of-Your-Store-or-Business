package intCount.controller;

import intCount.utility.TabContent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class HardwareViewerController implements TabContent {

	
	    @FXML private TextField tfHostname;
	    @FXML private Label lblHostname;
	    
	    @SuppressWarnings("unused")
		private Stage mainWindow;
	    @SuppressWarnings("unused")
		private TabPane tabPane;
		@Override
		public boolean shouldClose() {
			// TODO Auto-generated method stub
			return false;
		}
		@Override
		public void putFocusOnNode() {
			// TODO Auto-generated method stub
			
		}
		@Override
		public boolean loadData() {
			// TODO Auto-generated method stub
			return false;
		}
		@Override
		public void setMainWindow(Stage stage) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void setTabPane(TabPane tabPane) {
			// TODO Auto-generated method stub
			 this.tabPane = tabPane;
		}
	    
}
