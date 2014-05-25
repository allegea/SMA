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
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;

/**
 *
 * @author Administrador
 */
public class ResumenLiquidez extends javax.swing.JFrame {
    ConexionBD BD;
    int iteraciones = -1;
    int identificador;


    int productos = 0;
    ArrayList<String> nombres = new ArrayList<String>();
    
    ArrayList<ArrayList<Double>> minimos = new ArrayList<ArrayList<Double>>();
    ArrayList<ArrayList<Double>> maximos = new ArrayList<ArrayList<Double>>();
    ArrayList<ArrayList<Double>> avgs = new ArrayList<ArrayList<Double>>();
    ArrayList<ArrayList<Double>> desvs = new ArrayList<ArrayList<Double>>();

    DecimalFormat formateador = new DecimalFormat("########.####");
    /** Creates new form Resumen */
    public ResumenLiquidez() {
        initComponents();
        this.setTitle("Overview of liquidity");


         try{
         BD = new ConexionBD("mysql");
         ResultSet info = BD.consulta("Select max(repeticion) from cotizacion");
             if(info.next())
                 iteraciones = info.getInt(1);


         info = BD.consulta("Select idproducto, nombre from producto");
         while(info.next())
             {nombres.add(info.getString(2));
             productos++;
             }

             nombres.add("Market");



         BD.cerrarConexion();
         }
        catch (SQLException ex) {
                    ex.printStackTrace();
                }


         for(int i=0;i<=productos;i++)
        {
            minimos.add(i, new ArrayList<Double>());
            maximos.add(i, new ArrayList<Double>());
            avgs.add(i, new ArrayList<Double>());
            desvs.add(i, new ArrayList<Double>());

        }
        datos();
        resumen();



    }



    private void datos()
    {

        ConexionBD BD = new ConexionBD("mysql");
        ResultSet id = null;
        int fechas = -1;


        Texto.append("\n\t\tOVERVIEW OF LIQUIDITY");
        Texto.append("\n------------------------------------------------------------------------------------------------------------------------------\n");
        Texto.append("Iteration\tProduct\tMinimum\tMaximun\tAverage\tDesviation\n");

        try {

                    id = BD.consulta("Select max(fecha) from cotizacion");

                    if (id.next()) fechas = id.getInt(1);




                    double[][][] liquidez = new double[iteraciones][productos+1][fechas+1];
                    double[][] avg = new double[iteraciones][productos+1];
                    double[][] desv = new double[iteraciones][productos+1];
                    double[][] min = new double[iteraciones][productos+1];
                    double[][] max = new double[iteraciones][productos+1];
                    double[][] count = new double[iteraciones][productos+1];


                    for(int j=0;j<iteraciones;j++)
                    for(int i=0;i<=productos;i++)
                        Arrays.fill(liquidez[j][i], -1);

                    for(int j=1;j<=iteraciones;j++)
                    {
                        for(int i=0;i<productos;i++)
                        {

                            ResultSet r = BD.consulta("SELECT c.fecha, c.idcalce, c.idofertacompra, c.idofertaventa, oc.preciocompra, ov.precioventa, c.preciocompra as precio, avg(((ov.precioventa - oc.preciocompra)/((ov.precioventa + oc.preciocompra)/2))) FROM calce c, ofertacompra oc, ofertaventa ov where c.idofertacompra = oc.idofertacompra and c.idofertaventa = ov.idofertaventa and c.repeticion = "+j+" and ov.repeticion= "+j+" and oc.repeticion = "+j+" and c.idproducto = "+i+" and ov.idproducto = "+i+" and oc.idproducto = "+i+" group by fecha");

                            while(r.next()) liquidez[j-1][i][r.getInt(1)] = Math.abs(r.getDouble(8))*100;

                        }
                    }

                    for(int h=0;h<iteraciones;h++)
                    {
                        Arrays.fill(count[h], 0.0);
                        Arrays.fill(avg[h], 0.0);
                        Arrays.fill(min[h], 8.0);
                        Arrays.fill(max[h], 0.0);

                        ///Calcular max, min y liquidez para el mercado
                        for(int j=1;j<=fechas;j++)
                        {
                            int sumados=0;
                            double liquidM = 0;
                            for(int i=0;i<productos;i++)
                            {
                                if(liquidez[h][i][j]!=-1)
                                {
                                    liquidM+=liquidez[h][i][j];
                                    avg[h][i]+=liquidez[h][i][j];
                                    count[h][i]++;
                                    min[h][i] = min[h][i] > liquidez[h][i][j] ? liquidez[h][i][j] : min[h][i];
                                    max[h][i] = max[h][i] < liquidez[h][i][j] ? liquidez[h][i][j] : max[h][i];
                                    sumados++;
                                }

                            }
                            if(sumados>0)
                            {
                               liquidez[h][productos][j] = liquidM/sumados;
                               count[h][productos]++;
                               avg[h][productos]+=liquidez[h][productos][j];
                                    min[h][productos] = min[h][productos] > liquidez[h][productos][j] ? liquidez[h][productos][j] : min[h][productos];
                                    max[h][productos] = max[h][productos] < liquidez[h][productos][j] ? liquidez[h][productos][j] : max[h][productos];
                            }

                        }

                        for(int i=0;i<=productos;i++)
                                avg[h][i]/=count[h][i];

                        ///Calcular la Desviaci�n de la liquidez tanto por acci�n como para el mercado
                         for(int j=1;j<=fechas;j++)
                        {
                            for(int i=0;i<=productos;i++)
                            {
                                if(liquidez[h][i][j]!=-1)
                                {
                                    desv[h][i] += Math.pow((liquidez[h][i][j] - avg[h][i]),2);
                                }
                            }
                        }

 
                          for(int i=0;i<=productos;i++)
                                desv[h][i]=Math.sqrt(desv[h][i]/(count[h][i]-1));
    

                        /////////////////////////////
                         // Texto.append("------------------------------------------------------------------------------------------------------------------------------\n");

                    }
                    
                      //Texto.append(""+(h+1));
                    if(productos==1)productos--;
                        for(int i=0;i<=productos;i++)
                            {
                                for(int h=0;h<iteraciones;h++)
                                {
                                    Texto.append(""+(h+1));
                                    Texto.append("\t"+nombres.get(i)+"\t"+formateador.format(min[h][i])+"\t"+formateador.format(max[h][i])+"\t"+formateador.format(avg[h][i])+"\t"+formateador.format(desv[h][i])+"\n");
                                    minimos.get(i).add(min[h][i]);
                                    maximos.get(i).add(max[h][i]);
                                    avgs.get(i).add(avg[h][i]);
                                    desvs.get(i).add(desv[h][i]);
                                }
                            }




        } catch (SQLException ex) {

        }

        BD.cerrarConexion();
    }
    
