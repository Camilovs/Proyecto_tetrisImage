package tetris;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Clase que representa la ventana de la aplicacion.
 * @author Camilo
 */
public class Ventana extends JFrame{
    
    /*Representa el panel donde se dibujaran todos los elemetos*/
    private Panel panel;
    /*Representa en el estado del boton Grilla (apretado - no apretado)*/
    boolean botonGrillaPress;
    /*Variable logica para representar el estado de pausa del juego*/
    private boolean pausado;
    
    /**
     * Constructor clase Ventana.
     */
    public Ventana(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Tetris");       
        botonGrillaPress=false;
        pausado=false;
        this.setResizable(false);
        this.setLayout(new BorderLayout());
        this.buildVentana();                   
    }  
    /**
     * Metodo que construye la ventana con sus elementos correspondientes y 
     * agrega el panel de juego.
     */
    public void buildVentana(){
        panel=new Panel();
        panel.setPieza(null);    
        //Eventos para el teclado
        this.addKeyListener(new KeyListener(){
            @Override
            public void keyTyped(KeyEvent ke) {}
            /**
             * Eventos de presionar una tecla en el teclado, movera una pieza
             * siempte y cuando exista.
             * @param ke un evento del teclado
             */
            @Override
            public void keyPressed(KeyEvent ke) {
                if (panel.getPieza()!=null && panel.getTimer().isRunning()){
                    if (ke.getKeyCode()==ke.VK_RIGHT)
                        panel.getPieza().moverDer();                                  
                    if (ke.getKeyCode()==ke.VK_LEFT)
                        panel.getPieza().moverIzq();  
                    if (ke.getKeyCode()==ke.VK_DOWN)
                        panel.getPieza().moverAbajo();
                    panel.repaint();                  
                }
                              
            }

            @Override
            public void keyReleased(KeyEvent ke) {}
                      
        });
        setContentPane(panel);
        JMenuBar barraMenu = new JMenuBar(); 
        this.setJMenuBar (barraMenu);
        JMenu juego = new JMenu("Juego"); 
        barraMenu.add(juego);
        
        /*Opcion para iniciar el juego y crear la pieza.*/
        JMenuItem jugar = new JMenuItem("Nuevo Juego"); 
        juego.add(jugar); 
        jugar.addActionListener(new ActionListener(){
            /**
             * Evento cuando se decide iniciar un nuevo juego
             * @param e ActionEvent Eventos
             */
            @Override
            public void actionPerformed(ActionEvent e) {
               
                panel.stop();
                JFileChooser chooser = new JFileChooser();
                String file=null;
                int v = chooser.showOpenDialog(panel);                                
                if (v==JFileChooser.APPROVE_OPTION){
                    file=chooser.getSelectedFile().getAbsolutePath();
                    try {
                        panel.setImagenread(ImageIO.read(new File(file)));
                        panel.montarImgfondo();
                        panel.repaint();
                        Ventana.this.pack();
                    } catch (IOException ex) {
                        System.out.println("NO se encuentra archivo");
                    }
                }
                if(file!=null)
                    panel.restartGame();
                else
                    throw new Error("No se abrio la imagen correctamente,"
                            + "intentelo nuevamente");
                                  
            }
        });
        JMenuItem restart = new JMenuItem("Reiniciar"); 
        juego.add(restart);       
        restart.addActionListener(new ActionListener(){
            /**
             * Eventos cuando se decide reiniciar el juego, siempre que se pueda.
             * @param e ActionEvent Eventos
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                if (panel.getContador()>0)
                    panel.restartGame();
                else
                    System.out.println("El Juego aun no comienza");
            }
        });
        
        JMenuItem salir = new JMenuItem("Salir"); 
        juego.add(salir);       
        salir.addActionListener(new ActionListener(){
            /**
             * Eventos cuando se decide salir del juego
             * @param e 
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
              
        JMenu opciones = new JMenu("Opciones"); 
        barraMenu.add(opciones);
                      
        JMenuItem grilla = new JMenuItem("Grilla"); 
        opciones.add(grilla);
        grilla.addActionListener(new ActionListener(){
            /**
             * Evento para mostrar/borrar la Grilla.
             * @param e ActionEvent Evento.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                if (panel.getContador()>0){
                    if (botonGrillaPress){
                        panel.ordenDibujarGrilla(false);
                        botonGrillaPress=false;
                    }
                    else{
                        panel.ordenDibujarGrilla(true);
                        botonGrillaPress=true;
                    }
                }
            }
        });
        JMenu dificult = new JMenu("Velocidad"); 
        barraMenu.add(dificult);
        
        JMenuItem aum = new JMenuItem("Aumentar"); 
        dificult.add(aum);
        aum.addActionListener(new ActionListener(){
            /**
             * Eventos cuando se decide Aumentar la velocidad del juego.
             * @param e ActionEvent Eventos
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                if (panel.getTimer().isRunning()){
                    if (panel.getTimer().getDelay()-200>0)
                        panel.getTimer().setDelay(panel.getTimer().getDelay()-200);
                    else if (panel.getTimer().getDelay()-150>0)
                        panel.getTimer().setDelay(panel.getTimer().getDelay()-150);
                    else
                        System.out.println("Maxima velocidad alcanzada");
                    
                }
                else
                    System.out.println("El juego aun no comienza");
            }
        });
        
        JMenuItem dis = new JMenuItem("Disminuir"); 
        dificult.add(dis);
        dis.addActionListener(new ActionListener(){
            /**
             * Eventos cuando se decide disminuir la velocidad de juego
             * @param e ActionEvent Eventos
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                if (panel.getTimer().isRunning())
                    panel.getTimer().setDelay(panel.getTimer().getDelay()+200);
                else
                    System.out.println("El juego aun no comienza");
            }
        });
        JMenuItem pausa = new JMenuItem("Pausar/Reanudar"); 
        dificult.add(pausa);       
        pausa.addActionListener(new ActionListener(){
            /**
             * Eventos cuando se decide Pausar o Reanudar el juego siempre que se 
             * pueda
             * @param e ActionEvent Eventos 
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                if (panel.getContador()>0){
                    if (pausado){
                        panel.setPausado(-1);
                        pausado=false;
                    }
                    else{
                        panel.setPausado(1);
                        pausado=true;
                    }
                }
                else
                    System.out.println("El juego aun no comienza");
            }
        });
        JMenu Ayuda = new JMenu("Ayuda"); 
        barraMenu.add(Ayuda);
        
        JMenuItem como = new JMenuItem("Como jugar"); 
        Ayuda.add(como);    
        como.addActionListener(new ActionListener(){
            /**
             * Evento cuando se decide leer la seccion de Como jugar.
             * @param e ActionEvent Eventos
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,"-Para iniciar juego: Juego > "
                        + "Nuevo Juego > Seleccionar imagen.\n"
                        + "-Objetivo: Armar el tetris para formar la imagen elegida.\n"
                        + "-Mover piezas: Flechas izquierda, derecha y abajo.\n"
                        + "-Aumentar velocidad de caida: Velocidad > Aumentar.\n"
                        + "-Disminuir velocidad de caida: Velocidad > Disminuir.\n"
                        + "-Pausar/Reanudar juego: Velocidad > Pausar/Reanudar.\n"
                        + "-Reiniciar juego: Juego > Reniciar.\n"
                        + "-Grilla: Opciones > Grilla.\n"
                        + "Al final del juego (ya sea se gane o se pierda) se generará\n"
                        + "un archivo pdf con toda la  información del resultado del juego.");
            }     
        });
        
        JMenuItem acerca = new JMenuItem("Acerca de"); 
        Ayuda.add(acerca);    
        acerca.addActionListener(new ActionListener(){
            /**
             * Evento cuando se decide leer Acerca del juego
             * @param e ActionEvent Eventos
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,"Juego: Tetris\n"
                        + "Autor: Camilo Villalobos\n"
                        + "Año: 2017\n"
                        + "Version: 1.0.0");
            }     
        });
        this.pack();       
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
