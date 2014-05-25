
package graficos;

import basedatos.*;
import java.awt.Color;
import java.awt.GridLayout;
import java.sql.*;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;

import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.xy.IntervalXYDataset;

/**
 * A demo of the {@link HistogramDataset} class.
 *
 * @author Jelai Wang, jelaiw AT mindspring.com
 */
public class HistogramaRetornos extends JFrame {

    /** For generating random numbers. */
    
    String accion = new String();
    int categorias;
    double[] datos;
     ConexionBD BD;
     ArrayList<Double> retornos = new ArrayList<Double>();
     int repeticion;
    /**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
    public HistogramaRetornos(String title, String nombre, int repet) {
        super(title + " "+nombre+" Returns (Real vs. Simulated)");
        accion = nombre;
        repeticion = repet;
        JPanel panel;

        System.out.println(accion+" "+repeticion);

        IntervalXYDataset datasetReal = createDataset(true);
        JFreeChart chartReal = createChart(datasetReal,"");

        IntervalXYDataset datasetSim = createDataset(false);
        if(retornos.size()>0)
        {
            JFreeChart chartSim = createChart(datasetSim,"");
            panel = new JPanel(new GridLayout(0, 2));

            retornos.clear();

            panel.add(new ChartPanel(chartReal));
            panel.add(new ChartPanel(chartSim));
        }
        else
        {
            //System.out.println("fdfsfds");
             panel = new JPanel();
             panel.add(new ChartPanel(chartReal));
            
        }

        //ChartPanel chartPanel = new ChartPanel(chart);

        /*chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        chartPanel.setMouseZoomable(true, false);*/
        //setContentPane(general);
        setContentPane(panel);
    }

    /**
     * Creates a sample {@link HistogramDataset}.
     *
     * @return The dataset.
     */
    private IntervalXYDataset createDataset(boolean real) {
        HistogramDataset dataset = new HistogramDataset();
        dataset.setType(HistogramType.FREQUENCY);
        obtenerDatos(real);
        dataset.addSeries("Retornos", datos, categorias);
        return dataset;
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
     * Creates a chart.
     *
     * @param dataset  a dataset.
     *
     * @return The chart.
     */
    private JFreeChart createChart(IntervalXYDataset dataset, String name) {
        JFreeChart chart = ChartFactory.createHistogram(
            name,
            null,
            "Frecuency",
            dataset,
            PlotOrientation.VERTICAL,
            false,
            true,
            false
        );
        chart.getXYPlot().setForegroundAlpha(0.75f);
        chart.getXYPlot().getRenderer().setSeriesPaint(0, Color.blue);
        return chart;
    }

    /**
     * Generates an array of sample data.
     *
     * @param size  the array size.
     * @param shift  the shift from zero.
     *
     * @return The array of sample data.
     */
    private void obtenerDatos(boolean real) {
        
        int indice = 0;
        double precio_t, precio_t_1;
        precio_t = precio_t_1 = 0;
        ResultSet r;

        BD = new ConexionBD("mysql");
        if(real)
        r = BD.consulta("SELECT fecha, precioCierre, cantidad  FROM cotizacion WHERE repeticion = 0 AND IDProducto = (SELECT IDProducto FROM producto WHERE nombre = \""+accion+"\") GROUP BY fecha");
        else
        r = BD.consulta("SELECT fecha, precioCierre, cantidad  FROM cotizacion WHERE repeticion = "+repeticion+" AND IDProducto = (SELECT IDProducto FROM producto WHERE nombre = \""+accion+"\") GROUP BY fecha");
        //ResultSet r = BD.consulta("SELECT fecha, precioPromedio, count(fecha) as volumen FROM cotizacion WHERE IDProducto = "+IdProducto+" GROUP BY fecha");
        try {
         while(r.next())
                {

                    precio_t_1 = precio_t;
                    precio_t = r.getFloat(2);

                   if(indice !=0)
                       retornos.add(Math.log( precio_t/precio_t_1));
                    
                   indice++;
                }

                BD.cerrarConexion();
            }
        catch (SQLException ex) {
                    ex.printStackTrace();
                }

        if(retornos.size()>0)
        StylizedFacts(real);



    }

    void StylizedFacts(boolean real)
    {
         /*       categorias = (int) (1 + 3.3 * Math.log10(retornos.size()));


        double[] cosas = new double[categorias];
       for(int i = 0; i <categorias;i++)
           cosas[i] = 0;

        double min = Collections.min(retornos);
        double max = Collections.max(retornos);
        double paso = (max - min)/categorias;
        for(int i=0;i< retornos.size();i++)
        {
            for(int j = 0; j <categorias;j++)
            {
                if(j == 0)
                {
                    if(retornos.get(i)>= (min + paso*j) && retornos.get(i)<= (min+paso*(j+1)) )
                    {
                        cosas[j]++;
                    }
                }
                else
                {
                    if(retornos.get(i)> (min + paso*j) && retornos.get(i)<= (min+paso*(j+1)) )
                    {
                        cosas[j]++;
                    }
                }
            }
        }

        //for(int i = 0; i <categorias;i++)
        //System.out.println(cosas[i]);

*/

        datos = new double[retornos.size()];
        for(int i=0; i<retornos.size();i++)
            datos[i]=retornos.get(i);

        categorias = (int) (1 + 3.3 * Math.log10(retornos.size()));
        //System.out.println(categorias+"   "+retornos.size());

        //autoCorrelation(datos,retornos.size());



            if(real)retornos.clear();

    }



    /**
     * The starting point for the demo.
     *
     * @param args  ignored.
     *
     * @throws IOException  if there is a problem saving the file.
     */
   /* public static void main(String[] args) throws IOException {

        HistogramaRetornos demo = new HistogramaRetornos("Histogram Demo","otro");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }*/

}
