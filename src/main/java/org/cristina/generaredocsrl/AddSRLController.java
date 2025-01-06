package org.cristina.generaredocsrl;
import org.cristina.generaredocsrl.Utility;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import org.cristina.generaredocsrl.connection.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class AddSRLController {

    @FXML
    private ChoiceBox<String> usernameChoiceBox;
    @FXML
    private TextField srlNameTextField;
    @FXML
    private TextField hqTextField;
    @FXML
    private TextField taxCodeTextField;
    @FXML
    private TextField phoneNrTextField;
    @FXML
    private TextField bankAccountTextField;
    @FXML
    private ChoiceBox<String> bankChoiceBox;
    @FXML
    private ChoiceBox<String> countyChoiceBox;
    @FXML
    private Button backButton;
    @FXML
    private TextField rcRegisterTextField;

    private static final String SELECT_USER = "SELECT username FROM person WHERE priority = 'client'";
    private static final String SELECT_BANK = "SELECT bank_name FROM bank";
    private static final String SELECT_COUNTY = "SELECT county_name FROM county";
    private static final String SELECT_USER_ID = "SELECT person_id FROM person WHERE username = ?";
    private static final String SELECT_BANK_ID = "SELECT bank_id FROM bank WHERE bank_name = ?";
    private static final String SELECT_COUNTY_ID = "SELECT county_id FROM county WHERE county_name = ?";
    private static final String INSERT_SRL = "INSERT INTO srl (name, person_id, hq, tax_code, phone_nr, " +
            "bank_account, bank_id, bcounty_id, rcregister) VALUES (?,?,?,?,?,?,?,?,?)";

    private ObservableList<String> usernameList = FXCollections.observableArrayList     ();
    private ObservableList<String> bankList = FXCollections.observableArrayList();
    private ObservableList<String> countyList = FXCollections.observableArrayList();

    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^[0-9]{10}$");
    }

    public static boolean isValidIBAN(String iban){
        return iban.matches("^RO[0-9A-Z]{22}$");
    }

    public static boolean isValidTaxCode(String taxCode){
        return taxCode.matches("RO\\d{2,10}");
    }

    public void initialize() {
        usernameChoiceBoxOnAction();
        bankChoiceBoxOnAction();
        countyChoiceBoxOnAction();
    }

    private void usernameChoiceBoxOnAction() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        try{
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(SELECT_USER);

            while(queryResult.next()){
                String username = queryResult.getString("username");
                usernameList.add(username);
            }

            usernameChoiceBox.setItems(usernameList);

        } catch(Exception e){
            e.printStackTrace();
            Utility.showAlert(Alert.AlertType.ERROR, "Eroare", "Eroare la încărcarea utilizatorilor!");
        }
    }

    private void bankChoiceBoxOnAction(){
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        try{
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(SELECT_BANK);

            while(queryResult.next()){
                String bank = queryResult.getString("bank_name");
                bankList.add(bank);
            }

            bankChoiceBox.setItems(bankList);

        } catch(Exception e){
            e.printStackTrace();
            Utility.showAlert(Alert.AlertType.ERROR, "Eroare", "Eroare la încărcarea băncilor!");
        }
    }

    public void countyChoiceBoxOnAction(){
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        try{
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(SELECT_COUNTY);

            while(queryResult.next()){
                String county = queryResult.getString("county_name");
                countyList.add(county);
            }

            countyChoiceBox.setItems(countyList);

        } catch(Exception e){
            e.printStackTrace();
            Utility.showAlert(Alert.AlertType.ERROR, "Eroare", "Eroare la încărcarea judeţelor!");
        }
    }

    public void SRLButtonOnAction(){
        addSRL();
    }

    public void addSRL(){
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String srlName = srlNameTextField.getText();
        String hq = hqTextField.getText();
        String taxCode = taxCodeTextField.getText();
        String phoneNr = phoneNrTextField.getText();
        String bankAccount = bankAccountTextField.getText();
        String username = (String) usernameChoiceBox.getValue();
        String bank = (String) bankChoiceBox.getValue();
        String county = (String) countyChoiceBox.getValue();
        String rcRegister = rcRegisterTextField.getText();

        if (srlName.isEmpty() || hq.isEmpty() || taxCode.isEmpty() || phoneNr.isEmpty() ||
                bankAccount.isEmpty() || username == null || bank == null || county == null || rcRegister.isEmpty()) {

            Utility.showAlert(Alert.AlertType.WARNING, "Atenţie!", "Toate câmpurile sunt obligatorii!");
            return;
        }

        if(!isValidPhoneNumber(phoneNr)){
            Utility.showAlert(Alert.AlertType.ERROR, "Eroare", "Număr de telefon invalid!");
            return;
        }

        if(!isValidIBAN(bankAccount)){
            Utility.showAlert(Alert.AlertType.ERROR, "Eroare", "IBAN invalid!");
            return;
        }

        if(!isValidTaxCode(taxCode)){
            Utility.showAlert(Alert.AlertType.ERROR, "Eroare", "CUI invalid!");
            return;
        }

        try {
            PreparedStatement personStmt = connectDB.prepareStatement(SELECT_USER_ID);
            personStmt.setString(1, username);
            ResultSet personResult = personStmt.executeQuery();

            if (!personResult.next()) {
                Utility.showAlert(Alert.AlertType.ERROR, "Eroare", "Utilizator invalid!");
            }

            int personId = personResult.getInt("person_id");
            PreparedStatement bankStmt = connectDB.prepareStatement(SELECT_BANK_ID);
            bankStmt.setString(1, bank);
            ResultSet bankResult = bankStmt.executeQuery();

            if(!bankResult.next()){
                Utility.showAlert(Alert.AlertType.ERROR, "Eroare", "Bancă invalidă!");
                return;
            }

            int bankId = bankResult.getInt("bank_id");

            PreparedStatement countyStmt = connectDB.prepareStatement(SELECT_COUNTY_ID);
            countyStmt.setString(1, county);
            ResultSet countyResult = countyStmt.executeQuery();

            if(!countyResult.next()){
                Utility.showAlert(Alert.AlertType.ERROR, "Eroare", "Judeţ invalid!");
                return;
            }
            int countyId = countyResult.getInt("county_id");

            PreparedStatement srlStmt = connectDB.prepareStatement(INSERT_SRL);
            srlStmt.setString(1, srlName);
            srlStmt.setInt(2, personId);
            srlStmt.setString(3, hq);
            srlStmt.setString(4, taxCode);
            srlStmt.setString(5, phoneNr);
            srlStmt.setString(6, bankAccount);
            srlStmt.setInt(7, bankId);
            srlStmt.setInt(8, countyId);
            srlStmt.setString(9, rcRegister);

            int rowsAffected = srlStmt.executeUpdate();

            if(rowsAffected > 0){
                Utility.showAlert(Alert.AlertType.CONFIRMATION, "Succes", "SRL înregistrat cu succes!");
                return;
            }
            else{
                Utility.showAlert(Alert.AlertType.ERROR, "Eroare", "Eroare înregistrare SRL!");
                return;
            }

        }catch(Exception e){
            e.printStackTrace();
            Utility.showAlert(Alert.AlertType.ERROR, "Eroare", "Eroare la procesarea datelor!");
        }
    }

    public void backButtonOnAction(ActionEvent e) {
        Utility.navigateToView("srlDB-view.fxml", backButton);
    }
}