package agentes;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Resultados.java
 *
 * Created on 12-oct-2010, 20:40:19
 */




import informacion.OfertaCompra;
import informacion.OfertaVenta;

import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.PeriodAxis;
import org.jfree.chart.axis.PeriodAxisLabelInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

/**
 *
 * @author Allegea
 */
public class Libro2 extends javax.swing.JFrame {

 Random objeto = new java.util.Random() ;
    
    public TimeSeries encima  = new TimeSeries("Upper Price",  Millisecond.class);
    public TimeSeries debajo  = new TimeSeries("Lower Price",  Millisecond.class);
    public TimeSeries referencia  = new TimeSeries("Reference Price",  Millisecond.class);
    public TimeSeries actual  = new TimeSeries("Trading Price",  Millisecond.class);
    final TimeSeriesCollection dataset = new TimeSeriesCollection();
    public XYPlot plot;
   

    public DefaultTableModel dtm;
    public JTable tableData;

    public DefaultTableModel dtmInfo = new DefaultTableModel();
    public JTable tableInfo = new JTable(dtmInfo);

    javax.swing.JScrollPane TablaResultados = new javax.swing.JScrollPane();
    javax.swing.JScrollPane TablaInformacion = new javax.swing.JScrollPane();

    private ArrayList<ArrayList<Float>> historicoPrecios = new ArrayList<ArrayList<Float>>();
    private ArrayList<ArrayList<Float>> historicoEncima = new ArrayList<ArrayList<Float>>();
    private ArrayList<ArrayList<Float>> historicoDebajo = new ArrayList<ArrayList<Float>>();
    private ArrayList<ArrayList<Float>> historicoReferencia = new ArrayList<ArrayList<Float>>();
    private ArrayList<ArrayList<Millisecond>> historicoHora = new ArrayList<ArrayList<Millisecond>>();
    private ArrayList<ArrayList<Millisecond>> historicoHoraPrecioActual = new ArrayList<ArrayList<Millisecond>>();
    private ArrayList<Double> historicoHoraEtiqueta = new ArrayList<Double>();
    private ArrayList<String> historicoTextoEtiqueta = new ArrayList<String>();
    int cantidadProductos = 0;
    public int ProductoActual = 0;
    long tiempoSim;
    long tiempoFinal;
    long ajuste;
    Calendar horaActual = Calendar.getInstance();
    Date horaInicial = new Date(horaActual.get(Calendar.YEAR),horaActual.get(Calendar.MONTH),horaActual.get(Calendar.DATE),horaActual.get(Calendar.HOUR_OF_DAY),horaActual.get(Calendar.MINUTE),horaActual.get(Calendar.SECOND));
    Millisecond inicial = new Millisecond(horaInicial);
    Millisecond inicialA = new Millisecond();
    Date tiempos;


    DecimalFormat df = new DecimalFormat("##########.###");
    /** Creates new form Resultados */
    public Libro2(int acciones, long tiempo, long tiempoFin) {
        cantidadProductos = acciones;
        tiempoSim=tiempo;//son 235 minutos lo que dura
        tiempoFinal=tiempoFin;
        ajuste = tiempoFin;

       


        /*final CalcesEnSimulacion generar = new CalcesEnSimulacion();
        generar.pack();
        RefineryUtilities.centerFrameOnScreen(generar);
        generar.setVisible(true);*/

        initComponents();

        borrar();

        String[] header = new String[4];
        header[0] = "Mnemonic";
        header[1] = "Session";
        header[2] = "Higher Price";
        header[3] = "Lower Price";

        dtmInfo.setColumnIdentifiers(header);

         for (int i=0; i<cantidadProductos; i++)
         {
            historicoPrecios.add(i, new ArrayList<Float>());
            historicoEncima.add(i, new ArrayList<Float>());
            historicoDebajo.add(i, new ArrayList<Float>());
            historicoReferencia.add(i, new ArrayList<Float>());
            historicoHora.add(i, new ArrayList<Millisecond>());
            historicoHoraPrecioActual.add(i, new ArrayList<Millisecond>());

         }

        graficar();

       // new HiloGraficaRunTime();

    }

