/**
 * 
 */
package wei.db.common;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * ���ݿ�����ģ����,�ṩ��ݵ����ݿ��������.
 * 
 * @author wei
 * @since 2014-3-13
 */
public class DbTemplate {

	private static final Logger log = Logger.getLogger(DbTemplate.class);

	private DBManager dbManager;

	public DbTemplate() {
		dbManager = new DBManager();
	}

	private Connection getConnection() {
		return dbManager.getConnection();
	}

	/**
	 * �����ʹ���. ʹ���¹����TransactionExecutor���������װ, �������ڿɽ���update, insert, delete �Ȳ���,
	 * �����ص�������ķ�������, �ⶼ�����Զ����.
	 * 
	 * @param executor
	 *            �������ִ����.
	 */
	public void doWithinTransaction(TransactionExecutor executor) {
		Connection conn = getConnection();
		if (conn != null) {
			try {
				conn.setAutoCommit(false);
				log.debug("transaction begin...");
				executor.execute(new Session(conn));
				conn.commit();
				log.debug("transaction commited successfully!");
			} catch (SQLException e) {
				e.printStackTrace();
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			} finally {
				dbManager.close(conn);
			}
		}
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
		if ((type.equals(Character.TYPE)) && (Character.class.isInstance(value))) {
			return true;
		}
		if ((type.equals(Boolean.TYPE)) && (Boolean.class.isInstance(value))) {
			return true;
		}
		return false;
	}

}
