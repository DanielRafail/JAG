/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drafail.jag.presentation;

import com.drafail.jag.business.DAO.DAOAttachments;
import com.drafail.jag.business.DAO.DAOEmail;
import com.drafail.jag.business.DAO.DAOFolder;
import com.drafail.jag.business.DAO.DAORecipients;
import com.drafail.jag.business.DAO.DatabaseInteraction;
import com.drafail.jag.business.Email.Imap;
import com.drafail.jag.business.Properties.PropertiesAccessor;
import com.drafail.jag.data.EmailBean;
import com.drafail.jag.data.Language;
import com.drafail.jag.data.PropertyLocation;
import com.drafail.jag.data.ReplyStatus;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author 1633028
 */
public class EmailReader {

    private Stage window = new Stage();
    private final DatabaseInteraction emailDAO;
    private final DatabaseInteraction folderDAO;
    private final DatabaseInteraction recipientsDAO;
    private final DatabaseInteraction attachmentDAO;
    private Scene emailForm;
    private String[] language;
    private EmailSender emailSend;
    private EmailReceived emailReceived;
    private ResourceBundle msg;
    private final Properties props;
    private boolean firstOpen = true;
    private static final DataFormat dataFormat = new DataFormat("main.java.com.drafail.jag.data.EmailBean.java");
    private final static Logger LOG = LoggerFactory.getLogger(EmailReader.class);
    private String folderClicked;
    private Imap imap = new Imap();
    private TableView table;

    /**
     * Constructor used to create an emailReader
     *
     * @throws IOException
     */
    public EmailReader() throws IOException {
        emailDAO = new DAOEmail(PropertiesAccessor.readProperties(PropertyLocation.getPath(), new String[]{"sqlURL"}, PropertyLocation.getFileName())[0],
                PropertiesAccessor.readProperties(PropertyLocation.getPath(), new String[]{"dbUser"}, PropertyLocation.getFileName())[0],
                PropertiesAccessor.readProperties(PropertyLocation.getPath(), new String[]{"dbPass"}, PropertyLocation.getFileName())[0]);
        folderDAO = new DAOFolder(PropertiesAccessor.readProperties(PropertyLocation.getPath(), new String[]{"sqlURL"}, PropertyLocation.getFileName())[0],
                PropertiesAccessor.readProperties(PropertyLocation.getPath(), new String[]{"dbUser"}, PropertyLocation.getFileName())[0],
                PropertiesAccessor.readProperties(PropertyLocation.getPath(), new String[]{"dbPass"}, PropertyLocation.getFileName())[0]);
        recipientsDAO = new DAORecipients(PropertiesAccessor.readProperties(PropertyLocation.getPath(), new String[]{"sqlURL"}, PropertyLocation.getFileName())[0],
                PropertiesAccessor.readProperties(PropertyLocation.getPath(), new String[]{"dbUser"}, PropertyLocation.getFileName())[0],
                PropertiesAccessor.readProperties(PropertyLocation.getPath(), new String[]{"dbPass"}, PropertyLocation.getFileName())[0]);
        attachmentDAO = new DAOAttachments(PropertiesAccessor.readProperties(PropertyLocation.getPath(), new String[]{"sqlURL"}, PropertyLocation.getFileName())[0],
                PropertiesAccessor.readProperties(PropertyLocation.getPath(), new String[]{"dbUser"}, PropertyLocation.getFileName())[0],
                PropertiesAccessor.readProperties(PropertyLocation.getPath(), new String[]{"dbPass"}, PropertyLocation.getFileName())[0]);
        props = PropertiesAccessor.instantiateProperty(PropertyLocation.getPath(), PropertyLocation.getFileName());

    }

