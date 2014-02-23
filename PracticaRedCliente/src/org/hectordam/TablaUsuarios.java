package org.hectordam;

import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class TablaUsuarios extends JTable{

	private DefaultTableModel modelo;
	private ArrayList<String> lista;
	
	public TablaUsuarios(){
		
		String [] columna = {"Usuarios"};
		modelo = new DefaultTableModel(columna, 0);
		
		setModel(modelo);
		
	}
	
	public void listar(ArrayList<String> lista){
		modelo.setNumRows(0);
		
		this.lista = lista;
		
		for(int i = 0; i < lista.size(); i++){
			String [] fila = {lista.get(i)};
			modelo.addRow(fila);
		}
		
	}

	public DefaultTableModel getModelo() {
		return modelo;
	}

	public void setModelo(DefaultTableModel modelo) {
		this.modelo = modelo;
	}
	
	
}
