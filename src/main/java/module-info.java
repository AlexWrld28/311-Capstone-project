module org.csc311.capstone {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.opencsv;
    requires bcrypt;
    requires com.github.librepdf.openpdf;
    requires java.desktop;
    requires org.postgresql.jdbc;
    requires io.github.cdimascio.dotenv.java;

    opens org.csc311.capstone to javafx.fxml;
    opens org.csc311.capstone.models to javafx.fxml;

    exports org.csc311.capstone;
    exports org.csc311.capstone.models;
    exports org.csc311.capstone.db;
}