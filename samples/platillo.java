package korinver.com.ecomanda;

import java.util.ArrayList;

/**
 * Created by Luis on 1/19/2015.
 */
public class platillo {

    private int recno;
    private int entregado;
    private int recnodetalle;
    private double precioAcumulado;
    private String comentario;
    private ArrayList<Integer> opcionesPlato;


    public platillo(int recno, int entregado, int recnodetalle, double precioAcumulado, String comentario, ArrayList<Integer> opcionesPlato) {
        this.recno = recno;
        this.entregado = entregado;
        this.recnodetalle = recnodetalle;
        this.precioAcumulado = precioAcumulado;
        this.comentario = comentario;
        this.opcionesPlato = opcionesPlato;
    }

    public int getRecno() {
        return recno;
    }

    public void setRecno(int recno) {
        this.recno = recno;
    }

    public int getEntregado() {
        return entregado;
    }

    public void setEntregado(int entregado) {
        this.entregado = entregado;
    }

    public int getRecnodetalle() {
        return recnodetalle;
    }

    public void setRecnodetalle(int recnodetalle) {
        this.recnodetalle = recnodetalle;
    }

    public double getPrecioAcumulado() {
        return precioAcumulado;
    }

    public void setPrecioAcumulado(double precioAcumulado) {
        this.precioAcumulado = precioAcumulado;
    }

    public ArrayList<Integer> getOpcionesPlato() {
        return opcionesPlato;
    }

    public void setOpcionesPlato(ArrayList<Integer> opcionesPlato) {
        this.opcionesPlato = opcionesPlato;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}