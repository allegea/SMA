/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package graficos;


import basedatos.ConexionBD;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;


public class Liquidez extends JFrame {

    String accion;
    int repeticion;

    public Liquidez(String title, String acc, int repet) {

        super(title+" of "+acc);
        accion = acc;
        repeticion = repet;

        
        XYSeriesCollection data = new XYSeriesCollection(generarDatos());
        JFreeChart chart = ChartFactory.createXYLineChart(
            "",
            "Transaction",
            "%",
            data,
            PlotOrientation.VERTICAL,
            false, //Si quiere ver la etiqueta de que es cada linea del grafico
            true,
            false
        );

        XYPlot plot = (XYPlot) chart.getPlot();
        NumberAxis axis = (NumberAxis) plot.getRangeAxis();
        axis.setAutoRangeIncludesZero(false);
        axis.setAutoRangeMinimumSize(1.0);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

    }

    XYSeries generarDatos()
    {
        XYSeries series = new XYSeries("Bid-Ask Spread");
        ConexionBD BD = new ConexionBD("mysql");
        ConexionBD IDProducto = new ConexionBD("mysql");
        ResultSet id = null;
        int transaccion = 1;
        int idpro = -1;
        int fechas = -1;
        int productos = -1;

        try {

                if(accion.equals("Market"))
                {
                    id = BD.consulta("Select max(fecha) from cotizacion");

                    if (id.next()) fechas = id.getInt(1);


                    id = BD.consulta("Select count(*) from producto");

                    if (id.next()) productos = id.getInt(1);


                    double[][] liquidez = new double[productos][fechas+1];
                    double[] total = new double[fechas+1];

                    for(int i=0;i<productos;i++)
                        Arrays.fill(liquidez[i], -1);


                    for(int i=0;i<productos;i++)
                    {

                        ResultSet r = BD.consulta("SELECT c.fecha, c.idcalce, c.idofertacompra, c.idofertaventa, oc.preciocompra, ov.precioventa, c.preciocompra as precio, avg(((ov.precioventa - oc.preciocompra)/((ov.precioventa + oc.preciocompra)/2))) FROM calce c, ofertacompra oc, ofertaventa ov where c.idofertacompra = oc.idofertacompra and c.idofertaventa = ov.idofertaventa and c.repeticion = "+repeticion+" and ov.repeticion= "+repeticion+" and oc.repeticion = "+repeticion+" and c.idproducto = "+i+" and ov.idproducto = "+i+" and oc.idproducto = "+i+" group by fecha");

                        while(r.next()) liquidez[i][r.getInt(1)] = Math.abs(r.getDouble(8))*100;

                    }

                    for(int j=1;j<=fechas;j++)
                    {
                        int sumados=0;
                        for(int i=0;i<productos;i++)
                        {
                            if(liquidez[i][j]!=-1)
                            {
                                total[j]+=liquidez[i][j];
                                sumados++;
                            }
                        }
                        if(sumados>0)
                            series.add(j, total[j]/sumados);

                        System.out.println(j+"\t"+total[j]/sumados);

                    }



                }
                else
                {



                 id = IDProducto.consulta("Select idproducto from producto where nombre = \""+accion+"\"");

                    if (id.next()) {
                        idpro = id.getInt(1);
                    }


                IDProducto.cerrarConexion();

                //ResultSet r = BD.consulta("SELECT c.fecha, c.idcalce, c.idofertacompra, c.idofertaventa, oc.preciocompra, ov.precioventa, c.preciocompra as precio, ((ov.precioventa - oc.preciocompra)/((ov.precioventa + oc.preciocompra)/2)) FROM calce c, ofertacompra oc, ofertaventa ov where c.idofertacompra = oc.idofertacompra and c.idofertaventa = ov.idofertaventa and c.repeticion = "+repeticion+" and ov.repeticion= "+repeticion+" and oc.repeticion = "+repeticion+" and c.idproducto = "+idpro+" and ov.idproducto = "+idpro+" and oc.idproducto = "+idpro+"");
                    ResultSet r = BD.consulta("SELECT c.fecha, c.idcalce, c.idofertacompra, c.idofertaventa, oc.preciocompra, ov.precioventa, c.preciocompra as precio, avg(((ov.precioventa - oc.preciocompra)/((ov.precioventa + oc.preciocompra)/2))) FROM calce c, ofertacompra oc, ofertaventa ov where c.idofertacompra = oc.idofertacompra and c.idofertaventa = ov.idofertaventa and c.repeticion = "+repeticion+" and ov.repeticion= "+repeticion+" and oc.repeticion = "+repeticion+" and c.idproducto = "+idpro+" and ov.idproducto = "+idpro+" and oc.idproducto = "+idpro+" group by fecha");

                    while(r.next())
                    {
                        series.add(r.getInt(1), Math.abs(r.getDouble(8))*100);
                        transaccion++;
                    }

                }

        } catch (SQLException ex) {
            Logger.getLogger(Liquidez.class.getName()).log(Level.SEVERE, null, ex);
        }

        BD.cerrarConexion();

        
        return series;
    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        Liquidez demo = new Liquidez("Liquidity","Market",10);
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}