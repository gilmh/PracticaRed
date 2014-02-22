package org.hectordam;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class TablaEscritura extends JTable{

	private DefaultTableModel modelo;
	
	public TablaEscritura(){
		
		String [] columna = {"Escribiendo"};
		modelo = new DefaultTableModel(columna, 0);
		
		setModel(modelo);
	}
}
