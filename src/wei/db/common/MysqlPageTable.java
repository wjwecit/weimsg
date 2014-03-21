package wei.db.common;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * @author Wangjw ��ʾ��ҳ������ݣ�ʹ��ԭ��̬��SQLͳ�ƣ�������������ʽ֮���ݡ�
 */
public class MysqlPageTable extends AbstractPageTable{
	
	/**log��־����**/
	private static Logger log = Logger.getLogger(MysqlPageTable.class);
	
	protected String getCountSql(){
		return "select count(*) from(" + this.sql + ")ttcount";
	}
	
	@Override
	protected String getPageSql() {
		return "select * from (" + sql + ") tttable limit ?,?";
	}

	@Override
	protected PreparedStatement buildPageStatement() throws SQLException {
		String sql_s = getPageSql();
		PreparedStatement pst = getConnection().prepareStatement(sql_s);
		pst.setLong(1, (currentPage - 1) * pageSize);
		pst.setInt(2, pageSize);		
		log.info(sql_s+";pageNo:"+currentPage+";pageSize:"+pageSize);
		return pst;
	}	
}
