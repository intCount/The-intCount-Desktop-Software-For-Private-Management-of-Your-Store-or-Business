package intCount.controller;


import intCount.utility.TabContent;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import javafx.event.ActionEvent;


public class NoteController implements TabContent {

	 @SuppressWarnings("unused")
	private Stage mainWindow;
	    @SuppressWarnings("unused")
		private TabPane tabPane;
	    private FileChooser fileChooser = new FileChooser();
	    private File file;
	    
	    @FXML
	    private TextArea textpane;
	    
	    
	@Override
	public boolean shouldClose() {
		// TODO Auto-generated method stub
		
		return true;
	}

	@Override
	public void putFocusOnNode() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean loadData() {
		// TODO Auto-generated method stub
		return loadNotePad();
	}

	@Override
	public void setMainWindow(Stage stage) {
		mainWindow = stage;
	}

	@Override
	public void setTabPane(TabPane tabPane) {
		tabPane = this.tabPane;
	}
	
	public boolean loadNotePad(){
		return true;
	}
	
	@FXML
	 protected void newFile(ActionEvent event) {
		 textpane.clear();
		 @SuppressWarnings("unused")
		Stage stage = (Stage) textpane.getScene().getWindow();
		 //stage.setTitle("Untitled - Notepad");
		 file = null;
	 }
	 
	 @FXML
	 protected void openFile(ActionEvent event) {
		 file = fileChooser.showOpenDialog(null);
		 if (file != null) {
		  Stage stage = (Stage) textpane.getScene().getWindow();
		  stage.setTitle(file.getName() + " - Notepad");
		  BufferedReader br = null;
		  try {
		   String sCurrentLine;
		   br = new BufferedReader(new FileReader(file));
		    while ((sCurrentLine = br.readLine()) != null) {
		    textpane.appendText(sCurrentLine + "\n");
		   }
		  } catch (Exception e) {
		   e.printStackTrace();
		  }
		 }
	 }
	 
	 @FXML
	 protected void saveFile(ActionEvent event) {
		 String content = textpane.getText();
		 if (file != null) {
		  try {
		   // if file doesnt exists, then create it
		   if (!file.exists()) {
		    file.createNewFile();
		   }
		   FileWriter fw = new FileWriter(file.getAbsoluteFile());
		   BufferedWriter bw = new BufferedWriter(fw);
		   bw.write(content);
		   bw.close();
		  } catch (Exception e) {
		   e.printStackTrace();
		  }
		 } else {
		  // open a file dialog box
		  file = fileChooser.showSaveDialog(null);
		  if (file != null) {
		  @SuppressWarnings("unused")
		Stage stage = (Stage) textpane.getScene().getWindow();
		  //stage.setTitle(file.getName() + " - Notepad");
		  try {
		   // if file doesnt exists, then create it
		   if (!file.exists()) {
		    file.createNewFile();
		   }
		   FileWriter fw = new FileWriter(file.getAbsoluteFile());
		   BufferedWriter bw = new BufferedWriter(fw);
		   bw.write(content);
		   bw.close();
		   } catch (Exception e) {
		    e.printStackTrace();
		   }
		  }
		 }
	 }
}