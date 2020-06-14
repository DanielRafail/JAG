/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drafail.jag.presentation;

import com.drafail.jag.business.javaFX.GridHelper;
import com.drafail.jag.business.Properties.PropertiesAccessor;
import com.drafail.jag.data.ConfigurationBean;
import com.drafail.jag.data.Language;
import com.drafail.jag.data.PropertyLocation;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import static java.nio.file.Paths.get;
import java.util.Properties;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jodd.mail.MailServer;
import jodd.mail.SendMailSession;
import jodd.mail.SmtpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author 1633028
 */
public class EmailConfig {

    private EmailAuthen emailAuthen;
    private Stage primaryStage;
    private final Stage window = new Stage();
    private ResourceBundle msg;
    private String[] language;
    private final ConfigurationBean cf = new ConfigurationBean();
    private EmailReader emailRead;
    private final Properties props;
    private TextField nameInput;
    private TextField emailInput;
    private PasswordField emailPassInput;
    private TextField imapInput;
    private TextField smtpInput;
    private TextField imapPortInput;
    private TextField smtpPortInput;
    private TextField sqlURLInput;
    private TextField sqlNameInput;
    private TextField sqlPortInput;
    private TextField userInput;
    private Button send;
    private PasswordField userPassInput;
    private final static Logger LOG = LoggerFactory.getLogger(EmailConfig.class);

    /**
     * constructor used to create an EmailConfig
     *
     * @throws IOException
     */
    public EmailConfig() throws IOException {
        props = PropertiesAccessor.instantiateProperty(PropertyLocation.getPath(), PropertyLocation.getFileName());
    }

    /**
     * constructor used to create an EmailConfig with a parent stage
     *
     * @throws IOException
     */
    public EmailConfig(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        props = PropertiesAccessor.instantiateProperty(PropertyLocation.getPath(), PropertyLocation.getFileName());
    }

    /**
     * Method used to add events on the border
     *
     * @param border the border parent
     */
    private void addEventToBorder(BorderPane border) {
        border.setOnKeyPressed(ev -> {
            if (ev.getCode() == KeyCode.ENTER) {
                send.fire();
                ev.consume();
            }
        });
    }

    /**
     * Main method used to create scene, stage, and set up UUI
     *
     * @throws java.io.IOException
     */
    public void acceptValues() throws IOException {
        Path path = get(PropertyLocation.getPath(), PropertyLocation.getFileName());
        if (!Files.exists(path) || primaryStage != null) {
            String[] key = new String[]{"name", "address", "pass", "imap", "smtp", "imapPort", "smtpPort", "sqlUrl", "dbName", "sqlPort",
                "user", "pass", "save", "errorForm"};
            language = PropertiesAccessor.
                    readLanguage(Language.getCurrentLocale(), msg, key);
            window.setWidth(1250);
            window.setHeight(600);
            window.setTitle("JAG");
            BorderPane border = new BorderPane();
            border.setPadding(new Insets(10, 50, 10, 10));
            border.setCenter(addGrid());
            border.setTop(createMenuBar(
                    key, border));
            border.setBottom(addSend(border));
            Scene emailForm = new Scene(border);
            emailForm.getStylesheets().add("default.css");
            window.setScene(emailForm);
            if (primaryStage != null) {
                if (window.getModality() != Modality.WINDOW_MODAL) {
                    window.initModality(Modality.WINDOW_MODAL);
                    window.initOwner(primaryStage);
                }
            }
            window.getIcons().add(new Image((getClass().getResourceAsStream("/images/icon.png"))));
            window.show();
            LOG.debug("Main method from emailconfig called");
        } else {
            LOG.debug("Properties already exist, going to authenticate");
            emailAuthen = new EmailAuthen();
            emailAuthen.authenticate();
            window.close();
        }
    }

