/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drafail.jag.business.javaFX;

import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

/**
 *
 * @author 1633028
 */
public class GridHelper {

    /**
     * Private method
     */
    private GridHelper() {
    }

    /**
     * Create the grid and return it
     * @param grid the grid we instantiate
     * @return the grid with values instantiated
     */
    public static GridPane setupGrid(GridPane grid) {
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);
        return grid;
    }

    /**
     * Add columns to the grid
     * @param grid the grid we add columns to
     * @return the same grid with added columns
     */
    public static GridPane addColumns(GridPane grid) {
        ColumnConstraints col1 = new ColumnConstraints();
        ColumnConstraints col2 = new ColumnConstraints();

        col1.setPercentWidth(10);
        col2.setPercentWidth(90);

        grid.getColumnConstraints().addAll(col1, col2);
        return grid;
    }

    /**
     * Create a label in a grid
     * @param grid the grid we add the label in
     * @param str the value of the label
     * @param colNum the number of the column of the grid we add it to
     * @param rowNum the row of the column of the grid we add it to
     * @return the label created
     */
    public static Label createGridLabel(GridPane grid, String str, int colNum, int rowNum) {
        Label input = new Label(str);
        GridPane.setConstraints(input, colNum, rowNum);
        return input;
    }

    /**
     * create a text field in a grid
     * @param grid  the grid we add the field to
     * @param colNum the number of the column of the grid we add it to
     * @param rowNum the number of the row of the grid we add it to
     * @param str the string property we bind the text field to
     * @return 
     */
    public static TextField createGridTextField(GridPane grid, int colNum, int rowNum, StringProperty str) {
        TextField answer = new TextField();
        bind(answer, str);
        GridPane.setConstraints(answer, colNum, rowNum);
        return answer;
    }

    /**
     * create a text field with a prompt
     * @param grid the grid we add it to
     * @param colNum the the number of the column of the grid we add it to
     * @param rowNum the number of the row of the grid we add it to
     * @param str the string property we bind the text field to
     * @param prompt the prompt inside the text field
     * @return  the new text field
     */
    public static TextField createGridTextFieldPrompt(GridPane grid, int colNum, int rowNum, StringProperty str, String prompt) {
        TextField answer = new TextField();
        bind(answer, str);
        GridPane.setConstraints(answer, colNum, rowNum);
        answer.setPromptText(prompt);
        return answer;
    }
    
    /**
     * create a text field with a default value
     * @param grid the grid we add it to
     * @param colNum the column number of the grid we add it to
     * @param rowNum the row number of the grid we add it to
     * @param str the string property we bind the text field to
     * @param defaultValue the default value
     * @return 
     */
        public static TextField createGridTextFieldSet(GridPane grid, int colNum, int rowNum, StringProperty str, String defaultValue) {
        TextField answer = new TextField();
        bind(answer, str);
        GridPane.setConstraints(answer, colNum, rowNum);
        answer.setText(defaultValue);
        return answer;
    }
    
    /**
     * create a password field
     * @param grid the grid we add it to 
     * @param colNum the column number of the grid we add it to
     * @param rowNum the row number of the grid we add it to
     * @param str the string property we bind it to
     * @return  the new password field
     */
    public static PasswordField createGridPasswordField(GridPane grid, int colNum, int rowNum, StringProperty str) {
        PasswordField answer = new PasswordField();
        bind(answer, str);
        GridPane.setConstraints(answer, colNum, rowNum);
        return answer;
    }

    /**
     * create a password field and give it a prompt
     * @param grid the grid we add it to
     * @param colNum the column of the grid we add it to
     * @param rowNum the row of the grid we add it to
     * @param str the string property we bind it to
     * @param prompt the prompt inside the password field
     * @return 
     */
    public static PasswordField createGridPasswordFieldPrompt(GridPane grid, int colNum, int rowNum, StringProperty str, String prompt) {
        PasswordField answer = new PasswordField();
        bind(answer, str);
        GridPane.setConstraints(answer, colNum, rowNum);
        answer.setPromptText(prompt);
        return answer;
    }

    /**
     * This code binds the properties of the data bean to the JavaFX controls.
     * Changes to a control is immediately written to the bean and a change to
     * the bean is immediately shown in the control.
     */
    private static void bind(TextField txt, StringProperty str) {
        Bindings.bindBidirectional(txt.textProperty(), str);
    }
}
