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
package omelet.data.driverconf;

import org.openqa.selenium.remote.DesiredCapabilities;

public interface IBrowserConf {

	String getBrowser();


	String getuserName();

	String getKey();
	
	String host();
	
	String port();

	boolean isRemoteFlag();

	Integer getDriverTimeOut();

	Integer getRetryFailedTestCaseCount();

	String getLocalIEServerPath();

	String getLocalChromeServerPath();
	
	String getLocalPhantomServerPath();

	String getLocalFirefoxServerPath();

	boolean isHighLightElementFlag();

	boolean isScreenShotFlag();
	
	String getDataSource();
	
	DesiredCapabilities getCapabilities();
	
	void updateCapabilities(DesiredCapabilities dc);
	

}
