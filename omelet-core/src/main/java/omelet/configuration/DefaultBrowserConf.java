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

import java.util.HashMap;

import omelet.data.DriverConfigurations;
import omelet.data.IProperty;
import omelet.data.driverconf.IBrowserConf;
import omelet.data.driverconf.PrepareDriverConf;

import org.apache.log4j.Logger;

/***
 * This class set the variable required to configure Driver either from Command
 * Line or property file ,Command Line is given preference
 * 
 * @author kapilA
 * 
 */
public class DefaultBrowserConf {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger
			.getLogger(DefaultBrowserConf.class);
	private static IBrowserConf browserConf;
	private static IProperty customProp;

	/***
	 * hiding the constructor
	 */
	private DefaultBrowserConf() {

	}

	/***
	 * Return {@link IBrowserConf} depending on if {@link CustomBrowserConf} is
	 * used or Default values set configured
	 */
	public static IBrowserConf get() {
		// TODO: this browserConf is set once in complete execution , which
		// means if we do Driver.getDriver() multiple times then we get the same
		// conf
		if (browserConf == null) {
			synchronized (DefaultBrowserConf.class) {
				if (browserConf == null) {
					setEscapePropertyForReportNG();
					if (customProp == null) {
						browserConf = new PrepareDriverConf()
								.refineBrowserValues().checkForRules().get();
					} else {
						browserConf = new PrepareDriverConf(getKeyValue())
								.refineBrowserValues().checkForRules().get();
					}
				}
			}
		}
		return browserConf;
	}

	/****
	 * Check if custom properties file present or not if, present initialize
	 * customProp to prop file
	 */
	protected static void loadCustomPropertiesFile(IProperty prop) {
		// Delete the browserConf
		// Set Custom property
		browserConf = null;
		customProp = prop;
	}

	private static HashMap<String, String> getKeyValue() {
		HashMap<String, String> f_map = new HashMap<String, String>();
		for (DriverConfigurations.LocalEnvironmentConfig localConfig : DriverConfigurations.LocalEnvironmentConfig
				.values()) {
			f_map.put(localConfig.toString(), customProp.getValue(localConfig));
		}
		for (DriverConfigurations.CloudConfig bsConfig : DriverConfigurations.CloudConfig
				.values()) {
			f_map.put(bsConfig.toString(), customProp.getValue(bsConfig));
		}
		for (DriverConfigurations.HubConfig hubConfig : DriverConfigurations.HubConfig
				.values()) {
			f_map.put(hubConfig.toString(), customProp.getValue(hubConfig));
		}
		for (DriverConfigurations.FrameworkConfig frameworkConfig : DriverConfigurations.FrameworkConfig
				.values()) {
			f_map.put(frameworkConfig.toString(),
					customProp.getValue(frameworkConfig));
		}
		return f_map;
	}

	private static void setEscapePropertyForReportNG() {
		System.setProperty("org.uncommons.reportng.coverage-report", "true");
		final String ESCAPE_PROPERTY = "org.uncommons.reportng.escape-output";
		System.setProperty(ESCAPE_PROPERTY, "false");
	}
}
