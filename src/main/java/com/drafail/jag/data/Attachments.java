/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drafail.jag.data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author 1633028
 */
public class Attachments implements Serializable{
 
    String name;
    byte[] attachment;
    
    public Attachments(String name, byte[] attach){
    this.name = name;
    this.attachment = attach;
    }

    /**
     * Overridden hashcode which is baser off the byte array and the name of the attachment
     * @return int as hashcode
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.name);
        hash = 29 * hash + Arrays.hashCode(this.attachment);
        return hash;
    }

    /**
     * Overridden equals which is baser off the byte array and the name of the attachment
     * @param obj the object the attachment is compared to
     * @return true or false based on if its equals to each other
     */
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
        final Attachments other = (Attachments) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return Arrays.equals(this.attachment, other.attachment);
    }


    /**
     * Returns the name of the attachment
     * @return string name of attachment
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the attachment
     * @param name String name of the attachment
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the byte array of the attachment
     * @return byte array of attachment
     */
    public byte[] getAttachment() {
        return attachment;
    }

    /**
     * Sets the byte array of the attachment
     * @param attachment byte array of the attachment which is going to bet set
     */
    public void setAttachment(byte[] attachment) {
        this.attachment = attachment;
    }
    
    
}
