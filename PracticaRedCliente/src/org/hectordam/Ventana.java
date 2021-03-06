package org.hectordam;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;

import javax.swing.JTextArea;

import java.awt.Dimension;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.hectordam.JConectar.Accion;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Ventana {

	private Socket socket;
	private PrintWriter salida;
	private BufferedReader entrada;
	private boolean conectado;
	private static final int PUERTO = 7000;
	
	private JFrame frame;
	private JMenuBar menuBar;
	private JMenu mnChat;
	private JMenuItem mntmConectar;
	private JMenuItem mntmDesconectar;
	private JMenuItem mntmSalir;
	private JScrollPane spChat;
	private JTextArea taRecibir;
	private JPanel panelInferior;
	private JComboBox comboBox;
	private JButton btnEnviar;
	private JScrollPane spTexto;
	private JScrollPane spEscribiendo;
	private JTextArea taEnviar;
	private JPanel panel;
	private JButton btnEliminar;
	private JPanel panel_1;
	private JButton btnIgnorar;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;
	private TablaIgnorados tablaIgnorados;
	private TablaUsuarios tablaUsuarios;
	private TablaEscritura tablaEscritura;
	
	private ArrayList<String> lista;
	private boolean ignorado;
	private boolean existe;
	

	private void conectarServidor(String servidor) {
		
		try {
			socket = new Socket(servidor, PUERTO);
			salida = new PrintWriter(socket.getOutputStream(), true);
			entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			conectado = true;
			
			Thread hiloRecibir = new Thread(new Runnable() {
				@Override
				public void run() {
					while (conectado) {
						
						if (socket.isClosed()) {
							conectado = false;
							break;
						}
						
						try {
							String mensaje = entrada.readLine();
							if (mensaje == null)
								continue;
							
							int indice = 0;
							existe = false;
							
							if (mensaje.startsWith("/server")) {
								indice = mensaje.indexOf(" ");			
								taRecibir.append("** " + mensaje.substring(indice + 1) + " **\n");
							}
							else if (mensaje.startsWith("/ping")){
								salida.println("/pong");
							}
							else if (mensaje.startsWith("/write")){
								indice = mensaje.indexOf(" ");
								for(String nombre: tablaEscritura.getLista()){
									if(mensaje.substring(indice + 1).equalsIgnoreCase(nombre)){
										existe = true;
									}
								}
								if(!existe){
									tablaEscritura.insertar(mensaje.substring(indice + 1));
								}
							}
							else if (mensaje.startsWith("/nowrite")){
								indice = mensaje.indexOf(" ");
								for(int i = 0; i < tablaEscritura.getLista().size(); i++){
									if(mensaje.substring(indice + 1).equalsIgnoreCase(tablaEscritura.getLista().get(i))){
										tablaEscritura.eliminar(tablaEscritura.getLista().get(i));
									}
								}
							}
							else if (mensaje.startsWith("/nicks")) {
								String[] nicks = mensaje.split(",");
								lista.clear();
								for (int i = 1; i < nicks.length; i++) {
									lista.add(nicks[i]);
								}
								tablaUsuarios.listar(lista);
								listarCombo();
							}
							else if (mensaje.startsWith("/users")) {
								indice = mensaje.indexOf(" ", 7);
								String nick = mensaje.substring(7, indice);
								for(String nombre: tablaIgnorados.getLista()){
									if(nombre.equalsIgnoreCase(nick)){
										ignorado = true;
									}
								}
								if(!ignorado){
									taRecibir.append(nick + ": ");
									taRecibir.append(mensaje.substring(indice + 1) + "\n");
								}
								ignorado = false;
							}
							
							
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			});
			hiloRecibir.start();
			
		} catch (UnknownHostException uhe) {
			uhe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	private void listarCombo(){
		
		comboBox.removeAllItems();
		
		for(String nick: lista){
			comboBox.addItem(nick);
		}
	}
	
	private void enviarMensaje() {
		
		String mensaje = taEnviar.getText();
		
		if(comboBox.getSelectedItem() == null){
			salida.println(mensaje);
			taEnviar.setText("");
		}
		else{
			salida.println(mensaje + "-,-," + comboBox.getSelectedItem());
			taEnviar.setText("");
			salida.println("/nowrite " + comboBox.getSelectedItem());
		}
		
	}
	
	private void escribiendo(){
		
		if(comboBox.getSelectedItem() == null){
			return;
		}
		
		if(!taEnviar.getText().equalsIgnoreCase("")){
			//salida.println("/nowrite " + comboBox.getSelectedItem());
			salida.println("/write " + comboBox.getSelectedItem());
		}
		
	}

	private void desconectar() {
		try {
			salida.println("/quit");
			conectado = false;
			socket.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	private void inicializar(){
		
		lista = new ArrayList<String>();
		
		Thread hilo = new Thread(new Runnable() {
			@Override
			public void run() {
				
				try {
					InetAddress equipoRemoto = InetAddress.getByName("localhost");
					
					while(true){
						
						try {
							if (equipoRemoto.isReachable(3000))
								mntmConectar.setEnabled(true);
							else
								mntmConectar.setEnabled(false);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
		});
		hilo.start();
	}
	
	private void ignorarUsuario(){
		
		tablaIgnorados.insertar(lista.get(tablaUsuarios.getSelectedRow()));
		tablaIgnorados.listar();
	}
	
	private void eliminarIgnorado(){
		tablaIgnorados.eliminar(lista.get(tablaUsuarios.getSelectedRow()));
		tablaIgnorados.listar();
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
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
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				try {
					if(socket != null){
						socket.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.exit(0);
			}
		});
		frame.setBounds(100, 100, 530, 392);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		spChat = new JScrollPane();
		frame.getContentPane().add(spChat, BorderLayout.CENTER);
		
		taRecibir = new JTextArea();
		spChat.setViewportView(taRecibir);
		
		panelInferior = new JPanel();
		panelInferior.setPreferredSize(new Dimension(10, 70));
		frame.getContentPane().add(panelInferior, BorderLayout.SOUTH);
		
		comboBox = new JComboBox();
		
		btnEnviar = new JButton("Enviar");
		btnEnviar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				enviarMensaje();
			}
		});
		
		spTexto = new JScrollPane();
		
		spEscribiendo = new JScrollPane();
		GroupLayout gl_panelInferior = new GroupLayout(panelInferior);
		gl_panelInferior.setHorizontalGroup(
			gl_panelInferior.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelInferior.createSequentialGroup()
					.addGroup(gl_panelInferior.createParallelGroup(Alignment.LEADING, false)
						.addComponent(btnEnviar, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
						.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(spTexto, GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(spEscribiendo, GroupLayout.PREFERRED_SIZE, 102, GroupLayout.PREFERRED_SIZE))
		);
		gl_panelInferior.setVerticalGroup(
			gl_panelInferior.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelInferior.createSequentialGroup()
					.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnEnviar, GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE))
				.addComponent(spEscribiendo, GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
				.addComponent(spTexto, GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
		);
		
		tablaEscritura = new TablaEscritura();
		spEscribiendo.setViewportView(tablaEscritura);
		
		taEnviar = new JTextArea();
		taEnviar.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				escribiendo();
			}
		});
		spTexto.setViewportView(taEnviar);
		panelInferior.setLayout(gl_panelInferior);
		
		panel = new JPanel();
		panel.setPreferredSize(new Dimension(100, 10));
		frame.getContentPane().add(panel, BorderLayout.EAST);
		panel.setLayout(new BorderLayout(0, 0));
		
		btnEliminar = new JButton("Eliminar");
		btnEliminar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eliminarIgnorado();
			}
		});
		panel.add(btnEliminar, BorderLayout.NORTH);
		
		scrollPane_1 = new JScrollPane();
		panel.add(scrollPane_1, BorderLayout.CENTER);
		
		tablaIgnorados = new TablaIgnorados();
		scrollPane_1.setViewportView(tablaIgnorados);
		
		panel_1 = new JPanel();
		panel_1.setPreferredSize(new Dimension(100, 10));
		frame.getContentPane().add(panel_1, BorderLayout.WEST);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		btnIgnorar = new JButton("Ignorar");
		btnIgnorar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ignorarUsuario();
			}
		});
		panel_1.add(btnIgnorar, BorderLayout.NORTH);
		
		scrollPane = new JScrollPane();
		panel_1.add(scrollPane, BorderLayout.CENTER);
		
		tablaUsuarios = new TablaUsuarios();
		scrollPane.setViewportView(tablaUsuarios);
		
		menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		mnChat = new JMenu("Chat");
		menuBar.add(mnChat);
		
		mntmConectar = new JMenuItem("Conectar");
		mntmConectar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				JConectar jConecta = new JConectar();
				
				if (jConecta.mostrarDialogo() == Accion.CANCELAR)
					return;
				
				String servidor = jConecta.getServidor();
				conectarServidor(servidor);
			}
		});
		mnChat.add(mntmConectar);
		
		mntmDesconectar = new JMenuItem("Desconectar");
		mnChat.add(mntmDesconectar);
		
		mntmSalir = new JMenuItem("Salir");
		mntmSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				salida.println("/quit");
				
				tablaEscritura.getModelo().setNumRows(0);
				tablaIgnorados.getModelo().setNumRows(0);
				tablaUsuarios.getModelo().setNumRows(0);
				comboBox.removeAllItems();
			}
		});
		mnChat.add(mntmSalir);
	}
}
