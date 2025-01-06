package org.cristina.generaredocsrl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;


import javafx.event.ActionEvent;
import org.cristina.generaredocsrl.model.ClientData;

public class ClientsClientController {

    @FXML
    private ChoiceBox<String> activityDomainChoiceBox;
    @FXML
    private Button nextPageButton;
    @FXML
    private Button backButton;
    @FXML
    private TextField srlTextField;
    @FXML
    private TextField hqTextField;
    @FXML
    private TextField cuiTextField;
    @FXML
    private TextField rcRegisterTextField;
    @FXML
    private TextField administratorTextField;

    private ObservableList<String> activityDomainList = FXCollections.observableArrayList();
    public static ClientData clientData = new ClientData();

    public void initialize() {
        populateActivityDomainChoiceBox();
    }

    public void populateActivityDomainChoiceBox() {
        activityDomainList.add("Construcţii");
        activityDomainList.add("Magazin");
        activityDomainList.add("Transport");
        activityDomainList.add("Service Auto");
        activityDomainChoiceBox.setItems(activityDomainList);
    }

    public void nextPageButtonOnAction(ActionEvent event) {
        clientData.setClientSrl(srlTextField.getText());
        clientData.setHq(hqTextField.getText());
        clientData.setCui(cuiTextField.getText());
        clientData.setRcRegister(rcRegisterTextField.getText());
        clientData.setAdministrator(administratorTextField.getText());
        clientData.setActivityDomain(activityDomainChoiceBox.getValue());

        if(clientData.getClientSrl().isEmpty() || clientData.getHq().isEmpty() || clientData.getCui().isEmpty() ||
                clientData.getRcRegister().isEmpty() || clientData.getAdministrator().isEmpty() || clientData.getActivityDomain() == null) {
            Utility.showAlert(Alert.AlertType.WARNING, "Atenţie!", "Toate câmpurile trebuie completate!");
            return;
        }

        if(!AddSRLController.isValidTaxCode(clientData.getCui())){
            Utility.showAlert(Alert.AlertType.ERROR, "Eroare", "CUI invalid!");
            return;
        }

        Utility.navigateToView("chooseFolder-view.fxml", nextPageButton);
    }

    public void backButtonOnAction(ActionEvent event) {
        Utility.navigateToView("clientMenu-view.fxml", backButton);
    }
}
