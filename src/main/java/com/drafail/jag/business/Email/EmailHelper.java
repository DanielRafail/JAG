/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drafail.jag.business.Email;

import jodd.mail.RFC2822AddressParser;

/**
 *
 * @author 1633028
 */
public class EmailHelper {

    private EmailHelper() {
    }

    /**
     * Use the RFC2822AddressParser to validate that the email string could be a
     * valid address
     *
     * @param address
     * @return true is OK, false if not
     */
    public static boolean checkEmail(String address) {
        if (address != null) {
            return RFC2822AddressParser.STRICT.parseToEmailAddress(address) != null;
        } else {
            return false;
        }
    }
}
