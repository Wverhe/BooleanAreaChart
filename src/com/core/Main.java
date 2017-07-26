package com.core;

import com.core.component.CustomButton;
import com.core.component.CustomGridPane;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.*;
import java.util.LinkedList;
import java.util.Scanner;

public class Main extends Application {
    private LinkedList<XYChart.Data> clientA, clientB, serverListA, serverListB;
    private XYChart.Series seriesA, seriesB, seriesC;
    private AreaChart<Number, Number> areaChartA, areaChartB, areaChartC;
    private NumberAxis xAxisA, xAxisB, xAxisC, yAxisA, yAxisB, yAxisC;
    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setMaximized(true);

        clientA = new LinkedList<>();
        clientB = new LinkedList<>();
        serverListA = new LinkedList<>();
        serverListB = new LinkedList<>();

        /*for(int i = 0; i < 1440; i++){
            clientA.add(new XYChart.Data(i, ((int) (Math.random() * 2) == 1) ? 1 : -1));
            clientB.add(new XYChart.Data(i, ((int) (Math.random() * 2) == 1) ? 1 : -1));
        }*/

        xAxisA = new NumberAxis(0, 1, 1);
        xAxisB = new NumberAxis(0, 1, 1);
        xAxisC = new NumberAxis(0, 1, 1);

        yAxisA = new NumberAxis(-1, 1, 1);
        yAxisB = new NumberAxis(-1, 1, 1);
        yAxisC = new NumberAxis(-1, 1, 1);

        areaChartA = new AreaChart<>(xAxisA, yAxisA);
        areaChartB = new AreaChart<>(xAxisB, yAxisB);
        areaChartC = new AreaChart<>(xAxisC, yAxisC);

        areaChartA.setTitle("Boolean A");
        areaChartB.setTitle("Boolean B");
        areaChartC.setTitle("Boolean A&B");

        seriesA = new XYChart.Series();
        seriesA.setName("A");

        seriesB = new XYChart.Series();
        seriesB.setName("B");

        seriesC = new XYChart.Series();
        seriesC.setName("C");

        CustomGridPane gridPane = new CustomGridPane();

        ScrollPane scrollPaneA = new ScrollPane();
        scrollPaneA.setFitToHeight(true);
        areaChartA.getData().addAll(seriesA);
        //areaChartA.setPrefHeight(primaryStage.getHeight()/3);
        //areaChartA.setPrefWidth(20000);
        scrollPaneA.setContent(areaChartA);
        areaChartA.setMinWidth(scrollPaneA.getWidth());

        ScrollPane scrollPaneB = new ScrollPane();
        scrollPaneB.setFitToHeight(true);
        //scrollPaneB.setFitToWidth(true);
        areaChartB.getData().addAll(seriesB);
        /*areaChartB.setPrefWidth(20000);*/
        areaChartA.setMinWidth(scrollPaneA.getWidth());
        scrollPaneB.setContent(areaChartB);

        ScrollPane scrollPaneC = new ScrollPane();
        scrollPaneC.setFitToHeight(true);
        //scrollPaneC.setFitToWidth(true);
        areaChartC.getData().addAll(seriesC);
        /*areaChartC.setPrefWidth(20000);*/
        areaChartC.setMinWidth(scrollPaneB.getWidth());
        scrollPaneC.setContent(areaChartC);

