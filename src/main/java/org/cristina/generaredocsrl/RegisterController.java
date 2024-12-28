package org.cristina.generaredocsrl;
import org.cristina.generaredocsrl.Utility;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import org.cristina.generaredocsrl.connection.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterController{

    @FXML
    private Button backButton;
    @FXML
    private PasswordField setPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField firstnameTextField;
    @FXML
    private TextField lastnameTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField emailTextField;

    static String USER_CHECK_QUERY = "SELECT COUNT(*) FROM person WHERE username = ?";
    static String INSERT_REGISTER_QUERY = "INSERT INTO person (first_name, last_name, username, password, email) VALUES (?, ?, ?, ?, ?)";

    public void registerButtonOnAction(ActionEvent event) {
        if(firstnameTextField.getText().isEmpty() || lastnameTextField.getText().isEmpty()){
            Utility.showAlert(Alert.AlertType.WARNING,"Atenţie", "Toate câmpurile trebuie completate!");
            return;
        }

        if(!setPasswordField.getText().equals(confirmPasswordField.getText())){
            Utility.showAlert(Alert.AlertType.ERROR,"Eroare", "Parolele nu coincid!");
            return;
        }

        if(isUsernameTaken(usernameTextField.getText())){
            Utility.showAlert(Alert.AlertType.ERROR,"Eroare", "Acest nume de utilizator a fost utilizat deja!");
            return;
        }

        if(isEmailTaken(emailTextField.getText())){
            Utility.showAlert(Alert.AlertType.ERROR,"Eroare", "Acest email a fost utilizat deja!");
            return;
        }

        if(!isValidEmail(emailTextField.getText())){
            Utility.showAlert(Alert.AlertType.ERROR, "Eroare", "Email invalid");
            return;
        }

        registerUser();
    }

    public static boolean isUsernameTaken(String username) {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        boolean userExists = false;

        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(USER_CHECK_QUERY);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                userExists = count > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utility.showAlert(Alert.AlertType.ERROR, "Eroare", "Eroare verificare username!");
        }

        return userExists;
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isEmailTaken(String email) {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        boolean validEMail = false;

        String checkEmail = "SELECT COUNT(*) FROM person WHERE email = ?";

        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(checkEmail);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                validEMail = count > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utility.showAlert(Alert.AlertType.ERROR, "Eroare", "Eroare procesare statement!");
        }
        return validEMail;
    }

    public void registerUser(){
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String first_name = firstnameTextField.getText();
        String last_name = lastnameTextField.getText();
        String username = usernameTextField.getText();
        String password = setPasswordField.getText();
        String email = emailTextField.getText();

        try{
            PreparedStatement preparedStatement = connectDB.prepareStatement(INSERT_REGISTER_QUERY);
            preparedStatement.setString(1, first_name);
            preparedStatement.setString(2, last_name);
            preparedStatement.setString(3, username);
            preparedStatement.setString(4, password);
            preparedStatement.setString(5, email);

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                Utility.showAlert(Alert.AlertType.ERROR, "Eroare", "Eroare la înregistrarea utilizatorului!");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void backButtonOnAction(ActionEvent e) {
        Utility.navigateToView("clientsDB-view.fxml", backButton);
    }
}
