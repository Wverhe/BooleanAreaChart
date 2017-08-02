package com.core;

import com.core.component.CustomButton;
import com.core.component.CustomGridPane;
import com.core.component.InfoLabel;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.LinkedList;

public class Main extends Application {
    private LinkedList<XYChart.Data> clientA, clientB, serverListA, serverListB;
    private XYChart.Series seriesA, seriesB, seriesC;
    private AreaChart<Number, Number> areaChartA, areaChartB, areaChartC;
    private NumberAxis xAxisA, xAxisB, xAxisC, yAxisA, yAxisB, yAxisC;
    private DatePicker datePicker;
    private InfoLabel lblInfo;

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
        gridPane.setGridLinesVisible(true);

        ScrollPane scrollPaneA = new ScrollPane();
        scrollPaneA.setFitToHeight(true);
        areaChartA.getData().addAll(seriesA);
        areaChartA.setMinWidth(1000);
        scrollPaneA.setContent(areaChartA);

        ScrollPane scrollPaneB = new ScrollPane();
        scrollPaneB.setFitToHeight(true);
        areaChartB.getData().addAll(seriesB);
        areaChartB.setMinWidth(1000);
        scrollPaneB.setContent(areaChartB);

        ScrollPane scrollPaneC = new ScrollPane();
        scrollPaneC.setFitToHeight(true);
        areaChartC.getData().addAll(seriesC);
        areaChartC.setMinWidth(1000);
        scrollPaneC.setContent(areaChartC);

        gridPane.addRows(5);
        gridPane.addColumns(3);
        gridPane.add(scrollPaneA, 0, 0, 3, 1);
        gridPane.add(scrollPaneB, 0, 1, 3, 1);
        gridPane.add(scrollPaneC, 0, 2, 3, 1);
        CustomButton btnRequest = new CustomButton("Request");
        btnRequest.setOnAction(
            e -> {
                try {
                    InetAddress address = InetAddress.getByName("174.138.38.14");
                    //InetAddress address = InetAddress.getLocalHost();
                    DatagramSocket datagramSocket = new DatagramSocket();

                    byte[] buffer = ("request:" + datePicker.getValue().getMonthValue() + "/" + datePicker.getValue().getDayOfMonth() + "/" + datePicker.getValue().getYear()).getBytes();
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 5001);
                    datagramSocket.send(packet);

                    byte[] buffer2 = new byte[100000];
                    packet = new DatagramPacket(buffer2, buffer2.length);
                    datagramSocket.receive(packet);
                    String response1 = new String(buffer2);

                    if(response1.contains("Invalid")){
                        lblInfo.showError("Error: " + response1);
                    }else {
                        lblInfo.dismiss();
                        datagramSocket.receive(packet);
                        String response2 = new String(buffer2);

                        clientA.clear();
                        clientB.clear();

                        String[] allLines = response1.split("\n");
                        //TODO: Figure out if allLines.length - 1 or -2 [Should be -1 due to new line at bottom of file, but I honestly don't know]
                        for (int i = 0; i < allLines.length - 2; i++) {
                            String index = allLines[i].split(":")[0];
                            String value = allLines[i].split(":")[1];
                            System.out.println(index + ":" + value);
                            serverListA.add(new XYChart.Data(Integer.parseInt(index), Integer.parseInt(value)));
                        }

                        allLines = response2.split("\n");
                        //TODO: WHY DOES THIS HAVE TO BE -2
                        for (int i = 0; i < allLines.length - 2; i++) {
                            String index = allLines[i].split(":")[0];
                            String value = allLines[i].split(":")[1];
                            serverListB.add(new XYChart.Data(Integer.parseInt(index), Integer.parseInt(value)));
                        }
                        clientA = serverListA;
                        clientB = serverListB;
                        if(clientA.size() > 0 && clientB.size() > 0){
                            updateSeries(clientA, clientB);
                            fillSeries();
                        }
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        );
        CustomButton btnDownload = new CustomButton("Download");

        btnDownload.setOnAction(
            e -> {
                WritableImage image = areaChartA.snapshot(new SnapshotParameters(), null);
                File dirDownloads = new File("Downloads");
                dirDownloads.mkdir();
                File fileContainer = new File("Downloads/" + datePicker.getValue());
                fileContainer.mkdir();

                File fileA = new File("Downloads/" + datePicker.getValue() + "/GraphA.png");
                File fileB = new File("Downloads/" + datePicker.getValue() + "/GraphB.png");
                File fileC = new File("Downloads/" + datePicker.getValue() + "/GraphC.png");
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", fileA);
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", fileB);
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", fileC);
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
                e.consume();
            }
        );
        gridPane.add(btnRequest, 1, 3, 1, 1);
        gridPane.add(btnDownload, 2, 3, 1, 1);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -5);//Adjusting for NY time difference.
        datePicker = new DatePicker();
        datePicker.setValue(LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)));//Month is always off by 1?
        datePicker.setPrefWidth(Double.MAX_VALUE);
        gridPane.add(datePicker, 0, 3, 1, 1);
        lblInfo = new InfoLabel("");
        gridPane.add(lblInfo, 0, 4, 3, 1);

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
                seriesA.getData().add(clientA.get(i));
                seriesA.getData().add(new XYChart.Data<>(tempXPoint, tempYPoint * -1));
                prevY = tempYPoint;
            }
        }

        prevY = (int) clientB.get(0).getYValue();
        for (int i = 0; i < clientB.size(); i++) {
            int tempXPoint = (int) clientB.get(i).getXValue();
            int tempYPoint = (int) clientB.get(i).getYValue();
            if (tempYPoint != prevY) {
                seriesB.getData().add(clientB.get(i));
                seriesB.getData().add(new XYChart.Data<>(tempXPoint, tempYPoint * -1));
                prevY = tempYPoint;
            }
        }

        prevY = ((int)clientA.get(0).getYValue() == 1 && (int) clientB.get(0).getYValue() == 1) ? 1 : -1;
        for (int i = 0; i < clientA.size() && i < clientB.size(); i++) {
            int tempXPoint = (int)clientA.get(i).getXValue();
            int tempYPoint = ((int)clientA.get(i).getYValue() == 1 && (int) clientB.get(i).getYValue() == 1) ? 1 : -1;
            //System.out.println("i: " +  i + " A: " + clientA.get(i).getYValue() + " B: " + clientB.get(i).getYValue() + " TempValue: " + tempPoint + " prevY: " + prevY);
            if (tempYPoint != prevY) {
                seriesC.getData().add(new XYChart.Data(tempXPoint, tempYPoint));
                seriesC.getData().add(new XYChart.Data<>(tempXPoint, tempYPoint * -1));
                //System.out.println("Duplicate at: "  + i);
                prevY = tempYPoint;
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}