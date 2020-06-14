package com.rafail.jag.tests;

import com.drafail.jag.business.DAO.DAOEmail;
import com.drafail.jag.business.DAO.DAOFolder;
import com.drafail.jag.business.DAO.DAOHelper;
import com.drafail.jag.business.DAO.DAORecipients;
import com.drafail.jag.business.DAO.DatabaseInteraction;
import com.drafail.jag.business.Properties.PropertiesAccessor;
import com.drafail.jag.data.Attachments;
import com.drafail.jag.data.EmailBean;
import com.drafail.jag.data.PropertyLocation;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author 1633028
 */
public class TestDatabaseDelete {

    public TestDatabaseDelete() throws IOException {
        ldt = LocalDateTime.now();
        File fi = new File("testObjects\\kkona.jpg");
        byte[] fileContent = Files.readAllBytes(fi.toPath());
        File text = new File("testObjects\\textTest.txt");
        byte[] textContent = Files.readAllBytes(text.toPath());
        File word = new File("testObjects\\wordTest.docx");
        byte[] wordContent = Files.readAllBytes(word.toPath());
        Attachments img = new Attachments("kkona.jpg", fileContent);
        Attachments txt = new Attachments("textTest.txt", textContent);
        Attachments docx = new Attachments("wordTest.docx", wordContent);
        attachSmall[0] = img;
        textSmall[0] = txt;
        wordSmall[0] = docx;

        for (int i = 0; i < attachBig.length; i++) {
            attachBig[i] = img;
            textBig[i] = txt;
            wordBig[i] = docx;
        }
        this.emailBean = new EmailBean[]{
            //normal [0]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestNormal", "This is a test", "<h1>This is a bigger test</h1>", attachSmall, attachSmall, ldt.now(), 3, false),
            //no cc
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[0], new String[0],
            "TestNormal", "This is a test", "<h1>This is a bigger test</h1>", attachSmall, attachSmall, ldt.now(), 3, false),
            //no recipient
            new EmailBean("send.1633028@gmail.com", new String[0], new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestNormal", "This is a test", "<h1>This is a bigger test</h1>", attachSmall, attachSmall, ldt.now(), 3, false),};

    }
    private Properties props = PropertiesAccessor.instantiateProperty(PropertyLocation.getPath(), PropertyLocation.getFileName());
    private static final Logger LOG = LoggerFactory.getLogger(TestDatabaseDelete.class);
    private final EmailBean[] emailBean;
    private EmailBean[] received;
    private static final Attachments[] attachBig = new Attachments[5];
    private static final Attachments[] attachSmall = new Attachments[1];
    private static final Attachments[] textBig = new Attachments[5];
    private static final Attachments[] textSmall = new Attachments[1];
    private static final Attachments[] wordBig = new Attachments[5];
    private static Attachments[] wordSmall = new Attachments[1];
    private static LocalDateTime ldt;
    private final String url = "jdbc:mysql://localhost:3306/emails?zeroDateTimeBehavior=CONVERT_TO_NULL&autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/New_York";
    private final String id = "daniel";
    private final String pass = "password";
    private final DatabaseInteraction email = new DAOEmail(url, id, pass);
    private final DatabaseInteraction folder = new DAOFolder(url, id, pass);
    private final DatabaseInteraction receive = new DAORecipients(url, id, pass);
    private final DatabaseInteraction helper = new DAOHelper(url, id, pass);

    @Before
    public void seedDatabase() throws IOException, InterruptedException {
        LOG.debug("@Before seeding");

        final String seedDataScript = loadAsString("CreateAll.sql");
        try (Connection connection = DriverManager.getConnection(url, id, pass);) {
            for (String statement : splitStatements(new StringReader(seedDataScript), ";")) {
                connection.prepareStatement(statement).execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed seeding database", e);
        }
    }

    /**
     * The following methods support the seedDatabase method
     */
    private String loadAsString(final String path) {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
                Scanner scanner = new Scanner(inputStream)) {
            return scanner.useDelimiter("\\A").next();
        } catch (IOException e) {
            throw new RuntimeException("Unable to close input stream.", e);
        }

    }

    private List<String> splitStatements(Reader reader,
            String statementDelimiter) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(reader);
        final StringBuilder sqlStatement = new StringBuilder();
        final List<String> statements = new LinkedList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || isComment(line)) {
                continue;
            }
            sqlStatement.append(line);
            if (line.endsWith(statementDelimiter)) {
                statements.add(sqlStatement.toString());
                sqlStatement.setLength(0);
            }
        }
        return statements;
    }

    private boolean isComment(final String line) {
        return line.startsWith("--") || line.startsWith("//")
                || line.startsWith("/*");
    }

    @AfterClass
    public static void seedAfterTestCompleted() throws IOException, InterruptedException {
        LOG.debug("@AfterClass seeding");
        new TestDatabaseDelete().seedDatabase();
    }

    @Test
    public void testDeleteEmailSearchEmail() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test DatabaseDelete 1");
        ((DAOHelper) helper).addToDatabase(emailBean[0]);
        //fails because date in sql and date in dateSent is different by nanoseconds so it cant be found
        boolean found = ((DAOEmail) email).findEmail(emailBean[0]);
        ((DAOEmail) email).deleteEmail(emailBean[0]);
        LOG.debug("Finish test DatabaseDelete 1");
        Assert.assertEquals(!found, ((DAOEmail) email).findEmail(emailBean[0]));
    }

    @Test
    public void testDeleteEmailSearchReceiver() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test DatabaseDelete 2");
        ((DAOHelper) helper).addToDatabase(emailBean[0]);
        boolean found = ((DAORecipients) receive).findReceiver("to", emailBean[1].getReceiver()[0]);
        ((DAORecipients) receive).deleteReceiver("to", emailBean[0].getReceiver()[0]);
        LOG.debug("Finish test DatabaseDelete 2");
        Assert.assertEquals(!found, ((DAORecipients) receive).findReceiver("to", emailBean[0].getReceiver()[0]));
    }

    @Test
    public void testDeleteEmailSearchCc() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test DatabaseDelete 3");
        ((DAOHelper) helper).addToDatabase(emailBean[0]);
        boolean found = ((DAORecipients) receive).findReceiver("cc", emailBean[2].getCc()[0]);
        ((DAORecipients) receive).deleteReceiver("cc", emailBean[0].getCc()[0]);
        LOG.debug("Finish test DatabaseDelete 3");
        Assert.assertEquals(!found, ((DAORecipients) receive).findReceiver("cc", emailBean[0].getCc()[0]));
    }

    @Test
    public void testDeleteEmailSearchFolders() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test DatabaseDelete 4");
        ((DAOHelper) helper).addToDatabase(emailBean[0]);
        boolean found = ((DAOFolder) folder).findFolder(emailBean[0]);
        ((DAOFolder) folder).deleteFolder(emailBean[0]);
        LOG.debug("Finish test DatabaseDelete 4");
        Assert.assertEquals(!found, ((DAOFolder) folder).findFolder(emailBean[0]));
    }

    @Test
    public void testDeleteEmailSearchEmailFail() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test DatabaseDelete 5");
        boolean found = ((DAOEmail) email).findEmail(emailBean[0]);
        ((DAOEmail) email).deleteEmail(emailBean[0]);
        LOG.debug("Finish test DatabaseDelete 5");
        Assert.assertEquals(found, ((DAOEmail) email).findEmail(emailBean[0]));
    }

    @Test
    public void testDeleteEmailSearchReceiverFail() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test DatabaseDelete 6");
        boolean found = ((DAORecipients) receive).findReceiver("to", emailBean[0].getReceiver()[0]);
        ((DAORecipients) receive).deleteReceiver("to", emailBean[0].getReceiver()[0]);
        LOG.debug("Finish test DatabaseDelete 6");
        Assert.assertEquals(found, ((DAORecipients) receive).findReceiver("to", emailBean[0].getReceiver()[0]));
    }

    @Test
    public void testDeleteEmailSearchCcFail() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test DatabaseDelete 6");
        boolean found = ((DAORecipients) receive).findReceiver("cc", emailBean[0].getCc()[0]);
        ((DAORecipients) receive).deleteReceiver("cc", emailBean[0].getCc()[0]);
        LOG.debug("Finish test DatabaseDelete 6");
        Assert.assertEquals(found, ((DAORecipients) receive).findReceiver("cc", emailBean[0].getCc()[0]));
    }

    @Test
    public void testDeleteEmailSearchFoldersFail() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test DatabaseDelete 7");
        boolean found = ((DAOFolder) folder).findFolder(emailBean[0]);
        ((DAOFolder) folder).deleteFolder(emailBean[0]);
        LOG.debug("Finish test DatabaseDelete 7");
        Assert.assertEquals(!found, ((DAOFolder) folder).findFolder(emailBean[0]));
    }

}
