package pageobjects;

import omelet.common.ExpectedConditionExtended;
import omelet.data.IProperty;
import omelet.driver.DriverUtility;
import omelet.exception.FrameworkException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class SpringerGitPage {
	
	private WebDriver driver;
	@FindBy(css = "#your-repos-filter")
	private WebElement txtRepoFilter;
	@FindBy(xpath = "(//a[contains(text(),'omelet')])[1]")
	private WebElement linkOmelet;

	private IProperty prop;

	
	public SpringerGitPage(WebDriver driver){
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public SpringerGitPage(WebDriver driver,IProperty prop){
		this.driver = driver;
		this.prop = prop;
		PageFactory.initElements(driver, this);
	}
	
	public SpringerGitPage loadFromProperty(){
		driver.get(prop.getValue("SpringerGit_url"));
		return this;
	}
	public SpringerGitPage isLoaded(){
		if(null == DriverUtility.waitFor(ExpectedConditionExtended.elementToBeClickable(txtRepoFilter), driver, 10)){
			throw new FrameworkException("SpringerGit Home Page is not loaded in 10 seconds");
		}
		return this;
	}
	
	public SpringerGitPage search(){
		txtRepoFilter.sendKeys(prop.getValue("SpringerGit_value"));
		return this;
	}
	
	public SpringerGitPage clickOnRepo(){
		linkOmelet.click();
		return this;
	}

}
