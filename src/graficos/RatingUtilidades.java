

package graficos;

import java.sql.*;
import basedatos.*;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
//import org.jfree.ui.ApplicationFrame;
//import org.jfree.ui.RefineryUtilities;

/**
 * A demo showing four pie charts.
 */
public class RatingUtilidades extends JFrame {

    ConexionBD BD;
    ConexionBD BDcantidad;
    ConexionBD BDsaldosF;
    ConexionBD BDnombres;


    ConexionBD BDA;
    ConexionBD BDcantidadAgentes;

    ConexionBD BDcantidadAcciones;
    ConexionBD BDprecio;

    int repeticion;


    /**
     * Creates a new demo instance.
     * 
     * @param title  the frame title.
     */
    public RatingUtilidades(String title, int top, int repet) {

        super(title);
        JPanel panel = new JPanel(new GridLayout(1, 2));
        repeticion = repet;



        JFreeChart chart3 = ChartFactory.createPieChart("Initial Budgets", SaldosIniciales(top), true, true, false);
        PiePlot plot3 = (PiePlot) chart3.getPlot();
        plot3.setForegroundAlpha(0.6f);
        plot3.setCircular(true);


        JFreeChart chart4 = ChartFactory.createPieChart("Final Budgets", SaldosFinales(top), true, true, false);
        PiePlot plot4 = (PiePlot) chart4.getPlot();
        plot4.setForegroundAlpha(0.6f);
        plot4.setCircular(true);

        //panel.add(new ChartPanel(chart1));
        //panel.add(new ChartPanel(chart2));


        panel.add(new ChartPanel(chart3));
        panel.add(new ChartPanel(chart4));

        //panel.add(panel2);

        panel.setPreferredSize(new Dimension(800, 600));
        setContentPane(panel);

    }

    public DefaultPieDataset SaldosIniciales(int top)
    {   int indice = 0;

        float SumUtilidadesInicial = 0;
        DefaultPieDataset dataset = new DefaultPieDataset();
        int cantidad2 = 0;

        BDcantidadAgentes = new ConexionBD("mysql");
        BDnombres = new ConexionBD("mysql");
        BDcantidadAcciones = new ConexionBD("mysql");
        BDprecio = new ConexionBD("mysql");
        BDcantidad = new ConexionBD("mysql");


        ResultSet Acciones = null;

        ResultSet nombre = null;
        ResultSet precio = null;
        ResultSet cantidad = null;
        ResultSet IDagente = BDcantidadAgentes.consulta("SELECT * FROM bursatil order by nombre");
                 String[] IDAgentes= new String[1000];
             float[] IDSaldos= new float[1000];

        try {



                 while(IDagente.next())
                 {
                    SumUtilidadesInicial = 0;


                    nombre = BDnombres.consulta("SELECT nombre FROM bursatil WHERE IDAgente = "+IDagente.getInt(1));

                  //  System.out.println("ID agente es "+IDagente.getInt(1));

                     if(nombre.next())
                            {

                                 Acciones = BDcantidadAcciones.consulta("SELECT IDProducto FROM accionesxagente WHERE repeticion = "+repeticion+" AND fecha = (SELECT min(fecha) FROM accionesxagente WHERE IDAgente = "+IDagente.getInt(1)+" AND repeticion = "+repeticion+") AND IDAgente = "+IDagente.getInt(1));

                                 while(Acciones.next())
                                 {
                                        precio = BDprecio.consulta("SELECT precioCierre, fecha FROM cotizacion WHERE repeticion = 0 AND IDproducto = "+Acciones.getInt(1)+" AND precioPromedio is not NULL GROUP BY fecha ORDER BY fecha DESC");
                                        cantidad = BDcantidad.consulta("SELECT IDAgente, cantidad, fecha FROM accionesxagente WHERE repeticion = "+repeticion+" AND IDAgente = "+IDagente.getInt(1)+" AND IDProducto = "+Acciones.getInt(1)+" AND fecha = 0 GROUP BY fecha,IDAgente");

                                        if(precio.next() && cantidad.next())
                                        {
                                           //System.out.println("Uti "+SumUtilidadesInicial+" precio "+precio.getDouble(1)+" canti "+cantidad.getDouble(2));
                                            SumUtilidadesInicial =      (float) (SumUtilidadesInicial + precio.getDouble(1) * cantidad.getDouble(2));

                                        }
                                         
                                 }
                                        IDAgentes[indice]=nombre.getString(1);
                                        IDSaldos[indice]=SumUtilidadesInicial;
                                        indice++;


                            }

                  }


             //////////////
             //AQUI ORDENAR EL VECTOR
             //////////////////
             float auxS = 0;
             String auxA = new String();
                 for(int i=0; i<indice-1;i++)
                 {

                     for(int j=i+1;j<indice;j++)
                     {

                         if(IDSaldos[i]<IDSaldos[j])
                         {
                             auxS=IDSaldos[i];
                             IDSaldos[i]=IDSaldos[j];
                             IDSaldos[j]=auxS;

                             auxA=IDAgentes[i];
                             IDAgentes[i]=IDAgentes[j];
                             IDAgentes[j]=auxA;

                         }
                     }

                 }


             while(cantidad2<top)
             {
                 dataset.setValue(IDAgentes[cantidad2], IDSaldos[cantidad2]);
                 cantidad2++;
             }


            }
        catch (SQLException ex) {
                    ex.printStackTrace();
                }

        BDcantidadAgentes.cerrarConexion();
        BDnombres.cerrarConexion();
        BDcantidadAcciones.cerrarConexion();
        BDprecio.cerrarConexion();
        BDcantidad.cerrarConexion();

        System.out.println("----------");
       
        return dataset;

    }

