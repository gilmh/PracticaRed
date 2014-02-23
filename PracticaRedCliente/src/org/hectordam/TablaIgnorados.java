package org.hectordam;

import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class TablaIgnorados extends JTable{

	private DefaultTableModel modelo;
	private ArrayList<String> lista;
	
	public TablaIgnorados(){
		
		String [] columna = {"Ignorados"};
		modelo = new DefaultTableModel(columna, 0);
		
		setModel(modelo);
		
		lista = new ArrayList<String>();
	}
	
	public void listar(){
		
		modelo.setNumRows(0);
		
		for(String nombre: lista){
			
			String [] fila = {nombre};
			modelo.addRow(fila);
		}
	}
	
	public void insertar(String nombre){
		lista.add(nombre);
	}
	
	public void eliminar(String nombre){
		
		for(int i = lista.size()-1; i >= 0; i--){
			if(lista.get(i).equalsIgnoreCase(nombre)){
				lista.remove(i);
			}
		}
	}

	public ArrayList<String> getLista() {
		return lista;
	}

	public void setLista(ArrayList<String> lista) {
		this.lista = lista;
	}

	public DefaultTableModel getModelo() {
		return modelo;
	}

	public void setModelo(DefaultTableModel modelo) {
		this.modelo = modelo;
	}
	
	
}
