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
import java.util.concurrent.TimeUnit;

import omelet.data.driverconf.IBrowserConf;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

/***
 * Driver factory Class for Returning Driver Instances
 *
 * @author kapilA
 *
 */
class DriverFactory {
    private static final Logger LOGGER = Logger.getLogger(DriverFactory.class);

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
    private DesiredCapabilities dc;
    WebDriver webDriver = null;

    public DriverFactory(IBrowserConf browserConf) {
        IBrowserConf browsConf = browserConf;
        this.dc = browserConf.getCapabilities();
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
        if (remoteFlag) {
            RemoteBrowser rb = this.new RemoteBrowser();
            webDriver = rb.returnRemoteDriver();
        } else if (browser.toLowerCase().startsWith("f")) {
            System.setProperty("webdriver.gecko.driver", fireFoxServerPath);
            LOGGER.debug("Returning firefox driver-Without Remote.");
            webDriver = new FirefoxDriver(dc);
        } else if (browser.toLowerCase().startsWith("i")) {
            System.setProperty("webdriver.ie.driver", ieServerPath);
            LOGGER.debug("Returning ie driver-Without Remote.");
            webDriver = new InternetExplorerDriver(dc);
        } else if (browser.toLowerCase().startsWith("c")) {
            System.setProperty("webdriver.chrome.driver", chromeServerPath);
            LOGGER.debug("Returning chrome driver-Without Remote.");
            webDriver = new ChromeDriver(dc);
        } else if (browser.toLowerCase().startsWith("h")) {
            LOGGER.info("Browser is HTMLUNIT");
            webDriver = new HtmlUnitDriver(dc);
        } else if (browser.toLowerCase().startsWith("p")) {
            System.setProperty("phantomjs.binary.path", phantomServerPath);
            LOGGER.info("Browser is PhantomJS");
            webDriver = new PhantomJSDriver(dc);
        }

        // For set driver timeout
        if (webDriver != null) {
            webDriver.manage().timeouts()
                    .implicitlyWait(driverTimeOut, TimeUnit.SECONDS);
        }

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

        public RemoteBrowser() {
            // setDesiredCapability();
            if (StringUtils.isBlank(dc.getBrowserName()))
                dc.setBrowserName(browser);
        }

        public WebDriver returnRemoteDriver() {
            String remoteUrl;
            if (host.contains("browserstack") || host.contains("sauce")
                    || host.contains("testingbot")) {
                remoteUrl = "http://" + USERNAME + ":" + AUTOMATE_KEY + "@"
                        + host + ":" + port + "/wd/hub";
            } else {
                remoteUrl = "http://" + host + ":" + port + "/wd/hub";
            }
            try {
                RemoteWebDriver driver = new RemoteWebDriver(
                        new URL(remoteUrl), dc);

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