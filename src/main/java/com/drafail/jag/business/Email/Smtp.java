/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drafail.jag.business.Email;

import com.drafail.jag.business.DAO.*;
import com.drafail.jag.application.MainApp;
import com.drafail.jag.business.Properties.PropertiesAccessor;
import com.drafail.jag.data.Attachments;
import jodd.mail.Email;
import jodd.mail.EmailAttachment;
import jodd.mail.SendMailSession;
import jodd.mail.SmtpServer;
import com.drafail.jag.data.EmailBean;
import com.drafail.jag.data.PropertyLocation;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Properties;
import jodd.mail.MailServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to send an email using an emailBean which holds all the
 * information of said Email
 *
 * @author 1633028
 */
public class Smtp implements Runnable {

    /**
     * Constructor used to instantiate this item
     *
     */
    public Smtp(EmailBean emailbean) {
        this.emailbean = emailbean;
    }

    private final static Logger LOG = LoggerFactory.getLogger(MainApp.class);
    private EmailBean emailbean;
    private Thread thread;

    /**
     * Method used to send an email using a EmailBean which holds all the
     * information of said email
     *
     * @param emailbean The bean whose information will be read to then be sent
     * as an email
     * @throws IllegalArgumentException is thrown if the email of sender,
     * receiver, cc or bcc are invalid. Also if the text and the html msg and
     * the subject are null
     * @throws java.sql.SQLException
     * @throws java.io.IOException
     */
    public void sendEmail() throws IllegalArgumentException, SQLException, IOException {
        if (EmailHelper.checkEmail(emailbean.getSender()) && verifyRecipient(emailbean.getReceiver(), emailbean.getCc(),
                emailbean.getBcc()) && (emailbean.getTextMsg() != null && emailbean.getHtmlMsg() != null
                && emailbean.getSubject() != null)) {
            // Create am SMTP server object
            SmtpServer smtpServer = MailServer.create()
                    .ssl(true)
                    .host(PropertiesAccessor.readProperties(PropertyLocation.getPath(), new String[]{"smtp"}, PropertyLocation.getFileName())[0])
                    .auth(PropertiesAccessor.readProperties(PropertyLocation.getPath(), new String[]{"email"}, PropertyLocation.getFileName())[0],
                            PropertiesAccessor.readProperties(PropertyLocation.getPath(), new String[]{"pass"}, PropertyLocation.getFileName())[0])
                    .buildSmtpMailServer();

            // Using the fluent style of coding create a plain text message
            Properties props = PropertiesAccessor.instantiateProperty(PropertyLocation.getPath(), PropertyLocation.getFileName());

            Email email = Email.create().from(emailbean.getSender())
                    .to(emailbean.getReceiver())
                    .subject(emailbean.getSubject())
                    .priority(emailbean.getMessagePriority());
            email.bcc(emailbean.getBcc());
            email.cc(emailbean.getCc());
            emailbean.setDateSent(LocalDateTime.now().withNano(0));
            email.sentDate(Date.from(emailbean.getDateSent().atZone(ZoneId.systemDefault()).toInstant()));
            verifyAttachments(emailbean, email);
            email.textMessage(emailbean.getTextMsg());
            email.htmlMessage(emailbean.getHtmlMsg());
            DAOHelper helper = new DAOHelper(PropertiesAccessor.readProperties(PropertyLocation.getPath(), new String[]{"sqlURL"}, PropertyLocation.getFileName())[0],
                    PropertiesAccessor.readProperties(PropertyLocation.getPath(), new String[]{"dbUser"}, PropertyLocation.getFileName())[0],
                    PropertiesAccessor.readProperties(PropertyLocation.getPath(), new String[]{"dbPass"}, PropertyLocation.getFileName())[0]);
            helper.addToDatabase(emailbean);
            // Like a file we open the session, send the message and close the
            // session
            try ( // A session is the object responsible for communicating with the server
                    SendMailSession session = smtpServer.createSession()) {
                // Like a file we open the session, send the message and close the
                // session
                session.open();
                session.sendMail(email);
                LOG.debug("Email sent");
            }
        } else {
            LOG.debug("Unable to send email because either send or recieve addresses are invalid");
            throw new IllegalArgumentException();
        }
    }

    private void verifyAttachments(EmailBean eb, Email email) {
        if (eb.getAttachment() != null && eb.getAttachment().length > 0) {
            for (Attachments attach : eb.getAttachment()) {
                email.attachment(EmailAttachment.with().content(attach.getAttachment()).name(attach.getName()));
            }
        }
        if (eb.getEmbAttachments() != null && eb.getEmbAttachments().length > 0) {
            for (Attachments attach : eb.getEmbAttachments()) {
                email.embeddedAttachment(EmailAttachment.with().content(attach.getAttachment()).name(attach.getName()));
                eb.setHtmlMsg(eb.getHtmlMsg() + "<img src=\'cid:" + attach.getName() + "\'>");
            }
        }
    }

    /**
     *
     * @param recipient the receivers of the email bean
     * @param cc the cc of the email bean
     * @param bcc the bcc of the email bean
     * @return boolean to verify if the three arrays contain a valid email or
     * not
     */
    private boolean verifyRecipient(String[] recipient, String[] cc, String[] bcc) {
        if (recipient == null && cc == null && bcc == null) {
            return false;
        }
        if (recipient != null && recipient.length > 0) {
            for (String receiver : recipient) {
                if (!EmailHelper.checkEmail(receiver)) {
                    return false;
                }
            }
        }
        if (cc != null && cc.length > 0) {
            for (String shown : cc) {
                if (!EmailHelper.checkEmail(shown)) {
                    return false;
                }
            }
        }
        if (bcc != null && bcc.length > 0) {
            for (String hidden : bcc) {
                if (!EmailHelper.checkEmail(hidden)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void run() {
        LOG.debug("Thread started");
        try {
            sendEmail();
            LOG.debug("Thread closed");
        } catch (IllegalArgumentException ex) {
            LOG.debug("Illegal Argument Exception : " + ex);
            LOG.debug("Thread closed");
        } catch (SQLException ex) {
            LOG.debug("Error connecting to the database : " + ex);
            LOG.debug("Thread closed");
        } catch (IOException ex) {
            LOG.debug("IOException : " + ex);
            LOG.debug("Thread closed");
        }
    }
    
    public void start() {
        LOG.debug("Thread started");
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

}
