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
package omelet.common;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
/***
 * Class having function similar to {@link ExpectedConditions} Should be used in
 * DriverUtility.waitFor(). Assumption of the methods in this class is that they
 * all will be used in WebDriverWait as we are not catching
 * NoSuchElementException ,If at all Custom FluentWait is the be used then catch
 * {@link NoSuchElementException}
 * 
 * @author kapilA
 * 
 */
public class ExpectedConditionExtended {

	private static final Logger LOGGER = LogManager
			.getLogger(ExpectedConditionExtended.class);

	/***
	 * hiding the constructor
	 */
	private ExpectedConditionExtended() {
	}

	/***
	 * wait for the WebElement to be Clickable
	 * 
	 * @param element
	 *            : WebElement
	 * @return ExpectedCondition
	 */
	public static ExpectedCondition<WebElement> elementToBeClickable(
			final WebElement element) {
		return new ExpectedCondition<WebElement>() {

			public WebElement apply(WebDriver driver) {
				try {
					if (element.isDisplayed() && element.isEnabled()) {
						return element;
					} else {
						return null;
					}
				} catch (StaleElementReferenceException e) {
					LOGGER.error(e);
					return null;
				} catch (NoSuchElementException e) {
					LOGGER.error(e);
					return null;
				}
			}

			@Override
			public String toString() {
				return "Element is not enabled";
			}
		};
	}

	/***
	 * wait for the Element to be Disabled
	 * 
	 * @param element
	 *            : WebElement
	 * @return boolean
	 */
	public static ExpectedCondition<Boolean> elementToBeDisabled(
			final WebElement element) {
		return new ExpectedCondition<Boolean>() {

			public ExpectedCondition<WebElement> visibilityOfElement = ExpectedConditions
					.visibilityOf(element);

			public Boolean apply(WebDriver driver) {
				boolean isDisabled = false;
				WebElement element = visibilityOfElement.apply(driver);
				try {
					if (element != null && !(element.isEnabled())) {
						isDisabled = true;
					}
					return isDisabled;
				} catch (StaleElementReferenceException e) {
					// TODO check if error, debug or warn
					LOGGER.warn("Element not found: " + element.toString());
					return isDisabled;
				}
			}

			@Override
			public String toString() {
				return "element to be clickable: " + element;
			}
		};
	}

	/**
	 * An expectation for checking that an element is either invisible or not
	 * present in the DOM.
	 * 
	 * @param webelement
	 *            used to find the element
	 * @return boolean
	 */
	public static ExpectedCondition<Boolean> invisibilityOfElementLocated(
			final WebElement webelement) {
		return new ExpectedCondition<Boolean>() {

			public Boolean apply(WebDriver driver) {
				try {
					return !(webelement.isDisplayed());
				} catch (NoSuchElementException e) {
					LOGGER.error(e);
					return true;
				} catch (StaleElementReferenceException e) {
					// Returns true , need to check if stale means invisible
					LOGGER.error(e);
					return true;
				}
			}

			@Override
			public String toString() {
				return "element to no longer be visible: "
						+ webelement.toString();
			}
		};
	}

	/**
	 * An expectation for checking that an element is either invisible or not
	 * present on the DOM.
	 * 
	 * @param locator
	 *            used to find the element
	 * @return boolean
	 */
	public static ExpectedCondition<Boolean> invisibilityOfElementLocated(
			final By locator) {
		return new ExpectedCondition<Boolean>() {

			public Boolean apply(@Nonnull WebDriver driver) {
				try {
					return driver.findElements(locator).isEmpty();
				} catch (NoSuchElementException e) {
					LOGGER.error(e);
					return true;
				} catch (StaleElementReferenceException e) {
					// Returns true , need to check if stale means invisible
					LOGGER.error(e);
					return true;
				}
			}

			@Override
			public String toString() {
				return "element to no longer be visible: ";
			}
		};
	}

	/***
	 * This method accepts n number of WebElements and check for click ability
	 * if any of the WebElement is not click able will return false
	 * 
	 * @param elements
	 * 						list of WebElements
	 * @return boolean
	 */
	public static ExpectedCondition<Boolean> elementsToBeClickable(
			final WebElement... elements) {
		final List<Boolean> statusList = new ArrayList<Boolean>();

		return new ExpectedCondition<Boolean>() {
			final StringBuilder sb = new StringBuilder();

			public Boolean apply(WebDriver driver) {
				for (WebElement w : elements) {
					try {
						if (w.isDisplayed() && w.isEnabled()) {
							statusList.add(true);
						} else {
							statusList.add(false);
						}
					} catch (StaleElementReferenceException e) {
						LOGGER.error(e);
						statusList.add(false);
					}
				}
				if (statusList.contains(false)) {
					statusList.clear();
					return false;
				}
				return true;
			}

			@Override
			public String toString() {
				return "elements to be clickable: " + sb;
			}
		};
	}

	/***
	 * Check clikability for the list of WebElement
	 * 
	 * @param elements
	 * 						list of WebElements
	 * @return boolean
	 */
	public static ExpectedCondition<Boolean> elementToBeClickable(
			final List<WebElement> elements) {
		final List<Boolean> statusList = new ArrayList<Boolean>();
		return new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				if (elements.isEmpty()) {
					return false;
				}
				statusList.clear();
				for (WebElement w : elements) {
					try {
						if (w != null && w.isEnabled() && w.isDisplayed()) {
							statusList.add(true);
						} else {
							return false;
						}
					} catch (StaleElementReferenceException e) {
						LOGGER.error(e);
						return false;
					}
				}
				LOGGER.debug("element size is:" + elements.size()
						+ " and is sucesfull list is:" + statusList.size());
				return statusList.size() == elements.size();
			}

			@Override
			public String toString() {
				return "One of the Element is not clickable:";
			}
		};
	}

	/***
	 * Check if all the element in the List are displayed
	 * 
	 * @param elements
	 * 						list of WebElements
	 * @return boolean
	 */
	public static ExpectedCondition<Boolean> elementToBeDisplayed(
			final List<WebElement> elements) {
		final List<Boolean> statusList = new ArrayList<Boolean>();
		return new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {

				for (WebElement w : elements) {
					try {
						if (w != null && w.isDisplayed()) {
							statusList.add(true);
						} else {
							return null;
						}
					} catch (StaleElementReferenceException e) {
						LOGGER.error(e);
						return null;
					}
				}
				return statusList.size() == elements.size();
			}

			@Override
			public String toString() {
				return "One of the Element is not clickable:";
			}
		};
	}
}