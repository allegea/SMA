package graficos; 


import java.sql.ResultSet;
import java.sql.SQLException;
import basedatos.ConexionBD;

import java.awt.Dimension; 
import java.text.DecimalFormat; 
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel; 
import org.jfree.chart.ChartPanel; 
import org.jfree.chart.JFreeChart; 
import org.jfree.chart.axis.*; 
import org.jfree.chart.labels.StandardXYToolTipGenerator; 
import org.jfree.chart.plot.DatasetRenderingOrder; 
import org.jfree.chart.plot.XYPlot; 
import org.jfree.chart.renderer.xy.*; 
import org.jfree.data.xy.IntervalXYDataset; 
import org.jfree.data.xy.XYDataset; 
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities; 
 
public class Autocorrelacion extends JFrame 
{ 
     int identificador;
     String nombre;
     int repeticion;
     int type;
         ArrayList<Float> prices = new ArrayList<Float>();
        ArrayList<Double> retornos = new ArrayList<Double>();
        ArrayList<Double> retornosS = new ArrayList<Double>();
        ArrayList<Double> retornosA = new ArrayList<Double>();
        ArrayList<Double> volumen = new ArrayList<Double>();

         XYSeriesCollection encima; 
         XYSeriesCollection debajo; 
    
    public Autocorrelacion(String s, int rep, int tp) 
    { 
        
        super(s); 
        nombre=s;
        repeticion=rep;
        type=tp;
        
                 try{

                     ConexionBD BD =  new ConexionBD("mysql");
         ResultSet info = BD.consulta("Select idproducto, nombre from producto where nombre = '"+nombre+"'");
         if(info.next())
             identificador = info.getInt(1);
 


         BD.cerrarConexion();
         }
        catch (SQLException ex) {
                    ex.printStackTrace();
                }
        
        JPanel jpanel = createDemoPanel(); 
        jpanel.setPreferredSize(new Dimension(500, 270)); 
        setContentPane(jpanel); 
    } 
 
    private  JFreeChart createChart() 
    { 
       // Retornos();
        IntervalXYDataset intervalxydataset = createDataset1(); 
        XYBarRenderer xybarrenderer = new XYBarRenderer(0.05000000000000001D); 
        xybarrenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator("{0}: ({1}, {2})", new DecimalFormat("0"), new DecimalFormat("0.000000"))); 
        NumberAxis numberaxiss = new NumberAxis("lags");
        numberaxiss.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
       // dateaxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE); 
        NumberAxis numberaxis = new NumberAxis("Coefficients"); 
        XYPlot xyplot = new XYPlot(intervalxydataset, numberaxiss, numberaxis, xybarrenderer); 
        

        XYDataset xydataset = encima; 
        XYDataset xydataset2 = debajo; 
        StandardXYItemRenderer standardxyitemrenderer = new StandardXYItemRenderer(); 
        
        standardxyitemrenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator("{0}: ({1}, {2})", new DecimalFormat("0"), new DecimalFormat("0.000000"))); 
        xyplot.setDataset(1, xydataset); 
        xyplot.setDataset(2, xydataset2);
        xyplot.setRenderer(1, standardxyitemrenderer); 
        xyplot.setRenderer(2, standardxyitemrenderer); 
        xyplot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD); 
        
   

        String titulo = "";
        if (type == 0) {
            titulo="Autocorr Returns "+nombre+" for iteration "+repeticion;
            this.setTitle(titulo);
            return new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, xyplot, true);
            
        } else if (type == 1) {
            
            titulo="Autocorr Squared Returns "+nombre+" for iteration "+repeticion;
            this.setTitle(titulo);
            return new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, xyplot, true);
        } else {
            
            titulo="Autocorr Abs Returns "+nombre+" for iteration "+repeticion;
            this.setTitle(titulo);
            return new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, xyplot, true);
        }
        
        
       // return new JFreeChart("Autocorr Returns "+nombre, JFreeChart.DEFAULT_TITLE_FONT, xyplot, true); 
    } 
 
    private  IntervalXYDataset createDataset1() 
    {   Retornos();

        int lags = 21;
        double val=0;
        ArrayList<Double> autocorr = new ArrayList<Double>();
        if (type == 0) {
            autocorr = Retornos.autoCorrelation(retornos, lags);
            val = 2/(Math.sqrt(retornos.size()));
        } else if (type == 1) {
            autocorr = Retornos.autoCorrelation(retornosS, lags);
            val = 2/(Math.sqrt(retornosS.size()));
        } else {
            autocorr = Retornos.autoCorrelation(retornosA, lags);
            val = 2/(Math.sqrt(retornosA.size()));
        }
        
        //System.out.println(retornos.size()+" - "+val+" - "+type+" - "+autocorr);
        //System.out.println(retornosS.size()+" - "+val+" - "+type+" - "+autocorr);
        //System.out.println(retornosA.size()+" - "+val+" - "+type+" - "+autocorr);
         
        
        XYSeries auto = new XYSeries("Coefficients");
         XYSeries enc = new XYSeries("Upper limit");
          XYSeries deb = new XYSeries("Lower limit");
          
          for(int i=0;i<=autocorr.size();i++)
        {   
            if(i<autocorr.size()&& i>=0)
            auto.add(i, autocorr.get(i)); 
            enc.add(i, val); 
           deb.add(i, -1*val); 
        }
          
          encima = new XYSeriesCollection(); 
          encima.addSeries(enc);
        debajo = new XYSeriesCollection();
        debajo.addSeries(deb);
        /*TimeSeries timeseries = new TimeSeries("Coefficients", org.jfree.data.time.Day.class); 
        TimeSeries enc = new TimeSeries("Upper limit", org.jfree.data.time.Day.class); 
        TimeSeries deb = new TimeSeries("Lower limit", org.jfree.data.time.Day.class); 
        for(int i=0;i<autocorr.size();i++)
        {   actual.add(Calendar.DATE, 1);
            timeseries.add(new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH)+1,  actual.get(Calendar.YEAR)), autocorr.get(i)); 
            enc.add(new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH)+1,  actual.get(Calendar.YEAR)), val); 
           deb.add(new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH)+1,  actual.get(Calendar.YEAR)), -1*val); 
            
            
        }
        
        actual.add(Calendar.DATE, 1);
        enc.add(new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH) + 1, actual.get(Calendar.YEAR)), val);
        deb.add(new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH) + 1, actual.get(Calendar.YEAR)), -1 * val);

        encima = new TimeSeriesCollection(enc); 
        debajo = new TimeSeriesCollection(deb);*/ 
        XYSeriesCollection dataset = new XYSeriesCollection();

        dataset.addSeries(auto);
        //return new TimeSeriesCollection(timeseries); 
        return dataset;
    } 
 

 
    public  JPanel createDemoPanel() 
    { 
        JFreeChart jfreechart = createChart(); 
        jfreechart.getLegend().setVisible(false);
        return new ChartPanel(jfreechart); 
    } 
    
     private void Retornos()
    {
        ResultSet info = null;
        float precio_t_1=0;
        float precio_t = 0;
 
        ConexionBD BD = new ConexionBD("mysql");

            
            int indice =0;

         info = BD.consulta("SELECT precioCierre, cantidad  FROM cotizacion WHERE repeticion = "+repeticion+" AND IDProducto = "+identificador+" GROUP BY fecha");
        // System.out.println("SELECT precioCierre, cantidad  FROM cotizacion WHERE repeticion = "+repeticion+" AND IDProducto = "+identificador+" GROUP BY fecha");
        //ResultSet r = BD.consulta("SELECT fecha, precioPromedio, count(fecha) as volumen FROM cotizacion WHERE IDProducto = "+IdProducto+" GROUP BY fecha");
        try {
         while(info.next())
                {

                    precio_t_1 = precio_t;
                    precio_t = info.getFloat(1);
                    double volum = precio_t*info.getInt(2);

                    prices.add(precio_t);
                    volumen.add(volum);

                   if(indice !=0)
                   {
                       double retorno =  Math.log( precio_t/precio_t_1);
                       if(type == 0)
                       retornos.add(retorno);
                       else if(type == 1)
                       retornosS.add(Math.pow(retorno, 2));
                       else
                       retornosA.add(Math.abs(retorno));
                    }

                   indice++;

                }

         //System.out.println(retornos.size());
/*
                if(!retornos.isEmpty())
                {
                    distribucion(retornos);
                } createDataset1();*/
            //Retornos.autoCorrelation(null,1);


                
            }
        catch (SQLException ex) {
                    ex.printStackTrace();
                }

         

        
        BD.cerrarConexion();
        
        
    }
 
    public static void main(String args[]) 
    { 
        Autocorrelacion overlaidxyplotdemo1 = new Autocorrelacion("PREC",10,0); 
        overlaidxyplotdemo1.pack(); 
        RefineryUtilities.centerFrameOnScreen(overlaidxyplotdemo1); 
        overlaidxyplotdemo1.setVisible(true); 
    } 
} 