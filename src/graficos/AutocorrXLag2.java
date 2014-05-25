package graficos; 


import java.sql.ResultSet;
import java.sql.SQLException;
import basedatos.ConexionBD;

import java.awt.Dimension; 
import java.text.DecimalFormat; 
import java.text.SimpleDateFormat; 
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.JFrame;
import javax.swing.JPanel; 
import org.jfree.chart.ChartPanel; 
import org.jfree.chart.JFreeChart; 
import org.jfree.chart.axis.*; 
import org.jfree.chart.labels.StandardXYToolTipGenerator; 
import org.jfree.chart.plot.DatasetRenderingOrder; 
import org.jfree.chart.plot.XYPlot; 
import org.jfree.chart.renderer.xy.*; 
import org.jfree.data.time.*; 
import org.jfree.data.xy.IntervalXYDataset; 
import org.jfree.data.xy.XYDataset; 
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities; 
 
public class AutocorrXLag2 extends JFrame 
{ 
    ConexionBD BD;
    int repeticion;
    int iteraciones = -1;
    String nombre;
    int identificador;
    int lags = -1;
    static int ready;
    
    XYSeriesCollection encima; 
    XYSeriesCollection debajo; 
    
    static ArrayList<ArrayList<Float>> prices;
    static ArrayList<ArrayList<Double>> retornos;
    static ArrayList<ArrayList<Double>> retornosS;
    static ArrayList<ArrayList<Double>> retornosA ;
    
    static ArrayList<ArrayList<Double>> autocorrP ;
    static ArrayList<ArrayList<Double>> autocorrR ;
    static ArrayList<ArrayList<Double>> autocorrRS ;
    static ArrayList<ArrayList<Double>> autocorrRA ;
    int type = -1;
    
    public AutocorrXLag2(String s, int lag, int tp) 
    { 
        
                super();

                
        nombre = s;
        lags = lag;
        type = tp;
        
        try {
            BD = new ConexionBD("mysql");
            ResultSet info = BD.consulta("Select max(repeticion) from cotizacion");
            if (info.next()) {
                iteraciones = info.getInt(1);
            }


            info = BD.consulta("Select idproducto, nombre from producto where nombre = '" + nombre + "'");
            if (info.next()) {
                identificador = info.getInt(1);
            }



            BD.cerrarConexion();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        if (ready == 0) {
            prices = new ArrayList<ArrayList<Float>>();
            retornos = new ArrayList<ArrayList<Double>>();
            retornosS = new ArrayList<ArrayList<Double>>();
            retornosA = new ArrayList<ArrayList<Double>>();

            autocorrP = new ArrayList<ArrayList<Double>>();
            autocorrR = new ArrayList<ArrayList<Double>>();
            autocorrRS = new ArrayList<ArrayList<Double>>();
            autocorrRA = new ArrayList<ArrayList<Double>>();


            for (int i = 0; i <= iteraciones; i++) {
                prices.add(i, new ArrayList<Float>());
                retornos.add(i, new ArrayList<Double>());
                retornosS.add(i, new ArrayList<Double>());
                retornosA.add(i, new ArrayList<Double>());

                autocorrP.add(i, new ArrayList<Double>());
                autocorrR.add(i, new ArrayList<Double>());
                autocorrRS.add(i, new ArrayList<Double>());
                autocorrRA.add(i, new ArrayList<Double>());


            }

            autocorr();
            ready = -1;
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
       // DateAxis dateaxis = new DateAxis("Iteration"); 
         NumberAxis dateaxis = new NumberAxis("Iteration"); 
         dateaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        //dateaxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE); 
        NumberAxis numberaxis = new NumberAxis("Coefficients"); 
        XYPlot xyplot = new XYPlot(intervalxydataset, dateaxis, numberaxis, xybarrenderer); 
        
        XYDataset xydataset = encima; 
        XYDataset xydataset2 = debajo; 
        
        StandardXYItemRenderer standardxyitemrenderer = new StandardXYItemRenderer(); 
        standardxyitemrenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator("{0}: ({1}, {2})", new DecimalFormat("0"), new DecimalFormat("0.000000"))); 
        xyplot.setDataset(1, xydataset); 
        xyplot.setDataset(2, xydataset2);
        xyplot.setRenderer(1, standardxyitemrenderer); 
        xyplot.setRenderer(2, standardxyitemrenderer); 
        xyplot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD); 
        
        String title="";
        if(type==0)
            title = "Autocorr of "+nombre+" prices on lag "+lags;
        if(type==1)
            title = "Autocorr of "+nombre+" returns on lag "+lags;
        if(type==2)
            title = "Autocorr of "+nombre+" squared returns on lag "+lags;
        if(type==3)
            title = "Autocorr of "+nombre+" abs returns on lag "+lags;

        this.setTitle(title);
            return new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, xyplot, true);

