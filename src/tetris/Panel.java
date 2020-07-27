package tetris;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;
import org.imgscalr.Scalr;
import com.itextpdf.text.*;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;


/**
 * Clase que representa el panel principal del Juego. Aca se dibujan todos los 
 * elementos graficos del juego, se realizan las principales funcionalides y se 
 * aplica el Timer del juego.
 * @author Camilo
 */
public class Panel extends JPanel {   
    
    /*La pieza actual con la que se juega*/
    private Pieza pieza;
    /*Matriz de fondo, simula el tablero*/
    private MatrizFondo matriz;    
    /*Estado de la instruccion de dibujar Grilla False= No se dibuja, True= Se dibuja*/
    private boolean dibujarGrilla=false;
    /*Imagen que representa la imagen usada por la pieza*/
    private BufferedImage imgpieza;
    /*Afirmacion para representar una pieza validamente colocada*/
    private BufferedImage imgcaida;
    /*Variable logica para representar la caida valida de una pieza al fondo*/
    private boolean cayoPieza;
    /*Indice para elegir aleatoriamente la proxima imagen para la pieza*/
    private int indice;
    /*Variable para reiniciar la caida de la pieza o no*/
    private boolean flag=false;
    /*Variable que crea un bucle para la caida de las piezas*/
    private Timer timer;
    /*Cantidad de filas de division*/  
    private int filas;
    /*Cantidad de columnas de division*/
    private int col;    
    /*Imagen seleccionada para armar*/
    private BufferedImage imagenread;
    /*Lista con los nombres de sub imagenes obtenidas*/
    private ArrayList<String> nameimg;   
    /*Lista de nombres de subimagenes obtenidas de largo 8 para elegir una
    pieza aleatoria*/
    private ArrayList<String> sublista; 
    /*el valor para el lado de una sub imagen*/
    private int subLado;
    /*Valor para el lado de la imagen elegida*/
    private int ladoImg;
    /*Altura adicional agregada al panel*/
    private final int HPLUS = 124;
    /*Cantidad entera de sub imagenes formadas*/
    private int numimg;
    /*Imagen de fondo usada cuando se gana o se pierde*/
    private BufferedImage fondogameover;    
    /*Captura del panel al fin del juego*/
    private BufferedImage imgresult;
    /*Variable logica para el estado del juego FALSE: No gano, True: Gano*/
    private boolean win;
    /*Variable USADA como logica para cambiar el estado del juego a terminado*/
    private int gameover;
    /*Variable USADA como logica para cambiar el estado del juego a pausado*/
    private int pausado;
    /*Copia de la matriz de fondo para fines de invisibilidad al pausar*/
    private MatrizFondo matrizcopia;
    /*Copia de la pieza actual para fines de invisibilididad al pausar*/
    private BufferedImage imgpiezacopia;
    /*Contador de tiempo para el tiempo transcurrido del juego*/
    private int contador;
    /*Representa los segundos del tiempo transcurrido*/
    private int seg;
    /*Representa los minutos del tiempo transcurrido*/
    private int min;
      
