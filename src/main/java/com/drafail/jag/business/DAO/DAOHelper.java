/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drafail.jag.business.DAO;

import com.drafail.jag.data.Attachments;
import com.drafail.jag.data.DatabaseBean;
import com.drafail.jag.data.EmailBean;
import java.io.IOException;
import java.sql.SQLException;

/**
 *
 * @author 1633028
 */
public class DAOHelper extends DatabaseInteraction {

    private final DatabaseBean data;

    /**
     * Constructor for DAOHelper which takes in an url, id and pass and returns
     * an DatabaseBean
     *
     * @param url the string url of the connection
     * @param id the id of the user
     * @param pass the password of the user
     */
    public DAOHelper(String url, String id, String pass) {
        super(url, id, pass);
        data = returnData();
    }

    /**
     * Static method to add an emailbean to the database
     *
     * @param emailbean the email bean which will be added to the database
     * @throws SQLException
     * @throws java.io.IOException
     */
    public void addToDatabase(EmailBean emailbean) throws SQLException, IOException {
        DatabaseInteraction email = new DAOEmail(data.getConnectionString(), data.getSqlID(), data.getSqlPassword());
        DatabaseInteraction folder = new DAOFolder(data.getConnectionString(), data.getSqlID(), data.getSqlPassword());
        DatabaseInteraction attach = new DAOAttachments(data.getConnectionString(), data.getSqlID(), data.getSqlPassword());
        DatabaseInteraction receive = new DAORecipients(data.getConnectionString(), data.getSqlID(), data.getSqlPassword());
        DatabaseInteraction bridge = new DAOBridge(data.getConnectionString(), data.getSqlID(), data.getSqlPassword());
        ((DAOFolder) folder).addFolder(emailbean);
        ((DAOEmail) email).addEmail(emailbean);
        if (emailbean.getCc() != null && emailbean.getCc().length > 0) {
            for (String value : emailbean.getCc()) {
                if (value != null) {
                    ((DAORecipients) receive).addReceiver("cc", value, emailbean.getName());
                    ((DAOBridge) bridge).addBridge(emailbean, "cc", value);
                }
            }
        }
        if (emailbean.getBcc() != null && emailbean.getBcc().length > 0) {
            for (String value : emailbean.getBcc()) {
                if (value != null) {
                    ((DAORecipients) receive).addReceiver("bcc", value, emailbean.getName());
                    ((DAOBridge) bridge).addBridge(emailbean, "bcc", value);
                }
            }
        }
        if (emailbean.getReceiver() != null && emailbean.getReceiver().length > 0) {
            for (String value : emailbean.getReceiver()) {
                if (value != null) {
                    ((DAORecipients) receive).addReceiver("to", value, emailbean.getName());
                    ((DAOBridge) bridge).addBridge(emailbean, "to", value);
                }
            }
        }
        if (emailbean.getAttachment() != null && emailbean.getAttachment().length > 0) {
            for (Attachments value : emailbean.getAttachment()) {
                if (value != null) {
                    ((DAOAttachments) attach).addAttachment(value, "attachment");
                }
            }
        }
        if (emailbean.getEmbAttachments() != null && emailbean.getEmbAttachments().length > 0) {
            for (Attachments value : emailbean.getEmbAttachments()) {
                if (value != null) {
                    ((DAOAttachments) attach).addAttachment(value, "embedded");
                }
            }
        }
    }
}
