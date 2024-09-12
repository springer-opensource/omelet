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
package omelet.test.data;

import java.util.ArrayList;
import java.util.List;

import omelet.data.DriverConfigurations;
import omelet.data.driverconf.IBrowserConf;
import omelet.data.xml.BrowserXmlParser;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class BrowserXmlParserTest {
	private static String browserVersion = "browserversion";
	private static String platform = "platform";
	private static String device = "device";
	private static String os = "os";
	private static String osVersion = "osversion";

	@BeforeMethod
	public void beforeTest() {

		System.setProperty(DriverConfigurations.LocalEnvironmentConfig.browsername.toString(), "");
		System.setProperty(browserVersion, "");
		System.setProperty(DriverConfigurations.CloudConfig.key.toString(), "");
	}

	@Test
	public void verifyBrowserConf_singleXml() {
		List<String> xmlName = new ArrayList<String>();
		xmlName.add("BrowseXmlP_2.xml");
		List<IBrowserConf> browserIs = BrowserXmlParser.getInstance().getBrowserConf1(xmlName);
		Assert.assertEquals(browserIs.size(), 1);
		IBrowserConf browserI = browserIs.get(0);
		Assert.assertEquals("firefox", browserI.getBrowser());
//		Assert.assertEquals("25", browserI.getCapabilities().getCapability(browserVersion));
		Assert.assertEquals("testkey", browserI.getKey());
		Assert.assertEquals("testusername", browserI.getuserName());
		Assert.assertEquals("None", browserI.getCapabilities().getCapability(device));
		Assert.assertEquals("WINDOWS", browserI.getCapabilities().getCapability(os));
		Assert.assertEquals("xp", browserI.getCapabilities().getCapability(osVersion));
		Assert.assertEquals("WINDOWS", browserI.getCapabilities().getCapability(platform).toString());
		Assert.assertTrue(browserI.isRemoteFlag());
		Assert.assertEquals("http:testRemoteURL", browserI.host());
		Assert.assertEquals(Integer.valueOf("10"), browserI.getDriverTimeOut());
		/*
		 * browserI.getDriverTimeOut() browserI.getOsName()
		 * browserI.getOsVersion() browserI.getPlatform()
		 */
	}

	@Test
	public void verifySingleXMLMultiBrowser() {
		List<String> xmlName = new ArrayList<String>();
		xmlName.add("BrowseXmlP_multi_1.xml");
		List<IBrowserConf> browserConfList = BrowserXmlParser.getInstance().getBrowserConf1(xmlName);
		Assert.assertEquals(browserConfList.size(), 2);
		IBrowserConf browserConf1 = browserConfList.get(0);
		IBrowserConf browserConf2 = browserConfList.get(1);
		Assert.assertEquals("firefox", browserConf1.getBrowser());
//		Assert.assertEquals("25", browserConf1.getCapabilities().getCapability(browserVersion));
		Assert.assertEquals("testkey", browserConf1.getKey());
		Assert.assertEquals("testusername", browserConf1.getuserName());
		Assert.assertEquals("None", browserConf1.getCapabilities().getCapability(device));
		Assert.assertEquals("WINDOWS", browserConf1.getCapabilities().getCapability(os));
		Assert.assertEquals("xp", browserConf1.getCapabilities().getCapability(osVersion));
		Assert.assertEquals("WINDOWS", browserConf1.getCapabilities().getCapability(platform).toString());
		Assert.assertTrue(browserConf1.isRemoteFlag());
		Assert.assertEquals("http:testRemoteURL", browserConf1.host());
		Assert.assertEquals(Integer.valueOf("10"),
				browserConf1.getDriverTimeOut());

		// Second XML
		Assert.assertEquals("chrome", browserConf2.getBrowser());
		Assert.assertEquals("32", browserConf2.getCapabilities().getCapability(browserVersion));
		Assert.assertEquals("testkey1", browserConf2.getKey());
		Assert.assertEquals("testusername1", browserConf2.getuserName());
		Assert.assertEquals("None", browserConf2.getCapabilities().getCapability(device));
		Assert.assertEquals("LINUX", browserConf2.getCapabilities().getCapability(os));
		Assert.assertEquals("15", browserConf2.getCapabilities().getCapability(osVersion));
		Assert.assertEquals("LINUX", browserConf2.getCapabilities().getCapability(platform).toString());
		Assert.assertFalse(browserConf2.isRemoteFlag());
		// Assert.assertEquals("http:testRemoteURL",
		// browserConf1.getRemoteURL());
		// Assert.assertEquals(Integer.valueOf("10"),
		// browserConf1.getDriverTimeOut());
	}

	@Test
	public void mutipleXml() {

		List<String> xmlName = new ArrayList<String>();
		xmlName.add("BrowseXmlP_multi_1.xml");
		xmlName.add("BrowseXmlP_2.xml");
		List<IBrowserConf> browserConfList = BrowserXmlParser.getInstance().getBrowserConf1(xmlName);
		Assert.assertEquals(browserConfList.size(), 3);
		IBrowserConf browserConf1 = browserConfList.get(0);
		IBrowserConf browserConf2 = browserConfList.get(1);
		IBrowserConf browserConf3 = browserConfList.get(2);
		Assert.assertEquals("firefox", browserConf1.getBrowser());
//		Assert.assertEquals("25", browserConf1.getCapabilities().getCapability(browserVersion));
		Assert.assertEquals("testkey", browserConf1.getKey());
		Assert.assertEquals("testusername", browserConf1.getuserName());
		Assert.assertEquals("None", browserConf1.getCapabilities().getCapability(device));
		Assert.assertEquals("WINDOWS", browserConf1.getCapabilities().getCapability(os));
		Assert.assertEquals("xp", browserConf1.getCapabilities().getCapability(osVersion));
		Assert.assertEquals("WINDOWS", browserConf1.getCapabilities().getCapability(platform).toString());
		Assert.assertTrue(browserConf1.isRemoteFlag());
		Assert.assertEquals("http:testRemoteURL", browserConf1.host());
		Assert.assertEquals(Integer.valueOf("10"),
				browserConf1.getDriverTimeOut());

		// Second XML
		Assert.assertEquals("chrome", browserConf2.getBrowser());
		Assert.assertEquals("32", browserConf2.getCapabilities().getCapability(browserVersion));
		Assert.assertEquals("testkey1", browserConf2.getKey());
		Assert.assertEquals("testusername1", browserConf2.getuserName());
		Assert.assertEquals("None", browserConf2.getCapabilities().getCapability(device));
		Assert.assertEquals("LINUX", browserConf2.getCapabilities().getCapability(os));
		Assert.assertEquals("15", browserConf2.getCapabilities().getCapability(osVersion));
		Assert.assertEquals("LINUX", browserConf2.getCapabilities().getCapability(platform).toString());
		Assert.assertFalse(browserConf2.isRemoteFlag());
		// Assert.assertEquals("http:testRemoteURL",
		// browserConf1.getRemoteURL());
		// Assert.assertEquals(Integer.valueOf("10"),
		// browserConf1.getDriverTimeOut());

		// 3rd XML Browser
		Assert.assertEquals("firefox", browserConf3.getBrowser());
		Assert.assertEquals("25", browserConf3.getCapabilities().getCapability(browserVersion));
		Assert.assertEquals("testkey", browserConf3.getKey());
		Assert.assertEquals("testusername", browserConf3.getuserName());
		Assert.assertEquals("None", browserConf3.getCapabilities().getCapability(device));
		Assert.assertEquals("WINDOWS", browserConf3.getCapabilities().getCapability(os));
		Assert.assertEquals("xp", browserConf3.getCapabilities().getCapability(osVersion));
		Assert.assertEquals("WINDOWS", browserConf3.getCapabilities().getCapability(platform).toString());
		Assert.assertTrue(browserConf3.isRemoteFlag());
		Assert.assertEquals("http:testRemoteURL", browserConf3.host());
		Assert.assertEquals(Integer.valueOf("10"),
				browserConf3.getDriverTimeOut());
	}

}
