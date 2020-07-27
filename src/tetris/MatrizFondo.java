
package tetris;
import java.awt.Color;
import java.awt.Graphics;


/**
 * Clase para representar la matriz de fondo en el tablero, donde ser iran agregando
 * las imagenes a medida que caigan.
 * @author Camilo
 */
public class MatrizFondo {
    /*Filas y columnas de la metriz y el valor para el lado de cada cuadrado*/
    private int filas, columnas,lado=0;    
    /*Posicion x,y de la matriz*/
    private puntoCartesiano pos;
    /*Matriz que contendra los nombres de cada imagen para dibujarla*/
    private String matriz[][];
    /*Panel de dibujado*/
    private Panel panel;
    
    
    /**
     * Constructor de la clase Matriz de Fondo
     * @param panel Panel de dibujado
     */
    public MatrizFondo(Panel panel) {
        this.panel = panel;
        this.filas=panel.getFilas();
        this.columnas=panel.getCol();
        pos=new puntoCartesiano(0, 100);
        lado=panel.getSubLado();                    
        matriz=new String[columnas][filas];  
        espaciosVacios();
    }
    /**
     * Metodo para dibujar la Grilla 
     * @param g Graphics
     */
    public void dibujarGrilla(Graphics g){
        g.setColor(new Color(241, 180, 180));
        g.fillRect(0, 0, 496, panel.getHPLUS());
        for (int y = 0; y < filas+2; y++) {
            for (int x = 0; x < columnas; x++) {
                int xtemp=x*lado;               
                int ytemp=y*lado;
                g.setColor(Color.BLACK);
                g.drawRect(xtemp, ytemp, lado, lado);                              
                //g.setFont(new Font("verdana",Font.PLAIN,10));
                //g.drawString(xtemp+","+ytemp, xtemp, ytemp+11);                       
            }    
        }
        
        panel.repaint();
        
        
    }   
    /**
     * Metodo que rellena la matriz con string vacios
     */
    public void espaciosVacios(){
        for (int y = 0; y < filas; y++) {
            for (int x = 0; x < columnas; x++) {
                matriz[x][y]="";
            }
        }
    }
    /**
     * Metodo que agrega el nombre de una pieza caida en la posicion 
     * x,y de la matriz
     * @param pieza 
     */
    public void agregarPiezaCaida(Pieza pieza){
        /*Coordenada x actual de la pieza en el panel dividido en lado para 
        obtener una posicion valida en la matriz*/
        int x=(int)pieza.getPosx()/panel.getSubLado();
        /*Coordenada y actual de la pieza en el panel dividido en lado para 
        obtener una posicion valida en la matriz*/
        int y=(int)pieza.getPosy()/panel.getSubLado();
        try{
            matriz[x][y-2]=pieza.getNombreSubImg();
        }catch(Exception ex){
            panel.clearSublista();
        }
    }
    /**
     * Retorna el strin que se encuentra en una posicion x,y de la matriz
     * @param x Coordenada en x
     * @param y Coordenada en y
     * @return String Elemendo en la posicion especificada
     */
    public String getElemento(int x, int y) {
        if (0<=y && y<panel.getCol()) {
            //System.out.println("coor: "+x+","+y);
            //System.out.println("radar:"+matriz[x][y]);           
            return matriz[x][y];
        }
        return "";
    }
    /**
     * Retorna la cantidad de filas de la matriz
     * @return Int cantidad de filas
     */
    public int getFilas() {
        return filas;
    }
    /**
     * Retorna la cantidad de columnas de la matriz.
     * @return Int Cantidad de columnas.
     */
    public int getColumnas() {
        return columnas;
    }
    /**
     * Retorna la matriz completa
     * @return String[][] La matriz
     */
    public String[][] getMatriz() {
        return matriz;
    }         
}
