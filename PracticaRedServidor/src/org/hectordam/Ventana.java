package org.hectordam;

import java.awt.AWTException;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JButton;

public class Ventana {

	public static final int PUERTO = 7000;
	
	private Servidor servidor;
	private TrayIcon trayIcon;
	
	private JFrame frame;
	private JButton btnIncicar;
	private JButton btnPara;
	
	private void iniciar(){
		
		btnIncicar.setEnabled(false);
		btnPara.setEnabled(true);
		
		Cliente cliente = null;
		
		try {
			servidor.conectar();
			
			while (servidor.estaConectado()) {
				cliente = new Cliente(servidor.escuchar(), servidor);
				
				//if(servidor.existeEquipo()){
					//cliente.getSalida().println("/server Ya existe un equipo con esta direccion");
				//}
				//else{
					servidor.anadirCliente(cliente);
				
					cliente.start();
				//}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
	}
	
	private void parar(){
		
		try {
			servidor.desconectar();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		btnIncicar.setEnabled(true);
		btnPara.setEnabled(false);
		
	}
	
	private void inicializar(){
		
		servidor = new Servidor(PUERTO);
		
		trayIcon = new TrayIcon(new ImageIcon("personaje.png").getImage());
		
		// Crea un popupmenu con dos acciones
		PopupMenu popup = new PopupMenu();
		
		MenuItem iniciarItem = new MenuItem("Iniciar");
		iniciarItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				iniciar();
			}
		});
		MenuItem pararItem = new MenuItem("Parar");
		pararItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parar();
			}
		});
		popup.add(iniciarItem);
		popup.add(pararItem);
		
		// Añade el popupMenu al icono de notificación
		trayIcon.setPopupMenu(popup);
		
		// Añade el icono de notificación en la barra de notificación
        try {
			SystemTray.getSystemTray().add(trayIcon);
		} catch (AWTException awte) {
			awte.printStackTrace();
		}
		
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Ventana window = new Ventana();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Ventana() {
		initialize();
		inicializar();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 229, 81);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		btnIncicar = new JButton("Incicar");
		btnIncicar.setBounds(10, 11, 89, 23);
		frame.getContentPane().add(btnIncicar);
		
		btnPara = new JButton("Parar");
		btnPara.setBounds(109, 11, 89, 23);
		frame.getContentPane().add(btnPara);
	}
}
