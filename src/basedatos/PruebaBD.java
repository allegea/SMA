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
public class PruebaBD {
    
    public static void main(String[] args)
    {
        ConexionBD BD = new ConexionBD("mysql");
        ConexionBD BD1 = new ConexionBD("mysql");
        ConexionBD BD2 = new ConexionBD("mysql");
        ConexionBD BD3 = new ConexionBD("mysql");
        
        int cantidad = 3;
        int historico = 300;
        double[] enCirculacion = new double[cantidad];
            enCirculacion[0] =  262036432;
            enCirculacion[1] =  567914624;
            enCirculacion[2] = 1107677894;
        
        int tiempo = 60;
        ResultSet acciones = null;
        ResultSet transadas = null;
        ResultSet ofertadasVenta = null;
        ResultSet ofertadasCompra = null;
        
        try {
            
            for(int i=0;i<cantidad;i++)
            {
                System.out.println("\nAccion "+i);
                for(int j=0;j<historico/tiempo;j++)
                {   
                    acciones = BD.consulta("select sum(cantidad)/"+enCirculacion[i]+" from cotizacion where idproducto = "+i+" and fecha >="+(j*tiempo+1)+" and fecha <="+((j+1)*tiempo));
                    transadas = BD1.consulta("select 2*sum(cantidad*preciopromedio) from cotizacion where idproducto = "+i+" and fecha >="+(j*tiempo+1)+" and fecha <="+((j+1)*tiempo));
                    ofertadasVenta = BD2.consulta("select sum(cantidad*precioventa) from ofertaventa where idproducto = "+i+" and fecha >="+(j*tiempo+1)+" and fecha <="+((j+1)*tiempo));
                    ofertadasCompra = BD3.consulta("select sum(cantidad*preciocompra) from ofertacompra where idproducto = "+i+" and fecha >="+(j*tiempo+1)+" and fecha <="+((j+1)*tiempo));
                    
                    /*if(j==0)
                    {
                        System.out.println("select sum(cantidad)/"+enCirculacion[i]+" from cotizacion where idproducto = "+i+" and fecha >="+(j*tiempo+1)+" and fecha <="+((j+1)*tiempo));
                        System.out.println("select 2*sum(cantidad*preciopromedio) from cotizacion where idproducto = "+i+" and fecha >="+(j*tiempo+1)+" and fecha <="+((j+1)*tiempo));
                        System.out.println("select sum(cantidad*precioventa) from ofertaventa where idproducto = "+i+" and fecha >="+(j*tiempo+1)+" and fecha <="+((j+1)*tiempo));
                        System.out.println("select sum(cantidad*preciocompra) from ofertacompra where idproducto = "+i+" and fecha >="+(j*tiempo+1)+" and fecha <="+((j+1)*tiempo));
                    }*/
                    
                    
                    if(acciones.next() && transadas.next() && ofertadasVenta.next() && ofertadasCompra.next() )
                    {
                        double acc = acciones.getDouble(1)*100;
                        double vol = transadas.getDouble(1)/(ofertadasVenta.getDouble(1)+ofertadasCompra.getDouble(1));
                        vol=vol*100;
                        System.out.println(((j+1)*tiempo)+"\t\t"+acc+"\t\t"+vol+"\t\t"+vol*acc);
                    } 
                }
                
            }
            
            
            
            
            } catch (SQLException ex) {
                ex.printStackTrace();
            
        
        }
        
        
    }
}
