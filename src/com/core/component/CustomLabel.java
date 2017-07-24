package com.core.component;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Created by agaspari on 6/21/2017.
 */
public class CustomLabel extends Label {
    public CustomLabel(String text){
        super(text);
        setAlignment(Pos.TOP_LEFT);
        setMinWidth(50);
        //setPrefWidth(Double.MAX_VALUE);
        setTextFill(Color.WHITE);
        //setStyle("-fx-border-width: .5px; -fx-border-style: hidden hidden solid hidden; -fx-border-color: white;");
        setFont(Font.font("Tahoma", FontWeight.SEMI_BOLD, 12));
    }
}
