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
 * ���ݿ���Ĳ�����, ��������ԭ��̬��jdbc����ʽ������ݲ�����
 * 
 * @author Qin-Wei
 * @since 2014-3-13
 */
public class Session {

	private static final Logger log = Logger.getLogger(Session.class);

	private DBManager dbManager;

	private Connection g_connection = null;

	private boolean isInTransaction = false;

	public Session() {
		dbManager = new DBManager();
	}

	public Session(Connection conn) {
		dbManager = new DBManager();
		g_connection = conn;
	}

	/**
	 * ��õ�ǰ�߳��еİ󶨵�sql connection.
	 * 
	 * @return Connection ����
	 */
	public Connection getConnection() {
		return g_connection != null ? g_connection : dbManager.getConnection();
	}

	/**
	 * ����һ������,��Ὣ���е���������autoCommit=false,�����Ĳ���ֱ������{@link #endTransaction} ʱ �Ż����
	 * �����commit����rollback, ���ر�����. ���,һ������������,������ʽ��������,����
	 * ����������й©.
	 */
	public void beginTransaction() {
		try {
			if (g_connection == null || g_connection.isClosed()) {
				g_connection = dbManager.getConnection();
			}
			g_connection.setAutoCommit(false);
			isInTransaction = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���{@link #beginTransaction()}����ʹ��,�ύ���񲢹ر�����
	 */
	public void endTransaction() {
		dbManager.commitAndClose(g_connection);
		isInTransaction = true;
	}

	/**
	 * ����ѯ���ӳ�䵽ʵ��Map��.
	 * 
	 * @param sql
	 *            ִ�в�ѯ��sql���
	 * @return ���ִ�м�¼�����н��,�����ص�һ�������ע�뵽Map��,����޼�¼�򷵻�null.
	 */
	public Map<String, Object> getMap(String sql) {
		QueryRunner run = new QueryRunner();
		MapHandler handler = new MapHandler();
		try {
			Map<String, Object> map = run.query(getConnection(), sql, handler);
			log.debug("query sql=" + sql + ";mapsize=" + map.size());
			return map;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (g_connection == null && !isInTransaction) {
				dbManager.close();
			}
		}
		return null;
	}

	/**
	 * ����ѯ���ӳ�䵽ʵ��bean�е�����.
	 * 
	 * @param clz
	 *            bean��
	 * @param sql
	 *            ִ�в�ѯ��sql���
	 * @return ���ִ�м�¼�����н��,�����ص�һ�������ע�뵽bean��,����޼�¼�򷵻�null.
	 */
	public <T> T getBean(Class<T> clz, String sql) {

		QueryRunner run = new QueryRunner();
		ResultSetHandler<T> handler = new BeanHandler<T>(clz);
		try {
			T bean = run.query(getConnection(), sql, handler);
			log.debug("Query SQL:" + sql + ";bean=" + bean);
			return bean;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (g_connection == null && !isInTransaction) {
				dbManager.close();
			}
		}
		return null;
	}

	/**
	 * ����ѯ���ӳ�䵽ʵ��bean�е�����.
	 * 
	 * @param clz
	 *            bean��
	 * @param sql
	 *            ִ�в�ѯ��sql���
	 * @param params
	 *            sql����
	 * @return ���ִ�м�¼�����н��,�����ص�һ�������ע�뵽bean��,����޼�¼�򷵻�null.
	 */
	public <T> T getBean(Class<T> clz, String sql, Object[] params) {
		QueryRunner run = new QueryRunner();
		ResultSetHandler<T> handler = new BeanHandler<T>(clz);
		try {
			T bean = run.query(getConnection(), sql, handler, params);
			log.debug("Query SQL=" + sql + ";params=" + java.util.Arrays.toString(params) + ";bean=" + bean);
			return bean;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (g_connection == null && !isInTransaction) {
				dbManager.close();
			}
		}
		return null;
	}

	/**
	 * ����ѯ���ע�뵽ʵ��bean�еĶ�Ӧ����,�����б����ʽ��Ž����.
	 * 
	 * @param clz
	 *            bean��
	 * @param sql
	 *            ִ�в�ѯ��sql���
	 * @return ���ִ�м�¼�����н��,�����ؽ����ע�뵽bean��,����޼�¼�򷵻�null.
	 */
	public <T> List<T> getBeanList(Class<T> clz, String sql) {
		QueryRunner run = new QueryRunner();
		ResultSetHandler<List<T>> handler = new BeanListHandler<T>(clz);
		try {
			List<T> list = run.query(getConnection(), sql, handler);
			log.debug("Query SQL=" + sql + ";listsize=" + list.size());
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (g_connection == null && !isInTransaction) {
				dbManager.close();
			}
		}
		return null;
	}

	/**
	 * ����ѯ���ע�뵽ʵ��Map�еĶ�Ӧ����,�����б����ʽ��Ž����.
	 * 
	 * @param sql
	 *            ִ�в�ѯ��sql���
	 * @return ���ִ�м�¼�����н��,�����ؽ����ע�뵽Map��,����޼�¼�򷵻�null.
	 */
	public List<Map<String, Object>> getMapList(String sql) {
		QueryRunner run = new QueryRunner();
		MapListHandler handler = new MapListHandler();
		try {
			List<Map<String, Object>> list = run.query(getConnection(), sql, handler);
			log.debug("Query SQL=" + sql + ";listsize=" + list.size());
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (g_connection == null && !isInTransaction) {
				dbManager.close();
			}
		}
		return null;
	}

	/**
	 * ִ�и��²���. �˷�����ط���doWithinTransaction�����н���ʵʩ,�Դﵽ������Ĵ���.
	 * 
	 * @param bean
	 *            ���������µ�ʵ��bean.
	 * @param key
	 *            bean�е�����,���Բ����������������ݱ�����,����Ϊһ����������.
	 * @return ���ظ��µļ�¼����,�п��ܸ�����key���ڶ���ƥ��.
	 * @throws SQLException
	 *             �����ִ������SQL�쳣����,���׳�.
	 */
	public int update(Object bean, String key) throws SQLException {
		String tablename = bean.getClass().getSimpleName().toLowerCase();
		StringBuilder update = new StringBuilder("update " + tablename + " set ");
		Map<String, Object> props = mapBeanProperties(bean);
		ArrayList<Object> values = new ArrayList<Object>();
		Object keyValue = null;
		for (Map.Entry<String, Object> e : props.entrySet()) {
			if (!e.getKey().equalsIgnoreCase(key)) {
				update.append(e.getKey() + "=?,");
				values.add(e.getValue());
			} else {
				keyValue = e.getValue();
			}
		}
		if (keyValue == null) {
			throw new SQLException("Can not update, key value must be set.");
		}
		values.add(keyValue);
		int index = update.lastIndexOf(",");
		update.replace(index, update.length(), " ");
		update.append("where " + key + "=?");
		return update(update.toString(), values.toArray());
	}

	/**
	 * ִ�и��²���,������bean���������ע��<code>@TableKey</code>.
	 * �˷�����ط���doWithinTransaction�����н���ʵʩ,�Դﵽ������Ĵ���.
	 * 
	 * @param bean
	 *            ���������µ�ʵ��bean.
	 * @return ���ظ��µļ�¼����,�п��ܸ�����key���ڶ���ƥ��.
	 * @throws SQLException
	 *             �����ִ������SQL�쳣����,���׳�.
	 */
	public int update(Object bean) throws SQLException {
		return update(bean, getKeyPropertyName(bean));
	}

	/**
	 * ִ�в������. �˷�����ط���doWithinTransaction�����н���ʵʩ,�Դﵽ������Ĵ���.
	 * 
	 * @param bean
	 *            ��������ʵ��bean.
	 * @param aikey
	 *            ���ݱ��е�Ψһһ��������������
	 * @return �����Ƿ�ɹ�.
	 * @throws SQLException
	 *             �����ִ������SQL�쳣����,���׳�.
	 */
	public boolean insert(Object bean, String aikey) throws SQLException {
		String tablename = bean.getClass().getSimpleName();
		StringBuilder sqlk = new StringBuilder("insert into " + tablename + " ( ");
		StringBuilder sqlv = new StringBuilder("values( ");
		Map<String, Object> props = mapBeanProperties(bean);
		ArrayList<Object> values = new ArrayList<Object>();
		for (Map.Entry<String, Object> e : props.entrySet()) {
			if (!e.getKey().equalsIgnoreCase(aikey)) {
				sqlk.append(e.getKey() + ",");
				sqlv.append("?,");
				values.add(e.getValue());
			}
		}
		int indexk = sqlk.lastIndexOf(",");
		int indexv = sqlv.lastIndexOf(",");
		if (indexk < 0 || indexv < 0) {
			throw new RuntimeException("Can not insert, value must be set.");
		}
		sqlk.replace(indexk, sqlk.length(), ")");
		sqlv.replace(indexv, sqlv.length(), ")");
		sqlk.append(sqlv);
		return update(sqlk.toString(), values.toArray()) > 0;
	}

	/**
	 * ִ�в������. ������bean���������ע��<code>@TableKey</code>.
	 * �˷�����ط���doWithinTransaction�����н���ʵʩ,�Դﵽ������Ĵ���.
	 * 
	 * @param bean
	 *            ��������ʵ��bean.
	 * @return �����Ƿ�ɹ�.
	 * @throws SQLException
	 *             �����ִ������SQL�쳣����,���׳�.
	 */
	public boolean insert(Object bean) throws SQLException {
		return insert(bean, getKeyPropertyName(bean));
	}

	/**
	 * ʹ��?ռλ��ִ�и��²���,������update, insert, delete.
	 * �˷�����ط���doWithinTransaction�����н���ʵʩ,�Դﵽ������Ĵ���.
	 * 
	 * @param sql
	 *            Ҫִ�е�ԭ��̬SQL���.
	 * @param params
	 *            ��������
	 * @return Ӱ��ļ�¼��
	 * @throws SQLException
	 *             �����ִ������SQL�쳣����,���׳�.
	 */
	public int update(String sql, Object[] params) throws SQLException {
		int res = 0;
		Connection conn = getConnection();
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			fillStatement(pstmt, params);
			res = pstmt.executeUpdate();
			if (pstmt != null) {
				pstmt.close();
			}
			log.debug("Update:" + res + "; SQL=" + sql + ";params=" + java.util.Arrays.toString(params) + ";");
		} catch (SQLException e) {
			throw e;
		} finally {
			if (g_connection == null && !isInTransaction) {
				dbManager.close();
			}
		}
		return res;
	}

	/**
	 * ʹ�ò�������ע��PreaparedStatement,�������������滻����е�?��λ��.
	 * 
	 * @param pstmt
	 *            PreaparedStatement����
	 * @param params
	 *            ��������
	 * @throws SQLException
	 *             �����ִ������SQL�쳣����,���׳�.
	 */
	private static void fillStatement(PreparedStatement pstmt, Object[] params) throws SQLException {
		ParameterMetaData pmd = null;
		pmd = pstmt.getParameterMetaData();
		int stmtCount = pmd.getParameterCount();
		int paramsCount = params == null ? 0 : params.length;

		if (stmtCount != paramsCount) {
			throw new SQLException("Wrong number of parameters: expected " + stmtCount + ", was given " + paramsCount);
		}
		if (params == null) {
			return;
		}
		for (int i = 0; i < params.length; i++) {
			if (params[i] != null) {
				pstmt.setObject(i + 1, params[i]);
			} else {
				int sqlType = 12;
				try {
					sqlType = pmd.getParameterType(i + 1);
				} catch (SQLException e) {
					log.error(e.getMessage());
				}
				pstmt.setNull(i + 1, sqlType);
			}
		}
	}

	/**
	 * ��ʵ������ӳ�䵽map,�������java bean�淶,ӵ��get��ȡ������
	 * 
	 * @param beanʵ��
	 * @return map���͵�ӳ��
	 */
	private static Map<String, Object> mapBeanProperties(Object bean) {
		HashMap<String, Object> setterMap = new HashMap<String, Object>();
		try {
			BeanInfo info = Introspector.getBeanInfo(bean.getClass());
			PropertyDescriptor[] descritors = info.getPropertyDescriptors();
			int size = descritors.length;
			for (int index = 0; index < size; index++) {
				if (descritors[index].getName().equalsIgnoreCase("class")) {
					continue;
				}
				String propertyName = descritors[index].getName();
				Method method = descritors[index].getReadMethod();
				if (method != null) {
					Object value = method.invoke(bean, new Object[] {});
					if (value == null) {
						continue;
					}
					setterMap.put(propertyName, value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			setterMap = null;
		}
		return setterMap;
	}

	/**
	 * ͨ��ע��<code>@TableKey</code>�õ����ݱ�������ֶ���.<link>aa</link>
	 * 
	 * @param bean
	 *            ʵ��
	 * @return �ִ���ʽ�������ֶ���
	 */
	public static String getKeyPropertyName(Object bean) {
		String res = null;
		try {
			Field[] fields = bean.getClass().getDeclaredFields();
			for (Field field : fields) {
				TableKey annotation = field.getAnnotation(TableKey.class);
				if (annotation != null) {
					PropertyDescriptor properDescriptor = new PropertyDescriptor(field.getName(), bean.getClass());
					Method getter = properDescriptor.getReadMethod();
					if (getter != null) {
						res = annotation.columnName().equals("") ? field.getName() : annotation.columnName();
						return res;
					}
				}
			}
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		if (res == null || res.length() < 1) {
			throw new RuntimeException("Key must be set in the entity class.");
		}
		return res;
	}
}
