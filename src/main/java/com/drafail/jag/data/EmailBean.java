/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drafail.jag.data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author 1633028
 */
public class EmailBean implements Serializable {

    //sending
    private String name;
    private int emailKey;
    private String sender;
    private String[] receiver;
    private String[] cc;
    private String[] bcc;
    private String subject;
    private String textMsg;
    private String htmlMsg;
    private String folder;
    private Attachments[] attachment;
    private Attachments[] embAttachments;

    //receiving
    private LocalDateTime dateSent;
    private LocalDateTime dateReceived;
    private int messagePriority = 3;
    private Boolean read = false;

    /**
     * empty constructor
     */
    public EmailBean() {
        this.sender = "";
        this.subject = "";
        this.textMsg = "";
        this.htmlMsg = "";
        this.folder = "";
    }

    /**
     * parametized constructor
     */
    public EmailBean(String sender, String[] receiver, String[] cc, String[] bcc, String subject, String textMsg, String htmlMsg, Attachments[] attachment, Attachments[] embAttachments, LocalDateTime date, int priority, boolean read) {
        this.sender = sender;
        if (receiver != null) {
            this.receiver = new String[receiver.length];
            for (int i = 0; i < receiver.length; i++) {
                this.receiver[i] = receiver[i];
            }
        }
        if (cc != null) {
            this.cc = new String[cc.length];
            for (int i = 0; i < cc.length; i++) {
                this.cc[i] = cc[i];
            }
        }
        if (bcc != null) {
            this.bcc = new String[bcc.length];
            for (int i = 0; i < bcc.length; i++) {
                this.bcc[i] = bcc[i];
            }
        }
        this.subject = subject;
        this.textMsg = textMsg;
        this.htmlMsg = htmlMsg;
        this.attachment = attachment;
        this.embAttachments = embAttachments;
        this.dateSent = date;
        this.read = read;
        this.messagePriority = priority;
        this.folder = "Sent";
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.sender);
        hash = 97 * hash + Arrays.deepHashCode(this.receiver);
        hash = 97 * hash + Arrays.deepHashCode(this.cc);
        hash = 97 * hash + Objects.hashCode(this.subject);
        hash = 97 * hash + Objects.hashCode(this.textMsg);
        hash = 97 * hash + Objects.hashCode(this.htmlMsg);
        hash = 97 * hash + Objects.hashCode(this.folder);
        hash = 97 * hash + Arrays.deepHashCode(this.attachment);
        hash = 97 * hash + Arrays.deepHashCode(this.embAttachments);
        hash = 97 * hash + this.messagePriority;
        hash = 97 * hash + Objects.hashCode(this.read);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EmailBean other = (EmailBean) obj;
        if (this.messagePriority != other.messagePriority) {
            return false;
        }
        if (!Objects.equals(this.sender, other.sender)) {
            return false;
        }
        if (!Objects.equals(this.subject, other.subject)) {
            return false;
        }
        if (!Objects.equals(this.textMsg, other.textMsg)) {
            return false;
        }
        if (!Objects.equals(this.htmlMsg, other.htmlMsg)) {
            return false;
        }
        if (!Objects.equals(this.folder, other.folder)) {
            return false;
        }
        if (!Arrays.deepEquals(this.receiver, other.receiver)) {
            return false;
        }
        if (!Arrays.deepEquals(this.cc, other.cc)) {
            return false;
        }
        if (!Arrays.deepEquals(this.attachment, other.attachment)) {
            return false;
        }
        if (!Arrays.deepEquals(this.embAttachments, other.embAttachments)) {
            return false;
        }
        return Objects.equals(this.read, other.read);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public int getEmailKey() {
        return emailKey;
    }

    public void setEmailKey(int emailKey) {
        this.emailKey = emailKey;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String[] getReceiver() {
        return receiver;
    }

    public void setReceiver(String[] receiver) {
        this.receiver = receiver;
    }

    public String[] getCc() {
        return cc;
    }

    public void setCc(String[] cc) {
        this.cc = cc;
    }

    public String[] getBcc() {
        return bcc;
    }

    public void setBcc(String[] bcc) {
        this.bcc = bcc;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTextMsg() {
        return textMsg;
    }

    public void setTextMsg(String textMsg) {
        this.textMsg = textMsg;
    }

    public String getHtmlMsg() {
        return htmlMsg;
    }

    public void setHtmlMsg(String htmlMsg) {
        this.htmlMsg = htmlMsg;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public Attachments[] getAttachment() {
        return attachment;
    }

    public void setAttachment(Attachments[] attachment) {
        this.attachment = attachment;
    }

    public Attachments[] getEmbAttachments() {
        return embAttachments;
    }

    public void setEmbAttachments(Attachments[] embAttachments) {
        this.embAttachments = embAttachments;
    }

    public LocalDateTime getDateSent() {
        return dateSent;
    }

    public void setDateSent(LocalDateTime dateSent) {
        this.dateSent = dateSent;
    }

    public LocalDateTime getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(LocalDateTime dateReceived) {
        this.dateReceived = dateReceived;
    }

    public int getMessagePriority() {
        return messagePriority;
    }

    public void setMessagePriority(int messagePriority) {
        this.messagePriority = messagePriority;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

}
