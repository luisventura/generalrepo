package korinver.com.ecomanda;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Luis on 1/19/2015.
 */
public class persona{
    private int numeroPersona;
    private int rangoEdad;
    private String genero;
    private ArrayList<platillo> listaPlatillos;

    public int getRangoEdad() {
        return rangoEdad;
    }

    public void setRangoEdad(int rangoEdad) {
        this.rangoEdad = rangoEdad;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public persona(int numeroPersona, int rangoEdad, String genero){
        this.numeroPersona=numeroPersona;
        this.rangoEdad = rangoEdad;
        this.genero = genero;
        this.listaPlatillos = new ArrayList<platillo>();
    }

    public ArrayList<platillo> getListaPlatillos() {
        return listaPlatillos;
    }

    public void setListaPlatillos(ArrayList<platillo> listaPlatillos) {
        this.listaPlatillos = listaPlatillos;
    }

    public int getNumeroPersona() {
        return numeroPersona;
    }

    public void setNumeroPersona(int numeroPersona) {
        this.numeroPersona = numeroPersona;
    }

    public void anadirPlatillo(platillo platoAAgregar){
        this.listaPlatillos.add(platoAAgregar);
    }

    public void eliminarPlatillo(int indice){
        this.listaPlatillos.remove(indice);
    }
}
