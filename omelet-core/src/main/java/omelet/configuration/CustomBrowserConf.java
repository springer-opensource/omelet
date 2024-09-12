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
package omelet.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import omelet.data.IProperty;
import omelet.data.PropertyMapping;
import omelet.data.driverconf.IBrowserConf;

/***
 * Set Custom initialization of {@link IBrowserConf} using this class
 * 
 * @author kapilA
 * 
 */
public class CustomBrowserConf {
	private static final Logger LOGGER = LogManager.getLogger(CustomBrowserConf.class);

	protected static IProperty prop;

	private CustomBrowserConf() {

	}

	/***
	 * Load Custom Properties file and initialize {@link DefaultBrowserConf} Param
	 * 
	 * @param filePath:Full
	 *            qualified path
	 * 
	 */
	public static void loadPropFile(String filePath) {
		LOGGER.info("Loading New Properties File:" + filePath);
		prop = new PropertyMapping(filePath);
		DefaultBrowserConf.loadCustomPropertiesFile(prop);
	}

	public static void loadPropFile(IProperty prop) {
		LOGGER.info("Loading New Properties File");
		DefaultBrowserConf.loadCustomPropertiesFile(prop);
	}

}
