/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drafail.jag.business.DAO;

import com.drafail.jag.business.Email.RecipientHelper;
import com.drafail.jag.data.DatabaseBean;
import com.drafail.jag.data.EmailBean;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author 1633028
 */
public class DAORecipients extends DatabaseInteraction {

    private final static Logger LOG = LoggerFactory.getLogger(DAORecipients.class);
    private final DatabaseBean data;

    /**
     * Constructor to build a DAORecipients to add an recipient to the database
     *
     * @param connection the string to connect to the database
     * @param id the id of the user
     * @param pass the password of the user
     */
    public DAORecipients(String connection, String id, String pass) {
        super(connection, id, pass);
        data = returnData();
    }

    /**
     * Method to add the recipient to the database
     *
     * @param type the type of the recipient (cc, bcc, to)
     * @param email the email address of the recipient
     * @param name the name of the recipient
     * @throws SQLException
     */
    public void addReceiver(String type, String email, String name) throws SQLException {
        try (Connection conn = data.connect()) {
            if (!findReceiver(type, email)) {
                try (PreparedStatement pStmt = conn.prepareStatement("{call emails.addReceiver(?,?,?)}")) {
                    pStmt.setString(1, type);
                    pStmt.setString(2, email);
                    pStmt.setString(3, name);  
                    pStmt.execute();
                }
                LOG.debug("Receiver Added to database");
            } else {
                LOG.debug("Receiver already in database. Failed to add");
            }
        }
    }

    /**
     * Method to delete a recipient from the database
     *
     * @param type the type of the recipient (cc,bcc,to)
     * @param email the email address of the recipient
     * @param name the name of the recipient
     * @throws SQLException
     */
    public void deleteReceiver(String type, String email) throws SQLException {
        try (Connection conn = data.connect()) {
            if (findReceiver(type, email)) {

                try (PreparedStatement pStmt = conn.prepareStatement("{call emails.deleteReceivers(?,?)}")) {
                  pStmt.setString(1, type);
                    pStmt.setString(2, email);

                    pStmt.execute();
                }
                LOG.debug("Recipient deleted from database");
            } else {
                LOG.debug("Recipient not in database. Failed to delete");
            }
        }
    }

    /**
     * Method to find a recipient within the database
     *
     * @param type the type of the recipient (cc,bcc,to)
     * @param email the email address of the recipient
     * @param name the name of the recipient
     * @return boolean value based on whether or not the recipient was found in
     * the database
     * @throws SQLException
     */
    public boolean findReceiver(String type, String email) throws SQLException {
        String returnValue;
        try (Connection connect = data.connect()) {
            CallableStatement pStmt = connect.prepareCall("{ ? = call emails.findReceiver(?,?) }");
            pStmt.registerOutParameter(1, Types.CHAR);
            pStmt.setString(2, type);
            pStmt.setString(3, email);
            pStmt.execute();
            returnValue = pStmt.getString(1);
        }
        return !"0".equals(returnValue);
    }


    /**
     * Returns all the recipients based on an email id
     *
     * @param emailbean the emailbean we give the values to
     * @throws SQLException
     */
    public void getRecipient(EmailBean emailbean) throws SQLException {
        try (Connection connect = data.connect()) {
            int[] array = getRecipientID(emailbean).stream().mapToInt(Integer::intValue).toArray();
            for (int i = 0; i < array.length; i++) {
                CallableStatement pStmt = connect.prepareCall("select receiveEmail, receiveType from receive where receive_id  = ?");
                pStmt.setInt(1, array[i]);
                try (ResultSet rs = pStmt.executeQuery();) {
                    while (rs.next()) {
                        if ("cc".equals(rs.getString("receiveType"))) {
                            emailbean.setCc(RecipientHelper.add(emailbean.getCc(), rs.getString("receiveEmail")));
                        } else if ("bcc".equals(rs.getString("receiveType"))) {
                            emailbean.setBcc(RecipientHelper.add(emailbean.getBcc(), rs.getString("receiveEmail")));
                        } else {
                            emailbean.setReceiver(RecipientHelper.add(emailbean.getReceiver(), rs.getString("receiveEmail")));
                        }
                    }
                }
            }
        }
    }

    /**
     * find the id of the folder based on its name
     *
     * @param emailbean the emailbean we get the key from
     * @return list with all the ids of the recipients
     * @throws SQLException
     */
    private List<Integer> getRecipientID(EmailBean emailbean) throws SQLException {
        Connection connect = data.connect();
        CallableStatement pStmt = connect.prepareCall("select receive_id from email join bridge using(email_id) join receive using (receive_id) where email_id = ?");
        pStmt.setInt(1, emailbean.getEmailKey());
        List<Integer> returnValue = new ArrayList<>();
        try (ResultSet rs = pStmt.executeQuery();) {
            while (rs.next()) {
                returnValue.add(rs.getInt("receive_id"));
            }
        }
        connect.close();
        return returnValue;
    }

}
