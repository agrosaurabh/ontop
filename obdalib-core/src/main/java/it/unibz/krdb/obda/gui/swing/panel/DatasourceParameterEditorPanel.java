/***
 * Copyright (c) 2008, Mariano Rodriguez-Muro. All rights reserved.
 * 
 * The OBDA-API is licensed under the terms of the Lesser General Public License
 * v.3 (see OBDAAPI_LICENSE.txt for details). The components of this work
 * include:
 * 
 * a) The OBDA-API developed by the author and licensed under the LGPL; and, b)
 * third-party components licensed under terms that may be different from those
 * of the LGPL. Information about such licenses can be found in the file named
 * OBDAAPI_3DPARTY-LICENSES.txt.
 */

package it.unibz.krdb.obda.gui.swing.panel;

import it.unibz.krdb.obda.gui.swing.utils.DatasourceSelectorListener;
import it.unibz.krdb.obda.model.DataSource;
import it.unibz.krdb.obda.model.DatasourcesController;
import it.unibz.krdb.obda.model.impl.RDBMSourceParameterConstants;

import javax.swing.JOptionPane;


/**
 * 
 * @author mariano
 */
public class DatasourceParameterEditorPanel extends javax.swing.JPanel implements DatasourceSelectorListener {

	DataSource						currentDS			= null;

	/**
	 * 
	 */
	private static final long		serialVersionUID	= 1L;

	private DatasourcesController	dscontroller		= null;

	/** Creates new form DatasourceParameterEditorPanel */
	public DatasourceParameterEditorPanel(DatasourcesController ds) {
		this.dscontroller = ds;
		initComponents();
		init();
	}

	public void setDatasourcesController(DatasourcesController dscontroller) {
		this.dscontroller = dscontroller;
		fieldDBDriver.setText("");
		fieldDBPassword.setText("");
		fieldDBUser.setText("");
		fieldURL.setText("");
		//TODO here we are missing some fields.
	}

	private void init(){
		
		labelDSType.setText("");
		labelMapType.setText("");
		fieldURL.setEnabled(false);
		fieldDBUser.setEnabled(false);
		fieldDBPassword.setEnabled(false);
		fieldDBDriver.setEnabled(false);
	}
	
	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed"
	// desc=" Generated Code ">//GEN-BEGIN:initComponents
	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		panelDatasourceParametersBean = new javax.swing.JPanel();
		panelDatasourceInfo = new javax.swing.JPanel();
		labelHeaderDatasourceURI = new javax.swing.JLabel();
		labelDatasource = new javax.swing.JLabel();
		labelHeaderDatasourceType = new javax.swing.JLabel();
		labelDSType = new javax.swing.JLabel();
		labelHeaderMappingsType = new javax.swing.JLabel();
		labelMapType = new javax.swing.JLabel();
		jLabelPlaceholder2 = new javax.swing.JLabel();
		panelDataSourceEditor = new javax.swing.JPanel();
		labelURL = new javax.swing.JLabel();
		fieldURL = new javax.swing.JTextField();
		labelUsername = new javax.swing.JLabel();
		fieldDBUser = new javax.swing.JTextField();
		labelPassword = new javax.swing.JLabel();
		fieldDBPassword = new javax.swing.JTextField();
		labelDriver = new javax.swing.JLabel();
		fieldDBDriver = new javax.swing.JTextField();
		jLabelSpaceholder = new javax.swing.JLabel();
		jLabelPadding = new javax.swing.JLabel();
		jLabelPlaceholder3 = new javax.swing.JLabel();

		setLayout(new java.awt.GridBagLayout());

		setMinimumSize(new java.awt.Dimension(210, 200));
		setPreferredSize(new java.awt.Dimension(210, 235));
		panelDatasourceParametersBean.setLayout(new java.awt.GridBagLayout());

		panelDatasourceParametersBean.setBorder(javax.swing.BorderFactory.createTitledBorder("Datasource Settings"));
		panelDatasourceParametersBean.setAutoscrolls(true);
		panelDatasourceParametersBean.setMinimumSize(new java.awt.Dimension(200, 230));
		panelDatasourceParametersBean.setPreferredSize(new java.awt.Dimension(200, 240));
		panelDatasourceInfo.setLayout(new java.awt.GridBagLayout());

