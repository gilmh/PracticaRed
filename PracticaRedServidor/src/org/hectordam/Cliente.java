package org.hectordam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Cliente extends Thread{
	
	private Socket socket;
	private PrintWriter salida;
	private BufferedReader entrada;
	private Servidor servidor;
	private boolean conectado = true;
	private boolean ping = true;
	private String nick;
	private String equipo;
	
	
	public Cliente(Socket socket, Servidor servidor) throws IOException {
		this.socket = socket;
		this.servidor = servidor;
		
		salida = new PrintWriter(socket.getOutputStream(), true);
		entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	
	public void desconectar() throws IOException{
		
		conectado = false;
		socket.close();
		servidor.eliminarCliente(this);
		servidor.enviarNicks();
	}
	
	@Override
	public void run() {
		System.out.println("Iniciando comunicación con el cliente");
		
		// Envía algunos mensajes al cliente en cuanto éste se conecta
		salida.println("/server Hola " + socket.getInetAddress().getHostName());
		salida.println("/server Escribe tu nick y pulsa enter");
		try {
			String nick = entrada.readLine();
			setNick(nick);
			salida.println("/server Bienvenido " + nick);
			salida.println("/server Cuando escribas '/quit', se terminará la conexión");
			
		
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
							}
						}
					}
					
				}
			});
			hiloPing.start();
			
			String linea = null;
			
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
				
				String[] destinatario = linea.split("-,-,");
				
				if(destinatario.length > 1){
					
					servidor.enviarMensaje("/users " + nick + " " + destinatario[0], destinatario[destinatario.length -1]);
				}
				
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
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
	
}
