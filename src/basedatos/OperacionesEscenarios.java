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
public class OperacionesEscenarios {
    
    public static void main(String[] args)
    {
        ConexionBD BD = new ConexionBD("mysql");
        ConexionBD BD1 = new ConexionBD("mysql");
        ConexionBD BD2 = new ConexionBD("mysql");
        ConexionBD BD3 = new ConexionBD("mysql");
        
        
        int tiempo = 60;
        ResultSet compra = null;
        ResultSet venta = null;
        ResultSet continuous = null;
        ResultSet call = null;
        
        try {
            
            
  
                    compra = BD.consulta("SELECT count(*), repeticion, idproducto FROM ofertacompra group by idproducto, repeticion");
                    venta = BD1.consulta("SELECT count(*), repeticion, idproducto FROM ofertaventa group by idproducto, repeticion");
                    continuous = BD2.consulta("SELECT count(*), repeticion, idproducto FROM calce group by idproducto, repeticion");
                    call = BD3.consulta("SELECT count(*), repeticion, idproducto FROM calcecm group by idproducto, repeticion");
                    
                    while(compra.next() && venta.next() && continuous.next() && call.next())
                    {
                        System.out.println(compra.getInt(1)+"\t"+venta.getInt(1)+"\t"+continuous.getInt(1)+"\t"+call.getInt(1)+"\t"+call.getInt(2)+"\t"+call.getInt(3)+"\t");
                    }

            
            } catch (SQLException ex) {
                ex.printStackTrace();
            
        
        }
        BD.cerrarConexion();
        BD1.cerrarConexion();
        BD2.cerrarConexion();
        BD3.cerrarConexion();
        
    }
}
