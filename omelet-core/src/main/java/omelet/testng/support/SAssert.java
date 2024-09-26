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

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import omelet.driver.Driver;
import omelet.driver.DriverInitialization;
import omelet.driver.DriverUtility;
import omelet.driver.SuiteConfiguration;

import org.testng.asserts.Assertion;
import org.testng.asserts.IAssert;

import com.beust.jcommander.internal.Maps;

/****
 * This is soft assert class
 * 
 * @author kapilA
 * 
 */
public class SAssert extends Assertion {

	public static InheritableThreadLocal<Map<AssertionError, IAssert>> m_errors = new InheritableThreadLocal<Map<AssertionError, IAssert>>() {
		public Map<AssertionError, IAssert> initialValue() {
			return Maps.newHashMap();
		}
	};

	public static InheritableThreadLocal<Map<IAssert, String>> assertMap = new InheritableThreadLocal<Map<IAssert, String>>() {
		public Map<IAssert, String> initialValue() {
			return new LinkedHashMap<IAssert, String>();
		}
	};

	@Override
	public void executeAssert(IAssert assertObj) {
		try {
			assertObj.doAssert();
			assertMap.get().put(assertObj, "");
		} catch (AssertionError ex) {
			String screenShotPath;
			String screenShotName = "";
			if (Driver.getBrowserConf().isScreenShotFlag()) {
				screenShotName = UUID.randomUUID().toString();
				screenShotPath = DriverInitialization.outPutDir
						+ File.separator + screenShotName;
				DriverUtility
						.takeScreenShot(Driver.getDriver(), screenShotPath);
			}
			screenShotPath = "../" + SuiteConfiguration.suiteName + "/"
					+ screenShotName;
			assertMap.get().put(assertObj, screenShotPath);
			m_errors.get().put(ex, assertObj);
		}
	}

	/***
	 * perform all the assertion
	 */
	public void assertAll() {
		if (!m_errors.get().isEmpty()) {
			StringBuilder sb = new StringBuilder(
					"The following asserts failed:\n");
			boolean first = true;
			for (Map.Entry<AssertionError, IAssert> ae : m_errors.get()
					.entrySet()) {
				if (first) {
					first = false;
				} else {
					sb.append(", ");
				}
				sb.append(ae.getValue().getMessage());
			}
			throw new AssertionError(sb.toString());
		}
	}
}
