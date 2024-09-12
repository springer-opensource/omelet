package omelet.data.driverconf;

import java.util.HashMap;
import java.util.Map;

import omelet.exception.FrameworkException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author borz01
 *
 */
public class ValidateBrowserRules {
	private static final Logger LOGGER = LogManager.getLogger(ValidateBrowserRules.class);
	
	Map<String, String> refinedBrowserConf = new HashMap<String, String>();
	StringBuilder exceptionMessage = new StringBuilder();
	IBrowserConf browserConf;

	public ValidateBrowserRules(Map<String, String> refinedBrowserConf) {
		this.refinedBrowserConf = refinedBrowserConf;
		browserConf = new BrowserConfR(refinedBrowserConf);		
	}

	public void checkAndThrowExceptionForLocalBrowser() {
		if (!browserConf.isRemoteFlag()) {
			if (browserConf.getBrowser().toLowerCase().startsWith("i")
					&& browserConf.getLocalIEServerPath().isEmpty()) {
				exceptionMessage
						.append("Not able to find IE Serverpath , Please set it!!");
			}

			if (browserConf.getBrowser().toLowerCase().startsWith("c")
					&& browserConf.getLocalChromeServerPath().isEmpty()) {
				exceptionMessage
						.append("Browser selected in Chrome and not able to find Chrome Server");
			}
		}

		throwExceptionIfAny();
	}

	/*public void checkAndThrowExceptionForRemote() {
		if (browserConf.isRemoteFlag() && !browserConf.isBrowserStackSwitch()) {
			if (browserConf.getRemoteURL().isEmpty()) {
				exceptionMessage
						.append("As the remote Flag is true , please set remoteURL");
			}
		}
		throwExceptionIfAny();
	}*/

/*	public void checkAndThrowExceptionForBrowserStack() {
		if (browserConf.isRemoteFlag() && browserConf.isBrowserStackSwitch()) {
			if (!browserConf.isMobileTest()) {
				checkForBSBrowserOs();
			} else {
				checkForBSMobile();
			}

			if (browserConf.isBsLocalTesting()) {
				checkForBSLocalTesting();
			}
			throwExceptionIfAny();
		}
	}*/

	/*private void checkForBSBrowserOs() {
		if (browserConf.getBrowserVersion().isEmpty()) {
			exceptionMessage
					.append("As BS switch is on ,Please enter browserVersion");
		}
		if (browserConf.getOsName().isEmpty()) {
			exceptionMessage.append("As BS switch is on ,Please enter OsName");
		}
		if (browserConf.getOsVersion().isEmpty()) {
			exceptionMessage
					.append("As BS switch is on ,Please enter osversion");
		}
		throwExceptionIfAny();
	}*/

	/*private void checkForBSLocalTesting() {
		if (browserConf.getBsURLs().get(0).isEmpty()) {
			exceptionMessage
					.append("Please provide your local testing url for tunnel setup");
		}
	}*/

/*	private void checkForBSMobile() {
		if (browserConf.getDevice().isEmpty()) {
			exceptionMessage
					.append("As we want to run on Mobile , please give the device");
		}
		if (browserConf.getPlatform().isEmpty()) {
			exceptionMessage.append("Please set platform");
		}
		if (!(browserConf.getBrowser().equalsIgnoreCase("android")
				|| browserConf.getBrowser().equalsIgnoreCase("iphone") || browserConf
				.getBrowser().equalsIgnoreCase("ipad"))) {
			exceptionMessage.append("please set proper browserName for Mobile");
		}
	}*/

	private void throwExceptionIfAny() {
		if (exceptionMessage.length() != 0) {
			LOGGER.info("!!!!!!!! "+exceptionMessage.toString()+" !!!!!!!!");
			throw new FrameworkException(exceptionMessage.toString());
		}
	}

	
	public void validate(){
		checkAndThrowExceptionForLocalBrowser();
		/*checkAndThrowExceptionForRemote();
		checkAndThrowExceptionForBrowserStack();*/
	}

}
