/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Resumen.java
 *
 * Created on 30/09/2009, 10:04:06 PM
 */

package interfaz;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import basedatos.ConexionBD;
import java.text.DecimalFormat;

/**
 *
 * @author Administrador
 */
public class Resumen extends javax.swing.JFrame {
    ConexionBD BD;
    ConexionBD BDA;
    ConexionBD BDcantidadAgentes;
    ConexionBD BDnombres;
    ConexionBD BDcantidadAcciones;
    ConexionBD BDprecio;
    ConexionBD BDcantidad;
     ConexionBD BDcantidad2;
     ConexionBD BDpromedio;
     ConexionBD BDofertas;
    ConexionBD BDCompra;
    ConexionBD BDVenta;
    ConexionBD BDCantidad;

    ArrayList<Double> Volatilidades = new ArrayList<Double>();
    ArrayList<Integer> transaccionesA = new ArrayList<Integer>();
    ArrayList<Integer> calcesA = new ArrayList<Integer>();

    DecimalFormat formateador = new DecimalFormat("########.##");
    int repeticion=-1;
    /** Creates new form Resumen */
    public Resumen(int rep) {
        initComponents();
                repeticion =rep;
                informacion_general();
               agentes_rentabilidad();
               acciones_precios();
               acciones_rentabilidad();
               
               


    }

    private void informacion_general()
    {
         Texto.append("\t\t\tGeneral");
         Texto.append("\n-------------------------------------------------------------------------------------------------------\n");
         try{
         BD = new ConexionBD("mysql");
         ResultSet info = BD.consulta("SELECT max(fecha) FROM cotizacion c where repeticion = "+repeticion);
         if(info.next())
         Texto.append("Amount of dates simulated\t\t"+info.getInt(1));

         info = BD.consulta("SELECT count(idproducto) FROM producto p");
         if(info.next())
         Texto.append("\nAmount of stocks for negotiation\t"+info.getInt(1));

         info = BD.consulta("SELECT count(idagente) FROM bursatil b");
         if(info.next())
         Texto.append("\nAmount of agents\t\t"+info.getInt(1)+"\n");

         BD.cerrarConexion();
                             }
        catch (SQLException ex) {
                    ex.printStackTrace();
                }
        cantidad_agentes();


    }

