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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import omelet.common.Utils;
import omelet.data.IProperty;
import omelet.data.PropertyMapping;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/***
 * For Parssing Test Data xml return List of {@link IProperty}
 * 
 * @author kapilA
 * 
 */
public class XmlApplicationData {

	private DocumentBuilder builder = null;
	private Document document = null;
	private String envType = null;
	private static final Logger LOGGER = Logger
			.getLogger(XmlApplicationData.class);
	private static final Map<String, List<IProperty>> dataBucket = new HashMap<String, List<IProperty>>();
	private static XmlApplicationData instance = null;

	private XmlApplicationData() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e);
		}
	}

	public static XmlApplicationData getInstance() {
		if (null == instance) {
			synchronized (XmlApplicationData.class) {
				if (null == instance) {
					instance = new XmlApplicationData();
				}
			}
		}
		return instance;
	}
	/***
	 * Return List of DataObjects for All Environment <Data>
	 * 
	 * @param environment
	 * @return
	 */
	private List<Element> getDataObjects(String xmlName) {
		try {
			document = builder.parse(new FileInputStream(Utils.getResources(
					this, xmlName)));
		} catch (FileNotFoundException e) {
			LOGGER.error(e);
		} catch (SAXException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		}
		List<Element> dataList = new ArrayList<Element>();
		Element rootElement = document.getDocumentElement();
		NodeList nodes = rootElement.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);

			if (node instanceof Element) {
				Element data = (Element) node;
				if (envType != null) {
					if (data.getAttribute("environment").equals(envType)) {
						dataList.add(data);
					}
				} else {
					dataList.add(data);
				}
			}
		}

		// Check if Data list is empty throw framework exception as there is no
		// mapping for the xml
		if (dataList.isEmpty() && envType != null) {
			LOGGER.info("There is no such environment with name in :" + envType
					+ " in the xml:" + xmlName);
		}
		return dataList;
	}



	/***
	 * Key value pair data to be used in AUT
	 * 
	 * @return
	 */
	public List<IProperty> getAppData(String xmlName) {
		if (dataBucket.containsKey(xmlName)) {
			return dataBucket.get(xmlName);
		} else {
			List<IProperty> dataProperty = new ArrayList<IProperty>();
			HashMap<String, String> keyValue;
			for (Element dataObject : getDataObjects(xmlName)) {
				keyValue = new HashMap<String, String>();
				NodeList pagesOject = dataObject.getElementsByTagName("Pages")
						.item(0).getChildNodes();

				for (int i = 0; i < pagesOject.getLength(); i++) {
					Node pageObject = pagesOject.item(i);
					if (pageObject instanceof Element) {

						Element pageElement = (Element) pageObject;
						String appendText = pageElement.getAttribute("name");
						// GEt all the key value pairs of Page
						NodeList pageData = pageElement.getChildNodes();
						for (int j = 0; j < pageData.getLength(); j++) {
							Node key = pageData.item(j);
							if (key instanceof Element) {
								Element keyElement = (Element) key;
								keyValue.put(
										appendText + "_"
												+ keyElement.getNodeName(),
										keyElement.getTextContent());
							}
						}
					}
				}
				dataProperty.add(new PropertyMapping(keyValue));
			}
			dataBucket.put(xmlName, dataProperty);
			return dataProperty;
		}
	}
	
	public List<IProperty> getAppData(String xmlName,String environment){
		envType = environment;
		return getAppData(xmlName);
	}
}
