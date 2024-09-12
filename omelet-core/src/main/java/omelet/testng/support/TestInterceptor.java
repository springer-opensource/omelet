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
package omelet.testng.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

/***
 * Doesnot work if you are running in parallel , output will be distorted TestNG
 * Listener for updating console print ,Removing duplicate method run due to
 * RetryAnalyzer in report
 * 
 * @author kapilA
 * 
 */
public class TestInterceptor implements ITestListener {

	private static final Logger LOGGER = LogManager.getLogger(TestInterceptor.class);
	private boolean LogToStandardOutput = true;
	private static int count = 1;

	public void onTestStart(ITestResult arg0) {
		Reporter.setCurrentTestResult(arg0);
		Reporter.log("<br>");
		Reporter.log(
				"********************************************************",
				LogToStandardOutput);
		Reporter.log("</br>");
		int no = count++;
		Reporter.log(no + "::Initiating TestCase::" + arg0.getName() + " ("
				+ arg0.getMethod().getId() + ")", LogToStandardOutput);
		LOGGER.debug(no + "::Initiating TestCase::" + arg0.getName() + " ("
				+ arg0.getMethod().getId() + ")");
		Reporter.log("</br>");
		Reporter.log("Start Time::" + getTimeReport(), LogToStandardOutput);
		LOGGER.debug("Start Time::" + getTimeReport());
		Reporter.log("</br>");
	}

	public void onTestSuccess(ITestResult arg0) {
		Reporter.log("End Time " + getTimeReport(), LogToStandardOutput);
		LOGGER.debug("End Time " + getTimeReport());
		Reporter.log("</br>");
		long ms = arg0.getEndMillis() - arg0.getStartMillis();
		Reporter.log("Execution Time :: " + ms / 1000 + "." + ms % 1000
				+ " Seconds", LogToStandardOutput);
		Reporter.log("</br>");
		Reporter.log("Completed TestCase :: " + arg0.getName()
				+ " => Status: PASS", LogToStandardOutput);
		LOGGER.debug("Completed TestCase :: " + arg0.getName()
				+ " => Status: PASS");
		Reporter.log("</br>");
		Reporter.log(
				"********************************************************",
				LogToStandardOutput);
	}

	public void onTestFailure(ITestResult arg0) {
		Reporter.log("End Time " + getTimeReport(), LogToStandardOutput);
		LOGGER.debug("End Time " + getTimeReport());
		Reporter.log("</br>");
		long ms = arg0.getEndMillis() - arg0.getStartMillis();
		Reporter.log("Execution Time :: " + ms / 1000 + "." + ms % 1000
				+ " Seconds", LogToStandardOutput);
		Reporter.log("</br>");
		Reporter.log("Completed TestCase :: " + arg0.getName()
				+ " => Status: FAIL", LogToStandardOutput);
		LOGGER.debug("Completed TestCase :: " + arg0.getName()
				+ " => Status: FAIL");
		Reporter.log("</br>");
	}

	public void onTestSkipped(ITestResult result) {

	}

	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

	}

	public void onStart(ITestContext context) {
		Reporter.log("Starting with suite :: "
				+ context.getSuite().getParallel());
	}

	/***
	 * This is used for custom reports due to retry Analyzer Setting the id will
	 * take care of Data Provider as well
	 * 
	 * @param result
	 * @return
	 */
	public static int getId(ITestResult result) {
		
		int id = result.getTestClass().getName().hashCode();
		id = 31 * id + result.getMethod().getMethodName().hashCode();
		id = 31 * id + result.getMethod().getInstance().hashCode();
		id = 31
				* id
				+ (result.getParameters() != null ? Arrays.hashCode(result
						.getParameters()) : 0);
		return id;
	}

	/***
	 * This is for retry Analyzer report Logic is Passed = Overall Passed
	 * Failed, Passed = Overall Passed Failed ,Failed = Overall Failed Test Case
	 * will be logged once
	 * 
	 * TestLink will also get Updated Based on the input Params
	 */
	public void onFinish(ITestContext context) {
		List<ITestResult> testsToBeRemoved = new ArrayList<ITestResult>();
		Set<Integer> passedTest = new HashSet<Integer>();
		// Create passTest List
		for (ITestResult passTest : context.getPassedTests().getAllResults()) {
			passedTest.add(getId(passTest));
		}

		Set<Integer> failedTestID = new HashSet<Integer>();
		// create Fail test case list and list which are duplicate in short test
		// case to be removed
		for (ITestResult failTest : context.getFailedTests().getAllResults()) {
			int failTestID = getId(failTest);
			LOGGER.error(failedTestID.contains(failTestID));
			LOGGER.error(passedTest.contains(failTestID));
			if (failedTestID.contains(failTestID)
					|| passedTest.contains(failTestID)) {
				testsToBeRemoved.add(failTest);
			} else {
				failedTestID.add(failTestID);
			}
		}
		// update the context
		for (Iterator<ITestResult> iterator = context.getFailedTests()
				.getAllResults().iterator(); iterator.hasNext();) {
			ITestResult testResult = iterator.next();
			if (testsToBeRemoved.contains(testResult)) {
				iterator.remove();
			}
		}
	}

	private String getTimeReport() {
		Calendar calendar = new GregorianCalendar();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minutes = calendar.get(Calendar.MINUTE);
		int seconds = calendar.get(Calendar.SECOND);
		return ":: " + hour + ":" + minutes + ":" + seconds;
	}
}
