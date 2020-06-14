/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drafail.jag.presentation;

import com.drafail.jag.business.javaFX.GridHelper;
import com.drafail.jag.business.Properties.PropertiesAccessor;
import com.drafail.jag.data.Attachments;
import com.drafail.jag.data.EmailBean;
import com.drafail.jag.data.Language;
import com.drafail.jag.data.PropertyLocation;
import com.drafail.jag.data.ReplyStatus;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author 1633028
 */
public class EmailReceived {

    private Scene emailForm;
    private String[] language;
    private final EmailReader emailReader;
    private final EmailSender emailSend;
    private final EmailBean emailbean;
    private ResourceBundle msg;
    private Properties props;
    private GridPane grid;
    private final Stage primaryStage;
    private final static Logger LOG = LoggerFactory.getLogger(EmailReceived.class);

    /**
     * Constructor used to create an emailReceived
     *
     * @param primaryStage the parent stage
     * @param emailBean the emailbean we read the mail from
     * @throws IOException
     */
    public EmailReceived(Stage primaryStage, EmailBean emailBean, EmailReader emailreader) throws IOException {
        this.emailReader = emailreader;
        this.emailSend = new EmailSender(primaryStage, emailBean);
        this.emailbean = emailBean;
        this.primaryStage = primaryStage;
        props = PropertiesAccessor.instantiateProperty(PropertyLocation.getPath(), PropertyLocation.getFileName());
    }

    /**
     * Create a button with an onclick that sends u back to email reader
     *
     * @return return the button once it is set up
     */
    private Button addButton() {
        Button send = new Button(language[3]);
        send.setPrefWidth(200);
        send.setPadding(new Insets(5, 5, 5, 5));
        BorderPane.setAlignment(send, Pos.TOP_LEFT);
        send.setOnAction(e -> {
            LOG.debug("Going back to old page");
            try {
                emailReader.emailFormWindowBack(primaryStage);
            } catch (SQLException ex) {
                LOG.debug("Error loading table " + ex);
            }
        });
        return send;
    }

    /**
     * Create a grid containing all email short information (cc,subject,
     * receiver)
     *
     * @param border the border parent element
     * @return the grid once it is set up
     */
    private GridPane createGrid(BorderPane border) {
        grid = new GridPane();
        grid.prefHeightProperty().bind(border.heightProperty().divide(0.9));
        grid = GridHelper.setupGrid(grid);
        grid = GridHelper.addColumns(grid);

        Label to = GridHelper.createGridLabel(grid, language[0] + " : ", 0, 0);
        Label cc = GridHelper.createGridLabel(grid, "CC : ", 0, 1);
        Label subject = GridHelper.createGridLabel(grid, language[1] + " : ", 0, 2);
        Label toAnswer;
        Label ccAnswer;
        Label subjectAnswer;
        if (emailbean.getReceiver() != null && emailbean.getReceiver().length > 0) {
            toAnswer = GridHelper.createGridLabel(grid, String.join(", ", emailbean.getReceiver()), 1, 0);
        } else {
            toAnswer = GridHelper.createGridLabel(grid, "", 1, 0);
        }
        if (emailbean.getCc() != null && emailbean.getCc().length > 0) {
            ccAnswer = GridHelper.createGridLabel(grid, String.join(", ", emailbean.getCc()), 1, 1);
        } else {
            ccAnswer = GridHelper.createGridLabel(grid, "", 1, 0);
        }
        if (emailbean.getSubject() != null && emailbean.getSubject().length() > 0) {
            subjectAnswer = GridHelper.createGridLabel(grid, emailbean.getSubject(), 1, 2);
        } else {
            subjectAnswer = GridHelper.createGridLabel(grid, "", 1, 2);
        }
        grid.getChildren().addAll(to, toAnswer, cc, ccAnswer, subject, subjectAnswer);
        return grid;
    }

