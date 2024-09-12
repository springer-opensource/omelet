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

/***
 * This class highlight WebElement before clicking and in any value change
 * and add 3 seconds delay
 * @author kapilA
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;

public class MyWebDriverListner extends AbstractWebDriverEventListener {

	private static Logger LOGGER = LogManager.getLogger(MyWebDriverListner.class);

	@Override
	public void beforeClickOn(WebElement element, WebDriver driver) {
		highlight(element, driver);
		sleep(3);
		disableHighlight(element, driver);
	}

	
	public void beforeChangeValueOf(WebElement element, WebDriver driver) {
		highlight(element, driver);
		sleep(3);
		disableHighlight(element, driver);
	}

	public void highlight(WebElement element, WebDriver driver) {
		((JavascriptExecutor) driver).executeScript(
				"arguments[0].setAttribute('style', arguments[1]);", element,
				"color: yellow; border: 5px solid red;");
	}

	public void disableHighlight(WebElement element, WebDriver driver) {
		try {
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].setAttribute('style', arguments[1]);",
					element, "");
		} catch (Exception e) {
			// ignoring the exception which will usually would be
			// NoSuchElementException & StaleElementRefrenceException
		}
	}

	public void sleep(int timeOutInSeconds) {
		try {
			Thread.currentThread();
			Thread.sleep(timeOutInSeconds * 1000);
		} catch (InterruptedException e) {
			LOGGER.error(e);
		}
	}

}
