
package graficos;

import java.awt.Color;
import java.sql.*;
import basedatos.*;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Calendar;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
//import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleAnchor;
//import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;


public class Retornos extends JFrame {
ConexionBD BD;
int repeticion;

int type = -1;

String accion = new String();
     double millis;


    public Retornos(String title, String nombre, int repet, int ty) {

        
        super(title+" "+nombre);

        repeticion = repet;
        accion = nombre;
        type = ty;


        final JFreeChart chart = createChart();
        final ChartPanel panel = new ChartPanel(chart, true, true, true, false, true);
        panel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(panel);

    }

    /**
     * Creates a chart.
     *
     * @return a chart.
     */
    private JFreeChart createChart() {

        final XYDataset priceData = createPriceDataset();
        //final String title = ""+accion;
        final String title = "";
        final JFreeChart chart = ChartFactory.createTimeSeriesChart(
            title, 
            "Dates",
            "Returns",
            priceData, 
            false,
            true,
            false
        );
        final XYPlot plot = chart.getXYPlot();
        final NumberAxis rangeAxis1 = (NumberAxis) plot.getRangeAxis();
        //rangeAxis1.setLowerMargin(0.40);  // to leave room for volume bars
        
        ////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////
        //rangeAxis1.setLabelFont(new Font("Times New Roman",Font.PLAIN,12));
        ////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////
        
        final DecimalFormat format = new DecimalFormat("#,###.###");
        rangeAxis1.setNumberFormatOverride(format);

        final XYItemRenderer renderer1 = plot.getRenderer();
        renderer1.setToolTipGenerator(
            new StandardXYToolTipGenerator(
                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("#.##")
            )
        );


        Calendar actual = Calendar.getInstance();
        //actual.add(Calendar.DATE, -1);
         Hour hour = new Hour(2, new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH)+1,  actual.get(Calendar.YEAR)));

        Minute min = new Minute(15, hour);
        millis = min.getFirstMillisecond();
        Marker currentEnd = new ValueMarker(millis);
        currentEnd.setPaint(Color.BLACK);
        //currentEnd.setLabel("Real");
        currentEnd.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
        currentEnd.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
        plot.addDomainMarker(currentEnd);

        return chart;

    }

    /**
     * Creates a sample dataset.
     *
     * @return A sample dataset.
     */
    private XYDataset createPriceDataset() {

        // create dataset 1...
        final TimeSeries series1 = new TimeSeries("Returns", Day.class);
        
        int fecha;
        int indice = 0;
        Calendar actual = Calendar.getInstance();
        ArrayList<Double> Returns = new ArrayList<Double>();
        final DecimalFormat format = new DecimalFormat("#,###.#####");
        

        
        double precio_t, precio_t_1;
        precio_t = precio_t_1 = 0;

        BD = new ConexionBD("mysql");
        ResultSet r = BD.consulta("SELECT fecha, precioCierre, cantidad FROM cotizacion WHERE (repeticion = 0 or repeticion = "+repeticion+") AND IDProducto = (SELECT IDProducto FROM producto WHERE nombre = \""+accion+"\") GROUP BY fecha");
        //ResultSet r = BD.consulta("SELECT fecha, precioPromedio, count(fecha) as volumen FROM cotizacion WHERE IDProducto = "+IdProducto+" GROUP BY fecha");
        try {
         while(r.next())
                {
                    fecha = r.getInt(1);
                    actual = Calendar.getInstance();
                    precio_t_1 = precio_t;
                    precio_t = r.getFloat(2);
                   //System.out.println(actual.get(Calendar.DATE) + " " +actual.get(Calendar.MONTH)+ " " + actual.get(Calendar.YEAR) + " : "+fecha);
                   actual.add(Calendar.DATE, fecha-1);

                   if(indice !=0)
                   {

                       if(type==0)
                       {
                           series1.add(new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH)+1,  actual.get(Calendar.YEAR)), Math.log( precio_t/precio_t_1));
                          // System.out.println(format.format(Math.log( precio_t/precio_t_1))+"\t"+fecha);
                           Returns.add(Math.log( precio_t/precio_t_1));
                       }

                       else if(type==1)
                        {
                           series1.add(new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH)+1,  actual.get(Calendar.YEAR)), Math.pow(Math.log( precio_t/precio_t_1),2));
                           //System.out.println(format.format(Math.pow(Math.log( precio_t/precio_t_1),2))+"\t"+fecha);
                           Returns.add(Math.pow(Math.log( precio_t/precio_t_1),2));


                        }
                        else if(type == 2)
                        {
                           series1.add(new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH)+1,  actual.get(Calendar.YEAR)), Math.abs(Math.log( precio_t/precio_t_1)));
                           //System.out.println(format.format(Math.abs(Math.log( precio_t/precio_t_1)))+"\t"+fecha);
                           Returns.add(Math.abs(Math.log( precio_t/precio_t_1)));

                        }

                   }

                   indice++;
                }

                BD.cerrarConexion();
            }
        catch (SQLException ex) {
                    ex.printStackTrace();
                }




        //System.out.println(indice);
          

//            int size =10;
//            double[] R = new double[size];
//        double sum;
//
//        for (int i=0;i<size;i++) {
//            sum=0;
//            for (int j=0;j<size-i;j++) {
//                sum+=autocorrelacion.get(j)*autocorrelacion.get(j+i);
//            }
//            R[i]=sum;
//        }
//
//
//        for(int i=0;i<R.length;System.out.println(R[i++]));


        //if(type==0)System.out.println("Returns");
        //if(type==1)System.out.println("Squared Returns");
        //if(type==2)System.out.println("Abs Returns");

        autoCorrelation(Returns, 21);

        return new TimeSeriesCollection(series1);

    }



  public static ArrayList<Double> autoCorrelation(ArrayList date,int lag){

      //System.out.println("\n\nAutocorrelation\tLag");
      final DecimalFormat format = new DecimalFormat("#,###.#####");


      ArrayList<Double> R = new ArrayList<Double>();
      double avg = 0;
      for(int i=0;i<date.size();i++)
          avg+=Double.parseDouble(date.get(i).toString());

      avg/=date.size();

      int m=2; ///m=2,3,4,…,n/4Average\tSDesv\tKurtosis\tSkewness

      for(m=0;m<lag && m<date.size();m++)
      {
          double num = 0;
          double den = 0;
          for(int i=0; i<date.size()-m;i++)
          {
              num+=(Double.parseDouble(date.get(i).toString())-avg)*(Double.parseDouble(date.get(i+m).toString())-avg);
          }

          for(int i=0; i<date.size();i++)
          {
              den+=Math.pow((Double.parseDouble(date.get(i).toString())-avg),2);
          }

         // System.out.println(format.format(num/den)+"\t"+m);
          R.add(num/den);
      }

      return R;
}
   
    /**
     * Starting point for the price/volume chart demo application.
     *
     * @param args  ignored.
     */
    public static void main(final String[] args) {
        //Date pepe = null;
        //System.out.println(pepe.getYear());
        //java.util.Calendar fecha = java.util.Calendar.getInstance();
        //System.out.println(fecha.get(java.util.Calendar.YEAR));

        final Retornos generar = new Retornos("Precio Sobre Volumen","PREC",1,0);
        generar.pack();
        RefineryUtilities.centerFrameOnScreen(generar);
        generar.setVisible(true);
        
        

    }

}
