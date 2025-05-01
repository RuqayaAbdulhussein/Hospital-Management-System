import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
//this is the panel at the bottom of the admin page with all the text fields and drop-downs which the admin can use to add, update, delete users.
public class adminPanel {
    TextField QID = new TextField();
    TextField firstname = new TextField();
    TextField lastname = new TextField();
    TextField email = new TextField();
    TextField phone = new TextField();
    ComboBox<String> role = new ComboBox<>();
    ComboBox<String> specialization = new ComboBox<>();
    ComboBox<String> department = new ComboBox<>();
    Button clear = new Button("Clear");

    Label specLabel =  createRequiredLabel("Specialization: "); //i placed them here so they can be accessed in the userManagement class to set their visibility.
    Label depLabel = createRequiredLabel("Department: ");

    public VBox getadminPanel() {
        VBox adminPane = new VBox(10);
        adminPane.setAlignment(Pos.CENTER);
        adminPane.setPadding(new Insets(10));

        HBox fieldsPane1 = new HBox(10);
        fieldsPane1.setAlignment(Pos.CENTER);

        Label QIDLabel = createRequiredLabel("QID: ");
        Label FNLabel = createRequiredLabel("First Name: ");
        Label LNLabel = createRequiredLabel("Last Name: ");
        Label emailLabel = createRequiredLabel("Email: ");
        Label phoneLabel = createRequiredLabel("Phone: ");

        firstname.setPrefWidth(80);
        lastname.setPrefWidth(95);
        email.setPrefWidth(150);
        phone.setPrefWidth(80);

        fieldsPane1.getChildren().addAll(
                QIDLabel, QID,
                FNLabel, firstname,
                LNLabel, lastname,
                emailLabel, email,
                phoneLabel, phone
        );

        HBox fieldsPane2 = new HBox(10);
        fieldsPane2.setAlignment(Pos.CENTER);

        Label roleLabel = createRequiredLabel("Role: ");

        role.setPrefWidth(100);
        specialization.setPrefWidth(120);
        department.setPrefWidth(120);

        role.getItems().addAll("Doctor", "Nurse", "Admin", "Receptionist");
        specialization.getItems().addAll("Cardiology", "Neurology", "Orthopedics", "Pediatrics", "Dermatology", "Gastroenterology");
        department.getItems().addAll("Medical", "Surgery", "Pediatrics", "Neurology", "Orthopedics", "Oncology");

        fieldsPane2.getChildren().addAll(roleLabel, role, specLabel, specialization, depLabel, department);

        specialization.setVisible(false);
        department.setVisible(false);
        specLabel.setVisible(false);
        depLabel.setVisible(false);


        role.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.equals("Doctor")) {
                specialization.setVisible(true);
                department.setVisible(true);
                specLabel.setVisible(true);
                depLabel.setVisible(true);
            }else{
                specialization.setVisible(false);
                department.setVisible(false);
                specLabel.setVisible(false);
                depLabel.setVisible(false);
            }

        });

        HBox buttonPane = new HBox();
        buttonPane.setAlignment(Pos.CENTER);
        buttonPane.getChildren().add(clear);
        clear.setOnAction(e -> clearView());

        adminPane.getChildren().addAll(fieldsPane1, fieldsPane2, buttonPane);

        return adminPane;
    }

    private Label createRequiredLabel(String labelText) {
        Label label = new Label(labelText);
        Text asterisk = new Text(" *");
        asterisk.setFill(Color.RED);
        label.setGraphic(asterisk);
        return label;
    }

    public Label getSpecLabel() {
        return specLabel;
    }

    public Label getDepLabel() {
        return depLabel;
    }

    public String getSelectedRole() {
        return role.getValue() != null ? role.getValue() : "";
    }

    public String getSelectedSpec() {
        return specialization.getValue() != null ? specialization.getValue() : "";
    }

    public String getSelectedDep() {
        return department.getValue() != null ? department.getValue() : "";
    }

    private void clearView() { //clears aadmin pane fields when clicked on clear button
        QID.setDisable(false);
        QID.clear();
        firstname.clear();
        lastname.clear();
        email.clear();
        phone.clear();
        role.setValue(null);
        specialization.setValue(null);
        department.setValue(null);
        specialization.setVisible(false);
        department.setVisible(false);
        specLabel.setVisible(false);
        depLabel.setVisible(false);
    }
}
