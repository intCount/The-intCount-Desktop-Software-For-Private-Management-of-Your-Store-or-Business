/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.model;


/**
 * @author
 */
public class UserPreferences {

    public static final String KEY_APP_DATA_FOLDER = "App Data Folder";
    public static final String KEY_PRINT_ON_SAVE = "Print on Save";
    public static final String KEY_SHOW_PRINT_PREVIEW = "Show Print Preview";
    public static final String KEY_SHOW_PRINT_DIALOG = "Show Print Dialog";
    public static final String KEY_AUTO_LOAD_LAST_OPENED_YEAR = "Auto Load Last Opened Year";
    public static final String KEY_LAST_OPENED_YEAR = "Last Opened Year";
    public static final String KEY_IS_WINDOW_MAXIMIZED = "Is Window Maximized";
    public static final String KEY_XPOS = "X Pos";
    public static final String KEY_YPOS = "Y Pos";
    public static final String KEY_WIDTH = "Width";
    public static final String KEY_HEIGHT = "Height";
    public static final String KEY_LAST_BACKUP_LOCATION = "Last Backup Location";

    private boolean printOnSave = true;
    private boolean showPrintPreview = true;
    private boolean showPrintDialog = true;
    private boolean autoOpenLastOpenedYear = true;

    public boolean getPrintOnSave() {
        return printOnSave;
    }

    public void setPrintOnSave(boolean value) {
        printOnSave = value;
    }

    public boolean getShowPrintPreview() {
        return showPrintPreview;
    }

    public void setShowPrintPreview(boolean value) {
        showPrintPreview = value;
    }

    public boolean getShowPrintDialog() {
        return showPrintDialog;
    }

    public void setShowPrintDialog(boolean value) {
        showPrintDialog = value;
    }

    public boolean getAutoOpenLastOpenedYear() {
        return autoOpenLastOpenedYear;
    }

    public void setAutoOpenLastOpenedYear(boolean value) {
        autoOpenLastOpenedYear = value;
    }


}
