/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drafail.jag.data;

import java.util.Arrays;
import java.util.Objects;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Bean which holds all the information of an email
 *
 * @author 1633028
 */
public class EmailBeanFx {

    //sending
    private StringProperty subject;
    private Attachments[] attachment;
    private Attachments[] embAttachments;
    private StringProperty ccString;
    private StringProperty bccString;
    private StringProperty receiverString;

    /**
     * empty constructor
     */
    public EmailBeanFx() {
        this.subject = new SimpleStringProperty("");
        this.ccString = new SimpleStringProperty("");
        this.bccString = new SimpleStringProperty("");
        this.receiverString = new SimpleStringProperty("");
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.subject.get());
        hash = 53 * hash + Arrays.deepHashCode(this.attachment);
        hash = 53 * hash + Arrays.deepHashCode(this.embAttachments);
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
        final EmailBeanFx other = (EmailBeanFx) obj;
        if (!Objects.equals(this.subject.get(), other.subject.get())) {
            return false;
        }
        if (!Arrays.deepEquals(this.attachment, other.attachment)) {
            return false;
        }
        return Arrays.deepEquals(this.embAttachments, other.embAttachments);
    }

    public StringProperty getSubject() {
        return subject;
    }

    public void setSubject(StringProperty subject) {
        this.subject = subject;
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

    public StringProperty getCcString() {
        return ccString;
    }

    public void setCcString(StringProperty ccString) {
        this.ccString = ccString;
    }

    public StringProperty getBccString() {
        return bccString;
    }

    public void setBccString(StringProperty bccString) {
        this.bccString = bccString;
    }

    public StringProperty getReceiverString() {
        return receiverString;
    }

    public void setReceiverString(StringProperty receiverString) {
        this.receiverString = receiverString;
    }

    public String getSubjectString() {
        return subject.get();
    }
    public String getCcStringToString() {
        return ccString.get();
    }

    public String getBccStringToString() {
        return bccString.get();
    }

    public String getReceiverStringToString() {
        return receiverString.get();
    }
}
