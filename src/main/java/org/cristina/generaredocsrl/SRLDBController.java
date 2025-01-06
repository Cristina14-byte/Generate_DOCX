package org.cristina.generaredocsrl;
import org.cristina.generaredocsrl.Utility;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.cristina.generaredocsrl.connection.DatabaseConnection;
import org.cristina.generaredocsrl.model.srl;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


public class SRLDBController {
    @FXML
    private ChoiceBox<String> usernameChoiceBox;
    @FXML
    private TableView<srl> srlTable;
    @FXML
    private TableColumn<srl, String> srlColumn;
    @FXML
    private Button backButton;

    private static final String USERNAME_SELECT = "SELECT username FROM person WHERE priority = 'client'";
    private static final String SELECT_SRL = "SELECT srl.name FROM srl INNER JOIN person ON srl.person_id = person.person_id WHERE person.username = ?;";
    private static final String DELETE_SRL = "DELETE FROM srl WHERE name = ?";

    private ObservableList<srl> srlList = FXCollections.observableArrayList();
    private ObservableList<String> usernameList = FXCollections.observableArrayList();

    public void initialize() {
        ChoiceBoxOnAction();
        srlColumn.setCellValueFactory(new PropertyValueFactory<>("SRL"));
        usernameChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null) {
                loadSRLsForUser((newValue));
            }
        });
    }

    public void ChoiceBoxOnAction(){
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        try{
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(USERNAME_SELECT);

            while(queryResult.next()){
                String username = queryResult.getString("username");
                usernameList.add(username);
            }

            usernameChoiceBox.setItems(usernameList);

        } catch(Exception e){
            e.printStackTrace();
            Utility.showAlert(Alert.AlertType.ERROR, "Eroare", "Eroare la încărcarea utilizatorilor");
        }
    }

    private void loadSRLsForUser(String username) {
        srlList.clear();

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        try{
            PreparedStatement statement = connectDB.prepareStatement(SELECT_SRL);
            statement.setString(1, username);

            ResultSet queryResult = statement.executeQuery();
            while(queryResult.next()){
                String name = queryResult.getString("name");

                srlList.add(new srl(name));
            }
            srlTable.setItems(srlList);

        } catch(Exception e){
            e.printStackTrace();
            Utility.showAlert(Alert.AlertType.ERROR, "Eroare", "Eroare la încărcarea SRL-urilor");
        }
    }

    public void addSRLOnAction() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("addSRL-view.fxml"));

            Stage registerStage = new Stage();
            registerStage.setTitle("Adaugă SRL");
            registerStage.setScene(new Scene(root));
            registerStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            Utility.showAlert(Alert.AlertType.ERROR, "Eroare", "Eroare la adăugarea unui nou SRL");
        }
    }

    public void refreshButtonOnAction(ActionEvent event) {
        srlList.clear();
        loadSRLsForUser(usernameChoiceBox.getSelectionModel().getSelectedItem());
        srlTable.refresh();
    }

    public void deleteButtonOnAction(ActionEvent event) {
        srl selectedSRL = srlTable.getSelectionModel().getSelectedItem();

        if(selectedSRL != null){
            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDB = connectNow.getConnection();

            try{
                PreparedStatement statement = connectDB.prepareStatement(DELETE_SRL);
                statement.setString(1, selectedSRL.getSRL());

                int rowsDeleted = statement.executeUpdate();
                if(rowsDeleted > 0){
                    srlList.remove(selectedSRL);
                }
            } catch(Exception e){
                e.printStackTrace();
                Utility.showAlert(Alert.AlertType.ERROR,"Eroare", "Eroare la ştergerea SRL-ului selectat");
            }
        }
    }

    public void backButtonOnAction(ActionEvent e) {
        Utility.navigateToView("adminMenu-view.fxml", backButton);
    }
}