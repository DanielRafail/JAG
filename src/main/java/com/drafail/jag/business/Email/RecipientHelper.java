/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drafail.jag.business.Email;

import java.util.Arrays;

/**
 *
 * @author 1633028
 */
public class RecipientHelper {

    private RecipientHelper() {
    }

    /**
     *
     * @param obj the Attachments array which we will add the item to
     * @param toAdd the Attachment we will add to the array
     * @return obj array with an item added at its end
     */
    public static String[] add(String[] obj, String toAdd) {
        boolean created = false;
        if (obj == null || obj.length <= 0) {
            obj = new String[1];
            created = true;
        }
        if (toAdd != null) {
            String[] returnValue;
            if (!created) {
                returnValue = Arrays.copyOf(obj, obj.length + 1);
            } else {
                returnValue = obj;
            }
            returnValue[returnValue.length - 1] = toAdd;
            return returnValue;
        }
        return null;
    }
}
