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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IAnnotationTransformer;
import org.testng.IAnnotationTransformer2;
import org.testng.annotations.IConfigurationAnnotation;
import org.testng.annotations.IDataProviderAnnotation;
import org.testng.annotations.IFactoryAnnotation;
import org.testng.annotations.ITestAnnotation;

import omelet.common.Utils;
import omelet.data.IProperty;
import omelet.data.MethodContext;
import omelet.data.driverconf.IBrowserConf;

/***
 * For Appending Retry Annotation on All Test Cases and creating Map of classes
 * which are having @AfterMethod and @BeforeMethod
 * 
 * @author kapilA
 * 
 */
public class RetryIAnnotationTransformer implements IAnnotationTransformer, IAnnotationTransformer2 {
	private static final Logger Log = LogManager.getLogger(RetryIAnnotationTransformer.class);
	protected static final Map<String, MethodContext> methodContextHolder = new HashMap<String, MethodContext>();
	/*
	 * PrettyMessage prettyMessage = new PrettyMessage(); Thread t = new
	 * Thread(prettyMessage);
	 */

	@SuppressWarnings("rawtypes")
	public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
		Log.debug("value of passed parameter for method transform is: " + "ITestAnnotation: " + annotation + " Class: "
				+ testClass + " Constructor: " + testConstructor + " Method: " + testMethod);

		if (testMethod != null && !isPartOfFactoryTest(testMethod)) {
			MethodContext context = new MethodContext(testMethod);
			context.setRetryAnalyser(annotation);
			context.setDataProvider(annotation, testMethod);
			context.prepareData();
			// update methodContextCollection
			methodContextHolder.put(Utils.getFullMethodName(testMethod), context);
			
			Log.debug("Value in method context holder is: " + methodContextHolder);
		}

	}

	private boolean isPartOfFactoryTest(Method testMethod) {
		return !(testMethod.getGenericParameterTypes().length == 2
				&& testMethod.getGenericParameterTypes()[0].equals(IBrowserConf.class)
				&& testMethod.getGenericParameterTypes()[1].equals(IProperty.class));
	}

	@SuppressWarnings("rawtypes")
	public void transform(IConfigurationAnnotation annotation, Class testClass, Constructor testConstructor,
			Method testMethod) {
		// System.out.println(testConstructor.getName());

	}

	public void transform(IDataProviderAnnotation annotation, Method method) {
		// No need to add any implementation as we are overriding testng interface
	}

	/**
	 * If we are using test factory for single execution of class then this will be
	 * called
	 */
	public void transform(IFactoryAnnotation annotation, Method testMethod) {
		if (testMethod != null) {
			MethodContext context = new MethodContext(testMethod);
			context.setDataProvider(annotation, testMethod);
			context.prepareData();
			// update methodContextCollection
			methodContextHolder.put(Utils.getFullMethodName(testMethod), context);
		}

	}

}