    private void borrar()
    {
        dtm = new DefaultTableModel();
        String[] header = new String[7];
        header[0] = "Agent ID";
        header[1] = "Buy Order";
        header[2] = "Amount";
        header[3] = "";
        header[4] = "Agent ID";
        header[5] = "Sell Order";
        header[6] = "Amount";

        dtm.setColumnIdentifiers(header);

        tableData = new JTable(dtm);

    }

    /*public void TablaLibroOfertas(ArrayList<OfertaVenta> ofertasVentaTmp, ArrayList<OfertaCompra> ofertasCompraTmp )
    {

    }*/

    public void TablaLibroOfertas(ArrayList<OfertaVenta> ofertasVentaTmp, ArrayList<OfertaCompra> ofertasCompraTmp )
    {
        int indice = ofertasCompraTmp.size();
        Object[] row = new Object[7];

        
        borrar();

        if(indice < ofertasVentaTmp.size() )
            indice = ofertasVentaTmp.size();

        try{

        for(int i=0; i<indice;i++ )
        {
            if(i < ofertasCompraTmp.size())
            {
                //row[0] = ((OfertaCompra)ofertasCompraTmp.get(i)).getInfo().getIDComprador();
                row[0] = ((OfertaCompra)ofertasCompraTmp.get(i)).getAID().getLocalName();
                row[1] = ((OfertaCompra)ofertasCompraTmp.get(i)).getInfo().getPrecioCompra();
                row[2] = ((OfertaCompra)ofertasCompraTmp.get(i)).getInfo().getCantidad();
            }
            else
            {
                row[0] = "";
                row[1] = "";
                row[2] = "";
            }

            row[3] = "";

            if(i < ofertasVentaTmp.size())
            {
               // row[4] =  ((OfertaVenta)ofertasVentaTmp.get(i)).getInfo().getIDVendedor();
                row[4] =  ((OfertaVenta)ofertasVentaTmp.get(i)).getAID().getLocalName();
                row[5] =  ((OfertaVenta)ofertasVentaTmp.get(i)).getInfo().getPrecioVenta();
                row[6] =  ((OfertaVenta)ofertasVentaTmp.get(i)).getInfo().getCantidad();
            }
            else
            {
                row[4] = "";
                row[5] = "";
                row[6] = "";
            }
            dtm.addRow(row);
        }

        }catch(java.lang.ArrayIndexOutOfBoundsException e){
            //e.printStackTrace();
            System.out.println("*************************");
            System.out.println("*************************");
            System.out.println("Error java.lang.ArrayIndexOutOfBoundsException en  TablaLibroOfertas");
            System.out.println("*************************");
            System.out.println("*************************");
        }

        TablaResultados.setViewportView(tableData);
    }

    public void TablaInformacion(String idAccion, int estado, float mayorP, float menorP, boolean primera, int posicion)
    {
        Object[] row = new Object[4];
        row[0] = idAccion;
        if(estado == 0)
        row[1]= "Continuous";
        else
        row[1]= "Call Market";

        row[2]= mayorP;
        row[3]= menorP;

        if(primera)
        {
            dtmInfo.addRow(row);
            TablaInformacion.setViewportView(tableInfo);
        }
        else
        {
            for(int i= 0;i<4;i++)
                 dtmInfo.setValueAt(row[i], posicion, i);
        }

    }

    private void graficar()
    {

        tableInfo.addMouseListener(new MouseAdapter() {@Override
        public void mouseClicked(MouseEvent e)
        {

            int fila = tableInfo.rowAtPoint(e.getPoint());
            int columna = tableInfo.columnAtPoint(e.getPoint());
            if ((fila > -1) && (columna > -1))
            {
                //System.out.println(tableInfo.getValueAt(fila,columna).toString() + " "+fila+"  "+columna);

                ProductoActual = fila;
                nuevaGrafica(fila);

            }
        }});
        

        dataset.addSeries(encima);
        dataset.addSeries(debajo);
        dataset.addSeries(referencia);
        dataset.addSeries(actual);

        final JFreeChart chart = createChart(dataset);

        final ChartPanel chartPanel = new ChartPanel(chart);

        this.getContentPane().setLayout(new GridLayout(2,0));

        JPanel otro = new JPanel();
        otro.setLayout(new GridLayout(0,2));

        otro.add(TablaInformacion);
        otro.add(chartPanel);

        this.getContentPane().add(otro);
         this.getContentPane().add(TablaResultados);

        this.validate();


    }

