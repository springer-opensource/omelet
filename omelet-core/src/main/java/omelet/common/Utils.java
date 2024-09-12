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
package omelet.common;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/***
 * General Utility function
 * 
 * @author kapilA
 *
 */
public class Utils {

	private static final Logger LOGGER = LogManager.getLogger(Utils.class);

	/***
	 * Return text appended with unique String
	 * 
	 * @param text
	 * @return
	 */
	public static String getUniqueName(String text) {
		return text + UUID.randomUUID();
	}

	/***
	 * Return Substring text appended with unique String
	 * 
	 * @param text
	 * @param charCount
	 *            :Count of the Characters starting with 0
	 * @return
	 */
	public static String getUniqueName(String text, int charCount) {
		return (text + UUID.randomUUID()).substring(0, charCount);
	}

	/***
	 * Get List of Integer from the String
	 * 
	 * @param text
	 * @return
	 */
	public static List<String> getIntegerListFromString(String text) {
		List<String> integerList = new ArrayList<String>();
		Matcher matcher = Pattern.compile("\\d+").matcher(text);

		while (matcher.find()) {
			integerList.add(matcher.group());
		}
		return integerList;
	}
	
	public static String getFilePathForUpload(String fileName,Object className){
		LOGGER.debug("File name recieved is:"+fileName);
		return System.getProperty("user.dir")+File.separator+fileName;
	}

	/***
	 * This method return full path of the resource file name using class loader
	 * Can be used to upload files on website
	 * 
	 * @param className
	 *            : Class which is calling this method
	 * @param fileName
	 * @return
	 */
	public static String getResources(Object className, String fileName) {
		// Surely there will be better implementation for path of files
		// which should be used across OS
		LOGGER.debug("File Name Recieved is:" + fileName);
		String returFilePath = null;

		try {
			switch (OSName.get()) {
			case UNIX:
				returFilePath = className.getClass()
						.getResource("/" + fileName).getPath();
				break;
			case WIN:
				returFilePath = className.getClass()
						.getResource("/" + fileName).getPath().substring(1)
						.replace("%20", " ");
				break;
			case MAC:
				returFilePath = className.getClass()
						.getResource("/" + fileName).getPath();
				break;
			default:
				break;
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Returned File Path is:::" + returFilePath);
			}
			return returFilePath;

		} catch (NullPointerException e) {
			LOGGER.error("Not able to find File with name:" + fileName
					+ " in classPath. Class looking for this file is:"
					+ className.toString() + ". Hence returning Null");
			LOGGER.error(e);
			return null;
		}
	}
	
	public static String getFullMethodName(Method m) {
		return m.getDeclaringClass().getName() + "." + m.getName();
	}
}
