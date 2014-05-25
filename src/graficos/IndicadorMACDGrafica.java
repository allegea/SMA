
package graficos;

import java.sql.*;
import basedatos.*;


import java.awt.Color;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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

import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.renderer.xy.XYItemRenderer;

/**
 * A simple demo showing a dataset created using the {@link XYSeriesCollection} class.
 *
 */
public class IndicadorMACDGrafica extends JFrame {
    ConexionBD BD;
    ConexionBD BDMACD;
    final TimeSeriesCollection dataset = new TimeSeriesCollection();
    

   // public XYSeries series =  new XYSeries("fds");
    public TimeSeries series = new TimeSeries("dd", Day.class);
    /**
     * A demonstration application showing an XY series containing a null value.
     *
     * @param title  the frame title.
     */
    public IndicadorMACDGrafica(final String title, String agente, String accion, int repeticion) {

        super(title+" "+agente+" for "+accion);

       
  
         final TimeSeries series1 = new TimeSeries("Trigger", Day.class);
          final TimeSeries series2 = new TimeSeries("MACD", Day.class);
           

        try {

        int fecha;
        Calendar actual = Calendar.getInstance();


        int idproducto = -1;


        double precio=0;

        BD = new ConexionBD("mysql");
        ResultSet r = BD.consulta("SELECT fecha, precioCierre, cantidad, IDProducto FROM cotizacion WHERE fecha > 0 AND repeticion = "+repeticion+" AND IDProducto = (SELECT IDProducto FROM producto WHERE nombre = \""+accion+"\") GROUP BY fecha");
        System.out.println("SELECT fecha, precioCierre, cantidad, IDProducto FROM cotizacion WHERE fecha > 0 AND repeticion = "+repeticion+" AND IDProducto = (SELECT IDProducto FROM producto WHERE nombre = \""+accion+"\") GROUP BY fecha");
        
         while(r.next())
                {   fecha = r.getInt(1);
                    actual = Calendar.getInstance();


                   precio = r.getDouble(2);
                   //System.out.println(actual.get(Calendar.DATE) + " " +actual.get(Calendar.MONTH)+ " " + actual.get(Calendar.YEAR) + " : "+fecha);
                   actual.add(Calendar.DATE, fecha-1);

                   /*if(precio == 0.0)
                    //series1.add(new Day(dia, mes,  anno), null);
                       series1.add(new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH)+1,  actual.get(Calendar.YEAR)), null);
                   else
                   //series1.add(new Day(dia, mes,  anno), r.getDouble(2));
                       series1.add(new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH)+1,  actual.get(Calendar.YEAR)), r.getDouble(2));

                //    System.out.println(actual.get(Calendar.DATE) + " " +actual.get(Calendar.MONTH)+ " " + actual.get(Calendar.YEAR) + " : "+fecha+" - "+r.getDouble(2));
*/
                    idproducto = r.getInt(4);
         }

        //SELECT a.fecha, a.valor FROM analisistecnico a, bursatil b where b.nombre = 'aA4' and b.IDAgente = a.IDAgente and a.IDProducto = 1 order by fecha

        BDMACD = new ConexionBD("mysql");
        ResultSet rMACD = BDMACD.consulta("SELECT a.fecha, a.valor FROM analisistecnico a, bursatil b where b.nombre = '"+agente+"' and b.IDAgente = a.IDAgente and a.IDProducto = "+idproducto+" and a.indicador = 'MACD' and a.repeticion = "+repeticion+" order by fecha");
        System.out.println("SELECT a.fecha, a.valor FROM analisistecnico a, bursatil b where b.nombre = '"+agente+"' and b.IDAgente = a.IDAgente and a.IDProducto = "+idproducto+" and a.indicador = 'MACD' and a.repeticion = "+repeticion+" order by fecha");
        while(rMACD.next())
        {
                    fecha = rMACD.getInt(1);
                    actual = Calendar.getInstance();


                   precio = rMACD.getFloat(2);
                   //System.out.println(actual.get(Calendar.DATE) + " " +actual.get(Calendar.MONTH)+ " " + actual.get(Calendar.YEAR) + " : "+fecha);
                   actual.add(Calendar.DATE, fecha-1);
                   
                   series2.add(new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH)+1,  actual.get(Calendar.YEAR)), precio);
                
            
        }

        rMACD = BDMACD.consulta("SELECT a.fecha, a.valor FROM analisistecnico a, bursatil b where b.nombre = '"+agente+"' and b.IDAgente = a.IDAgente and a.IDProducto = "+idproducto+" and a.indicador = 'MACDSenal' and a.repeticion = "+repeticion+" order by fecha");
        System.out.println("SELECT a.fecha, a.valor FROM analisistecnico a, bursatil b where b.nombre = '"+agente+"' and b.IDAgente = a.IDAgente and a.IDProducto = "+idproducto+" and a.indicador = 'MACDSenal' and a.repeticion = "+repeticion+" order by fecha");
        while(rMACD.next())
        {
                    fecha = rMACD.getInt(1);
                    actual = Calendar.getInstance();


                   precio = rMACD.getFloat(2);
                   //System.out.println(actual.get(Calendar.DATE) + " " +actual.get(Calendar.MONTH)+ " " + actual.get(Calendar.YEAR) + " : "+fecha);
                   actual.add(Calendar.DATE, fecha-1);

                   series1.add(new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH)+1,  actual.get(Calendar.YEAR)), precio);


        }

            dataset.addSeries(series1);
            dataset.addSeries(series2);


      
         BD.cerrarConexion();
         BDMACD.cerrarConexion();


        }
        catch (SQLException ex) {
                    ex.printStackTrace();
                }
        catch (java.lang.NullPointerException ex) {
                    ex.printStackTrace();
                }
        catch(Exception e) {
            e.printStackTrace();
        }


        //data.addSeries(series);
        //data.addSeries(series);
        final JFreeChart chart = ChartFactory.createTimeSeriesChart(
            "MACD of "+accion,
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
        final DecimalFormat format = new DecimalFormat("00.00");
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

        plot.setRenderer(renderer1);

                final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);


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

        final IndicadorMACDGrafica demo = new IndicadorMACDGrafica("Cantidad de Acciones del Agente", "aT1", "Ecopetrol");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }*/

}