    private void nuevaGrafica(int fila)
    {
        encima.clear();
        debajo.clear();
        referencia.clear();
        actual.clear();

        try{
            for(int i=0;i<historicoReferencia.get(fila).size();i++)
            {
                 encima.add(historicoHora.get(fila).get(i),historicoEncima.get(fila).get(i) );
                 debajo.add(historicoHora.get(fila).get(i),historicoDebajo.get(fila).get(i) );
                 referencia.add(historicoHora.get(fila).get(i),historicoReferencia.get(fila).get(i) );


            }

            for(int i=0;i<historicoPrecios.get(fila).size();i++)
            actual.add(historicoHoraPrecioActual.get(fila).get(i),historicoPrecios.get(fila).get(i) );

            for(int i=0; i<historicoHoraEtiqueta.size();i++)
            {
                 Marker currentEnd = new ValueMarker(historicoHoraEtiqueta.get(i));
                 currentEnd.setPaint(Color.black);
                 currentEnd.setLabel(historicoTextoEtiqueta.get(i));
                 currentEnd.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
                 currentEnd.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
                 plot.addDomainMarker(currentEnd);
            }
        }catch(org.jfree.data.general.SeriesException ce) {
                    //ce.printStackTrace();
                System.out.println("Error en dato duplicado para la serie del libro de ordenes");
                }



        this.validate();

    }



     public void serie(float pEncima, float pDebajo, float pReferencia, float pActual, int Accion)
    {

         /*horaActual = Calendar.getInstance();
         horaNow = new Date(horaActual.get(Calendar.YEAR),horaActual.get(Calendar.MONTH),horaActual.get(Calendar.DATE),horaActual.get(Calendar.HOUR_OF_DAY),horaActual.get(Calendar.MINUTE),horaActual.get(Calendar.SECOND));

          tiempos = new Date((horaNow.getTime()-horaInicial.getTime())*(ajuste/tiempoSim)+horaInicial.getTime());*/

         tiempos = new Date(((new Millisecond().getFirstMillisecond()-inicialA.getFirstMillisecond()))*(ajuste/tiempoSim)+inicial.getFirstMillisecond());
          try
            {
             //System.out.println(Accion + "   ------   "+ProductoActual);

             if(ProductoActual == Accion)
             {
                 encima.add(new Millisecond(tiempos),pEncima );
                 debajo.add(new Millisecond(tiempos),pDebajo );
                 referencia.add(new Millisecond(tiempos),pReferencia );
                 if(pActual != (-1))
                 actual.add(new Millisecond(tiempos),pActual );
             }

             if(pActual != (-1))
             {
                historicoPrecios.get(Accion).add(pActual);
                historicoHoraPrecioActual.get(Accion).add(new Millisecond(tiempos));
             }
             
            historicoEncima.get(Accion).add(pEncima);
            historicoDebajo.get(Accion).add(pDebajo);
            historicoReferencia.get(Accion).add(pReferencia);
            historicoHora.get(Accion).add(new Millisecond(tiempos));

        }
       catch( org.jfree.data.general.SeriesException oe)
       {
            System.out.println("----------------Dato duplicado en el grafico de calces---------------");
       }

    }

    public void agregarEtiquetaDia(String fecha)
    {
        
          tiempos = new Date(((new Millisecond().getFirstMillisecond()-inicialA.getFirstMillisecond()))*(ajuste/tiempoSim)+inicial.getFirstMillisecond());
        double milies = (new Millisecond(tiempos)).getFirstMillisecond();
        Marker currentEnd = new ValueMarker(milies);
        currentEnd.setPaint(Color.black);
        currentEnd.setLabel(fecha);
        currentEnd.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
        currentEnd.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
        plot.addDomainMarker(currentEnd);

        historicoTextoEtiqueta.add(fecha);
        historicoHoraEtiqueta.add(milies);

    }

