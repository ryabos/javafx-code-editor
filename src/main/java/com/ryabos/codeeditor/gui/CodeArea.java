package com.ryabos.codeeditor.gui;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class CodeArea extends Control {
    private final StringProperty text = new SimpleStringProperty("");
    private final IntegerProperty columnCount = new SimpleIntegerProperty(75);
    private final IntegerProperty offset = new SimpleIntegerProperty(0);
    private final IntegerProperty caretPosition = new SimpleIntegerProperty(0);

    public int getCaretPosition() {
        return caretPosition.get();
    }

    public void setCaretPosition(int caretPosition) {
        this.caretPosition.set(caretPosition);
    }

    public IntegerProperty caretPositionProperty() {
        return caretPosition;
    }

    public int getColumnCount() {
        return columnCount.get();
    }

    public void setColumnCount(int columnCount) {
        this.columnCount.set(columnCount);
    }

    public IntegerProperty columnCountProperty() {
        return columnCount;
    }

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

    private int getOffset() {
        return offset.get();
    }

    private void setOffset(int offset) {
        this.offset.set(offset);
    }

    private IntegerProperty offsetProperty() {
        return offset;
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
            control.columnCountProperty().addListener((observable, oldValue, newValue) -> updateControl());
            control.caretPositionProperty().addListener((observable, oldValue, newValue) -> updateControl());
            control.offsetProperty().addListener((observable, oldValue, newValue) -> updateControl());
        }

        @Override
        protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
            if (invalidControl) {
                updateControl();
                invalidControl = false;
            }
            double currentX = 0;
            double currentY = 0;

            for (int i = 0; i < Math.min(panes.length, getSkinnable().getColumnCount()); i++) {
                StackPane pane = panes[i];
                if (getSkinnable().getText().charAt(i) == '\n') {
                    currentX = 0;
                    currentY += CELL_HEIGHT;
                    getChildren().remove(pane);
                } else {
                    layoutInArea(pane, currentX, currentY, contentWidth, contentHeight, -1, HPos.LEFT, VPos.TOP);
                    currentX += CELL_WIDTH;
                }
            }
        }

        @Override
        protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
            return getSkinnable().getColumnCount() * CELL_WIDTH;
        }

        @Override
        protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
            return CELL_HEIGHT;
        }

        @Override
        protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
            return computeMinWidth(height, topInset, rightInset, bottomInset, leftInset);
        }

        @Override
        protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
            return computeMinHeight(width, topInset, rightInset, bottomInset, leftInset);
        }

        @Override
        protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
            return computeMinWidth(height, topInset, rightInset, bottomInset, leftInset);
        }

        @Override
        protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
            return computeMinHeight(width, topInset, rightInset, bottomInset, leftInset);
        }

        private void updateControl() {
            String text = getSkinnable().getText();
            updateSymblos(text);
            updateCells(text.length());
            updatePanes(text.length());
        }

        private void updateSymblos(String text) {
            int size = Math.min(text.length(), getSkinnable().getColumnCount());
            if (symbols != null) {
                for (Text symbol : symbols) {
                    getChildren().remove(symbol);
                }
            }
            symbols = new Text[size];

            int offset = getSkinnable().getOffset();
            for (int i = 0; i < size; i++) {
                String s = String.valueOf(text.charAt(offset + i));
                Text symbol = new Text(s);
                symbol.setFont(MONOSPACED_FONT);
                symbols[i] = symbol;
            }
        }

        private void updateCells(int size) {
            size = Math.min(size, getSkinnable().getColumnCount());
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

        private void updatePanes(int size) {
            size = Math.min(size, getSkinnable().getColumnCount());
            if (panes != null) {
                for (StackPane pane : panes) {
                    getChildren().remove(pane);
                }
            }
            panes = new StackPane[size];

            for (int i = 0; i < size; i++) {
                StackPane pane = new StackPane(cells[i], symbols[i]);
                pane.setAlignment(Pos.TOP_LEFT);
                if (i == getSkinnable().getCaretPosition()) {
                    Path path = new Path();
                    path.getElements().add(new MoveTo(0, 0));
                    path.getElements().add(new LineTo(0, CELL_HEIGHT));
                    path.getElements().add(new ClosePath());
                    path.setFill(Color.BLACK);
                    path.visibleProperty().bind(getSkinnable().focusedProperty());
                    pane.getChildren().add(path);
                    FadeTransition fadeTransition = new FadeTransition(Duration.millis(900));
                    fadeTransition.setNode(path);
                    fadeTransition.setFromValue(1);
                    fadeTransition.setToValue(0);
                    fadeTransition.setCycleCount(Animation.INDEFINITE);
                    fadeTransition.play();
                }
                int finalI = i;
                pane.setOnMouseClicked(event -> {
                    getSkinnable().setCaretPosition(finalI);
                    getSkinnable().requestFocus();
                });
                getSkinnable().setOnKeyPressed(event -> {
                    final int caretPosition = getSkinnable().getCaretPosition();
                    if (event.getCode() == KeyCode.RIGHT &&
                            caretPosition < getSkinnable().getText().length() - getSkinnable().getOffset() - 1) {
                        if (caretPosition < getSkinnable().getColumnCount() - 2 ||
                                caretPosition + getSkinnable().getOffset() >= getSkinnable().getText().length() - 2) {
                            getSkinnable().setCaretPosition(caretPosition + 1);
                            event.consume();
                        } else {
                            if (getSkinnable().getOffset() + getSkinnable().getColumnCount() <
                                    getSkinnable().getText().length()) {
                                getSkinnable().setOffset(getSkinnable().getOffset() + 1);
                                event.consume();
                            }
                        }
                    } else if (event.getCode() == KeyCode.LEFT &&
                            caretPosition > 0) {
                        if (getSkinnable().getOffset() > 0 && getSkinnable().getCaretPosition() == 2) {
                            getSkinnable().setOffset(getSkinnable().getOffset() - 1);
                        } else {
                            getSkinnable().setCaretPosition(caretPosition - 1);
                        }
                        event.consume();
                    } else if (event.getCode() == KeyCode.BACK_SPACE) {
                        if (caretPosition > 0) {
                            getSkinnable().setCaretPosition(caretPosition - 1);
                            getSkinnable().setText(
                                    getSkinnable().getText().substring(0, caretPosition - 1) +
                                            getSkinnable().getText().substring(caretPosition)
                            );
                        }
                        event.consume();
                    } else if (event.getCode() == KeyCode.DELETE) {
                        if (caretPosition < getSkinnable().getText().length() - 1) {
                            getSkinnable().setText(
                                    getSkinnable().getText().substring(0, caretPosition) +
                                            getSkinnable().getText().substring(caretPosition + 1));
                            getSkinnable().setCaretPosition(caretPosition);
                        }
                        event.consume();
                    } else {
                        String substring = getSkinnable().getText().substring(0, caretPosition);
                        String code = "";
                        if (event.getCode().isLetterKey()) {
                            code = event.isShiftDown() ? event.getText().toUpperCase() : event.getText();
                        } else if (event.getCode().isDigitKey()) {
                            code = event.isShiftDown() ? event.getText().toUpperCase() : event.getText();
                        }
                        String substring1 = getSkinnable().getText().substring(caretPosition);
                        String text = substring + code + substring1;
                        getSkinnable().setText(text);
                        getSkinnable().setCaretPosition(substring.length() + code.length());
                        event.consume();
                    }
                });
                getChildren().add(pane);
                panes[i] = pane;
            }
        }
    }
}