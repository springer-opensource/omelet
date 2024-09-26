/*******************************************************************************
 * Copyright 2014 Springer Science+Business Media Deutschland GmbH
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package omelet.mail;

import com.google.common.base.Stopwatch;
import org.apache.log4j.Logger;
import org.testng.Reporter;

import javax.mail.*;
import javax.mail.Message.RecipientType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Checking email using pop3 protocol
 *
 * @author kapilA
 */
public class Email implements IEmail {
	final private String host;
	final private String port;
	final private String userName;
	final private String password;
	private Properties props = System.getProperties();
	private String folderName;
	private Folder folder;
	private boolean sslEnabled;

	private static final int maxcountEMailCheck = 10;
	private static final Logger LOGGER = Logger.getLogger(Email.class);

	private Email(Builder builder) {
		MailProtocol protocol;

		this.host = builder.host;
		this.port = builder.port;
		this.userName = builder.userName;
		this.password = builder.password;
		this.sslEnabled = builder.sslEnabled;
		protocol = builder.protocol;
		this.folderName = "Inbox";
		connect(protocol);
	}

	private void connect(MailProtocol protocol) {
		Session session;
		switch (protocol) {
			case POP3:
				setPop3Config();
				break;
			case IMAP:
				setImapConfig();
				break;
			case SMTP:
				setSmtpConfig();
				break;
			default:
				setPop3Config();
				break;
		}

		session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userName, password);
			}
		});
		connectToStore(protocol.toString().toLowerCase(), session);
	}

	private void setPop3Config() {
		props.setProperty("mail.pop3.host", host);
		props.setProperty("mail.pop3.port", port);
		props.setProperty("mail.store.protocol", "pop3s");
		props.setProperty("mail.pop3.user", userName);
		if (sslEnabled) {
			props.setProperty("mail.pop3.socketFactory.class",
							  "javax.net.ssl.SSLSocketFactory");
			props.setProperty("mail.pop3.socketFactory.fallback", "true");
		}
	}

	private void setImapConfig() {
		props.setProperty("mail.imap.host", host);
		props.setProperty("mail.imap.port", port);
		if (sslEnabled) {
			props.setProperty("mail.imap.socketFactory.class",
							  "javax.net.ssl.SSLSocketFactory");
			props.setProperty("mail.imap.socketFactory.fallback", "false");
		}
	}

	private void setSmtpConfig() {
		props.setProperty("mail.smtp.host", host);
		props.setProperty("mail.smtp.port", port);
		props.setProperty("mail.smtp.auth", "true");
		if (sslEnabled) {
			props.setProperty("mail.smtp.starttls.enable", "true");
		}
	}

	private void connectToStore(String protocol, Session session) {
		Store store;

		try {
			store = session.getStore(protocol);
			store.connect(userName, password);
			folder = store.getFolder(folderName);
		} catch (NoSuchProviderException e) {
			LOGGER.error(e);
		} catch (MessagingException e) {
			LOGGER.error(e);
		}
	}

	/**
	 * Builder class to build email objects
	 *
	 * @author kapil
	 */
	public static class Builder {

		private String host;
		private String port;
		private String userName;
		private String password;
		private boolean sslEnabled = true;
		private MailProtocol protocol = MailProtocol.POP3;

		public Builder setHost(String host) {
			this.host = host;
			return this;
		}

		public Builder setPort(String portNo) {
			this.port = portNo;
			return this;
		}

		public Builder setUserName(String userName) {
			this.userName = userName;
			return this;
		}

		public Builder setPassword(String password) {
			this.password = password;
			return this;
		}

		public Builder setProtocol(MailProtocol protocol) {
			this.protocol = protocol;
			return this;
		}

		public Builder setSSL(boolean sslEnabled) {
			this.sslEnabled = sslEnabled;
			return this;
		}

		public Email build() {
			return new Email(this);
		}
	}

	/**
	 * Return format of email Message
	 *
	 * @param msg message to return the mail format from
	 */
	public String getMailFormat(Message msg) {
		String format = null;

		try {
			format = msg.getContentType();
		} catch (MessagingException e) {
			LOGGER.error(e);
		}
		return format;
	}

	/**
	 * Return List of Message filter by {@link FilterEmails} Element 0 is the newest one!!
	 *
	 * @param searchCat  enum
	 * @param filterText :text present in Subject of email
	 * @return list of messages
	 */
	public List<Message> getEmailsBy(FilterEmails searchCat, String filterText) {
		Stopwatch sw = new Stopwatch();
		sw.start();

		int inboxMessageCount = getMailCount();

		List<Message> returnMessages = new ArrayList<Message>();
		try {
			folder.open(Folder.READ_ONLY);
			Message[] messages;
			LOGGER.info("Message count in folder: " + folder.getName() + " is: " + inboxMessageCount);

			int end = folder.getMessageCount();
			int start = 1;

			if (inboxMessageCount >= 10) {
				start = end - maxcountEMailCheck + 1;
			}

			messages = folder.getMessages(start, end);

			returnMessages = filterFromToSubject(searchCat, filterText, messages);
			folder.close(true);
		} catch (MessagingException e) {
			LOGGER.error(e);
		}
		sw.stop();
		LOGGER.info("Time Taken by getMessage is: "
							+ sw.elapsedTime(TimeUnit.SECONDS));
		return returnMessages;
	}

	/**
	 * Return List of Message filter by {@link FilterEmails}
	 *
	 * @param searchCat  enum
	 * @param messages   the list of messages to filter
	 * @param filterText :text present in Subject of email
	 * @return list of messages
	 */
	public List<Message> filterEmailsBy(FilterEmails searchCat,
			List<Message> messages, String filterText) {
		try {
			return filterFromToSubject(searchCat, filterText, messages.toArray(new Message[messages.size()]));
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e);
		}
		return null;
	}

	/**
	 * Filter messages by {@link FilterEmails}
	 *
	 * @param searchCat  enum
	 * @param filterText email address or subject to filter
	 * @param messages   the list of messages to filter
	 * @return list of messages
	 * @throws MessagingException
	 */
	private List<Message> filterFromToSubject(FilterEmails searchCat, String filterText, Message[] messages)
			throws MessagingException {
		switch (searchCat) {
			case FROM:
				return filterFrom(filterText, messages);
			case TO:
				return filterTo(filterText, messages);
			case SUBJECT:
				return filterSubject(filterText, messages);
			default:
				break;
		}
		return null;
	}

	/**
	 * Filter messages by FROM
	 *
	 * @param filterText email address to filter
	 * @param messages   the list of messages to filter
	 * @return list of messages
	 * @throws MessagingException
	 */
	private List<Message> filterFrom(String filterText, Message[] messages) throws
			MessagingException {
		List<Message> returnMessages = new ArrayList<Message>();
		for (Message msg : messages) {
			if (msg.getFrom()[0].toString().contains(filterText)) {
				returnMessages.add(msg);
			}
		}
		return returnMessages;
	}

	/**
	 * Filter messages by To
	 *
	 * @param filterText email address to filter
	 * @param messages   the list of messages to filter
	 * @return list of messages
	 * @throws MessagingException
	 */
	private List<Message> filterTo(String filterText, Message[] messages) throws
			MessagingException {
		List<Message> returnMessages = new ArrayList<Message>();
		for (Message msg : messages) {
			for (Address addr : msg.getRecipients(RecipientType.TO)) {
				if (addr.toString().contains(filterText)) {
					returnMessages.add(msg);
				}
			}
		}
		return returnMessages;
	}

	/**
	 * Filter messages by SUBJECT
	 *
	 * @param filterText subject to filter
	 * @param messages   the list of messages to filter
	 * @return list of messages
	 * @throws MessagingException
	 */
	private List<Message> filterSubject(String filterText, Message[] messages) throws
			MessagingException {
		List<Message> returnMessages = new ArrayList<Message>();
		for (Message msg : messages) {
			if (msg.getSubject()
				   .equalsIgnoreCase(filterText)) {
				returnMessages.add(msg);
			}
		}
		return returnMessages;
	}

	/**
	 * Get the Body of the Email message
	 *
	 * @param message Message to get the mail body
	 */
	public String getEmailBody(Message message) {
		Object content;
		StringBuilder messageBody = new StringBuilder();
		try {
			folder.open(Folder.READ_ONLY);
			content = message.getContent();
			if (content instanceof Multipart) {
				Multipart mp = (Multipart) content;
				for (int i = 0; i < mp.getCount(); i++) {
					BodyPart bp = mp.getBodyPart(i);
					if (Pattern.compile(Pattern.quote("text/html"), Pattern.CASE_INSENSITIVE)
							   .matcher(bp.getContentType()).find()) {
						messageBody.append(bp.getContent());
					} else {
						messageBody.append(bp.getContent());
					}
				}
			} else {
				messageBody.append(content);
			}
			folder.close(true);
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return messageBody.toString();
	}

	/**
	 * Return the Html link in the body of email after the text
	 *
	 * @param message                        Message to parse the HTML link from
	 * @param textAfterWhichtoFetchHtmlLinks search after the text for a html link
	 */
	public String getHTMLLinkAfterText(Message message,
			String textAfterWhichtoFetchHtmlLinks) {

		String text = getEmailBody(message);
		String filteredText;
		String httpLink = null;
		// check if the URL is present
		if (text.indexOf(textAfterWhichtoFetchHtmlLinks) != -1) {
			filteredText = text.substring(text.indexOf(textAfterWhichtoFetchHtmlLinks));
			// ideally this should be the link
			httpLink = filteredText.substring(filteredText.indexOf("http"))
								   .split(" ")[0].split(">")[0].split("\"")[0];
		} else {
			Reporter.log(text);
		}
		return httpLink;
	}

	/**
	 * if not set then folder name default is inbox Note in pop3 protocol folder
	 * will always be inbox
	 *
	 * @param folderName the folder name to set
	 */
	public void setFolder(String folderName) {
		this.folderName = folderName;
	}

	public boolean verifyPatternInEmail(Message message, String patterToMatch) {
		String messageBody = getEmailBody(message);
		return Pattern.matches(messageBody, patterToMatch);
	}

	/**
	 * Delete a message from folder
	 *
	 * @param message the message to delete from folder
	 * @return boolean as result
	 */
	public boolean deleteMessage(Message message) {
		int mailCountBefore = getMailCount();
		try {
			folder.open(Folder.READ_WRITE);
			message.setFlag(Flags.Flag.DELETED, true);
			folder.close(true);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return mailCountBefore > getMailCount();
	}

	/**
	 * Get the count of mails in folder
	 *
	 * @return the count of mails in folder
	 */
	private int getMailCount() {
		int mailCount = -1;
		try {
			folder.open(Folder.READ_ONLY);
			mailCount = folder.getMessageCount();
			folder.close(true);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return mailCount;
	}
}