/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drafail.jag.data;

import java.io.Serializable;
import java.util.Objects;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
  * Bean used to hold all the information of the configuration page
 * @author 1633028
 */
public class ConfigurationBean implements Serializable {

    StringProperty name;
    StringProperty address;
    StringProperty addressPass;
    StringProperty imap;
    StringProperty smtp;
    StringProperty imapPort;
    StringProperty smtpPort;
    StringProperty sqlURL;
    StringProperty sqlPort;

    StringProperty dbName;
    StringProperty user;
    StringProperty userPass;

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.name);
        hash = 37 * hash + Objects.hashCode(this.address);
        hash = 37 * hash + Objects.hashCode(this.addressPass);
        hash = 37 * hash + Objects.hashCode(this.imap);
        hash = 37 * hash + Objects.hashCode(this.smtp);
        hash = 37 * hash + Objects.hashCode(this.imapPort);
        hash = 37 * hash + Objects.hashCode(this.smtpPort);
        hash = 37 * hash + Objects.hashCode(this.sqlURL);
        hash = 37 * hash + Objects.hashCode(this.sqlPort);
        hash = 37 * hash + Objects.hashCode(this.dbName);
        hash = 37 * hash + Objects.hashCode(this.user);
        hash = 37 * hash + Objects.hashCode(this.userPass);
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
        final ConfigurationBean other = (ConfigurationBean) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.address, other.address)) {
            return false;
        }
        if (!Objects.equals(this.addressPass, other.addressPass)) {
            return false;
        }
        if (!Objects.equals(this.imap, other.imap)) {
            return false;
        }
        if (!Objects.equals(this.smtp, other.smtp)) {
            return false;
        }
        if (!Objects.equals(this.imapPort, other.imapPort)) {
            return false;
        }
        if (!Objects.equals(this.smtpPort, other.smtpPort)) {
            return false;
        }
        if (!Objects.equals(this.sqlURL, other.sqlURL)) {
            return false;
        }
        if (!Objects.equals(this.sqlPort, other.sqlPort)) {
            return false;
        }
        if (!Objects.equals(this.dbName, other.dbName)) {
            return false;
        }
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        return Objects.equals(this.userPass, other.userPass);
    }

    public StringProperty getName() {
        return name;
    }

    public void setName(StringProperty name) {
        this.name = name;
    }

    public StringProperty getAddress() {
        return address;
    }

    public void setAddress(StringProperty address) {
        this.address = address;
    }

    public StringProperty getAddressPass() {
        return addressPass;
    }

    public void setAddressPass(StringProperty addressPass) {
        this.addressPass = addressPass;
    }

    public StringProperty getImap() {
        return imap;
    }

    public void setImap(StringProperty imap) {
        this.imap = imap;
    }

    public StringProperty getSmtp() {
        return smtp;
    }

    public void setSmtp(StringProperty smtp) {
        this.smtp = smtp;
    }

    public StringProperty getImapPort() {
        return imapPort;
    }

    public void setImapPort(StringProperty imapPort) {
        this.imapPort = imapPort;
    }

    public StringProperty getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(StringProperty smtpPort) {
        this.smtpPort = smtpPort;
    }

    public StringProperty getSqlURL() {
        return sqlURL;
    }

    public void setSqlURL(StringProperty sqlURL) {
        this.sqlURL = sqlURL;
    }

    public StringProperty getDbName() {
        return dbName;
    }

    public void setDbName(StringProperty dbName) {
        this.dbName = dbName;
    }

    public StringProperty getUser() {
        return user;
    }

    public void setUser(StringProperty user) {
        this.user = user;
    }

    public StringProperty getUserPass() {
        return userPass;
    }

    public void setUserPass(StringProperty userPass) {
        this.userPass = userPass;
    }

    public StringProperty getSqlPort() {
        return sqlPort;
    }

    public void setSqlPort(StringProperty sqlPort) {
        this.sqlPort = sqlPort;
    }

    public String getSqlPortString() {
        return sqlPort.get();
    }

    public String getNameString() {
        return name.get();
    }

    public String getAddressString() {
        return address.get();
    }

    public String getAddressPassString() {
        return addressPass.get();
    }

    public String getImapString() {
        return imap.get();
    }

    public String getSmtptString() {
        return smtp.get();
    }

    public String getImapPortString() {
        return imapPort.get();
    }

    public String getSmtpPortString() {
        return smtpPort.get();
    }

    public String getSqlUrlString() {
        return sqlURL.get();
    }

    public String getDbNameString() {
        return dbName.get();
    }

    public String getUserString() {
        return user.get();
    }

    public String getUserPassString() {
        return userPass.get();
    }

    public ConfigurationBean(StringProperty name, StringProperty address, StringProperty addressPass, StringProperty imap, StringProperty smtp, StringProperty imapPort, StringProperty smtpPort, StringProperty sqlURL, StringProperty dbName, StringProperty user, StringProperty userPass, StringProperty sqlPort) {
        this.name = name;
        this.address = address;
        this.addressPass = addressPass;
        this.imap = imap;
        this.smtp = smtp;
        this.imapPort = imapPort;
        this.smtpPort = smtpPort;
        this.sqlURL = sqlURL;
        this.dbName = dbName;
        this.user = user;
        this.userPass = userPass;
        this.sqlPort = sqlPort;
    }

    public ConfigurationBean() {
        this.name = new SimpleStringProperty("");
        this.address = new SimpleStringProperty("");
        this.addressPass = new SimpleStringProperty("");
        this.imap = new SimpleStringProperty("");
        this.smtp = new SimpleStringProperty("");
        this.imapPort = new SimpleStringProperty("");
        this.smtpPort = new SimpleStringProperty("");
        this.sqlURL = new SimpleStringProperty("");
        this.dbName = new SimpleStringProperty("");
        this.user = new SimpleStringProperty("");
        this.userPass = new SimpleStringProperty("");
        this.sqlPort = new SimpleStringProperty("");

    }

}
