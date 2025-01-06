package org.cristina.generaredocsrl;
import javafx.scene.control.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.cristina.generaredocsrl.connection.DatabaseConnection;
import org.cristina.generaredocsrl.model.ClientData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ChooseFolderController {

    @FXML
    private Button nextPageButton;
    @FXML
    private Button backButton;
    @FXML
    private TextField contractNrTextField;
    @FXML
    private TextField priceTextField;
    @FXML
    private CheckBox psiCheckBox;
    @FXML
    private CheckBox ssmCheckBox;
    @FXML
    private ChoiceBox<String> srlChoiceBox;

    public static ClientData clientData = ClientsClientController.clientData;

    private static final String SELECT_SRL = "SELECT name FROM srl WHERE person_id = ?";

    private ObservableList<String> srlList = FXCollections.observableArrayList();

    public void initialize() {
        int personId = LoginController.loggedInPersonId;
        clientData.setPersonId(personId);
        populateSrlChoiceBox(personId);
    }

    private void populateSrlChoiceBox(int personId) {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        try{
            PreparedStatement preparedStatement = connectDB.prepareStatement(SELECT_SRL);
            preparedStatement.setInt(1,personId);

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

    public void nextPageButtonOnAction(ActionEvent event) {

        clientData.setPersonalSrl(srlChoiceBox.getValue());
        clientData.setContractNr(contractNrTextField.getText());
        clientData.setPrice(Integer.parseInt(priceTextField.getText()));
        clientData.setPsi(psiCheckBox.isSelected());
        clientData.setSsm(ssmCheckBox.isSelected());

        if(clientData.getPersonalSrl() == null || clientData.getContractNr().isEmpty() || priceTextField.getText().isEmpty()) {
            Utility.showAlert(Alert.AlertType.WARNING, "Atenţie!", "Toate câmpurile trebuie completate!");
            return;
        }

        if (!isValidInteger(clientData.getContractNr())) {
            Utility.showAlert(Alert.AlertType.ERROR, "Eroare!", "Nr. de contract invalid!");
            return;
        }

        if(!isValidInteger(priceTextField.getText())) {
            Utility.showAlert(Alert.AlertType.ERROR, "Eroare!", "Preţ invalid!");
            return;
        }

        if(!clientData.isPsi() && !clientData.isSsm()){
            Utility.showAlert(Alert.AlertType.WARNING, "Atenţie!", "Trebuie să bifaţi cel puţin o căsuţă!");
            return;
        }

        Utility.navigateToView(clientData.isSsm() ? "ssm-view.fxml" : "psi-view.fxml", nextPageButton);
    }

    public boolean isValidInteger(String integer){
        return integer.matches("^\\d+$");
    }

    public void backButtonOnAction(ActionEvent e) {
        Utility.navigateToView("clientsClient-view.fxml", backButton);
    }
}
