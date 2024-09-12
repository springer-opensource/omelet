package omelet.testng.support;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import omelet.data.IMethodContext;

public class MethodContextCollection {

	private static final Logger Log = LogManager.getLogger(MethodContextCollection.class);

	public static IMethodContext getMethodContext(String methodName) {

		Log.debug("Returing data from MethodContextCollection "
				+ RetryIAnnotationTransformer.methodContextHolder.get(methodName));

		return RetryIAnnotationTransformer.methodContextHolder.get(methodName);
	}

}
