/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Resumen.java
 *
 * Created on 30/09/2009, 10:04:06 PM
 */

package graficos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import basedatos.ConexionBD;
import java.text.DecimalFormat;
import java.util.Collections;

/**
 *
 * @author Administrador
 */
public class ResumenStylized extends javax.swing.JFrame {
    ConexionBD BD;
    int iteraciones = -1;
    String accion;
    int identificador;
    ArrayList<ArrayList<Float>> prices = new ArrayList<ArrayList<Float>>();
    ArrayList<ArrayList<Double>> retornos = new ArrayList<ArrayList<Double>>();
    ArrayList<ArrayList<Double>> retornosS = new ArrayList<ArrayList<Double>>();
    ArrayList<ArrayList<Double>> retornosA = new ArrayList<ArrayList<Double>>();
    ArrayList<ArrayList<Double>> volumen = new ArrayList<ArrayList<Double>>();

    ArrayList<Double> promedios = new ArrayList<Double>();
    ArrayList<Double> desviaciones = new ArrayList<Double>();
    ArrayList<Double> kurtosis = new ArrayList<Double>();
    ArrayList<Double> skewness = new ArrayList<Double>();




    DecimalFormat formateador = new DecimalFormat("########.##");
    /** Creates new form Resumen */
    public ResumenStylized(String nombre) {
        initComponents();
        this.setTitle(this.getTitle()+" of "+nombre);
        accion = nombre;

         try{
         BD = new ConexionBD("mysql");
         ResultSet info = BD.consulta("Select max(repeticion) from cotizacion");
             if(info.next())
                 iteraciones = info.getInt(1);


         info = BD.consulta("Select idproducto, nombre from producto where nombre = '"+accion+"'");
         if(info.next())
             identificador = info.getInt(1);



         BD.cerrarConexion();
         }
        catch (SQLException ex) {
                    ex.printStackTrace();
                }

        System.out.println(iteraciones);

        for(int i=0;i<=iteraciones;i++)
        {
            prices.add(i, new ArrayList<Float>());
            retornos.add(i, new ArrayList<Double>());
            retornosS.add(i, new ArrayList<Double>());
            retornosA.add(i, new ArrayList<Double>());
            volumen.add(i, new ArrayList<Double>());

        }

        informacion_general();
        Distribution();
        resumen_distribucion();
        autocorrelations();



    }

    private void informacion_general()
    {
         Texto.append("\t\t\tSUMMARY");
         Texto.append("\n-------------------------------------------------------------------------------------------------------------\n\n");
    }

    private void Distribution()
    {
        ResultSet info = null;
        float precio_t_1=0;
        float precio_t = 0;

        BD = new ConexionBD("mysql");

        for(int i=0;i<=iteraciones;i++)
        {

            int indice =0;

         info = BD.consulta("SELECT precioCierre, cantidad  FROM cotizacion WHERE repeticion = "+i+" AND IDProducto = "+identificador+" GROUP BY fecha");

        //ResultSet r = BD.consulta("SELECT fecha, precioPromedio, count(fecha) as volumen FROM cotizacion WHERE IDProducto = "+IdProducto+" GROUP BY fecha");
        try {
         while(info.next())
                {

                    precio_t_1 = precio_t;
                    precio_t = info.getFloat(1);
                    double volum = precio_t*info.getInt(2);

                    prices.get(i).add(precio_t);
                    volumen.get(i).add(volum);

                   if(indice !=0)
                   {
                       double retorno =  Math.log( precio_t/precio_t_1);
                       retornos.get(i).add(retorno);
                       retornosS.get(i).add(Math.pow(retorno, 2));
                       retornosA.get(i).add(Math.abs(retorno));
                    }

                   indice++;

                }

                if(i==0)
                {
                    Texto.append("\n-------------------------------------------------------------------------------------------------------------\n");
                    Texto.append("\t\tHeavy Tails");
                    Texto.append("\n-------------------------------------------------------------------------------------------------------------\n\n");
                    Texto.append("\nIteration\tAverage\tDesviation\tKurtosis\tSkewness\n");
                }

                if(!retornos.isEmpty())
                {
                    Texto.append(i + "\t");
                    distribucion(retornos.get(i),i);
                }

                //Retornos.autoCorrelation(null,1);



            }
        catch (SQLException ex) {
                    ex.printStackTrace();
                }



        }
        BD.cerrarConexion();


    }

