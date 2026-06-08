/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package boundary;

import core.AppContext;
import entity.Retailer;
import entity.Role;
import entity.Shipment;
import entity.Shipper;
import entity.User;
import entity.Warehouse;
import entity.events.Event;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SkyPC
 */
public class ShipmentManagementUI extends javax.swing.JFrame {
    private final AppContext context;
    private final User currentUser;
    private final JFrame parentFrame;
    private final List<Shipment> visibleShipments;
    private final List<Warehouse> warehouses;
    private final List<Retailer> retailers;
    private final DefaultTableModel shipmentModel;

    /**
     * Creates new form ShipmentManagementUI
     */
    public ShipmentManagementUI() {
        this.context = null;
        this.currentUser = null;
        this.parentFrame = null;
        this.visibleShipments = new ArrayList<>();
        this.warehouses = new ArrayList<>();
        this.retailers = new ArrayList<>();
        this.shipmentModel = new DefaultTableModel(new Object[]{"Shipment ID", "Source WH", "Pickup", "Destination", "Shipper ID", "Destination Type"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        initComponents();
        wireLogic();
    }

    public ShipmentManagementUI(AppContext context, User currentUser, JFrame parentFrame) {
        this.context = context;
        this.currentUser = currentUser;
        this.parentFrame = parentFrame;
        this.visibleShipments = new ArrayList<>();
        this.warehouses = new ArrayList<>();
        this.retailers = new ArrayList<>();
        this.shipmentModel = new DefaultTableModel(new Object[]{"Shipment ID", "Source WH", "Pickup", "Destination", "Shipper ID", "Destination Type"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        initComponents();
        wireLogic();
    }

    private void wireLogic() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        jTable1.setModel(shipmentModel);
        jTextArea1.setText("Recent actions will appear here.\n");
        jTextArea2.setText("");
        jLabel8.setText("-");

        if (context == null || currentUser == null) {
            setInteractive(false);
            return;
        }

        jButton2.setText("Open Event Recording");
        jComboBox2.setModel(new DefaultComboBoxModel<>(new String[]{"WAREHOUSE", "RETAILER"}));
        jComboBox2.addActionListener(e -> refreshDestinationOptions());
        CreateShipmentButton.addActionListener(e -> createShipment());
        RefreshShipmentsButton.addActionListener(e -> reloadShipmentList());
        ViewSelectedStatus.addActionListener(e -> viewSelectedShipment());
        jButton2.addActionListener(e -> openEventRecordingForSelected());

        loadEndpoints();
        loadShippers();
        reloadShipmentList();

        if (currentUser.getRole() == Role.ADMIN) {
            applyCreateLockedMode("Admin mode: Create Shipment is disabled. Use Track Shipment for viewing.", false);
        } else if (currentUser.getRole() == Role.SHIPPER) {
            applyCreateLockedMode("Shipper mode: Create Shipment is disabled. Use Track Shipment to record shipment events.", true);
        } else if (currentUser.getRole() == Role.RETAILER) {
            applyCreateLockedMode("Retailer mode: Create Shipment is disabled. Use Track Shipment for viewing and confirmations.", true);
        }
    }

    private void setInteractive(boolean enabled) {
        jComboBox1.setEnabled(enabled);
        jComboBox2.setEnabled(enabled);
        jComboBox3.setEnabled(enabled);
        jComboBox4.setEnabled(enabled);
        CreateShipmentButton.setEnabled(enabled);
        RefreshShipmentsButton.setEnabled(enabled);
        BackToDashboardButton.setEnabled(enabled);
        ViewSelectedStatus.setEnabled(enabled);
        jButton2.setEnabled(enabled);
    }

    private void applyCreateLockedMode(String message, boolean allowEventRecording) {
        jTabbedPane1.setSelectedComponent(jPanel2);
        jTextArea1.setText(message + "\n");

        jComboBox1.setEnabled(false);
        jComboBox2.setEnabled(false);
        jComboBox3.setEnabled(false);
        jComboBox4.setEnabled(false);
        CreateShipmentButton.setEnabled(false);
        RefreshShipmentsButton.setEnabled(true);
        jButton2.setEnabled(allowEventRecording);
        jTable1.setEnabled(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        CreateShipmentButton = new javax.swing.JButton();
        RefreshShipmentsButton = new javax.swing.JButton();
        BackToDashboardButton = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        jComboBox3 = new javax.swing.JComboBox<>();
        jComboBox4 = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        ViewSelectedStatus = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setText("Shipment Management");

        jTabbedPane1.setBackground(new java.awt.Color(189, 210, 255));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("Source Warehouse");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setText("Destination Type");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setText("Destination");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setText("Assigned Shipper (Optional)");

        CreateShipmentButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        CreateShipmentButton.setText("Create Shipment");

        RefreshShipmentsButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        RefreshShipmentsButton.setText("Refresh Shipments");

        BackToDashboardButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        BackToDashboardButton.setText("Back to Dashboard");
        BackToDashboardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BackToDashboardButtonActionPerformed(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel6.setText("Activity Log");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setText("Recent activities will appear here.");
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CreateShipmentButton, javax.swing.GroupLayout.PREFERRED_SIZE, 457, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel6))
                        .addGap(71, 71, 71)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(BackToDashboardButton, javax.swing.GroupLayout.PREFERRED_SIZE, 457, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(RefreshShipmentsButton, javax.swing.GroupLayout.DEFAULT_SIZE, 457, Short.MAX_VALUE)
                                .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jComboBox3, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jComboBox4, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(RefreshShipmentsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(CreateShipmentButton, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(BackToDashboardButton, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(41, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Create Shipment", jPanel2);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel7.setText("Current Status");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel8.setText("status");

        ViewSelectedStatus.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        ViewSelectedStatus.setText("View Selected Status + Timeline");

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton2.setText("Open Event Recording");

        jLabel9.setText("Visible Shipment List");

        jLabel10.setText("Timeline Viewer");

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane2.setViewportView(jTextArea2);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6"
            }
        ));
        jScrollPane3.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(555, 555, 555))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(74, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel10)
                    .addComponent(jLabel9)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(ViewSelectedStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 404, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(55, 55, 55)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 428, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane3))
                .addGap(72, 72, 72))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addGap(28, 28, 28)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ViewSelectedStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Track Shipment", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 407, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel1)
                .addGap(34, 34, 34)
                .addComponent(jTabbedPane1))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BackToDashboardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BackToDashboardButtonActionPerformed
        Navigation.goBack(this, parentFrame);
    }//GEN-LAST:event_BackToDashboardButtonActionPerformed

    private void loadEndpoints() {
        warehouses.clear();
        retailers.clear();
        warehouses.addAll(context.shipmentControl.listShipmentWarehouses());
        retailers.addAll(context.shipmentControl.listShipmentRetailers());
        loadSourceWarehouses();
        refreshDestinationOptions();
    }

    private void loadSourceWarehouses() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        for (Warehouse warehouse : warehouses) {
            model.addElement(warehouse.getWarehouseId() + " - " + warehouse.getName() + " (" + warehouse.getLocation() + ")");
        }
        jComboBox1.setModel(model);
        String defaultSource = context.shipmentControl.suggestDefaultSourceWarehouse(currentUser);
        if (defaultSource != null) {
            selectSourceWarehouse(defaultSource);
            if (currentUser.getRole() == Role.MANUFACTURER || currentUser.getRole() == Role.WAREHOUSE_MANAGER) {
                jComboBox1.setEnabled(false);
            }
        }
    }

