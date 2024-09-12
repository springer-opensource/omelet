package omelet.common;

import static omelet.common.CommonHelper.isElementDisplayed;
import static omelet.common.CommonHelper.isElementEnabled;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import omelet.exception.ElementException;

/**
 * Element Action class has all the general actions needed for element
 * interactions Use {@link #click(WebElement)} Use
 * {@link #EnterText(WebElement , String)}
 *
 * @author mlp8076
 */
public class ElementActions {

	private static final Logger Log = LogManager.getLogger(ElementActions.class);

	/**
	 * Method to double click on element
	 *
	 * @param element
	 *            pass the web element for double click
	 * @return true when double click action happened
	 * @author mlp8076
	 * @throws ElementException
	 */

	public static boolean doubleClick(WebDriver driver, WebElement element, String nameOfWebElement)
			throws ElementException {
		boolean isDoubleClickSuccess = false;
		Log.debug("Double click Web Element");

		try {
			if (element != null) {
				Log.debug("Element for Double click  is:" + element.getText());
				if (isElementDisplayed(driver, element, nameOfWebElement)) {
					// double click to the Target element
					Actions actions = new Actions(driver);
					Action mouseHover = actions.doubleClick(element).build();
					mouseHover.perform();
					isDoubleClickSuccess = true;
				} else {
					Log.error("Target Webelement is not displayed");
					isDoubleClickSuccess = false;
				}
			} else {
				Log.error("Target Webelement is null");
			}
		} catch (Exception e) {
			Log.error("Double click of Web Element " + element.getText() + " failed " + e);
			throw new ElementException("Double click Action failed :: " + e.getMessage());
		}
		Log.debug("Double click Web Element was succesfull");
		return isDoubleClickSuccess;
	}

	/**
	 * Method to normal click on element
	 * 
	 * @param driver
	 *            webdriver instance
	 * @param nameOfWebElement
	 *            pass a meaningful name for this weblement. helps in debugging in
	 *            case of failure. Empty string is also accepted.
	 * @param element
	 *            pass the Web element for click
	 * @return true when click on element happened
	 * @author mlp8076
	 * @throws ElementException
	 */

	public static boolean click(WebDriver driver, WebElement element, String nameOfWebElement) throws ElementException {

		Log.debug("Trying to normal click on element:" + nameOfWebElement + " - " + element);

		if (!clickBySeleniumMethod(driver, element, nameOfWebElement)) {

			Log.debug("Trying to js click on element:" + nameOfWebElement + " - " + element);

			clickByJs(driver, element, nameOfWebElement);

			return true;
		}

		return false;
	}

	/**
	 * Method to call selenium click on element
	 *
	 * @param element
	 *            pass the Web element for click
	 * @return true when click on element happened
	 * @author mlp8076
	 * @throws ElementException
	 */

	private static boolean clickBySeleniumMethod(WebDriver driver, WebElement element, String nameOfWebElement)
			throws ElementException {
		boolean clickResult = false;
		try {
			if (isElementDisplayed(driver, element, nameOfWebElement)) {
				if (isElementEnabled(driver, element, nameOfWebElement)) {
					CommonHelper.expclicitWait(driver, 5).until(ExpectedConditions.elementToBeClickable(element));
					element.click();
					clickResult = true;
					Log.debug("clicked on element: " + nameOfWebElement);
				} else
					Log.error("Was unable to click the locator " + nameOfWebElement + " as it is not enabled");
			} else
				Log.error("Was unable to click the locator " + nameOfWebElement + " as it is not displayed");
		} catch (ElementClickInterceptedException e) {
			Log.error("Unable to click on: " + nameOfWebElement, element + " :element not found " + e);
			clickResult = false;
		} catch (Exception e) {
			Log.error("Webelement: " + nameOfWebElement + "-" + element + " :element not found " + e);
			throw new ElementException("Webelement: " + nameOfWebElement, element + " :element not found: " + "\n" + e);
		}
		return clickResult;
	}

	/**
	 * Method to click by js on element
	 *
	 * @param element
	 *            pass the Web element for click
	 * @return true when click on element happened
	 * @author mlp8076
	 * @throws ElementException
	 */

