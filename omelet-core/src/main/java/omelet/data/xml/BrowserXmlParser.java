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
package omelet.data.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import omelet.common.Utils;
import omelet.data.IMappingData;
import omelet.data.driverconf.IBrowserConf;
import omelet.data.driverconf.PrepareDriverConf;

/***
 * Return list of {@link IBrowserConf} given name of Xml file
 * 
 * @author kapilA
 * 
 */

public class BrowserXmlParser {
	private DocumentBuilder builder = null;
	private Document document = null;
	private static final Logger LOGGER = LogManager.getLogger(BrowserXmlParser.class);
	private static final Map<String,List<IBrowserConf>> xmlBrowserMap = new HashMap<String, List<IBrowserConf>>();
	private static BrowserXmlParser instance = null;

	private BrowserXmlParser() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			LOGGER.error(e);
		}
	}
	
	public static BrowserXmlParser getInstance(){
		if(instance == null){
			synchronized (BrowserXmlParser.class) {
				if(instance == null){
					instance = new BrowserXmlParser();
				}
			}
		}
		return instance;
	}
	
	

	private List<HashMap<String, String>> readXml(String xmlN) {

		List<HashMap<String, String>> singleXMlList = new ArrayList<HashMap<String, String>>();
		try {
			document = builder.parse(Utils.getResources(this, xmlN));
		} catch (SAXException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		}

		Element rootElement = document.getDocumentElement();
		NodeList nodes = rootElement.getChildNodes();

		// How many Client environments
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node instanceof Element) {
				Element instanceBConf = (Element) node;
				singleXMlList.add(getKeyValue(instanceBConf));
			}

		}
		return singleXMlList;
	}

	/**
	 * Return list of {@link IBrowserConf} prepared from the list of the name of the xml recieved
	 * @return
	 */
	/*public List<IBrowserConf> getBrowserConf() {

		List<HashMap<String, String>> totalList = new ArrayList<HashMap<String, String>>();

		// get the browser xml names compare them in HashMap
		for (String browserXml : browserXmls) {
			if (singleXmlBrowserMap.containsKey(browserXml)) {
				totalList.addAll(singleXmlBrowserMap.get(browserXml));
			} else {
				totalList.addAll(readXml(browserXml));
			}
		}
		// if present then combine them into Single List and return
		List<IBrowserConf> ibrowserList = new ArrayList<IBrowserConf>();
		// Check if for xml there is Already a mapping in the
		// Iterate over the complete list
		for (HashMap<String, String> b_data : totalList) {
			ibrowserList.add(
					new PrepareDriverConf(b_data).refineBrowserValues().checkForRules().get());
		}
		return ibrowserList;
	}*/
	
	public List<IBrowserConf> getBrowserConf1(List<String> xmlList){
		List<IBrowserConf> returnList = new ArrayList<IBrowserConf>();
		for(String xml:xmlList){
			if(xmlBrowserMap.containsKey(xml)){
				returnList.addAll(xmlBrowserMap.get(xml));
			}else{
				//update master list for future accessing
				//read all the values one by prepare list
				List<IBrowserConf> singleXmlBrowser = getBrowserForSingleXml(xml);
				xmlBrowserMap.put(xml, singleXmlBrowser);
				returnList.addAll(singleXmlBrowser);
			}
		}
		return returnList;
	}
	
	private List<IBrowserConf> getBrowserForSingleXml(String xml){
		List<IBrowserConf> browserL = new ArrayList<IBrowserConf>();
		for(Map<String,String> keyValue:readXml(xml)){
			browserL.add(new PrepareDriverConf(keyValue).refineBrowserValues().checkForRules().get());
		}
		return browserL;
	}
	
	
	
	public List<IBrowserConf> getBrowserConf1(IMappingData methodXmls){
		return getBrowserConf1(methodXmls.getClientEnvironment());
	}

	/**
	 * Return key values pair for the the tag ClientEnvironment of the Browser Xml
	 * @param keyElement
	 * @return
	 */
	private HashMap<String, String> getKeyValue(Element keyElement) {

		HashMap<String, String> browserData = new HashMap<String, String>();
		NamedNodeMap browserL = keyElement.getAttributes();
		for(int i=0;i<browserL.getLength();i++){
			Node attr = browserL.item(i);
			browserData.put(attr.getNodeName(), attr.getNodeValue());
		}
		return browserData;
		
		
		
	}

}
