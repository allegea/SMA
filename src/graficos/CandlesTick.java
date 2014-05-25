/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package graficos;
import java.sql.*;
import basedatos.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;

import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.date.DateUtilities;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

/**
 * A demo showing a candlestick chart.
 *
 * @author David Gilbert
 */
public class CandlesTick extends JFrame {

    int repeticion;
    String nombre;
    /**
     * A demonstration application showing a candlestick chart.
     *
     * @param title  the frame title.
     */
    public CandlesTick(String title,String nombr ,int repet) {

        super(title+" "+nombr);
        repeticion = repet;
        nombre = nombr;

        DefaultHighLowDataset dataset = createHighLowDataset();
        JFreeChart chart = createChart(dataset);
        chart.getXYPlot().setOrientation(PlotOrientation.VERTICAL);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

    }

     public DefaultHighLowDataset createHighLowDataset() {

         int fecha;
        Calendar actual = Calendar.getInstance();



        ArrayList<Double> maximo = new ArrayList<Double>();
        ArrayList<Double> minimo = new ArrayList<Double>();
        ArrayList<Double> inicio = new ArrayList<Double>();
        ArrayList<Double> cierre = new ArrayList<Double>();
        ArrayList<Double> cantidad = new ArrayList<Double>();
        ArrayList<Date> calendario = new ArrayList<Date>();

        ConexionBD BD = new ConexionBD("mysql");
        ResultSet r = BD.consulta("SELECT fecha, precioInicio, precioCierre, precioMax, precioMin, cantidad FROM cotizacion WHERE (repeticion = 0 or repeticion = "+repeticion+") AND IDProducto = (SELECT IDProducto FROM producto WHERE nombre = \""+nombre+"\") GROUP BY fecha");
        //ResultSet r = BD.consulta("SELECT fecha, precioPromedio, count(fecha) as volumen FROM cotizacion WHERE IDProducto = "+IdProducto+" GROUP BY fecha");
        

            try {
         while(r.next())
                {   fecha = r.getInt(1);
                    actual = Calendar.getInstance();

                   
                   //System.out.println(actual.get(Calendar.DATE) + " " +actual.get(Calendar.MONTH)+ " " + actual.get(Calendar.YEAR) + " : "+fecha);
                   actual.add(Calendar.DATE, fecha-1);

                   inicio.add(r.getDouble(2));
                   cierre.add(r.getDouble(3));
                   maximo.add(r.getDouble(4));
                   minimo.add(r.getDouble(5));
                   cantidad.add(r.getDouble(6));
                   calendario.add(DateUtilities.createDate(actual.get(Calendar.YEAR), actual.get(Calendar.MONTH)+1, actual.get(Calendar.DATE)));

                }

                BD.cerrarConexion();
            }
        catch (SQLException ex) {
                    ex.printStackTrace();
                }

        //Double[] abcd = (Double[])arraynumbers.ToArray(typeof(Double));




        Date[] date = new Date[inicio.size()];
        double[] high = new double[inicio.size()];
        double[] low = new double[inicio.size()];
        double[] open = new double[inicio.size()];
        double[] close = new double[inicio.size()];
        double[] volume = new double[inicio.size()];

        int j = 0;
        for(int i=0;i<inicio.size();i++)
        {
            //if(minimo.get(i)!=0){
            high[j] = maximo.get(i);
            low[j] = minimo.get(i);
            open[j] = inicio.get(i);
            close[j] = cierre.get(i);
            volume[j] = 0;//cantidad.get(i);
            date[j] = calendario.get(i);
            j++;
           // }

        }


        return new DefaultHighLowDataset(nombre, date, high, low, open, close, volume);

    }

    /**
     * Creates a chart.
     *
     * @param dataset  the dataset.
     *
     * @return The dataset.
     */
    private JFreeChart createChart(DefaultHighLowDataset dataset) {
        JFreeChart chart = ChartFactory.createCandlestickChart(
            //nombre,
                "",
            "Date",
            "Value",
            dataset,
            true
        );


        XYPlot plot = (XYPlot) chart.getPlot();
        NumberAxis rangeAxis1 = (NumberAxis) plot.getRangeAxis();
        rangeAxis1.setAutoRange(true);

        Calendar actual = Calendar.getInstance();
        //actual.add(Calendar.DATE, -1);
         Hour hour = new Hour(2, new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH)+1,  actual.get(Calendar.YEAR)));

        Minute min = new Minute(15, hour);
        double millis = min.getFirstMillisecond();
        Marker currentEnd = new ValueMarker(millis);
        currentEnd.setPaint(Color.BLACK);
        //currentEnd.setLabel("Real");
        currentEnd.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
        currentEnd.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
        plot.addDomainMarker(currentEnd);

        return chart;
    }


    public static void main(String[] args) {

        CandlesTick demo = new CandlesTick("Candlestick Demo ","PREC",1);
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}