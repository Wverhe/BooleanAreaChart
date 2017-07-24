package com.core.component;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Created by agaspari on 6/20/2017.
 */
public class InfoLabel extends Label {
    public InfoLabel(String text){
        super();
        setPrefWidth(Double.MAX_VALUE);
        setAlignment(Pos.CENTER);
        setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
        setVisible(false);
    }

    public void showError(String error){
        setText(error);
        setTextFill(Color.web("#a21616"));
        setStyle("-fx-border-color: #800000; -fx-background-color: rgba(128, 0, 0, .5); -fx-padding:10px;");
        setVisible(true);
    }

    public void showInfo(String info){
        setText(info);
        setTextFill(Color.web("#479b07"));
        setStyle("-fx-border-color: #439108; -fx-background-color: rgba(67, 145, 8, .5); -fx-padding:10px;");
        setVisible(true);
    }

    public void dismiss(){
        setVisible(false);
    }
}
