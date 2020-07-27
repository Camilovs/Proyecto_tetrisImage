
package tetris;

/**
 * Clase que representa un punto de coordenadas x, y en un plano.
 * @author Camilo
 */
public class puntoCartesiano {
    /*Coordenada en x*/
    private double x;
    /*Coordenada en y*/
    private double y;
    /**
     * Constructor del punto cartesiano
     * @param x posicion actual en x
     * @param y posicion actual en y
     */
    public puntoCartesiano(double x, double y) {
        this.x = x;
        this.y = y;
    }
    /**
     * Retorna la posicion exacta en la coordenada x.
     * @return Double posicion exacta en x.
     */
    public double getX() {
        return x;
    }
    /**
     * Cambia el valor exacto de la coordenada x
     * @param x Nueva valor en x
     */
    public void setX(double x) {
        this.x = x;
    }
    /**
     * Retorna la posicion exacta en la coordenada y.
     * @return Double posicion exacta en y.
     */
    public double getY() {
        return y;
    }
    /**
     * Cambia el valor exacto de la coordenada y.
     * @param y Nuevo valor en y
     */
    public void setY(double y) {
        this.y = y;
    }
}