        gridPane.addRows(4);
        gridPane.addColumns(3);
        gridPane.add(scrollPaneA, 0, 0, 3, 1);
        gridPane.add(scrollPaneB, 0, 1, 3, 1);
        gridPane.add(scrollPaneC, 0, 2, 3, 1);
        CustomButton btnRequest = new CustomButton("Request");
        btnRequest.setOnAction(
            e -> {
                try {
                    InetAddress address = InetAddress.getByName("174.138.38.14"); //TODO: Fill IP
                    //InetAddress address = InetAddress.getLocalHost();
                    DatagramSocket datagramSocket = new DatagramSocket();

                    byte[] buffer = "request".getBytes();
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 5001);
                    datagramSocket.send(packet);

                    byte[] buffer2 = new byte[100000];
                    packet = new DatagramPacket(buffer2, buffer2.length);
                    datagramSocket.receive(packet);

                    String response1 = new String(buffer2);
                    System.out.println("HERE1");

                    datagramSocket.receive(packet);

                    String response2 = new String(buffer2);

                    System.out.println(response1 + "-----\n" + response2);

                    clientA.clear();
                    clientB.clear();

                    String[] allLines = response1.split("\n");
                    //TODO: Figure out if allLines.length - 1 or -2
                    for(int i = 0; i < allLines.length - 2; i++){
                        String index = allLines[i].split(":")[0];
                        String value = allLines[i].split(":")[1];
                        System.out.println(index + ":" + value);
                        serverListA.add(new XYChart.Data(Integer.parseInt(index), Integer.parseInt(value)));
                    }

                    allLines = response2.split("\n");
                    //TODO: WHY DOES THIS HAVE TO BE -2
                    for(int i = 0; i < allLines.length - 2; i++){
                        String index = allLines[i].split(":")[0];
                        String value = allLines[i].split(":")[1];
                        serverListB.add(new XYChart.Data(Integer.parseInt(index), Integer.parseInt(value)));
                    }
                    clientA = serverListA;
                    clientB = serverListB;
                    updateSeries(clientA, clientB);
                    fillSeries();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        );
        gridPane.add(btnRequest, 1, 3, 1, 1);

        primaryStage.setTitle("Boolean Comparison Graph");
        Scene scene = new Scene(gridPane, 300, 275);
        scene.getStylesheets().add("Area.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateSeries(LinkedList<XYChart.Data> data1, LinkedList<XYChart.Data> data2){
        xAxisA.setLowerBound((Integer) data1.get(0).getXValue());
        xAxisA.setUpperBound((Integer) data1.getLast().getXValue());
        xAxisB.setLowerBound((Integer) data2.get(0).getXValue());
        xAxisB.setUpperBound((Integer) data2.getLast().getXValue());
        xAxisC.setLowerBound((Integer) data1.get(0).getXValue());
        xAxisC.setUpperBound((Integer) data1.getLast().getXValue());
        areaChartA.setPrefWidth(data1.size() * 10);
        areaChartB.setPrefWidth(data2.size() * 10);
        areaChartC.setPrefWidth(data1.size() * 10);
    }

    private void fillSeries(){
        seriesA.getData().clear();
        seriesB.getData().clear();
        seriesC.getData().clear();

        int prevY = (int) clientA.get(0).getYValue();
        for (int i = 0; i < clientA.size(); i++) {
            int tempXPoint = (int) clientA.get(i).getXValue();
            int tempYPoint = (int) clientA.get(i).getYValue();

            if (tempYPoint != prevY) {
                seriesA.getData().add(new XYChart.Data<>(tempXPoint, tempYPoint * -1));
                prevY = tempYPoint;
            }
            seriesA.getData().add(clientA.get(i));
        }

        prevY = (int) clientB.get(0).getYValue();
        for (int i = 0; i < clientB.size(); i++) {
            int tempXPoint = (int) clientB.get(i).getXValue();
            int tempYPoint = (int) clientB.get(i).getYValue();
            if (tempYPoint != prevY) {
                seriesB.getData().add(new XYChart.Data<>(tempXPoint, tempYPoint * -1));
                prevY = tempYPoint;
            }
            seriesB.getData().add(clientB.get(i));
        }

        prevY = ((int)clientA.get(0).getYValue() == 1 && (int) clientB.get(0).getYValue() == 1) ? 1 : -1;
        for (int i = 0; i < clientA.size() && i < clientB.size(); i++) {
            int tempXPoint = (int)clientA.get(i).getXValue();
            int tempYPoint = ((int)clientA.get(i).getYValue() == 1 && (int) clientB.get(i).getYValue() == 1) ? 1 : -1;
            //System.out.println("i: " +  i + " A: " + clientA.get(i).getYValue() + " B: " + clientB.get(i).getYValue() + " TempValue: " + tempPoint + " prevY: " + prevY);
            if (tempYPoint != prevY) {
                seriesC.getData().add(new XYChart.Data<>(tempXPoint, tempYPoint * -1));
                //System.out.println("Duplicate at: "  + i);
                prevY = tempYPoint;
            }
            seriesC.getData().add(new XYChart.Data(tempXPoint, tempYPoint));
        }
        //TODO: Remove this Below Probably
        areaChartA.applyCss();
        areaChartA.layout();
    }

    public static void main(String[] args) {
        launch(args);
    }
}