
package tetris;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Clase que representa una pieza del juego
 * @author Camilo
 */
public class Pieza {
    
    /*Imagen de la pieza*/
    private BufferedImage img;
    /*posicion de la pieza en el panel*/
    private puntoCartesiano pos=new puntoCartesiano(0, 0);
    /*String del nombre de la imagen de la pieza*/
    private String nombreSubImg;
    /*Matriz de fondo en la que se juega*/
    private MatrizFondo matriz;
    /*Panel en el que se dibuja*/
    private Panel panel;
    /*Entero que indica cuanta distancia se mueven las piezas*/
    private int MOV;
    /*Variable logica que restringe el movimiento horizontal de las piezas en
    ciertos momentos*/
    private boolean permitirmov;
    
    /**
     * Constructor de la clase Pieza
     * @param p Panel de dibujado
     * @param m Matriz de fondo en la que se trabaja
     * @param n nombre de la imagen para la pieza
     * @throws IOException Error en leer el archivo de imagen.
     */
    public Pieza(Panel p, MatrizFondo m, String n) throws IOException {
        nombreSubImg=n;
        img = ImageIO.read(new FileInputStream(new File(n)));
        this.matriz=m;
        this.panel= p;        
        MOV=img.getWidth();
        permitirmov=true;
    }
    /**
     * Metodo que simula la caida de la pieza sumando un valor en el eje y
     * @param newy nuevo valor en el eje y.
     * @return un boolean para representar si la pieza todo superficie o no.
     */
    public boolean caidaPieza(double newy){       
        int aux2=(int)((newy+pos.getY())/panel.getSubLado());
        int aux=(int)pos.getX()/panel.getSubLado();       
        if (pos.getY()+newy<=panel.getHeight()-panel.getSubLado())        
            if (!choquePieza(aux, aux2)){               
                setPosy(newy+pos.getY());
                
                return false;
            }
            else
                permitirmov=false;
        return true;
    }
    /**
     * Metodo que mueve hacia abajo una pieza una distancia especificada por 
     * la variable MOV
     */
    public void moverAbajo(){
        int aux=(int)(pos.getX())/panel.getSubLado();
        int aux2=(int)(pos.getY()+MOV)/panel.getSubLado();
        if (pos.getY()+MOV<=panel.getHeight()-panel.getSubLado() && permitirmov) 
            if (!choquePieza(aux, aux2))
                setPosy(pos.getY()+MOV);           
    }
    /**
     * Metodo que mueve a la derecha una pieza una distancia especificada por 
     * la variable MOV
     */
    public void moverDer(){
        int aux=(int)(pos.getX()+MOV)/panel.getSubLado();
        int aux2=(int)pos.getY()/panel.getSubLado();
        if ((int)pos.getX()+MOV<=panel.getLadoImg()-panel.getSubLado() && permitirmov)            
            if(!choquePieza(aux, aux2))
                setPosx(pos.getX()+MOV);
    }
     /**
     * Metodo que mueve a la izquierda una pieza una distancia especificada por 
     * la variable MOV
     */
    public void moverIzq(){
        int aux=(int)(pos.getX()-MOV)/panel.getSubLado();
        int aux2=(int)pos.getY()/panel.getSubLado();           
        if (0<=pos.getX()-MOV && permitirmov)
            if(!choquePieza(aux, aux2))
                setPosx(pos.getX()-MOV);
    }   
    /**
     * Metodo que retorna true si hay un choque de piezas y false cuando no.
     * @param x posicion fila de la matriz a analizar
     * @param y posicion columna de la matriz a analizar
     * @return True si las piezas chocaron o false si no hay choque
     */  
    public boolean choquePieza(int x, int y){       
        return !matriz.getElemento(x, y-2).equals("");
    }   
    /**
     * Retornta la imagen actual de la pieza
     * @return BufferedImage imagen actual de la pieza
     */
    public BufferedImage getImg() {
        return img;
    }
    /**
     * Retorna la posicion en x actualmente de la pieza
     * @return Double posicion en x.
     */
    public double getPosx() {
        return pos.getX();
    }
    /**
     * Cambia el valor de la posicion en x de la pieza.
     * @param posx nueva posicion en x.
     */
    public void setPosx(double posx) {
        this.pos.setX(posx);
    }
    /**
     * Retorna la posicion en y actualmende de la pieza.
     * @return Double posicion en y.
     */
    public double getPosy() {
        return pos.getY();
    }
    /**
     * Cambia el valor de la posicion en y de la pieza.
     * @param posy Nueva posicion en y.
     */
    public void setPosy(double posy) {
        this.pos.setY(posy);
    }
    /**
     * Retorna el nombre de la imagen de la pieza
     * @return String nombre de la imagen.
     */
    public String getNombreSubImg() {
        return nombreSubImg;
    }

    
   
    
    
    
}
