
package graficos;

import java.sql.*;
import basedatos.*;



import java.awt.Color;

import java.util.Calendar;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYSeriesCollection;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.renderer.xy.XYItemRenderer;




/**
 * A simple demo showing a dataset created using the {@link XYSeriesCollection} class.
 *
 */
public class IndicadorRSIGrafica extends JFrame {
    ConexionBD BD;
    final TimeSeriesCollection dataset = new TimeSeriesCollection();

   // public XYSeries series =  new XYSeries("fds");
    public TimeSeries series = new TimeSeries("dd", Day.class);
    /**
     * A demonstration application showing an XY series containing a null value.
     *
     * @param title  the frame title.
     */
    public IndicadorRSIGrafica(final String title, String agente, String accion, int repeticion) {

        super(title+" "+agente+" for "+accion);
       
         final TimeSeries series1 = new TimeSeries("Price", Day.class);
         final TimeSeries series2 = new TimeSeries("Upper Limit", Day.class);
         final TimeSeries series3 = new TimeSeries("Lower Limit", Day.class);
           //final TimeSeries series4 = new TimeSeries("RSI", Day.class);



        int fecha;
        int idproducto = -1;
        Calendar actual = Calendar.getInstance();

        try {





        double precio=0;

        BD = new ConexionBD("mysql");
      ResultSet r = BD.consulta("SELECT a.fecha, a.valor FROM analisistecnico a, bursatil b, producto p where b.nombre = '"+agente+"' and b.IDAgente = a.IDAgente and a.IDProducto = p.IDProducto and p.nombre = '"+accion+"' and a.repeticion = "+repeticion+" order by fecha");
        System.out.println("SELECT a.fecha, a.valor FROM analisistecnico a, bursatil b, producto p where b.nombre = '"+agente+"' and b.IDAgente = a.IDAgente and a.IDProducto = p.IDProducto and p.nombre = '"+accion+"' and a.repeticion = "+repeticion+" order by fecha");
        
         while(r.next())
                {
                    fecha = r.getInt(1);
                    actual = Calendar.getInstance();


                   precio = r.getFloat(2);
                   //System.out.println(actual.get(Calendar.DATE) + " " +actual.get(Calendar.MONTH)+ " " + actual.get(Calendar.YEAR) + " : "+fecha);
                   actual.add(Calendar.DATE, fecha-1);

                       series1.add(new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH)+1,  actual.get(Calendar.YEAR)), precio);
                       series2.add(new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH)+1,  actual.get(Calendar.YEAR)), 70);
                       series3.add(new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH)+1,  actual.get(Calendar.YEAR)), 30);

                           //idproducto = r.getInt(4);
              
                   }

            dataset.addSeries(series2);
            dataset.addSeries(series1);
            dataset.addSeries(series3);
            //dataset.addSeries(series4);


         BD.cerrarConexion();

        }
        /*catch (SQLException ex) {
                    ex.printStackTrace();
                }*/
        catch (java.lang.NullPointerException ex) {
                    ex.printStackTrace();
                }
        catch(Exception e) {
            e.printStackTrace();
        }

        final JFreeChart chart = ChartFactory.createTimeSeriesChart(
            "RSI of "+accion,
            "Date",
            "Value",
            dataset,
            true,
            true,
            true
        );

        final XYPlot plot = chart.getXYPlot();
        final NumberAxis rangeAxis1 = (NumberAxis) plot.getRangeAxis();
        rangeAxis1.setLowerMargin(0.40);  // to leave room for volume bars
        final DecimalFormat format = new DecimalFormat("##");//Cambie formato
        rangeAxis1.setNumberFormatOverride(format);

        final XYItemRenderer renderer1 = plot.getRenderer();
        renderer1.setToolTipGenerator(
            new StandardXYToolTipGenerator(
                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0.00")
            )
        );
       renderer1.setSeriesPaint(0, Color.black);
       renderer1.setSeriesPaint(1, Color.blue);

        renderer1.setSeriesPaint(2, Color.black);

        plot.setRenderer(renderer1);

                final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

    }

    /*public static void main(final String[] args) {

        final IndicadorRSIGrafica demo = new IndicadorRSIGrafica("Índice de Fuerza Relativa de ", "aT1", "Ecopetrol");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }*/

}
