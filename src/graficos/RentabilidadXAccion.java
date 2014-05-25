

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

public class RentabilidadXAccion extends JFrame {
    ConexionBD BD;
    ConexionBD BDA;
    ConexionBD BDcantidad;
    ConexionBD BDnombres;
    int repeticion;
    /**
     * Creates a new demo instance.
     *
     * @param title  the frame title.
     */
    public RentabilidadXAccion(final String title, int repet) {

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
        
              // column keys...
        final String category1 = "";
        
        // create the dataset...
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        float precioFinal = 0;
        float precioInicial = 0;

        BD = new ConexionBD("mysql");
        BDA = new ConexionBD("mysql");
        BDcantidad = new ConexionBD("mysql");
        BDnombres = new ConexionBD("mysql");
        ResultSet ValorInicial = null;
        ResultSet ValorFinal = null;
        ResultSet nombre = null;
        ResultSet IDProducto = BDcantidad.consulta("SELECT IDProducto FROM producto");
        try {

                 

                 while(IDProducto.next())
                 {
                    ValorInicial = BD.consulta("SELECT IDProducto, precioCierre FROM cotizacion WHERE  fecha = 0 AND precioCierre is not NULL AND IDProducto = "+IDProducto.getInt(1));
                    ValorFinal = BDA.consulta("SELECT IDProducto, precioCierre FROM cotizacion WHERE repeticion = "+repeticion+" AND fecha = (SELECT max(fecha) FROM cotizacion WHERE repeticion = "+repeticion+" AND IDProducto = "+IDProducto.getInt(1)+" AND precioCierre is not NULL) AND IDProducto = "+IDProducto.getInt(1));
                    nombre = BDnombres.consulta("SELECT nombre FROM producto WHERE IDProducto = "+IDProducto.getInt(1));

                   //System.out.println("ID  "+IDProducto.getInt(1));
                    
                     if(ValorInicial.next() && ValorFinal.next() && nombre.next())
                            {
                               //System.out.println("ID producto es "+ValorInicial.getFloat(2));
                                precioInicial = ValorInicial.getFloat(2);
                                precioFinal = ValorFinal.getFloat(2);
                                if(precioInicial != 0.0 && precioFinal !=0)
                                    dataset.addValue((precioFinal/precioInicial-1)*100,  nombre.getString(1),category1);
                                //dataset.setValue(r.getString(1), Math.abs((precioFinal/precioInicial-1))*100);



                            }

                  }
            }
        catch (SQLException ex) {
                    ex.printStackTrace();
                }

        BD.cerrarConexion();
        BDA.cerrarConexion();
        BDcantidad.cerrarConexion();
        BDnombres.cerrarConexion();
        
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
            "Profitability per Stock",       // chart title
            "Stocks",               // domain axis label
            "Percentage",                  // range axis label
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
    

    /*public static void main(final String[] args) {

        final RentabilidadXAccion demo = new RentabilidadXAccion("Bar Chart Demo 8");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }*/

}
