module co.edu.uptc.automatonsimulationservice {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;

    opens co.edu.uptc.automatonsimulationservice.ui.controllers to javafx.fxml;
    opens co.edu.uptc.automatonsimulationservice.automaton.models to com.fasterxml.jackson.databind;
    opens co.edu.uptc.automatonsimulationservice.persistence.models to com.fasterxml.jackson.databind;
    exports co.edu.uptc.automatonsimulationservice;
}