	private static boolean clickByJs(WebDriver driver, WebElement element, String nameOfWebElement)
			throws ElementException {
		boolean clickResult = false;
		try {
			if (isElementDisplayed(driver, element, nameOfWebElement)) {
				if (isElementEnabled(driver, element, nameOfWebElement)) {
					JavascriptExecutor executor = (JavascriptExecutor) (driver);
					executor.executeScript("arguments[0].click();", element);
					clickResult = true;
					Log.debug("Clicked using js on element" + nameOfWebElement);
				} else
					Log.error("Was unable to click the locator " + nameOfWebElement + " as it is not enabled");
			} else
				Log.error("Was unable to click the locator " + nameOfWebElement + " as it is not displayed");
		} catch (Exception e) {
			Log.error("Webelement: " + nameOfWebElement + " : " + element + " :element not found " + e);
			throw new ElementException("Webelement: " + nameOfWebElement, element + " :element not found: " + "\n" + e);
		}
		return clickResult;

	}

	/**
	 * Method to mouse hover on element
	 * 
	 * @param driver
	 * @param nameOfWebElement
	 * @param element
	 *            pass the Web element for click
	 * @return true when click on element happened
	 * @author mlp8076
	 * @throws ElementException
	 */
	public static boolean mouseHover(WebDriver driver, WebElement element, String nameOfWebElement)
			throws ElementException {
		boolean isMouseHoverSuccessful = false;
		Log.debug("Mouse Hover Web Element");
		try {
			if (element != null) {
				Log.debug("Element for mouse hovering is:" + element.getText());
				if (isElementDisplayed(driver, element, nameOfWebElement)) {
					// MouseHovering to the Target element
					Actions actions = new Actions(driver);
					Action mouseHover = actions.moveToElement(element).build();
					mouseHover.perform();
					isMouseHoverSuccessful = true;
				} else {
					Log.error("Target Webelement is not displayed");
					isMouseHoverSuccessful = false;
				}
			} else {
				Log.error("Target Webelement is null");
			}
		} catch (Exception e) {
			Log.error("Mouse Hovering of Web Element " + element.getText() + " failed " + e);
			throw new ElementException("Mouse Hovering Action failed :: " + e.getMessage());
		}
		Log.debug("Mouse Hover on Web Element was succesfull");
		return isMouseHoverSuccessful;
	}

	/**
	 * This Method is used to submit element which is in the form of type button and
	 * it should have submit attribute
	 * 
	 * @param driver
	 * @param element
	 * @return boolean
	 * @throws ButtonElementException
	 */
	public static boolean submit(WebDriver driver, WebElement element, String nameOfWebElement)
			throws ElementException {
		boolean elementResult = false;
		try {
			if (element != null) {
				if (isElementDisplayed(driver, element, nameOfWebElement)) {
					if (isElementEnabled(driver, element, nameOfWebElement)) {
						element.submit();
						elementResult = true;
					} else
						Log.error("Was unable to click the locator " + nameOfWebElement + " as it is not enabled");
				} else
					Log.error("Was unable to click the locator " + nameOfWebElement + " as it is not displayed");
			}
		} catch (Exception e) {
			Log.error("Webelement: " + nameOfWebElement + " - " + element + " :element not found " + e);
			throw new ElementException("Webelement: " + nameOfWebElement, element + " :element not found " + e);
		}
		return elementResult;
	}

	/**
	 * This Method is used to enter text for element.
	 * 
	 * @param driver
	 *            pass the WebDriver instance
	 * @param element
	 *            pass the webelement to enter the text
	 * @param nameOfWebElement
	 *            name of weblement will be used in debugging purpose
	 * @param textToEnter
	 *            pass the text to enter in textbox
	 * @return true in case text is entered
	 * @throws ElementException
	 */
	public static boolean enterText(WebDriver driver, WebElement element, String nameOfWebElement, String textToEnter)
			throws ElementException {

		boolean isTextEntered = false;
		try {
			if (element != null) {
				if (isElementDisplayed(driver, element, nameOfWebElement)) {
					if (isElementEnabled(driver, element, nameOfWebElement)) {
						if (element.getAttribute("value") != null && !element.getAttribute("value").isEmpty()) {
							if (!isReadOnly(element, nameOfWebElement))
								element.clear();
							CommonHelper.waitInSeconds(1);
						}
						element.sendKeys(textToEnter);
						Log.debug("Entered text:" + textToEnter + " \t in field: " + nameOfWebElement);

						String textValue = getText(driver, element, nameOfWebElement);
						if (textValue != null && !textValue.isEmpty()) {
							isTextEntered = true;
						} else {
							Log.error("Text: " + textToEnter + ":not entered in " + nameOfWebElement);
						}
					} else {
						Log.error("Unable to enter data in:" + nameOfWebElement + " :textbox as it is not enabled");
					}
				} else {
					Log.error("Unable to enter data in : " + nameOfWebElement + " :textbox as it is not displayed");
				}
			} else {
				Log.error("Unable to enter data in: " + nameOfWebElement
						+ " :textbox as the Value might be NULL or Empty");
			}
		} catch (Exception e) {
			Log.error("For passed Element:" + nameOfWebElement + " :textField not found " + e);
			throw new ElementException("For passed Element:" + nameOfWebElement + " :textField not found");
		}
		return isTextEntered;
	}

