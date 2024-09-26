package pageobjects;

import omelet.common.ExpectedConditionExtended;
import omelet.data.IProperty;
import omelet.driver.DriverUtility;
import omelet.exception.FrameworkException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class OmeletRepoPage {
	
	private WebDriver driver;
	@FindBy(css = ".btn.btn-sm.new-pull-request-btn")
	private WebElement btnNewPullReuest;
	@FindBy(xpath = "//button[@class='btn btn-sm btn-primary select-menu-button js-menu-target']/span")
	private WebElement btnCloneOrDownload;

	private IProperty prop;

	
	public OmeletRepoPage(WebDriver driver){
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public OmeletRepoPage(WebDriver driver,IProperty prop){
		this.driver = driver;
		this.prop = prop;
		PageFactory.initElements(driver, this);
	}
	
	public OmeletRepoPage isLoaded(){
		if(null == DriverUtility.waitFor(ExpectedConditionExtended.elementToBeClickable(btnNewPullReuest), driver, 10)){
			throw new FrameworkException("Omelet Repo Page is not loaded in 10 seconds");
		}
		return this;
	}
	
	public String getBtnText(){
		return btnCloneOrDownload.getText();
	}
	


}
