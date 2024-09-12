package omelet.common;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import omelet.exception.ElementException;

public class CommonHelper {

	private static final Logger Log = LogManager.getLogger(CommonHelper.class);

	/**
	 * Web driver wait will wait for defined period of time
	 *
	 * @param waitTime
	 *            pass the amount of time needed to wait for element in seconds
	 * @param element
	 *            pass the element to wait
	 * @author mlp8076
	 */

	public static void waitForVisibilityOfElement(WebDriver driver, WebElement element, int waitTime) {

		expclicitWait(driver, waitTime).until(ExpectedConditions.visibilityOf(element));
	}

	/**
	 * Method to create webdriver wait object
	 *
	 * @param waitTimeInSeconds
	 *            pass number of seconds to wait for webdriver wait object
	 */

	public static WebDriverWait expclicitWait(WebDriver driver, int waitTimeInSeconds) {

		return new WebDriverWait(driver, waitTimeInSeconds);
	}

	/**
	 * Method to switch to latest window opened
	 *
	 * @param windowTitle
	 *            Pass the window title to switch to particular window, else pass "
	 *            " or null to switch to latest window
	 * @author mlp8076
	 * @throws ElementException
	 */

	public static boolean switchToWindow(WebDriver driver, String windowTitle) throws ElementException {

		Set<String> windowHandles = driver.getWindowHandles();

		Iterator<String> itr = windowHandles.iterator();

		Log.debug("Ids of all windows are " + windowHandles);

		boolean isSwitchedToWindow = false;

		try {

			if (windowTitle == null || windowTitle.isEmpty()) {
				while (itr.hasNext()) {

					driver.switchTo().window(itr.next());

					Log.debug("Title of window is " + driver.getTitle());
				}

			} else {

				while (itr.hasNext()) {

					driver.switchTo().window(itr.next());

					Log.debug("Title of window is " + driver.getTitle());

					if (driver.getTitle().contains(windowTitle)) {
						break;
					}
				}
			}
			isSwitchedToWindow = true;

		} catch (Exception e) {
			throw new ElementException("Switch To window Failed for title: " + windowTitle +"\n"+ e.getMessage());
		}

		Log.debug("switched to window and is: " + isSwitchedToWindow);
		return isSwitchedToWindow;

	}

	/**
	 * Method to switch to frame
	 *
	 * @param frameName
	 *            pass the frame name or id to switch to frame
	 * @author mlp8076
	 * @throws ElementException
	 */

