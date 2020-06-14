/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drafail.jag.business.Email;

import com.drafail.jag.data.Attachments;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author 1633028
 */
public class AttachmentHelper {
    
    private AttachmentHelper(){}

    /**
     *
     * @param obj the Attachments array which we will add the item to
     * @param toAdd the Attachment we will add to the array
     * @return obj array with an item added at its end
     */
    public static Attachments[] add(Attachments[] obj, Attachments toAdd) {
        boolean created = false;
        if (obj == null || obj.length <= 0) {
            obj = new Attachments[1];
            created = true;
        }
        if (toAdd != null) {
            Attachments[] returnArray;
            if (!created) {
                returnArray = Arrays.copyOf(obj, obj.length + 1);
            } else {
                returnArray = obj;
            }
            returnArray[returnArray.length - 1] = toAdd;
            return returnArray;
        }
        return null;
    }

    public static Attachments[] remove(Attachments[] obj, Attachments toDelete) {
        if (obj == null || obj.length <= 0) {
            return null;
        }
        if (toDelete != null) {
            List<Attachments> list = new LinkedList<>(Arrays.asList(obj));
            list.remove(toDelete);
            return list.toArray(new Attachments[list.size()]);
        }
        return null;
    }
}
