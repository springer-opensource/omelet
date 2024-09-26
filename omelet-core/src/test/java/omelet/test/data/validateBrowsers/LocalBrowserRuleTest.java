package omelet.test.data.validateBrowsers;

import java.util.HashMap;
import java.util.Map;

import omelet.data.DriverConfigurations;
import omelet.data.driverconf.ValidateBrowserRules;
import omelet.exception.FrameworkException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LocalBrowserRuleTest {

	Map<String,String> browserMap;
	
	@BeforeMethod
	public void setup(){
		browserMap = new HashMap<String, String>();
		browserMap.put(DriverConfigurations.FrameworkConfig.remoteflag.toString(), "false");
	}
	
	@Test(expectedExceptions=FrameworkException.class)
	public void verifyIfBrowserIsChromeAndServerPathEmpty(){
		
		browserMap.put(DriverConfigurations.LocalEnvironmentConfig.browsername.toString(), "chrome");
		browserMap.put(DriverConfigurations.LocalEnvironmentConfig.chromedriverpath.toString(), "");
		ValidateBrowserRules vbr = new ValidateBrowserRules(browserMap);
		vbr.checkAndThrowExceptionForLocalBrowser();
	}
	
	@Test(expectedExceptions=FrameworkException.class)
	public void verifyIfBrowserIsIEAndServerPathEmpty(){
		browserMap.put(DriverConfigurations.LocalEnvironmentConfig.browsername.toString(), "IE");
		browserMap.put(DriverConfigurations.LocalEnvironmentConfig.iedriverpath.toString(), "");
		ValidateBrowserRules vbr = new ValidateBrowserRules(browserMap);
		vbr.checkAndThrowExceptionForLocalBrowser();
	}
	
	@Test()
	public void verifyIfBrowserIsChromeAndServerPathNotEmpty(){
		//There should not be any FrameworkExceptionThrown
		browserMap.put(DriverConfigurations.LocalEnvironmentConfig.browsername.toString(), "chrome");
		browserMap.put(DriverConfigurations.LocalEnvironmentConfig.chromedriverpath.toString(), "testServerPath");
		ValidateBrowserRules vbr = new ValidateBrowserRules(browserMap);
		vbr.checkAndThrowExceptionForLocalBrowser();
	}
	
	@Test()
	public void verifyIfBrowserIsIEAndServerPathNotEmpty(){
		//There should not be any FrameworkExceptionThrown
		browserMap.put(DriverConfigurations.LocalEnvironmentConfig.browsername.toString(), "IE");
		browserMap.put(DriverConfigurations.LocalEnvironmentConfig.iedriverpath.toString(), "testServerPath");
		ValidateBrowserRules vbr = new ValidateBrowserRules(browserMap);
		vbr.checkAndThrowExceptionForLocalBrowser();
	}
}