    private JFreeChart createChart(final XYDataset dataset) {
        final JFreeChart chart = ChartFactory.createTimeSeriesChart(
            "Trading Channel",
            "Date",
            "",
            dataset,
            true,
            true,
            true
        );

        chart.setBackgroundPaint(Color.white);

        //XYPlot plot = (XYPlot) chart.getPlot();

        plot = chart.getXYPlot();

        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);


        PeriodAxis domainAxis = new PeriodAxis("Date");
        domainAxis.setTimeZone(TimeZone.getTimeZone("Pacific/Auckland"));
        domainAxis.setAutoRangeTimePeriodClass(Second.class);
        PeriodAxisLabelInfo[] info = new PeriodAxisLabelInfo[2];
       // info[0] = new PeriodAxisLabelInfo(Second.class, new SimpleDateFormat("s"));
        //info[0] = new PeriodAxisLabelInfo(Minute.class, new SimpleDateFormat("m"));
        info[0] = new PeriodAxisLabelInfo(Hour.class, new SimpleDateFormat("h:mm:s a"),
                new RectangleInsets(2, 2, 2, 2), new Font("SansSerif", Font.BOLD, 10), Color.blue,
                false, new BasicStroke(0.0f), Color.lightGray);
        info[1] = new PeriodAxisLabelInfo(Day.class, new SimpleDateFormat("d"));
        domainAxis.setLabelInfo(info);
        plot.setDomainAxis(domainAxis);

       final XYItemRenderer renderer1 = plot.getRenderer();

        renderer1.setSeriesPaint(0,  Color.red);
        renderer1.setSeriesPaint(1, Color.red);
        renderer1.setSeriesPaint(2, Color.blue);
        renderer1.setSeriesPaint(3, Color.BLACK);
        
       // renderer1.setSeriesPaint(3, new Color(0.72f,0f,0.84f));

       agregarEtiquetaDia("Day - 1");
        
        plot.setRenderer(renderer1);
        return chart;
    }


     class HiloGraficaRunTime extends java.lang.Thread
    {
        public HiloGraficaRunTime()
        {
            this.start();
        }

        @Override
        public void run()
        {

            try {

                for(int i=1;i<30;i++)
                {
                    System.out.println("Esperando 0.5 segundos");
 

                    HiloGraficaRunTime.sleep(500);

                   // System.out.println("Pasaron 1 segundos");
//                     series.add(new Millisecond(),i );
//                     series2.add(new Millisecond(),10-i );
                    //serie(10,2,6,aleatorio(10,2),0);

          //tiempos = new Date((new Millisecond().getFirstMillisecond()-inicial.getFirstMillisecond())*(ajuste/tiempoSim));

          //tiempos = new Date(((new Millisecond().getFirstMillisecond()-inicialA.getFirstMillisecond()))*(ajuste/tiempoSim)+inicial.getFirstMillisecond());

          //System.out.println(tiempos);
          //System.out.println((new Millisecond().getFirstMillisecond()-inicial.getFirstMillisecond())*(ajuste/tiempoSim));
         // System.out.println(ajuste/tiempoSim);
                    //System.out.println((new Millisecond()).getFirstMillisecond()-inicial.getFirstMillisecond());
                    
                    //System.out.println((new Date().getTime())-tiempos.getTime());
               }


           }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }





        }
    }

     public class CalcesEnSimulacion extends JFrame {


        public CalcesEnSimulacion()
        {
            final TimeSeriesCollection datos = new TimeSeriesCollection();
            datos.addSeries(encima);
            datos.addSeries(debajo);
            datos.addSeries(referencia);
            datos.addSeries(actual);

        final JFreeChart chart = createChart(dataset);
            final ChartPanel panel = new ChartPanel(chart, true, true, true, false, true);
            panel.setPreferredSize(new java.awt.Dimension(500, 270));
            setContentPane(panel);

        }
     }







    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setTitle("Stock Market Information");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 786, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 725, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Libro2(1,15000, 86400000).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

        private float aleatorio(float limiteSuperior, float limiteInferior)
    {
        return (objeto.nextFloat())*(limiteSuperior-limiteInferior)+limiteInferior;
    }
}