    /**
     * Menu bar used for localization
     *
     * @param key String array used for localization that holds all the key
     * values in the message bundle
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
            border.setBottom(addSend(border));
        });
        menuFrench.setOnAction(e -> {
            Language.setCurrentLocale(new Locale("fr", "CA"));
            language = PropertiesAccessor.readLanguage(Language.getCurrentLocale(), msg, key);
            border.setCenter(addGrid());
            border.setBottom(addSend(border));
        });

        menuLanguage.getItems().addAll(menuEnglish, menuFrench);
        menuBar.getMenus().addAll(menuLanguage);
        return menuBar;
    }

    /**
     * Create a grid the user has to fill in only in his first interaction with
     * the program to set up his account
     *
     * @return the grid created
     */
    private GridPane addGrid() {
        GridPane grid = new GridPane();

        grid = GridHelper.setupGrid(grid);
        grid = GridHelper.addColumns(grid);

        Label name = GridHelper.createGridLabel(grid, language[0], 0, 0);
        Label address = GridHelper.createGridLabel(grid, language[1], 0, 1);
        Label addressPassword = GridHelper.createGridLabel(grid, language[2], 0, 2);
        Label imap = GridHelper.createGridLabel(grid, language[3], 0, 3);
        Label smtp = GridHelper.createGridLabel(grid, language[4], 0, 4);
        Label imapPort = GridHelper.createGridLabel(grid, language[5], 0, 5);
        Label smtpPort = GridHelper.createGridLabel(grid, language[6], 0, 6);
        Label sqlURL = GridHelper.createGridLabel(grid, language[7], 0, 7);
        Label dbName = GridHelper.createGridLabel(grid, language[8], 0, 8);
        Label sqlPort = GridHelper.createGridLabel(grid, language[9], 0, 9);
        Label user = GridHelper.createGridLabel(grid, language[10], 0, 10);
        Label userPass = GridHelper.createGridLabel(grid, language[11], 0, 11);

        nameInput = GridHelper.createGridTextFieldPrompt(grid, 1, 0, cf.getName(), "Bob");
        emailInput = GridHelper.createGridTextFieldPrompt(grid, 1, 1, cf.getAddress(), "MyEmail@address.com");
        emailPassInput = GridHelper.createGridPasswordFieldPrompt(grid, 1, 2, cf.getAddressPass(), language[11]);
        imapInput = GridHelper.createGridTextFieldSet(grid, 1, 3, cf.getImap(), "imap.gmail.com");
        smtpInput = GridHelper.createGridTextFieldSet(grid, 1, 4, cf.getSmtp(), "smtp.gmail.com");
        imapPortInput = GridHelper.createGridTextFieldSet(grid, 1, 5, cf.getImapPort(), "993");
        smtpPortInput = GridHelper.createGridTextFieldSet(grid, 1, 6, cf.getSmtpPort(), "465");
        sqlURLInput = GridHelper.createGridTextFieldSet(grid, 1, 7, cf.getSqlURL(), "jdbc:mysql://localhost:3306/emails?zeroDateTimeBehavior=CONVERT_TO_NULL&autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true");
        sqlNameInput = GridHelper.createGridTextFieldSet(grid, 1, 8, cf.getDbName(), "emails");
        sqlPortInput = GridHelper.createGridTextFieldSet(grid, 1, 9, cf.getSqlPort(), "3306");
        userInput = GridHelper.createGridTextFieldSet(grid, 1, 10, cf.getUser(), "daniel");
        userPassInput = GridHelper.createGridPasswordFieldPrompt(grid, 1, 11, cf.getUserPass(), language[11]);

        grid.getChildren().addAll(name, address, addressPassword, imap, smtp, imapPort, smtpPort, sqlURL, dbName,
                sqlPort, user, userPass, nameInput, emailInput, emailPassInput, imapInput, smtpInput,
                imapPortInput, smtpPortInput, sqlURLInput, sqlNameInput, sqlPortInput, userInput, userPassInput);

        return grid;
    }

    /**
     * Vertical Box that holds a button and message error
     *
     * @return the vertical box set up
     */
    private VBox addSend(BorderPane border) {
        // BOTTOM
        send = new Button(language[12]);
        Label errorMsg = new Label("");
        send.setPrefWidth(200);
        send.setPadding(new Insets(5, 5, 5, 5));
        BorderPane.setAlignment(send, Pos.BOTTOM_RIGHT);
        addEventToBorder(border);
        send.setOnAction(e -> {
            if (!verifyParams()) {
                errorMsg.setTextFill(Color.web("#8B0000"));
                errorMsg.setText(language[13]);
                LOG.debug("Parameters were invalid. Couldn't proceed");
            } else {
                LOG.debug("Parameters were valid. Proceeding with authentication");
                SmtpServer smtpServer = MailServer.create()
                        .ssl(true)
                        .host("smtp.gmail.com")
                        .auth(emailInput.getText(), emailPassInput.getText())
                        .buildSmtpMailServer();
                try (SendMailSession session = smtpServer.createSession()) {
                    try {
                        LOG.debug("Attempting to authenticate");
                        session.open();
                        PropertiesAccessor.setProperties(PropertyLocation.getPath(), new String[]{
                            cf.getName().get(), cf.getAddress().get(), cf.getAddressPass().get(), cf.getImap().get(),
                            cf.getSmtp().get(), cf.getImapPort().get(), cf.getSmtpPort().get(), cf.getSqlURL().get(),
                            cf.getDbName().get(), cf.getSqlPort().get(), cf.getUser().get(), cf.getUserPass().get()},
                                PropertyLocation.getFileName());
                        window.close();
                        LOG.debug("Authentication valid");
                        if (window.getModality() != Modality.WINDOW_MODAL) {
                            emailRead = new EmailReader();
                            emailRead.emailFormWindow();
                        }
                    } catch (IOException error) {
                        errorMsg.setTextFill(Color.web("#8B0000"));
                        errorMsg.setText(language[13]);
                        LOG.debug("Authentication invalid : " + error);
                    }
                }
            }
        });
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(8);
        vbox.setAlignment(Pos.CENTER);

        vbox.getChildren().addAll(send, errorMsg);

        return vbox;
    }

    /**
     * verify the parameters of the grid
     *
     * @return boolean based on if its valid
     */
    private boolean verifyParams() {
        return !(nameInput.toString() == null || nameInput.toString().length() == 0
                || emailInput.toString() == null || emailInput.toString().length() == 0
                || emailPassInput.toString() == null || emailPassInput.toString().length() == 0
                || imapInput.toString() == null || imapInput.toString().length() == 0
                || smtpInput.toString() == null || smtpInput.toString().length() == 0
                || imapPortInput.toString() == null || imapPortInput.toString().length() == 0
                || smtpPortInput.toString() == null || smtpPortInput.toString().length() == 0
                || sqlURLInput.toString() == null || sqlURLInput.toString().length() == 0
                || sqlNameInput.toString() == null || sqlNameInput.toString().length() == 0
                || sqlPortInput.toString() == null || sqlPortInput.toString().length() == 0
                || userInput.toString() == null || userInput.toString().length() == 0
                || userPassInput.toString() == null || userPassInput.toString().length() == 0);
    }
}
