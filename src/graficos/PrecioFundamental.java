
package graficos;



import javax.swing.JFrame;
//import org.jfree.ui.ApplicationFrame;
//import org.jfree.ui.RefineryUtilities;


//////////////

import java.sql.*;
import basedatos.*;

import java.awt.BasicStroke;
import java.util.Calendar;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

/**
 * A simple demonstration of the {@link XYStepRenderer} class.
 */
public class PrecioFundamental extends JFrame {
    ConexionBD BD;
    TimeSeriesCollection preciosP = new TimeSeriesCollection();
    int repeticion;
    String accion;
    /**
     * Constructs the demo application.
     *
     * @param title  the frame title.
     */
    public PrecioFundamental(String nombre, int rep) {

        super();
        accion = nombre;
        repeticion = rep;
        JPanel chartPanel = createDemoPanel();
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 300));
        setContentPane(chartPanel);
        this.setTitle("Price vs Fundamental value of "+accion+" for iteration "+repeticion);

    }
    
    /**
     * Creates a sample chart.
     * 
     * @param dataset  a dataset for the chart.
     * 
     * @return A sample chart.
     */
    private  JFreeChart createChart(XYDataset dataset) {
        JFreeChart chart = ChartFactory.createXYLineChart(
            "",
            "X",
            "Price(COL$)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        XYPlot plot = (XYPlot) chart.getPlot();
        DateAxis dateaxis = new DateAxis("Dates"); 
        plot.setDomainAxis(dateaxis);
        plot.setDataset(1, preciosP);
        
        XYStepRenderer renderer = new XYStepRenderer();

        NumberAxis axis = (NumberAxis) plot.getRangeAxis();
        axis.setAutoRangeIncludesZero(false);
        axis.setAutoRangeMinimumSize(1.0);
        
       // renderer.setStroke(new BasicStroke(2.0f));
        renderer.setToolTipGenerator(new StandardXYToolTipGenerator());
        renderer.setDefaultEntityRadius(6);
        plot.setRenderer(1,renderer);
        return chart;
    }
    
    /**
     * Creates a sample dataset.
     * 
     * @return A dataset.
     */
    private  XYDataset createDataset() {
        
        preciosFundamentales();
        TimeSeriesCollection precios = new TimeSeriesCollection();
         TimeSeries market = new TimeSeries("Market", org.jfree.data.time.Day.class); 
         Calendar actual = Calendar.getInstance();

         double precio=0;

        BD = new ConexionBD("mysql");
        ResultSet r = BD.consulta("SELECT fecha, precioCierre, cantidad FROM cotizacion WHERE  repeticion = "+repeticion+" AND IDProducto = (SELECT IDProducto FROM producto WHERE nombre = \""+accion+"\") GROUP BY fecha");
       // System.err.println("SELECT fecha, precioCierre, cantidad FROM cotizacion WHERE (repeticion = 0 or repeticion = "+repeticion+") AND IDProducto = (SELECT IDProducto FROM producto WHERE nombre = \""+accion+"\") GROUP BY fecha");
        //ResultSet r = BD.consulta("SELECT fecha, precioPromedio, count(fecha) as volumen FROM cotizacion WHERE IDProducto = "+IdProducto+" GROUP BY fecha");
        try {
         while(r.next())
                {   
                    actual.add(Calendar.DATE, 1);

                   precio = r.getFloat(2);
                   //System.out.println(actual.get(Calendar.DATE) + " " +actual.get(Calendar.MONTH)+ " " + actual.get(Calendar.YEAR) + " : "+fecha);

                    
                   if(precio == 0.0)
                    //series1.add(new Day(dia, mes,  anno), null);
                       market.add(new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH)+1,  actual.get(Calendar.YEAR)), null);
                   else
                   //series1.add(new Day(dia, mes,  anno), r.getDouble(2));
                       market.add(new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH)+1,  actual.get(Calendar.YEAR)), r.getFloat(2));

                   
                    //System.out.println(actual.get(Calendar.DATE) + " " +actual.get(Calendar.MONTH)+ " " + actual.get(Calendar.YEAR) + " : "+fecha+" - "+r.getFloat(2));

                }

                BD.cerrarConexion();
            }
        catch (SQLException ex) {
                    ex.printStackTrace();
                }
  
        
        precios.addSeries(market);
      
        return precios;
        
        /*XYSeries series1 = new XYSeries("Series 1");
        series1.add(1.0, 3.0);
        series1.add(2.0, 4.0);
        series1.add(3.0, 2.0);
        series1.add(6.0, 3.0);
        XYSeries series2 = new XYSeries("Series 2");
        series2.add(1.0, 7.0);
        series2.add(2.0, 6.0);
        series2.add(3.0, 9.0);
        series2.add(4.0, 5.0);
        series2.add(6.0, 4.0);
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        return dataset;*/
    }
    
    private void  preciosFundamentales()
    {
        TimeSeries fundam = new TimeSeries("Fundamental", org.jfree.data.time.Day.class);  
        Calendar actual = Calendar.getInstance();
        //actual.add(Calendar.DATE, 8);
       double precio=0;

        BD = new ConexionBD("mysql");
        //ResultSet r = BD.consulta("SELECT fecha, precioCierre, cantidad FROM cotizacion WHERE  repeticion = "+repeticion+" AND IDProducto = (SELECT IDProducto FROM producto WHERE nombre = \""+accion+"\") GROUP BY fecha");
       ResultSet r = BD.consulta("SELECT fecha, preciofundamental FROM preciofundamental WHERE  repeticion = "+repeticion+" AND IDProducto = (SELECT IDProducto FROM producto WHERE nombre = \""+accion+"\") GROUP BY fecha");
       
        // System.err.println("SELECT fecha, precioCierre, cantidad FROM cotizacion WHERE (repeticion = 0 or repeticion = "+repeticion+") AND IDProducto = (SELECT IDProducto FROM producto WHERE nombre = \""+accion+"\") GROUP BY fecha");
        //ResultSet r = BD.consulta("SELECT fecha, precioPromedio, count(fecha) as volumen FROM cotizacion WHERE IDProducto = "+IdProducto+" GROUP BY fecha");
        try {
         while(r.next())
                {   
                    actual.add(Calendar.DATE, 1);

                   precio = r.getFloat(2);
                   //System.out.println(actual.get(Calendar.DATE) + " " +actual.get(Calendar.MONTH)+ " " + actual.get(Calendar.YEAR) + " : "+fecha);

                    
                   if(precio == 0.0)
                    //series1.add(new Day(dia, mes,  anno), null);
                       fundam.add(new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH)+1,  actual.get(Calendar.YEAR)), null);
                   else
                   //series1.add(new Day(dia, mes,  anno), r.getDouble(2));
                       fundam.add(new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH)+1,  actual.get(Calendar.YEAR)), r.getFloat(2));

                   
                    //System.out.println(actual.get(Calendar.DATE) + " " +actual.get(Calendar.MONTH)+ " " + actual.get(Calendar.YEAR) + " : "+fecha+" - "+r.getFloat(2));

                }

                BD.cerrarConexion();
            }
        catch (SQLException ex) {
                    ex.printStackTrace();
                }
        
          preciosP.addSeries(fundam);
    }

    /**
     * Creates a panel for the demo (used by SuperDemo.java).
     * 
     * @return A panel.
     */
    public  JPanel createDemoPanel() {
        
        JFreeChart chart = createChart(createDataset());
        return new ChartPanel(chart);
    }
    
    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {
        PrecioFundamental demo = new PrecioFundamental("PREC",1);
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }

}