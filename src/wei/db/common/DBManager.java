/**
 * 
 */
package wei.db.common;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * @author wei
 * 
 */
public class DBManager {

	private static final Logger log = Logger.getLogger(DBManager.class);
	/** mysql����DB, ֵΪ{@value} **/
	public static final int DB_TYPE_MYSQL = 0x1;

	/** oracle����DB, ֵΪ{@value} **/
	public static final int DB_TYPE_ORACLE = 0x2;

	private static DataSource dataSource;

	/** ���ݿ�����, ������{@link #DB_TYPE_MYSQL}��{@link #DB_TYPE_ORACLE} **/
	public static int dbType;

	private volatile static boolean isInit = false;

	/** ������Connection�󶨵���ǰ�߳��ϵı��� **/
	private ThreadLocal<Connection> threadSession = new ThreadLocal<Connection>();

	static {
		try {
			initConnPool();
		} catch (Exception e) {
			log.error("Can not init connection pool," + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	private synchronized static void initConnPool() {
		ComboPooledDataSource cpds = new ComboPooledDataSource("weidb");
		dataSource = cpds;
		isInit = true;
		String dbClass = cpds.getDriverClass();
		if (dbClass.matches(".*\\.mysql\\..*")) {
			dbType = DB_TYPE_MYSQL;
		} else if (dbClass.matches(".*\\.oracle\\..*")) {
			dbType = DB_TYPE_ORACLE;
		} else {
			dbType = 0;
		}
		log.info("Connection pool init successfully. " + cpds.toString());
	}

	public DataSource getDataSource() {
		if (!isInit) {
			initConnPool();
		}
		return dataSource;
	}

	/**
	 * �������ݿ��Ĭ�����Ӳ�����ȡ���ݿ��Connection���󣬲��󶨵���ǰ�߳���
	 * 
	 * @return �ɹ�������Connection���󣬷��򷵻�null
	 */
	public synchronized Connection getConnection() {
		Connection conn = threadSession.get(); // �ȴӵ�ǰ�߳���ȡ������ʵ��
		try {
			if (null == conn || conn.isClosed()) { // �����ǰ�߳���û��Connection��ʵ��
				if (!isInit) {
					initConnPool();
				}
				if (dataSource != null) {
					conn = dataSource.getConnection(); // �����ӳ���ȡ��һ������ʵ��
					threadSession.set(conn); // �����󶨵���ǰ�߳���
				} else {
					log.error("Connection can not fetch from datasource!");
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 * �ر�session���ѱ�������ݿ�����,�����ǰ�߳�����connection����Nothing.
	 */
	public void close() {
		close(threadSession.get());
	}

	/**
	 * �ر����Ӳ��ͷ�session
	 * 
	 * @param conn
	 *            �ر�ָ��������
	 */
	protected void close(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				log.error("�ر�����ʱ�����쳣", e);
			} finally {
				/** жװ�̰߳� **/
				threadSession.remove();
			}
		}
	}

	/**
	 * �ύ���񲢹ر� sql ���ӡ�
	 * 
	 * @param conn
	 *            �������رյ�����
	 */
	protected void commitAndClose(Connection conn) {
		if (conn != null) {
			try {
				conn.commit();
			} catch (SQLException e) {
				log.error("�ύ����ʱ�����쳣", e);
			} finally {
				try {
					conn.close();
				} catch (SQLException e) {
					log.error("�ر�����ʱ�����쳣", e);
				}
				/** жװ�̰߳� **/
				threadSession.remove();
			}
		}
	}

}
