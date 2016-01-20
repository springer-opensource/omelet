/*******************************************************************************
 * Copyright 2014 Springer Science+Business Media Deutschland GmbH
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package omelet.data;

import omelet.common.Utils;
import omelet.data.DataProvider.mapStrategy;
import omelet.exception.FrameworkException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Refine data based on the hierarchy method-->Class-->Package
 * take {@link IDataSource} and return
 *
 * @author kapil
 */
public class RefineMappedData {
	private static final Logger LOGGER = Logger.getLogger(RefineMappedData.class);

	private Map<String, IMappingData> primaryDataMap;

	public RefineMappedData(IDataSource dataSource) {
		primaryDataMap = dataSource.getPrimaryData();
		//Primary Map value
		for (String s : primaryDataMap.keySet()) {
			LOGGER.debug("Primary key:" + s + "value:" + primaryDataMap.get(s).getRunStartegy());
		}
	}

	/**
	 * Get refined methodData based on the Hierarchy
	 * if attributes "testData","ClientStrategy","RunStrategy" found in method else in class else in package
	 * else exception
	 *
	 * @param methodName
	 * @return
	 */
	public IMappingData getMethodDataWithClientData(Method methodName) {

		return new ImplementIMap.Builder()
				.withTestData(getRefinedTestData(methodName))
				.withClientEnvironment(getRefinedClientEnvironment(methodName))
				.withRunStartegy(getRunStrategy(methodName).toString()).build();
	}

	public IMappingData getMethodDataWithoutClientData(Method methodName) {

		return new ImplementIMap.Builder()
				.withTestData(getRefinedTestData(methodName))
				.withRunStartegy(getRunStrategy(methodName).toString()).build();
	}

	private String getRefinedTestData(Method method) {
		IMappingData methodVal = primaryDataMap.get(Utils.getFullMethodName(method));
		IMappingData classVal = primaryDataMap.get(method.getDeclaringClass().getName().toString());
		IMappingData packageVal = primaryDataMap.get(method.getDeclaringClass().getPackage().getName().toString());

		if (methodVal != null && StringUtils.isNotBlank(methodVal.getTestData())) {
			return methodVal.getTestData();
		} else if (classVal != null && StringUtils.isNotBlank(classVal.getTestData())) {
			return classVal.getTestData();
		} else if (packageVal != null && StringUtils.isNotBlank(packageVal.getTestData())) {
			return packageVal.getTestData();
		}
		LOGGER.error("There is no Test Data defined for method:" + method.getName()
							 + "in Mapping or the entry for this method/class/package is missing in Mapping");
		throw new FrameworkException("There is no Test Data defined for method:" + method.getName()
											 + "in Mapping or the entry for this method/class/package is missing in "
											 + "Mapping");
	}

	private List<String> getRefinedClientEnvironment(Method method) {
		IMappingData methodClientData = primaryDataMap.get(Utils.getFullMethodName(method));
		IMappingData classClientData = primaryDataMap.get(method.getDeclaringClass().getName().toString());
		IMappingData packageClientData = primaryDataMap.get(
				method.getDeclaringClass().getPackage().getName().toString());

		if (methodClientData != null && !methodClientData.getClientEnvironment().isEmpty()
				&& StringUtils.isNotBlank(methodClientData.getClientEnvironment().get(0))) {
			return methodClientData.getClientEnvironment();
		} else if (classClientData != null && !classClientData.getClientEnvironment().isEmpty()
				&& StringUtils.isNotBlank(classClientData.getClientEnvironment().get(0))) {
			return classClientData.getClientEnvironment();
		} else if (packageClientData != null && !packageClientData.getClientEnvironment().isEmpty()
				&& StringUtils.isNotBlank(packageClientData.getClientEnvironment().get(0))) {
			return packageClientData.getClientEnvironment();
		}
		LOGGER.error("There is no ClientEnvironment/Browser defined for method:" + method.getName() +
							 "in Mapping or the entry for this method/class/package is missing in Mapping");
		throw new FrameworkException(
				"There is no ClientEnvironment/Browser defined for method:" + method.getName() +
						"in Mapping or the entry for this method/class/package is missing in Mapping");
	}

	private mapStrategy getRunStrategy(Method method) {
		IMappingData methodRunStartegy = primaryDataMap.get(Utils.getFullMethodName(method));
		IMappingData classRunStartegy = primaryDataMap.get(method.getDeclaringClass().getName().toString());
		IMappingData packageRunStartegy = primaryDataMap.get(
				method.getDeclaringClass().getPackage().getName().toString());

		if (methodRunStartegy != null && methodRunStartegy.getRunStartegy() != null) {
			return methodRunStartegy.getRunStartegy();
		} else if (classRunStartegy != null && classRunStartegy.getRunStartegy() != null) {
			return classRunStartegy.getRunStartegy();
		} else if (packageRunStartegy != null && packageRunStartegy.getRunStartegy() != null) {
			return packageRunStartegy.getRunStartegy();
		}
		return mapStrategy.Optimal;
	}
}
