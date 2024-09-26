package tests;

import omelet.data.IProperty;
import omelet.data.driverconf.IBrowserConf;
import omelet.driver.Driver;
import omelet.testng.support.SAssert;

import org.testng.annotations.Test;
import pageobjects.OmeletRepoPage;
import pageobjects.SpringerGitPage;

public class SampleTest {
	
	SAssert sassert = new SAssert();
	
	//@Test(description = "verify presence of Clone button on omelet repo page", enabled = true,dataProvider="XmlData")
	public void verifyOmeletGitPage_XML(IBrowserConf browserConf, IProperty prop) {
		SpringerGitPage sp = new SpringerGitPage(Driver.getDriver(browserConf), prop);
		sp.loadFromProperty().isLoaded().search().clickOnRepo();
		OmeletRepoPage orp = new OmeletRepoPage(Driver.getDriver());
		sassert.assertEquals(orp.isLoaded().getBtnText(),	"Clone or download", "Check for presence of clone button on omelet repo page");
		sassert.assertAll();
	}

	//Ignoring below test as it need google account id and P12 key
	//@Test(description = "verify presence of clone button on omelet repo page",dataProvider = "GoogleData")
	public void verifyOmeletGitPage_GoogleSheet(IBrowserConf browserConf,
			IProperty prop) {
		SpringerGitPage sp = new SpringerGitPage(Driver.getDriver(browserConf), prop);
		sp.loadFromProperty().isLoaded().search().clickOnRepo();
		/*OmeletRepoPage orp = new OmeletRepoPage(Driver.getDriver());
		sassert.assertEquals(orp.isLoaded().getBtnText(),	"Clone or download", "Check for presence of clone button on omelet repo page");*/
		sassert.assertAll();
	}


}