       // return new JFreeChart("Autocorr Returns "+nombre, JFreeChart.DEFAULT_TITLE_FONT, xyplot, true); 
    } 
 
    private  IntervalXYDataset createDataset1() 
    {  
      
       
        double val=0;

        if(type==0)
           val = 2/(Math.sqrt(prices.get(0).size()));
        if(type==1)
            val = 2/(Math.sqrt(prices.get(0).size()));
        if(type==2)
            val = 2/(Math.sqrt(prices.get(0).size()));
        if(type==3)
           val = 2/(Math.sqrt(prices.get(0).size()));
        

        XYSeries auto = new XYSeries("Coefficients");
         XYSeries enc = new XYSeries("Upper limit");
          XYSeries deb = new XYSeries("Lower limit");
       
        //System.out.println(retornosS.size()+" - "+val+" - "+type+" - "+autocorr);
        //System.out.println(retornosA.size()+" - "+val+" - "+type+" - "+autocorr);
        /*TimeSeries timeseries = new TimeSeries("Coefficients", org.jfree.data.time.Day.class); 
        TimeSeries enc = new TimeSeries("Upper limit", org.jfree.data.time.Day.class); 
        TimeSeries deb = new TimeSeries("Lower limit", org.jfree.data.time.Day.class); 
        
        
       
        for(int i=0;i<=iteraciones;i++)
        {  
            actual.add(Calendar.DATE, 1);
            enc.add(new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH)+1,  actual.get(Calendar.YEAR)), val); 
           deb.add(new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH)+1,  actual.get(Calendar.YEAR)), -1*val); 
           
           
            if(type==0)
               timeseries.add(new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH)+1,  actual.get(Calendar.YEAR)), autocorrP.get(i).get(lags)); 
            if(type==1)
                timeseries.add(new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH)+1,  actual.get(Calendar.YEAR)), autocorrR.get(i).get(lags)); 
            if(type==2)
               timeseries.add(new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH)+1,  actual.get(Calendar.YEAR)), autocorrRS.get(i).get(lags)); 
            if(type==3)
               timeseries.add(new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH)+1,  actual.get(Calendar.YEAR)), autocorrRA.get(i).get(lags)); 

        }
        
        actual.add(Calendar.DATE, 1);
            enc.add(new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH)+1,  actual.get(Calendar.YEAR)), val); 
           deb.add(new Day(actual.get(Calendar.DATE), actual.get(Calendar.MONTH)+1,  actual.get(Calendar.YEAR)), -1*val); 

        encima = new TimeSeriesCollection(enc); 
        debajo = new TimeSeriesCollection(deb); */
          
          for(int i=0;i<=iteraciones;i++)
        {  
            //actual.add(Calendar.DATE, 1);
            enc.add(i, val); 
           deb.add(i, -1*val); 
           
           
            if(type==0)
               auto.add(i, autocorrP.get(i).get(lags)); 
            if(type==1)
                auto.add(i, autocorrR.get(i).get(lags)); 
            if(type==2)
               auto.add(i, autocorrRS.get(i).get(lags)); 
            if(type==3)
               auto.add(i, autocorrRA.get(i).get(lags)); 

        }
        
        encima = new XYSeriesCollection(); 
        encima.addSeries(enc);
        debajo = new XYSeriesCollection();
        debajo.addSeries(deb);
        XYSeriesCollection dataset = new XYSeriesCollection();

        dataset.addSeries(auto);
        
        //return new TimeSeriesCollection(timeseries); 
        return dataset;
    } 
 

 
    public  JPanel createDemoPanel() 
    { 
        JFreeChart jfreechart = createChart(); 
        return new ChartPanel(jfreechart); 
    } 
    
    
     private void autocorr()
    {
        ResultSet info = null;
        float precio_t_1=0;
        float precio_t = 0;
 
        BD = new ConexionBD("mysql");

        for(int i=0;i<=iteraciones;i++)
        {
            
            int indice =0;

         info = BD.consulta("SELECT precioCierre FROM cotizacion WHERE repeticion = "+i+" AND IDProducto = "+identificador+" GROUP BY fecha");
         //System.out.println("SELECT precioCierre FROM cotizacion WHERE repeticion = "+i+" AND IDProducto = "+identificador+" GROUP BY fecha");
        //ResultSet r = BD.consulta("SELECT fecha, precioPromedio, count(fecha) as volumen FROM cotizacion WHERE IDProducto = "+IdProducto+" GROUP BY fecha");
        try {
         while(info.next())
                {

                    precio_t_1 = precio_t;
                    precio_t = info.getFloat(1);
                  

                    prices.get(i).add(precio_t);

                   if(indice !=0)
                   {
                       double retorno =  Math.log( precio_t/precio_t_1);
                       retornos.get(i).add(retorno);
                       retornosS.get(i).add(Math.pow(retorno, 2));
                       retornosA.get(i).add(Math.abs(retorno));
                    }

                   indice++;

                }


         
        
            autocorrP.set(i, Retornos.autoCorrelation(prices.get(i), 21));
            autocorrR.set(i, Retornos.autoCorrelation(retornos.get(i), 21));
            autocorrRS.set(i, Retornos.autoCorrelation(retornosS.get(i), 21));
            autocorrRA.set(i, Retornos.autoCorrelation(retornosA.get(i), 21));
 

                //Retornos.autoCorrelation(null,1);


                
            }
        catch (SQLException ex) {
                    ex.printStackTrace();
                }

         

        }
        BD.cerrarConexion();
        
        
    }
 
    public static void main(String args[]) 
    { 
        AutocorrXLag2 overlaidxyplotdemo1 = new AutocorrXLag2("PREC",1,1); 
        overlaidxyplotdemo1.pack(); 
        RefineryUtilities.centerFrameOnScreen(overlaidxyplotdemo1); 
        overlaidxyplotdemo1.setVisible(true); 
    } 
} 