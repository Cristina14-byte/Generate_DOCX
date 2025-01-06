package org.cristina.generaredocsrl;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Utility {

    public static void navigateToView(String fxmlFile, Button button){
        try{
            Stage stage = (Stage) button.getScene().getWindow();
            Parent root = FXMLLoader.load(Utility.class.getResource(fxmlFile));
            stage.getScene().setRoot(root);

        } catch(Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Eroare", "");
        }
    }

    public static void showAlert(Alert.AlertType alertType, String title, String content){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
