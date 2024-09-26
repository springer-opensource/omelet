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

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.google.common.base.Stopwatch;
import static com.google.common.base.Preconditions.checkArgument;

/***
 * WebDriver Related Utility function
 * 
 * @author kapilA
 * 
 */
public class DriverUtility {

	public enum CHECK_UNCHECK {
		CHECK, UNCHECK
	}

	private static final Logger LOGGER = Logger.getLogger(DriverUtility.class);

	/***
	 * Generic waitFor Function which waits for condition to be successful else
	 * return null
	 * 
	 * @param expectedCondition
	 *            :ExpectedCondition
	 * @param driver
	 *            :WebDriver
	 * @param timeOutInSeconds
	 *            in seconds
	 * @return T or null
	 */
	public static <T> T waitFor(ExpectedCondition<T> expectedCondition,
			WebDriver driver, int timeOutInSeconds) {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		try {
			return new WebDriverWait(driver, timeOutInSeconds)
					.pollingEvery(500, TimeUnit.MILLISECONDS).until(
							expectedCondition);
		} catch (TimeoutException e) {
			LOGGER.error(e);
			return null;
		} finally {
			driver.manage()
					.timeouts()
					.implicitlyWait(Driver.getBrowserConf().getDriverTimeOut(),
							TimeUnit.SECONDS);
			stopwatch.stop();
			LOGGER.debug("Time Taken for waitFor method for Expected Condition is:"
					+ stopwatch.elapsedTime(TimeUnit.SECONDS));
		}
	}

	/***
	 * Switching between windows.
	 * 
	 * @param driver
	 * @param sString
	 *            :Target window Title
	 * @return:True if window switched
	 */
	public static boolean switchToWindow(WebDriver driver, String sString) {
		String currentHandle = driver.getWindowHandle();
		Set<String> handles = driver.getWindowHandles();
		if (!handles.isEmpty()) {
			for (String handle : handles) {
				LOGGER.debug("Switching to other window");
				driver.switchTo().window(handle);
				if (sString.equals(driver.getTitle())) {
					LOGGER.info("switched to window with title:" + sString);
					return true;
				}
			}
			driver.switchTo().window(currentHandle);

			LOGGER.info("Window with title:" + sString
					+ " Not present,Not able to switch");
			return false;
		} else {
			LOGGER.info("There is only one window handle :" + currentHandle);
			return false;
		}
	}

	/***
	 * Take Screen Shot
	 * 
	 * @param driver
	 * @param path
	 *            :File path to store screen Shot
	 * @author kapilA
	 */
	public static File takeScreenShot(WebDriver driver, String path) {
		File saved = new File(path);
		File scrFile;
		try {
			if (driver != null) {
				if (Driver.getBrowserConf().isRemoteFlag()) {
					Augmenter augumenter = new Augmenter();
					scrFile = ((TakesScreenshot) augumenter.augment(driver))
							.getScreenshotAs(OutputType.FILE);
				} else {
					scrFile = ((TakesScreenshot) driver)
							.getScreenshotAs(OutputType.FILE);
				}
				FileUtils.moveFile(scrFile, saved);
			} else {
				LOGGER.info("As the driver is null no point in taking screen shot");
			}
		} catch (Exception e) {
			LOGGER.info("Not able to take Screen Shot", e);
		}
		return saved;
	}

	/***
	 * Double click on WebElement using JavaScript or Actions Class
	 * 
	 * @param element
	 *            :Element on which Double click needs to be performed
	 * @param clickStrategy
	 *            : double click using javascript or using action class
	 * @param driver
	 * @author kapilA
	 */
	public static void doubleClick(WebElement element, WebDriver driver,
			CLICK_STRATEGY clickStrategy) {

		switch (clickStrategy) {

		case USING_ACTION:
			Actions action = new Actions(driver);
			action.doubleClick(element).perform();
			break;
		case USING_JS:
			((JavascriptExecutor) driver)
					.executeScript(
							"var evt = document.createEvent('MouseEvents');"
									+ "evt.initMouseEvent('dblclick',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);"
									+ "arguments[0].dispatchEvent(evt);",
							element);
			break;
		default:
			String clickStrategyParameter;
			try {
				clickStrategyParameter = clickStrategy.toString();
			} catch (Exception e) {
				clickStrategyParameter = "null";
			}
			LOGGER.error("Parameter missmatch: Unknown click strategy. "
					+ clickStrategyParameter);
		}
	}

	public enum CLICK_STRATEGY {
		USING_JS, USING_ACTION
	}

	/***
	 * Perform drag and drop
	 * 
	 * @param sourceElement
	 *            :element which need to be dragged
	 * @param targetElement
	 *            :element on which dragged Element needs to be dropped
	 * @param driver
	 *            :WebDriver
	 * @author kapilA
	 */
	public static void dragAndDrop(WebElement sourceElement,
			WebElement targetElement, WebDriver driver) {
		Actions a = new Actions(driver);
		a.dragAndDrop(sourceElement, targetElement).perform();
	}

	/***
	 * Select Value from Drop Down with visible Text ,if no Such Element Found <br>
	 * Select default index
	 * 
	 * @param webElement
	 *            :Select WebElement
	 * @param visibleText
	 *            :String to be Selected
	 * @param defaultIndex
	 *            :index to be selected by if value with string is not found
	 * @author kapilA
	 */
	public static void selectDropDown(WebElement webElement,
			String visibleText, Integer defaultIndex) {
		checkArgument(visibleText != null && !visibleText.isEmpty(),
				"Text Entered to method should not be null and not empty");
		Select s = new Select(webElement);
		try {
			s.selectByVisibleText(visibleText);
		} catch (NoSuchElementException e) {
			LOGGER.error(e);
			s.selectByIndex(defaultIndex);
		}
	}
	
	/**
	* select a drop down value by using partial text comparison
	* @param element
	* @param partialText
	* @author nageshM
	*
	*/
	public static void selectByPartialText(WebElement element, String partialText) {
		List<WebElement> optionList = element.findElements(By.tagName("option"));
		for (WebElement option : optionList) {
			if (option.getText().toLowerCase().contains(partialText.toLowerCase())) 
				option.click();
				break;
		}
	}
	
	/***
	 * Accept Or Dismiss Window Alert
	 * @param driver
	 * @param acceptOrDismiss
	 * 
	 * @author nageshM
	 */		
	public static void acceptOrDismissAlert(WebDriver driver,String acceptOrDismiss) {
		Alert alert = driver.switchTo().alert();
		if (acceptOrDismiss.toLowerCase().startsWith("a")) {
			alert.accept();
		} else if (acceptOrDismiss.toLowerCase().startsWith("d")) {
			alert.dismiss();
		}
	}
	
	/***
	 * Forcefully check/uncheck checkbox irrespective of the state(Element
	 * should be visible)
	 * 
	 * @param webElement
	 *            :Check box element
	 * @param checkUnCheck
	 *            enum
	 */
	public static void checkUncheckCheckBox(WebElement webElement,
			CHECK_UNCHECK checkUnCheck) {
		boolean checked = webElement.isSelected();
		if (checked) {
			if (checkUnCheck.toString().equalsIgnoreCase("uncheck")) {
				webElement.click();
			}
		} else {
			if (checkUnCheck.toString().equalsIgnoreCase("check")) {
				webElement.click();
			}
		}
	}
}
