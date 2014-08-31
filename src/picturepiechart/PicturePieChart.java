/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package picturepiechart;

import com.sun.javafx.charts.Legend;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 *
 * @author Daniel
 */
public class PicturePieChart extends PieChart {

    ObservableList<PieChart.Data> slices;
    WritableImage w;

    public PicturePieChart(File image, Color color, boolean useAlpha) {
        super();

        slices = analyseImage(image, color, useAlpha);

        setData(slices);
        for (Data data : slices) {
            //System.out.println(data.getName());
            data.getNode().setStyle("-fx-pie-color: #" + data.getName());

        }
        
        setLegendVisible(false);
        setLabelsVisible(false);
        

        drawImageOnTop();

    }

    private ObservableList<Data> analyseImage(File image, Color colorToIgnore, boolean useAlpha) {
        try {
            Map<String, Double> colors = new HashMap();
            Image i = new Image(new FileInputStream(image));
            PixelReader pr = i.getPixelReader();
            w = new WritableImage(pr, (int) i.getWidth(), (int) i.getHeight());
            PixelWriter pw = w.getPixelWriter();

            //iterate over x
            for (int x = 0; x < w.getWidth(); x++) {
                //iterate over y
                for (int y = 0; y < w.getHeight(); y++) {
                    
                    Color colorHere = pr.getColor(x, y);
                    if (useAlpha && colorHere.getOpacity() == 0.0) {
                        continue;

                    } else if (!useAlpha && (colorHere.getBlue() == colorToIgnore.getBlue()
                            && colorHere.getRed() == colorToIgnore.getRed()
                            && colorHere.getGreen() == colorToIgnore.getGreen()
                            && colorHere.getOpacity() == colorToIgnore.getOpacity())) {
                        Color colorNew = new Color(0, 0, 0, 0);
                        pw.setColor(x, y, colorNew);
                        continue;
                    }

                    String hex = Integer.toHexString(pr.getArgb(x, y));
                    if (hex.equals("0")) {
                        continue;

                    }
                    hex = hex.substring(2);
                    if (colors.containsKey(hex)) {
                        colors.put(hex, colors.get(hex) + 1.0);

                    } else {
                        colors.put(hex, 1.0);
                    }

                }

            }
            // now convert to the ObservableList
            ObservableList<PieChart.Data> ret = FXCollections.observableArrayList();
            for (Map.Entry<String, Double> entry : colors.entrySet()) {
                String string = entry.getKey();
                Double double1 = entry.getValue();
                PieChart.Data pd = new Data(string, double1);
                ret.add(pd);
            }
            return ret;

        } catch (IOException ex) {
            Logger.getLogger(PicturePieChart.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void drawImageOnTop() {

        ImageView view = new ImageView(w);
        /**
         * double currentWidth = view.getFitWidth(), currentHeight =
         * view.getFitHeight(); //What size do I want => 1/3 of my size double
         * goalWidth = 100, goalHeight = 100;
         *
         * double scaleWidth = goalWidth / currentWidth, scaleHeight =
         * goalHeight / currentHeight;
        * *
         */
        view.setPreserveRatio(true);
        view.fitWidthProperty().bind(this.widthProperty().divide(4));
        view.fitHeightProperty().bind(this.widthProperty().divide(4));


        view.xProperty().bind(this.widthProperty().divide(2).subtract(view.fitHeightProperty().divide(2)));
        view.yProperty().bind(this.heightProperty().divide(2).subtract(view.fitHeightProperty().divide(2)));
        view.setSmooth(false);
        this.getChartChildren().add(view);
        

    }

}
