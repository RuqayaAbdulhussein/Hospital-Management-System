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

public class NursePanel {

    ComboBox<String> DoctorName = new ComboBox<>();
    TextArea diagnosis = new TextArea();
    Button clear = new Button("Clear");

    public VBox getNursePane() {
        VBox doctorPane = new VBox(10);
        doctorPane.setAlignment(Pos.CENTER);

        HBox fieldsPane = new HBox(10);
        fieldsPane.setAlignment(Pos.CENTER);

        Label DNameLabel = createRequiredLabel("Doctor Name: ");
        Label DiagnosisLabel = createRequiredLabel("Diagnosis: ");

        DoctorName.setPrefWidth(160);
        diagnosis.setPrefWidth(250);

        populateDoctorComboBox();

        clear.setOnAction(e -> clearView());

        fieldsPane.getChildren().addAll(
                DNameLabel, DoctorName,
                DiagnosisLabel, diagnosis
        );

        HBox textPane = new HBox(10);
        textPane.setAlignment(Pos.CENTER);
        textPane.getChildren().addAll(DiagnosisLabel, diagnosis);

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

    public String getSelectedDoctor() {
        return DoctorName.getValue();
    }

    private void populateDoctorComboBox() {
        DoctorName.getItems().clear();
        String sql = "SELECT firstname, lastname FROM Users WHERE role = 'doctor'";
        try (Connection con = DBUtils.establishConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String firstName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                String doctorName = firstName + " " + lastName;
                DoctorName.getItems().add(doctorName);
            }
            DBUtils.closeConnection(con, stmt);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlertError("Error", "Failed to fetch doctor names from database.");
        }
    }


    private void clearView() {
        DoctorName.setValue(null);
        diagnosis.clear();
    }

    private void showAlertError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

