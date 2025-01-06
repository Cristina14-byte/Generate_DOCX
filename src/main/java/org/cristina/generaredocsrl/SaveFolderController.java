package org.cristina.generaredocsrl;

import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xwpf.usermodel.*;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import org.cristina.generaredocsrl.connection.DatabaseConnection;
import org.cristina.generaredocsrl.model.ClientData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class SaveFolderController {

    @FXML
    private Button backMenuButton;
    @FXML
    private Button backButton;

    public static ClientData clientData = ChooseFolderController.clientData;

    int person_id = LoginController.loggedInPersonId;
    private static final String SRL_DATA = """
    SELECT srl.name, srl.hq, srl.tax_code, srl.phone_nr, srl.bank_account, 
           bank.bank_name AS bank_name, county.county_name AS county_name,
           srl.rcregister
    FROM srl
    JOIN bank ON srl.bank_id = bank.bank_id
    JOIN county ON srl.bcounty_id = county.county_id
    WHERE srl.person_id = ?
""";

    private static final String CONTRACT_PATH = "D:\\Generare_Documente\\Materiale\\Contract";
    private static final String PSI_PATH = "D:\\Generare_Documente\\Materiale\\PSI";
    private static final String SSM_PATH = "D:\\Generare_Documente\\Materiale\\SSM";

    public void createDocButtonAction() {
        String srlName = ClientsClientController.clientData.getClientSrl();
        String SAVE_PATH = "D:\\Generare_Documente\\DOCUMENTE GENERATE\\" + srlName;

        File mainFolder = new File(SAVE_PATH);
        mainFolder.mkdir();

        File contractFolder = new File(mainFolder, "Contract");
        contractFolder.mkdir();

        if (clientData.isPsi() && clientData.isSsm()){
            copyAndEditFile(new File(CONTRACT_PATH + "\\Contract SSM+PSI"), contractFolder);
            File ssmFolder = new File(mainFolder, "SSM");
            ssmFolder.mkdir();
            copyAndEditFile(new File(SSM_PATH), ssmFolder);

            File psiFolder = new File(mainFolder, "PSI");
            psiFolder.mkdir();
            copyAndEditFile(new File(PSI_PATH), psiFolder);

        }else if(clientData.isSsm()){
            copyAndEditFile(new File(CONTRACT_PATH + "\\Contract SSM"), contractFolder);
            File ssmFolder = new File(mainFolder, "SSM");
            ssmFolder.mkdir();
            copyAndEditFile(new File(SSM_PATH), ssmFolder);
        } else if(clientData.isPsi()){
            copyAndEditFile(new File(CONTRACT_PATH + "\\Contract PSI"), contractFolder);
            File psiFolder = new File(mainFolder, "PSI");
            psiFolder.mkdir();
            copyAndEditFile(new File(PSI_PATH), psiFolder);
        }

        Utility.showAlert(Alert.AlertType.CONFIRMATION,"Confirmare" ,"Fişierele au fost create cu succes!");
    }

    public void copyFile(String sourcePath, String destinationPath) {
        try{
            Files.copy(Path.of(sourcePath), Path.of(destinationPath), StandardCopyOption.REPLACE_EXISTING);
        } catch(Exception e){
            e.printStackTrace();
            Utility.showAlert(Alert.AlertType.ERROR,"Eroare", "Fişierul nu a putut fi copiat!");
        }
    }

    public void copyAndEditFile(File sourceFolder, File destinationFolder) {

        for(File file:sourceFolder.listFiles()){
            if(file.isDirectory()){
                File newDir = new File(destinationFolder, file.getName());
                newDir.mkdir();
                copyAndEditFile(file, newDir);
            } else if(file.getName().endsWith(".docx")){
                File newFile = new File(destinationFolder, file.getName());
                copyFile(file.getPath(), newFile.getPath());
                editFile(newFile.getPath(), newFile.getPath());
            } else{
                File newFile = new File(destinationFolder, file.getName());
                copyFile(file.getPath(), newFile.getPath());
              }
        }
    }

    public void replacePlaceHoldersInDocx(String sourcePath, String destinationPath, Map<String, String> placeholders) {
        ZipSecureFile.setMinInflateRatio(0.001);

        try (FileInputStream fis = new FileInputStream(sourcePath);
             XWPFDocument doc = new XWPFDocument(fis);
             FileOutputStream fos = new FileOutputStream(destinationPath)) {

            for (XWPFParagraph paragraph : doc.getParagraphs()) {
                for (XWPFRun run : paragraph.getRuns()) {
                    String text = run.getText(0);
                    if (text != null) {
                        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                            if (text.contains(entry.getKey())) {
                                text = text.replace(entry.getKey(), entry.getValue());
                            }
                        }
                        run.setText(text, 0);
                    }
                }
            }

            String clientSrl = placeholders.get("?CLIENT_SRL?");
            for (XWPFHeader header : doc.getHeaderList()) {
                for (XWPFParagraph paragraph : header.getParagraphs()) {
                    for (XWPFRun run : paragraph.getRuns()) {
                        String text = run.getText(0);
                        if (text != null && text.contains("?CLIENT_SRL?")) {
                            text = text.replace("?CLIENT_SRL?", clientSrl);
                            run.setText(text, 0);
                        }
                    }
                }
            }

            String usersName = placeholders.get("?USERS_NAME?");
            for (XWPFFooter footer : doc.getFooterList()) {
                for (XWPFParagraph paragraph : footer.getParagraphs()) {
                    for (XWPFRun run : paragraph.getRuns()) {
                        String text = run.getText(0);
                        if (text != null && text.contains("?USERS_NAME?")) {
                            text = text.replace("?USERS_NAME?", "Ing. " + usersName);
                            run.setText(text, 0);
                        }
                    }
                }
            }

            doc.write(fos);

        } catch (Exception e) {
            e.printStackTrace();
            Utility.showAlert(Alert.AlertType.ERROR, "Eroare", "Eroare la modificarea documentului!");
        }
    }


    public void editFile(String sourceDocPath, String destinationPath) {
        Map<String, String> placeholders = new HashMap<>();

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = currentDate.format(formatter);

        placeholders.put("?DATE?", formattedDate);
        placeholders.put("?CONTRACT_NR?", clientData.getContractNr());
        placeholders.put("?PERSONAL_SRL?", clientData.getPersonalSrl());
        placeholders.put("?CLIENT_SRL?", clientData.getClientSrl());
        placeholders.put("?CLIENTS_SRL?", clientData.getClientSrl());
        placeholders.put("?CHQ?", clientData.getHq());
        placeholders.put("?CCUI?",clientData.getCui());
        placeholders.put("?CRCREGISTER?",clientData.getRcRegister());
        placeholders.put("?ADMINISTRATOR?", clientData.getAdministrator());
        placeholders.put("?PSIRESP?", clientData.getPsiResponsible());

        String usersName = getUsersNameFromPerson(person_id);
        placeholders.put("?USERS_NAME?", usersName);
        placeholders.put("?USER_NAME?", usersName); //might be the case that I wrote the name wrong

        String email = getEmailFromPerson(person_id);
        placeholders.put("?PEMAIL?", email);

        try{
            Connection connectNow = DatabaseConnection.getConnection();
            PreparedStatement stmt = connectNow.prepareStatement(SRL_DATA);
            stmt.setInt(1, person_id);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                placeholders.put("?PERSONAL_SRL?", rs.getString("name"));
                placeholders.put("?PHQ?", rs.getString("hq"));
                placeholders.put("?PCUI?", rs.getString("tax_code"));
                placeholders.put("?PPHONENR?", rs.getString("phone_nr"));
                placeholders.put("?PIBAN?", rs.getString("bank_account"));
                placeholders.put("?PBANK?", rs.getString("bank_name"));
                placeholders.put("?PCOUNTY?", rs.getString("county_name"));
                placeholders.put("?PRCREGISTER?", rs.getString("rcregister"));
            }

        } catch(Exception e){
            e.printStackTrace();
            Utility.showAlert(Alert.AlertType.ERROR, "Eroare", "Eroare conectare BD");
        }

        replacePlaceHoldersInDocx(sourceDocPath, destinationPath, placeholders);
    }

    private String getUsersNameFromPerson(int personId) {
        String fullName = "";
        try (Connection connectNow = DatabaseConnection.getConnection();
             PreparedStatement stmt = connectNow.prepareStatement(
                     "SELECT first_name, last_name FROM person WHERE person_id = ?")) {
            stmt.setInt(1, personId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                fullName = firstName + " " + lastName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fullName;
    }

    private String getEmailFromPerson(int personId) {
        String email = "";
        try (Connection connectNow = DatabaseConnection.getConnection();
             PreparedStatement stmt = connectNow.prepareStatement(
                     "SELECT email FROM person WHERE person_id = ?")) {
            stmt.setInt(1, personId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                email = rs.getString("email");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return email;
    }

    public void backMenuButtonOnAction() {
        Utility.navigateToView("clientMenu-view.fxml", backMenuButton);
    }

    public void backButtonOnAction() {
        if (clientData.isPsi()) {
            Utility.navigateToView("psi-view.fxml", backButton);
        }
        else{
            Utility.navigateToView("ssm-view.fxml", backButton);
        }
    }
}
