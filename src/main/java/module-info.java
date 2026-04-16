module org.csc311.capstone {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.csc311.capstone to javafx.fxml;
    exports org.csc311.capstone;
    exports org.csc311.capstone.models;
    opens org.csc311.capstone.models to javafx.fxml;
}