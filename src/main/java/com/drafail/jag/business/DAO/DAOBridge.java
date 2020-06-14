/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drafail.jag.business.DAO;

import com.drafail.jag.data.DatabaseBean;
import com.drafail.jag.data.EmailBean;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author 1633028
 */
public class DAOBridge extends DatabaseInteraction {

    private final static Logger LOG = LoggerFactory.getLogger(DAOBridge.class);
    private final DatabaseBean data;

    /**
     * Constructor to build a DAOBridge to add a bridge to the database
     *
     * @param connection the string to connect to the database
     * @param id the id of the user
     * @param pass the password of the user
     */
    public DAOBridge(String connection, String id, String pass) {
        super(connection, id, pass);
        data = returnData();

    }

    /**
     * Method to add a bridge to the database
     * @param emailBean the emailBean we add
     * @param type the type of the attachment
     * @param email the email of the user
     * @param name the name of the user
     * @throws SQLException
     */
    public void addBridge(EmailBean emailBean, String type, String email) throws SQLException {
        try (Connection conn = data.connect()) {
            if (!findBridge(emailBean, type, email)) {
                try (PreparedStatement pStmt = conn.prepareStatement("{call emails.addBridge(?,?,?)}")) {
                    pStmt.setInt(1, emailBean.getEmailKey());
                    pStmt.setString(2, type);
                    pStmt.setString(3, email);
                    pStmt.execute();
                }
                LOG.debug("Bridge Added to database");
            } else {
                LOG.debug("Folder already in database. Failed to add");
            }
        }
    }

    /**
     * Method to find a bridge from the database
     *
     * @param emailBean the emailbean we find
     * @param type the type of the receiver (cc, bcc, to)
     * @param email the email address of the receiver
     * @param name the name of the receiver
     * @return boolean if bridge is found
     * @throws SQLException
     */
    public boolean findBridge(EmailBean emailBean, String type, String email) throws SQLException {
        String returnValue;
        try (Connection connect = data.connect()) {
            CallableStatement pStmt = connect.prepareCall("{ ? = call emails.findBridge(?,?,?) }");
            pStmt.registerOutParameter(1, Types.CHAR);
            pStmt.setInt(2, emailBean.getEmailKey());
            pStmt.setString(3, type);
            pStmt.setString(4, email);
            pStmt.execute();
            returnValue = pStmt.getString(1);
        }
        return !"0".equals(returnValue);
    }
}