    /**
     * Vertical box that holds both the menu bar and a toolbar
     *
     * @param border the border the vertical box resides in
     * @param key String array used by the menu bar for localization
     * @return the vertical box once it is set up
     */
    private VBox createVBox(BorderPane border, String[] key) {
        VBox vbox = new VBox();
        vbox.prefWidthProperty().bind(border.widthProperty());
        MenuBar menu = createMenuBar(key, border);
        ToolBar tool = createToolbar(border, key);
        vbox.getChildren().addAll(menu, tool);
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
    private ToolBar createToolbar(BorderPane border, String[] key) {
        ToolBar tool = new ToolBar();

        Button sendEmail = new Button();
        sendEmail.setId("buttons");
        sendEmail.setGraphic(getImage("/images/send_email.png"));
        sendEmail.setOnAction(e -> {
            LOG.debug("Creating send window");
            try {
                emailSend = new EmailSender(window);
            } catch (IOException ex) {
                LOG.debug("Error opening emailSend : " + ex);
            }
            emailSend.emailSendWindow(ReplyStatus.None);
        });

        Button createFolder = new Button();
        createFolder.setId("buttons");
        createFolder.setGraphic(getImage("/images/folder.png"));
        createFolder.setOnAction(e -> {
            dialogAdd();
            try {
                border.setLeft(foldersForm(border));
            } catch (SQLException ex) {
                LOG.debug("Error loading folders from database " + ex);
            }
            border.setTop(createVBox(border, key));
        });

        Button refresh = new Button();
        refresh.setId("buttons");
        refresh.setGraphic(getImage("/images/refresh.png"));
        refresh.setOnAction(e -> {
            LOG.debug("Refreshing the page");
            imap.start();
            try {
                border.setLeft(foldersForm(border));
                if (border.getChildren().contains(table)) {
                    border.getChildren().remove(table);
                }
            } catch (SQLException ex) {
                LOG.debug("Error loading folders from database " + ex);
            }
        });

        Button settings = new Button();
        settings.setId("buttons");
        settings.setGraphic(getImage("/images/gear.png"));
        settings.setOnAction(e -> {
            LOG.debug("Opening setting page");
            EmailConfig emailConfig;
            try {
                emailConfig = new EmailConfig(window);
                emailConfig.acceptValues();
            } catch (IOException ex) {
                LOG.debug("Error writting to database " + ex);
            }
        });

        tool.getItems().addAll(toolbarVBox(sendEmail, 4), toolbarVBox(createFolder, 5), toolbarVBox(refresh, 6), toolbarVBox(settings, 11));
        return tool;
    }

    /**
     * create a TextInputDialog to rename a folder
     *
     * @return the TextInputDialog set up
     */
    private TextInputDialog dialogRename(String old) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("JAG");
        dialog.setHeaderText(language[8]);
        dialog.setContentText(language[9]);
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (!name.equalsIgnoreCase("Sent") && !name.equalsIgnoreCase("Inbox")) {
                try {
                    ((DAOFolder) folderDAO).renameFolder(old, name);
                } catch (SQLException ex) {
                    LOG.debug("Error connecting to database : " + ex);
                }
            }
        });
        return dialog;
    }

    /**
     * create a TextInputDialog to add a folder
     *
     * @return the TextInputDialog set up
     */
    private TextInputDialog dialogAdd() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("JAG");
        dialog.setHeaderText(language[9]);
        dialog.setContentText(language[10]);
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (!name.equalsIgnoreCase("Sent") && !name.equalsIgnoreCase("Inbox") && name.length() > 0) {
                try {
                    ((DAOFolder) folderDAO).addFolder(name);
                } catch (SQLException ex) {
                    LOG.debug("Error connecting to database : " + ex);
                }
            }
        });
        return dialog;
    }

    /**
     * Create Listview to hold all the folders
     *
     * @param border the border the listview resides in
     * @return the listview once it is set up
     */
    private ListView foldersForm(BorderPane border) throws SQLException {
        //later create algorithm to get all folders from database
        ListView<String> list = new ListView<>();
        list.setId("folders");
        String[] arr = ((DAOFolder) folderDAO).getFolder();
        ObservableList<String> items = FXCollections.observableArrayList();
        for (int i = 0; i < arr.length; i++) {
            items.add(arr[i]);
        }
        listRightClick(list, border);
        listDragOver(list);
        listDragDrop(list, border);
        onListClick(list, border);

        list.setItems(items);
        return list;

    }

    /**
     * method that creates the event for the list once you click it
     *
     * @param list the list the event is added to
     */
    private void onListClick(ListView<String> list, BorderPane border) {
        LOG.debug("Clicking on list");
        list.setOnMouseClicked(x -> {
            if (x.getButton() == MouseButton.PRIMARY) {
                folderClicked = list.getSelectionModel().getSelectedItem();
                try {
                    if (border.getChildren().contains(table)) {
                        border.getChildren().remove(border);
                    }
                    border.setCenter(createTable(border));
                } catch (SQLException ex) {
                    LOG.debug("Error getting emails from database : " + ex);
                }
            }
        });
    }

    /**
     * method that creates the event for the list once you right click it
     *
     * @param list the list the event will be added to
     */
    private void listRightClick(ListView<String> list, BorderPane border) {
        ContextMenu context = new ContextMenu();
        MenuItem rename = new MenuItem(language[8]);
        MenuItem delete = new MenuItem(language[7]);
        rename.setOnAction(e -> {
            LOG.debug("Attempting to rename folders");
            if (!"Inbox".equalsIgnoreCase(list.getSelectionModel().getSelectedItem()) && !"Sent".equalsIgnoreCase(list.getSelectionModel().getSelectedItem())) {
                dialogRename(list.getSelectionModel().getSelectedItem());
                try {
                    border.setLeft(foldersForm(border));
                } catch (SQLException ex) {
                    LOG.debug("Error getting folders from database: " + ex);
                }
            } else {
                LOG.debug("User attempted to rename Inbox or Sent folder. Refusing input. Quitting action");
            }
        });

        delete.setOnAction(e -> {
            LOG.debug("Attempting to delete folder");
            if (!"Inbox".equalsIgnoreCase(list.getSelectionModel().getSelectedItem()) && !"Sent".equalsIgnoreCase(list.getSelectionModel().getSelectedItem())) {
                try {
                    ((DAOFolder) folderDAO).deleteFolder(list.getSelectionModel().getSelectedItem());
                    border.setLeft(foldersForm(border));
                } catch (SQLException ex) {
                    LOG.debug("Error connecting to the database : " + ex);
                }
            } else {
                LOG.debug("User attempted to delete inbox or Sent. Refusing input. Quitting action");
            }
        });

        context.getItems().addAll(rename, delete);
        list.setContextMenu(context);
    }

    /**
     * Takes a list and attach a dragOver event to it
     *
     * @param list the list the event is added to
     */
    private void listDragOver(ListView list) {
        list.addEventHandler(DragEvent.DRAG_OVER, e -> {
            if (e.getGestureSource() != list
                    && e.getDragboard().hasContent(dataFormat)) {
                /* allow for both copying and moving, whatever user chooses */
                e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            e.consume();
        });
    }

    /**
     * Takes a list and attach a DragDrop to
     *
     * @param list the list the event is added to
     */
    private void listDragDrop(ListView<String> list, BorderPane border) {
        list.addEventHandler(DragEvent.DRAG_DROPPED, e -> {
            Dragboard db = e.getDragboard();
            boolean success = false;
            if (db.hasContent(dataFormat)) {
                try {
                    /*  int operator = (int) (border.getHeight() - list.getLayoutY());
                    int a = (int) border.getHeight();
                    int b = (int) list.getLayoutY();
                    int divider = (int) (list.getHeight()/ list.getItems().size());
                    int listLocation = operator / divider;
                    String s  = list.getItems().get(listLocation);*/
                    //doesnt work. Keeps giving you the last clicked on list item,  not the one you are currently over.
                    ((DAOEmail) emailDAO).changeFolder(e.getTarget().toString().substring(e.getTarget().toString().indexOf("\"") + 1,
                            e.getTarget().toString().indexOf("\"", e.getTarget().toString().indexOf("\"") + 1)),
                            ((EmailBean) db.getContent(dataFormat)));
                    if (border.getChildren().contains(table)) {
                        border.getChildren().remove(table);
                    }
                    border.setCenter(createTable(border));
                } catch (SQLException ex) {
                    LOG.debug("Error moving email in database " + ex);
                }
                success = true;
            }
            /* let the source know whether the string was successfully 
         * transferred and used */
            e.setDropCompleted(success);

            e.consume();
        });
    }

    /**
     * Create a menu bar used for localization
     *
     * @param key String array that holds all key values used by message bundle
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
            try {
                border.setLeft(foldersForm(border));
            } catch (SQLException ex) {
                LOG.debug("Error loading folders from database :  " + ex);
            }
            if (border.getChildren().contains(table)) {
                border.getChildren().remove(table);
            }
            border.setTop(createVBox(border, key));

        });
        menuFrench.setOnAction(e -> {
            Language.setCurrentLocale(new Locale("fr", "CA"));
            language = PropertiesAccessor.readLanguage(Language.getCurrentLocale(), msg, key);
            try {
                border.setLeft(foldersForm(border));
            } catch (SQLException ex) {
                LOG.debug("Error loading folders from database :" + ex);
            }
            if (border.getChildren().contains(table)) {
                border.getChildren().remove(table);
            }
            border.setTop(createVBox(border, key));
        });

        menuLanguage.getItems().addAll(menuEnglish, menuFrench);
        menuBar.getMenus().addAll(menuLanguage);
        return menuBar;
    }

    /**
     * Create a table that holds all the emails based on a folder
     *
     * @param border the border the table resides in
     * @return the table after it has been set up
     */
    private TableView createTable(BorderPane border) throws SQLException {
        //change this later to be based on folders and use real data
        table = new TableView();

        TableColumn name = new TableColumn(language[1]);
        TableColumn subject = new TableColumn(language[3]);
        TableColumn dateSent = new TableColumn(language[2]);
        TableColumn priority = new TableColumn(language[12]);

        name.prefWidthProperty().bind(table.widthProperty().divide(3.5));
        subject.prefWidthProperty().bind(table.widthProperty().divide(3.5));
        dateSent.prefWidthProperty().bind(table.widthProperty().divide(3.5));
        priority.prefWidthProperty().bind(table.widthProperty().subtract(name.widthProperty()).subtract(subject.widthProperty()).subtract(dateSent.widthProperty()));

        name.setCellValueFactory(new PropertyValueFactory<>("sender"));
        subject.setCellValueFactory(new PropertyValueFactory<>("subject"));
        dateSent.setCellValueFactory(new PropertyValueFactory<>("dateSent"));
        priority.setCellValueFactory(new PropertyValueFactory<>("messagePriority"));

        table.getColumns().addAll(name, subject, dateSent, priority);

        EmailBean[] beanArray = ((DAOEmail) emailDAO).getEmail(folderClicked);
        if (beanArray[0] != null) {
            for (int i = 0; i < beanArray.length; i++) {
                ((DAORecipients) recipientsDAO).getRecipient(beanArray[i]);
                ((DAOAttachments) attachmentDAO).getAttachments(beanArray[i]);
            }
            table.getItems().addAll(Arrays.asList(beanArray));

            eventsOnTable(table, border);
        }
        return table;

    }

    /**
     * Centralized method that adds all events to a table
     *
     * @param table the table the events are added to
     * @param emailbean EmailBean array containing all the email beans on the
     * table to add the events to
     * @param border the border the table resides in
     */
    private void eventsOnTable(TableView table, BorderPane border) {
        table.setRowFactory(tv -> {
            TableRow<EmailBean> row = new TableRow<>();
            tableClick(row, table);
            tableDragStart(row, table);
            tableDragEnd(row, border);
            tableRightClick(row, table, border);
            return row;
        });
    }

    /**
     * Method used to create the context menu for the row
     *
     * @param row
     * @param emailbean
     * @param table
     */
    private void tableRightClick(TableRow<EmailBean> row, TableView table, BorderPane border) {
        ContextMenu context = new ContextMenu();
        MenuItem delete = new MenuItem(language[7]);
        delete.setOnAction(e -> {
            LOG.debug("Attempting to delete email from database");
            try {
                ((DAOEmail) emailDAO).deleteEmail((EmailBean) table.getSelectionModel().getSelectedItem());
                if (border.getChildren().contains(table)) {
                    border.getChildren().remove(border);
                }
                border.setCenter(createTable(border));
                LOG.debug("Email successfuly deleted from database");
            } catch (SQLException ex) {
                LOG.debug("Error deleting email from database: " + ex);
            }
        });

        context.getItems().add(delete);

        row.setContextMenu(context);

    }

    /**
     * Add event when a row is double clicked on the table to open the email and
     * read it
     *
     * @param row the row that has been clicked
     * @param emailbean the emailbean array that exists in the table
     * @param table the table the row exists in
     */
    private void tableClick(TableRow<EmailBean> row, TableView table) {
        row.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && (!row.isEmpty())) {
                LOG.debug("Attempting to open email");
                try {
                    emailReceived = new EmailReceived(window, (EmailBean) table.getSelectionModel().getSelectedItem(), this);
                    LOG.debug("Email has been successfully opened");
                } catch (IOException ex) {
                    LOG.debug("Error occured while opening email : " + ex);
                }
                emailReceived.readEmail();
            }
        });
    }

    /**
     * Add event when a row is being clicked on the table to send the id of th
     * eemailbean
     *
     * @param row the row being clicked
     * @param table the table the row is in
     */
    private void tableDragStart(TableRow<EmailBean> row, TableView table) {
        row.setOnDragDetected(event -> {
            Dragboard db = table.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            //get id from table
            content.put(dataFormat, (EmailBean) table.getSelectionModel().getSelectedItem());
            db.setContent(content);

            event.consume();
        });
    }

    /**
     * Add event when a row is finished being dragged to reset the table
     *
     * @param row the row that is being clicked
     * @param border the border that the table resides in
     */
    private void tableDragEnd(TableRow<EmailBean> row, BorderPane border) {
        row.setOnDragDone(event -> {
            if (event.getTransferMode() == TransferMode.MOVE) {
                try {
                    if (border.getChildren().contains(table)) {
                        border.getChildren().remove(border);
                    }
                    border.setCenter(createTable(border));
                } catch (SQLException ex) {
                    LOG.debug("Could not read emails from database " + ex);
                }
            }
            event.consume();
        });
    }

    /**
     * main method of this class that creates the scene, stage, and builds the
     * ui
     */
    public void emailFormWindow() {
        String[] key = new String[]{"send", "sender", "date", "subject", "prepare", "folder", "refresh", "delete", "rename", "folder_name", "create", "settings", "priority"};
        window.setWidth(1250);
        window.setHeight(1000);
        language = PropertiesAccessor.
                readLanguage(Language.getCurrentLocale(), msg, key);
        window.setTitle("JAG Email Receiver Service");
        BorderPane border = new BorderPane();
        border.setPadding(new Insets(10, 50, 10, 10));
        try {
            border.setLeft(foldersForm(border));
        } catch (SQLException ex) {
            LOG.debug("Error connecting to database: " + ex);
        }
        border.setTop(createVBox(border, key));
        emailForm = new Scene(border);
        emailForm.getStylesheets().add("default.css");
        window.setScene(emailForm);
        if (firstOpen) {
            imap.start();
            firstOpen = false;
        }
        window.getIcons().add(new Image((getClass().getResourceAsStream("/images/icon.png"))));
        window.show();
    }

    /**
     * Main method for when the user chose to read an email
     *
     * @param primaryStage the primary stage used by the children
     * @throws java.sql.SQLException
     */
    public void emailFormWindowBack(Stage primaryStage) throws SQLException {
        String[] key = new String[]{"send", "sender", "date", "subject", "prepare", "folder", "refresh", "delete", "rename", "folder_name", "create", "settings", "priority"};
        language = PropertiesAccessor.
                readLanguage(Language.getCurrentLocale(), msg, key);
        BorderPane border = new BorderPane();
        border.setPadding(new Insets(10, 50, 10, 10));
        try {
            border.setLeft(foldersForm(border));
        } catch (SQLException ex) {
            LOG.debug("Error connecting to database: " + ex);
        }
        primaryStage.setWidth(1250);
        primaryStage.setHeight(1000);
        border.setTop(createVBox(border, key));
        border.setCenter(createTable(border));
        emailForm = new Scene(border);
        emailForm.getStylesheets().add("default.css");
        primaryStage.setScene(emailForm);
        primaryStage.getIcons().add(new Image((getClass().getResourceAsStream("/images/icon.png"))));
        primaryStage.show();
        window = primaryStage;
    }

}
