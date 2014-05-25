
package graficos;

import basedatos.ConexionBD;
import java.awt.Color;
import java.sql.*;
import java.util.Calendar;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RefineryUtilities;
//import org.jfree.ui.ApplicationFrame;
//import org.jfree.ui.RefineryUtilities;

/**
 * A demo showing a time series with per minute data.
 *
 */
public class UtilidadesAgentes extends JFrame {
    ConexionBD BDfechas;
    ConexionBD BDsaldo;
    ConexionBD BDprecio;
    ConexionBD BDcantidad;
    ConexionBD BDproducto;
    int repeticion;
    /**
     * A demonstration application.
     *
     * @param title  the frame title.
     */
    public UtilidadesAgentes(final String title, String IDagente, int repet) {

        super(title+" "+IDagente);
        repeticion = repet;
        final TimeSeries series = new TimeSeries("Profits", Day.class);
        
         Calendar actual = Calendar.getInstance();
        
         BDfechas = new ConexionBD("mysql");
        BDsaldo = new ConexionBD("mysql");
        BDprecio = new ConexionBD("mysql");
        BDcantidad = new ConexionBD("mysql");
        BDproducto = new ConexionBD("mysql");
        double SumUtilidades = 0;

        ResultSet fechas = BDfechas.consulta("SELECT fecha FROM cotizacion WHERE fecha >= 0 and repeticion = "+repeticion+" GROUP BY fecha");
        ResultSet saldo = null;
        ResultSet precio = null;
        ResultSet cantidad = null;
        ResultSet producto = null;

        try {
         while(fechas.next())
                {
                     actual = Calendar.getInstance();
                    producto = BDproducto.consulta("SELECT IDProducto FROM producto");
                    saldo = BDsaldo.consulta("SELECT IDAgente, saldo, fecha FROM saldoxagente WHERE repeticion = "+repeticion+" and IDAgente = (SELECT IDAgente FROM bursatil WHERE nombre = \""+IDagente+"\") AND fecha = "+fechas.getInt(1)+" GROUP BY fecha, IDAgente");

                    while(producto.next())
                    {

                    precio = BDprecio.consulta("SELECT precioCierre, fecha FROM cotizacion WHERE repeticion = "+repeticion+" and IDproducto = "+producto.getInt(1)+" AND fecha <= "+fechas.getInt(1)+" AND precioCierre is not NULL GROUP BY fecha ORDER BY fecha DESC");
                    cantidad = BDcantidad.consulta("SELECT IDAgente, cantidad, fecha FROM accionesxagente WHERE repeticion = "+repeticion+" and IDAgente = (SELECT IDAgente FROM bursatil WHERE nombre = \""+IDagente+"\") AND IDProducto = "+producto.getInt(1)+" AND fecha = "+fechas.getInt(1)+" GROUP BY fecha,IDAgente");

                        if(precio.next() && cantidad.next())
                        {
                            SumUtilidades = SumUtilidades + precio.getDouble(1) * cantidad.getDouble(2);
                            //System.out.println(precio.getDouble(1)+" "+cantidad.getDouble(2));

                        }

                    }

                    if(saldo.next())
                        SumUtilidades = SumUtilidades + saldo.getDouble(2);

                    //series.add(fechas.getInt(1), SumUtilidades);
                    actual.add(Calendar.DATE, fechas.getInt(1));
                    //System.out.println(actual.get(Calendar.DATE)+""+actual.get(Calendar.MONTH)+""+actual.get(Calendar.YEAR));
                    //System.out.println(saldo.getDouble(2)+" "+SumUtilidades);
                    series.add(new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH)+1,  actual.get(Calendar.YEAR)), SumUtilidades);
                 //  System.out.println(actual.get(Calendar.DATE) + " " +actual.get(Calendar.MONTH)+ " " + actual.get(Calendar.YEAR) + " : "+fechas.getInt(1)+" - "+SumUtilidades);

                    SumUtilidades = 0;
                }


            }
        catch (SQLException ex) {
                    ex.printStackTrace();
                }

        BDfechas.cerrarConexion();
        BDsaldo.cerrarConexion();
        BDprecio.cerrarConexion();
        BDcantidad.cerrarConexion();
        BDproducto.cerrarConexion();

        
        
        final TimeSeriesCollection dataset = new TimeSeriesCollection(series);
        final JFreeChart chart = ChartFactory.createTimeSeriesChart(
            "Profits of the Agent "+IDagente,
            "Date",
            "Profits",
            dataset,
            true,
            true,
            false
        );

        chart.setBackgroundPaint(Color.white);

//        final StandardLegend legend = (StandardLegend) chart.getLegend();
  //      legend.setDisplaySeriesShapes(true);

        // get a reference to the plot for further customisation...
        
                

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

    }

    public static void main(final String[] args) {

        final UtilidadesAgentes demo = new UtilidadesAgentes("Utilidades del agente","aA3",1);
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
