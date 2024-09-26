package omelet.data;

import java.util.Map;

/***
 * Data Sources like xml , Excel , GoogleSheet should implement this interface
 * @author kapil
 *
 */
public interface IDataSource {
	/**
	 * This is intial read of Mapping data
	 * @return
	 */
	Map<String, IMappingData> getPrimaryData();
}