		panelDatasourceInfo.setAutoscrolls(true);
		panelDatasourceInfo.setMaximumSize(new java.awt.Dimension(32767, 23));
		panelDatasourceInfo.setMinimumSize(new java.awt.Dimension(100, 75));
		panelDatasourceInfo.setPreferredSize(new java.awt.Dimension(140, 75));
		labelHeaderDatasourceURI.setBackground(new java.awt.Color(153, 153, 153));
		labelHeaderDatasourceURI.setFont(new java.awt.Font("Arial", 1, 11));
		labelHeaderDatasourceURI.setForeground(new java.awt.Color(153, 153, 153));
		labelHeaderDatasourceURI.setText("For datasource:");
		labelHeaderDatasourceURI.setFocusable(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 10;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
		panelDatasourceInfo.add(labelHeaderDatasourceURI, gridBagConstraints);

		labelDatasource.setFont(new java.awt.Font("Arial", 0, 11));
		labelDatasource.setFocusable(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 10;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		panelDatasourceInfo.add(labelDatasource, gridBagConstraints);

		labelHeaderDatasourceType.setBackground(new java.awt.Color(153, 153, 153));
		labelHeaderDatasourceType.setFont(new java.awt.Font("Arial", 1, 11));
		labelHeaderDatasourceType.setForeground(new java.awt.Color(153, 153, 153));
		labelHeaderDatasourceType.setText("Type:");
		labelHeaderDatasourceType.setFocusable(false);
		labelHeaderDatasourceType.setMaximumSize(new java.awt.Dimension(119, 14));
		labelHeaderDatasourceType.setMinimumSize(new java.awt.Dimension(119, 14));
		labelHeaderDatasourceType.setPreferredSize(new java.awt.Dimension(119, 14));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 10;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
		panelDatasourceInfo.add(labelHeaderDatasourceType, gridBagConstraints);

		labelDSType.setFont(new java.awt.Font("Arial", 0, 11));
		labelDSType.setText("RDBMS");
		labelDSType.setFocusable(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 10;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		panelDatasourceInfo.add(labelDSType, gridBagConstraints);

		labelHeaderMappingsType.setBackground(new java.awt.Color(153, 153, 153));
		labelHeaderMappingsType.setFont(new java.awt.Font("Arial", 1, 11));
		labelHeaderMappingsType.setForeground(new java.awt.Color(153, 153, 153));
		labelHeaderMappingsType.setText("Mapping Type:");
		labelHeaderMappingsType.setFocusable(false);
		labelHeaderMappingsType.setMaximumSize(new java.awt.Dimension(119, 14));
		labelHeaderMappingsType.setMinimumSize(new java.awt.Dimension(119, 14));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 10;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
		panelDatasourceInfo.add(labelHeaderMappingsType, gridBagConstraints);

		labelMapType.setFont(new java.awt.Font("Arial", 0, 11));
		labelMapType.setText("Direct Mapping");
		labelMapType.setFocusable(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 10;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		panelDatasourceInfo.add(labelMapType, gridBagConstraints);

		jLabelPlaceholder2.setPreferredSize(new java.awt.Dimension(1, 1));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weighty = 1.0;
		panelDatasourceInfo.add(jLabelPlaceholder2, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		panelDatasourceParametersBean.add(panelDatasourceInfo, gridBagConstraints);

		panelDataSourceEditor.setLayout(new java.awt.GridBagLayout());

		panelDataSourceEditor.setAlignmentX(5.0F);
		panelDataSourceEditor.setAlignmentY(5.0F);
		panelDataSourceEditor.setAutoscrolls(true);
		panelDataSourceEditor.setFocusTraversalPolicyProvider(true);
		panelDataSourceEditor.setMaximumSize(new java.awt.Dimension(6404444, 34452345));
		panelDataSourceEditor.setMinimumSize(new java.awt.Dimension(220, 120));
		panelDataSourceEditor.setPreferredSize(new java.awt.Dimension(220, 130));
		labelURL.setFont(new java.awt.Font("Arial", 1, 11));
		labelURL.setForeground(new java.awt.Color(153, 153, 153));
		labelURL.setText("  JDBC URL:");
		labelURL.setFocusable(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		panelDataSourceEditor.add(labelURL, gridBagConstraints);

		fieldURL.setMaximumSize(new java.awt.Dimension(25, 2147483647));
		fieldURL.setMinimumSize(new java.awt.Dimension(180, 19));
		fieldURL.setName("somename");
		fieldURL.setNextFocusableComponent(fieldDBUser);
		fieldURL.setPreferredSize(new java.awt.Dimension(180, 19));
		fieldURL.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				fieldChangeHandler(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 0);
		panelDataSourceEditor.add(fieldURL, gridBagConstraints);

		labelUsername.setFont(new java.awt.Font("Arial", 1, 11));
		labelUsername.setForeground(new java.awt.Color(153, 153, 153));
		labelUsername.setText("  Database Username:");
		labelUsername.setFocusable(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		panelDataSourceEditor.add(labelUsername, gridBagConstraints);

		fieldDBUser.setFocusTraversalPolicy(getFocusTraversalPolicy());
		fieldDBUser.setMaximumSize(new java.awt.Dimension(25, 2147483647));
		fieldDBUser.setMinimumSize(new java.awt.Dimension(180, 19));
		fieldDBUser.setName("somename");
		fieldDBUser.setNextFocusableComponent(fieldDBPassword);
		fieldDBUser.setPreferredSize(new java.awt.Dimension(180, 19));
		fieldDBUser.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				fieldChangeHandler(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 0);
		panelDataSourceEditor.add(fieldDBUser, gridBagConstraints);

		labelPassword.setFont(new java.awt.Font("Arial", 1, 11));
		labelPassword.setForeground(new java.awt.Color(153, 153, 153));
		labelPassword.setText("  Database Password:");
		labelPassword.setFocusable(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		panelDataSourceEditor.add(labelPassword, gridBagConstraints);

		fieldDBPassword.setMaximumSize(new java.awt.Dimension(25, 2147483647));
		fieldDBPassword.setMinimumSize(new java.awt.Dimension(180, 19));
		fieldDBPassword.setName("somename");
		fieldDBPassword.setNextFocusableComponent(fieldDBDriver);
		fieldDBPassword.setPreferredSize(new java.awt.Dimension(180, 19));
		fieldDBPassword.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				fieldChangeHandler(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 0);
		panelDataSourceEditor.add(fieldDBPassword, gridBagConstraints);

		labelDriver.setFont(new java.awt.Font("Arial", 1, 11));
		labelDriver.setForeground(new java.awt.Color(153, 153, 153));
		labelDriver.setText("  JDBC Driver:");
		labelDriver.setFocusable(false);
		labelDriver.setMaximumSize(new java.awt.Dimension(120, 14));
		labelDriver.setMinimumSize(new java.awt.Dimension(120, 14));
		labelDriver.setPreferredSize(new java.awt.Dimension(120, 14));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		panelDataSourceEditor.add(labelDriver, gridBagConstraints);

		fieldDBDriver.setMaximumSize(new java.awt.Dimension(25, 2147483647));
		fieldDBDriver.setMinimumSize(new java.awt.Dimension(180, 19));
		fieldDBDriver.setName("somename");
		fieldDBDriver.setNextFocusableComponent(fieldURL);
		fieldDBDriver.setPreferredSize(new java.awt.Dimension(180, 19));
		fieldDBDriver.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				fieldChangeHandler(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 0);
		panelDataSourceEditor.add(fieldDBDriver, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		panelDataSourceEditor.add(jLabelSpaceholder, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		panelDatasourceParametersBean.add(panelDataSourceEditor, gridBagConstraints);

		jLabelPadding.setPreferredSize(new java.awt.Dimension(1, 10));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		panelDatasourceParametersBean.add(jLabelPadding, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		add(panelDatasourceParametersBean, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(jLabelPlaceholder3, gridBagConstraints);

	}// </editor-fold>//GEN-END:initComponents

	private void fieldChangeHandler(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_fieldChangeHandler

		if (currentDS == null) {
			JOptionPane.showMessageDialog(this, "Select a data source first");
			return;
		}
		// currentsrc.setUri(fieldURI.getText());
		currentDS.setParameter(RDBMSourceParameterConstants.DATABASE_USERNAME, fieldDBUser.getText());
		currentDS.setParameter(RDBMSourceParameterConstants.DATABASE_PASSWORD, fieldDBPassword.getText());
		currentDS.setParameter(RDBMSourceParameterConstants.DATABASE_DRIVER, fieldDBDriver.getText());
		currentDS.setParameter(RDBMSourceParameterConstants.DATABASE_URL, fieldURL.getText());
		// currentDS.setParameter(RDBMSsourceParameterConstants.ONTOLOGY_URI,
		// apic.getCurrentOntologyURI().toString());
		dscontroller.fireParametersUpdated();

		return;
	}// GEN-LAST:event_fieldChangeHandler

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JTextField	fieldDBDriver;
	private javax.swing.JTextField	fieldDBPassword;
	private javax.swing.JTextField	fieldDBUser;
	private javax.swing.JTextField	fieldURL;
	private javax.swing.JLabel		jLabelPadding;
	private javax.swing.JLabel		jLabelPlaceholder2;
	private javax.swing.JLabel		jLabelPlaceholder3;
	private javax.swing.JLabel		jLabelSpaceholder;
	private javax.swing.JLabel		labelDSType;
	private javax.swing.JLabel		labelDatasource;
	private javax.swing.JLabel		labelDriver;
	private javax.swing.JLabel		labelHeaderDatasourceType;
	private javax.swing.JLabel		labelHeaderDatasourceURI;
	private javax.swing.JLabel		labelHeaderMappingsType;
	private javax.swing.JLabel		labelMapType;
	private javax.swing.JLabel		labelPassword;
	private javax.swing.JLabel		labelURL;
	private javax.swing.JLabel		labelUsername;
	private javax.swing.JPanel		panelDataSourceEditor;
	private javax.swing.JPanel		panelDatasourceInfo;
	private javax.swing.JPanel		panelDatasourceParametersBean;

	// End of variables declaration//GEN-END:variables

	private void currentDatasourceChange(DataSource previousdatasource, DataSource currentsource) {

		if (currentsource == null) {
			labelDSType.setText("");
			labelMapType.setText("");
			labelDatasource.setText("");
			fieldDBDriver.setText("");
			fieldDBUser.setText("");
			fieldDBPassword.setText("");
			fieldURL.setText("");
			labelDSType.setEnabled(false);
			labelMapType.setEnabled(false);
			labelDatasource.setEnabled(false);
			fieldDBDriver.setEnabled(false);
			fieldDBUser.setEnabled(false);
			fieldDBPassword.setEnabled(false);
			fieldURL.setEnabled(false);
			currentDS = null;
		} else {

			/*******************************************************************
			 * Updating the GUI fields with the sources info
			 * 
			 */
			labelDSType.setText("RDBMS");
			labelMapType.setText("OBDAMappings");
			labelDatasource.setText(currentsource.getSourceID().toString());
			fieldDBDriver.setText(currentsource.getParameter(RDBMSourceParameterConstants.DATABASE_DRIVER));
			fieldDBUser.setText(currentsource.getParameter(RDBMSourceParameterConstants.DATABASE_USERNAME));
			fieldDBPassword.setText(currentsource.getParameter(RDBMSourceParameterConstants.DATABASE_PASSWORD));
			fieldURL.setText(currentsource.getParameter(RDBMSourceParameterConstants.DATABASE_URL));
			labelDSType.setEnabled(true);
			labelMapType.setEnabled(true);
			labelDatasource.setEnabled(true);
			fieldDBDriver.setEnabled(true);
			fieldDBUser.setEnabled(true);
			fieldDBPassword.setEnabled(true);
			fieldURL.setEnabled(true);
			currentDS = currentsource;
		}
	}

	@Override
	public void datasourceChanged(DataSource oldSource, DataSource newSource) {
		currentDatasourceChange(oldSource, newSource);

	}

}
