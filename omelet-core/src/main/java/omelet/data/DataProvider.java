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
package omelet.data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import omelet.common.Utils;
import omelet.data.driverconf.IBrowserConf;
import omelet.exception.FrameworkException;
import omelet.testng.support.MethodContextCollection;

import org.apache.log4j.Logger;

/***
 * Data Provider class for the @Test Methods
 * 
 * @author kapilA
 * 
 */
public class DataProvider {

	public enum mapStrategy {
		Full, Optimal
	}

	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(DataProvider.class);


	/***
	 *
	 * @param m
	 *  method
	 * @return list of objects
	 */
	@org.testng.annotations.DataProvider(name = "GoogleData", parallel = true)
	public static Object[][] googleSheetDataProvider(Method m) {
		String testMethodName = Utils.getFullMethodName(m);
		return getData(testMethodName);
	}

	/***
	 *
	 * @param m
	 *  method
	 * @return list of objects
	 */
	@org.testng.annotations.DataProvider(name = "XmlData", parallel = true)
	public static Object[][] xmlDataProvider(Method m) {
		String methodName = Utils.getFullMethodName(m);
		return getData(methodName);
	}
	
	/*@org.testng.annotations.DataProvider(name = "XmlData", parallel = true)
	public static Object[][] xmlDataProvider1(ITestContext testContext) {
		String methodName = Utils.getFullMethodName(m);
		return getData(methodName);
	}*/
	

	public static List<IBrowserConf> filterSameBrowsers(
			List<IBrowserConf> fullBrowserList) {
		Set<IBrowserConf> browserConfSet = new HashSet<IBrowserConf>(
				fullBrowserList);
		return new ArrayList<IBrowserConf>(browserConfSet);
	}

	/***
	 * Removes duplicate browsers and prepare data based on the MapStrategy
	 * 
	 * @param methodName
	 * @return
	 */
	public static Object[][] getData(String methodName) {
		Object[][] testMethodData = null;
		List<IBrowserConf> browserConfFilteredList = filterSameBrowsers(MethodContextCollection.getMethodContext(methodName).
				getBrowserConf());
		List<IProperty> testMData = MethodContextCollection.getMethodContext(methodName).getMethodTestData();
		mapStrategy strategy = MethodContextCollection.getMethodContext(methodName).getRunStrategy();
		int browserConfCount = browserConfFilteredList.size();
		int testDataCount = testMData.size();
		verifyCount(browserConfCount, "IBrowserConf");
		verifyCount(testDataCount, "IProperty");
		int loopCombination;
		int k = 0;
		switch (strategy) {
		case Full:
			loopCombination = browserConfCount * testDataCount;
			testMethodData = new Object[loopCombination][2];

			for (int i = 0; i < browserConfCount; i++) {
				for (int j = 0; j < testDataCount; j++) {
					testMethodData[k][0] = browserConfFilteredList.get(i);
					testMethodData[k][1] = testMData.get(j);
					k++;
				}
			}
			break;
		case Optimal:
			/*if(testDataCount <=0)
				testDataCount = 1;*/
			if (browserConfCount >= testDataCount) {
				loopCombination = browserConfCount;
			} else {
				loopCombination = testDataCount;
			}
			testMethodData = new Object[loopCombination][2];
			for (int i = 0; i < loopCombination; i++) {
				Random r = new Random();
				// check whose value is greater and start the loop
				if (i >= browserConfCount) {
					testMethodData[i][0] = browserConfFilteredList.get(r
							.nextInt(browserConfCount));
				} else {
					testMethodData[i][0] = browserConfFilteredList.get(i);
				}
				if (i >= testDataCount) {
					testMethodData[i][1] = testMData.get(r
							.nextInt(testDataCount));
				} else {
					testMethodData[i][1] = testMData.get(i);
				}
			}
			break;
		default:
			break;
		}
		return testMethodData;
	}
	
	private static void verifyCount(int count,String dataName){
		if(count <=0){
			throw new FrameworkException("Data Provider of Type:"+dataName+"not present , there is some problem please check!");
		}
	}
	
}
