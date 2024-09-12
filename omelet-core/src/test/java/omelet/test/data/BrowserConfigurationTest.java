package omelet.test.data;

import java.io.File;

import omelet.data.DriverConfigurations;

import org.testng.Assert;
import org.testng.annotations.Test;

public class BrowserConfigurationTest {
	static final String REMOEFLAG = "false";
	static final String DRIVERTIMEOUT = "30";
	static final String RETRYFAILEDTESTCASE = "0";
	static final String HIGHLIGHTELEMENTFLAG = "false";
	static final String SCREENSHOTFLAG = "true";

	static final String BSSWITCH = "false";
	static final String BSLOCALTESTING = "false";
	static final String MOBILETEST = "false";
	static final String BSUSERNAME = "";
	static final String BSKEY = "";
	static final String BSURLS = "";
	static final String DEVICE = "";
	static final String BROWSERNAME = "fireFox";
	static final String BROWSERVERSION = "";
	static final String OS = "";
	static final String OSVERSION = "";
	static final String PLATFORM = "";

	static final String BROWSERNAMELOCAL = "fireFox";
	static final String IEDRIVERPATH = System.getProperty("user.dir")+"/src/main/resources/IEDriverServer.exe".replace("/", File.separator);
	static final String CHROMEDRIVERPATH = System.getProperty("user.dir")+"/src/main/resources/chromedriver".replace("/", File.separator);

	static final String REMOTEURL = "localhost";

	@Test
	public void verifyFrameworkConfigDefaults() {
		Assert.assertEquals(DriverConfigurations.FrameworkConfig.remoteflag.get(), REMOEFLAG);
		Assert.assertEquals(DriverConfigurations.FrameworkConfig.drivertimeOut.get(),
				DRIVERTIMEOUT);
		Assert.assertEquals(DriverConfigurations.FrameworkConfig.retryfailedtestcase.get(),
				RETRYFAILEDTESTCASE);
		Assert.assertEquals(DriverConfigurations.FrameworkConfig.highlightelementflag.get(),
				HIGHLIGHTELEMENTFLAG);
		Assert.assertEquals(DriverConfigurations.FrameworkConfig.screenshotflag.get(),
				SCREENSHOTFLAG);
	}
	
	@Test
	public void verifyBrowserStackConfigDefaults() {
	
		Assert.assertEquals(
				DriverConfigurations.CloudConfig.username.get(),
				BSUSERNAME);
		Assert.assertEquals(
				DriverConfigurations.CloudConfig.key.get(), BSKEY);
		Assert.assertEquals(
				DriverConfigurations.LocalEnvironmentConfig.browsername.get(),
				BROWSERNAME);
	}

	@Test
	public void verifyLocalConfigDefaults() {
		Assert.assertEquals(DriverConfigurations.LocalEnvironmentConfig.browsername.get(),
				BROWSERNAMELOCAL);
		Assert.assertEquals(DriverConfigurations.LocalEnvironmentConfig.iedriverpath.get(),
				IEDRIVERPATH);
		Assert.assertTrue(DriverConfigurations.LocalEnvironmentConfig.chromedriverpath.get().contains(CHROMEDRIVERPATH));
	}
	
	@Test
	public void verifyHubConfigDefaults() {
		Assert.assertEquals(DriverConfigurations.HubConfig.host.get(),
				REMOTEURL);
	}
}
