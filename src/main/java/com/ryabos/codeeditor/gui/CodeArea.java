package com.ryabos.codeeditor.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class CodeArea extends Control {
    private final StringProperty text = new SimpleStringProperty("");

    public String getText() {
        return text.get();
    }

    public void setText(String text) {
        this.text.set(text);
    }

    public StringProperty textProperty() {
        return text;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new CodeAreaSkin(this);
    }

    private static class CodeAreaSkin extends SkinBase<CodeArea> {
        static final Font MONOSPACED_FONT = Font.font("monospaced");
        static final int CELL_HEIGHT = 15;
        static final int CELL_WIDTH = 8;
        private Rectangle[] cells = new Rectangle[0];
        private Text[] symbols = new Text[0];
        private StackPane[] panes = new StackPane[0];
        private boolean invalidControl = true;

        CodeAreaSkin(CodeArea control) {
            super(control);
            control.widthProperty().addListener((observable, oldValue, newValue) -> invalidControl = true);
            control.heightProperty().addListener((observable, oldValue, newValue) -> invalidControl = true);
            control.textProperty().addListener((observable, oldValue, newValue) -> updateControl());
        }

        private void updateControl() {
            String text = getSkinnable().getText();
            updateSymblos(text);
            updateCells(text.length());
            updatePanes(text.length());
        }

        private void updatePanes(int size) {
            if (panes != null) {
                for (StackPane pane : panes) {
                    getChildren().remove(pane);
                }
            }
            panes = new StackPane[size];

            for (int i = 0; i < size; i++) {
                StackPane pane = new StackPane(cells[i], symbols[i]);
                pane.setAlignment(Pos.TOP_LEFT);
                getChildren().add(pane);
                panes[i] = pane;
            }
        }

        private void updateSymblos(String text) {
            if (symbols != null) {
                for (Text symbol : symbols) {
                    getChildren().remove(symbol);
                }
            }
            symbols = new Text[text.length()];

            for (int i = 0; i < text.length(); i++) {
                Text symbol = new Text(String.valueOf(text.charAt(i)));
                symbol.setFont(MONOSPACED_FONT);
                symbols[i] = symbol;
            }
        }

        private void updateCells(int size) {
            if (cells != null) {
                for (Rectangle cell : cells) {
                    getChildren().remove(cell);
                }
            }
            cells = new Rectangle[size];

            for (int i = 0; i < size; i++) {
                Rectangle cell = new Rectangle(CELL_WIDTH, CELL_HEIGHT);
                cell.setFill(Color.WHITE);
                cells[i] = cell;
            }
        }

        @Override
        protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
            return 200 + leftInset + rightInset;
        }

        @Override
        protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
            return 50 + topInset + bottomInset;
        }

        @Override
        protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
            if (invalidControl) {
                updateControl();
                invalidControl = false;
            }
            double currentX = 0;
            for (StackPane pane : panes) {
                layoutInArea(pane, currentX, contentY, contentWidth, contentHeight, -1, HPos.LEFT, VPos.TOP);
                currentX += CELL_WIDTH;
            }
        }
    }
}