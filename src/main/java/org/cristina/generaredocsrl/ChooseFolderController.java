package org.cristina.generaredocsrl;
import org.cristina.generaredocsrl.Utility;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import org.cristina.generaredocsrl.connection.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ChooseFolderController {

    @FXML
    private Button backButton;
    @FXML
    private ChoiceBox<String> srlChoiceBox;

    private static final String SELECT_SRL = "SELECT name FROM srl WHERE person_id = ?";

    private ObservableList<String> srlList = FXCollections.observableArrayList();

    public void initialize() {
        populateSrlChoiceBox();
    }

    private void populateSrlChoiceBox() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        try{
            PreparedStatement preparedStatement = connectDB.prepareStatement(SELECT_SRL);
            preparedStatement.setInt(1,1);

            ResultSet queryResult = preparedStatement.executeQuery();

            while(queryResult.next()){
                String name = queryResult.getString("name");
                srlList.add(name);
            }

            srlChoiceBox.setItems(srlList);

        } catch(Exception e){
            e.printStackTrace();
            Utility.showAlert(Alert.AlertType.ERROR, "Eroare", "Eroare la încărcarea SRL-urilor");
        }
    }

    public void backButtonOnAction(ActionEvent e) {
        Utility.navigateToView("clientMenu-view.fxml", backButton);
    }
}
