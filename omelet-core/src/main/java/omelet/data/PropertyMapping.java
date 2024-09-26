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
package omelet.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import omelet.exception.FrameworkException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/***
 * Load properties file
 * 
 * @author kapilA
 * 
 */
public class PropertyMapping implements IProperty {

	private Properties prop = new Properties();
	private Map<String, String> propertiesValue = Collections
			.synchronizedMap(new HashMap<String, String>());
	private Set<String> classEnumCheck = new HashSet<String>();
	private FileInputStream fis;
	boolean isEnumMappingChecked;
	private static final Logger LOGGER = Logger
			.getLogger(PropertyMapping.class);

	public PropertyMapping(Properties prop) {
		this.prop = prop;
		createHashMap(prop);
	}

	@SuppressWarnings("unused")
	private PropertyMapping() {

	}

	private void loadFile(String filePath) {
		try {
			fis = new FileInputStream(new File(filePath));
			prop.load(fis);
			fis.close();
			createHashMap(prop);
		} catch (IOException e) {
			LOGGER.error(e);
		} catch (NullPointerException aE) {
			// TODO: handle exception
			LOGGER.error(aE);
		} finally {
			IOUtils.closeQuietly(fis);
		}
	}

	public PropertyMapping(String filePath) {
		loadFile(filePath);
	}

	public PropertyMapping(String[] filePaths) {
		for (String filePath : filePaths) {
			loadFile(filePath);
		}
	}

	public PropertyMapping(Map<String, String> data) {
		propertiesValue = data;
	}

	/***
	 * Return values of the Key
	 */
	public <E extends Enum<E>> String getValue(E key) {
		// TODO : check if it is not producing unexpected
		// checkEnumMapping(key);
		String value;
		try {
			value = propertiesValue.get(key.toString());
			if (value == null) {
				throw new NullPointerException();
			}
			return value;
		} catch (NullPointerException e) {
			LOGGER.error(e);
			throw new FrameworkException("Value for key: " + key
					+ " not specified in Data file");
		}
	}

	/***
	 * return the value of the key passed for the properties file passed in
	 * constructor of the class
	 */
	public String getValue(String key) {
		String value;
		try {
			value = propertiesValue.get(key);
			if (value == null) {
				throw new NullPointerException();
			}
			return value;
		} catch (NullPointerException e) {
			LOGGER.error(e);
			throw new FrameworkException("Value for key: " + key
					+ " not specified in Data file");
		}
	}

	/***
	 * Create hash map for all the loaded properties file
	 * 
	 * @param prop
	 */
	private void createHashMap(Properties prop) {
		String key;
		for (Object o : prop.keySet()) {
			key = (String) o;
			propertiesValue.put(key, prop.getProperty(key));
		}
	}

	/***
	 * Compares the Enum with loaded properties file and prevents Null in Run
	 * time
	 * 
	 * @param key
	 */
	@SuppressWarnings("unused")
	private <E extends Enum<E>> void checkEnumMapping(E key) {
		String lineSeprator = System.getProperty("line.separator");
		if (!classEnumCheck.contains(key.getClass().getName())) {
			Set<String> misMatchEnum = new HashSet<String>();
			for (Enum<?> value : key.getClass().getEnumConstants()) {
				if (propertiesValue.get(value.toString()) == null) {
					misMatchEnum.add("Key with text=" + value.toString()
							+ " in class=" + key.getClass().getName() + ""
							+ " is not present in the loaded  file");
				}
			}
			if (!misMatchEnum.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				for (String aMisMatchEnum : misMatchEnum) {
					sb.append(aMisMatchEnum);
					sb.append(lineSeprator);
				}
				throw new FrameworkException(sb.toString());
			}
			classEnumCheck.add(key.getClass().getName());
		}
	}

	@Override
	public String toString() {
		Integer randomNum = 30000 + (int) (Math.random() * 90000000);

		StringBuilder sb = new StringBuilder();
		sb.append("<script language='JavaScript'>");
		sb.append("function showDiv").append(randomNum).append("() {");
		sb.append("document.getElementById('testData").append(randomNum).append("').style.display = 'block';");
		sb.append("document.getElementById('showData").append(randomNum).append("').style.display = 'none';");
		sb.append("document.getElementById('hideData").append(randomNum).append("').style.display = 'block';}");
		sb.append("function hideDiv").append(randomNum).append("() {");
		sb.append("document.getElementById('testData").append(randomNum).append("').style.display = 'none';");
		sb.append("document.getElementById('showData").append(randomNum).append("').style.display = 'block';");
		sb.append("document.getElementById('hideData").append(randomNum).append("').style.display = 'none';}");
		sb.append("</script>");
		sb.append("<br>");

		StringBuilder newSb = new StringBuilder();
		for (String key : propertiesValue.keySet()) {
			newSb.append("<span style='font-weight:normal'>").append(key).append("</span>").append(" : ")
				 .append("<span style='font-weight:bold'>").append(propertiesValue.get(key)).append("</span>")
				 .append("<br>");
		}
		sb.append("<a id='showData").append(randomNum).append("' onclick='showDiv").append(randomNum)
		  .append("()''>show test data</a>");
		sb.append("<a id='hideData").append(randomNum).append("' style='display:none' onclick='hideDiv")
		  .append(randomNum).append("()''>hide test data</a>");
		sb.append("<div id='testData").append(randomNum).append("' style='display:none'>").append(newSb.toString())
		  .append("</div>");
		return sb.toString();
	}
}
