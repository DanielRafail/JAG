/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drafail.jag.business.DAO;

import com.drafail.jag.business.Properties.PropertiesAccessor;
import com.drafail.jag.data.DatabaseBean;
import com.drafail.jag.data.EmailBean;
import com.drafail.jag.data.PropertyLocation;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author 1633028
 */
public class DAOEmail extends DatabaseInteraction {

    private final static Logger LOG = LoggerFactory.getLogger(DAOEmail.class);
    private final DatabaseBean data;
    Properties props;

    /**
     * Constructor to build a DAOEmail to add an email to the database
     *
     * @param connection the string to connect to the database
     * @param id the id of the user
     * @param pass the password of the user
     */
    public DAOEmail(String connection, String id, String pass) throws IOException {
        super(connection, id, pass);
        props = PropertiesAccessor.instantiateProperty(PropertyLocation.getPath(), PropertyLocation.getFileName());
        data = returnData();
    }

    /**
     * Method to add the email to the database
     *
     * @param emailBean the emailbean we add
     * @throws SQLException
     */
    public void addEmail(EmailBean emailBean) throws SQLException {
        try (Connection conn = data.connect()) {
            if (!findEmail(emailBean)) {

                try (CallableStatement stmt = conn.prepareCall("{? = call emails.addEmail(?,?,?,?,?,?,?,?,?)}")) {

                    stmt.registerOutParameter(1, Types.INTEGER);
                    stmt.setString(2, emailBean.getSubject());
                    stmt.setString(3, emailBean.getTextMsg());
                    stmt.setString(4, emailBean.getHtmlMsg());
                    stmt.setTimestamp(5, Timestamp.from(emailBean.getDateSent().atZone(ZoneId.systemDefault()).toInstant()));
                    if (emailBean.getDateReceived() == null) {
                        stmt.setTimestamp(6, null);

                    } else {
                        stmt.setTimestamp(6, Timestamp.from(emailBean.getDateReceived().atZone(ZoneId.systemDefault()).toInstant()));
                    }
                    stmt.setInt(7, emailBean.getMessagePriority());
                    if (emailBean.getRead()) {
                        stmt.setString(8, "1");

                    } else {
                        stmt.setString(8, "0");

                    }
                    stmt.setString(9, emailBean.getFolder());
                    stmt.setString(10, emailBean.getSender());
                    try (ResultSet rs = stmt.executeQuery();) {
                        if (rs.next()) {
                            emailBean.setEmailKey(rs.getInt(1));
                        }
                    }
                }
                LOG.debug("Email Added to database");
            } else {
                LOG.debug("Email already in database. Failed to add");
            }
        }
    }

    /**
     * Method to delete an email from the database
     *
     * @param emailBean the emailbean we delete
     * @throws SQLException
     */
    public void deleteEmail(EmailBean emailBean) throws SQLException {
        try (Connection conn = data.connect()) {
            if (findEmail(emailBean)) {

                try (PreparedStatement pStmt = conn.prepareStatement("{call emails.deleteEmail(?)}")) {
                    pStmt.setInt(1, emailBean.getEmailKey());

                    pStmt.execute();
                }
                LOG.debug("Email deleted from database");
            } else {
                LOG.debug("Email not in database. Failed to delete");
            }
        }
    }

    /**
     * Method to find an email in the database
     *
     * @param emailBean we the emailBean we look for
     * @return boolean if the email was found
     * @throws SQLException
     */
    public boolean findEmail(EmailBean emailBean) throws SQLException {
        String returnValue;
        try (Connection connect = data.connect()) {
            CallableStatement pStmt = connect.prepareCall("{ ? = call emails.findEmail(?) }");
            pStmt.registerOutParameter(1, Types.CHAR);
            pStmt.setInt(2, emailBean.getEmailKey());
            pStmt.execute();
            returnValue = pStmt.getString(1);
        }
        return !"0".equals(returnValue);
    }

    /**
     * Returns an array of emailbeans with correct values based on a folder
     *
     * @param folder the name of the folder
     * @return array of email beans
     * @throws SQLException
     */
    public EmailBean[] getEmail(String folder) throws SQLException {
        List<EmailBean> emailBean;
        try (Connection connect = data.connect();) {
            CallableStatement pStmt = connect.prepareCall("select subject, textMsg, htmlMsg, dateSent, dateReceived, priority, isread, email_id, sender from email where folders_id = ?");
            int counter = 0;
            pStmt.setInt(1, getFolder(folder));
            emailBean = new ArrayList<>();
            try (ResultSet rs = pStmt.executeQuery();) {
                while (rs.next()) {
                    emailBean.add(new EmailBean());
                    emailBean.get(counter).setSubject(rs.getString("subject"));
                    emailBean.get(counter).setTextMsg(rs.getString("textMsg"));
                    emailBean.get(counter).setHtmlMsg(rs.getString("htmlMsg"));
                    emailBean.get(counter).setDateSent(rs.getTimestamp("dateSent").toLocalDateTime());
                    try{
                    emailBean.get(counter).setDateReceived(rs.getTimestamp("dateReceived").toLocalDateTime());
                    }catch(NullPointerException ex){
                        
                    }
                    emailBean.get(counter).setMessagePriority(rs.getInt("priority"));
                    emailBean.get(counter).setRead(rs.getBoolean("isread"));
                    emailBean.get(counter).setEmailKey(rs.getInt("email_id"));
                    emailBean.get(counter).setSender(rs.getString("sender"));
                    counter++;
                }
            }
        }
        return emailBean.toArray(new EmailBean[1]);
    }

    /**
     * find the id of the folder based on its name
     *
     * @param folder the name of the folder
     * @return the id of the folder
     * @throws SQLException
     */
    private int getFolder(String folder) throws SQLException {
        int returnValue;
        try (Connection connect = data.connect()) {
            CallableStatement pStmt = connect.prepareCall("{ ? = call emails.getFolders(?) }");
            pStmt.registerOutParameter(1, Types.INTEGER);
            pStmt.setString(2, folder);
            pStmt.execute();
            returnValue = pStmt.getInt(1);
        }
        return returnValue;
    }

    /**
     * Change the folder of an emailbean
     *
     * @param folder name of the new folder
     * @param emailbean the emailbean we get the key from
     * @return boolean based on if it succeeds or not
     * @throws SQLException
     */
    public boolean changeFolder(String folder, EmailBean emailbean) throws SQLException {
        try (Connection connect = data.connect()) {
            CallableStatement pStmt = connect.prepareCall("update email set folders_id  = ? where email_id = ?");
            pStmt.setInt(1, getFolder(folder));
            pStmt.setInt(2, emailbean.getEmailKey());
            pStmt.execute();
        }
        return true;
    }

}
