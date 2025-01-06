module org.cristina.generaredocsrl {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    exports org.cristina.generaredocsrl;

    requires java.sql;
    requires org.postgresql.jdbc;
    requires jdk.jshell;
    requires org.apache.poi.ooxml;

    exports org.cristina.generaredocsrl.model;

    opens org.cristina.generaredocsrl.connection to javafx.sql;
    exports org.cristina.generaredocsrl.connection;

    opens org.cristina.generaredocsrl.repository to javafx.sql;
    exports org.cristina.generaredocsrl.repository;
    opens org.cristina.generaredocsrl to javafx.fxml, javafx.sql;
    opens org.cristina.generaredocsrl.model to javafx.base, javafx.fxml, javafx.sql;

}