    private void autocorrelations()
    {
        int lags = 21;
        final DecimalFormat format = new DecimalFormat("##.####");
        ArrayList<Double> autocorr = new ArrayList<Double>();
        Texto.append("\n\n\n-------------------------------------------------------------------------------------------------------------\n");
        Texto.append("\t\tAUTOCORRELATION");
        Texto.append("\n-------------------------------------------------------------------------------------------------------------\n\n");
        Texto.append("\t\tPrices");


        Texto.append("\nIteration\t\tLag\n");
        for(int i=0;i<lags;i++)
            Texto.append("\t"+i);

        for(int i=0;i<=iteraciones;i++)
        {
            Texto.append("\n"+i+"");
            autocorr = Retornos.autoCorrelation(prices.get(i), lags);
            for(int j=0;j<autocorr.size();j++)
                Texto.append("\t"+format.format(autocorr.get(j)));
        }

        ////////////////////////////////////////////////////////////////////

        Texto.append("\n\n\t\tReturn");


        Texto.append("\nIteration\t\tLag\n");
        for(int i=0;i<lags;i++)
            Texto.append("\t"+i);

        String x="";
        for(int i=0;i<=iteraciones;i++)
        {   //x+="\nIteration "+i+"\n";
            Texto.append("\n"+i+"");
            autocorr = Retornos.autoCorrelation(retornos.get(i), lags);
            double limits = Math.sqrt(retornos.get(i).size());
            for(int j=0;j<autocorr.size();j++)
            {
                Texto.append("\t"+format.format(autocorr.get(j)));
               // if(Math.abs(autocorr.get(j)*limits)>=2)x+="  Lag "+j+" - "+format.format(autocorr.get(j))+"\n\t";
            }
        }

        ////////////////////////////////////////////////////////////////////

        //Texto.append(x);

        Texto.append("\n\n\t\tSquared Return");


        x="";
        Texto.append("\nIteration\t\tLag\n");
        for(int i=0;i<lags;i++)
            Texto.append("\t"+i);

        for(int i=0;i<=iteraciones;i++)
        {
            Texto.append("\n"+i+"");
            autocorr = Retornos.autoCorrelation(retornosS.get(i), lags);
            double limits = Math.sqrt(retornosS.get(i).size());
            for(int j=0;j<autocorr.size();j++)
            {
                Texto.append("\t"+format.format(autocorr.get(j)));
                //if(Math.abs(autocorr.get(j)*limits)>=2)x+="  Lag "+j+" - "+format.format(autocorr.get(j))+"\n\t";
            }
        }

        ////////////////////////////////////////////////////////////////////

        //Texto.append(x);

        ////////////////////////////////////////////////////////////////////

        Texto.append("\n\n\t\tAbsolute Value Return");


        x="";
        Texto.append("\nIteration\t\tLag\n");
        for(int i=0;i<lags;i++)
            Texto.append("\t"+i);

        for(int i=0;i<=iteraciones;i++)
        {
            Texto.append("\n"+i+"");
            autocorr = Retornos.autoCorrelation(retornosA.get(i), lags);
            double limits = Math.sqrt(retornosA.get(i).size());
            for(int j=0;j<autocorr.size();j++)
            {
                Texto.append("\t"+format.format(autocorr.get(j)));
                //if(Math.abs(autocorr.get(j)*limits)>=2)x+="  Lag "+j+" - "+format.format(autocorr.get(j))+"\n\t";
            }
        }

        ////////////////////////////////////////////////////////////////////

        //Texto.append(x);
        
        ////////////////////////////////////////////////////////////////////

        Texto.append("\n\n\t\tVolume");


        Texto.append("\nIteration\t\tLag\n");
        for(int i=0;i<lags;i++)
            Texto.append("\t"+i);

        for(int i=0;i<=iteraciones;i++)
        {
            Texto.append("\n"+i+"");
            autocorr = Retornos.autoCorrelation(volumen.get(i), lags);
            for(int j=0;j<autocorr.size();j++)
                Texto.append("\t"+format.format(autocorr.get(j)));
        }


    }

