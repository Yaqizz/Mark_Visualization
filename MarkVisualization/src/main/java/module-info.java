module ui.assignments.a2enhanced {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;


    opens ui.assignments.a2enhanced to javafx.fxml;
    exports ui.assignments.a2enhanced;
}