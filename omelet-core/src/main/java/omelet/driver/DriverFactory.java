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

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import omelet.data.driverconf.IBrowserConf;

/***
 * Driver factory Class for Returning Driver Instances
 *
 * @author kapilA
 *
 */
class DriverFactory {
	private static final Logger LOGGER = LogManager.getLogger(DriverFactory.class);

	private boolean remoteFlag;
	private String browser;
	private String host;
	private String port;
	private int driverTimeOut;
	private String USERNAME;
	private String AUTOMATE_KEY;
	private String ieServerPath;
	private String chromeServerPath;
	private String phantomServerPath;
	private boolean ishiglightElementFlag;
	private String fireFoxServerPath;
	private Capabilities capabilities;
	WebDriver webDriver;

	public DriverFactory(IBrowserConf browserConf) {
		IBrowserConf browsConf = browserConf;
		this.capabilities = browserConf.getCapabilities();
		this.browser = browserConf.getBrowser();
		this.remoteFlag = browsConf.isRemoteFlag();
		this.host = browserConf.host();
		this.port = browserConf.port();
		this.driverTimeOut = browserConf.getDriverTimeOut();
		this.USERNAME = browsConf.getuserName();
		this.AUTOMATE_KEY = browsConf.getKey();
		this.ieServerPath = browserConf.getLocalIEServerPath();
		this.chromeServerPath = browserConf.getLocalChromeServerPath();
		this.phantomServerPath = browserConf.getLocalPhantomServerPath();
		this.fireFoxServerPath = browserConf.getLocalFirefoxServerPath();
		this.ishiglightElementFlag = browserConf.isHighLightElementFlag();
	}

	/***
	 * Return WebDriver either Remote/BrowserStack or local Browser based on
	 * remoteFlag
	 *
	 * @return
	 */
	public WebDriver intializeDriver() {
		RemoteBrowser rb = null;
		if (remoteFlag)
			rb = this.new RemoteBrowser();

		if (browser.toLowerCase().startsWith("f")) {

			System.setProperty("webdriver.gecko.driver", fireFoxServerPath);

			LOGGER.debug("Returning firefox driver-Without Remote.");

			FirefoxOptions options = new FirefoxOptions();
			options.merge(capabilities);

			options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			options.addPreference("toolkit.startup.max_resumed_crashes", 2);
			options.addPreference("browser.download.manager.showWhenStarting", false);
			options.addPreference("pdfjs.disabled", true);
			options.addPreference("plugin.scan.plid.all", false);
			options.addPreference("plugin.scan.Acrobat", "99.0");
			options.addPreference("plugin.disable_full_page_plugin_for_types",
					"application/pdf,application/vnd.adobe.xfdf,application/vnd.fdf,"
							+ "application/vnd.adobe.xdp+xml");
			options.addPreference("browser.helperApps.neverAsk.saveToDisk",
					"application/vnd.ms-excel, " + "application/octet-stream, application/pdf, application/vnd.fdf, "
							+ "application/x-unknown-application-octet-stream");
			options.addPreference("browser.helperApps.alwaysAsk.force", false);
			options.addPreference("network.http.use-cache", false);

			if (remoteFlag)
				webDriver = rb.returnRemoteDriver(options);
			else
				webDriver = new FirefoxDriver(options);

		} else if (browser.toLowerCase().startsWith("i")) {

			System.setProperty("webdriver.ie.driver", ieServerPath);

			InternetExplorerOptions options = new InternetExplorerOptions();

			options.merge(capabilities);

			LOGGER.debug("Returning ie driver-Without Remote.");

			if (remoteFlag)
				webDriver = rb.returnRemoteDriver(options);
			else
				webDriver = new InternetExplorerDriver(options);

		}

		else if (browser.toLowerCase().startsWith("c")) {

			System.setProperty("webdriver.chrome.driver", chromeServerPath);

			ChromeOptions options = new ChromeOptions();

			options.merge(capabilities);
			options.addArguments("--disable-extensions");
			options.addArguments("start-maximized");
			options.addArguments("--kiosk");
			options.setAcceptInsecureCerts(true);

			if (remoteFlag)
				webDriver = rb.returnRemoteDriver(options);
			else
				webDriver = new ChromeDriver(options);

		} else if (browser.toLowerCase().startsWith("h")) {

			LOGGER.info("Browser is HTMLUNIT");

			if (remoteFlag)
				webDriver = rb.returnRemoteDriver(capabilities);
			else
				webDriver = new HtmlUnitDriver(capabilities);

		}

		else if (browser.toLowerCase().startsWith("p"))

		{
			System.setProperty("phantomjs.binary.path", phantomServerPath);
			LOGGER.info("Browser is PhantomJS");

			if (remoteFlag)
				webDriver = rb.returnRemoteDriver(capabilities);
			else
				webDriver = new PhantomJSDriver(capabilities);

		}

		/**
		 * commenting this because it creates problem with explicit wait. Advised to use
		 * explicit wait whenever required.
		 */

		/*
		 * if (webDriver != null) {
		 * 
		 * webDriver.manage().timeouts().implicitlyWait(driverTimeOut,
		 * TimeUnit.SECONDS);
		 * 
		 * }
		 */

		if (ishiglightElementFlag) {

			EventFiringWebDriver efw = new EventFiringWebDriver(webDriver);
			efw.register(new MyWebDriverListner());
			webDriver = efw;

		}
		return webDriver;
	}

	/***
	 * This class return Remote Driver either for Hub or BrowserStack
	 *
	 * @author kapilA
	 *
	 */
	private class RemoteBrowser {

		public WebDriver returnRemoteDriver(Capabilities options) {

			// setDesiredCapability();
			if (StringUtils.isBlank(capabilities.getBrowserName()))
				LOGGER.debug("Browser name is not set");

			String remoteUrl;
			if (host.contains("browserstack") || host.contains("sauce") || host.contains("testingbot")) {
				remoteUrl = "http://" + USERNAME + ":" + AUTOMATE_KEY + "@" + host + ":" + port + "/wd/hub";
			} else {
				remoteUrl = "http://" + host + ":" + port + "/wd/hub";
			}
			try {
				RemoteWebDriver driver = new RemoteWebDriver(new URL(remoteUrl), options);

				// set local file detector for uploading file
				driver.setFileDetector(new LocalFileDetector());
				return driver;
			} catch (MalformedURLException e) {
				LOGGER.error(e);
				return null;
			}
		}
	}
}