    private void distribucion( ArrayList<Double> retornos, int curr)
    {
                double sumatoria = 0;
                // Sumatoria del precio promedio en todas los periodos anteriores de la accion i
                for (int w=0; w<retornos.size(); w++) {
                    sumatoria += retornos.get(w);
                }
                // Promedio del precio promedio en todas las fechas de la accion i
                double promedio = sumatoria/retornos.size();
                if(curr!=0)
                promedios.add(promedio);
                
                sumatoria = 0;
                for (int w=0; w<retornos.size(); w++) {
                    sumatoria += Math.pow((retornos.get(w) - promedio),2);
                }

                sumatoria = Math.sqrt(sumatoria/(retornos.size()-1));
                if(curr!=0)
                desviaciones.add(sumatoria);
                
                final DecimalFormat format = new DecimalFormat("###.####");
                           //System.out.println(format.format(Math.log( precio_t/precio_t_1)));
                //System.out.println(format.format(promedio)+" P");
                Texto.append(format.format(promedio)+"\t");
                //System.out.println(format.format(sumatoria)+" D");
                Texto.append(format.format(sumatoria)+"\t");

                double kurtosisV = 0;
                for (int w=0; w<retornos.size(); w++)
                    kurtosisV += Math.pow((retornos.get(w) - promedio),4);


                kurtosisV = kurtosisV/((retornos.size()-1)*Math.pow(sumatoria, 4));
                if(curr!=0)
                kurtosis.add(kurtosisV);
                //System.out.println(format.format(kurtosis)+" K");
                Texto.append(format.format(kurtosisV)+"\t");

                double skewnessV = 0;
                for (int w=0; w<retornos.size(); w++) {
                    skewnessV += Math.pow((retornos.get(w) - promedio),3);
                }

                skewnessV = skewnessV/((retornos.size()-1)*Math.pow(sumatoria, 3));
                if(curr!=0)
                skewness.add(skewnessV);
                //System.out.println(format.format(skewness)+" S");
                Texto.append(format.format(skewnessV)+"\n");
    }
    
    private void resumen_distribucion() {
        final DecimalFormat format = new DecimalFormat("###.####");
        Texto.append("-------------------------------------------------------------------------------------------------------------\n");
         Texto.append("\tMinimum\tMaximum\tAverage\tDesviation\n");
        
         if(promedios.size()!=0){       
         double min = Collections.min(promedios);
         double max = Collections.max(promedios);
         double avg = promedio(promedios);
         double des = desviacion(promedios,avg);
         
         Texto.append("Average\t"+format.format(min)+"\t"+format.format(max)+"\t"+format.format(avg)+"\t"+format.format(des)+"\n");
         
         min = Collections.min(desviaciones);
         max = Collections.max(desviaciones);
         avg = promedio(desviaciones);
         des = desviacion(desviaciones,avg);
         
         Texto.append("Desviation\t"+format.format(min)+"\t"+format.format(max)+"\t"+format.format(avg)+"\t"+format.format(des)+"\n");
         
         min = Collections.min(kurtosis);
         max = Collections.max(kurtosis);
         avg = promedio(kurtosis);
         des = desviacion(kurtosis,avg);
         
         Texto.append("Kurtosis\t"+format.format(min)+"\t"+format.format(max)+"\t"+format.format(avg)+"\t"+format.format(des)+"\n");
         
         min = Collections.min(skewness);
         max = Collections.max(skewness);
         avg = promedio(skewness);
         des = desviacion(skewness,avg);
         
         Texto.append("Skewness\t"+format.format(min)+"\t"+format.format(max)+"\t"+format.format(avg)+"\t"+format.format(des)+"\n");
         }

    }



   public double promedio(ArrayList<Double> x)
    {
        double sumatoria = 0;
                // Sumatoria del precio promedio en todas los periodos anteriores de la accion i
                for (int w=0; w<x.size(); w++) {
                    sumatoria += x.get(w);
                }
                // Promedio del precio promedio en todas las fechas de la accion i
        double promedio = sumatoria/x.size();

        return promedio;
    }

    public double desviacion(ArrayList<Double> x, double media)
    {
                double sumatoria = 0;
                for (int w=0; w<x.size(); w++) {
                    sumatoria += Math.pow((x.get(w) - media),2);
                }

                sumatoria = Math.sqrt(sumatoria/(x.size()-1));
                
                return sumatoria;
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        Texto = new javax.swing.JTextArea();

        setTitle("Stylized Facts Summary");

        Texto.setColumns(20);
        Texto.setEditable(false);
        Texto.setRows(5);
        jScrollPane2.setViewportView(Texto);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 775, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 654, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ResumenStylized("PREC").setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea Texto;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables

    

}
