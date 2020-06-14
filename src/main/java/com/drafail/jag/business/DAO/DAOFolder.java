/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drafail.jag.business.DAO;

import com.drafail.jag.data.DatabaseBean;
import com.drafail.jag.data.EmailBean;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author 1633028
 */
public class DAOFolder extends DatabaseInteraction {

    private final static Logger LOG = LoggerFactory.getLogger(DAOFolder.class);
    private final DatabaseBean data;

    /**
     * Constructor to build a DAOFolder to add a folder to the database
     *
     * @param connection string to connect to the database
     * @param id the id of the user
     * @param pass the password of the user
     */
    public DAOFolder(String connection, String id, String pass) {
        super(connection, id, pass);
        data = returnData();

    }

    /**
     * method to add a folder to the database
     *
     * @param emailBean the emailBean we get the values from to add
     * @throws SQLException
     */
    public void addFolder(EmailBean emailBean) throws SQLException {
        Connection conn = data.connect();
        if (!findFolder(emailBean)) {
            try (PreparedStatement pStmt = conn.prepareStatement("{call emails.addFolders(?)}")) {
                getFolderPrepared(pStmt, 1, emailBean);

                pStmt.execute();
            }
            LOG.debug("Folder Added to database");
        } else {
            LOG.debug("Folder already in database. Failed to add");
            conn.close();
        }
    }

    /**
     * method to add a folder to the database
     *
     * @param str String used to search in the database for the folder
     * @throws SQLException
     */
    public void addFolder(String str) throws SQLException {
        Connection conn = data.connect();
        if (!findFolder(str)) {
            try (PreparedStatement pStmt = conn.prepareStatement("{call emails.addFolders(?)}")) {
                getFolderPrepared(pStmt, 1, str);

                pStmt.execute();
            }
            LOG.debug("Folder Added to database");
        } else {
            LOG.debug("Folder already in database. Failed to add");
            conn.close();
        }
    }

    /**
     * Method to delete a folder from the database
     *
     * @param emailBean the emailBean get the values from to delete
     * @throws SQLException
     */
    public void deleteFolder(EmailBean emailBean) throws SQLException {
        try (Connection conn = data.connect()) {
            if (findFolder(emailBean)) {
                try (PreparedStatement pStmt = conn.prepareStatement("{call emails.deleteFolders(?)}")) {
                    getFolderPrepared(pStmt, 1, emailBean);

                    pStmt.execute();
                }
                LOG.debug("Folder deleted from database");
            } else {
                LOG.debug("Folder not in database. Failed to delete");
            }
        }
    }

    /**
     * Method to delete a folder from the database
     *
     * @param str the string used to identify the folder and delete it
     * @throws SQLException
     */
    public void deleteFolder(String str) throws SQLException {
        try (Connection conn = data.connect()) {
            if (findFolder(str)) {
                try (PreparedStatement pStmt = conn.prepareStatement("{call emails.deleteFolders(?)}")) {
                    getFolderPrepared(pStmt, 1, str);

                    pStmt.execute();
                }
                LOG.debug("Folder deleted from database");
            } else {
                LOG.debug("Folder not in database. Failed to delete");
            }
        }
    }

    /**
     * Method to find a folder in the database
     *
     * @param emailBean the emailbean we get our values from to find
     * @return boolean based on if the folder was found
     * @throws SQLException
     */
    public boolean findFolder(EmailBean emailBean) throws SQLException {
        String returnValue;
        try (Connection connect = data.connect()) {
            CallableStatement pStmt = connect.prepareCall("{ ? = call emails.findFolders(?) }");
            pStmt.registerOutParameter(1, Types.CHAR);
            getFolderPrepared(pStmt, 2, emailBean);
            pStmt.execute();
            returnValue = pStmt.getString(1);
        }
        return !"0".equals(returnValue);
    }

    /**
     * method to prepare the prepared statement
     *
     * @param pStmt the prepared statement
     * @param startingValue the starting value (differentiate between functions
     * and procedures)
     * @param emailBean the emailbean we get the values from
     * @throws SQLException
     */
    private void getFolderPrepared(PreparedStatement pStmt, int startingValue, EmailBean emailBean) throws SQLException {
        pStmt.setString(startingValue, emailBean.getFolder());
    }

    /**
     * Method to find a folder in the database
     *
     * @param str String used to find the folder in the database
     * @return boolean based on if the folder was found
     * @throws SQLException
     */
    public boolean findFolder(String str) throws SQLException {
        String returnValue;
        try (Connection connect = data.connect()) {
            CallableStatement pStmt = connect.prepareCall("{ ? = call emails.findFolders(?) }");
            pStmt.registerOutParameter(1, Types.CHAR);
            getFolderPrepared(pStmt, 2, str);
            pStmt.execute();
            returnValue = pStmt.getString(1);
        }
        return !"0".equals(returnValue);
    }

    /**
     * method to prepare the prepared statement
     *
     * @param pStmt the prepared statement
     * @param startingValue the starting value (differentiate between functions
     * and procedures)
     * @param str the string we use to prepare the statement with
     * @throws SQLException
     */
    private void getFolderPrepared(PreparedStatement pStmt, int startingValue, String str) throws SQLException {
        pStmt.setString(startingValue, str);
    }

    /**
     * get all the folders in the database
     *
     * @return array with all the strings
     * @throws SQLException
     */
    public String[] getFolder() throws SQLException {
        List<String> returnList;
        try (Connection connect = data.connect();) {
            CallableStatement pStmt = connect.prepareCall("select name from folders");
            returnList = new ArrayList<>();
            try (ResultSet rs = pStmt.executeQuery();) {
                while (rs.next()) {
                    returnList.add(rs.getString(1));
                }
            }
        }
        return returnList.toArray(new String[1]);
    }

    /**
     * method used to rename a folder in the database
     *
     * @param old the old name of the folder
     * @param newName the new name of the folder
     */
    public void renameFolder(String old, String newName) throws SQLException {
        try (Connection connect = data.connect();) {
            CallableStatement pStmt = connect.prepareCall("Update folders set name = ? where name = ?");
            pStmt.setString(2, old);
            pStmt.setString(1, newName);
            pStmt.execute();
        }
    }

}
