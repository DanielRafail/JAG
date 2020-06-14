/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drafail.jag.presentation;

import com.drafail.jag.business.Email.AttachmentHelper;
import com.drafail.jag.business.Email.Smtp;
import com.drafail.jag.business.javaFX.GridHelper;
import com.drafail.jag.business.Properties.PropertiesAccessor;
import com.drafail.jag.data.Attachments;
import com.drafail.jag.data.EmailBean;
import com.drafail.jag.data.EmailBeanFx;
import com.drafail.jag.data.Language;
import com.drafail.jag.data.PropertyLocation;
import com.drafail.jag.data.ReplyStatus;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author 1633028
 */
public class EmailSender {

    private Stage window = new Stage();
    private Scene emailForm;
    private String[] language;
    private EmailBeanFx emailbeanFx = new EmailBeanFx();
    private ResourceBundle msg;
    private Properties props;
    private GridPane grid;
    private Smtp smtp;
    private int priority = 3;
    private Stage primaryStage;
    private EmailBean emailbean;
    private final static Logger LOG = LoggerFactory.getLogger(EmailSender.class);
    private HTMLEditor htmlEditor;

    /**
     * Constructor used to create an email sender
     *
     * @param primaryStage parent stage
     * @throws IOException
     */
    public EmailSender(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        props = PropertiesAccessor.instantiateProperty(PropertyLocation.getPath(), PropertyLocation.getFileName());
    }

    /**
     * Constructor used to create an email sender
     *
     * @param primaryStage parent stage
     * @param emailBean the email bean used for forwarding and replying
     * @throws IOException
     */
    public EmailSender(Stage primaryStage, EmailBean emailBean) throws IOException {
        this.primaryStage = primaryStage;
        this.emailbean = emailBean;
        props = PropertiesAccessor.instantiateProperty(PropertyLocation.getPath(), PropertyLocation.getFileName());
    }

    /**
     * Creates a button which sends the email and then closes the window
     *
     * @return
     */
    private Button addSend() {
        Button send = new Button(language[3]);
        send.setPrefWidth(200);
        send.setPadding(new Insets(5, 5, 5, 5));
        BorderPane.setAlignment(send, Pos.BOTTOM_RIGHT);
        send.setOnAction(e -> {
            LOG.debug("Attempting to send email");
            emailbean = new EmailBean();
            if (emailbeanFx.getCcString().get().length() > 0
                    || emailbeanFx.getReceiverString().get().length() > 0) {
                    emailbean.setHtmlMsg(htmlEditor.getHtmlText());
                emailbean.setSender(props.getProperty("email"));
                if (emailbeanFx.getReceiverStringToString().length() > 0) {
                    emailbean.setReceiver(StringToArray(emailbeanFx.getReceiverStringToString().trim()));
                }
                if (emailbeanFx.getCcStringToString().length() > 0) {
                    emailbean.setCc(StringToArray(emailbeanFx.getCcStringToString().trim()));
                }
                if (emailbeanFx.getBccStringToString().length() > 0) {
                    emailbean.setBcc(StringToArray(emailbeanFx.getBccStringToString().trim()));
                }
                emailbean.setAttachment(emailbeanFx.getAttachment());
                emailbean.setEmbAttachments(emailbeanFx.getEmbAttachments());
                emailbean.setFolder("Sent");
                emailbean.setSubject(emailbeanFx.getSubjectString());
                emailbean.setMessagePriority(priority);

                LOG.debug("Sending Email from GUI");
                smtp = new Smtp(emailbean);
                smtp.start();

                emailbean = null;
                emailbeanFx = new EmailBeanFx();
                window.close();
            } else {
            }

        });
        return send;
    }

    /**
     * method that takes a string, splits its, and returns a StringProperty[]
     * based on the split result
     *
     * @param str
     * @return
     */
    private String[] StringToArray(String str) {
        String[] splitString;
        str = str.trim();
        if (str.contains(", ")) {
            splitString = str.split(", ");
        } else if (str.contains(",")) {
            splitString = str.split(",");
        } else {
            splitString = str.split(" ");
        }
        return splitString;
    }

