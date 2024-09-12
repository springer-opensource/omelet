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
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import omelet.exception.FrameworkException;

public class PropertyValueMin {

	Properties prop = new Properties();
	Map<String, String> propertiesValue = Collections
			.synchronizedMap(new HashMap<String, String>());
	Set<String> classEnumCheck = new HashSet<String>();
	FileInputStream fis;
	boolean isEnumMappingChecked;
	private static final Logger LOGGER = LogManager.getLogger(PropertyValueMin.class);

	public PropertyValueMin(Properties prop) {
		this.prop = prop;
		createHashMap(prop);
	}

	@SuppressWarnings("unused")
	private PropertyValueMin() {

	}

	public PropertyValueMin(String filePath) {
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

	/***
	 * return the value of the Enum passed for the properties file passed in
	 * constructor of the class If user is happy with null return then
	 * CheckEnumMapping can be removed from this
	 */

	public <E extends Enum<E>> String getValue(E key) {
		String value;
		try {
			value = propertiesValue.get(key.toString());
			if (value == null) {
				throw new NullPointerException();
			}
			return value;
		} catch (NullPointerException e) {
			LOGGER.debug(e);
			throw new FrameworkException("Value for key: " + key
					+ " not specified in Data file");
		}
	}

	/***
	 * return the value of the key passed for the properties file passed in
	 * constructor of the class
	 */
	public String getValue(String key) {
		return propertiesValue.get(key);

	}

	/***
	 * Create has map for all the loaded properties file
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

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append("<script language='JavaScript'>");
		sb.append("function change(text){testData.innerHTML=text}");
		sb.append("</script>");

		StringBuilder newSb = new StringBuilder();
		for (String key : propertiesValue.keySet()) {
			newSb.append(key).append(":").append(propertiesValue.get(key)).append("<br>");
		}
		sb.append("<a href='Data' onmouseover=\"javascript:change('").append(newSb.toString())
		  .append("')\" onmouseout=\"javascript:change('')\">Data</a>");
		sb.append("<div id='testData'></div>");
		return sb.toString();
	}

}
