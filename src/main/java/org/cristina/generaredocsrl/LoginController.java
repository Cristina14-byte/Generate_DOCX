package org.cristina.generaredocsrl;
import org.cristina.generaredocsrl.Utility;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import org.cristina.generaredocsrl.connection.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class LoginController {

    @FXML
    private Button cancelButton;
    @FXML
    private Button loginButton;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private Label forgotPasswordLabel;

    public static int loggedInPersonId = -1;

    private static final String VERIFY_LOGIN_QUERY = "SELECT person_id, priority FROM person WHERE username = ? AND password = ?";
    private static final String FORGOT_PASSWORD_QUERY = "SELECT password FROM person WHERE username = ?";

    public void loginButtonOnAction(ActionEvent event) {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();

        if(username.isBlank() || password.isBlank()) {
            Utility.showAlert(Alert.AlertType.WARNING, "Atenţie", "Completaţi toate datele necesare!");
        }
        else{
            validateLogin(username, password);
        }
    }

    public void cancelButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
        Platform.exit();
    }

    private void validateLogin(String username, String password){
        try {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        PreparedStatement statement = connectDB.prepareStatement(VERIFY_LOGIN_QUERY);
        statement.setString(1, username);
        statement.setString(2, password);
        ResultSet queryResult = statement.executeQuery();

        if (queryResult.next()) {
            loggedInPersonId = queryResult.getInt("person_id");
            String priority = queryResult.getString("priority");
            if ("admin".equals(priority)) {
                Utility.navigateToView("adminMenu-view.fxml",loginButton);
            } else {
                Utility.navigateToView("clientMenu-view.fxml",loginButton);
            }
        } else {
            Utility.showAlert(Alert.AlertType.ERROR, "Eroare", "Conectare invalidă!");
        }
        } catch (Exception e){
            e.printStackTrace();
            Utility.showAlert(Alert.AlertType.ERROR, "Eroare", "Eroare validare logare!");
        }
    }

    public void forgotPasswordButtonOnAction(ActionEvent event) {
        String username = usernameTextField.getText();
        if(username.isEmpty()){
            Utility.showAlert(Alert.AlertType.ERROR, "Eroare", "Introduceţi un nume de utilizator!");
            return;
        }
        
        try{
            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDB = connectNow.getConnection();

            PreparedStatement statement = connectDB.prepareStatement(FORGOT_PASSWORD_QUERY);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String password = resultSet.getString("password");
                forgotPasswordLabel.setText(password);
            }
            else{
                Utility.showAlert(Alert.AlertType.ERROR, "Eroare", "Utilizatorul nu există!");
            }
        } catch(Exception e) {
            e.printStackTrace();
            Utility.showAlert(Alert.AlertType.ERROR, "Eroare", "Eroare recuperare parolă!");
        }
    }
}
