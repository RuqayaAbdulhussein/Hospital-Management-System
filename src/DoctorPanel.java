import javafx.scene.control.*;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DoctorPanel {

    ComboBox<String> NurseName = new ComboBox<>();
    TextArea diagnosis = new TextArea();
    TextArea treatment = new TextArea();
    Button clear = new Button("Clear");

    public VBox getDoctorPane() {
        VBox doctorPane = new VBox(10);
        doctorPane.setAlignment(Pos.CENTER);

        HBox fieldsPane = new HBox(10);
        fieldsPane.setAlignment(Pos.CENTER);

        Label NNameLabel = createRequiredLabel("Nurse Name: ");
        Label DiagnosisLabel = createRequiredLabel("Diagnosis: ");
        Label TreatmentLabel = createRequiredLabel("Treatment: : ");

        NurseName.setPrefWidth(160);
        diagnosis.setPrefWidth(250);
        treatment.setPrefWidth(250);

        populateNurseComboBox();

        clear.setOnAction(e -> clearView());

        fieldsPane.getChildren().addAll(
                NNameLabel, NurseName,
                DiagnosisLabel, diagnosis,
                TreatmentLabel, treatment
        );

        HBox textPane = new HBox(10);
        textPane.setAlignment(Pos.CENTER);
        textPane.getChildren().addAll(DiagnosisLabel, diagnosis, TreatmentLabel, treatment);

        HBox buttonPane = new HBox();
        buttonPane.setAlignment(Pos.CENTER);
        buttonPane.getChildren().add(clear);

        doctorPane.getChildren().addAll(fieldsPane, textPane ,buttonPane);

        return doctorPane;
    }

    private Label createRequiredLabel(String labelText) {
        Label label = new Label(labelText);
        Text asterisk = new Text(" *");
        asterisk.setFill(Color.RED);
        label.setGraphic(asterisk);
        return label;
    }

    public String getSelectedNurse() {
        return NurseName.getValue();
    }

    private void populateNurseComboBox() {
        NurseName.getItems().clear();
        String sql = "SELECT firstname, lastname FROM Users WHERE role = 'nurse'";
        try (Connection con = DBUtils.establishConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String firstName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                String nurseName = firstName + " " + lastName;
                NurseName.getItems().add(nurseName);
            }
            DBUtils.closeConnection(con, stmt);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlertError("Error", "Failed to fetch doctor names from database.");
        }
    }


    private void clearView() {
        NurseName.setValue(null);
        diagnosis.clear();
        treatment.clear();
    }

    private void showAlertError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

