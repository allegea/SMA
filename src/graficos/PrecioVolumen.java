
package graficos;

import java.awt.Color;
import java.awt.Paint;
import java.sql.*;
import basedatos.*;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
//import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleAnchor;
//import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

/**
 * A demonstration application showing how to create a price-volume chart.
 *
 *///SELECT fecha, precioPromedio, count(fecha) as volumen FROM cotizacion WHERE IDProducto = (SELECT IDProducto FROM producto WHERE nombre = "ecopetrol") AND fecha > -1 GROUP BY fecha
public class PrecioVolumen extends JFrame {
ConexionBD BD;

String accion = new String();
int repeticion;
     double millis;

    public PrecioVolumen(String title, String nombre, int repet) {

        
        super(title+" "+nombre);

        repeticion = repet;
        accion = nombre;


        final JFreeChart chart = createChart();
        final ChartPanel panel = new ChartPanel(chart, true, true, true, false, true);
        panel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(panel);

    }

    /**
     * Creates a chart.
     *
     * @return a chart.
     */
    private JFreeChart createChart() {

        boolean mostrarLegend = false;
        final XYDataset priceData = createPriceDataset();
        //final String title = ""+accion;
        final String title = "";
        final JFreeChart chart = ChartFactory.createTimeSeriesChart(
            title, 
            "Dates",
           // "Price(COL$)",
            "Price(COL$ x 1.000)",
            priceData, 
            mostrarLegend,
            true,
            true
        );
        final XYPlot plot = chart.getXYPlot();
        final NumberAxis rangeAxis1 = (NumberAxis) plot.getRangeAxis();
        rangeAxis1.setLowerMargin(0.40);  // to leave room for volume bars
        //rangeAxis1.setVisible(false);
        
        ////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////
        //rangeAxis1.setLabelFont(new Font("Times New Roman",Font.PLAIN,12));
        ////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////
        
        final DecimalFormat format = new DecimalFormat("#,###.###");
        rangeAxis1.setNumberFormatOverride(format);

        final XYItemRenderer renderer1 = plot.getRenderer();
        renderer1.setToolTipGenerator(
            new StandardXYToolTipGenerator(
                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("#.##")
            )
        );
        //renderer1.setSeriesItemLabelFont(0, new Font("Times New Roman",Font.PLAIN,12));

       //renderer1.setSeriesPaint(0, Color.red);
        

        final NumberAxis rangeAxis2 = new NumberAxis("Volume (1.000.000.000)");
        

        ////////////////////////////////////////////////////////////////////////////////
        ////////////Cambiar tipo de fuente al titulo//////////////////////////////////
        //rangeAxis2.setLabelFont(new Font("Times New Roman",Font.PLAIN,12));
        ////////////Cambiar tipo de fuente a los números//////////////////////////////////
        //rangeAxis2.setTickLabelFont(new Font("Times New Roman",Font.PLAIN,12));
        ////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////
        //rangeAxis2.setTickLabelsVisible(false);
        
        //rangeAxis2.setVisible(false);
        rangeAxis2.setUpperMargin(1.00);  // to leave room for price line
        plot.setRangeAxis(1, rangeAxis2);
        plot.setDataset(1, createVolumeDataset());
        plot.setRangeAxis(1, rangeAxis2);

       /* ValueAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelFont(new Font("SanSerif",Font.PLAIN,36));
        plot.setDomainAxis(domainAxis);*/

        plot.mapDatasetToRangeAxis(1, 1);
        final XYBarRenderer renderer2 = new XYBarRenderer(0.20);
        renderer2.setToolTipGenerator(
            new StandardXYToolTipGenerator(
                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0,000.00")//Cambiar formato
            )
        );
        //////////////////////////////////
        //Cambiar tipo de fuente al volumen
        //renderer2.setItemLabelFont(new Font("Times New Roman",Font.PLAIN,12));
        //renderer2.setSeriesItemLabelFont(0, new Font("Times New Roman",Font.PLAIN,12));
        ///////////////////////////////////
        ///////////////////////////////////
      
        if(!mostrarLegend)
       renderer2.setSeriesPaint(0, new Color(85,85,255));
       
        plot.setRenderer(1, renderer2);
        
       // Paint seriesPaint = plot.getRenderer().getSeriesPaint(1);
        
       // renderer1.setSeriesPaint(0, Color.RED);
        if(!mostrarLegend)
        renderer1.setSeriesPaint(0, new Color(255,85,85));
       // renderer1.setSeriesPaint(1, Color.RED);
        
       // renderer1.setSeriesPaint(1, new Color(0f,0f,255f));
        
        
        Calendar actual = Calendar.getInstance();
        actual.add(Calendar.DATE, -240);
         Hour hour = new Hour(2, new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH)+1,  actual.get(Calendar.YEAR)));

        Minute min = new Minute(15, hour);
        millis = min.getFirstMillisecond();
        Marker currentEnd = new ValueMarker(millis);
        currentEnd.setPaint(Color.BLACK);
        //currentEnd.setLabel("Real");
        currentEnd.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
        currentEnd.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
        plot.addDomainMarker(currentEnd);

        return chart;

    }

    /**
     * Creates a sample dataset.
     *
     * @return A sample dataset.
     */
    private XYDataset createPriceDataset() {

        // create dataset 1...
        final TimeSeries series1 = new TimeSeries("Price", Day.class);
        
        int fecha;
        Calendar actual = Calendar.getInstance();
        



        double precio=0;

        BD = new ConexionBD("mysql");
        ResultSet r = BD.consulta("SELECT fecha, precioCierre, cantidad FROM cotizacion WHERE (repeticion = 0 or repeticion = "+repeticion+" ) AND IDProducto = (SELECT IDProducto FROM producto WHERE nombre = \""+accion+"\") GROUP BY fecha");
      // System.err.println("SELECT fecha, precioCierre, cantidad FROM cotizacion WHERE (repeticion = 0 or repeticion = "+repeticion+") AND IDProducto = (SELECT IDProducto FROM producto WHERE nombre = \""+accion+"\") GROUP BY fecha");
        //ResultSet r = BD.consulta("SELECT fecha, precioPromedio, count(fecha) as volumen FROM cotizacion WHERE IDProducto = "+IdProducto+" GROUP BY fecha");
        try {
         while(r.next())
                {   fecha = r.getInt(1)-240;
                    actual = Calendar.getInstance();

                   precio = r.getFloat(2);
                   //System.out.println(actual.get(Calendar.DATE) + " " +actual.get(Calendar.MONTH)+ " " + actual.get(Calendar.YEAR) + " : "+fecha);
                   actual.add(Calendar.DATE, fecha-1);
                    
                   if(precio == 0.0)
                    //series1.add(new Day(dia, mes,  anno), null);
                       series1.add(new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH)+1,  actual.get(Calendar.YEAR)), null);
                   else
                   //series1.add(new Day(dia, mes,  anno), r.getDouble(2));
                       series1.add(new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH)+1,  actual.get(Calendar.YEAR)), r.getFloat(2)/1000);

                   
                    //System.out.println(actual.get(Calendar.DATE) + " " +actual.get(Calendar.MONTH)+ " " + actual.get(Calendar.YEAR) + " : "+fecha+" - "+r.getFloat(2));

                }

                BD.cerrarConexion();
            }
        catch (SQLException ex) {
                    ex.printStackTrace();
                }

          


        return new TimeSeriesCollection(series1);

    }

    /**
     * Creates a sample dataset.
     *
     * @return A sample dataset.
     */
    private IntervalXYDataset createVolumeDataset() {

        // create dataset 2...
        final TimeSeries series1 = new TimeSeries("Volume", Day.class);

        int fecha;
        Calendar actual = Calendar.getInstance();
        
         
        
       // new Calendar.

        BD = new ConexionBD("mysql");
        //ResultSet r = BD.consulta("SELECT fecha, precioPromedio, count(fecha) as volumen FROM cotizacion WHERE IDProducto = "+IdProducto+" GROUP BY fecha");
        ResultSet r = BD.consulta("SELECT fecha, precioPromedio, cantidad FROM cotizacion WHERE (repeticion = 0 or repeticion = "+repeticion+") AND IDProducto = (SELECT IDProducto FROM producto WHERE nombre = \""+accion+"\") GROUP BY fecha");
        try {

            while(r.next())
                {actual = Calendar.getInstance();
                fecha = r.getInt(1)-240;
 
                actual.add(Calendar.DATE, fecha-1);
                   series1.add(new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH)+1,  actual.get(Calendar.YEAR)), ((double)r.getInt(3)*r.getInt(2))/1000000000l);
                   //System.out.println(r.getInt(2) + " - " + r.getInt(3) + " - "+(double)r.getInt(3)*r.getInt(2));
                  //series1.add(new Day(dia, mes,  anno), r.getInt(3));

                }

                BD.cerrarConexion();
            }
         catch (SQLException ex) {
                    ex.printStackTrace();
                }

        

        return new TimeSeriesCollection(series1);

    }

   
    /**
     * Starting point for the price/volume chart demo application.
     *
     * @param args  ignored.
     */
    public static void main(final String[] args) {
        //Date pepe = null;
        //System.out.println(pepe.getYear());
      

        final PrecioVolumen generar = new PrecioVolumen("Precio Sobre Volumen","PREC",10);
        generar.pack();
        RefineryUtilities.centerFrameOnScreen(generar);
        generar.setVisible(true);
        
        

    }

}
