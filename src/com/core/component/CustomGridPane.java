package com.core.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

/**
 * Created by agaspari on 6/20/2017.
 */
public class CustomGridPane extends GridPane{
    private ColumnConstraints column;
    private RowConstraints row;
    public CustomGridPane(){
        super();
        setGridLinesVisible(false);
        column = new ColumnConstraints();
        column.setHgrow(Priority.ALWAYS);
        row = new RowConstraints();
        row.setVgrow(Priority.ALWAYS);
        setAlignment(Pos.TOP_LEFT);
        setVgap(5);
        setHgap(5);
        setPadding(new Insets(5));
        setStyle("-fx-background-color: #3c3c3c;");
    }

    public void addColumns(int numColumns){
        column.setPercentWidth(100/numColumns);
        for(int i = 0; i < numColumns; i++){
            getColumnConstraints().add(column);
        }
    }

    public void addRows(int numRows){
        row.setPercentHeight(100/numRows);
        for(int i = 0; i < numRows; i++){
            getRowConstraints().add(row);
        }
    }
}
