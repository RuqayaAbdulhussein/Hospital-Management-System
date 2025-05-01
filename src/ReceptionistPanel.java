import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

public class ReceptionistPanel {
    TextField QID = new TextField();
    TextField firstname = new TextField();
    TextField lastname = new TextField();
    DatePicker dateOFBirth = new DatePicker();
    ComboBox<String> gender = new ComboBox<>();
    ComboBox<String> bloodType = new ComboBox<>();
    ComboBox<String> nationality = new ComboBox<>();
    TextField phone = new TextField();
    TextField email = new TextField();
    TextField emergencyContact = new TextField();
    ComboBox<String> emergencyContactRS  = new ComboBox<>();
    Button clear = new Button("Clear");

    public VBox getReceptionistPanel() {
        VBox receptionistPane = new VBox(10);
        receptionistPane.setAlignment(Pos.CENTER);
        receptionistPane.setPadding(new Insets(10));

        HBox fieldsPane = new HBox(10);
        fieldsPane.setAlignment(Pos.CENTER);

        Label qidLabel = createRequiredLabel("QID: ");
        Label FNLabel = createRequiredLabel("First Name: ");
        Label LNLabel = createRequiredLabel("Last Name: ");
        Label DOBLabel = createRequiredLabel("Date of Birth: ");
        Label genderLebel = createRequiredLabel("Gender: ");
        Label bloodTypeLabel = createRequiredLabel("Blood Type: ");


        QID.setPrefWidth(110);
        firstname.setPrefWidth(80);
        lastname.setPrefWidth(95);
        dateOFBirth.setPrefWidth(110);
        gender.setPrefWidth(95);
        bloodType.setPrefWidth(95);

        gender.getItems().addAll("Male", "Female");
        bloodType.getItems().addAll("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");


        fieldsPane.getChildren().addAll(
                qidLabel, QID,
                FNLabel, firstname,
                LNLabel, lastname,
                DOBLabel, dateOFBirth,
                genderLebel, gender,
                bloodTypeLabel, bloodType
        );

        HBox secondRowPane = new HBox(10);
        secondRowPane.setAlignment(Pos.CENTER);

        Label nationalityLabel = createRequiredLabel("Nationality: ");
        Label phoneLabel = createRequiredLabel("Phone: ");
        Label emailLabel = createRequiredLabel("Email: ");
        Label emergencyContactLabel = createRequiredLabel("Emergency Contact: ");
        Label emergencyContactRSLabel = createRequiredLabel("Emergency Contact Relationship: ");

        nationality.setPrefWidth(150);
        phone.setPrefWidth(80);
        email.setPrefWidth(150);
        emergencyContact.setPrefWidth(80);
        emergencyContactRS.setPrefWidth(100);

        nationality.getItems().addAll(
                "Afghan", "Albanian", "Algerian", "American", "Andorran", "Angolan", "Antiguan", "Argentine", "Armenian",
                "Australian", "Austrian", "Azerbaijani", "Bahamian", "Bahraini", "Bangladeshi", "Barbadian", "Belarusian",
                "Belgian", "Belizean", "Beninese", "Bhutanese", "Bolivian", "Bosnian", "Botswanan", "Brazilian", "British",
                "Bruneian", "Bulgarian", "Burundian", "Cambodian", "Cameroonian", "Canadian", "Cape Verdean", "Central African",
                "Chadian", "Chilean", "Chinese", "Colombian", "Comorian", "Congolese", "Costa Rican", "Croatian", "Cuban",
                "Cypriot", "Czech", "Danish", "Djiboutian", "Dominican", "Egyptian", "Emirati", "Equatorial Guinean", "Eritrean",
                "Estonian", "Ethiopian", "Fijian", "Finnish", "French", "Gabonese", "Gambian", "Georgian", "German", "Ghanaian",
                "Greek", "Grenadian", "Guatemalan", "Guinean", "Guinea-Bissauan", "Guyanese", "Haitian", "Honduran", "Hungarian",
                "Icelander", "Indian", "Indonesian", "Iranian", "Iraqi", "Irish", "Italian", "Ivorian", "Jamaican", "Japanese",
                "Jordanian", "Kazakh", "Kenyan", "Kittian and Nevisian", "Korean", "Kuwaiti", "Kyrgyzstani", "Laotian", "Latvian",
                "Lebanese", "Liberian", "Libyan", "Liechtenstein", "Lithuanian", "Luxembourgish", "Macedonian", "Malagasy",
                "Malawian", "Malaysian", "Maldivian", "Malian", "Malta", "Marshallese", "Mauritanian", "Mauritian", "Mexican",
                "Micronesian", "Moldovan", "Monacan", "Mongolian", "Moroccan", "Mozambican", "Namibian", "Nauruan", "Nepali",
                "New Zealander", "Nicaraguan", "Nigerian", "Nigerien", "North Korean", "Norwegian", "Omani", "Pakistani", "Palauan",
                "Palestinian", "Panamanian", "Papua New Guinean", "Paraguayan", "Peruvian", "Philippine", "Polish", "Portuguese",
                "Qatari", "Romanian", "Russian", "Rwandan", "Saint Lucian", "Salvadoran", "Samoan", "San Marinese", "Sao Tomean",
                "Saudi Arabian", "Scottish", "Senegalese", "Serbian", "Seychellois", "Sierra Leonean", "Singaporean", "Slovak",
                "Slovenian", "Solomon Islander", "Somali", "South African", "South Korean", "Spanish", "Sri Lankan", "Sudanese",
                "Surinamese", "Swazi", "Swedish", "Swiss", "Syrian", "Taiwanese", "Tajikistani", "Tanzanian", "Thai", "Togolese",
                "Tongan", "Trinidadian", "Tunisian", "Turkish", "Turkmen", "Tuvaluan", "Ugandan", "Ukrainian", "Uruguayan",
                "Uzbekistani", "Vanuatu", "Venezuelan", "Vietnamese", "Yemeni", "Zambian", "Zimbabwean"
        );

        emergencyContactRS.getItems().addAll("Mother","Father","Sibling","Spouse","Relative","Friend","Other");


        clear.setOnAction(e -> clearView());
        secondRowPane.getChildren().addAll(
                nationalityLabel, nationality,
                phoneLabel, phone,
                emailLabel, email,
                emergencyContactLabel, emergencyContact,
                emergencyContactRSLabel, emergencyContactRS
        );

        receptionistPane.getChildren().addAll(fieldsPane, secondRowPane);

        HBox buttonPane = new HBox();
        buttonPane.setAlignment(Pos.CENTER);
        buttonPane.getChildren().add(clear);

        receptionistPane.getChildren().add(buttonPane);

        return receptionistPane;
    }

    private Label createRequiredLabel(String labelText) {
        Label label = new Label(labelText);
        Text asterisk = new Text(" *");
        asterisk.setFill(Color.RED);
        label.setGraphic(asterisk);
        return label;
    }

    public String getSelectedGender() {
        return gender.getValue();
    }

    public String getSelectedBloodType() {return bloodType.getValue();}

    public String getSelectedNationality() {
        return nationality.getValue();
    }

    public String getSelectedRS() {return emergencyContactRS.getValue();}

    private void clearView() {
        QID.clear();
        QID.setDisable(false);
        firstname.clear();
        lastname.clear();
        dateOFBirth.setValue(null);
        gender.setValue(null);
        bloodType.setValue(null);
        nationality.setValue(null);
        phone.clear();
        email.clear();
        emergencyContact.clear();
        emergencyContactRS.setValue(null);
    }
}
