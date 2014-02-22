package org.hectordam;

import java.io.IOException;


public class Principal {
	
	public static final int PUERTO = 7000;

	public static void main(String[] args) {
		
		Servidor servidor = new Servidor(PUERTO);
		Cliente cliente = null;
		
		try {
			servidor.conectar();
			
			while (servidor.estaConectado()) {
				cliente = new Cliente(servidor.escuchar(), servidor);
				
				if(servidor.existeEquipo()){
					cliente.getSalida().println("/server Ya existe un equipo con esta direccion");
				}
				else{
					servidor.anadirCliente(cliente);
				
					cliente.start();
				}
				
				
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
