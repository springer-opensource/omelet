package tests;

import omelet.data.IProperty;
import omelet.data.driverconf.IBrowserConf;
import omelet.driver.DriverManager;
import omelet.testng.support.SAssert;

import org.testng.annotations.Test;

import pageobjects.GooglePage;
import pageobjects.SeleniumPage;

public class SimpleGoogleTest {
	SAssert sassert = new SAssert();

	@Test(description = "verify if Selenium title is as expected", enabled = true, dataProvider = "XmlData")
	public void verifySeleniumTitleXML(IBrowserConf browserConf, IProperty prop) {
		GooglePage gp = new GooglePage(DriverManager.getSetDriver(browserConf), prop);
		gp.loadFromProperty().isLoaded().search("Selenium")
				.clickOnLink("Selenium - Web Browser Automation");
		// Selenium
		SeleniumPage sp = new SeleniumPage(DriverManager.getSetDriver());
		sassert.assertEquals(sp.isLoaded().getTitle(),
				prop.getValue("Selenium_Title"),
				"Check for the title of the page");
		sassert.assertAll();
	}

	// @Test(dataProvider = "GoogleData")
	public void verifySeleniumTitleGoogleSheet(IBrowserConf browserConf,
			IProperty prop) {
		GooglePage gp = new GooglePage(DriverManager.getSetDriver(browserConf), prop);
		gp.loadFromProperty().isLoaded().search("Selenium")
				.clickOnLink("Selenium - Web Browser Automation");
		// Selenium
		SeleniumPage sp = new SeleniumPage(DriverManager.getSetDriver());

		sassert.assertEquals(sp.isLoaded().getTitle(),
				prop.getValue("Selenium_Title"),
				"Check for the title of the page");
		sassert.assertAll();
	}
}
