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
import java.util.UUID;

import omelet.common.Utils;
import omelet.data.IMethodContext;
import omelet.data.IProperty;
import omelet.data.driverconf.IBrowserConf;
import omelet.testng.support.HtmlTable;
import omelet.testng.support.MethodContextCollection;
import omelet.testng.support.SAssert;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;
import org.testng.Reporter;

/***
 * This class initialize the thread local variable, clean up WebDriver
 * instances, and Report Generation, Screen shot etc
 * 
 * @author kapilA
 * 
 */
public class DriverInitialization implements IInvokedMethodListener {

	private static final Logger LOGGER = LogManager.getLogger(DriverInitialization.class);
	// output dir of TestNg currently being used in SAssert
	public static String outPutDir;

	/***
	 * This Method Set the driver if @BeforeMethod Configuration present if not
	 * then set the driver for @Test Methods
	 * 
	 * @author kapilA
	 */
	public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
		// Setting the output directory

		if (outPutDir == null) {
			outPutDir = testResult.getTestContext().getOutputDirectory();
		}
		if (method.getTestMethod().isBeforeMethodConfiguration()) {
			LOGGER.debug("Setting the WebDriver in Before Method");
			// Initializing browser so that will be same across all the child
			// threads
			Driver.browserConf.set(null);
		}

		if (method.isTestMethod()) {
			//if factory method is used then no need to set the browserConf to null ,it should be taken care in cleanup
			if (!isPartOfFactoryTest(method)) {
				Driver.browserConf.set(null);
			}
			// need as otherwise will produce unexpected output
			SAssert.m_errors.get();
			SAssert.assertMap.get();
		}

	}

	private boolean isPartOfFactoryTest(IInvokedMethod method) {
		java.lang.reflect.Method testMethod = method.getTestMethod()
				.getConstructorOrMethod().getMethod();
		if (testMethod.getGenericParameterTypes().length == 2
				&& testMethod.getGenericParameterTypes()[0]
						.equals(IBrowserConf.class)
				&& testMethod.getGenericParameterTypes()[1]
						.equals(IProperty.class)) {
			return false;
		}
		return true;
	}

	/***
	 * Quits Driver and generate table report, if @BeforeMethod is not present
	 * and @AfterMethod is present in TestClass then Driver.getDriver() in after
	 * method will be null
	 * 
	 * @author kapilA
	 */
	public void afterInvocation(IInvokedMethod method, ITestResult testResult) {

		if (method.isTestMethod() && !isPartOfFactoryTest(method)) {
			IMethodContext methodContext = MethodContextCollection
					.getMethodContext(Utils.getFullMethodName(method
							.getTestMethod().getConstructorOrMethod()
							.getMethod()));
			boolean beforeMethodPresent = methodContext.isBeforeMethod();
			boolean afterMethodShouldbePresent = methodContext.isAfterMethod();

			publishHtmlTable(testResult);
			addScreenShot(testResult);

			if (beforeMethodPresent) {
				if (!afterMethodShouldbePresent) {

					cleanup(method, testResult);
				}
			} else {
				cleanup(method, testResult);
			}
		}
		
		//Set reports after every test cases irrespective is the test method is as part of factory or independent parallel run
		//cleanReportingAfterEveryMethod(testResult);

		// Check for AfterMethod if present check for browser and quit
		if (method.getTestMethod().isAfterMethodConfiguration()) {
			if (Driver.driver.get() != null) {
				cleanup(method, testResult);
			}
		}
	}

	
	private void cleanup(IInvokedMethod method, ITestResult testResult) {
		try {
			Reporter.setCurrentTestResult(testResult);
			LOGGER.info("Quiting Driver for Method:"
					+ method.getTestMethod().getMethodName());
			if (!Driver.driverRemovedStatus()) {
				Driver.tearDown();
			}
		} catch (Exception e) {
			LOGGER.info(
					"Catching Exception in After Invocation So test Result are not altered due to it",
					e);
		} finally {
			if (!Driver.driverRemovedStatus()) {
				Driver.tearDown();
			}
			SAssert.m_errors.get().clear();
			SAssert.assertMap.get().clear();
		}
	}

	private void cleanReportingAfterEveryMethod(ITestResult testResult) {
		Reporter.setCurrentTestResult(testResult);
		publishHtmlTable(testResult);
		addScreenShot(testResult);
		SAssert.m_errors.get().clear();
		SAssert.assertMap.get().clear();
	}

	/***
	 * Adds HTML Table with data in the output report
	 * 
	 * @param testResult
	 */
	private void publishHtmlTable(ITestResult testResult) {
		try {
			Reporter.setCurrentTestResult(testResult);
			HtmlTable report = new HtmlTable(SAssert.assertMap.get(),
					testResult.getName());
			Reporter.log("Table Report is:::" + report.getTable());
		} catch (Exception e) {
			LOGGER.debug("Catching exception in public HTML Method", e);
		}
	}

	/***
	 * Add the screen shot to test case due to throwable exception other than
	 * Soft Assert
	 * 
	 * @param testResult
	 * @author kapilA
	 */
	private void addScreenShot(ITestResult testResult) {
		try {
			Reporter.setCurrentTestResult(testResult);
			if (Driver.getBrowserConf().isScreenShotFlag()) {
				if (testResult.getThrowable() != null) {
					String throwMessage = (testResult.getThrowable()
							.getMessage() != null) ? testResult.getThrowable()
							.getMessage() : "";
					if (!throwMessage.contains("asserts failed")) {
						String screenShotName = UUID.randomUUID().toString()
								+ ".png";
						String outPutDirectory = testResult.getTestContext()
								.getOutputDirectory();
						String filePath = outPutDirectory + File.separator
								+ screenShotName;
						DriverUtility.takeScreenShot(Driver.driver.get(),
								filePath);
						// Append the screen Shot in the Reporter Log
						Reporter.log("Test Case -" + testResult.getName()
								+ " failed due to exception screen shot below");
						Reporter.log("<div style=\"height:400px; width: 750px; overflow:scroll\"><img src=\""
								+ "../"
								+ SuiteConfiguration.suiteName
								+ "/"
								+ screenShotName + "\"></div>");
					}
				}
			}
		} catch (Exception e) {
			LOGGER.debug("Catching exception in add screen shot Method", e);
		}
	}
}
