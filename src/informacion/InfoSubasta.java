/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package informacion;

/**
 *
 * @author Sebas
 */
public class InfoSubasta {
    private float precioInicio;
    private float precioCierre;
    private float precioMax;
    private float precioMin;
    private float precioPromedio;
    private int periodo;
    private int IDAccion;
    private int cantidad;

    public InfoSubasta()
    {
        this.precioInicio = 0;
        this.precioCierre = 0;
        this.precioMax = 0;
        this.precioMin = 0;
        this.precioPromedio = 0;
        this.periodo = 0;
        this.IDAccion = 0;
        this.cantidad = 0;
    }

    public InfoSubasta(float pi, float pc, float pmax, float pmin, float ppro, int per, int accion, int cant)
    {
        this.precioInicio = pi;
        this.precioCierre = pc;
        this.precioMax = pmax;
        this.precioMin = pmin;
        this.precioPromedio = ppro;
        this.periodo = per;
        this.IDAccion = accion;
        this.cantidad = cant;
    }

    public float getPrecioInicio()
    {
        return this.precioInicio;
    }

    public void setPrecioInicio(float pi)
    {
        this.precioInicio = pi;
    }

    public float getPrecioCierre()
    {
        return this.precioCierre;
    }

    public void setPrecioCierre(float pc)
    {
        this.precioCierre = pc;
    }

    public float getPrecioMax()
    {
        return this.precioMax;
    }

    public void setPrecioMax(float pmax)
    {
        this.precioMax = pmax;
    }

    public float getPrecioMin()
    {
        return this.precioMin;
    }

    public void setPrecioMin(float pmin)
    {
        this.precioMin = pmin;
    }

    public float getPrecioPromedio()
    {
        return this.precioPromedio;
    }

    public void setPrecioPromedio(float ppro)
    {
        this.precioPromedio = ppro;
    }

    public int getPeriodo()
    {
        return this.periodo;
    }

    public void setPeriodo(int per)
    {
        this.periodo = per;
    }

    public int getIDAccion()
    {
        return this.IDAccion;
    }

    public void setIDAccion(int accion)
    {
        this.IDAccion = accion;
    }

    public int getCantidad()
    {
        return this.cantidad;
    }

    public void setCantidad(int cant)
    {
        this.cantidad = cant;
    }
}