    private VBox attachmentsImage(Button button, String str) {
        Label sendEmailLabel = new Label(str);
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.BOTTOM_CENTER);
        vbox.getChildren().addAll(button, sendEmailLabel);
        return vbox;
    }

    /**
     * Create a list box that holds a vertical box (that holds a button and a
     * label). Point of this ListView is to keep a horizontal list of all
     * attachments
     *
     * @param border the border this horizontal box resides in (used to bind)
     * @return the horizontal box when it is set up
     */
    private ListView addList(BorderPane border) {
        ListView list = new ListView();
        list.setFocusTraversable(false);
        list.setPrefHeight(100);
        list.setMinHeight(100);
          list.setOrientation(Orientation.HORIZONTAL);
        list.prefWidthProperty().bind(border.widthProperty());
        if (emailbean.getAttachment() != null && emailbean.getAttachment().length > 0) {
            for (Attachments attach : emailbean.getAttachment()) {
                Button button = createFileSaver(attach);
                button.setId("buttons");
                button.setText("");
                button.setGraphic(getImage("/images/attachment.png"));
                button.prefWidthProperty().bind(list.widthProperty().multiply(0.10));
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
    private Button createFileSaver(Attachments attach) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Button attachButton = new Button();
        attachButton.setText(language[2]);
        attachButton.setOnAction(x
                -> {
            LOG.debug("Opening directory choser to find location to download file");
            File selectedDirectory;
            selectedDirectory = directoryChooser.showDialog(primaryStage);
            if (selectedDirectory != null) {
                Path path = Paths.get(selectedDirectory.getAbsolutePath() + "\\" + attach.getName());
                try {
                    LOG.debug("Attempting to download file");
                    Files.write(path, attach.getAttachment());
                } catch (IOException ex) {
                    LOG.debug("Error downloaded from file : " + ex);
                }
            }
        });
        return attachButton;
    }

    /**
     * method used to create a web view that holds the html message and the
     * embedded images of the email
     *
     * @param border the parent border element
     * @return the webview once it is set up
     */
    private WebView getWebView(BorderPane border) {
        WebView content = new WebView();
        if (emailbean != null) {
            String embedded = "";
            if (emailbean.getEmbAttachments() != null && emailbean.getEmbAttachments().length > 0) {

                for (Attachments attach : emailbean.getEmbAttachments()) {
                    if (attach.getAttachment() != null) {
                        String HTML_FORMAT = "<img src=\"data:image/png;base64,%1$s\" />";
                        String b64Image = Base64.getMimeEncoder().encodeToString(attach.getAttachment());
                        embedded = String.format(HTML_FORMAT, b64Image) + embedded;
                    }
                }
            }
                content.getEngine().loadContent("<html><body bgcolor='grey'>" + emailbean.getHtmlMsg() + embedded + "</body></html>");
        }
        content.setOnDragDetected(e -> {
            e.consume();
        });
        content.prefHeightProperty()
                .bind(border.heightProperty().divide(0.4));
        return content;
    }

    /**
     * method used to create a vertical box containing all the nodes at the
     * center of the screen
     *
     * @param border the parent border element
     * @return the vertical box once it is setup
     */
    private VBox center(BorderPane border) {
        VBox vbox = new VBox(5);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(5));
        vbox.getChildren().addAll(createGrid(border), addList(border), getWebView(border));
        vbox.setId("silver");
        return vbox;
    }

    /**
     * Method used to create and instantiate a menu bar used for localization
     *
     * @param key string array containing all the keys for localization
     * @param border parent border element
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
            border.setTop(top(border, key));
            border.setCenter(center(border));
        });
        menuFrench.setOnAction(e -> {
            Language.setCurrentLocale(new Locale("fr", "CA"));
            language = PropertiesAccessor.readLanguage(Language.getCurrentLocale(), msg, key);
            border.setTop(top(border, key));
            border.setCenter(center(border));
        });

        menuLanguage.getItems().addAll(menuEnglish, menuFrench);
        menuBar.getMenus().addAll(menuLanguage);
        return menuBar;
    }

    /**
     * Method used to create a vertical box that holds a button and a label
     * (used by toolbar)
     *
     * @param button the button used
     * @param languageLoc integer value for the Language array to select
     * specific localized words
     * @return the vertical box once it is set up
     */
    private VBox toolbarVBox(Button button, int languageLoc) {

        Label sendEmailLabel = new Label(language[languageLoc]);
        VBox vbox = new VBox(5);
        vbox.setAlignment(Pos.BOTTOM_CENTER);
        vbox.setPadding(new Insets(5));
        vbox.getChildren().addAll(button, sendEmailLabel);
        return vbox;
    }

    /**
     * Create a toolbar that holds images and labels
     *
     * @param border the border the toolbar resides in
     * @param key String array used for localization that holds all values taken
     * from message bundle
     * @return the toolbar once it is created
     */
    private ToolBar createToolbar() {
        ToolBar tool = new ToolBar();

        Button reply = new Button();
        reply.setId("buttons");
        reply.setGraphic(getImage("/images/reply.png"));
        reply.setOnAction(e -> {
            LOG.debug("Opening reply window");
            emailSend.emailSendWindow(ReplyStatus.One);
        });

        Button replyAll = new Button();
        replyAll.setId("buttons");
        replyAll.setGraphic(getImage("/images/replyAll.png"));
        replyAll.setOnAction(e -> {
            LOG.debug("Opening reply window");
            emailSend.emailSendWindow(ReplyStatus.Many);
        });

        Button forward = new Button();
        forward.setId("buttons");
        forward.setGraphic(getImage("/images/forward.png"));
        forward.setOnAction(e -> {
            LOG.debug("Opening forwarding window");
            emailSend.emailSendWindow(ReplyStatus.None);
        });

        tool.getItems().addAll(toolbarVBox(reply, 5), toolbarVBox(replyAll, 7), toolbarVBox(forward, 6));
        return tool;
    }

    private VBox top(BorderPane border, String[] key) {
        VBox vbox = new VBox(5);
        vbox.setPadding(new Insets(5));
        vbox.getChildren().addAll(createMenuBar(key, border), addButton(), createToolbar());
        return vbox;
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
        returnImage.setFitWidth(50);
        returnImage.setFitHeight(50);
        return returnImage;
    }

    /**
     * main method used to show the email chosen
     */
    public void readEmail() {
        String[] key = new String[]{"to", "subject", "attachments", "back", "subject", "reply", "forward", "replyAll"};
        language = PropertiesAccessor.
                readLanguage(Language.getCurrentLocale(), msg, key);
        BorderPane border = new BorderPane();
        border.setPadding(new Insets(10, 50, 10, 10));
        border.setTop(top(border, key));
        border.setCenter(center(border));
        primaryStage.setWidth(1400);
        primaryStage.setHeight(1000);
        emailForm = new Scene(border);
        emailForm.getStylesheets().add("attachment.css");
        primaryStage.setScene(emailForm);
        primaryStage.getIcons().add(new Image((getClass().getResourceAsStream("/images/icon.png"))));
        primaryStage.show();
    }
}
