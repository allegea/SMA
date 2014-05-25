

package graficos;

import java.sql.*;
import basedatos.*;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RefineryUtilities;
//import org.jfree.ui.ApplicationFrame;
//import org.jfree.ui.RefineryUtilities;

/**
 * A simple demonstration application showing how to create a bar chart with a custom item
 * label generator.
 *ApplicationFrame
 */
public class AccionesVolatilidad extends JFrame {
ConexionBD BD;
ConexionBD BDnombre;
ConexionBD BDpromedio;
int repeticion;
    /**
     * Creates a new demo instance.
     *
     * @param title  the frame title.
     */
    public AccionesVolatilidad(final String title, int repeti) {


        super(title);

        repeticion = repeti;

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
        
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        BD = new ConexionBD("mysql");
        BDnombre = new ConexionBD("mysql");
        BDpromedio = new ConexionBD("mysql");
        ResultSet r = BD.consulta("SELECT IDProducto, std(precioCierre) FROM cotizacion where repeticion = 0 or repeticion = "+repeticion+" GROUP BY IDProducto");
        ResultSet nombre = null;
        ResultSet promedio = null;
        try {
         while(r.next())
                {

                   nombre = BDnombre.consulta("SELECT nombre FROM producto WHERE IDProducto = "+r.getString(1));
                   promedio = BDpromedio.consulta("SELECT avg(precioCierre) FROM cotizacion WHERE IDProducto = "+r.getString(1)+" and (repeticion = 0 or repeticion = "+repeticion+")");
                   if(nombre.next() && promedio.next())
                   dataset.addValue((r.getDouble(2)/promedio.getDouble(1))*100,  nombre.getString(1),category1);


                }

                
            }
        catch (SQLException ex) {
                    ex.printStackTrace();
                }

        BD.cerrarConexion();
        BDnombre.cerrarConexion();
         BDpromedio.cerrarConexion();
        
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
            "Volatility of the Stocks",       // chart title
            "Stock",               // domain axis label
            "Porcentage",                  // range axis label
            dataset,                  // data
            PlotOrientation.VERTICAL, // orientation
            true,                    // include legend
            true,                     // tooltips?
            false                     // URLs?
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.white);

        // get a reference to the plot for further customisation...
        final CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        
        // set the range axis to display integers only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setUpperMargin(0.15);
        
        // disable bar outlines...
        final CategoryItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesItemLabelsVisible(0, Boolean.TRUE);
        
        final CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        // OPTIONAL CUSTOMISATION COMPLETED.
        
        return chart;
        
    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(final String[] args) {

        final AccionesVolatilidad demo = new AccionesVolatilidad("Bar Chart Demo 8",1);
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