    private void cantidad_agentes()
    {
        String[] indicadores = {"PDMCorto","PMSimple","PMTCorto","MACD","Momento","ROC","RSI","VHF"};
        String[] nombreIndicador = {"Promedios Moviles Simples","Promedios Moviles Dobles","Promedios Moviles Triples","MACD","Momento","ROC","RSI","VHF"};
        BD = new ConexionBD("mysql");
        BDA = new ConexionBD("mysql");
        ResultSet agentes = BD.consulta("SELECT count(*), tipo FROM bursatil b group by tipo");
        ResultSet indicadoresC = null;
        int cantidad = 0;
       try{
        while(agentes.next())
        {
            switch(agentes.getInt(2))
            {
                    case(0): Texto.append("\n\tFuzzy agents\t"+agentes.getInt(1)); break;
                    case(1): 
                        
                        Texto.append("\n\tTechnical agents\t"+agentes.getInt(1));
                        for(int w = 0; w < 8; w++)
                        {
                            cantidad = 0;
                            indicadoresC = BDA.consulta("SELECT * FROM analisistecnico a where indicador = '"+indicadores[w]+"' and repeticion = "+repeticion+" group by idagente;");
                            while(indicadoresC.next())
                                cantidad++;
                            if(cantidad!=0)
                            {
                                if(w < 3)
                                Texto.append("\n\t\t"+nombreIndicador[w]+"\t"+cantidad);
                                else
                                Texto.append("\n\t\t"+nombreIndicador[w]+"\t\t"+cantidad);

                            }
                                

                        }


                    break;
                    case(2): Texto.append("\n\tZero intelligence agents\t"+agentes.getInt(1)); break;
            }

        }

                    }
        catch (SQLException ex) {
                    ex.printStackTrace();
                }
        BD.cerrarConexion();
        BDA.cerrarConexion();



    }
    private void agentes_rentabilidad()
    {
        ArrayList<Float> f = new ArrayList<Float>();

             float precioFinal = 0;
        float precioInicial = 0;
        double SumUtilidadesInicial = 0;
        double SumUtilidadesFinal = 0;

        BD = new ConexionBD("mysql");
        BDA = new ConexionBD("mysql");
        BDcantidadAgentes = new ConexionBD("mysql");
        BDnombres = new ConexionBD("mysql");
        BDcantidadAcciones = new ConexionBD("mysql");
        BDprecio = new ConexionBD("mysql");
        BDcantidad = new ConexionBD("mysql");
        BDcantidad2 = new ConexionBD("mysql");
        ResultSet SaldoInicial = null;
        ResultSet Acciones = null;
        ResultSet SaldoFinal = null;
        ResultSet nombre = null;
        ResultSet precio = null;
        ResultSet cantidad = null;
        ResultSet IDagente = null;

        float[] valores = null;
        float[] proCalces = null;
        float[] proTran = null;

        try {

            //*****************************************************************************

            //*****************************************************************************
            
            for(int i = 0 ; i < 3; i++)
            {
                IDagente = BDcantidadAgentes.consulta("SELECT * FROM bursatil where tipo = "+i+" order by nombre");

                 while(IDagente.next())
                 {
                    SumUtilidadesInicial = 0;
                    SumUtilidadesFinal = 0;
                    SaldoInicial = BD.consulta("SELECT IDAgente, saldo FROM saldoXagente WHERE repeticion = "+repeticion+" and fecha=(SELECT min(fecha) FROM saldoXagente WHERE IDAgente =  "+IDagente.getInt(1)+"  AND repeticion = "+repeticion+") AND IDAgente =  "+IDagente.getInt(1));
                    SaldoFinal = BDA.consulta("SELECT IDAgente, saldo FROM saldoXagente WHERE repeticion = "+repeticion+" and fecha=(SELECT max(fecha) FROM saldoXagente WHERE IDAgente =  "+IDagente.getInt(1)+" AND repeticion = "+repeticion+") AND IDAgente =  "+IDagente.getInt(1));
                    nombre = BDnombres.consulta("SELECT nombre FROM bursatil WHERE IDAgente = "+IDagente.getInt(1));

                  //  System.out.println("ID agente es "+IDagente.getInt(1));

                     if(SaldoInicial.next() && SaldoFinal.next() && nombre.next())
                            {

                                 Acciones = BDcantidadAcciones.consulta("SELECT IDProducto FROM accionesxagente WHERE repeticion = "+repeticion+" AND fecha = (SELECT min(fecha) FROM accionesxagente WHERE IDAgente = "+IDagente.getInt(1)+" AND repeticion = "+repeticion+") AND IDAgente = "+IDagente.getInt(1));

                                 while(Acciones.next())
                                 {
                                        precio = BDprecio.consulta("SELECT precioCierre, fecha FROM cotizacion WHERE repeticion = "+repeticion+" and IDproducto = "+Acciones.getInt(1)+" AND fecha <= 0 AND precioCierre is not NULL GROUP BY fecha ORDER BY fecha DESC");
                                        cantidad = BDcantidad.consulta("SELECT IDAgente, cantidad, fecha FROM accionesxagente WHERE repeticion = "+repeticion+" and IDAgente = "+IDagente.getInt(1)+" AND IDProducto = "+Acciones.getInt(1)+" AND fecha = 0 GROUP BY fecha,IDAgente");

                                        if(precio.next() && cantidad.next())
                                        {
                                           //System.out.println("Uti "+SumUtilidadesInicial+" precio "+precio.getDouble(1)+" canti "+cantidad.getDouble(2));
                                            SumUtilidadesInicial = (long) (SumUtilidadesInicial + precio.getDouble(1) * cantidad.getDouble(2));

                                        }

                                        precio = BDprecio.consulta("SELECT precioCierre, fecha FROM cotizacion WHERE repeticion = "+repeticion+" AND IDproducto = "+Acciones.getInt(1)+" AND fecha <= (SELECT max(fecha) FROM cotizacion where repeticion = "+repeticion+") AND precioCierre is not NULL GROUP BY fecha ORDER BY fecha DESC");
                                        cantidad = BDcantidad2.consulta("SELECT IDAgente, cantidad, fecha FROM accionesxagente WHERE repeticion = "+repeticion+" AND IDAgente = "+IDagente.getInt(1)+" AND IDProducto = "+Acciones.getInt(1)+" AND fecha <= (Select max(fecha) from accionesxagente Where IDAgente = "+IDagente.getInt(1)+" AND repeticion = "+repeticion+") GROUP BY fecha,IDAgente order by fecha desc");


                                        if(precio.next() && cantidad.next())
                                        {
                                            SumUtilidadesFinal = (long) (SumUtilidadesFinal + precio.getDouble(1) * cantidad.getDouble(2));
                                            //System.out.println("UtiFin "+SumUtilidadesInicial+" precio "+precio.getDouble(1)+" canti "+cantidad.getDouble(2));

                                        }


                                 }
                                        //System.out.println("saldo es es "+SaldoInicial.getFloat(2));
                                       // System.out.println("saldo final es "+SaldoFinal.getFloat(2));
                                        precioInicial = (long) (SaldoInicial.getFloat(2) + (float) SumUtilidadesInicial);
                                        //System.out.println("SalINi "+SaldoInicial.getFloat(2)+" INICIAL "+precioInicial);
                                        precioFinal = (long) (SaldoFinal.getFloat(2) + (float) SumUtilidadesFinal);
                                        // System.out.println("SalFIN "+SaldoFinal.getFloat(2)+" FINAL "+precioFinal);
                                        if(precioInicial != 0.0 && precioFinal !=0)
                                        {
                                           //System.out.println(nombre.getString(1)+" la rentabilidad =  "+(precioFinal/precioInicial-1)*100);
                                           //Texto.append(nombre.getString(1)+" la rentabilidad =  "+(precioFinal/precioInicial-1)*100+"\n");
                                           f.add((precioFinal/precioInicial-1)*100);

                                        }


       

                            }

                  }
                    if(!f.isEmpty())
                    {
                        Collections.sort(f);
                        valores = new float[f.size()];
                        for(int j = 0; j < f.size(); j++)
                            valores[j] = f.get(j);
                        double media = media(valores);

                         if(i == 0)
                            Texto.append("\n\n\t\t\tFuzzy agents");
                        if(i == 1)
                            Texto.append("\n\n\n\t\t\tTechnical agents");
                        if(i == 2)
                            Texto.append("\n\n\n\t\t\tZero intelligence agents");

                        transacciones(i);
                        proCalces = new float[calcesA.size()];
                        proTran = new float[transaccionesA.size()];

                        Collections.sort(calcesA);
                        Collections.sort(transaccionesA);


                        int cantidadCalces = 0;
                        int cantidadOfertas = 0;
                        for(int h = 0; h< calcesA.size() ; h++)
                        {
                            proCalces[h] = calcesA.get(h);
                            cantidadCalces += proCalces[h];
                            proTran[h] =  transaccionesA.get(h);
                            cantidadOfertas += proTran[h];

                        }

                        double promedioCalces = media(proCalces);
                        double promedioTransacciones = media(proTran);

                        Texto.append("\n-------------------------------------------------------------------------------------------------------\n");
                        Texto.append("Total amount of negotiations\t"+cantidadCalces+"\n");
                        Texto.append("Total amount of orders\t"+cantidadOfertas+"\n\n");
                         Texto.append("\t\tAverage\tDesviation\tMin\tMax\n");
                         Texto.append("Profitability(%)\t\t"+formateador.format(media)+"\t"+formateador.format(desviacion(valores,media))+"\t"+formateador.format(f.get(0))+"\t"+formateador.format(f.get(f.size()-1)));

                         
                         Texto.append("\nAmount of orders\t"+formateador.format(promedioTransacciones)+"\t"+formateador.format(desviacion(proTran,promedioTransacciones))+"\t"+formateador.format(transaccionesA.get(0))+"\t"+formateador.format(transaccionesA.get(transaccionesA.size()-1)));

                         
                         Texto.append("\nAmount of negotiations\t"+formateador.format(promedioCalces)+"\t"+formateador.format(desviacion(proCalces,promedioCalces))+"\t"+formateador.format(calcesA.get(0))+"\t"+formateador.format(calcesA.get(calcesA.size()-1)));

                        //Texto.append("primero "+f.get(0));
                         //Texto.append("final "+f.get(f.size()-1));
                         f.clear();
                    }
                }
            }
        catch (SQLException ex) {
                    ex.printStackTrace();
                }



        BD.cerrarConexion();
        BDA.cerrarConexion();
        BDcantidadAgentes.cerrarConexion();
        BDnombres.cerrarConexion();
        BDcantidadAcciones.cerrarConexion();
        BDprecio.cerrarConexion();
        BDcantidad.cerrarConexion();
        BDcantidad2.cerrarConexion();
        //BDnombres.cerrarConexion();


    }

