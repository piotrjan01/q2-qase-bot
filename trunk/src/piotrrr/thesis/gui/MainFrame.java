/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MainFrame.java
 *
 * Created on 2010-04-25, 19:40:25
 */

package piotrrr.thesis.gui;

import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JTextArea;

import piotrrr.thesis.bots.StartBots;
import piotrrr.thesis.bots.simplebot.SimpleBot;
import piotrrr.thesis.common.CommFun;
import piotrrr.thesis.common.GameObject;
import piotrrr.thesis.common.jobs.DebugStepJob;
import piotrrr.thesis.common.jobs.HitsReporter;
import piotrrr.thesis.tools.Dbg;

/**
 *
 * @author piotrrr
 */
public class MainFrame extends javax.swing.JFrame {

    /** Creates new form MainFrame */
    public MainFrame() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        infoButtonGroup = new javax.swing.ButtonGroup();
        panelRunBots = new javax.swing.JPanel();
        connectDebugedButton = new javax.swing.JButton();
        nrBotsField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        connectOthersButton = new javax.swing.JButton();
        discDbgButton = new javax.swing.JButton();
        discAnotherBotsButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        pauseToggleButton = new javax.swing.JToggleButton();
        pauseAnotherBotsToggle = new javax.swing.JToggleButton();
        jTextField1 = new javax.swing.JTextField();
        sendCommandToDBGButton = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        sendCommandToAnothersButton = new javax.swing.JButton();
        stepSizeTextField2 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        stepButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        reqListScrollPane2 = new javax.swing.JScrollPane();
        reqList = new javax.swing.JList();
        jLabel4 = new javax.swing.JLabel();
        fullInfoScrollPane3 = new javax.swing.JScrollPane();
        fullInfo = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        allEntsRadioButton1 = new javax.swing.JRadioButton();
        visibleWaypointsRadioButton2 = new javax.swing.JRadioButton();
        navPlanRadioButton = new javax.swing.JRadioButton();
        visibleEntsRadioButton2 = new javax.swing.JRadioButton();
        seenEntsBuffRadioButton1 = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        distanceLabel = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        botStateInfoTextArea1 = new javax.swing.JTextArea();
        jPanel5 = new javax.swing.JPanel();
        messagesScrollPane1 = new javax.swing.JScrollPane();
        messages = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Bot Debugging");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        panelRunBots.setBorder(javax.swing.BorderFactory.createTitledBorder("Run bots"));

