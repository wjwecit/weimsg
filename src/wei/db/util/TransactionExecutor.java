/**
 * 
 */
package wei.db.util;

import java.sql.SQLException;

/**
 * ����ִ����,ͨ���˽ӿ��������Ĵ���.
 * @author wei
 * @ 2014-3-12
 */
public interface TransactionExecutor {
	
	/**
	 * ��ʼִ��������
	 * @throws SQLException
	 */
	public void execute() throws SQLException;

}
