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

package omelet.driver;

import omelet.configuration.DefaultBrowserConf;
import omelet.data.driverconf.IBrowserConf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

/***
 * Driver class which returns Webdriver specific to configuration
 * 
 * @author kapilA
 * 
 */
public class Driver {

	private static final Logger LOGGER = LogManager.getLogger(Driver.class);
	protected static InheritableThreadLocal<IBrowserConf> browserConf = new InheritableThreadLocal<IBrowserConf>();
	protected static InheritableThreadLocal<WebDriver> driver = new InheritableThreadLocal<WebDriver>();

	/***
	 * sets the driver in ThreadLocal
	 */
	protected static void setDriverValue() {
		browserConf.set(DefaultBrowserConf.get());
		DriverFactory df = new DriverFactory(DefaultBrowserConf.get());
		driver.set(df.intializeDriver());
	}

	protected static void setDriverValue(IBrowserConf b_conf) {
		browserConf.set(b_conf);
		DriverFactory df = new DriverFactory(b_conf);
		driver.set(df.intializeDriver());
	}

	/***
	 * Fetch browserConfiguration
	 * 
	 * @return {@link IBrowserConf}
	 */
	public static IBrowserConf getBrowserConf() {
		return browserConf.get();
	}

	/***
	 * Fetch driver instance
	 * 
	 * @return {@link WebDriver}
	 * @author kapilA
	 */
	public static WebDriver getDriver() {
		try {

			if (driver.get() == null) {
				setDriverValue();
			}
			return driver.get();
		} catch (Exception e) {
			LOGGER.error(e);
			return null;
		}
	}

	/***
	 * Fetch driver instance for a particular Configuration
	 * 
	 * @return {@link WebDriver}
	 * @author kapilA
	 */
	public static WebDriver getDriver(IBrowserConf browserConf) {

		if (driver.get() == null) {
			setDriverValue(browserConf);
		}
		return driver.get();
	}

	/***
	 * This method checks if driver present yes then quit else ignore
	 */
	public static void tearDown() {
		if (driver.get() != null) {
			driver.get().quit();
		}
		if(null != browserConf.get()){
			browserConf.set(null);
		}
	}

	protected static boolean driverRemovedStatus() {

		WebDriver d = driver.get();
		if (d == null) {
			return true;
		}
		return false;
	}
}