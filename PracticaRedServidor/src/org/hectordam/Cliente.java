package org.hectordam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.gjt.mm.mysql.Driver;


public class Cliente extends Thread{
	
	private Socket socket;
	private PrintWriter salida;
	private BufferedReader entrada;
	private Servidor servidor;
	private boolean conectado = true;
	private boolean ping = true;
	private String nick;
	private String equipo;
	
	private Connection conexion;
	
	
	public Cliente(Socket socket, Servidor servidor) throws IOException {
		this.socket = socket;
		this.servidor = servidor;
		
		salida = new PrintWriter(socket.getOutputStream(), true);
		entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/basechat", "root", "");
			
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void desconectar() throws IOException, SQLException{
		
		conectado = false;
		socket.close();
		servidor.eliminarCliente(this);
		servidor.enviarNicks();
	}
	
	@Override
	public void run() {
		System.out.println("Iniciando comunicación con el cliente");
		
		//salida.println("/server Hola " + socket.getInetAddress().getHostName());
		salida.println("/server Escribe tu nick (/reg y espacio para registrarte) y enviar");
		try {
			String nick = entrada.readLine();
			int pos = 0;
			if(nick.startsWith("/reg")){
				pos = nick.indexOf(" ");
				
				String sentenciaSql = "INSERT INTO usuarios (nombre) VALUES (?)";
				PreparedStatement sentencia = conexion.prepareStatement(sentenciaSql);
				sentencia.setString(1, nick.substring(pos + 1));
				sentencia.executeUpdate();
				pos += 1;
			}
			
			setNick(nick.substring(pos));
			salida.println("/server Bienvenido " + nick.substring(pos));
			servidor.enviarNicks();
		
			Thread hiloPing = new Thread(new Runnable(){
				@Override
				public void run() {
					
					while(conectado){
						
						ping = false;
						salida.println("/ping");
						
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						if(!ping){
							try {
								desconectar();
							} catch (IOException e) {
								e.printStackTrace();
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
					}
				}
			});
			hiloPing.start();
			
			String linea = null;
			int indice = 0;
			
			while (conectado) {
				linea = entrada.readLine();
				if (linea.equals("/quit")) {
					
					salida.println("/server Saliendo . . .");
					desconectar();
					break;
				}
				else if(linea.equals("/pong")){
					ping = true;
					continue;
				}
				else if (linea.startsWith("/write")) {
					indice = linea.indexOf(" ");
					
					servidor.escribiendo(linea.substring(indice + 1), nick);
				}
				else if (linea.startsWith("/nowrite")) {
					indice = linea.indexOf(" ");
					
					servidor.noEscribir(linea.substring(indice + 1), nick);
				}
				
				String[] destinatario = linea.split("-,-,");
				
				if(destinatario.length > 1){
					
					servidor.enviarMensaje("/users " + nick + " " + destinatario[0], destinatario[destinatario.length -1]);
				}
				
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}	
	
	public PrintWriter getSalida() {
		return salida;
	}
	
	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getEquipo() {
		return equipo;
	}

	public void setEquipo(String equipo) {
		this.equipo = equipo;
	}

	public Connection getConexion() {
		return conexion;
	}

	public void setConexion(Connection conexion) {
		this.conexion = conexion;
	}
	
}
