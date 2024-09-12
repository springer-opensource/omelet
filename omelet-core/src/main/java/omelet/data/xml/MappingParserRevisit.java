package omelet.data.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import omelet.common.Utils;
import omelet.data.IDataSource;
import omelet.data.IMappingData;
import omelet.data.ImplementIMap;
import omelet.data.PropertyValueMin;
import omelet.driver.SuiteConfiguration;

/**
 * Mapping.xml parser
 * 
 * @author kapil
 * 
 */
public class MappingParserRevisit implements IDataSource {

	private Document document = null;
	private String xmlName;
	private static final String DELIMITTER = ";";
	private static final Logger LOGGER = LogManager.getLogger(MappingParserRevisit.class);
	private HashMap<String, IMappingData> bucket = new HashMap<String, IMappingData>();

	public MappingParserRevisit() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder
					.parse(Utils.getResources(this, getMappingFile()));
		} catch (ParserConfigurationException e) {
			LOGGER.error(e);
		} catch (SAXException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}

	/***
	 * walk to every element of the Xml from the root Element which updates Map
	 * as well for all the values
	 * 
	 * @param element
	 */
	private void walkInXml(Element element) {
		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node n = nodeList.item(i);
			if (n instanceof Element) {
				Element childElement = (Element) n;
				updateBucket(childElement);
				walkInXml(childElement);
			}
		}
	}

	/***
	 * Simple Primary data read from the datasource for obvious reason
	 * {@link IMappingData} will have nulls as well
	 */
	public Map<String, IMappingData> getPrimaryData() {
		// get the root Element
		walkInXml(document.getDocumentElement());
		/*
		 * for (String key : bucket.keySet()) { LOGGER.info("TestClass Name: " +
		 * key); LOGGER.info(" TestData: " + bucket.get(key).getTestData());
		 * LOGGER.info(" ClientEnv: " + bucket.get(key).getClientEnvironment());
		 * LOGGER.info(" Strategy is: " + bucket.get(key).getRunStartegy()); }
		 */
		return bucket;
	}

	/**
	 * update the master bucket with values
	 * 
	 * @param element
	 */
	private void updateBucket(Element element) {
		LOGGER.debug("TestClass Name: " + element.getAttribute("name"));
		bucket.put(element.getAttribute("name"), getImap(element));
	}

	/**
	 * Get the {@link IMappingData} for any Entry in Datasource
	 * 
	 * @param element
	 * @return
	 */
	private IMappingData getImap(Element element) {
		return new ImplementIMap.Builder()
				.withRunStartegy(element.getAttribute("runStrategy"))
				.withTestData(element.getAttribute("testData"))
				.withClientEnvironment(
						getList(element.getAttribute("clientEnvironment")))
				.build();
	}

	/**
	 * Helper method to get List from "," seprated strings
	 * 
	 * @param commaSepratedList
	 * @return
	 */
	private List<String> getList(String commaSepratedList) {

		List<String> returnedList = new ArrayList<String>();
		if (StringUtils.isNotBlank(commaSepratedList)) {
			if (commaSepratedList.contains(DELIMITTER)) {
				String array[] = commaSepratedList.split(";");
				Collections.addAll(returnedList, array);
			} else {
				returnedList.add(commaSepratedList);
			}
		}
		return returnedList;
	}

	public static String getProjectName() {
		if (StringUtils.isNotBlank(getCalcValue("projectName"))) {
			return getCalcValue("projectName");
		} else {
			return SuiteConfiguration.suiteName;
		}
	}

	private static String getCalcValue(String key) {
		if (StringUtils.isNotBlank(System.getProperty(key))) {
			return System.getProperty(key);
		} else {
			return getFrameworkPropertyValue(key);
		}
	}

	public static String getBuildNumber() {
		return getCalcValue("buildNumber");
	}

	public static String getMappingFile() {
		if (StringUtils.isBlank(getCalcValue("mappingfile"))) {
			return "Mapping.xml";
		}
		return getCalcValue("mappingfile");
	}

	private static String getFrameworkPropertyValue(String key) {
		PropertyValueMin prop;
		if (isFrameworkProperties()) {
			prop = new PropertyValueMin(Utils.getResources(
					MappingParserRevisit.class, "Framework.properties"));
			return prop.getValue(key);
		} else {
			return "";
		}
	}

	private static boolean isFrameworkProperties() {
		return Utils.getResources(MappingParserRevisit.class,
								  "Framework.properties") != null;
	}

	@Override
	public String toString() {
		return "Reading the Xml file with name:" + xmlName;
	}
}