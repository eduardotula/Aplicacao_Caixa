package com.viewadmin;

import java.awt.Font;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;

import net.miginfocom.swing.MigLayout;

public class FrameFiltroData{
	private JPanel panel = new JPanel();
	private JFrame frame;
	 private boolean group = false;
	 private JRadioButton rdnAgrupar = new JRadioButton("Agrupar");
	private  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	public FrameFiltroData() {
		panel = getPanel();
		frame = getFrame();
	}
    public void startGUIFiltroEsto() {
        JOptionPane.showConfirmDialog(frame,
                        panel,
                        "Data: ",
                        JOptionPane.PLAIN_MESSAGE);
    }

    private JFrame getFrame() {
    	JFrame frame = new JFrame();
    	frame.setSize(200,100);
    	frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	frame.setResizable(false);
    	return frame;
    }
    private JPanel getPanel() {
		JLabel lblDataIni = new JLabel("Data Inicial");
		JLabel lblDataFin = new JLabel("Data FInal");
		JFormattedTextField txtDataIni = null;
		JFormattedTextField txtDataFin = null;
		try {
			MaskFormatter data = new MaskFormatter("##/##/####");
			data.setPlaceholderCharacter('_');
			txtDataIni = new JFormattedTextField(data);
			txtDataFin = new JFormattedTextField(data);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		panel.setVisible(true);
		panel.setLayout(new MigLayout("", "[78.00][]", "[][][][]"));
		panel.add(lblDataIni, "cell 0 0");
		panel.add(txtDataIni, "cell 1 0,growx");
		panel.add(lblDataFin, "cell 0 1");
		panel.add(txtDataFin, "cell 1 1,growx");
		lblDataIni.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblDataFin.setFont(new Font("Tahoma", Font.BOLD, 13));
		txtDataFin.setColumns(10);
        return panel;
    }
    public LocalDate[]  getData() {
        String txtDataIni = ((JTextField)panel.getComponent(1)).getText();
        String txtDataFin = ((JTextField)panel.getComponent(3)).getText();
      //Checa se o rdn de agrupar esta presente
      if(panel.getComponentCount() == 5) {
    	  group = ((JRadioButton)(panel.getComponent(4))).isSelected();
      }
      try {
          LocalDate[] localD = new LocalDate[] {LocalDate.parse(txtDataIni,
        		  formatter), LocalDate.parse(txtDataFin, formatter)};
          return localD;
      }catch (Exception e) {
    	  e.printStackTrace();
    	  JOptionPane.showMessageDialog(null, "Data Inv�lida");
      }
      return null;
    }
    public boolean getGroup() {
    	return group;
    }
    public void setGroupBtn(boolean b) {
    	if(b) {
    		panel.add(rdnAgrupar, "cell 0 2 2 1,alignx center");
    	}
    }

}
