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
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;

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
							
							if (mensaje.startsWith("/server")) {
								indice = mensaje.indexOf(" ");			
								taRecibir.append("** " + mensaje.substring(indice + 1) + " **\n");
							}
							else if (mensaje.startsWith("/ping")){
								salida.println("/pong");
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
		}
		else{
			salida.println(mensaje + "-,-," + comboBox.getSelectedItem());
		}
		
		taEnviar.setText("");
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
		frame.setBounds(100, 100, 530, 392);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
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
		mnChat.add(mntmSalir);
	}
}