        connectDebugedButton.setText("Connect debuged bot");
        connectDebugedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectDebugedButtonActionPerformed(evt);
            }
        });

        nrBotsField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        nrBotsField.setText("2");

        jLabel1.setText("Connect");

        jLabel2.setText("other bots");

        connectOthersButton.setText("Connect them!");
        connectOthersButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectOthersButtonActionPerformed(evt);
            }
        });

        discDbgButton.setText("disconnect dbg bot");
        discDbgButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                discDbgButtonActionPerformed(evt);
            }
        });

        discAnotherBotsButton.setText("disconnect other bots");
        discAnotherBotsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                discAnotherBotsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelRunBotsLayout = new javax.swing.GroupLayout(panelRunBots);
        panelRunBots.setLayout(panelRunBotsLayout);
        panelRunBotsLayout.setHorizontalGroup(
            panelRunBotsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRunBotsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelRunBotsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(connectDebugedButton)
                    .addGroup(panelRunBotsLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(nrBotsField, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(connectOthersButton))
                    .addGroup(panelRunBotsLayout.createSequentialGroup()
                        .addComponent(discDbgButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(discAnotherBotsButton)))
                .addContainerGap(42, Short.MAX_VALUE))
        );
        panelRunBotsLayout.setVerticalGroup(
            panelRunBotsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRunBotsLayout.createSequentialGroup()
                .addComponent(connectDebugedButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelRunBotsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nrBotsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(connectOthersButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelRunBotsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(discDbgButton)
                    .addComponent(discAnotherBotsButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Bot control"));

        pauseToggleButton.setText("Pause dbg bot");
        pauseToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseToggleButtonActionPerformed(evt);
            }
        });

        pauseAnotherBotsToggle.setText("Pause other bots");
        pauseAnotherBotsToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseAnotherBotsToggleActionPerformed(evt);
            }
        });

        jTextField1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField1.setText("disc");

        sendCommandToDBGButton.setText("<html><center>send cmd <br> to dbg");
        sendCommandToDBGButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendCommandToDBGButtonActionPerformed(evt);
            }
        });

        jLabel6.setText("Command: ");

        sendCommandToAnothersButton.setText("<html><center>send cmd <br> to others");
        sendCommandToAnothersButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendCommandToAnothersButtonActionPerformed(evt);
            }
        });

        stepSizeTextField2.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        stepSizeTextField2.setText("10");

        jLabel7.setText("Step size:");

        stepButton1.setText("step");
        stepButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stepButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(pauseToggleButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pauseAnotherBotsToggle))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(95, 95, 95)
                                        .addComponent(sendCommandToAnothersButton))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(stepSizeTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(stepButton1))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(sendCommandToDBGButton)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pauseToggleButton)
                    .addComponent(pauseAnotherBotsToggle))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(sendCommandToAnothersButton)
                            .addComponent(sendCommandToDBGButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addGap(19, 19, 19)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(stepSizeTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stepButton1))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Bot world info"));

        reqList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        reqList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                reqListValueChanged(evt);
            }
        });
        reqListScrollPane2.setViewportView(reqList);

        jLabel4.setText("Info items list");

        fullInfo.setColumns(20);
        fullInfo.setRows(5);
        fullInfoScrollPane3.setViewportView(fullInfo);

        jLabel5.setText("Full information on selected item:");

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Display info options"));

        infoButtonGroup.add(allEntsRadioButton1);
        allEntsRadioButton1.setSelected(true);
        allEntsRadioButton1.setText("all entities in the WorldKB");
        allEntsRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allEntsRadioButton1ActionPerformed(evt);
            }
        });

        infoButtonGroup.add(visibleWaypointsRadioButton2);
        visibleWaypointsRadioButton2.setText("visible waypoints");
        visibleWaypointsRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                visibleWaypointsRadioButton2ActionPerformed(evt);
            }
        });

        infoButtonGroup.add(navPlanRadioButton);
        navPlanRadioButton.setText("navigation plan details");
        navPlanRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                navPlanRadioButtonActionPerformed(evt);
            }
        });

        infoButtonGroup.add(visibleEntsRadioButton2);
        visibleEntsRadioButton2.setText("visible entities from WorldKB");
        visibleEntsRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                visibleEntsRadioButton2ActionPerformed(evt);
            }
        });

        infoButtonGroup.add(seenEntsBuffRadioButton1);
        seenEntsBuffRadioButton1.setText("seen entities buffer");
        seenEntsBuffRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seenEntsBuffRadioButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(allEntsRadioButton1)
                    .addComponent(visibleWaypointsRadioButton2)
                    .addComponent(navPlanRadioButton)
                    .addComponent(visibleEntsRadioButton2)
                    .addComponent(seenEntsBuffRadioButton1))
                .addContainerGap(89, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(allEntsRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(visibleWaypointsRadioButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(navPlanRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(visibleEntsRadioButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(seenEntsBuffRadioButton1)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jLabel3.setText("Distance from the bot:");

        distanceLabel.setText("0");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(reqListScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(distanceLabel))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel5)
                        .addComponent(fullInfoScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(reqListScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel3)
                                    .addComponent(distanceLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fullInfoScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Bot state info"));

        botStateInfoTextArea1.setColumns(20);
        botStateInfoTextArea1.setRows(5);
        jScrollPane1.setViewportView(botStateInfoTextArea1);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Messages"));

        messages.setColumns(20);
        messages.setRows(5);
        messagesScrollPane1.setViewportView(messages);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(messagesScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 741, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(messagesScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panelRunBots, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(19, 19, 19))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelRunBots, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void connectDebugedButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectDebugedButtonActionPerformed
    	SimpleBot bot = new SimpleBot("DebuggedBot", StartBots.skinName);
    	bot.addBotJob(new HitsReporter(bot));
		bot.connect(StartBots.serverIP, StartBots.serverPort);
		stepJob = new DebugStepJob(bot, this);
		bot.addBotJob(stepJob);
		dbgBot = bot;
    }//GEN-LAST:event_connectDebugedButtonActionPerformed

    private void connectOthersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectOthersButtonActionPerformed
    	int num = Integer.parseInt(nrBotsField.getText());
    	for (int i=0; i<num; i++) {
    		SimpleBot bot = new SimpleBot("AnotherBot-"+i, StartBots.skinName);
    		bot.dtalk.active = false;
    		bot.addBotJob(new HitsReporter(bot));
    		bot.connect(StartBots.serverIP, StartBots.serverPort);	
    		anotherBots.add(bot);
    	}
    }//GEN-LAST:event_connectOthersButtonActionPerformed

    private void pauseToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseToggleButtonActionPerformed
       dbgBot.botPaused = pauseToggleButton.isSelected();
       updateDisplayedInfo();
    }//GEN-LAST:event_pauseToggleButtonActionPerformed

    private void reqListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_reqListValueChanged
        int ind = reqList.getSelectedIndex();
        if (ind < 0 || ind > requiredList.size()) return;
        fullInfo.setText(requiredList.get(ind).toDetailedString());
        dbgBot.setPauseLookDirection(requiredList.get(ind).getPosition());
        distanceLabel.setText(""+CommFun.getDistanceBetweenPositions(dbgBot.getBotPosition(), requiredList.get(ind).getPosition()));
    }//GEN-LAST:event_reqListValueChanged

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (dbgBot != null) dbgBot.disconnect();
        for (SimpleBot b : anotherBots) b.disconnect();
    }//GEN-LAST:event_formWindowClosing

    private void pauseAnotherBotsToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseAnotherBotsToggleActionPerformed
       for (SimpleBot b : anotherBots) {
           b.botPaused = pauseAnotherBotsToggle.isSelected();
       }
    }//GEN-LAST:event_pauseAnotherBotsToggleActionPerformed

    private void discDbgButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_discDbgButtonActionPerformed
    	if (dbgBot != null) dbgBot.disconnect();
    }//GEN-LAST:event_discDbgButtonActionPerformed

    private void discAnotherBotsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_discAnotherBotsButtonActionPerformed
    	for (SimpleBot b : anotherBots) b.disconnect();
    }//GEN-LAST:event_discAnotherBotsButtonActionPerformed

    private void sendCommandToDBGButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendCommandToDBGButtonActionPerformed
        String cmd = jTextField1.getText();
        dbgBot.handleCommand(cmd);
    }//GEN-LAST:event_sendCommandToDBGButtonActionPerformed

    private void sendCommandToAnothersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendCommandToAnothersButtonActionPerformed
    	String cmd = jTextField1.getText();
    	for (SimpleBot b : anotherBots) b.handleCommand(cmd);
    }//GEN-LAST:event_sendCommandToAnothersButtonActionPerformed

    private void allEntsRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allEntsRadioButton1ActionPerformed
      Vector<GameObject> v = new Vector<GameObject>();
      v.addAll(dbgBot.kb.getAllItems());
      setReqList(v);
    }//GEN-LAST:event_allEntsRadioButton1ActionPerformed

    private void visibleWaypointsRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_visibleWaypointsRadioButton2ActionPerformed
    	 Vector<GameObject> v = new Vector<GameObject>();
         v.addAll(dbgBot.kb.getAllVisibleWaypoints(dbgBot));
         setReqList(v);
    }//GEN-LAST:event_visibleWaypointsRadioButton2ActionPerformed

    private void stepButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stepButton1ActionPerformed
       int steps = Integer.parseInt(stepSizeTextField2.getText());
       stepJob.pauseIn(steps);
    }//GEN-LAST:event_stepButton1ActionPerformed

    private void navPlanRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_navPlanRadioButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_navPlanRadioButtonActionPerformed

    private void visibleEntsRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_visibleEntsRadioButton2ActionPerformed
    	 Vector<GameObject> v = new Vector<GameObject>();
         v.addAll(dbgBot.kb.getAllVisibleEntities(dbgBot));
         setReqList(v);
    }//GEN-LAST:event_visibleEntsRadioButton2ActionPerformed

    private void seenEntsBuffRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seenEntsBuffRadioButton1ActionPerformed
    	Vector<GameObject> v = new Vector<GameObject>();
    	v.addAll(dbgBot.debugSeenEntsBuffer);
        setReqList(v);
    }//GEN-LAST:event_seenEntsBuffRadioButton1ActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
    	System.setProperty("QUAKE2", StartBots.quakePath);
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MainFrame mf = new MainFrame();
                mf.setVisible(true);
                Dbg.toAppend = mf.getMessagesTextArea();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton allEntsRadioButton1;
    private javax.swing.JTextArea botStateInfoTextArea1;
    private javax.swing.JButton connectDebugedButton;
    private javax.swing.JButton connectOthersButton;
    private javax.swing.JButton discAnotherBotsButton;
    private javax.swing.JButton discDbgButton;
    private javax.swing.JLabel distanceLabel;
    private javax.swing.JTextArea fullInfo;
    private javax.swing.JScrollPane fullInfoScrollPane3;
    private javax.swing.ButtonGroup infoButtonGroup;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextArea messages;
    private javax.swing.JScrollPane messagesScrollPane1;
    private javax.swing.JRadioButton navPlanRadioButton;
    private javax.swing.JTextField nrBotsField;
    private javax.swing.JPanel panelRunBots;
    private javax.swing.JToggleButton pauseAnotherBotsToggle;
    private javax.swing.JToggleButton pauseToggleButton;
    private javax.swing.JList reqList;
    private javax.swing.JScrollPane reqListScrollPane2;
    private javax.swing.JRadioButton seenEntsBuffRadioButton1;
    private javax.swing.JButton sendCommandToAnothersButton;
    private javax.swing.JButton sendCommandToDBGButton;
    private javax.swing.JButton stepButton1;
    private javax.swing.JTextField stepSizeTextField2;
    private javax.swing.JRadioButton visibleEntsRadioButton2;
    private javax.swing.JRadioButton visibleWaypointsRadioButton2;
    // End of variables declaration//GEN-END:variables
    
    private SimpleBot dbgBot = null;
    
    private Vector<SimpleBot> anotherBots = new Vector<SimpleBot>();
    
    private Vector<GameObject> requiredList = new Vector<GameObject>();
    
    private DebugStepJob stepJob = null;
    
    public JTextArea getMessagesTextArea() {
    	return messages;
    }
    
    public void updateDisplayedInfo() {
    	if (visibleWaypointsRadioButton2.isSelected()) visibleWaypointsRadioButton2ActionPerformed(null);
    	else if (allEntsRadioButton1.isSelected()) allEntsRadioButton1ActionPerformed(null);
    	else if (visibleEntsRadioButton2.isSelected()) visibleEntsRadioButton2ActionPerformed(null);
    	else if (navPlanRadioButton.isSelected()) navPlanRadioButtonActionPerformed(null);
    	else if (seenEntsBuffRadioButton1.isSelected()) seenEntsBuffRadioButton1ActionPerformed(null);
    	botStateInfoTextArea1.setText(dbgBot.toString());
    }
    private void setReqList(Vector<GameObject> vect) {
    	 requiredList.clear();
         requiredList.addAll(vect);
         DefaultListModel m = new DefaultListModel();
         for (Object o : requiredList) {
      	   m.addElement(o);
         }
         reqList.setModel(m);
         fullInfo.setText("");
    }

}
