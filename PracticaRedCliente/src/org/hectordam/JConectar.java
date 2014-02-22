package org.hectordam;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class JConectar extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JLabel lblPuerto;
	private JTextField textField;
	
	private String servidor;
	private Accion accion;
	
	public enum Accion {
		ACEPTAR, CANCELAR;
	}
	
	private void aceptar(){
		
		accion = Accion.ACEPTAR;
		servidor = textField.getText();
		setVisible(false);
	}
	
	private void cancelar(){
		
		accion = Accion.CANCELAR;
		setVisible(false);
	}
	
	public Accion mostrarDialogo() {
		
		setVisible(true);
		
		return accion;
	}

	/**
	 * Create the dialog.
	 */
	public JConectar() {
		setUndecorated(true);
		setModal(true);
		setBounds(100, 100, 226, 75);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		lblPuerto = new JLabel("Servidor");
		lblPuerto.setBounds(10, 11, 46, 14);
		contentPanel.add(lblPuerto);
		
		textField = new JTextField();
		textField.setBounds(66, 8, 150, 20);
		contentPanel.add(textField);
		textField.setColumns(10);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						aceptar();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cancelar();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	public String getServidor() {
		return servidor;
	}

	public void setServidor(String servidor) {
		this.servidor = servidor;
	}
	
	
}
