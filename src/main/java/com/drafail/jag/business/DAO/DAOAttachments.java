/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drafail.jag.business.DAO;

import com.drafail.jag.business.Email.AttachmentHelper;
import com.drafail.jag.data.Attachments;
import com.drafail.jag.data.DatabaseBean;
import com.drafail.jag.data.EmailBean;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author 1633028
 */
public class DAOAttachments extends DatabaseInteraction {

    private final static Logger LOG = LoggerFactory.getLogger(DAOAttachments.class);
    private final DatabaseBean data;

    /**
     * Constructor to build a DAOAttachment to add a attachment to the database
     *
     * @param connection the string to connect to the database
     * @param id the id of the user
     * @param pass the password of the user
     */
    public DAOAttachments(String connection, String id, String pass) {
        super(connection, id, pass);
        data = returnData();
    }

    /**
     * Method to add an attachment to the database
     *
     * @param attach the attachment we add
     * @param type type of the attachment (embedded or attachment)
     * @throws SQLException
     */
    public void addAttachment(Attachments attach, String type) throws SQLException {
        try (Connection conn = data.connect()) {
            if (!findAttachment(attach, type)) {
                try (PreparedStatement pStmt = conn.prepareStatement("{call emails.addAttachments(?,?,?)}")) {
                    getAttachmentPrepared(pStmt, 1, type, attach);

                    pStmt.execute();
                }
                LOG.debug("Attachment Added to database");
            } else {
                LOG.debug("Attachment already in database. Failed to add");
            }
        }
    }

    /**
     * *
     * Method used to find an attachment from the database
     *
     * @param attach the attachment we add
     * @param type type of the attachment (embedded or attachment)
     * @return returns a boolean if its able to find it
     * @throws SQLException
     */
    public boolean findAttachment(Attachments attach, String type) throws SQLException {
        String returnValue;
        try (Connection connect = data.connect()) {
            CallableStatement pStmt = connect.prepareCall("{ ? = call emails.findAttachments(?,?,?) }");
            pStmt.registerOutParameter(1, Types.CHAR);
            getAttachmentPrepared(pStmt, 2, type, attach);
            pStmt.execute();
            returnValue = pStmt.getString(1);
        }
        return !"0".equals(returnValue);
    }

    /**
     * method used to set the prepared values of the prepared Statement
     *
     * @param pStmt the prepared statment
     * @param startingValue the value you start with (to differentiate between
     * procedures and functions)
     * @param type the type of the attachment
     * @param attach the attachment we look for
     * @throws SQLException
     */
    private void getAttachmentPrepared(PreparedStatement pStmt, int startingValue, String type, Attachments attach) throws SQLException {
        if (!(type.equals("attachment")) && !(type.equals("embedded"))) {
            throw new IllegalArgumentException();
        }
        pStmt.setString(startingValue, type);
        pStmt.setString(startingValue + 1, attach.getName());
        pStmt.setBytes(startingValue + 2, attach.getAttachment());
    }

    /**
     * Returns all the attachments based on an email id
     *
     * @param emailbean the emailbean we add the attachments to
     * @throws SQLException
     */
    public void getAttachments(EmailBean emailbean) throws SQLException {
        try (Connection connect = data.connect();) {
            CallableStatement pStmt = connect.prepareCall("select attachmentName, attachmentValue, attachmentType from attachment where email_id = ?");
            pStmt.setInt(1, emailbean.getEmailKey());
            try (ResultSet rs = pStmt.executeQuery();) {
                while (rs.next()) {
                    Attachments attach = new Attachments(rs.getString("attachmentName"), rs.getBytes("attachmentValue"));
                    if ("attachment".equals(rs.getString("attachmentType"))) {
                        emailbean.setAttachment(AttachmentHelper.add(emailbean.getAttachment(), attach));
                    } else {
                        emailbean.setEmbAttachments(AttachmentHelper.add(emailbean.getEmbAttachments(), attach));
                    }
                }
            }
        }
    }
}
