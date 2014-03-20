/**
 * 
 */
package wei.db.common;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.log4j.Logger;

import wei.db.annotation.TableKey;


/**
 * 数据库存操作模板类,提供便捷的数据库操作方法.
 * @author wei
 * @since 2014-3-13
 */
public class DbTemplate {

	private static final Logger log=Logger.getLogger(DbTemplate.class);
	
	private DbManager dbManager;
	
	public DbTemplate(){
		dbManager=new DbManager();
	}
	
	private Connection getConnection(){
		return dbManager.getConnection();
	}
	/**
	 * 将查询结果映射到实体Map中.
	 * 
	 * @param sql
	 *            执行查询的sql语句
	 * @return 如果执行记录集中有结果,将返回第一个结果并注入到Map中,如果无记录则返回null.
	 */
	public Map<String,Object> getMap(String sql){
		QueryRunner run = new QueryRunner();
		MapHandler handler=new MapHandler();
		try {
			Map<String,Object> map=run.query(getConnection(),sql,handler);
			log.debug("query sql="+sql+";mapsize="+map.size());
			return map;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 将查询结果映射到实体bean中的属性.
	 * 
	 * @param clz
	 *            bean类
	 * @param sql
	 *            执行查询的sql语句
	 * @return 如果执行记录集中有结果,将返回第一个结果并注入到bean中,如果无记录则返回null.
	 */
	public <T>T getBean(Class<T> clz,String sql){
		
		QueryRunner run = new QueryRunner();
		ResultSetHandler<T> handler = new BeanHandler<T>(clz);
		try {
			T bean=run.query(getConnection(),sql,handler);
			log.debug("Query SQL:"+sql+";bean="+bean);
			return bean;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 将查询结果映射到实体bean中的属性.
	 * 
	 * @param clz
	 *            bean类
	 * @param sql
	 *            执行查询的sql语句
	 * @param params
	 *            sql参数
	 * @return 如果执行记录集中有结果,将返回第一个结果并注入到bean中,如果无记录则返回null.
	 */
	public <T>T getBean(Class<T> clz,String sql,Object[]params){
		QueryRunner run = new QueryRunner();
		ResultSetHandler<T> handler = new BeanHandler<T>(clz);
		try {
			T bean=run.query(getConnection(),sql,handler,params);
			log.debug("Query SQL="+sql+";params="+arrayToString(params)+";bean="+bean);
			return bean; 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;		
	}
	
	/**
	 * 将查询结果注入到实体bean中的对应属性,并以列表的形式存放结果集.
	 * 
	 * @param clz
	 *            bean类
	 * @param sql
	 *            执行查询的sql语句
	 * @return 如果执行记录集中有结果,将返回结果并注入到bean中,如果无记录则返回null.
	 */
	public <T>List<T> getBeanList(Class<T> clz,String sql){
		QueryRunner run = new QueryRunner();
		ResultSetHandler<List<T>> handler = new BeanListHandler<T>(clz);
		try {
			List<T> list=run.query(getConnection(),sql,handler);
			log.debug("Query SQL="+sql+";listsize="+list.size());
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 将查询结果注入到实体Map中的对应属性,并以列表的形式存放结果集.
	 * 
	 * @param sql
	 *            执行查询的sql语句
	 * @return 如果执行记录集中有结果,将返回结果并注入到Map中,如果无记录则返回null.
	 */
	public List<Map<String,Object>> getMapList(String sql){
		QueryRunner run = new QueryRunner();
		MapListHandler handler=new MapListHandler();
		try {
			List<Map<String,Object>> list=run.query(getConnection(),sql,handler);
			log.debug("Query SQL="+sql+";listsize="+list.size());
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 执行更新操作. 此方法务必放在doWithinTransaction方法中进行实施,以达到对事务的处理.
	 * 
	 * @param bean
	 *            即将被更新的实体bean.
	 * @param key
	 *            bean中的主键,可以不是真正的物理数据表主键,仅作为一个更新条件.
	 * @return 返回更新的记录行数,有可能给出的key存在多条匹配.
	 * @throws SQLException
	 *             如果在执行中有SQL异常发生,则抛出.
	 */
	public int update(Object bean, String key) throws SQLException {
		String tablename = bean.getClass().getSimpleName().toLowerCase();
		StringBuilder update = new StringBuilder("update " + tablename	+ " set ");
		Map<String, Object> props = mapBeanProperties(bean);
		ArrayList<Object> values = new ArrayList<Object>();
		Object keyValue=null;
		for (Map.Entry<String, Object> e : props.entrySet()) {
			if(!e.getKey().equalsIgnoreCase(key)){
				update.append(e.getKey() + "=?,");
				values.add(e.getValue());
			}else{
				keyValue=e.getValue();
			}
		}
		if(keyValue==null){
			throw new SQLException("Can not update, key value must be set.");
		}
		values.add(keyValue);
		int index = update.lastIndexOf(",");
		update.replace(index, update.length(), " ");
		update.append("where " + key + "=?");
		return update(update.toString(),values.toArray());
	}
	
	/**
	 * 执行更新操作,必须在bean中添加主键注解<code>@TableKey</code>.
	 * 此方法务必放在doWithinTransaction方法中进行实施,以达到对事务的处理.
	 * 
	 * @param bean
	 *            即将被更新的实体bean.
	 * @return 返回更新的记录行数,有可能给出的key存在多条匹配.
	 * @throws SQLException
	 *             如果在执行中有SQL异常发生,则抛出.
	 */
	public int update(Object bean) throws SQLException {
		return update(bean,getKeyPropertyName(bean));
	}
	
	/**
	 * 执行插入操作. 此方法务必放在doWithinTransaction方法中进行实施,以达到对事务的处理.
	 * 
	 * @param bean
	 *            被操作的实体bean.
	 * @param aikey
	 *            数据表中的唯一一个自增长键名称
	 * @return 操作是否成功.
	 * @throws SQLException
	 *             如果在执行中有SQL异常发生,则抛出.
	 */
	public boolean insert(Object bean,String aikey) throws SQLException{
		String tablename = bean.getClass().getSimpleName();
		StringBuilder sqlk = new StringBuilder("insert into " + tablename	+ " ( ");
		StringBuilder sqlv = new StringBuilder("values( ");
		Map<String, Object> props = mapBeanProperties(bean);
		ArrayList<Object> values = new ArrayList<Object>();
		for (Map.Entry<String, Object> e : props.entrySet()) {
			if(!e.getKey().equalsIgnoreCase(aikey)){
				sqlk.append(e.getKey() +",");
				sqlv.append("?,");
				values.add(e.getValue());
			}
		}
		int indexk=sqlk.lastIndexOf(",");
		int indexv=sqlv.lastIndexOf(",");
		if(indexk<0||indexv<0){
			throw new RuntimeException("Can not insert, value must be set.");
		}
		sqlk.replace(indexk, sqlk.length(), ")");
		sqlv.replace(indexv, sqlv.length(), ")");
		sqlk.append(sqlv);
		return update(sqlk.toString(),values.toArray())>0;
	}
	
	/**
	 * 执行插入操作. 必须在bean中添加主键注解<code>@TableKey</code>.
	 * 此方法务必放在doWithinTransaction方法中进行实施,以达到对事务的处理.
	 * 
	 * @param bean
	 *            被操作的实体bean.
	 * @return 操作是否成功.
	 * @throws SQLException
	 *             如果在执行中有SQL异常发生,则抛出.
	 */
	public boolean insert(Object bean) throws SQLException{
		return insert(bean,getKeyPropertyName(bean));
	}
	
	/**
	 * 使用?占位符执行更新操作,可以是update, insert, delete.
	 * 此方法务必放在doWithinTransaction方法中进行实施,以达到对事务的处理.
	 * 
	 * @param sql
	 *            要执行的原生态SQL语句.
	 * @param params
	 *            参数数组
	 * @return 影响的记录数
	 * @throws SQLException
	 * 			如果在执行中有SQL异常发生,则抛出.
	 */
	public int update(String sql,Object[]params)throws SQLException {
		int res=0;
		Connection conn=getConnection();
		PreparedStatement pstmt=conn.prepareStatement(sql);
		fillStatement(pstmt, params);
		res=pstmt.executeUpdate();
		if(pstmt!=null){
			pstmt.close();
		}
		log.debug("Update:"+res+"; SQL="+sql+";params="+arrayToString(params)+";");
		return res;
	}
	
	
	/**
	 * 使用参数数组注入PreaparedStatement,即将参数数组替换语句中的?点位符.
	 * 
	 * @param pstmt
	 *            PreaparedStatement对象
	 * @param params
	 *            参数数组
	 * @throws SQLException
	 *             如果在执行中有SQL异常发生,则抛出.
	 */
	private static void fillStatement(PreparedStatement pstmt, Object[] params)	throws SQLException {
		ParameterMetaData pmd = null;
		pmd = pstmt.getParameterMetaData();
		int stmtCount = pmd.getParameterCount();
		int paramsCount = params == null ? 0 : params.length;

		if (stmtCount != paramsCount) {
			throw new SQLException("Wrong number of parameters: expected "	+ stmtCount + ", was given " + paramsCount);
		}
		if (params == null) {
			return;
		}
		for (int i = 0; i < params.length; i++){
			if (params[i] != null) {
				pstmt.setObject(i + 1, params[i]);
			} else {
				int sqlType = 12;
				try {
					sqlType = pmd.getParameterType(i + 1);
				} catch (SQLException e) {
					
				}
				pstmt.setNull(i + 1, sqlType);
			}
		}
	}

	
	/**
	 * 事务型处理. 使用新构造的TransactionExecutor进行事务封装, 在事务内可进行update, insert, delete 等操作,
	 * 而不必担心事务的繁琐处理, 这都将会自动完成.
	 * @param executor 事务操作执行器.
	 */
	public void doWithinTransaction(TransactionExecutor executor){
		Connection conn=getConnection();
		if(conn!=null){
			try {
				conn.setAutoCommit(false);
				executor.execute();
				conn.commit();
			} catch (SQLException e) {
				e.printStackTrace();
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}finally{
				dbManager.close(conn);
			}
		}
	}
	
	/**
	 * 将实体属性映射到map,必须符合java bean规范,拥有get读取方法．
	 * @param bean实体
	 * @return map类型的映射
	 */
	private Map<String,Object> mapBeanProperties(Object bean){
		HashMap<String,Object> setterMap=new HashMap<String,Object>();
		try {
			BeanInfo info = Introspector.getBeanInfo(bean.getClass());
			PropertyDescriptor[] descritors = info.getPropertyDescriptors();
			int size = descritors.length;
			for (int index = 0; index < size; index++) {
				if(descritors[index].getName().equalsIgnoreCase("class")){
					continue;
				}
				String propertyName = descritors[index].getName();
				Method method = descritors[index].getReadMethod();
				if (method != null) {					
					Object value = method.invoke(bean, new Object[] {});
					if (value == null) {
						continue;
					}
					setterMap.put(propertyName,value);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			setterMap=null;
		}
		return setterMap;
	}
	
	/**
	 * 通过注解<code>@TableKey</code>得到数据表的主键字段名.<link>aa</link>
	 * @param bean 实体
	 * @return 字串格式的主键字段名
	 */
	public String getKeyPropertyName(Object bean) {
		String res=null;
		try {
			Field[] fields = bean.getClass().getDeclaredFields();
			for (Field field : fields) {
				TableKey annotation = field.getAnnotation(TableKey.class);
				if (annotation != null) {
					PropertyDescriptor properDescriptor = new PropertyDescriptor(field.getName(),bean.getClass());
					Method getter = properDescriptor.getReadMethod();
					if (getter != null) {
						res=annotation.columnName().equals("")?field.getName():annotation.columnName();
						return res;
					}
				}
			}
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		if(res==null||res.length()<1){
			throw new RuntimeException("Key must be set in the entity class.");
		}
		return res;
	}
	
	public static boolean isCompatibleType(Object value, Class<?> type) {
		if ((value == null) || (type.isInstance(value))) {
			return true;
		}
		if ((type.equals(Integer.TYPE)) && (Integer.class.isInstance(value))) {
			return true;
		}
		if ((type.equals(Long.TYPE)) && (Long.class.isInstance(value))) {
			return true;
		}
		if ((type.equals(Double.TYPE)) && (Double.class.isInstance(value))) {
			return true;
		}
		if ((type.equals(Float.TYPE)) && (Float.class.isInstance(value))) {
			return true;
		}
		if ((type.equals(Short.TYPE)) && (Short.class.isInstance(value))) {
			return true;
		}
		if ((type.equals(Byte.TYPE)) && (Byte.class.isInstance(value))) {
			return true;
		}
		if ((type.equals(Character.TYPE))
				&& (Character.class.isInstance(value))) {
			return true;
		}
		if ((type.equals(Boolean.TYPE)) && (Boolean.class.isInstance(value))) {
			return true;
		}
		return false;
	}
	
	private String arrayToString(Object[] array) {
		if (array == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder("[");
		for (Object obj : array) {
			if(String.class.isInstance(obj)){
				sb.append('"').append(obj).append('"');
			}else{
				sb.append(obj.toString());
			}
			sb.append(",");
		}
		int index = sb.lastIndexOf(",");
		sb.replace(index, sb.length(), "");
		sb.append("]");
		return sb.toString();
	}
}
