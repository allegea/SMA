/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package informacion;

/**
 *
 * @author Sebas
 */
public class InfoCalce {
    private int IDCalce;
    private int IDOfertaCompra;
    private int IDOfertaVenta;
    private int IDProducto;
    private float precioCompra;
    private float precioVenta;
    private int fecha;
    private int cantidad;

    public InfoCalce(int IDCalce, int IDOC, int IDOV, int IDProd, float pCompra,
            float pVenta, int fecha, int cant)
    {
        this.IDCalce = IDCalce;
        this.IDOfertaCompra = IDOC;
        this.IDOfertaVenta = IDOV;
        this.IDProducto = IDProd;
        this.precioCompra = pCompra;
        this.precioVenta = pVenta;
        this.fecha = fecha;
        this.cantidad = cant;
    }

    public int getIDCalce()
    {
        return this.IDCalce;
    }

    public void setIDCalce(int id)
    {
        this.IDCalce = id;
    }

    public int getIDOfertaCompra()
    {
        return this.IDOfertaCompra;
    }

    public void setIDOfertaCompra(int id)
    {
        this.IDOfertaCompra = id;
    }

    public int getIDOfertaVenta()
    {
        return this.IDOfertaVenta;
    }

    public void setIDOfertaVenta(int id)
    {
        this.IDOfertaVenta = id;
    }

    public int getIDProducto()
    {
        return this.IDProducto;
    }

    public void setIDProducto(int id)
    {
        this.IDProducto = id;
    }

    public float getPrecioCompra()
    {
        return this.precioCompra;
    }

    public void setPrecioCompra(float p)
    {
        this.precioCompra = p;
    }

    public float getPrecioVenta()
    {
        return this.precioVenta;
    }

    public void setPrecioVenta(float p)
    {
        this.precioVenta = p;
    }

    public int getFecha()
    {
        return this.fecha;
    }

    public void setFecha(int fecha)
    {
        this.fecha = fecha;
    }

    public int getCantidad()
    {
        return this.cantidad;
    }

    public void setCantidad(int c)
    {
        this.cantidad = c;
    }
}
