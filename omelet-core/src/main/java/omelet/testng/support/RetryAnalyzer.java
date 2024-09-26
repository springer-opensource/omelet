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

import omelet.configuration.DefaultBrowserConf;
import omelet.data.driverconf.IBrowserConf;
import omelet.driver.Driver;

import org.apache.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/***
 * For Re-Running Failed Test Cases
 * 
 * @author kapilA
 * 
 */
public class RetryAnalyzer implements IRetryAnalyzer {

	private static final Logger LOGGER = Logger.getLogger(RetryAnalyzer.class);
	private int count = 0;

	public boolean retry(ITestResult result) {
		IBrowserConf browserConf = Driver.getBrowserConf();
		//below is required if test case without Browser is used i.e. @Test method without IBrowserConf & IProperty as param
		if(null == browserConf)
			browserConf = DefaultBrowserConf.get();
		int maxCount = browserConf
				.getRetryFailedTestCaseCount();
		LOGGER.debug("Max retry count for a Test case is: " + maxCount);
		if (count < maxCount) {
			LOGGER.info("Error in " + result.getName() + " with status "
					+ result.getStatus() + " Retrying " + count + " times");
			count++;
			return true;
		}
		return false;
	}

}
