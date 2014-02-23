package org.hectordam;

import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class TablaEscritura extends JTable{

	private DefaultTableModel modelo;
	private ArrayList<String> lista;
	
	public TablaEscritura(){
		
		String [] columna = {"Escribiendo"};
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
		listar();
	}
	
	public void eliminar(String nombre){
		lista.remove(nombre);
		/*
		for(int i = lista.size() - 1; i >= 0; i--){
			if(lista.get(i).equalsIgnoreCase(nombre)){
				lista.remove(i);
			}
		}
		*/
		listar();
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
