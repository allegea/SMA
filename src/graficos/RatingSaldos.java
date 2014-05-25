

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
public class RatingSaldos extends JFrame {
ConexionBD BD;
ConexionBD BDcantidad;
ConexionBD BDsaldosF;
ConexionBD BDnombres;
int repeticion;
    /**
     * Creates a new demo instance.
     * 
     * @param title  the frame title.
     */
    public RatingSaldos(String title, int top, int repet) {

        super(title);
        repeticion = repet;
        JPanel panel = new JPanel(new GridLayout(1, 2));
         
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
    {
        int cantidad = 0;
        DefaultPieDataset dataset = new DefaultPieDataset();

          BD = new ConexionBD("mysql");
          BDnombres = new ConexionBD("mysql");
          ResultSet nombre = null;

        ResultSet r = BD.consulta("SELECT IDAgente, saldo FROM saldoXagente WHERE fecha=0 AND repeticion = "+repeticion+" ORDER BY saldo DESC");
        try {
         while(r.next() && cantidad < top)
                {
                  nombre = BDnombres.consulta("SELECT nombre FROM bursatil WHERE IDAgente = "+r.getInt(1));
                  if(nombre.next())
                  {
                      dataset.setValue(nombre.getString(1), r.getFloat(2));
                      cantidad++;
                  }

                }

                BD.cerrarConexion();
                BDnombres.cerrarConexion();
            }
        catch (SQLException ex) {
                    ex.printStackTrace();
                }
       
        return dataset;

    }

     public DefaultPieDataset SaldosFinales(int top)
    {
         int cantidad = 0;
         int indice = 0;
        DefaultPieDataset dataset = new DefaultPieDataset();

        BD = new ConexionBD("mysql");
        BDcantidad = new ConexionBD("mysql");
        BDsaldosF = new ConexionBD("mysql");
        BDnombres = new ConexionBD("mysql");
        try {
        ResultSet r = BD.consulta("SELECT IDAgente FROM saldoXagente WHERE fecha = 0");
        ResultSet cant = BDcantidad.consulta("SELECT count(IDAgente) FROM saldoXagente WHERE fecha = 0");
        ResultSet sald = null;
        ResultSet nombre = null;

             cant.next();

             String[] IDAgentes= new String[cant.getInt(1)];
             float[] IDSaldos= new float[cant.getInt(1)];


        
       
         while(r.next())
                {

                      sald = BDsaldosF.consulta("SELECT saldo FROM saldoXagente WHERE fecha = (SELECT max(fecha) FROM saldoXagente WHERE IDAgente = "+r.getInt(1)+" AND repeticion = "+repeticion+") and IDAgente =  "+r.getInt(1)+" and repeticion = "+repeticion);

                      if(sald.next())
                      {
                          nombre = BDnombres.consulta("SELECT nombre FROM bursatil WHERE IDAgente = "+r.getInt(1));
                        if(nombre.next())
                        {  IDAgentes[indice]=nombre.getString(1);
                          IDSaldos[indice]=sald.getFloat(1);
                          indice++;
                        }
                      }

                }
             
             //////////////
             //AQUI ORDENAR EL VECTOR
             //////////////////
             float auxS = 0;
             String auxA = new String();
                 for(int i=0; i<IDAgentes.length-1;i++)
                 {

                     for(int j=i+1;j<IDAgentes.length;j++)
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


             while(cantidad<top)
             {
                 dataset.setValue(IDAgentes[cantidad], IDSaldos[cantidad]);
                 cantidad++;
             }
                BD.cerrarConexion();
                BDcantidad.cerrarConexion();
                BDsaldosF.cerrarConexion();
                BDnombres.cerrarConexion();
            }
        catch (SQLException ex) {
                    ex.printStackTrace();
        }

        return dataset;

    }

    /**
     * The starting point for the demo.
     * 
     * @param args  ignored.
     */
    /*public static void main(String[] args) {
        RatingSaldos demo = new RatingSaldos("Rating de los Saldos",3);
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }*/

}
