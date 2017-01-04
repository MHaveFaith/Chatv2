package client;

/**
 *
 * @author Keno
 */
public class Account extends javax.swing.JFrame {

    String message;

    /**
     * Creates new form Account
     */
    public Account(String message) {
        this.message = message;
        initComponents();
        setText();
    }

    private void setText(){
        String split_message [] = message.split(":");
        String username = split_message[0];
        String rank = split_message[1];
        String reg_date = split_message[2];
        usernameField.setText(username);
        rankField.setText(rank.toUpperCase());
        regdateField.setText(reg_date);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        usernameLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        rankField = new javax.swing.JTextField();
        regdateField = new javax.swing.JTextField();
        usernameField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Account");
        setResizable(false);

        usernameLabel.setText("Username:");

        jLabel2.setText("Your rank:");

        jLabel3.setText("Reg date:");

        rankField.setEditable(false);
        rankField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        rankField.setText("null");

        regdateField.setEditable(false);
        regdateField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        regdateField.setText("null");

        usernameField.setEditable(false);
        usernameField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        usernameField.setText("null");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel2)
                                        .addComponent(usernameLabel)
                                        .addComponent(jLabel3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(regdateField, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                                        .addComponent(usernameField)
                                        .addComponent(rankField))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(usernameLabel)
                                        .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2)
                                        .addComponent(rankField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel3)
                                        .addComponent(regdateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        setLocationRelativeTo(null);
        pack();
    }// </editor-fold>


    // Variables declaration - do not modify
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField rankField;
    private javax.swing.JTextField regdateField;
    private javax.swing.JTextField usernameField;
    private javax.swing.JLabel usernameLabel;
    // End of variables declaration
}
