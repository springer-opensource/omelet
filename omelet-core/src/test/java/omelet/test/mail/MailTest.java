package omelet.test.mail;

import omelet.mail.Email;
import omelet.mail.FilterEmails;
import omelet.mail.MailProtocol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by borz01 on 27.08.15.
 */
public class MailTest {
    private static final Logger LOGGER = LogManager.getLogger(MailTest.class);
    private String userName = null;
    private String password = null;
    private Email mail;

    public void MailTest() {
        this.userName = System.getProperty("mailUserName");
        this.password = System.getProperty("mailUserPassword");
    }

    @Test
    public void createPop3Connection() {
        if(this.userName != null && this.password != null) {
            mail = new Email.Builder().setHost("pop.gmail.com").setProtocol(MailProtocol.POP3).setPort("995").setUserName(this.userName).setPassword(this.password).build();
            Assert.assertNotNull(mail.getEmailsBy(FilterEmails.TO, this.userName));
        } else {
            LOGGER.error("System property: mailUserName and mailUserPassword must be set.");
        }
    }

    @Test
    public void createImapConnection() {
        if(this.userName != null && this.password != null) {
            mail = new Email.Builder().setHost("imap.gmail.com").setProtocol(MailProtocol.IMAP).setPort("993").setUserName(this.userName).setPassword(this.password).build();
            Assert.assertNotNull(mail.getEmailsBy(FilterEmails.TO, this.userName));
        } else {
            LOGGER.error("System property: mailUserName and mailUserPassword must be set.");
        }
    }
}
