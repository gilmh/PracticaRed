package org.hectordam;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;




/**
 * Representa al servidor echo
 * @author Santiago Faci
 *
 */
public class Servidor {

	private int puerto;
	private ServerSocket socket;
	
	private ArrayList<Cliente> clientes;
	private ArrayList<String> registrados;
	
	public static ObjectContainer db;
	//private Connection conexion;
	
	public Servidor(int puerto) {
		this.puerto = puerto;
		clientes = new ArrayList<Cliente>();
		registrados = new ArrayList<String>();
		
		db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), "usuarios.db4o");
		/*
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
		*/
	}
	
	public void anadirCliente(Cliente cliente) {
		cliente.setEquipo(socket.getInetAddress().getHostName());
		clientes.add(cliente);
	}
	
	public void eliminarCliente(Cliente cliente) {
		clientes.remove(cliente);
	}
	
	public void enviarMensaje(String mensaje, String destino) {
		
		for (Cliente cliente : clientes) {
			if(cliente.getNick().equalsIgnoreCase(destino)){
				cliente.getSalida().println(mensaje);
			}
		}
	}
	
	public void escribiendo(String destino, String origen){
		for (Cliente cliente : clientes) {
			if(cliente.getNick().equalsIgnoreCase(destino)){
				cliente.getSalida().println("/write " + origen);
			}
		}
	}
	
	public void noEscribir(String destino, String origen){
		
		for (Cliente cliente : clientes) {
			if(cliente.getNick().equalsIgnoreCase(destino)){
				cliente.getSalida().println("/nowrite " + origen);
				System.out.println(origen);
			}
		}
	}
	
	public void enviarNicks() throws SQLException {
		
		List<Usuario> registro = Servidor.db.query(Usuario.class);
		registrados.clear();
		
		for(Usuario usuario: registro){
			registrados.add(usuario.getNick());
		}
		
		String nicks = "/nicks,";
		
		for (Cliente cliente : clientes) {
			nicks += cliente.getNick() + ",";
			
			for(int i = registrados.size()-1; i >= 0; i--){
				if(cliente.getNick().equalsIgnoreCase(registrados.get(i))){
					registrados.remove(i);
				}
			}
		}
		for (int i = 0; i < registrados.size(); i++) {
			nicks += "#" + registrados.get(i) + ",";			
		}
		
		for (Cliente cliente : clientes) {
			cliente.getSalida().println(nicks);			
		}
	}
	
	public String obtenerNicks() {
	
		String nicks = "/nicks,";
		
		for (Cliente cliente : clientes) {
			nicks += cliente.getNick() + ",";
		}
		
		return nicks;
	}
	
	public boolean existeEquipo(){
		
		for(Cliente cliente: clientes){
			if(cliente.getEquipo().equalsIgnoreCase(socket.getInetAddress().getHostName())){
				return true;
			}
		}
		
		return false;
	}
	
	public boolean estaConectado() {
		return !socket.isClosed();
	}
	
	public void conectar() throws IOException {
		socket = new ServerSocket(puerto);
	}
	
	public void desconectar() throws IOException {
		socket.close();
	}
	
	public Socket escuchar() throws IOException {
		return socket.accept();
	}
}