	public static boolean switchToFrame(WebDriver driver, String frameName) throws ElementException {
		Log.debug("trying to switch to frame using framename: " + frameName);

		boolean isSwitchedToFrame = false;

		try {

			if (frameName != null && !frameName.isEmpty()) {

				expclicitWait(driver, 20).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameName));
				isSwitchedToFrame = true;

			}
		} catch (Exception e) {
			throw new ElementException("For frame: " + frameName + " Switch To Frame Action Failed " +"\n "+ e.getMessage());
		}

		Log.debug("switched to frame and is:" + isSwitchedToFrame);
		return isSwitchedToFrame;

	}

	/**
	 * Method to switch to frame
	 *
	 * @param frameElement
	 *            pass the frame webelement to switch to frame
	 * @author mlp8076
	 * @throws ElementException
	 */

	public static boolean switchToFrame(WebDriver driver, WebElement frameElement) throws ElementException {
		Log.debug("trying to switch to frame using webelement");

		boolean isSwitchedToFrame = false;

		try {

			if (frameElement != null) {

				expclicitWait(driver, 15).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameElement));
				isSwitchedToFrame = true;

			}
		} catch (Exception e) {
			throw new ElementException("Switch To Frame Action Failed using weblement: " +"\n "+ e.getMessage());
		}

		Log.debug("switched to frame and is:" + isSwitchedToFrame);
		return isSwitchedToFrame;

	}

	/**
	 * Method to switch to frame
	 *
	 * @param frameElement
	 *            pass the frame number to switch to frame
	 * @author mlp8076
	 * @throws ElementException
	 */

	public static boolean switchToFrame(WebDriver driver, Integer frameNubmer) throws ElementException {
		Log.debug("trying to switch to frame using frame number: " + frameNubmer);

		boolean isSwitchedToFrame = false;

		try {

			if (frameNubmer != null && !(frameNubmer < 0)) {

				expclicitWait(driver, 15).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameNubmer));
				isSwitchedToFrame = true;

			}
		} catch (Exception e) {
			throw new ElementException(
					"Switch To Frame Action Failed using frameNumber: " + frameNubmer +"\n "+ e.getMessage());
		}

		Log.debug("switched to frame and is:" + isSwitchedToFrame);
		return isSwitchedToFrame;

	}

	/**
	 * Method to statically wait uses Thread.sleep method
	 *
	 * @param timeOutInSeconds
	 *            pass the seconds to wait
	 * @author mlp8076
	 */

	public static void waitInSeconds(int timeOutInSeconds) {
		Log.debug(" waitInSeconds(): " + timeOutInSeconds + " seconds");

		try {
			Thread.sleep(timeOutInSeconds * 1000);
		} catch (Exception e) {
			Log.debug(e.getMessage());
		}
	}

	/**
	 * Method to check weather element is displayed or not
	 *
	 * @param element
	 *            pass the element to check if it is diplayed or not
	 * @author mlp8076
	 * @throws ElementException
	 */
	public static boolean isElementDisplayed(WebDriver driver, WebElement element, String nameOfWebElement)
			throws ElementException {

		boolean isDisplayed = false;
		try {
			if (element != null) {
				Log.debug("waiting for visiblity of element: " + nameOfWebElement);
				expclicitWait(driver, 15).until(ExpectedConditions.visibilityOf(element));
				isDisplayed = element.isDisplayed();
			} else {
				Log.debug("Element: " + nameOfWebElement + " is null");
				isDisplayed = false;
			}
		} catch (Exception e) {
			Log.error("Tried waiting for visibility of element:" + nameOfWebElement + " : for 20 seconds,error occured"
					+"\n "+ e.getMessage());
			throw new ElementException("Tried waiting for visibility of element: " + nameOfWebElement
					+ " : for 20 seconds,error occured" +"\n "+ e.getMessage());
		}
		return isDisplayed;
	}

	/**
	 * Method to check weather element is enabled or not
	 *
	 * @param element
	 *            pass the element to check if it is enabled or not
	 * @author mlp8076
	 * @throws ElementException
	 */
	public static boolean isElementEnabled(WebDriver driver, WebElement element, String nameOfWebElement)
			throws ElementException {
		boolean isEnabled = false;

		Log.debug("Trying to check if element is enabled ");
		try {
			if (element != null) {
				if (isElementDisplayed(driver, element, nameOfWebElement)) {

					isEnabled = element.isEnabled();
				} else {
					Log.error("Target Webelement: " + nameOfWebElement + "is not enabled");
				}
			} else {
				Log.error("Target Webelement " + nameOfWebElement + "is null");
			}
		} catch (Exception e) {
			Log.error("Target Webelement is not enabled");
			throw new ElementException("Target Webelement " + element.toString() + "is not enabled");

		}
		return isEnabled;
	}

	/**
	 * open the passed URL
	 * 
	 * @author mlp8076
	 * @param url
	 *            pass the URL to open the application
	 */

	public static void getURL(WebDriver driver, String url) {

		try {
			if (url.isEmpty() || url != null) {
				driver.get(url);
			}
		} catch (Exception e) {
			Log.fatal("Not able to open passed URL: " +"\n "+ e.getMessage());
		}
	}

	/**
	 * Method to return the title of page
	 * 
	 * @return title of page
	 */
	public static String getPageTitle(WebDriver driver) {

		return driver.getTitle();
	}

	/**
	 * This method checks for the alert to be present.
	 * 
	 * @param driver
	 * @return
	 */
	public static boolean isAlertPresent(WebDriver driver) {

		Log.debug("Check if Alert is present or not");

		try {
			driver.switchTo().alert();
			Log.debug("Alert Exists");
			return true;
		} catch (NoAlertPresentException e) {
			Log.debug("Alert Not Found: " +"\n "+ e.getMessage());

			return false;
		}
	}

	/**
	 * This method accepts the alert.
	 * 
	 * @param driver
	 */
	public static void dismissAlert(WebDriver driver) {
		try {
			expclicitWait(driver, 5).until(ExpectedConditions.alertIsPresent());
			Alert alert = driver.switchTo().alert();
			String text = alert.getText();
			alert.dismiss();
			Log.debug("Dismmissed alert");
			Log.debug("Alert Message : " + text);
		} catch (TimeoutException e) {
			Log.warn("Alert not present");
		}
	}

	/**
	 * This method accepts the alert.
	 * 
	 * @param driver
	 */
	public static boolean acceptAlert(WebDriver driver) {
		try {
			expclicitWait(driver, 5).until(ExpectedConditions.alertIsPresent());
			Alert alert = driver.switchTo().alert();
			String text = alert.getText();
			alert.accept();
			Log.debug("Alert accepted");
			Log.debug("Alert Text : " + text);
			return true;
		} catch (TimeoutException e) {
			Log.warn("Alert not present");
			return false;
		}
	}

	/**
	 * accept alert with js
	 */

	public static void acceptAlertbyJS(WebDriver driver) {

		Log.debug("Trying to accept alert by js");

		((JavascriptExecutor) driver).executeScript("window.confirm = function(msg) { return true; }");

		Log.debug("Alert is accepted by js");
	}

	/**
	 * To get next Random number
	 * 
	 * @return random integer
	 */
	public static int getRandomNumber() {
		return new Random().nextInt();
	}

	/**
	 * This Method is used to switch back to default content after switching to any
	 * frame.
	 * 
	 */
	public static void switchToDefaultContent(WebDriver driver) {

		Log.debug("switch to default content");

		driver.switchTo().defaultContent();

		Log.debug("switch to default content is successful");
	}

	/**
	 * this method will refresh the browser
	 * 
	 */
	public static void refreshBrowser(WebDriver driver) {

		Log.debug("before browser refresh");

		driver.navigate().refresh();

		Log.debug("after browser refresh");
	}

}