    private void resumen() {
        
        Texto.append("------------------------------------------------------------------------------------------------------------------------------\n");
         Texto.append("Product\tStatistical\tMinimum\tMaximum\tAverage\tDesviation\n");
        
         double min;
         double max;
         double avg;
         double des;
         
         for(int i=0;i<=productos;i++)
        {
            
            Texto.append(nombres.get(i));
             
            min = Collections.min(minimos.get(i));
            max = Collections.max(minimos.get(i));
            avg = promedio(minimos.get(i));
            des = desviacion(minimos.get(i),avg);
            Texto.append("\tMinimum\t"+formateador.format(min)+"\t"+formateador.format(max)+"\t"+formateador.format(avg)+"\t"+formateador.format(des)+"\n");
            
            min = Collections.min(maximos.get(i));
            max = Collections.max(maximos.get(i));
            avg = promedio(maximos.get(i));
            des = desviacion(maximos.get(i),avg);
            Texto.append("\tMaximum\t"+formateador.format(min)+"\t"+formateador.format(max)+"\t"+formateador.format(avg)+"\t"+formateador.format(des)+"\n");
            
            min = Collections.min(avgs.get(i));
            max = Collections.max(avgs.get(i));
            avg = promedio(avgs.get(i));
            des = desviacion(avgs.get(i),avg);
            Texto.append("\tAverage\t"+formateador.format(min)+"\t"+formateador.format(max)+"\t"+formateador.format(avg)+"\t"+formateador.format(des)+"\n");
            
            min = Collections.min(desvs.get(i));
            max = Collections.max(desvs.get(i));
            avg = promedio(desvs.get(i));
            des = desviacion(desvs.get(i),avg);
            Texto.append("\tDesviation\t"+formateador.format(min)+"\t"+formateador.format(max)+"\t"+formateador.format(avg)+"\t"+formateador.format(des)+"\n");
            

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
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 601, Short.MAX_VALUE)
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
                new ResumenLiquidez().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea Texto;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables

}
