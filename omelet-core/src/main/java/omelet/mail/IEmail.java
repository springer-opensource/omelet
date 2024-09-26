/*******************************************************************************
 *
 * 	Copyright 2014 Springer Science+Business Media Deutschland GmbH
 * 	
 * 	Licensed under the Apache License, Version 2.0 (the "License");
 * 	you may not use this file except in compliance with the License.
 * 	You may obtain a copy of the License at
 * 	
 * 	    http://www.apache.org/licenses/LICENSE-2.0
 * 	
 * 	Unless required by applicable law or agreed to in writing, software
 * 	distributed under the License is distributed on an "AS IS" BASIS,
 * 	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 	See the License for the specific language governing permissions and
 * 	limitations under the License.
 *******************************************************************************/
package omelet.mail;

import java.util.List;

import javax.mail.Message;

public interface IEmail {

	void setFolder(String folderName);

	String getMailFormat(Message msg);

	List<Message> getEmailsBy(FilterEmails searchCat,
			String filterText);

	List<Message> filterEmailsBy(FilterEmails searchCat,
			List<Message> messages, String filterText);


	String getEmailBody(Message message);

	String getHTMLLinkAfterText(Message message,
			String textAfterWhichtoFetchHtmlLinks);

	boolean verifyPatternInEmail(Message message, String patterToMatch);

	boolean deleteMessage(Message message);

}
