package org.cristina.generaredocsrl;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.cristina.generaredocsrl.connection.DatabaseConnection;
import org.cristina.generaredocsrl.model.Person;
import org.cristina.generaredocsrl.Utility;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;

public class ClientsDBController {

    @FXML
    private Button backButton;
    @FXML
    private TextField searchUsernameTextField;
    @FXML
    private TableView<Person> clientsTable;
    @FXML
    private TableColumn<Person, String> usernameColumn;
    @FXML
    private TableColumn<Person, String> firstnameColumn;
    @FXML
    private TableColumn<Person, String> lastnameColumn;
    @FXML
    private TableColumn<Person, String> emailColumn;

    private static final String TABLE_DATA_QUERY = "SELECT username, first_name, last_name, password, email FROM person WHERE priority != 'admin'";
    private static final String DELETE_CLIENT_QUERY = "DELETE FROM person WHERE username = ?";

    private ObservableList<Person> personList = FXCollections.observableArrayList();


    public void initialize(){
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        firstnameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastnameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        loadClientsFromDatabase();
    }

    private void loadClientsFromDatabase() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        try{
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(TABLE_DATA_QUERY);

            while(queryResult.next()){
                String username = queryResult.getString("username");
                String firstname = queryResult.getString("first_name");
                String lastname = queryResult.getString("last_name");
                String password = queryResult.getString("password");
                String email = queryResult.getString("email");

                personList.add(new Person(username, firstname, lastname, password, email));
            }

            clientsTable.setItems(personList);

        } catch(Exception e){
            e.printStackTrace();
            Utility.showAlert(Alert.AlertType.ERROR, "Eroare", "Eroare încărcarea clienţilor!");
        }
    }

    public void createRegisterForm() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("register-view.fxml"));

            Stage registerStage = new Stage();
            registerStage.setTitle("Register Client");
            registerStage.setScene(new Scene(root));
            registerStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            Utility.showAlert(Alert.AlertType.ERROR, "Eroare", "Eroare la deschiderea formularului de înregistrare!");
        }
    }

    public void refreshButtonOnAction(ActionEvent event) {
        personList.clear();
        loadClientsFromDatabase();
        clientsTable.refresh();
    }

    public void deleteButtonOnAction(ActionEvent event) {
        Person selectedPerson = clientsTable.getSelectionModel().getSelectedItem();

        if(selectedPerson != null){
            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDB = connectNow.getConnection();

            try{
                PreparedStatement statement = connectDB.prepareStatement(DELETE_CLIENT_QUERY);
                statement.setString(1, selectedPerson.getUsername());

                int rowsDeleted = statement.executeUpdate();
                if(rowsDeleted > 0){
                    personList.remove(selectedPerson);
                }
            } catch(Exception e){
                e.printStackTrace();
                Utility.showAlert(Alert.AlertType.ERROR, "Eroare", "Eroare la ştergerea unui client!");
            }
        }
    }

    public void searchUsername(){
        String searchUsernameString = searchUsernameTextField.getText().toLowerCase();

        ObservableList<Person> filteredList = FXCollections.observableArrayList();
        for(Person person : personList){
            if(person.getUsername().toLowerCase().contains(searchUsernameString)){
                filteredList.add(person);
            }

            clientsTable.setItems(filteredList);
        }
    }

    public void backButtonOnAction(ActionEvent e) {
        Utility.navigateToView("adminMenu-view.fxml", backButton);
    }
}