    /**
     * Constructor de la clase Panel
     */
    public Panel(){
        filas=8;
        col=8;                  
        this.setPreferredSize(new Dimension(496,496+HPLUS));                  
        matriz=new MatrizFondo(this);
        imgpieza=null;
        imgresult=null;
        win=false;       
        cayoPieza=false;
        indice=-1;
        gameover=0;
        pausado=0;
        matrizcopia=null;
        imgpiezacopia=null;
        contador=0;
        timer = new Timer (1000, new ActionListener () {            
            /**
             * Repuesta al evento de timer cada 1 segundo, en este caso hacer 
             * caer una pieza, reinicar la caida, analizar el termino de juego,
             * modificar la lista sublista y la matriz
             * @param e 
             */
            @Override
            public void actionPerformed(ActionEvent e) {    
                contador = contador + timer.getDelay();               
                if (!flag){                   
                    flag=pieza.caidaPieza(imgpieza.getHeight());                    
                }
                else{
                    if (sublista.isEmpty()){                                                     
                        analizarwin();
                    }
                    else{   
                        flag=false;
                        cayoPieza=true;
                        matriz.agregarPiezaCaida(pieza);
                        modificarSubLista(indice);
                        if (sublista.size()>0)
                            crearPieza();
                    }
                }                       
                repaint();
        }});       
    }
    /**
     * Metodo para dibujar los diveros elementos en le panel; Grilla,
     * piezas que van cayendo y las piezas ya caidas.
     * @param g Tipo graphics
     */
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        /*Si existe un matriz fondo y la instruccion para dibujar grilla es true */
        if (matriz!=null && dibujarGrilla)
            matriz.dibujarGrilla(g);
        /*Si existe una imagen para la pieza actual*/
        if (imgpieza!=null)
            g.drawImage(imgpieza, (int)pieza.getPosx(),(int)pieza.getPosy(), this);
        /*Si efectivamente la pieza cayo en algun lugar*/
        if (cayoPieza)           
            try {
                dibujarCaidas(g);
            } catch (IOException ex) {}
        /*Si se termino el juego*/
        if (gameover==1){
            try {
                if (win)
                    dibujarGameover(g, "win.png");
                else
                    dibujarGameover(g, "lose.png");
            } catch (IOException ex) {}
        }
        /*Los if que siguen no corresponden a dibujar elementos, pero aproveche 
        la constante escucha del metodo paintcomponent para pausar/reanudar el juego
        instantaneamente*/
        if (pausado==1){//juego pausado
            matrizcopia=matriz;
            imgpiezacopia=imgpieza;
            pausar();
            pausado=0;
        }
        if (pausado==-1){//juego reanudado
            reanudar();
            pausado=0;
        }                   
    }
    /**
     * Metodo que una vez seleccionada la imagen para jugar la redimensiona al tamaño
     * exacto necesario para jugar, e instancia algunas variables para preparar el inicio
     * del juego. Tambien crea las divisiones de la imagen elegida.
     */  
    public void montarImgfondo(){
        imagenread = Scalr.resize(imagenread,Scalr.Method.BALANCED,Scalr.Mode.FIT_EXACT,496,496);
        nameimg=new ArrayList<>();
        sublista= new ArrayList<>();
        ladoImg = imagenread.getHeight();       
        subLado = imagenread.getHeight()/col;        
        crearDivisiones();
    }
    /**
     * Metodo que retorna el nombre de una imagen para una pieza aleatoriamente.
     * @return String Nombre de la imagen
     */
    public String elegirPieza(){      
        indice = (int) (Math.random() * sublista.size());       
        return sublista.get(indice);
    }
    /**
     * Metodo que crea la pieza y le asocia la imagen elegida en el metodo elegirPieza
     * @throws IOException
    */
    public void crearPieza(){        
        try {
            pieza= new Pieza(this,matriz,elegirPieza());
        } catch (IOException ex) {}
        pieza.setPosx(186);       
        pieza.setPosy(0);
        imgpieza=pieza.getImg();              
    }   
    /**
     * Metodo que inicia el timer
     */   
    public void run(){
        timer.start();
    }
    /**
     Metodo que detiene el timer
     */
    public void stop(){
        timer.stop();
    }
    /**
     * Metodo que reinicia el juego
     */     
    public void restartGame(){        
        flag=false;
        matriz=new MatrizFondo(this);
        crearSubLista();
        imgpieza=null;
        cayoPieza=false;
        imgresult=null;
        pausado=0;
        gameover=0;
        indice=-1;
        contador=0;
        crearPieza();               
        run();
        repaint();
    }
    /**
     * Metodo que analiza si el jugador gano o perdio y crea una imagen del panel
     * para posteriores usos.
     */
    public void analizarwin(){       
        //detener el juego y quitar la grilla si esta puesta
        stop();
        dibujarGrilla=false;       
        //Crear imagen del juego terminado
        imgresult = new BufferedImage(this.getWidth(), this.getHeight()
                ,BufferedImage.TYPE_INT_ARGB);       
        this.paint(imgresult.getGraphics());       
        imgresult = imgresult.getSubimage(0, 124, 496,496);//Cortar resto       
        //analizas si usuario gano o perdio
        int iguales=0, aux2=0;
        for (int y = 0; y < matriz.getFilas(); y++) {
            for (int x = 0; x < matriz.getColumnas(); x++) {               
                if (matriz.getMatriz()[x][y].equals(nameimg.get(aux2)))
                    iguales+=1;                   
                aux2+=1;
            }
        }
        if (iguales==numimg)           
            win=true;        
        else          
            win=false;
       
        seg = contador/1000;
        min = seg/60;
        seg = seg%60; 
        
        gameover = 1;
    }
    /**
     * Metodo que cambia el estado de la orden para mostrar la grilla en el juego.
     * @param o Variable logica usada para cambiar el valor de dibujarGrilla.
     */
    public void ordenDibujarGrilla(boolean o){       
        dibujarGrilla=o;
    }
    /**
     * Metodo que crea y guarda las divisiones de la imagen elegida
     */
    public void crearDivisiones(){       
        int aux=0,x=0,y;
        BufferedImage imgs[] = new BufferedImage[filas*col];
        /*Ciclos anidados para recorrer la cantidad de filas y columnas de la division*/
        while (x < filas) {
            y=0;
            while (y < col) {
                /*Lista de sub imagenes*/               
                imgs[aux] = new BufferedImage(subLado, subLado, BufferedImage.TYPE_3BYTE_BGR) ;           
                Graphics2D imgpart = imgs[aux++].createGraphics();                
                imgpart.drawImage(imagenread, 0, 0,subLado, subLado, 
                       subLado*y, subLado*x, subLado*y + subLado, 
                      subLado*x + subLado, null);
                y++;
            }
            x++;
        }
        /*Ciclo para guardar imagenes en el directorio del proyecto y agregar
        el nombre de cada una a la lista nameimg*/
        int i;
        for (i = 0; i < imgs.length; i++) {
            try {
                ImageIO.write(imgs[i], "jpg", new File("img" + i + ".jpg"));
            } catch (IOException ex) {}
            nameimg.add("img"+i+".jpg");                
        }
        numimg=i;        
        crearSubLista();
    }
    /**
     * Metodo que crea una sublista de tamaño 8 con nombres de imagenes
     * NOTA: Siempre creara la sublista con ls ultimos 8 elementos de nameimg, 
     * solo que despues iran modificandose a medida que las piezas caen
     */  
    public void crearSubLista(){       
        clearSublista();
        int count;               
        for (count=0; count < filas; count++){//Crear sublista                
            sublista.add(nameimg.get(nameimg.size()-1-count));                      
        }       
    }
    /**
     * Metodo que modifica la sub lista cuando la pieza cae
     * @param pos Posicion en la sublista
     */
    public void modificarSubLista(int pos){
        int aux=-1;//numero de imagen       
        for(String s: nameimg){
            try{
                if (sublista.get(pos).equals(s)){
                    aux=nameimg.indexOf(s);               
                }
            }catch(Exception ex){clearSublista();}
        }
        if (sublista.size()>0){
            if (aux-filas>=0){
                sublista.set(pos, nameimg.get(aux-filas));
            }
            else{
                sublista.remove(pos);
            }
        }
              
    }
    /**
     * Metodo que dibuja las piezas ya caidas en el tablero
     * @param g Graphics para acceder a los metodos de dibujo
     * @throws IOException No se encuentra la imagen
     */
    public void dibujarCaidas(Graphics g) throws IOException{
        
        for (int y = 0; y < matriz.getFilas(); y++) {
            for (int x = 0; x < matriz.getColumnas(); x++) {               
                if(matriz.getMatriz()[x][y]!=""){
                    String n=matriz.getMatriz()[x][y];
                    int xpos=x*subLado;
                    int ypos=(y*subLado)+HPLUS;
                    try {
                        imgcaida = ImageIO.read(new FileInputStream(new File(n)));
                    } catch (FileNotFoundException ex) {}
                    g.drawImage(imgcaida, xpos, ypos, this);
                }
            }
        }       
    }
    /**
     * Metodo que dibuja la fase de GameOver. Carga la imagen de fondo y le agrega
     * el resultado del juego (imgresult) mas la imagen original a armar (imagenread)
     * @param g Clase Graphics para acceder al metodo de drawImage
     * @param n nombre de la imagen, puede ser win.png o lose.png, dependiendo si se gana o pierde
     * @throws IOException Problema al leer la imagen.
     */
    public void dibujarGameover(Graphics g, String n) throws IOException{       
        try {
            fondogameover = ImageIO.read(new FileInputStream(new File(n)));
        } catch (FileNotFoundException ex) {}     
        toPdf();
        imgresult = Scalr.resize(imgresult,Scalr.Method.BALANCED,Scalr.Mode.FIT_EXACT,200,200);       
        imagenread = Scalr.resize(imagenread,Scalr.Method.BALANCED,Scalr.Mode.FIT_EXACT,200,200);
        g.drawImage(fondogameover, 0, 0, this);
        g.drawImage(imgresult, 148, 70, this);       
        g.drawImage(imagenread, 148, 350, this);
    }
    /**
     * Metodo que pausa el juego. Reemplaza la matriz(piezas caidas) y la pieza que constantemente 
     * se estan pintando en el panel por elementos vacios para volverlos invisibles. 
     */
    public void pausar(){                                    
        MatrizFondo matrizvacia = new MatrizFondo(this); 
        stop();
        matriz = matrizvacia;
        imgpieza=null;
        repaint();       
    }
    /**
     * Metodo que reanuda el juego exclusivamente despues que fue pausado. Regresa los valores
     * reales a la matriz(piezas caidas) y a la pieza para hacerlos aparecer.
     */
    public void reanudar(){
        matriz=matrizcopia;
        imgpieza=imgpiezacopia;
        repaint();
        run();
    }
    /**
     * Metodo que vacia completamente la sublista
     */
    public void clearSublista(){
        sublista.removeAll(sublista);
    }
    /**
     * Metodo que crea un archivo PDF y le agrega la informacion del termino del juego
     * junto con la imagen resultante del panel. Agrega informacion del usuario, especificamente
     * si gano o perdio y el tiempo transcurrido.
     * @throws Si ya existe el archivo creado se borrara para crear uno nuevo
     * @throws Exeption e Algun error en la creacion, guardado en memoria o lectura del 
     * documento
     */
    public void toPdf(){
        try{            
            if (new File("resultado.pdf").exists()){
                File pdf = new File("resultado.pdf");
                if (pdf.delete())
                    System.out.println("Archivo \""+"resultado.pdf"+ "\" "+
                            "ya existe, pero se borró correctamente.");
                else
                    throw new Error("\""+"resultado.pdf"+"\""
                            +" ya existe, pero no se pudo borrar");
            }
            Document pdfdoc = new Document();
            PdfWriter.getInstance(pdfdoc,new FileOutputStream("resultado.pdf"));                   
            ByteArrayOutputStream bas=new ByteArrayOutputStream();
            ImageIO.write(imgresult, "png", bas);
            Image img=Image.getInstance(bas.toByteArray());           
            img.setAlignment(Element.ALIGN_CENTER);            
            llenarPdf(pdfdoc, img);
            System.out.println("Toda la información respecto al resultado"
                    + " del juego se guardo en "+"\""+"resultado.pdf"+"\"");
            
        }catch(Exception e){e.printStackTrace();}
    }
    /**
     * Metodo para llenar el documento ya creado con la informacion del juego y la imagen
     * de captura del resultado del juego.
     * @param pdfdoc Documento creado en el metodo toPdf()
     * @param img Imagen capturada del resultado del juego
     * @throws Exception e Algun error en lectura, escritura de los parametros entregados
     */
    public void llenarPdf(Document pdfdoc, Image img){
        try{
            pdfdoc.open();
            pdfdoc.addTitle("Resultado Juego Tetris");
            Paragraph tablero = new Paragraph("Resultado del tablero:");
            tablero.setIndentationLeft(20);
            pdfdoc.add(tablero);
            pdfdoc.add(img);
            if (win){
                Paragraph win = new Paragraph("\n- Estado de partida: GANADA");
                win.setIndentationLeft(20);
                pdfdoc.add(win);
            }
            if (!win){
                Paragraph win = new Paragraph("\n- Estado de partida: PERDIDA");
                win.setIndentationLeft(20);
                pdfdoc.add(win);
            }
            Paragraph time = new Paragraph ("\n- Tiempo transcurrido: "
                    + ""+min+"m "+seg+"s");
            time.setIndentationLeft(20);
            pdfdoc.add(time);
            pdfdoc.close();
        }catch(Exception e){e.printStackTrace();}
    }
    /**
     * Obtener la pieza actual
     * @return Pieza actual
     */
    public Pieza getPieza() {
        return pieza;
    }
    /**
     * Reemplaza la pieza actual
     * @param pieza 
     */
    public void setPieza(Pieza pieza) {
        this.pieza = pieza;
    }
    /**
     * Obtiene la variable de tipo Timer
     * @return Timer
     */
    public Timer getTimer() {
        return timer;
    }
    /**
     * Reemplaza la imagen objetivo del juego
     * @param imagenread Nueva imagen objetivo
     */
    public void setImagenread(BufferedImage imagenread) {
        this.imagenread = imagenread;
    }
    /**
     * Reemplaza la variable pausado
     * @param pausado nuevo estado para la variable pausado
     */
    public void setPausado(int pausado) {
        this.pausado = pausado;
    }
    /**
     * Obtiene el valor del lado elegido para cada pieza
     * @return Entero valor de sublado
     */
    public int getSubLado() {
        return subLado;
    }
    /**
     * Obtiene el valor del lado de la imagen objetivo
     * @return Int valor de la variable ladoImg
     */
    public int getLadoImg() {
        return ladoImg;
    }
    /**
     * Obtiene la cantidad de filas de division de la imagen objetivo
     * @return Int cantidad de filas
     */
    public int getFilas() {
        return filas;
    }
    /**
     * Obtiene la cantidad de columndas de division de la imagen objetivo.
     * @return Int cantidad de columndas.
     */
    public int getCol() {
        return col;
    }
    /**
     * Obtiene el valor de altura adicional del panel
     * @return Int altura adicional
     */
    public int getHPLUS() {
        return HPLUS;
    }
    /**
     * Metodo que retorna el estado de la bandera de caida para la pieza
     * @return Estado logico de la variable flag
     */
    public boolean isFlag() {
        return flag;
    }
    /**
     * Metodo que retorna el contador actual del timer
     * @return contador actual
     */
    public int getContador() {
        return contador;
    }

    
    
    
    

    
    
}
