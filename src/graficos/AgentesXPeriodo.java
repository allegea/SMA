
package graficos;

import java.sql.*;
import basedatos.*;

import java.util.Calendar;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
//import org.jfree.ui.ApplicationFrame;
//import org.jfree.ui.RefineryUtilities;

/**
 * A simple demo showing a dataset created using the {@link XYSeriesCollection} class.
 *
 */
public class AgentesXPeriodo extends JFrame {
ConexionBD BD;
    /**
     * A demonstration application showing an XY series containing a null value.
     *
     * @param title  the frame title.
     */
    public AgentesXPeriodo(final String title, int repeticion) {

        super(title);
        //repeticion = repet;
        //final XYSeries series = new XYSeries("Agentes en cada periodo");
         final TimeSeries series = new TimeSeries("Agents by each date", Day.class);


         Calendar actual = Calendar.getInstance();

        BD = new ConexionBD("mysql");
        ResultSet r = BD.consulta("SELECT count(IDAgente), fecha FROM saldoXagente WHERE fecha > 0  AND repeticion = "+repeticion+" GROUP BY fecha");
        try {
         while(r.next())
                {   actual = Calendar.getInstance();
                    actual.add(Calendar.DATE, r.getInt(2)+1);
//                  series.add(r.getInt(2), r.getInt(1));
                  series.add(new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH)+1,  actual.get(Calendar.YEAR)), r.getInt(1));
                  // System.out.println(actual.get(Calendar.DATE) + " " +actual.get(Calendar.MONTH)+ " " + actual.get(Calendar.YEAR) + " : "+r.getInt(2)+" - "+r.getInt(1));

                }

                BD.cerrarConexion();
            }
        catch (SQLException ex) {
                    ex.printStackTrace();
                }


        final TimeSeriesCollection dataset = new TimeSeriesCollection(series);
        final JFreeChart chart = ChartFactory.createTimeSeriesChart(
            "Agents X Date",
            "Date",
            "Agents",
            dataset,
            true,
            true,
            false
        );


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

        final AgentesXPeriodo demo = new AgentesXPeriodo("Agentes X Periodo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }*/

}