    private void acciones_rentabilidad()
    {
        float precioFinal = 0;
        float precioInicial = 0;
        ArrayList<Float> rentabilidades = new ArrayList<Float>();
        
        ArrayList<String> nombres = new ArrayList<String>();
        
        //ArrayList<Integer> paquetes = new ArrayList<Integer>();
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


                    ValorInicial = BD.consulta("SELECT IDProducto, precioCierre FROM cotizacion WHERE fecha = 0 AND precioCierre is not NULL AND IDProducto = "+IDProducto.getInt(1));
                    ValorFinal = BDA.consulta("SELECT IDProducto, precioCierre FROM cotizacion WHERE repeticion = "+repeticion+" AND fecha = (SELECT max(fecha) FROM cotizacion WHERE repeticion = "+repeticion+" AND IDProducto = "+IDProducto.getInt(1)+" AND precioCierre is not NULL) AND IDProducto = "+IDProducto.getInt(1));
                    nombre = BDnombres.consulta("SELECT nombre FROM producto WHERE IDProducto = "+IDProducto.getInt(1));

                   // System.out.println("ID producto es "+IDProducto.getInt(1));

                     if(ValorInicial.next() && ValorFinal.next() && nombre.next())
                            {
                               // System.out.println("ID producto es "+ValorInicial.getFloat(2));
                                precioInicial = ValorInicial.getFloat(2);
                                precioFinal = ValorFinal.getFloat(2);
                                if(precioInicial != 0.0 && precioFinal !=0)
                                {
                                   // System.out.println((precioFinal/precioInicial-1)*100 + " " + nombre.getString(1));
                                rentabilidades.add((precioFinal/precioInicial-1)*100);
                                nombres.add(nombre.getString(1));
                                
                                //paquetes.add(nombre.getInt(3));
                                //dataset.setValue(r.getString(1), Math.abs((precioFinal/precioInicial-1))*100);
                                }

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
        
        acciones_volatilidad();

        

        
        Texto.append("\n\n\n\t\t\tStocks summary\n");
        Texto.append("-------------------------------------------------------------------------------------------------------\n");
        Texto.append("Stock\tProfitability\tVolatility\n");

        for(int k = 0; k<rentabilidades.size() ; k++)
                Texto.append(nombres.get(k)+"\t"+formateador.format(rentabilidades.get(k))+"\t"+formateador.format(Volatilidades.get(k))+"\n");



    }
    private void acciones_volatilidad()
    {
        BD = new ConexionBD("mysql");
        BDnombres = new ConexionBD("mysql");
        BDpromedio = new ConexionBD("mysql");
        ResultSet r = BD.consulta("SELECT IDProducto, std(precioCierre) FROM cotizacion where (repeticion = 0 or repeticion = "+repeticion+") GROUP BY IDProducto");
        ResultSet nombre = null;
        ResultSet promedio = null;
        try {
         while(r.next())
                {

                   nombre = BDnombres.consulta("SELECT nombre FROM producto WHERE IDProducto = "+r.getString(1));
                   promedio = BDpromedio.consulta("SELECT avg(precioCierre) FROM cotizacion WHERE IDProducto = "+r.getString(1)+" and (repeticion = 0 or repeticion = "+repeticion+")");
                   if(nombre.next() && promedio.next())
                   Volatilidades.add((r.getDouble(2)/promedio.getDouble(1))*100);


                }


            }
        catch (SQLException ex) {
                    ex.printStackTrace();
                }

        BD.cerrarConexion();
        BDnombres.cerrarConexion();
         BDpromedio.cerrarConexion();

    }

    private void transacciones(int tipos)
    {
        try {
            transaccionesA.clear();
            calcesA.clear();

        int cantidadAgentes = 0;
        BDCantidad = new ConexionBD("mysql");
        ResultSet cantidad = BDCantidad.consulta("SELECT count(*) FROM bursatil where tipo = "+tipos);

        if(cantidad.next())
            cantidadAgentes = cantidad.getInt(1);

        BDCantidad.cerrarConexion();

        String[] nombresTCompra = new String[cantidadAgentes];
        String[] nombresTVenta = new String[cantidadAgentes];
        String[] nombresOCompra = new String[cantidadAgentes];
        String[] nombresOVenta = new String[cantidadAgentes];
        String[] nombres = new String[cantidadAgentes];
        int[] Ofertas = new int[cantidadAgentes];
        int[] Calces = new int[cantidadAgentes];


        int[] transaccionTCompra = new int[cantidadAgentes];
        int[] transaccionTVenta = new int[cantidadAgentes];
        int[] transaccionOCompra = new int[cantidadAgentes];
        int[] transaccionOVenta = new int[cantidadAgentes];

        ConexionBD BDotro = new ConexionBD("mysql");
        BDofertas = new ConexionBD("mysql");
        BDCompra = new ConexionBD("mysql");
        BDVenta = new ConexionBD("mysql");
         BDCantidad = new ConexionBD("mysql");



           ResultSet ov = null;
           ResultSet oc = null;
           ResultSet rCompra = null;
           ResultSet rVenta = null;


           cantidad = BDCantidad.consulta("SELECT nombre FROM bursatil where tipo = "+tipos+" order by nombre");


                   rCompra = BDCompra.consulta("select  b.nombre, count(*)  from calce c, ofertacompra oc, bursatil b where oc.idofertacompra = c.idofertacompra and oc.idagente = b.idagente and b.tipo = "+tipos+" and c.repeticion = "+repeticion+" and oc.repeticion = "+repeticion+" group by b.idagente order by b.nombre");
                   rVenta = BDVenta.consulta("select  b.nombre, count(*)  from calce c, ofertaventa ov, bursatil b where ov.idofertaventa = c.idofertaventa and ov.idagente = b.idagente and b.tipo = "+tipos+" and ov.repeticion = "+repeticion+" and c.repeticion = "+repeticion+" group by b.idagente order by b.nombre");
                   oc = BDofertas.consulta("SELECT count(*), b.nombre FROM ofertacompra o, bursatil b where o.idagente = b.idagente and b.tipo = "+tipos+" and o.repeticion = "+repeticion+" group by b.idagente order by b.nombre");
                   ov = BDotro.consulta("SELECT count(*), b.nombre FROM ofertaventa v, bursatil b where v.idagente = b.idagente and b.tipo = "+tipos+" and v.repeticion = "+repeticion+" group by b.idagente order by b.nombre");



                int indice = 0;
                while(cantidad.next())
                {
                    nombres[indice] = cantidad.getString(1);
                    Ofertas[indice] = 0;
                    Calces[indice] = 0;
                    indice++;

                }
                int indiceTCompra = 0;
                int indiceOCompra = 0;


                int indiceOVenta = 0;
                int indiceTVenta = 0;



                while(rCompra.next())
                {
                    nombresTCompra[indiceTCompra] = rCompra.getString(1);
                    transaccionTCompra[indiceTCompra] = rCompra.getInt(2);
                    indiceTCompra++;
                }


                while(rVenta.next())
                {
                    nombresTVenta[indiceTVenta] = rVenta.getString(1);
                    transaccionTVenta[indiceTVenta] = rVenta.getInt(2);
                    indiceTVenta++;
                }


                while(oc.next())
                {
                    nombresOCompra[indiceOCompra] = oc.getString(2);
                    transaccionOCompra[indiceOCompra] = oc.getInt(1);
                    indiceOCompra++;
                }


                while(ov.next())
                {
                    nombresOVenta[indiceOVenta] = ov.getString(2);
                    transaccionOVenta[indiceOVenta] = ov.getInt(1);
                    indiceOVenta++;
                }




                for(int i = 0; i< cantidadAgentes; i++)
                { for(int j = 0; j< indiceTCompra; j++ )
                  {
                      if(nombres[i].equalsIgnoreCase(nombresTCompra[j]))
                      {
                          Calces[i] = Calces[i] + transaccionTCompra[j];
                          break;
                      }
                  }
                }

                for(int i = 0; i< cantidadAgentes; i++)
                { for(int j = 0; j< indiceTVenta; j++ )
                  {
                      if(nombres[i].equalsIgnoreCase(nombresTVenta[j]))
                      {
                          Calces[i] = Calces[i] + transaccionTVenta[j];
                          break;
                      }
                  }
                }

              for(int i = 0; i< cantidadAgentes; i++)
                { for(int j = 0; j< indiceOCompra; j++ )
                  {
                      if(nombres[i].equalsIgnoreCase(nombresOCompra[j]))
                      {
                          Ofertas[i] = Ofertas[i] + transaccionOCompra[j];
                          break;
                      }
                  }
                }

               for(int i = 0; i< cantidadAgentes; i++)
                { for(int j = 0; j< indiceOVenta; j++ )
                  {
                      if(nombres[i].equalsIgnoreCase(nombresOVenta[j]))
                      {
                          Ofertas[i] = Ofertas[i] + transaccionOVenta[j];
                          break;
                      }
                  }
                }

            //*****************************************************************************

            //*****************************************************************************

              for(int i = 0; i< cantidadAgentes; i++)
                {

                  calcesA.add(Calces[i]);
                  transaccionesA.add(Ofertas[i]);

                }




                BDCompra.cerrarConexion();
                BDVenta.cerrarConexion();
                BDofertas.cerrarConexion();
                //BD.cerrarConexion();
                BDCantidad.cerrarConexion();
                BDotro.cerrarConexion();
            

            
        }
        catch (SQLException ex) {
                    ex.printStackTrace();
                }
        catch (java.lang.NullPointerException ex) {
                    ex.printStackTrace();
                }

    }

   private void acciones_precios()
   {

        BD = new ConexionBD("mysql");
        BDnombres = new ConexionBD("mysql");

        ResultSet precios = null;
        ResultSet acciones = BDnombres.consulta("SELECT IDProducto, nombre FROM producto");
        try{
        Texto.append("\n\n\n\t\t\tPrices summary ($)\n");
        Texto.append("-------------------------------------------------------------------------------------------------------\n");
        Texto.append("Stock\tAverage\tDesviation\tMin\tMax\n");

            while(acciones.next())
            {
                precios = BD.consulta("SELECT avg(precioCierre), std(precioCierre), min(precioCierre), max(precioCierre) FROM cotizacion c where idproducto = "+acciones.getInt(1)+" and (repeticion = 0 or repeticion = "+repeticion+")");
                //System.out.println("SELECT avg(precioPromedio), std(precioPromedio), min(precioPromedio), max(precioPromedio) FROM cotizacion c where idproducto = "+acciones.getInt(1));
                if(precios.next())
                   Texto.append(acciones.getString(2)+"\t"+formateador.format(precios.getFloat(1))+"\t"+formateador.format(precios.getFloat(2))+"\t"+formateador.format(precios.getFloat(3))+"\t"+formateador.format(precios.getFloat(4))+"\n");
            }


                }
        catch (SQLException ex) {
                    ex.printStackTrace();
                }
        catch (java.lang.NullPointerException ex) {
                    ex.printStackTrace();
                }
        BDnombres.cerrarConexion();
        BD.cerrarConexion();
   }

   public double media(float arr[ ])
    {
        double sum = 0.0;

        for(int i = 0; i < arr.length ; i++)
        {
            sum += arr[i];
        }

        return sum / arr.length;
    }

    public double desviacion(float arr[ ], double media)
    {
        double sum = 0.0;
        double desviacion;

        for(int i = 0; i < arr.length ; i++)
        {
            sum += Math.pow(arr[i] - media, 2);
        }
        desviacion = Math.sqrt(sum / (arr.length - 1));
        return desviacion;
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

        setTitle("Overview");

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
                new Resumen(1).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea Texto;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables

}
