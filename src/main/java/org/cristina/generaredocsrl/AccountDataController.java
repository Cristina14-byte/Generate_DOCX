package org.cristina.generaredocsrl;
import org.cristina.generaredocsrl.RegisterController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.cristina.generaredocsrl.connection.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class AccountDataController  {

    @FXML
    private Button backButton;
    @FXML
    private Label surnameLabel;
    @FXML
    private Label firstnameLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private TextField newUsernameTextField;
    @FXML
    private TextField newEmailTextField;

    private int personId = LoginController.loggedInPersonId;

    static String ACCOUNT_DATA_QUERY = "SELECT first_name, last_name, username, email FROM person WHERE person_id = ?;";
    static String UPDATE_QUERY = "UPDATE person SET username = ?, email = ? WHERE person_id = ?;";

    public void initialize(){
        loadAccountData(null);
    }

    public void loadAccountData(ActionEvent event) {

        try {
            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDB = connectNow.getConnection();

            PreparedStatement statement = connectDB.prepareStatement(ACCOUNT_DATA_QUERY);
            statement.setInt(1, personId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                firstnameLabel.setText(resultSet.getString("first_name"));
                surnameLabel.setText(resultSet.getString("last_name"));
                usernameLabel.setText(resultSet.getString("username"));
                emailLabel.setText(resultSet.getString("email"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            Utility.showAlert(Alert.AlertType.ERROR, "Eroare", "Eroare încărcare date cont!");
        }
    }

    public void saveNewDataButtonOnAction(ActionEvent event) {
        String newUsername = newUsernameTextField.getText();
        String newEmail = newEmailTextField.getText();

        if (newUsername.isEmpty() && newEmail.isEmpty()) {
            Utility.showAlert(Alert.AlertType.WARNING, "Atenţie!", "Completaţi cel puţin o căsuţă!");
            return;
        }

        try {
            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDB = connectNow.getConnection();

            if(!newUsername.isEmpty()){
                if(RegisterController.isUsernameTaken(newUsername)){
                    Utility.showAlert(Alert.AlertType.WARNING, "Atenţie!", "Nume de utilizator deja folosit!");
                    return;
                }
            }

            if(!newEmail.isEmpty()){
                if(!RegisterController.isValidEmail(newEmail)){
                    Utility.showAlert(Alert.AlertType.WARNING, "Atenţie!", "Email invalid!");
                    return;
                }

                if(RegisterController.isEmailTaken(newEmail)){
                    Utility.showAlert(Alert.AlertType.WARNING, "Atenţie!", "Emailul este deja folosit!");
                    return;
                }
            }

            PreparedStatement updateStatement = connectDB.prepareStatement(UPDATE_QUERY);

            updateStatement.setString(1, newUsername.isEmpty() ? usernameLabel.getText() : newUsername);
            updateStatement.setString(2, newEmail.isEmpty() ? emailLabel.getText() : newEmail);
            updateStatement.setInt(3, personId);

            int rowsUpdated = updateStatement.executeUpdate();
            if (rowsUpdated > 0) {
                Utility.showAlert(Alert.AlertType.CONFIRMATION, "Succes!", "Datele au fost modificate!");
                loadAccountData(null);
            } else{
                Utility.showAlert(Alert.AlertType.ERROR, "Eroare!", "Nu s-au putut actualiza datele!");
            }

        }  catch(Exception e) {
            e.printStackTrace();
            Utility.showAlert(Alert.AlertType.ERROR, "Eroare", "Eroare la salvarea datelor!");
        }
    }

    public void backButtonOnAction(ActionEvent event) {
        Utility.navigateToView("clientMenu-view.fxml", backButton);
    }
}