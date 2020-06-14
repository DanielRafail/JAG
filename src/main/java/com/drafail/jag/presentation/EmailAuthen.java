/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drafail.jag.presentation;

import com.drafail.jag.business.Properties.PropertiesAccessor;
import com.drafail.jag.data.Language;
import com.drafail.jag.data.PropertyLocation;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import jodd.mail.MailServer;
import jodd.mail.SendMailSession;
import jodd.mail.SmtpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author 1633028
 */
public class EmailAuthen {

    private final Stage window;
    private final EmailReader emailRead;
    private TextField userInput;
    private PasswordField passInput;
    private Button loginButton;
    private String[] language;
    private BorderPane border;
    private ResourceBundle msg;
    private final Properties props;
    private final static Logger LOG = LoggerFactory.getLogger(EmailAuthen.class);

    /**
     * constructor used to create an EmailAuthen
     *
     * @throws IOException
     */
    public EmailAuthen() throws IOException {
        this.window = new Stage();
        this.emailRead = new EmailReader();
        props = PropertiesAccessor.instantiateProperty(PropertyLocation.getPath(), PropertyLocation.getFileName());
    }

    /**
     * main method used to set up the stage, the scene and the UI
     */
    public void authenticate() {
        String[] key = new String[]{"loginInput", "address", "pass", "invalid"};
        language = PropertiesAccessor.readLanguage(Language.getCurrentLocale(), msg,
                key);
        window.setTitle("JAG");
        window.setMinWidth(250);
        window.setResizable(false);
        border = new BorderPane();
        border.setCenter(addGrid());
        border.setBottom(addVBox(border));
        border.setTop(createMenuBar(
                key, border));
        Scene scene = new Scene(border);
        scene.getStylesheets().add("default.css");
        window.setScene(scene);
        window.getIcons().add(new Image((getClass().getResourceAsStream("/images/icon.png"))));
        window.showAndWait();
        LOG.debug("authenticate called");
    }

    private void addEventToBorder(BorderPane border) {
        border.setOnKeyPressed(ev -> {
            if (ev.getCode() == KeyCode.ENTER) {
                loginButton.fire();
                ev.consume();
            }
        });
    }

    /**
     * creates a menu bar that takes care of localization
     *
     * @param key String array that holds the key used for localization by
     * message bundle
     * @param border the border the menu bar resides in
     * @return the menu bar once it has been set up
     */
    private MenuBar createMenuBar(String[] key, BorderPane border) {

        MenuBar menuBar = new MenuBar();
        Menu menuLanguage = new Menu("Language");
        MenuItem menuEnglish = new MenuItem("EN");
        MenuItem menuFrench = new MenuItem("FR");
        menuEnglish.setOnAction(e -> {
            Language.setCurrentLocale(new Locale("en", "CA"));
            language = PropertiesAccessor.readLanguage(Language.getCurrentLocale(), msg, key);
            border.setCenter(addGrid());
            border.setBottom(addVBox(border));
        });
        menuFrench.setOnAction(e -> {
            Language.setCurrentLocale(new Locale("fr", "CA"));
            language = PropertiesAccessor.readLanguage(Language.getCurrentLocale(), msg, key);
            border.setCenter(addGrid());
            border.setBottom(addVBox(border));
        });

        menuLanguage.getItems().addAll(menuEnglish, menuFrench);
        menuBar.getMenus().addAll(menuLanguage);
        return menuBar;
    }

    /**
     * Creates a Vertical box that holds a button (used to login ) and error
     * Message
     *
     * @return the vertical box
     */
    private VBox addVBox(BorderPane border) {
        Label errorMsg = new Label("");
        loginButton = new Button();
        loginButton.setText(language[0]);
        addEventToBorder(border);
        loginButton.setOnAction(e
                -> {
            SmtpServer smtpServer = MailServer.create()
                    .ssl(true)
                    .host(props.getProperty("smtp"))
                    .auth(userInput.getText(), passInput.getText())
                    .buildSmtpMailServer();
            try (SendMailSession session = smtpServer.createSession()) {
                try {
                    LOG.debug("Attempting to validate email");
                    session.open();
                    LOG.debug("Email validated");
                    emailRead.emailFormWindow();
                    window.close();
                } catch (Exception error) {
                    errorMsg.setTextFill(Color.web("#8B0000"));
                    errorMsg.setText(language[3]);
                    LOG.debug("Email not validated : " + error);
                }
            }
        }
        );
        VBox vbox = new VBox();

        vbox.setPadding(
                new Insets(10));
        vbox.setSpacing(
                8);
        vbox.setAlignment(Pos.CENTER);

        vbox.getChildren()
                .addAll(errorMsg, loginButton);

        return vbox;
    }

    /**
     * creates a grid the user has to fill in order to login
     *
     * @return the grid
     */
    private GridPane addGrid() {
        Label user = new Label(language[1]);
        Label pass = new Label(language[2]);
        userInput = new TextField();
        passInput = new PasswordField();
        passInput.setPromptText(language[2]);
        userInput.setText(props.getProperty("email"));

        GridPane grid = new GridPane();
        grid.setVgap(8);
        grid.setHgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        GridPane.setConstraints(user, 0, 0);
        GridPane.setConstraints(pass, 0, 1);
        GridPane.setConstraints(userInput, 1, 0);
        GridPane.setConstraints(passInput, 1, 1);

        grid.getChildren().addAll(user, pass, userInput, passInput);
        return grid;
    }
}
