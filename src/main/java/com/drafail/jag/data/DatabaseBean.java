/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drafail.jag.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

/**
 *
 * @author 1633028
 */
public class DatabaseBean {

    private String connectionString;
    private String sqlID;
    private String sqlPassword;

    /**
     * method to change the hashcode of the database bean based on different
     * parameters
     *
     * @return the new hashcode
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.connectionString);
        hash = 53 * hash + Objects.hashCode(this.sqlID);
        hash = 53 * hash + Objects.hashCode(this.sqlPassword);
        return hash;
    }

    /**
     * method to change the equal of the database bean based on different
     * parameters
     *
     * @param obj the object we compare the database bean to
     * @return boolean based on if the obj is equals to the database bean
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
        final DatabaseBean other = (DatabaseBean) obj;
        if (!Objects.equals(this.connectionString, other.connectionString)) {
            return false;
        }
        if (!Objects.equals(this.sqlID, other.sqlID)) {
            return false;
        }
        return Objects.equals(this.sqlPassword, other.sqlPassword);
    }

    /**
     * getter for the string of the connection
     *
     * @return string for the string of the connection
     */
    public String getConnectionString() {
        return connectionString;
    }

    /**
     * setter for the string of the connection
     * @param connectionString the string we will replaced the string with 
     */
    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    /**
     * getter for the id of the user
     * @return the id of the user
     */
    public String getSqlID() {
        return sqlID;
    }

    /**
     * setter for the id of the user
     * @param sqlID  the new id of the user 
     */
    public void setSqlID(String sqlID) {
        this.sqlID = sqlID;
    }

    /**
     * getter for the password of the user
     * @return  the password of the user
     */
    public String getSqlPassword() {
        return sqlPassword;
    }

    /**
     * setter for the password of the user
     * @param sqlPassword new password of the user
     */
    public void setSqlPassword(String sqlPassword) {
        this.sqlPassword = sqlPassword;
    }

    /**
     * constructor to build the database bean
     * @param connection the connection to the database
     * @param id the id of the user
     * @param pass  the password of the user
     */
    public DatabaseBean(String connection, String id, String pass) {
        this.connectionString = connection;
        this.sqlID = id;
        this.sqlPassword = pass;
    }

    /**
     * Method used to connect to the database
     * @return the connection item
     * @throws SQLException 
     */
    public Connection connect() throws SQLException {
        Connection connection;
        connection = DriverManager.getConnection(connectionString, sqlID, sqlPassword);
        return connection;
    }
}