    /**
     * Method used to create an imageView
     *
     * @param name the name of the file (image) used
     * @return the ImageView set with an image and width and height
     */
    private ImageView getImage(String name) {
        Image image = new Image(getClass().getResourceAsStream(name));
        ImageView returnImage = new ImageView(image);
        returnImage.setFitWidth(45);
        returnImage.setFitHeight(45);
        return returnImage;
    }

    /**
     * creates a vertical box that contains both a button and a label describing
     * the button
     *
     * @param button the button used
     * @param str the string used to create a label
     * @return a vertical box containing both elements
     */
    private VBox attachmentsImage(Button button, String str) {

        Label sendEmailLabel = new Label(str);
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.BOTTOM_CENTER);
        vbox.getChildren().addAll(button, sendEmailLabel);
        return vbox;
    }

    /**
     * Create a horizontal box that holds a vertical box (that holds a button
     * and a label). Point of this HBox is to keep a horizontal list of all
     * attachments
     *
     * @param border the border this horizontal box resides in (used to bind)
     * @param vbox the vertical box added to this horizontal box
     * @return the horizontal box when it is set up
     */
    private ListView addList(BorderPane border, VBox vbox) {
        ListView list = new ListView();
        list.setFocusTraversable(false);
        list.setPrefHeight(100);
        list.setOrientation(Orientation.HORIZONTAL);
        list.prefWidthProperty().bind(border.widthProperty());
        Button filechooser = createFileChooser(border, vbox);
        filechooser.prefWidthProperty().bind(list.widthProperty().multiply(0.15));
        list.getItems().add(filechooser);
        if (emailbeanFx.getAttachment() != null && emailbeanFx.getAttachment().length > 0) {
            LOG.debug("Adding all attachments to email");
            for (Attachments attach : emailbeanFx.getAttachment()) {
                Button button = new Button();
                button.setId("buttons");
                button.setGraphic(getImage("/images/attachment.png"));
                button.prefWidthProperty().bind(list.widthProperty().multiply(0.10));
                button.setOnAction(e -> {
                    LOG.debug("Removing attachment from email");
                    emailbeanFx.setAttachment(AttachmentHelper.remove(emailbeanFx.getAttachment(), attach));
                    vbox.getChildren().remove(0);
                    vbox.getChildren().add(0, addList(border, vbox));
                });
                list.getItems().add(attachmentsImage(button, attach.getName()));
            }
        }
        return list;
    }

    /**
     * Method used to create a button that allows you to chose files
     *
     * @param border the border the fileChooser resides in
     * @param vbox the vertical box the the button resides in
     * @return
     */
    private Button createFileChooser(BorderPane border, VBox vbox) {
        FileChooser fileChooser = new FileChooser();
        Button attachButton = new Button();
        attachButton.setText(language[2]);
        attachButton.setOnAction(x
                -> {
            LOG.debug("Open window to add attachment");
            List<File> list
                    = fileChooser.showOpenMultipleDialog(window);
            if (list != null) {
                list.forEach((file) -> {
                    try {
                        emailbeanFx.setAttachment(AttachmentHelper.add(emailbeanFx.getAttachment(),
                                new Attachments(file.getName(), Files.readAllBytes(file.toPath()))));
                        vbox.getChildren().remove(0);
                        vbox.getChildren().add(0, addList(border, vbox));
                        LOG.debug("Attachment added to email");
                        //border.setBottom(vbox);
                    } catch (IOException ex) {
                        LOG.debug("Error adding attachment to email : " + ex);
                    }
                });
            }
        });
        return attachButton;
    }

    /**
     * Create a grid that holds TextFields and labels so the user can send an
     * email
     *
     * @param reply int to see if its a reply
     * @param border the border the grid resides in
     * @return the grid once it is set up
     */
    private GridPane sendEmailForm(BorderPane border, ReplyStatus reply) {
        grid = new GridPane();
        grid.prefHeightProperty().bind(border.heightProperty().multiply(0.20));

        grid = GridHelper.setupGrid(grid);
        grid = GridHelper.addColumns(grid);

        Label errorMsg = new Label("");
        GridPane.setConstraints(errorMsg, 0, 6);

        Label to = GridHelper.createGridLabel(grid, language[0], 0, 0);
        Label cc = GridHelper.createGridLabel(grid, "CC", 0, 1);
        Label bcc = GridHelper.createGridLabel(grid, "BCC", 0, 2);
        Label subject = GridHelper.createGridLabel(grid, language[1], 0, 3);

        TextField toAnswer = null;
        TextField ccAnswer = null;
        TextField subjectAnswer;
        if (emailbean == null || emailbean.getDateSent() == null) {
            toAnswer = GridHelper.createGridTextFieldPrompt(grid, 1, 0, emailbeanFx.getReceiverString(), props.getProperty("email"));
            ccAnswer = GridHelper.createGridTextFieldPrompt(grid, 1, 1, emailbeanFx.getCcString(), props.getProperty("email"));
            subjectAnswer = GridHelper.createGridTextFieldPrompt(grid, 1, 3, emailbeanFx.getSubject(), language[4]);

        } else {
            if (reply != ReplyStatus.None) {
                if (reply == ReplyStatus.Many) {
                    if (emailbean.getReceiver() != null && emailbean.getReceiver().length > 0) {
                        toAnswer = GridHelper.createGridTextFieldSet(grid, 1, 0, emailbeanFx.getReceiverString(), emailbean.getSender() + (emailbean.getReceiver().length > 0 ? String.join(", ", emailbean.getReceiver()).replace(props.getProperty("email"), "") : ""));
                    } else {
                        toAnswer = GridHelper.createGridTextFieldPrompt(grid, 1, 0, emailbeanFx.getReceiverString(), props.getProperty("email"));

                    }
                    if (emailbean.getCc() != null && emailbean.getCc().length > 0) {
                        ccAnswer = GridHelper.createGridTextFieldSet(grid, 1, 1, emailbeanFx.getCcString(), String.join(", ", emailbean.getCc()).replace(props.getProperty("email"), ""));
                    } else {
                        ccAnswer = GridHelper.createGridTextFieldPrompt(grid, 1, 1, emailbeanFx.getCcString(), props.getProperty("email"));
                    }
                } else if (reply == ReplyStatus.One) {
                    toAnswer = GridHelper.createGridTextFieldSet(grid, 1, 0, emailbeanFx.getReceiverString(), emailbean.getSender());
                    ccAnswer = GridHelper.createGridTextFieldPrompt(grid, 1, 1, emailbeanFx.getCcString(), props.getProperty("email"));
                    ccAnswer.clear();
                }
                subjectAnswer = GridHelper.createGridTextFieldSet(grid, 1, 3, emailbeanFx.getSubject(), "re:" + emailbean.getSubject());
                emailbeanFx.setAttachment(null);
                emailbeanFx.setEmbAttachments(null);
            } else {
                toAnswer = GridHelper.createGridTextFieldPrompt(grid, 1, 0, emailbeanFx.getReceiverString(), props.getProperty("email"));
                ccAnswer = GridHelper.createGridTextFieldPrompt(grid, 1, 1, emailbeanFx.getCcString(), props.getProperty("email"));
                subjectAnswer = GridHelper.createGridTextFieldSet(grid, 1, 3, emailbeanFx.getSubject(), "fw:" + emailbean.getSubject());
                toAnswer.clear();
                ccAnswer.clear();
                emailbeanFx.setAttachment(emailbean.getAttachment());
                emailbeanFx.setEmbAttachments(emailbean.getEmbAttachments());
            }
        }

        TextField bccAnswer = GridHelper.createGridTextFieldPrompt(grid, 1, 2, emailbeanFx.getBccString(), props.getProperty("email"));

        grid.getChildren()
                .addAll(to, toAnswer, cc, ccAnswer, bcc, bccAnswer, subject, subjectAnswer);
        return grid;
    }

    private ChoiceBox setPriority() {
        ChoiceBox cb = new ChoiceBox(FXCollections.observableArrayList(
                "1", "2", "3", "4", "5")
        );
        cb.getSelectionModel().select(2);
        cb.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                priority = newValue.intValue() + 1;
            }
        
        });
        return cb;
    }

    /**
     * Create a vertical box that holds the bottom half of the UI (attachment,
     * html editor, button to send)
     *
     * @param border the border the vertical box resides in
     * @return the vertical box once it is set up
     */
    private VBox addVBox(BorderPane border) {
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.prefWidthProperty().bind(border.widthProperty());
        vbox.prefHeightProperty().bind(border.heightProperty().subtract(grid.prefHeightProperty()));
        htmlEditor = new HTMLEditor();
        htmlEditor.prefWidthProperty().bind(border.widthProperty());
        htmlEditor.prefHeightProperty().bind(vbox.prefHeightProperty().multiply(0.55));
        if (emailbean != null) {
            if (emailbean.getHtmlMsg() != null && emailbean.getHtmlMsg().length() > 0) {
                htmlEditor.setHtmlText("</br><p>_______________________<p>"
                        + emailbean.getHtmlMsg());
            } else {
                htmlEditor.setHtmlText("</br><p>_______________________<p>"
                        + emailbean.getTextMsg());
            }
        }
        Label messagePriority = new Label(language[6]);
        HBox hbox = new HBox(5);
        hbox.setSpacing(5);
        hbox.getChildren().addAll(messagePriority,setPriority());
        vbox.getChildren().addAll(addList(border, vbox), hbox, htmlEditor, addSend());
        return vbox;
    }

    /**
     * Menu bar that holds localization
     *
     * @param key String array containing all the information we look for inside
     * the message bundle
     * @param border the border the menu bar resides in
     * @return the menu bar once it is set up
     */
    private MenuBar createMenuBar(String[] key, BorderPane border) {

        MenuBar menuBar = new MenuBar();
        Menu menuLanguage = new Menu("Language");
        MenuItem menuEnglish = new MenuItem("EN");
        MenuItem menuFrench = new MenuItem("FR");

        menuEnglish.setOnAction(e -> {
            Language.setCurrentLocale(new Locale("en", "CA"));
            language = PropertiesAccessor.readLanguage(Language.getCurrentLocale(), msg, key);
            border.setBottom(addSend());
            border.setCenter(sendEmailForm(border, ReplyStatus.None));
            border.setBottom(addVBox(border));
        });
        menuFrench.setOnAction(e -> {
            Language.setCurrentLocale(new Locale("fr", "CA"));
            language = PropertiesAccessor.readLanguage(Language.getCurrentLocale(), msg, key);
            border.setBottom(addSend());
            border.setCenter(sendEmailForm(border, ReplyStatus.None));
            border.setBottom(addVBox(border));
        });

        menuLanguage.getItems().addAll(menuEnglish, menuFrench);
        menuBar.getMenus().addAll(menuLanguage);
        return menuBar;
    }

    /**
     * Method gotten from
     * https://stackoverflow.com/questions/23735815/how-get-only-text-from-javafx2-htmleditor
     * to remove html tags from html edtiro
     *
     * @param htmlText the text you remove the tags from
     * @return the htmlText without tags
     */
    public static String getText(String htmlText) {

        String result = "";

        Pattern pattern = Pattern.compile("<[^>]*>");
        Matcher matcher = pattern.matcher(htmlText);
        final StringBuffer text = new StringBuffer(htmlText.length());

        while (matcher.find()) {
            matcher.appendReplacement(
                    text,
                    " ");
        }

        matcher.appendTail(text);

        result = text.toString().trim();

        return result;
    }

    /**
     * Main method for this class that sets up the scene, stage, and create all
     *
     * @param reply int to see if this window opens for a reply or not UI nodes
     */
    public void emailSendWindow(ReplyStatus reply) {
        String[] key = new String[]{"to", "subject", "attachments", "send", "subject", "errorForm", "priorityM"};
        language = PropertiesAccessor.
                readLanguage(Language.getCurrentLocale(), msg, key);
        window.setWidth(600);
        window.setHeight(750);
        window.setTitle("JAG");
        BorderPane border = new BorderPane();
        border.setPadding(new Insets(10, 50, 10, 10));
        grid = sendEmailForm(border, reply);
        border.setCenter(grid);
        border.setTop(createMenuBar(
                key, border));
        border.setBottom(addVBox(border));
        emailForm = new Scene(border);
        emailForm.getStylesheets().add("attachment.css");
        window.setScene(emailForm);
        if (window.getModality() != Modality.WINDOW_MODAL) {
            window.initModality(Modality.WINDOW_MODAL);
            window.initOwner(primaryStage);
        }
        window.getIcons().add(new Image((getClass().getResourceAsStream("/images/icon.png"))));
        window.show();
    }
}