     public DefaultPieDataset SaldosFinales(int top)
    {   int indice = 0;

        float SumUtilidadesFinal = 0;
        DefaultPieDataset dataset = new DefaultPieDataset();
        int cantidad2 = 0;

        BDcantidadAgentes = new ConexionBD("mysql");
        BDnombres = new ConexionBD("mysql");
        BDcantidadAcciones = new ConexionBD("mysql");
        BDprecio = new ConexionBD("mysql");
        BDcantidad = new ConexionBD("mysql");


        ResultSet Acciones = null;

        ResultSet nombre = null;
        ResultSet precio = null;
        ResultSet cantidad = null;
        ResultSet IDagente = BDcantidadAgentes.consulta("SELECT * FROM bursatil order by nombre");
                 String[] IDAgentes= new String[1000];
             float[] IDSaldos= new float[1000];

        try {



                 while(IDagente.next())
                 {
                    SumUtilidadesFinal = 0;


                    nombre = BDnombres.consulta("SELECT nombre FROM bursatil WHERE IDAgente = "+IDagente.getInt(1));

                  //  System.out.println("ID agente es "+IDagente.getInt(1));

                     if(nombre.next())
                            {

                                 Acciones = BDcantidadAcciones.consulta("SELECT IDProducto FROM accionesxagente WHERE repeticion = "+repeticion+" AND fecha = (SELECT min(fecha) FROM accionesxagente WHERE IDAgente = "+IDagente.getInt(1)+" AND repeticion = "+repeticion+") AND IDAgente = "+IDagente.getInt(1));

                                 while(Acciones.next())
                                 {
                                        precio = BDprecio.consulta("SELECT precioCierre, fecha FROM cotizacion WHERE repeticion = "+repeticion+" AND IDproducto = "+Acciones.getInt(1)+" AND fecha <= (SELECT max(fecha) FROM cotizacion where repeticion = "+repeticion+") AND precioCierre is not NULL GROUP BY fecha ORDER BY fecha DESC");
                                        cantidad = BDcantidad.consulta("SELECT IDAgente, cantidad, fecha FROM accionesxagente WHERE repeticion = "+repeticion+" AND IDAgente = "+IDagente.getInt(1)+" AND IDProducto = "+Acciones.getInt(1)+" AND fecha <= (Select max(fecha) from accionesxagente Where IDAgente = "+IDagente.getInt(1)+" AND repeticion = "+repeticion+") GROUP BY fecha,IDAgente order by fecha desc");

                                        if(precio.next() && cantidad.next())
                                        {
                                            SumUtilidadesFinal = (float) (SumUtilidadesFinal + precio.getDouble(1) * cantidad.getDouble(2));
                                            //System.out.println("UtiFin "+SumUtilidadesInicial+" precio "+precio.getDouble(1)+" canti "+cantidad.getDouble(2));

                                        }


                                 }
                                        IDAgentes[indice]=nombre.getString(1);
                                        IDSaldos[indice]=SumUtilidadesFinal;
                                        indice++;
                                        //System.out.println("saldo es es "+SumUtilidadesFinal+" "+nombre.getString(1));

                            }

                  }


             //////////////
             //AQUI ORDENAR EL VECTOR
             //////////////////
             float auxS = 0;
             String auxA = new String();
                 for(int i=0; i<indice-1;i++)
                 {

                     for(int j=i+1;j<indice;j++)
                     {

                         if(IDSaldos[i]<IDSaldos[j])
                         {
                             auxS=IDSaldos[i];
                             IDSaldos[i]=IDSaldos[j];
                             IDSaldos[j]=auxS;

                             auxA=IDAgentes[i];
                             IDAgentes[i]=IDAgentes[j];
                             IDAgentes[j]=auxA;

                         }
                     }

                 }


             while(cantidad2<top)
             {
                 dataset.setValue(IDAgentes[cantidad2], IDSaldos[cantidad2]);
                 cantidad2++;
             }


            }
        catch (SQLException ex) {
                    ex.printStackTrace();
                }

        BDcantidadAgentes.cerrarConexion();
        BDnombres.cerrarConexion();
        BDcantidadAcciones.cerrarConexion();
        BDprecio.cerrarConexion();
        BDcantidad.cerrarConexion();


        return dataset;
    }

    /**
     * The starting point for the demo.
     * 
     * @param args  ignored.
     */
    /*public static void main(String[] args) {
        RatingUtilidades demo = new RatingUtilidades("Rating de los Saldos",5);
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }*/

}