	/**
	 * return the text for passed webelement
	 * 
	 * @param driver
	 * @param textFieldElement
	 * @param nameOfWebElement
	 * @return
	 * @throws ElementException
	 */
	public static String getText(WebDriver driver, WebElement textFieldElement, String nameOfWebElement)
			throws ElementException {

		String returnText = null;
		try {
			if (textFieldElement != null) {
				if (isElementDisplayed(driver, textFieldElement, nameOfWebElement)) {
					if (isElementEnabled(driver, textFieldElement, nameOfWebElement)) {
						// waiting for 1 second as we are immediately reading
						// the message entered.
						CommonHelper.waitInSeconds(1);
						if (textFieldElement.getText() != null && !textFieldElement.getText().isEmpty()) {
							returnText = textFieldElement.getText();
						} else if (textFieldElement.getAttribute("value") != null
								&& !textFieldElement.getAttribute("value").isEmpty()) {
							returnText = textFieldElement.getAttribute("value");
						}

						Log.debug("Element:" + nameOfWebElement + "Text Field : Available data: " + returnText);
					} else {
						Log.error("Element:" + nameOfWebElement + " :textField is not enabled");
					}
				} else {
					Log.error("Element: " + nameOfWebElement + " textField not displayed");
				}
			} else {
				Log.error("Element: " + nameOfWebElement + " textField is null");
			}
		} catch (Exception e) {
			Log.error("Element: " + nameOfWebElement + " :textField not found " + e);
			throw new ElementException(nameOfWebElement + " :textField not found");
		}
		return returnText;

	}

	/**
	 * This Method is used to check for Element is ReadOnly.
	 * 
	 * @author mlp8076
	 * @param strLogicalName
	 * @param element
	 * @return boolean
	 * @throws ElementException
	 */
	public static boolean isReadOnly(WebElement element, String nameOfWebElement) throws ElementException {
		boolean isReadOnly = false;
		try {
			if (element != null) {
				if (element.getAttribute("readonly") != null
						&& (element.getAttribute("readonly").equalsIgnoreCase("readonly")
								|| element.getAttribute("readonly").equalsIgnoreCase("true"))) {
					isReadOnly = true;
				} else if (null != element.getAttribute("disabled")
						&& element.getAttribute("disabled").equalsIgnoreCase("true")) {
					isReadOnly = true;
				} else {
					isReadOnly = false;
					Log.debug(nameOfWebElement + " Element is not readonly");
				}
			} else {
				isReadOnly = false;
				Log.error(nameOfWebElement + " Element does not exist");
			}
		} catch (Exception e) {
			Log.error("Element: " + nameOfWebElement + ": WebElement not found " + e);
			throw new ElementException(nameOfWebElement + " :WebElement not found ");
		}
		return isReadOnly;
	}

	/**
	 * get the attribute of passed weblement
	 * 
	 * @param pass
	 *            the driver instance of web driver object
	 * @param element
	 *            pass the webelement
	 * @param attribute
	 *            pass the attribute value to get
	 * @param nameOfWebElement
	 *            pass the name of this webelement
	 * @return value of the attribute
	 * @throws ElementException
	 */
	public static String getAttribute(WebDriver driver, WebElement element, String attribute, String nameOfWebElement)
			throws ElementException {
		String attributeValue = null;

		try {
			if (element != null) {
				if (isElementDisplayed(driver, element, nameOfWebElement)) {
					attributeValue = element.getAttribute(attribute);
				} else {
					Log.debug("Element: " + nameOfWebElement + " is not displayed");
				}
			} else {
				Log.debug("Element: " + nameOfWebElement + " might be empty or null");
			}
		} catch (Exception e) {
			Log.error("Error occured while getting attribute: " + attribute + " of element: " + nameOfWebElement);
			throw new ElementException("Excetion occured while getting attribute: " + attribute + " of element: "
					+ nameOfWebElement + e.getMessage());
		}
		return attributeValue;
	}
}
