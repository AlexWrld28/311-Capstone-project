module org.csc311.capstone {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;
    requires java.desktop;

    requires com.opencsv;
    requires com.github.librepdf.openpdf;
    requires bcrypt;
    requires org.postgresql.jdbc;
    requires io.github.cdimascio.dotenv.java;

    requires minio;

    opens org.csc311.capstone to javafx.fxml;
    opens org.csc311.capstone.models to javafx.fxml;

    exports org.csc311.capstone;
    exports org.csc311.capstone.models;
    exports org.csc311.capstone.db;
    exports org.csc311.capstone.services;
    exports org.csc311.capstone.storage;
    exports org.csc311.capstone.util;
}