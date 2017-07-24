package com.core.component;

import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

/**
 * Created by agaspari on 6/20/2017.
 */
public class CustomButton extends Button{

    public CustomButton(String text) {
        super(text);
        setTextFill(Color.WHITE);
        setFont(javafx.scene.text.Font.font("Tahoma", FontWeight.BOLD, 10));
        setPrefWidth(Double.MAX_VALUE);
        setStyle("-fx-background-color: #1d8893; -fx-border-radius: 4px;");
        setCursor(Cursor.HAND);
    }
}