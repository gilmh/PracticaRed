package org.hectordam;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


/**
 * Representa al servidor echo
 * @author Santiago Faci
 *
 */
public class Servidor {

	private int puerto;
	private ServerSocket socket;
	
	private ArrayList<Cliente> clientes;
	
	public Servidor(int puerto) {
		this.puerto = puerto;
		clientes = new ArrayList<Cliente>();
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

	
	public void enviarNicks() {
		
		for (Cliente cliente : clientes) {
			cliente.getSalida().println(obtenerNicks());
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