    private void refreshDestinationOptions() {
        String destinationType = selectedDestinationType();
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        if ("WAREHOUSE".equals(destinationType)) {
            for (Warehouse warehouse : warehouses) {
                model.addElement(warehouse.getWarehouseId() + " - " + warehouse.getName() + " (" + warehouse.getLocation() + ")");
            }
        } else {
            for (Retailer retailer : retailers) {
                model.addElement(retailer.getUserId() + " - " + retailer.getOrganizationName() + " (" + retailer.getLocation() + ")");
            }
        }
        jComboBox3.setModel(model);
    }

    private void loadShippers() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement("(None)");
        for (User user : context.userRepository.findAllUsers()) {
            if (user.getRole() == Role.SHIPPER) {
                model.addElement(user.getUserId());
            }
        }
        jComboBox4.setModel(model);
    }

    private void createShipment() {
        try {
            String sourceWarehouseId = selectedSourceWarehouseId();
            String destinationType = selectedDestinationType();
            String destinationId = selectedDestinationId();
            Shipper assignedShipper = selectedShipper();

            Shipment shipment = context.shipmentControl.createShipment(
                    currentUser,
                    sourceWarehouseId,
                    destinationType,
                    destinationId,
                    List.of(),
                    assignedShipper);

            jTextArea1.append("Created shipment: " + shipment.getShipmentId() + "\n");
            jComboBox4.setSelectedIndex(0);
            reloadShipmentList();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Create Shipment failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void reloadShipmentList() {
        try {
            shipmentModel.setRowCount(0);
            visibleShipments.clear();
            List<Shipment> shipments = context.shipmentControl.getVisibleShipments(currentUser);
            jLabel8.setText("Loaded: " + shipments.size() + " shipment(s)");
            for (Shipment shipment : shipments) {
                visibleShipments.add(shipment);
                shipmentModel.addRow(new Object[]{
                    shipment.getShipmentId(),
                    shipment.getSourceWarehouseId() == null ? "-" : shipment.getSourceWarehouseId(),
                    shipment.getPickupLocation(),
                    formatDestination(shipment),
                    shipment.getAssignedShipper() == null ? "-" : shipment.getAssignedShipper().getUserId(),
                    shipment.getDestinationType()
                });
            }
            if (shipments.isEmpty()) {
                jTextArea2.setText("No shipments are currently visible for this account.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load shipments: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewSelectedShipment() {
        try {
            Shipment shipment = selectedShipment();
            String shipmentId = shipment.getShipmentId();
            String status = context.shipmentControl.viewShipmentStatus(currentUser, shipmentId);
            List<Event> timeline = context.shipmentControl.getShipmentTimeline(currentUser, shipmentId);

            jLabel8.setText(status);
            jTextArea2.setText("");
            jTextArea2.append("Shipment: " + shipmentId + "\n");
            jTextArea2.append("Status: " + status + "\n");
            jTextArea2.append("Route: " + shipment.getPickupLocation() + " -> " + formatDestination(shipment) + "\n\n");
            jTextArea2.append("Timeline (" + timeline.size() + " events):\n");
            int index = 1;
            for (Event event : timeline) {
                jTextArea2.append(index + ". " + event.getTimestamp() + " | " + event.getEventType() + " | " + event.getEventDetails() + "\n");
                index++;
            }
            if (timeline.isEmpty()) {
                jTextArea2.append(" - No events yet.\n");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Track failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openEventRecordingForSelected() {
        try {
            Shipment shipment = selectedShipment();
            Navigation.openChild(this, new EventRecordingFrame(context, currentUser, this, shipment.getShipmentId()));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
        }
    }

    private Shipment selectedShipment() {
        int row = jTable1.getSelectedRow();
        if (row < 0) {
            throw new IllegalArgumentException("Select a shipment from the list first.");
        }
        return visibleShipments.get(row);
    }

    private void selectSourceWarehouse(String warehouseId) {
        for (int i = 0; i < jComboBox1.getItemCount(); i++) {
            String entry = jComboBox1.getItemAt(i);
            if (entry.startsWith(warehouseId + " - ")) {
                jComboBox1.setSelectedIndex(i);
                return;
            }
        }
    }

    private String selectedSourceWarehouseId() {
        Object selected = jComboBox1.getSelectedItem();
        if (selected == null) {
            throw new IllegalArgumentException("Select source warehouse.");
        }
        return selected.toString().split(" - ")[0];
    }

    private String selectedDestinationType() {
        Object selected = jComboBox2.getSelectedItem();
        return selected == null ? "WAREHOUSE" : selected.toString();
    }

    private String selectedDestinationId() {
        Object selected = jComboBox3.getSelectedItem();
        if (selected == null) {
            throw new IllegalArgumentException("Select destination.");
        }
        return selected.toString().split(" - ")[0];
    }

    private Shipper selectedShipper() {
        Object selected = jComboBox4.getSelectedItem();
        if (selected == null || "(None)".equals(selected.toString())) {
            return null;
        }
        String shipperId = selected.toString();
        for (User user : context.userRepository.findAllUsers()) {
            if (user.getRole() == Role.SHIPPER && user.getUserId().equalsIgnoreCase(shipperId)) {
                return (Shipper) user;
            }
        }
        return null;
    }

    private String formatDestination(Shipment shipment) {
        if (shipment == null) {
            return "-";
        }
        if (shipment.isDestinationRetailer()) {
            String retailerId = shipment.getDestinationRetailerId();
            if (retailerId != null && !retailerId.isBlank()) {
                Retailer retailer = context.userRepository.findRetailerById(retailerId);
                if (retailer != null) {
                    return retailerId + " - " + retailer.getOrganizationName() + " (" + retailer.getLocation() + ")";
                }
                return retailerId + " (" + shipment.getDestination() + ")";
            }
        }
        if (shipment.isDestinationWarehouse()) {
            String warehouseId = shipment.getDestinationWarehouseId();
            if (warehouseId != null && !warehouseId.isBlank()) {
                String warehouseName = context.warehouseRepository.findWarehouseNameById(warehouseId);
                String warehouseLocation = context.warehouseRepository.findWarehouseLocationById(warehouseId);
                if (warehouseName != null && warehouseLocation != null) {
                    return warehouseId + " - " + warehouseName + " (" + warehouseLocation + ")";
                }
                return warehouseId + " (" + shipment.getDestination() + ")";
            }
        }
        return shipment.getDestination();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ShipmentManagementUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ShipmentManagementUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ShipmentManagementUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ShipmentManagementUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ShipmentManagementUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BackToDashboardButton;
    private javax.swing.JButton CreateShipmentButton;
    private javax.swing.JButton RefreshShipmentsButton;
    private javax.swing.JButton ViewSelectedStatus;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    // End of variables declaration//GEN-END:variables
}
