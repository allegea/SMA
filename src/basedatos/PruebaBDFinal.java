/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package basedatos;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 *
 * @author allegea
 */
public class PruebaBDFinal {
    
    public static void main(String[] args)
    {
        ConexionBD BD = new ConexionBD("mysql");
        ConexionBD BD1 = new ConexionBD("mysql");
        ConexionBD BD2 = new ConexionBD("mysql");
        ConexionBD BD3 = new ConexionBD("mysql");
        ConexionBD BD4 = new ConexionBD("mysql");
        
        ConexionBD BD5 = new ConexionBD("mysql");
        ConexionBD BD6 = new ConexionBD("mysql");
        ConexionBD BD7 = new ConexionBD("mysql");
        ConexionBD BD8 = new ConexionBD("mysql");
        
        int cantidad = 3;
        int historico = 70;

        
        int tiempo = 3;
        ResultSet frecuencia = null;
        ResultSet transadasContinuous = null;
        ResultSet transadasCallMarket = null;
        ResultSet ofertadasVenta = null;
        ResultSet ofertadasCompra = null;
        
        ResultSet OfertasCompraSin = null;
        ResultSet OfertasVentaSin = null;
        ResultSet CantidadOfertasCompra = null;
        ResultSet CantidadOfertasVenta = null;
        
        
        /*select count(idofertacompra) from simab.ofertacompra where repeticion = 1 and fecha = 1 and idproducto = 0 and idofertacompra not in (select idofertacompra from simab.calce where repeticion = 1 and fecha = 1 and idproducto = 0)
        select count(idofertacompra) from simab.ofertacompra where repeticion = 1 and fecha = 1 and idproducto = 0 and idofertacompra not in (select idofertacompra from simab.calcecm where repeticion = 1 and fecha = 1 and idproducto = 0)
        select count(idofertacompra) from simab.ofertacompra where repeticion = 1 and fecha = 1 and idproducto = 0
        
        select count(idofertacompra) from simab.ofertacompra where repeticion = 1 and fecha = 1 and idproducto = 0 and idofertacompra not in (select idofertacompra from simab.calcecm where repeticion = 1 and fecha = 1 and idproducto = 0) and idofertacompra not in (select idofertacompra from simab.calce where repeticion = 1 and fecha = 1 and idproducto = 0)
        select count(idofertaventa) from simab.ofertaventa where repeticion = 1 and fecha = 1 and idproducto = 0 and idofertaventa not in (select idofertaventa from simab.calcecm where repeticion = 1 and fecha = 1 and idproducto = 0) and idofertaventa not in (select idofertaventa from simab.calce where repeticion = 1 and fecha = 1 and idproducto = 0) 
         * 
        select count(idofertaventa) from simab.ofertaventa where repeticion = 1 and fecha = 1 and idproducto = 0 and idofertaventa not in (select idofertaventa from simab.calce where repeticion = 1 and fecha = 1 and idproducto = 0)
        select count(idofertaventa) from simab.ofertaventa where repeticion = 1 and fecha = 1 and idproducto = 0 and idofertaventa not in (select idofertaventa from simab.calcecm where repeticion = 1 and fecha = 1 and idproducto = 0)
        select count(idofertaventa) from simab.ofertaventa where repeticion = 1 and fecha = 1 and idproducto = 0        
                */
        
        
        //SELECT 2*sum(precioCompra*cantidad) FROM simab.calce where idproducto = 0 and fecha >=1 and fecha <=60;
        //SELECT 2*sum(precioCalce*cantidad) FROM simab.calcecm where idproducto = 0 and fecha >=1 and fecha <=60;
        
        /*
         * 
         *                         transadasCallMarket = BD.consulta("SELECT 2*sum(precioCalce*cantidad) FROM calcecm where idproducto = "+i+" and fecha >="+(j*tiempo+1)+" and fecha <="+((j+1)*tiempo));
                        transadasContinuous = BD1.consulta("SELECT 2*sum(precioCompra*cantidad) FROM calce where idproducto = "+i+" and fecha >="+(j*tiempo+1)+" and fecha <="+((j+1)*tiempo));
                        ofertadasVenta = BD2.consulta("select sum(cantidad*precioventa) from ofertaventa where idproducto = "+i+" and fecha >="+(j*tiempo+1)+" and fecha <="+((j+1)*tiempo));
                        ofertadasCompra = BD3.consulta("select sum(cantidad*preciocompra) from ofertacompra where idproducto = "+i+" and fecha >="+(j*tiempo+1)+" and fecha <="+((j+1)*tiempo));

                        frecuencia = BD4.consulta("select cantidad from cotizacion where idproducto = "+i+" and fecha >="+(j*tiempo+1)+" and fecha <="+((j+1)*tiempo));

                        OfertasCompraSin = BD5.consulta("select count(idofertacompra) from ofertacompra where repeticion = 1 and fecha = "+(j*tiempo+1)+" and idproducto = "+i+" and idofertacompra not in (select idofertacompra from calcecm where repeticion = 1 and fecha = "+(j*tiempo+1)+" and idproducto = "+i+") and idofertacompra not in (select idofertacompra from calce where repeticion = 1 and fecha = "+(j*tiempo+1)+" and idproducto = "+i+")");
                        CantidadOfertasCompra = BD6.consulta("select count(idofertacompra) from ofertacompra where repeticion = 1 and fecha = "+(j*tiempo+1)+" and idproducto = "+i);
                        OfertasVentaSin = BD7.consulta("select count(idofertaventa) from ofertaventa where repeticion = 1 and fecha = "+(j*tiempo+1)+" and idproducto = "+i+" and idofertaventa not in (select idofertaventa from calcecm where repeticion = 1 and fecha = "+(j*tiempo+1)+" and idproducto = "+i+") and idofertaventa not in (select idofertaventa from calce where repeticion = 1 and fecha = "+(j*tiempo+1)+" and idproducto = "+i+") ");
                        CantidadOfertasVenta = BD8.consulta("select count(idofertaventa) from ofertaventa where repeticion = 1 and fecha = "+(j*tiempo+1)+" and idproducto = "+i);
         */
        
        
                double volumen = 0;
                double porcetajeExito = 0;
        try {
            
            for(int i=0;i<cantidad;i++)
            {
                System.out.println("\nAccion "+i);
                for(int j=0;j<historico/tiempo;j++)
                {
                    StringBuffer salida = new StringBuffer();
                    double volAcumulado = 0;
                    double porcentajeAcum = 0;
                    double frecuAcumu = 0;
                    int totalFre = 0;
                    int totalVol = 0;
                    int totalPor = 0;
                    for(int h =j*tiempo+1;h<=(j+1)*tiempo;h++)
                    {  
                        transadasCallMarket = BD.consulta("SELECT 2*sum(precioCalce*cantidad) FROM calcecm where idproducto = "+i+" and fecha >="+h+" and fecha <="+h);
                        transadasContinuous = BD1.consulta("SELECT 2*sum(precioCompra*cantidad) FROM calce where idproducto = "+i+" and fecha >="+h+" and fecha <="+h);
                        ofertadasVenta = BD2.consulta("select sum(cantidad*precioventa) from ofertaventa where idproducto = "+i+" and fecha >="+h+" and fecha <="+h);
                        ofertadasCompra = BD3.consulta("select sum(cantidad*preciocompra) from ofertacompra where idproducto = "+i+" and fecha >="+h+" and fecha <="+h);

                        frecuencia = BD4.consulta("select cantidad from cotizacion where idproducto = "+i+" and fecha >="+h+" and fecha <="+h);

                        OfertasCompraSin = BD5.consulta("select count(idofertacompra) from ofertacompra where repeticion = 1 and fecha = "+h+" and idproducto = "+i+" and idofertacompra not in (select idofertacompra from calcecm where repeticion = 1 and fecha = "+h+" and idproducto = "+i+") and idofertacompra not in (select idofertacompra from calce where repeticion = 1 and fecha = "+h+" and idproducto = "+i+")");
                        CantidadOfertasCompra = BD6.consulta("select count(idofertacompra) from ofertacompra where repeticion = 1 and fecha = "+h+" and idproducto = "+i);
                        OfertasVentaSin = BD7.consulta("select count(idofertaventa) from ofertaventa where repeticion = 1 and fecha = "+h+" and idproducto = "+i+" and idofertaventa not in (select idofertaventa from calcecm where repeticion = 1 and fecha = "+h+" and idproducto = "+i+") and idofertaventa not in (select idofertaventa from calce where repeticion = 1 and fecha = "+h+" and idproducto = "+i+") ");
                        CantidadOfertasVenta = BD8.consulta("select count(idofertaventa) from ofertaventa where repeticion = 1 and fecha = "+h+" and idproducto = "+i);
                        /*if(j==0)
                        {
                            System.out.println("select sum(cantidad)/"+enCirculacion[i]+" from cotizacion where idproducto = "+i+" and fecha >="+(j*tiempo+1)+" and fecha <="+((j+1)*tiempo));
                            System.out.println("select 2*sum(cantidad*preciopromedio) from cotizacion where idproducto = "+i+" and fecha >="+(j*tiempo+1)+" and fecha <="+((j+1)*tiempo));
                            System.out.println("select sum(cantidad*precioventa) from ofertaventa where idproducto = "+i+" and fecha >="+(j*tiempo+1)+" and fecha <="+((j+1)*tiempo));
                            System.out.println("select sum(cantidad*preciocompra) from ofertacompra where idproducto = "+i+" and fecha >="+(j*tiempo+1)+" and fecha <="+((j+1)*tiempo));
                        }*/

                        double tranCM = 0;
                        double tranC = 0;
                        double OC = 0;
                        double OV = 0;
                        if(transadasCallMarket.next())
                            tranCM = transadasCallMarket.getDouble(1);
                        
                        if(transadasContinuous.next())
                            tranC = transadasContinuous.getDouble(1);
                        
                        if( ofertadasVenta.next())
                            OV = ofertadasVenta.getDouble(1);
                        
                        if(ofertadasCompra.next())
                            OC = ofertadasCompra.getDouble(1);
                        
                        if(OC+OV!=0)
                        {
                                volumen = 100*(tranCM+tranC)/(OV+OC);
                                volAcumulado+=volumen;
                                totalVol++;
                            // salida.append((j+1)*tiempo).append("\t\t").append(volumen).append("\t\t");
                        }
                        
                        

                        if(frecuencia.next())
                        {
                            if(frecuencia.getInt(1)>0)
                                frecuAcumu++;
                            totalFre++;
                            
                            //salida.append(frecuencia.getInt(1)>0?"Se negocio":"No se negocio").append("\t\t");
                        }
                        
                        int OCS = 0;
                        int OVS = 0;
                        int COC = 0;
                        int COV = 0;
                        
                        if(OfertasCompraSin.next())
                            OCS = OfertasCompraSin.getInt(1);
                        
                        if(CantidadOfertasCompra.next())
                            COC = CantidadOfertasCompra.getInt(1);
                        
                        if(OfertasVentaSin.next())
                            OVS = OfertasVentaSin.getInt(1);
                        
                        if(CantidadOfertasVenta.next())
                            COV = CantidadOfertasVenta.getInt(1);
                        
                        if(COC+COV!=0)
                        {
                            int ofertasTotales = COC+COV;
                            int ofertasSinCalzar = OVS+OCS;
                            porcetajeExito = 100*(1-(ofertasSinCalzar/((float)ofertasTotales)));
                            porcentajeAcum+=porcetajeExito;
                            totalPor++;
                             //salida.append(porcetajeExito).append("\t\t").append(volumen*porcetajeExito);
                            
                        }
                    }
                    //System.out.println(salida);
                    double resp = (volAcumulado/totalVol)*(frecuAcumu/(double)totalFre)*(porcentajeAcum/totalPor);
                    System.out.println((j+1)*tiempo+"\t\t"+(volAcumulado/totalVol)+"\t\t"+(frecuAcumu/(double)totalFre)+"\t\t"+(porcentajeAcum/totalPor)+"\t\t"+resp);
                    
                    
                    
                }
                
            }
            
            
            
            
            } catch (SQLException ex) {
                ex.printStackTrace();
            
                BD.cerrarConexion();
                BD1.cerrarConexion();
                BD2.cerrarConexion();
                BD3.cerrarConexion();
                BD4.cerrarConexion();
                BD5.cerrarConexion();
                BD6.cerrarConexion();
                BD7.cerrarConexion();
                BD8.cerrarConexion();
        
        }
        
        
    }
}
