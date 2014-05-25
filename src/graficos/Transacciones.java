
package graficos;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.swing.JFrame;
import basedatos.*;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
//import org.jfree.chart.renderer.BarRenderer3D;
import org.jfree.chart.renderer.category.BarRenderer3D;
//import org.jfree.data.CategoryDataset;
//import org.jfree.data.DatasetUtilities;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;


/**
 * A simple demonstration application showing how to create a vertical 3D bar chart using data
 * from a {@link CategoryDataset}.
 *
 * @author David Gilbert
 */
public class Transacciones extends JFrame {
ConexionBD BD;
ConexionBD BDofertas;
ConexionBD BDCompra;
ConexionBD BDVenta;
ConexionBD BDCantidad;

String cate;
String calces = "Matches";
int categoria = -1;
int repeticion;

     public static FileWriter ArchivoCalces=null;
    public static PrintWriter ArchivoCalcesTotales=null;

    public static FileWriter ArchivoOfertas=null;
    public static PrintWriter ArchivoOfertasTotales=null;


    /**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
    public Transacciones(String title,int catego, int repet) {

        super(title);
        categoria = catego;
        repeticion = repet;
       
        if(categoria == 0) 
            cate="Buy Order";

        else {
            if(categoria == 1)
                cate="Sell Order";
            else
                cate="Total";
        }
        

         DefaultCategoryDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);

        // add the chart to a panel...
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(1000, 540));
        setContentPane(chartPanel);

    }

    /**
     * Creates a sample dataset.
     *
     * @return a sample dataset.
     */
   private  DefaultCategoryDataset createDataset() {


        DefaultCategoryDataset dataset = new DefaultCategoryDataset();


        try {

        int cantidadAgentes = 0;
        BDCantidad = new ConexionBD("mysql");
        ResultSet cantidad = BDCantidad.consulta("SELECT count(*) FROM bursatil");

        if(cantidad.next())
            cantidadAgentes = cantidad.getInt(1);

        BDCantidad.cerrarConexion();

        String[] nombresTCompra = new String[cantidadAgentes];
        String[] nombresTVenta = new String[cantidadAgentes];
        String[] nombresOCompra = new String[cantidadAgentes];
        String[] nombresOVenta = new String[cantidadAgentes];
        String[] nombres = new String[cantidadAgentes];
        int[] Ofertas = new int[cantidadAgentes];
        int[] Calces = new int[cantidadAgentes];


        int[] transaccionTCompra = new int[cantidadAgentes];
        int[] transaccionTVenta = new int[cantidadAgentes];
        int[] transaccionOCompra = new int[cantidadAgentes];
        int[] transaccionOVenta = new int[cantidadAgentes];

        BD = new ConexionBD("mysql");
        BDofertas = new ConexionBD("mysql");
        BDCompra = new ConexionBD("mysql");
        BDVenta = new ConexionBD("mysql");
         BDCantidad = new ConexionBD("mysql");

       

           ResultSet r = null;
           ResultSet of = null;

           ResultSet ov = null;
           ResultSet oc = null;
           ResultSet rCompra = null;
           ResultSet rVenta = null;
           

           cantidad = BDCantidad.consulta("SELECT nombre FROM bursatil order by nombre");

           
         if(categoria == 0)
         {
               r = BD.consulta("select  b.nombre, count(*)  from calce c, ofertacompra oc, bursatil b where oc.idofertacompra = c.idofertacompra and oc.idagente = b.idagente and c.repeticion = "+repeticion+" and oc.repeticion = "+repeticion+" group by b.idagente order by b.nombre");
               of = BDofertas.consulta("SELECT count(*), b.nombre FROM ofertacompra o, bursatil b where o.idagente = b.idagente and o.repeticion = "+repeticion+" group by b.idagente order by b.nombre");
         }
         else
         {
               if(categoria == 1)
               {
                   r = BD.consulta("select  b.nombre, count(*)  from calce c, ofertaventa ov, bursatil b where ov.idofertaventa = c.idofertaventa and ov.idagente = b.idagente and c.repeticion = "+repeticion+" and ov.repeticion = "+repeticion+" group by b.idagente order by b.nombre");
                   of = BDofertas.consulta("SELECT count(*), b.nombre FROM ofertaventa v, bursatil b where v.idagente = b.idagente and v.repeticion = "+repeticion+" group by b.idagente order by b.nombre");
               }
               else
               {
                   rCompra = BDCompra.consulta("select  b.nombre, count(*)  from calce c, ofertacompra oc, bursatil b where oc.idofertacompra = c.idofertacompra and oc.idagente = b.idagente and c.repeticion = "+repeticion+" and oc.repeticion = "+repeticion+"  group by b.idagente order by b.nombre");
                   rVenta = BDVenta.consulta("select  b.nombre, count(*)  from calce c, ofertaventa ov, bursatil b where ov.idofertaventa = c.idofertaventa and ov.idagente = b.idagente and ov.repeticion = "+repeticion+" and c.repeticion = "+repeticion+" group by b.idagente order by b.nombre");
                   oc = BDofertas.consulta("SELECT count(*), b.nombre FROM ofertacompra o, bursatil b where o.idagente = b.idagente and o.repeticion = "+repeticion+" group by b.idagente order by b.nombre");
                   ov = BD.consulta("SELECT count(*), b.nombre FROM ofertaventa v, bursatil b where v.idagente = b.idagente and v.repeticion = "+repeticion+" group by b.idagente order by b.nombre");
                   
               }
         }

                int indice = 0;
                while(cantidad.next())
                {
                    nombres[indice] = cantidad.getString(1);
                    Ofertas[indice] = 0;
                    Calces[indice] = 0;
                    indice++;

                }
                int indiceTCompra = 0;
                int indiceOCompra = 0;

            if(categoria != 0 && categoria != 1)
            {
                int indiceOVenta = 0;
                int indiceTVenta = 0;



                while(rCompra.next())
                {
                    nombresTCompra[indiceTCompra] = rCompra.getString(1);
                    transaccionTCompra[indiceTCompra] = rCompra.getInt(2);
                    indiceTCompra++;
                }
                

                while(rVenta.next())
                {
                    nombresTVenta[indiceTVenta] = rVenta.getString(1);
                    transaccionTVenta[indiceTVenta] = rVenta.getInt(2);
                    indiceTVenta++;
                }
                

                while(oc.next())
                {
                    nombresOCompra[indiceOCompra] = oc.getString(2);
                    transaccionOCompra[indiceOCompra] = oc.getInt(1);
                    indiceOCompra++;
                }
                

                while(ov.next())
                {
                    nombresOVenta[indiceOVenta] = ov.getString(2);
                    transaccionOVenta[indiceOVenta] = ov.getInt(1);
                    indiceOVenta++;
                }



                
                for(int i = 0; i< cantidadAgentes; i++)
                { for(int j = 0; j< indiceTCompra; j++ )
                  {
                      if(nombres[i].equalsIgnoreCase(nombresTCompra[j]))
                      {
                          Calces[i] = Calces[i] + transaccionTCompra[j];
                          break;
                      }
                  }
                }

                for(int i = 0; i< cantidadAgentes; i++)
                { for(int j = 0; j< indiceTVenta; j++ )
                  {
                      if(nombres[i].equalsIgnoreCase(nombresTVenta[j]))
                      {
                          Calces[i] = Calces[i] + transaccionTVenta[j];
                          break;
                      }
                  }
                }

              for(int i = 0; i< cantidadAgentes; i++)
                { for(int j = 0; j< indiceOCompra; j++ )
                  {
                      if(nombres[i].equalsIgnoreCase(nombresOCompra[j]))
                      {
                          Ofertas[i] = Ofertas[i] + transaccionOCompra[j];
                          break;
                      }
                  }
                }

               for(int i = 0; i< cantidadAgentes; i++)
                { for(int j = 0; j< indiceOVenta; j++ )
                  {
                      if(nombres[i].equalsIgnoreCase(nombresOVenta[j]))
                      {
                          Ofertas[i] = Ofertas[i] + transaccionOVenta[j];
                          break;
                      }
                  }
                }

            //*****************************************************************************
                        ArchivoCalces = new FileWriter("CalcesAgentes.txt");
                        ArchivoCalcesTotales = new PrintWriter(ArchivoCalces,true);
                        ArchivoCalcesTotales.flush();
            //*****************************************************************************

            //*****************************************************************************
                        ArchivoOfertas = new FileWriter("OfertasAgentes.txt");
                        ArchivoOfertasTotales = new PrintWriter(ArchivoOfertas,true);
                        ArchivoOfertasTotales.flush();
            //*****************************************************************************

              for(int i = 0; i< cantidadAgentes; i++)
                {
                  dataset.addValue(Calces[i], calces, nombres[i]);
                  dataset.addValue(Ofertas[i], "Total", nombres[i]);
                   ArchivoCalcesTotales.println(nombres[i]+" = "+Calces[i]);
                   ArchivoOfertasTotales.println(Ofertas[i]);
                   System.out.println(nombres[i]+" = "+Calces[i]);
                }

                ArchivoOfertasTotales.close();
                  ArchivoCalcesTotales.close();

                
            }

            else
            {
                 while(r.next())
                        {

                          
                            transaccionTCompra[indiceTCompra] = r.getInt(2);
                            nombresTCompra[indiceTCompra] = r.getString(1);
                            indiceTCompra++;

                        }

                while(of.next())
                        {


                            transaccionOCompra[indiceOCompra] = of.getInt(1);
                            nombresOCompra[indiceOCompra] = of.getString(2);
                            indiceOCompra++;
                        }

              for(int i = 0; i< cantidadAgentes; i++)
                { for(int j = 0; j< indiceTCompra; j++ )
                  {
                      if(nombres[i].equalsIgnoreCase(nombresTCompra[j]))
                      {
                          Calces[i] = Calces[i] + transaccionTCompra[j];
                          break;
                      }
                  }
                }

              for(int i = 0; i< cantidadAgentes; i++)
                { for(int j = 0; j< indiceOCompra; j++ )
                  {
                      if(nombres[i].equalsIgnoreCase(nombresOCompra[j]))
                      {
                          Ofertas[i] = Ofertas[i] + transaccionOCompra[j];
                          break;
                      }
                  }
                }


              for(int i = 0; i< cantidadAgentes; i++)
                {
                  dataset.addValue(Calces[i], calces, nombres[i]);
                  dataset.addValue(Ofertas[i], cate, nombres[i]);
                }

                 
            }

                BDCompra.cerrarConexion();
                BDVenta.cerrarConexion();
                BDofertas.cerrarConexion();
                BD.cerrarConexion();
                BDCantidad.cerrarConexion();
        }
        catch (SQLException ex) {
                    ex.printStackTrace();
                }
        catch (java.lang.NullPointerException ex) {
                    ex.printStackTrace();
                }
                         catch (IOException ex) {
           ex.printStackTrace();
        }



        return dataset;

    }

    /**
     * Creates a chart.
     *
     * @param dataset  the dataset.
     *
     * @return The chart.
     */
    private JFreeChart createChart(CategoryDataset dataset) {

        JFreeChart chart = ChartFactory.createBarChart3D(
            " ",      // chart title
            "Transactions",               // domain axis label
            "Amount",                  // range axis label
            dataset,                  // data
            PlotOrientation.VERTICAL, // orientation
            true,                     // include legend
            true,                     // tooltips
            false                     // urls
        );

        CategoryPlot plot = chart.getCategoryPlot();

       CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryMargin(0.25);
        domainAxis.setUpperMargin(0.05);
        domainAxis.setLowerMargin(0.05);

        CategoryAxis axis = plot.getDomainAxis();
        axis.setCategoryLabelPositions(
            CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 8.0)
        );
        BarRenderer3D renderer = (BarRenderer3D) plot.getRenderer();
        renderer.setDrawBarOutline(false);

        return chart;

    }

   /* public static void main(String[] args) {

        Transacciones demo = new Transacciones("3D Bar Chart Demo 1",1);
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }*/

}
