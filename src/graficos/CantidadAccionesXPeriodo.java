
package graficos;

import java.sql.*;
import basedatos.*;

import java.awt.Color;
import java.awt.Paint;
import java.text.DecimalFormat;
import java.util.Calendar;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYSeriesCollection;

//import org.jfree.ui.ApplicationFrame;
//import org.jfree.ui.RefineryUtilities;

/**
 * A simple demo showing a dataset created using the {@link XYSeriesCollection} class.
 *
 */
public class CantidadAccionesXPeriodo extends JFrame {
    ConexionBD BD;
    ConexionBD BDA;
    //ConexionBD BDnombres;
    final TimeSeriesCollection dataset = new TimeSeriesCollection();
    

   // public XYSeries series =  new XYSeries("fds");
    public TimeSeries series = new TimeSeries("dd", Day.class);
    /**
     * A demonstration application showing an XY series containing a null value.
     *
     * @param title  the frame title.
     */
    public CantidadAccionesXPeriodo(final String title, String IDagente, int repeticion) {

        super(title+" "+IDagente);
        
        final XYSeriesCollection data = new XYSeriesCollection();


        int contador = 0;
       
        BD = new ConexionBD("mysql");
        ResultSet r = BD.consulta("SELECT IDProducto, nombre FROM producto ");

        BDA = new ConexionBD("mysql");
        ResultSet s = null;

        //BDnombres = new ConexionBD("mysql");
        //ResultSet nombre = null;

        try {
         while(r.next())
                {
                 
                  s = BDA.consulta("SELECT IDAgente, cantidad, fecha FROM accionesxagente WHERE IDProducto="+r.getInt(1)+" AND repeticion = "+repeticion+" AND IDAgente = (SELECT IDAgente FROM bursatil WHERE nombre = \""+IDagente+"\") GROUP BY fecha, IDAgente");
                  //nombre = BDnombres.consulta("SELECT nombre FROM producto WHERE IDProducto = "+r.getInt(1));
                  //if(nombre.next())
                  //series = new TimeSeries(nombre.getString(1), Day.class);
                  series = new TimeSeries(r.getString(2), Day.class);
                  while(s.next())
                        {
                           // System.out.println(s.getInt(2)+" hg "+s.getInt(3));
                             Calendar actual = Calendar.getInstance();
                             actual.add(Calendar.DATE, s.getInt(3));

                             series.add(new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH)+1,  actual.get(Calendar.YEAR)), s.getInt(2));

                            // System.out.println(actual.get(Calendar.DATE) + " " +actual.get(Calendar.MONTH)+ " " + actual.get(Calendar.YEAR));
                           // series.add(s.getInt(3), s.getInt(2));




                        }
                        //series.setDescription("Producto "+r.getInt(1));
                        dataset.addSeries(series);
                        
                        contador++;
                }

      
         BD.cerrarConexion();
         BDA.cerrarConexion();
         //BDnombres.cerrarConexion();
        }
        catch (SQLException ex) {
                    ex.printStackTrace();
                }
        catch (java.lang.NullPointerException ex) {
                    ex.printStackTrace();
                }


        final JFreeChart chart = ChartFactory.createTimeSeriesChart(
            "Agent's Stocks "+IDagente,
            "Date",
            "Amount",
            dataset,
            true,
            true,
            true
        );

        Paint[] ff =  new Paint[] {Color.red, Color.blue, Color.green,
                Color.yellow, Color.orange, Color.cyan,
                Color.magenta, Color.blue};
        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.lightGray);
    //    plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);


        /////////////////////////////////
        /////////////////////////////////
        final NumberAxis rangeAxis1 = (NumberAxis) plot.getRangeAxis();
        rangeAxis1.setLowerMargin(0.40);  // to leave room for volume bars
        final DecimalFormat format = new DecimalFormat("#,###.###");
        rangeAxis1.setNumberFormatOverride(format);
     /////////////////////////////////////

        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(50, false);
        renderer.setSeriesPaint(2, Color.DARK_GRAY);
        
       // renderer.setPaint(Color.orange);

        plot.setRenderer(renderer);
        

        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        // OPTIONAL CUSTOMISATION COMPLETED.

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

//dataset.removeSeries(2);
 
     

    }

 

    // ****************************************************************************
    // * JFREECHART DEVELOPER GUIDE                                               *
    // * The JFreeChart Developer Guide, written by David Gilbert, is available   *
    // * to purchase from Object Refinery Limited:                                *
    // *                                                                          *
    // * http://www.object-refinery.com/jfreechart/guide.html                     *
    // *                                                                          *
    // * Sales are used to provide funding for the JFreeChart project - please    * 
    // * support us so that we can continue developing free software.             *
    // ****************************************************************************
    
    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    /*public static void main(final String[] args) {

        final CantidadAccionesXPeriodo demo = new CantidadAccionesXPeriodo("Cantidad de Acciones del Agente", "a5");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }*/

}
