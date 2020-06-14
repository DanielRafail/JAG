/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rafail.jag.tests;

import com.drafail.jag.business.DAO.DAOAttachments;
import com.drafail.jag.business.DAO.DAOBridge;
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
import javafx.beans.property.SimpleStringProperty;
import junit.framework.Assert;
import static junit.framework.Assert.fail;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author 1633028
 */
public class TestDatabaseAdd {

    public TestDatabaseAdd() throws IOException {
        localDateTime = LocalDateTime.now();
        File smtp = new File("testObjects\\kkona.jpg");
        byte[] fileContent = Files.readAllBytes(smtp.toPath());
        File text = new File("testObjects\\textTest.txt");
        byte[] textContent = Files.readAllBytes(text.toPath());
        File word = new File("testObjects\\wordTest.docx");
        byte[] wordContent = Files.readAllBytes(word.toPath());
        Attachments img = new Attachments("kkona.jpg", fileContent);
        Attachments txt = new Attachments("textTest.txt", textContent);
        Attachments docx = new Attachments("wordTest.docx", wordContent);
        incorrect[0] = new Attachments(img.getName(), img.getAttachment());
        incorrect[0].setName(longWord);
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
            "TestNormal", "This is a test", "<h1>This is a bigger test</h1>", attachSmall, attachSmall, localDateTime.now(), 3, false),
            //Long subject
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            longWord, "This is a test", "<h1>This is a bigger test</h1>", attachSmall, attachSmall, localDateTime.now(), 3, false),
            //long recieve email
            new EmailBean("send.1633028@gmail.com", new String[]{longWord + "@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestNormal", "This is a test", "<h1>This is a bigger test</h1>", attachSmall, attachSmall, localDateTime.now(), 3, false),
            //long folder name
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestNormal", "This is a test", "<h1>This is a bigger test</h1>", attachSmall, attachSmall, localDateTime.now(), 3, false),
            //invalid recipient
            new EmailBean("send.1633028@gmail.com", new String[]{"receasdsadsadive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestNormal", "This is a test", "<h1>This is a bigger test</h1>", attachSmall, attachSmall, localDateTime.now(), 3, false)
        };
        emailBean[3].setFolder(longWord);
    }
    private Properties props = PropertiesAccessor.instantiateProperty(PropertyLocation.getPath(), PropertyLocation.getFileName());
    private static Logger LOG = LoggerFactory.getLogger(TestDatabaseAdd.class);
    private EmailBean[] emailBean;
    private static Attachments[] incorrect = new Attachments[1];
    private static Attachments[] attachBig = new Attachments[5];
    private static Attachments[] attachSmall = new Attachments[1];
    private static Attachments[] textBig = new Attachments[5];
    private static Attachments[] textSmall = new Attachments[1];
    private static Attachments[] wordBig = new Attachments[5];
    private static Attachments[] wordSmall = new Attachments[1];
    private static LocalDateTime localDateTime;
    private final String longWord = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    private String url = "jdbc:mysql://localhost:3306/emails?zeroDateTimeBehavior=CONVERT_TO_NULL&autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/New_York";
    private String id = "daniel";
    private String pass = "password";
    private DatabaseInteraction email = new DAOEmail(url, id, pass);
    private DatabaseInteraction folder = new DAOFolder(url, id, pass);
    private DatabaseInteraction attach = new DAOAttachments(url, id, pass);
    private DatabaseInteraction receive = new DAORecipients(url, id, pass);
    private DatabaseInteraction bridge = new DAOBridge(url, id, pass);
    private DatabaseInteraction helper = new DAOHelper(url, id, pass);

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
        new TestDatabaseAdd().seedDatabase();
    }

    @Test
    public void testAddEmailSearchEmail() throws IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test DatabaseAdd 1");
        ((DAOHelper) helper).addToDatabase(emailBean[0]);
        LOG.debug("Finish test DatabaseAdd 1");
        Assert.assertEquals(true, ((DAOEmail) email).findEmail(emailBean[0]));
    }

    @Test
    public void testAddEmailSearchAttachment() throws IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test DatabaseAdd 2");
        ((DAOHelper) helper).addToDatabase(emailBean[0]);
        LOG.debug("Finish test DatabaseAdd 2");
        Assert.assertEquals(true, ((DAOAttachments) attach).findAttachment(emailBean[0].getAttachment()[0], "attachment"));
    }

    @Test
    public void testAddEmailSearchEmbedded() throws IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test DatabaseAdd 3");
        ((DAOHelper) helper).addToDatabase(emailBean[0]);
        LOG.debug("Finish test DatabaseAdd 3");
        Assert.assertEquals(true, ((DAOAttachments) attach).findAttachment(emailBean[0].getAttachment()[0], "embedded"));
    }

    @Test
    public void testAddEmailSearchReceiver() throws IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test DatabaseAdd 4");
        ((DAOHelper) helper).addToDatabase(emailBean[0]);
        LOG.debug("Finish test DatabaseAdd 4");
        Assert.assertEquals(true, ((DAORecipients) receive).findReceiver("to", emailBean[0].getReceiver()[0]));
    }

    @Test
    public void testAddEmailSearchCc() throws IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test DatabaseAdd 5");
        ((DAOHelper) helper).addToDatabase(emailBean[0]);
        LOG.debug("Finish test DatabaseAdd 5");
        Assert.assertEquals(true, ((DAORecipients) receive).findReceiver("cc", emailBean[0].getCc()[0]));
    }

    @Test
    public void testAddEmailSearchFolders() throws IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test DatabaseAdd 6");
        ((DAOHelper) helper).addToDatabase(emailBean[0]);
        LOG.debug("Finish test DatabaseAdd 6");
        Assert.assertEquals(true, ((DAOFolder) folder).findFolder(emailBean[0]));
    }

    @Test
    public void testAddEmailSearchBridgeCc() throws IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test DatabaseAdd 7");
        ((DAOHelper) helper).addToDatabase(emailBean[0]);
        LOG.debug("Finish test DatabaseAdd 7");
        Assert.assertEquals(true, ((DAOBridge) bridge).findBridge(emailBean[0], "cc", emailBean[0].getCc()[0]));
    }

    @Test
    public void testAddEmailSearchBridgeTo() throws IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test DatabaseAdd 8");
        ((DAOHelper) helper).addToDatabase(emailBean[0]);
        LOG.debug("Finish test DatabaseAdd 8");
        Assert.assertEquals(true, ((DAOBridge) bridge).findBridge(emailBean[0], "to", emailBean[0].getReceiver()[0]));
    }

    @Test
    public void testSearchFakeEmail() throws IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test DatabaseAdd 9");
        LOG.debug("Finish test DatabaseAdd 9");
        String placeHolder = emailBean[0].getSubject();
        emailBean[0].setSubject("non-existent subject");
        Assert.assertEquals(false, ((DAOEmail) email).findEmail(emailBean[0]));
        emailBean[0].setSubject(placeHolder);
    }

    @Test
    public void testSearchFakeAttachment() throws IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test DatabaseAdd 10");
        String placeHolder = emailBean[0].getAttachment()[0].getName();
        emailBean[0].getAttachment()[0].setName("random name");
        LOG.debug("Finish test DatabaseAdd 10");
        Assert.assertEquals(false, ((DAOAttachments) attach).findAttachment(emailBean[0].getAttachment()[0], "attachment"));
        emailBean[0].getAttachment()[0].setName(placeHolder);
    }

    @Test
    public void testSearchFakeRecipient() throws IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test DatabaseAdd 11");
        ((DAOHelper) helper).addToDatabase(emailBean[4]);
        LOG.debug("Finish test DatabaseAdd 11");
        Assert.assertEquals(false, ((DAORecipients) receive).findReceiver("no", emailBean[4].getReceiver()[0]));
    }

    @Test
    public void testSearchFakeFolders() throws IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test DatabaseAdd 12");
        ((DAOHelper) helper).addToDatabase(emailBean[0]);
        LOG.debug("Finish test DatabaseAdd 12");
        String placeHolder = emailBean[0].getFolder();
        emailBean[0].setFolder("inexistant");
        Assert.assertEquals(false, ((DAOFolder) folder).findFolder(emailBean[0]));
        emailBean[0].setFolder(placeHolder);
    }

    @Test
    public void testEmailSearchFakeBridge() throws IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test DatabaseAdd 13");
        LOG.debug("Finish test DatabaseAdd 13");
        String placeHolder = emailBean[0].getSubject();
        emailBean[0].setSubject("non-existent subject");
        Assert.assertEquals(false, ((DAOBridge) bridge).findBridge(emailBean[0], "cc", emailBean[0].getCc()[0]));
        emailBean[0].setSubject(placeHolder);
    }

    @Test(expected = SQLException.class)
    public void testSearchTooBigEmailSubject() throws IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test DatabaseAdd 14");
        String placeHolder = emailBean[0].getSubject();
        emailBean[0].setSubject(longWord);
        ((DAOHelper) helper).addToDatabase(emailBean[0]);
        ((DAOEmail) email).findEmail(emailBean[0]);
        LOG.debug("Finish test DatabaseAdd 14");
        fail("Test 14 failed");
        emailBean[0].setSubject(placeHolder);
    }

    @Test(expected = SQLException.class)
    public void testSearchTooBigReceiverEmail() throws IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test DatabaseAdd 15");
        ((DAOHelper) helper).addToDatabase(emailBean[0]);
        LOG.debug("Finish test DatabaseAdd 15");
        ((DAORecipients) receive).findReceiver("to",
                longWord);
        fail("Test 15 failed");
    }

    @Test(expected = SQLException.class)
    public void testSearchTooBigReceiverName() throws IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test DatabaseAdd 16");
        ((DAOHelper) helper).addToDatabase(emailBean[2]);
        LOG.debug("Finish test DatabaseAdd 16");
        ((DAORecipients) receive).findReceiver("cc", "send.1633028@gmail.com");
        fail("Test 16 failed");
    }

    @Test(expected = SQLException.class)
    public void testSearchTooBigReceiverType() throws IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test DatabaseAdd 17");
        ((DAOHelper) helper).addToDatabase(emailBean[0]);
        LOG.debug("Finish test DatabaseAdd 17");
        ((DAORecipients) receive).findReceiver(longWord,
                "send.1633028@gmail.com");
        fail("Test 17 failed");
    }

    @Test(expected = SQLException.class)
    public void testSearchTooBigAttachmentName() throws IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test DatabaseAdd 18");
        ((DAOHelper) helper).addToDatabase(emailBean[0]);
        LOG.debug("Finish test DatabaseAdd 18");
        ((DAOAttachments) attach).findAttachment(incorrect[0], "attachment");
        fail("Test 18 failed");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSearchTooBigAttachmentType() throws IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test DatabaseAdd 19");
        ((DAOHelper) helper).addToDatabase(emailBean[0]);
        LOG.debug("Finish test DatabaseAdd 19");
        ((DAOAttachments) attach).findAttachment(attachSmall[0], longWord);
        fail("Test 19 failed");
    }

    @Test(expected = SQLException.class)
    public void testSearchTooBigFolderName() throws IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test DatabaseAdd 20");
        ((DAOHelper) helper).addToDatabase(emailBean[3]);
        ;
        LOG.debug("Finish test DatabaseAdd 20");
        ((DAOFolder) folder).findFolder(emailBean[3]);
        fail("Test 20 failed");
    }

}
