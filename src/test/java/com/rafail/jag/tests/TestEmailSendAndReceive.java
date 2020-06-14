package com.rafail.jag.tests;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.drafail.jag.business.Email.Imap;
import com.drafail.jag.business.Email.Smtp;
import com.drafail.jag.data.Attachments;
import com.drafail.jag.data.EmailBean;
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
import junit.framework.Assert;
import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import javafx.beans.property.SimpleIntegerProperty;
import org.junit.Before;

/**
 *
 * @author 1633028
 */
public class TestEmailSendAndReceive {

    /**
     * Constructor for the tests, sets up the attachments, receiver, sender, and
     * an array with multiple EmailBeans inside, each with different variables
     *
     * @throws IOException If file is invalid
     */
    public TestEmailSendAndReceive() throws IOException {

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

        imap = new Imap();

        this.emailBean = new EmailBean[]{
            //normal [0]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestNormal", "This is a test", "<h1>This is a bigger test</h1>", attachSmall, attachSmall, ldt.now(), 3, false),
            //no attachments or embedded [1]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestNoAttachOrEmb", "This is a test", "<h1>This is a bigger test</h1>", null, null, ldt.now(), 3, false),
            //no emb [2]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestNoEmb", "This is a test", "<h1>This is a bigger test</h1>", attachSmall, null, ldt.now(), 3, false),
            //no attach [3] 
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestNoAttach", "This is a test", "<h1>This is a bigger test</h1>", null, attachSmall, ldt.now(), 3, false),
            //empty subject [4]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "", "This is a test", "<h1>This is a bigger test</h1>", attachSmall, attachSmall, ldt.now(), 3, false),
            //no subject [5]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            null, "This is a test", "<h1>This is a bigger test</h1>", attachSmall, attachSmall, ldt.now(), 3, false),
            //no html or msg [6]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestNoHTMLorMsg", null, null, attachSmall, attachSmall, ldt.now(), 3, false),
            //no html [7]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestNoHTML", "This is a test", null, attachSmall, attachSmall, ldt.now(), 3, false),
            //no msg [8]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestNoMsg", null, "<h1>This is a bigger test</h1>", attachSmall, attachSmall, ldt.now(), 3, false),
            //empty text and msg [9]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestEmptyHTMLandMsg", "", "", attachSmall, attachSmall, ldt.now(), 3, false),
            //empty text [10]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestEmptyText", "", "<h1>This is a bigger test</h1>", attachSmall, attachSmall, ldt.now(), 3, false),
            //empty html [11]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestEmptyHtml", "This is a test", "", attachSmall, attachSmall, ldt.now(), 3, false),
            //no sender [12]
            new EmailBean("", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestInvalidSender", "This is a test", "<h1>This is a bigger test</h1>", attachSmall, attachSmall, ldt.now(), 3, false),
            //no receiver [13]
            new EmailBean("send.1633028@gmail.com", null, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestNoReceiver", "This is a test", "<h1>This is a bigger test</h1>", attachSmall, attachSmall, ldt.now(), 3, false),
            //empty receiver [14]
            new EmailBean("send.1633028@gmail.com", new String[]{""}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestEmptyReceiver", "This is a test", "<h1>This is a bigger test</h1>", attachSmall, attachSmall, ldt.now(), 3, false),
            //invalid receiver [15]
            new EmailBean("send.1633028@gmail.com", new String[]{"asdasd"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestInvalidReceiver", "This is a test", "<h1>This is a bigger test</h1>", attachSmall, attachSmall, ldt.now(), 3, false),
            //no cc [16]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, null, new String[0],
            "TestNoCc", "This is a test", "<h1>This is a bigger test</h1>", attachSmall, attachSmall, ldt.now(), 3, false),
            //empty cc [17]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{""}, new String[0],
            "TestEmptyCc", "This is a test", "<h1>This is a bigger test</h1>", attachSmall, attachSmall, ldt.now(), 3, false),
            //no cc/receiver/bcc [18]
            new EmailBean("send.1633028@gmail.com", null, null, null,
            "TestNoCcAndReceiverAndBcc", "This is a test", "<h1>This is a bigger test</h1>", attachSmall, attachSmall, ldt.now(), 3, false),
            //empty cc/receiver/bcc [19]
            new EmailBean("send.1633028@gmail.com", new String[]{""}, new String[]{""}, new String[]{""},
            "TestEmptyCcAndReceiverAndBcc", "This is a test", "<h1>This is a bigger test</h1>", attachSmall, attachSmall, ldt.now(), 3, false),
            //bad priorities [20]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestBadPriorities", "This is a test", "<h1>This is a bigger test</h1>", attachSmall, attachSmall, ldt.now(), 3, false),
            //Invalid cc [21]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"asdasd"}, new String[0],
            "TestInvalidCc", "This is a test", "<h1>This is a bigger test</h1>", attachSmall, attachSmall, ldt.now(), 3, false),
            //no sender [22]
            new EmailBean(null, new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestNoSender", "This is a test", "<h1>This is a bigger test</h1>", attachSmall, attachSmall, ldt.now(), 3, false),
            //invalid sender [23]
            new EmailBean("asdasd", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestInvalidSender", "This is a test", "<h1>This is a bigger test</h1>", attachSmall, attachSmall, ldt.now(), 3, false),
            //no bcc [24]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, null,
            "TestNoBcc", "This is a test", "<h1>This is a bigger test</h1>", attachSmall, attachSmall, ldt.now(), 3, false),
            //empty bcc [25]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[]{""},
            "TestEmptyBcc", "This is a test", "<h1>This is a bigger test</h1>", attachSmall, attachSmall, ldt.now(), 3, false),
            //invalid bcc [26]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[]{"asdasd"},
            "TestInvalidBcc", "This is a test", "<h1>This is a bigger test</h1>", attachSmall, attachSmall, ldt.now(), 3, false),
            //multiple attach [27]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestMultipleAttach", "This is a test", "<h1>This is a bigger test</h1>", attachBig, attachSmall, ldt.now(), 3, false),
            //multipleEmb [28]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestMultipleEmb", "This is a test", "<h1>This is a bigger test</h1>", attachSmall, attachBig, ldt.now(), 3, false),
            //MultipleAttachAndEmb [29]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestMultipleAttachAndEmb", "This is a test", "<h1>This is a bigger test</h1>", attachBig, attachBig, ldt.now(), 3, false),
            //sending txt file attach[30]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestAttachText", "This is a test", "<h1>This is a bigger test</h1>", textSmall, null, ldt.now(), 3, false),
            //sending word file attach[31]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestAttachWord", "This is a test", "<h1>This is a bigger test</h1>", wordSmall, null, ldt.now(), 3, false),
            //sending multiple txt file attach [32]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestAttachTexts", "This is a test", "<h1>This is a bigger test</h1>", textBig, null, ldt.now(), 3, false),
            //sending multiple word file attach [33]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestAttachWords", "This is a test", "<h1>This is a bigger test</h1>", wordBig, null, ldt.now(), 3, false),
            //sending multiple txt file emb [34]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestEmbTexts", "This is a test", "<h1>This is a bigger test</h1>", null, textBig, ldt.now(), 3, false),
            //sending multiple word file emb [35]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestEmbWords", "This is a test", "<h1>This is a bigger test</h1>", null, wordBig, ldt.now(), 3, false),
            //sending multiple txt file attach and emb [36]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestEmbAndAttachTexts", "This is a test", "<h1>This is a bigger test</h1>", textBig, textBig, ldt.now(), 3, false),
            //sending multiple word file attach and emb [37]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestEmbAndAttachWords", "This is a test", "<h1>This is a bigger test</h1>", wordBig, wordBig, ldt.now(), 3, false),
            //sending text file as emb [38]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestEmbText", "This is a test", "<h1>This is a bigger test</h1>", null, textSmall, ldt.now(), 3, false),
            //sending word file as emb [39]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestEmbWord", "This is a test", "<h1>This is a bigger test</h1>", null, wordBig, ldt.now(), 3, false),
            //sending one file text as emb and attach [40]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestEmbAndAttachText", "This is a test", "<h1>This is a bigger test</h1>", textSmall, textSmall, ldt.now(), 3, false),
            //sending one word file as emb and attach[41]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            "TestEmbAndAttachWord", "This is a test", "<h1>This is a bigger test</h1>", wordSmall, wordSmall, ldt.now(), 3, false),
            //all optional values null [42]
            new EmailBean("send.1633028@gmail.com", new String[]{"receive.1633028@gmail.com"}, new String[]{"other.1633028@gmail.com"}, new String[0],
            null, null, null, null, null, ldt.now(), 3, false)
        };
    }

    private static Logger LOG = LoggerFactory.getLogger(TestEmailSendAndReceive.class);
    private EmailBean[] emailBean;
    private Smtp smtp;
    private Imap imap;
    private EmailBean[] received;
    private static Attachments[] attachBig = new Attachments[5];
    private static Attachments[] attachSmall = new Attachments[1];
    private static Attachments[] textBig = new Attachments[5];
    private static Attachments[] textSmall = new Attachments[1];
    private static Attachments[] wordBig = new Attachments[5];
    private static Attachments[] wordSmall = new Attachments[1];
    private static LocalDateTime ldt;
    private String url = "jdbc:mysql://localhost:3306/emails?zeroDateTimeBehavior=CONVERT_TO_NULL&autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/New_York";
    private String id = "daniel";
    private String pass = "password";

    //added thread sleep. gotta see if it works
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

    @Test
    public void testEmail() throws InterruptedException, IOException, IllegalArgumentException, SQLException, IllegalArgumentException, SQLException {
        LOG.debug("starting test 1");
        smtp = new Smtp(emailBean[0]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("Finished test 1");
        Assert.assertEquals(emailBean[0], received[received.length - 1]);
    }

    @Test
    public void TestNoAttachAndEmbAttachTest() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 2");
        smtp = new Smtp(emailBean[1]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("Finished test 2");
        Assert.assertNull(received[received.length - 1].getAttachment());
    }

    @Test
    public void TestNoAttachAndEmbEmbTest() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 3");
        smtp = new Smtp(emailBean[1]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 3");
        Assert.assertNull(received[received.length - 1].getEmbAttachments());
    }

    @Test
    public void TestNoEmb() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 4");
        smtp = new Smtp(emailBean[2]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 4");
        Assert.assertNull(received[received.length - 1].getEmbAttachments());
    }

    @Test
    public void TestNoAttach() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 5");

        smtp = new Smtp(emailBean[3]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 5");
        Assert.assertNull(received[received.length - 1].getAttachment());
    }

    @Test
    public void testSubjectFailEmpty() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 6");

        smtp = new Smtp(emailBean[4]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 6");
        Assert.assertEquals(received[received.length - 1].getSubject(), "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSubjectFailNull() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 7");
        smtp = new Smtp(emailBean[5]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 7");
        fail("test 7 failed"); 
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHtmlAndMsgFailMsgNull() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 8");
        smtp = new Smtp(emailBean[6]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 8");
        fail("test 8 failed"); 

    }

    @Test(expected = IllegalArgumentException.class)
    public void testHtmlAndMsgFailHtmlNull() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 9");
        smtp = new Smtp(emailBean[6]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 9");
        fail("test 9 failed"); 

    }

    @Test(expected = IllegalArgumentException.class)
    public void testHtmlFailNull() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 10");
        smtp = new Smtp(emailBean[7]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 10");
        fail("test 10 failed"); 

    }

    @Test(expected = IllegalArgumentException.class)
    public void testMsgFailNull() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 11");
        smtp = new Smtp(emailBean[8]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 11");
        fail("test 11 failed"); 

    }

    @Test
    public void testHtmlAndMsgFailEmptyMsg() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 12");
        smtp = new Smtp(emailBean[9]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 12");
        assertEquals(emailBean[9].getHtmlMsg(), received[received.length - 1]
                .getHtmlMsg());
    }

    @Test
    public void testHtmlAndMsgFailEmptyHtml() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 13");
        smtp = new Smtp(emailBean[9]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 13");

        assertEquals(emailBean[9].getHtmlMsg(), received[received.length - 1]
                .getHtmlMsg());
    }

    @Test
    public void testMsgFailEmpty() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 14");
        smtp = new Smtp(emailBean[10]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 14");
        assertEquals(emailBean[10].getTextMsg(), received[received.length - 1]
                .getTextMsg());
    }

    @Test
    public void testHtmlFailEmpty() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 15");
        smtp = new Smtp(emailBean[11]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 15");
        assertEquals(emailBean[11].getHtmlMsg(), received[received.length - 1]
                .getHtmlMsg());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSenderFailEmpty() throws InterruptedException, IOException, IllegalArgumentException, SQLException {

        LOG.debug("start test 16");
        smtp = new Smtp(emailBean[12]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 16");
        fail("test 16 failed"); 

    }

    @Test(expected = IllegalArgumentException.class)
    public void testReceiverFailNull() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 17");
        smtp = new Smtp(emailBean[13]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 17");
        fail("test 17 failed"); 
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReceiverFailEmpty() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 18");
        smtp = new Smtp(emailBean[14]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 18");
        fail("test 18 failed"); 
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReceiverFailInvalid() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 19");
        smtp = new Smtp(emailBean[15]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 19");
        fail("test 19 failed"); 
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCcfailNull() throws InterruptedException, IOException, IllegalArgumentException, SQLException {

        LOG.debug("start test 20");
        smtp = new Smtp(emailBean[16]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 20");
        fail("test 20 failed"); 
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCcFailEmpty() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 21");
        smtp = new Smtp(emailBean[17]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 21");
        fail("test 21 failed");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullCcAndReceiverFailCc() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 22");
        smtp = new Smtp(emailBean[18]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 22");
        fail("test 22 failed"); 
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullCcAndReceiverFailReceiver() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 23");
        smtp = new Smtp(emailBean[18]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 23");
        fail("test 23 failed"); 
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullCcAndReceiverFailSubject() throws InterruptedException, IOException, IllegalArgumentException, SQLException {

        LOG.debug("start test 24");
        smtp = new Smtp(emailBean[19]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 24");
        fail("test 24 failed"); 

    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyCcAndReceiverFailCc() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 25");
        smtp = new Smtp(emailBean[19]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 25");
        fail("test 25 failed"); 
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyCcAndReceiverFailReceiver() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 26");
        smtp = new Smtp(emailBean[19]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 26");
        fail("test 26 failed"); 

    }

    @Test
    public void testPriorityFail() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 27");
        emailBean[20].setMessagePriority(8);
        smtp = new Smtp(emailBean[20]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 27");
        assertEquals(emailBean[20].getMessagePriority(), received[received.length - 1]
                .getMessagePriority());
        
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCcFailInvalid() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 28");
        smtp = new Smtp(emailBean[21]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 28");
        fail("test 28 failed"); 
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSenderFailNull() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 29");
        smtp = new Smtp(emailBean[22]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 29");
        fail("test 29 failed"); 

    }

    @Test(expected = IllegalArgumentException.class)
    public void testSenderFailInvalid() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 30");
        smtp = new Smtp(emailBean[23]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 30");
        fail("test 30 failed"); 

    }

    @Test(expected = IllegalArgumentException.class)
    public void testBccFailNull() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 31");
        smtp = new Smtp(emailBean[24]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 31");
        fail("test 31 failed"); 
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBccFailEmpty() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 32");
        smtp = new Smtp(emailBean[25]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 32");
        fail("test 32 failed"); 
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBccFailInvalid() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 33");
        smtp = new Smtp(emailBean[26]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 33");
        fail("test 33 failed"); 
    }

    @Test
    public void testMultipleAttach() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("finish test 34");
        smtp = new Smtp(emailBean[27]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 34");
        Assert.assertEquals(emailBean[27], received[received.length - 1]);
    }

    @Test
    public void testMultipleEmb() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 35");
        smtp = new Smtp(emailBean[28]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 35");
        Assert.assertEquals(emailBean[28], received[received.length - 1]);
    }

    @Test
    public void testMultipleAttachAndEmb1() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 36");
        smtp = new Smtp(emailBean[29]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 36");
        Assert.assertEquals(emailBean[29], received[received.length - 1]);
    }

    @Test
    public void testMultipleAttachAndEmb2() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 37");
        smtp = new Smtp(emailBean[29]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 37");
        Assert.assertEquals(emailBean[29], received[received.length - 1]);
    }

    @Test
    public void testTxtAttach() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 38");
        smtp = new Smtp(emailBean[30]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 38");

        Assert.assertEquals(emailBean[30], received[received.length - 1]);
    }

    @Test
    public void testWordAttach() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 39");
        smtp = new Smtp(emailBean[31]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 39");
        Assert.assertEquals(emailBean[31], received[received.length - 1]);
    }

    @Test
    public void testTxtsAttach() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 40");
        smtp = new Smtp(emailBean[32]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 40");
        Assert.assertEquals(emailBean[32], received[received.length - 1]);
    }

    @Test
    public void testWordsAttach() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 41");
        smtp = new Smtp(emailBean[33]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 41");
        Assert.assertEquals(emailBean[33], received[received.length - 1]);
    }

    @Test
    public void testTxtsEmb() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 42");
        smtp = new Smtp(emailBean[34]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 42");
        Assert.assertEquals(emailBean[34], received[received.length - 1]);
    }

    @Test
    public void testWordsEmb() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 43");
        smtp = new Smtp(emailBean[35]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 43");
        Assert.assertEquals(emailBean[35], received[received.length - 1]);
    }

    @Test
    public void testTxtsAttachAndEmb1() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 44");
        smtp = new Smtp(emailBean[36]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 44");
        Assert.assertEquals(emailBean[36], received[received.length - 1]);
    }

    @Test
    public void testTxtsAttachAndEmb2() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 45");
        smtp = new Smtp(emailBean[36]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 45");
        Assert.assertEquals(emailBean[36], received[received.length - 1]);
    }

    @Test
    public void testWordsAttachAndEmb1() throws InterruptedException, IOException, IllegalArgumentException, SQLException {

        LOG.debug("start test 46");
        smtp = new Smtp(emailBean[37]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 46");
        Assert.assertEquals(emailBean[37], received[received.length - 1]);
    }

    @Test
    public void testWordsAttachAndEmb2() throws InterruptedException, IOException, IllegalArgumentException, SQLException {

        LOG.debug("start test 47");
        smtp = new Smtp(emailBean[37]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 47");
        Assert.assertEquals(emailBean[37], received[received.length - 1]);
    }

    @Test
    public void testTxtEmb() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 48");
        smtp = new Smtp(emailBean[38]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 48");
        Assert.assertEquals(emailBean[38], received[received.length - 1]);
    }

    @Test
    public void testWordEmb() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 49");
        smtp = new Smtp(emailBean[39]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 49");
        Assert.assertEquals(emailBean[39], received[received.length - 1]);
    }

    @Test
    public void testTxtAttachAndEmb1() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 50");
        smtp = new Smtp(emailBean[40]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 50");
        Assert.assertEquals(emailBean[40], received[received.length - 1]);
    }

    @Test
    public void testTxtAttachAndEmb2() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 51");
        smtp = new Smtp(emailBean[40]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 51");
        Assert.assertEquals(emailBean[40], received[received.length - 1]);
    }

    @Test
    public void testWordAttachAndEmb1() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 52");
        smtp = new Smtp(emailBean[41]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 52");
        Assert.assertEquals(emailBean[41], received[received.length - 1]);
    }

    @Test
    public void testWordAttachAndEmb2() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 53");
        smtp = new Smtp(emailBean[41]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 53");
        Assert.assertEquals(emailBean[41], received[received.length - 1]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOptionalsNull() throws InterruptedException, IOException, IllegalArgumentException, SQLException {
        LOG.debug("start test 54");
        smtp = new Smtp(emailBean[42]);
        smtp.run();
        Thread.sleep(2000);
        received = imap.receiveEmail();
        LOG.debug("finish test 54");
    }
}
