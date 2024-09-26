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

import omelet.common.OSName;
import omelet.common.OSName.OSN;

/***
 * Keys which can be set from CommandLine , in any property file ,XML etc
 * 
 * @author kapilA
 * 
 */
public class DriverConfigurations {

	/**
	 * 
	 * @author borz01
	 * 
	 */
	public enum FrameworkConfig {
		remoteflag("false"), drivertimeOut("30"), retryfailedtestcase("0"), highlightelementflag(
				"false"), screenshotflag("true"), datasource("");
		private String defaultValue;

		FrameworkConfig(String defaultValue) {
			this.defaultValue = defaultValue;
		}

		public String get() {
			return this.defaultValue;
		}

	}

	/**
	 * 
	 * @author borz01
	 * 
	 */
	public enum CloudConfig {
		/*
		 * device(""), browserVersion(""), os(""), osVersion( ""),
		 * bsSwitch("false"), , bs_localTesting( "false"), bs_urls(""),
		 * platform(""), mobileTest("false");
		 */
		username(""), key("");
		private String defaultValue;

		CloudConfig(String defaultValue) {
			this.defaultValue = defaultValue;
		}

		public String get() {
			return this.defaultValue;
		}
	}

	/**
	 * 
	 * @author borz01
	 * 
	 */
	public enum LocalEnvironmentConfig {

		browsername("FireFox"), iedriverpath(System.getProperty("user.dir")
				+ "/src/main/resources/IEDriverServer.exe".replace("/",
						File.separator)), chromedriverpath(System
				.getProperty("user.dir")
				+ "/src/main/resources/chromedriver".replace("/",
						File.separator)), phantomdriverpath(System
								.getProperty("user.dir")
								+ "/src/main/resources/phantomjs".replace("/",
										File.separator)),firefoxdriverpath(System
				.getProperty("user.dir")
				+ "/src/main/resources/geckodriver".replace("/",
				File.separator));

		private String defaultValue;

		LocalEnvironmentConfig(String defaultValue) {
			if (defaultValue.contains("chromedriver")) {
				if (OSName.get().equals(OSN.WIN)) {
					this.defaultValue = defaultValue.replace("chromedriver",
							"chromedriver.exe");
				} else {
					this.defaultValue = defaultValue;
				}
			}
			else if (defaultValue.contains("firefox")) {
				if (OSName.get().equals(OSN.WIN)) {
					this.defaultValue = defaultValue.replace("geckodriver",
							"geckodriver.exe");
				} else {
					this.defaultValue = defaultValue;
				}
			}
			else if (defaultValue.contains("phantomjs")) {
				if (OSName.get().equals(OSN.WIN)) {
					this.defaultValue = defaultValue.replace("phantomjs",
							"phantomjs.exe");
				} else {
					this.defaultValue = defaultValue;
				}
			} else {
				this.defaultValue = defaultValue;
			}
			
		}

		public String get() {
			return this.defaultValue;
		}

	}

	/**
	 * 
	 * @author borz01
	 * 
	 */
	public enum HubConfig {
		host("localhost"), port("4444");
		private String defaultValue;

		HubConfig(String defaultValue) {
			this.defaultValue = defaultValue;
		}

		public String get() {
			return this.defaultValue;
		}

	}
}
