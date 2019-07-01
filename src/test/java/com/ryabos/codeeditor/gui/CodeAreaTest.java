package com.ryabos.codeeditor.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CodeAreaTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        CodeArea codeArea = new CodeArea();
        codeArea.setPadding(new Insets(10));

        TextArea textArea = new TextArea();
        textArea.textProperty().addListener((observable, oldValue, newValue) -> codeArea.setText(newValue));
        codeArea.textProperty().addListener((observable, oldValue, newValue) -> textArea.setText(newValue));
        textArea.setText("12345678901234567890");

        Spinner<Integer> columnCountSpinner = new Spinner<>(1, 1000, 1);
        columnCountSpinner.valueProperty().addListener((observable, oldValue, newValue) ->
                codeArea.setColumnCount(newValue));
        columnCountSpinner.getValueFactory().setValue(10);

        Spinner<Integer> cursorPositionSpinner = new Spinner<>(0, 1000, 0);
        textArea.textProperty().addListener((observable, oldValue, newValue) -> cursorPositionSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, newValue.length(), codeArea.getCaretPosition())));
        cursorPositionSpinner.valueProperty().addListener((observable, oldValue, newValue) ->
                codeArea.setCaretPosition(newValue));
        codeArea.caretPositionProperty().addListener((observable, oldValue, newValue) ->
                cursorPositionSpinner.getValueFactory().setValue(newValue.intValue()));
        cursorPositionSpinner.getValueFactory().setValue(1);

        primaryStage.setScene(new Scene(new HBox(
                codeArea,
                new VBox(
                        textArea,
                        new HBox(new Label("Ширина"), columnCountSpinner),
                        new HBox(new Label("Курсор"), cursorPositionSpinner)
                ))));
        primaryStage.show();
    }
}
