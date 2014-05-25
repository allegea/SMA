/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================

 */

package graficos;

import java.io.IOException;
import java.sql.*;
import basedatos.*;

import java.awt.Dimension;

import java.io.FileWriter;
import java.io.PrintWriter;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
//import org.jfree.ui.ApplicationFrame;
//import org.jfree.ui.RefineryUtilities;

/**
 * A simple demonstration application showing how to create a bar chart with a custom item
 * label generator.
 *
 */
public class RentabilidadXAgente extends JFrame {
    ConexionBD BD;
    ConexionBD BDA;
    ConexionBD BDcantidadAgentes;
    ConexionBD BDnombres;
    ConexionBD BDcantidadAcciones;
    ConexionBD BDprecio;
    ConexionBD BDcantidad;
     ConexionBD BDcantidad2;

     int repeticion;


     public static FileWriter Origen=null;
    public static PrintWriter archivo=null;


    /**
     * Creates a new demo instance.
     *
     * @param title  the frame title.
     */
    public RentabilidadXAgente(final String title, int repet) {

        super(title);
        repeticion = repet;

        final CategoryDataset dataset = createDataset();
        final JFreeChart chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 270));
        setContentPane(chartPanel);

    }

    /**
     * Returns a sample dataset.
     * 
     * @return The dataset.
     */
    private CategoryDataset createDataset() {

        final String category1 = "";
        
        // create the dataset...
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

         float precioFinal = 0;
        float precioInicial = 0;
        double SumUtilidadesInicial = 0;
        double SumUtilidadesFinal = 0;

        BD = new ConexionBD("mysql");
        BDA = new ConexionBD("mysql");
        BDcantidadAgentes = new ConexionBD("mysql");
        BDnombres = new ConexionBD("mysql");
        BDcantidadAcciones = new ConexionBD("mysql");
        BDprecio = new ConexionBD("mysql");
        BDcantidad = new ConexionBD("mysql");
        BDcantidad2 = new ConexionBD("mysql");
        ResultSet SaldoInicial = null;
        ResultSet Acciones = null;
        ResultSet SaldoFinal = null;
        ResultSet nombre = null;
        ResultSet precio = null;
        ResultSet cantidad = null;
        ResultSet IDagente = BDcantidadAgentes.consulta("SELECT * FROM bursatil order by nombre");

        try {

            //*****************************************************************************
                        Origen = new FileWriter("RentabilidadAgentes.txt");
                        archivo = new PrintWriter(Origen,true);
                        archivo.flush();
            //*****************************************************************************


                 while(IDagente.next())
                 {
                    SumUtilidadesInicial = 0;
                    SumUtilidadesFinal = 0;
                    SaldoInicial = BD.consulta("SELECT IDAgente, saldo FROM saldoXagente WHERE repeticion = "+repeticion+" AND fecha=(SELECT min(fecha) FROM saldoXagente WHERE IDAgente =  "+IDagente.getInt(1)+" AND repeticion = "+repeticion+" ) AND IDAgente =  "+IDagente.getInt(1));
                    SaldoFinal = BDA.consulta("SELECT IDAgente, saldo FROM saldoXagente WHERE repeticion = "+repeticion+" AND fecha=(SELECT max(fecha) FROM saldoXagente WHERE IDAgente =  "+IDagente.getInt(1)+" AND repeticion = "+repeticion+") AND IDAgente =  "+IDagente.getInt(1));
                    nombre = BDnombres.consulta("SELECT nombre FROM bursatil WHERE IDAgente = "+IDagente.getInt(1));

                  //  System.out.println("ID agente es "+IDagente.getInt(1));

                     if(SaldoInicial.next() && SaldoFinal.next() && nombre.next())
                            {
                         
                                 Acciones = BDcantidadAcciones.consulta("SELECT IDProducto FROM accionesxagente WHERE repeticion = "+repeticion+" AND fecha = (SELECT min(fecha) FROM accionesxagente WHERE IDAgente = "+IDagente.getInt(1)+" AND repeticion = "+repeticion+") AND IDAgente = "+IDagente.getInt(1));

                                 while(Acciones.next())
                                 {
                                        precio = BDprecio.consulta("SELECT precioCierre, fecha FROM cotizacion WHERE repeticion = "+repeticion+" AND IDproducto = "+Acciones.getInt(1)+" AND fecha <= 0 AND precioCierre is not NULL GROUP BY fecha ORDER BY fecha DESC");
                                        cantidad = BDcantidad.consulta("SELECT IDAgente, cantidad, fecha FROM accionesxagente WHERE repeticion = "+repeticion+" AND IDAgente = "+IDagente.getInt(1)+" AND IDProducto = "+Acciones.getInt(1)+" AND fecha = 0 GROUP BY fecha,IDAgente");

                                        if(precio.next() && cantidad.next())
                                        {
                                           //System.out.println("Uti "+SumUtilidadesInicial+" precio "+precio.getDouble(1)+" canti "+cantidad.getDouble(2));
                                            SumUtilidadesInicial = (long) (SumUtilidadesInicial + precio.getDouble(1) * cantidad.getDouble(2));

                                        }

                                        precio = BDprecio.consulta("SELECT precioCierre, fecha FROM cotizacion WHERE repeticion = "+repeticion+" AND IDproducto = "+Acciones.getInt(1)+" AND fecha <= (SELECT max(fecha) FROM cotizacion where repeticion = "+repeticion+") AND precioCierre is not NULL GROUP BY fecha ORDER BY fecha DESC");
                                        cantidad = BDcantidad2.consulta("SELECT IDAgente, cantidad, fecha FROM accionesxagente WHERE repeticion = "+repeticion+" AND IDAgente = "+IDagente.getInt(1)+" AND IDProducto = "+Acciones.getInt(1)+" AND fecha <= (Select max(fecha) from accionesxagente Where IDAgente = "+IDagente.getInt(1)+" AND repeticion = "+repeticion+") GROUP BY fecha,IDAgente order by fecha desc");

                                        if(precio.next() && cantidad.next())
                                        {
                                            SumUtilidadesFinal = (long) (SumUtilidadesFinal + precio.getDouble(1) * cantidad.getDouble(2));
                                            //System.out.println("UtiFin "+SumUtilidadesInicial+" precio "+precio.getDouble(1)+" canti "+cantidad.getDouble(2));

                                        }





                                 }
                                        //System.out.println("saldo es es "+SaldoInicial.getFloat(2));
                                       // System.out.println("saldo final es "+SaldoFinal.getFloat(2));
                                        precioInicial = (long) (SaldoInicial.getFloat(2) + (float) SumUtilidadesInicial);
                                        //System.out.println("SalINi "+SaldoInicial.getFloat(2)+" INICIAL "+precioInicial);
                                        precioFinal = (long) (SaldoFinal.getFloat(2) + (float) SumUtilidadesFinal);
                                        // System.out.println("SalFIN "+SaldoFinal.getFloat(2)+" FINAL "+precioFinal);
                                        if(precioInicial != 0.0 && precioFinal !=0)
                                        {  dataset.addValue((precioFinal/precioInicial-1)*100,  nombre.getString(1),category1);
                                           //System.out.println(nombre.getString(1)+" la rentabilidad =  "+(precioFinal/precioInicial-1)*100);

                                           archivo.println(nombre.getString(1)+" = "+(precioFinal/precioInicial-1)*100);
                                        }
                                        //dataset.setValue(r.getString(1), Math.abs((precioFinal/precioInicial-1))*100);



                            }

                  }
                        archivo.close();
            }
        catch (SQLException ex) {
                    ex.printStackTrace();
                }
                 catch (IOException ex) {
           ex.printStackTrace();
        }

        BD.cerrarConexion();
        BDA.cerrarConexion();
        BDcantidadAgentes.cerrarConexion();
        BDnombres.cerrarConexion();
        BDcantidadAcciones.cerrarConexion();
        BDprecio.cerrarConexion();
        BDcantidad.cerrarConexion();
        BDcantidad2.cerrarConexion();
        //BDnombres.cerrarConexion();
        
        return dataset;
        
    }
    
    /**
     * Creates a sample chart.
     * 
     * @param dataset  the dataset.
     * 
     * @return The chart.
     */
    private JFreeChart createChart(final CategoryDataset dataset) {
        
        // create the chart...
        final JFreeChart chart = ChartFactory.createBarChart(
            "Profitability per Agent",       // chart title
            "Agents",               // domain axis label
            "Percentage",                  // range axis label
            dataset,                  // data
            PlotOrientation.VERTICAL, // orientation
            true,                    // include legend
            true,                     // tooltips?
            false                     // URLs?
        );



        // OPTIONAL CUSTOMISATION COMPLETED.
        
        return chart;
        
    }
    

    /*public static void main(final String[] args) {

        final RentabilidadXAgente demo = new RentabilidadXAgente("Bar Chart Demo 8");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }*/

}
