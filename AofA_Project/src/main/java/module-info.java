module red.black.aofa_project {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens red.black.aofa_project to javafx.fxml;
    opens red.black.aofa_project.models to javafx.base;
    exports red.black.aofa_project;
    exports red.black.aofa_project.controllers;
    opens red.black.aofa_project.controllers to javafx.fxml;
}