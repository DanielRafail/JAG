/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drafail.jag.business.Email;

import com.drafail.jag.business.DAO.DAOHelper;
import com.drafail.jag.business.Properties.PropertiesAccessor;
import com.drafail.jag.data.Attachments;
import com.drafail.jag.data.EmailBean;
import com.drafail.jag.data.PropertyLocation;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Properties;
import javax.activation.DataSource;
import jodd.mail.EmailAttachment;
import jodd.mail.EmailMessage;
import jodd.mail.ImapServer;
import jodd.mail.MailServer;
import jodd.mail.ReceiveMailSession;
import jodd.mail.ReceivedEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.mail.Flags;
import jodd.mail.EmailFilter;

/**
 * Class used to receive all the emails from an email address to store into a
 * bean
 *
 * @author 1633028
 */
public class Imap implements Runnable {

    private final Properties props;
    private Thread thread;
    private final static Logger LOG = LoggerFactory.getLogger(Imap.class);

    /**
     * Constructor used to instantiate an emailReceive item
     *
     * @throws java.io.IOException
     */
    public Imap() throws IOException {
        props = PropertiesAccessor.instantiateProperty(PropertyLocation.getPath(), PropertyLocation.getFileName());
    }

    /**
     * Method which receives all the emails from an email address and takes all
     * the information and stores into a bean
     *
     * @return EmailBean[] array of emailbeans with all the emails
     * @throws java.sql.SQLException
     * @throws java.io.IOException
     */
    public EmailBean[] receiveEmail() throws SQLException, IOException {
        DAOHelper helper = new DAOHelper(PropertiesAccessor.readProperties(PropertyLocation.getPath(), new String[]{"sqlURL"}, PropertyLocation.getFileName())[0],
                PropertiesAccessor.readProperties(PropertyLocation.getPath(), new String[]{"dbUser"}, PropertyLocation.getFileName())[0],
                PropertiesAccessor.readProperties(PropertyLocation.getPath(), new String[]{"dbPass"}, PropertyLocation.getFileName())[0]);
        EmailBean[] emailbean = null;
        if (EmailHelper.checkEmail(props.getProperty("email"))) {
            ImapServer imapServer = MailServer.create()
                    .host(PropertiesAccessor.readProperties(PropertyLocation.getPath(), new String[]{"imap"}, PropertyLocation.getFileName())[0])
                    .ssl(true)
                    .auth("receive.1633028@gmail.com", "8bae86edb")
                    .buildImapMailServer();

            try (ReceiveMailSession session = imapServer.createSession()) {
                session.open();
                ReceivedEmail[] emails = session.receiveEmailAndMarkSeen(EmailFilter.filter().flag(Flags.Flag.SEEN, false));
                if (emails != null && emails.length > 0) {
                    emailbean = new EmailBean[emails.length];
                    for (int x = 0; x < emails.length; x++) {
                        emailbean[x] = new EmailBean();
                        emailbean[x].setSender(emails[x].from().getEmail());
                        String[] receive = new String[emails.length];
                        String[] cc = new String[emails.length];
                        setRecipients(receive, cc, emails, x, emailbean);
                        emailbean[x].setSubject(replaceNull(emails[x].subject()));
                        emailbean[x].setName(emails[x].from().getPersonalName());
                        emailbean[x].setMessagePriority(emails[x].priority());
                        emailbean[x].setDateReceived(LocalDateTime.ofInstant(emails[x].receivedDate().toInstant(), ZoneId.systemDefault()));
                        emailbean[x].setDateSent(LocalDateTime.ofInstant(emails[x].sentDate().toInstant(), ZoneId.systemDefault()));
                        emailbean[x].setFolder("Inbox");
                        setAttachments(emailbean, x, emails);
                        setMessages(emailbean, x, emails);
                        helper.addToDatabase(emailbean[x]);
                    }
                } else {
                    LOG.debug("Unable to send email because either send or recieve addresses are invalid");
                }
            }
            return emailbean;
        }
        return null;
    }

    private String replaceNull(String str) {
        if (str == null) {
            str = "";
        }
        return str;
    }

    private void setRecipients(String[] receive, String[] cc, ReceivedEmail[] emails, int arrayLoc, EmailBean emailbean[]) {
        for (int i = 0; i < emails[arrayLoc].to().length; i++) {
            receive[i] = emails[arrayLoc].to()[i].toString();
        }
        emailbean[arrayLoc].setReceiver(receive);

        for (int i = 0; i < emails[arrayLoc].cc().length; i++) {
            cc[i] = emails[arrayLoc].cc()[i].toString();
        }
        emailbean[arrayLoc].setCc(cc);
    }

    private void setMessages(EmailBean eb[], int arrayLoc, ReceivedEmail[] emails) {
        // process messages
        List<EmailMessage> messages = emails[arrayLoc].messages();
        messages.forEach((message) -> {
            if (message.getMimeType().equals("TEXT/PLAIN")) {
                eb[arrayLoc].setTextMsg(eb[arrayLoc].getTextMsg() + removeCID(message.getContent()));
            } else if (message.getMimeType().equals("TEXT/HTML")) {
                eb[arrayLoc].setHtmlMsg(eb[arrayLoc].getHtmlMsg() + removeCID(message.getContent()));
            }
        });
    }

    private String removeCID(String str) {
        if (str.contains("cid")) {
            int loc = str.indexOf("<img");
            str = str.substring(0, loc) + str.substring(str.indexOf(">", loc) + 1);
        }
        return str;
    }

    private void setAttachments(EmailBean eb[], int arrayLoc, ReceivedEmail[] emails) {
        // process attachments
        List<EmailAttachment<? extends DataSource>> attachments = emails[arrayLoc].attachments();
        Attachments attachment;
        Attachments[] item;
        if (attachments != null && attachments.size() > 0) {
            for (EmailAttachment attach : attachments) {
                attachment = new Attachments(attach.getName(), attach.toByteArray());
                if (attach.getContentId() == null) {
                    item = AttachmentHelper.add(eb[arrayLoc].getAttachment(), attachment);
                    eb[arrayLoc].setAttachment(item);
                } else {
                    item = AttachmentHelper.add(eb[arrayLoc].getEmbAttachments(), attachment);
                    eb[arrayLoc].setEmbAttachments(item);
                }
            }
        }
    }

    public void start() {
        LOG.debug("Thread started");
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    @Override
    public void run() {
        try {
            receiveEmail();
        } catch (SQLException ex) {
            LOG.debug("Error getting into the database: " + ex);
            LOG.debug("Thread closed");
        } catch (IOException ex) {
            LOG.debug("IOException error :" + ex);
            LOG.debug("Thread closed");
        }
    }

}
