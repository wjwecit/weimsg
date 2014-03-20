package wei.db.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * @author Wangjw 显示分页表格数据，使用原生态的SQL统计，并返回数组形式之数据。
 */
public class PageTable {
	/**
	 * slf日志对象
	 */
	private static Logger log = Logger.getLogger(PageTable.class);
	/**
	 * 查询结果总页数
	 */
	private long totalPage = -1;
	/**
	 * 返回字串数组格式之数据
	 */
	private ArrayList<HashMap<String,String>> dataArray;
	

	/**
	 * 查询结果中总记录数
	 */
	private long totalRow = 0;
	/**
	 * 分页大小
	 */
	private int pageSize;
	/**
	 * 当前页面中记录数
	 */
	private long currentPageRows = 0;
	/**
	 * 当前页数
	 */
	private long currentPage;
	/**
	 * 查询SQL语句
	 */
	private String sql;
	/**
	 * 查询结果中记录总列数
	 */
	private int columnCount = 1;
	/**
	 * 标识查询是否成功
	 */
	private boolean inited = false;
	/**
	 * 默认页面大小页面
	 */
	public static final int INIT_PAGE_SIZE = 10;
	/**
	 * 默认页数
	 */
	public static final int INIT_CURRENT_PAGE = 1;

	private String[] labels = null;

	/**
	 * 默认构造函数
	 */
	public PageTable() {
		this.currentPage = INIT_CURRENT_PAGE;
		this.pageSize = INIT_PAGE_SIZE;
	}

	public PageTable(String selectsql, long pno, int ps) {
		this.sql = selectsql;
		if (pno < 1) {
			pno = INIT_CURRENT_PAGE;
		}
		this.currentPage = pno;
		if (ps < 1 || ps > 999) {
			ps = INIT_PAGE_SIZE;
		}
		this.pageSize = ps;
		rend();
	}
	
	protected void initCount(ResultSet rsc) throws NumberFormatException, SQLException{
		if (rsc != null) {
			while (rsc.next()) {
				Object rows_o = rsc.getObject(1);
				totalRow = Integer.parseInt(rows_o.toString());
				totalPage = (totalRow % pageSize == 0) ? totalRow
						/ pageSize : (totalRow / pageSize) + 1;
				if (this.currentPage > totalPage) {
					this.currentPage = totalPage;
				}
				break;
			}
		}
	}

	/**
	 * 逻辑主体，对各参数进行正确赋值.
	 */
	public void rend() {
		String sql_s = this.sql;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null, rsc = null;
		try {
			conn = this.getConnection();
			String sql_c = "select count(*) from(" + this.sql + ")ttcount";
			pst = conn.prepareStatement(sql_c);
			rsc = pst.executeQuery();
			
			/**开始渲染总记录数**/
			initCount(rsc);
			
			if (this.totalRow == 0) {
				log.info("no data found:" + this.sql);
				this.dataArray = null;
				return;
			}
			sql_s = "select * from (" + sql + ") tttable limit ?,?";
			pst = conn.prepareStatement(sql_s);
			pst.setLong(1, (currentPage - 1) * pageSize);
			pst.setInt(2, pageSize);
			rs = pst.executeQuery();
			log.info(sql_s+";pageNo:"+currentPage+";pageSize:"+pageSize);
			if (rs != null) {
				ResultSetMetaData rmd = rs.getMetaData();
				int iCols = rmd.getColumnCount();
				setColumnCount(iCols);
				labels = new String[iCols];
				for (int col = 1; col <= iCols; col++) {
					labels[col - 1] = rmd.getColumnLabel(col);
				}
				dataArray = new ArrayList<HashMap<String,String>>();
				String scell = null;
				long rowc = 0;
				while (rs.next()) {
					HashMap<String,String> rowMap=new HashMap<String,String>();
					for (int i = 1; i <= iCols; i++) {
						scell = rs.getString(i);
						rowMap.put(labels[i-1], (scell == null ? "" : scell).trim());
					}
					rowMap.put("rownum", ++rowc+currentPage*pageSize-pageSize+"");
					dataArray.add(rowMap);
				}
				currentPageRows = rowc;
				inited = true;
			}
		} catch (SQLException e) {
			log.error(e.getMessage());
			try {
				throw e;
			} catch (SQLException e1) {
				log.error(e1.getMessage());
			}
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public long getTotalRow() {
		return totalRow;
	}

	/**
	 * 获得总页数，当结果集中记录数为0时，则返回0。
	 * 
	 * @return 总页数。
	 */
	public long getTotalPage() {
		if (!isInited()) {
			return 0;
		}
		return totalPage;
	}

	public long getCurrentPageRows() {

		return this.currentPageRows;
	}

	public void setCurrentPage(long currentPage) {
		if (currentPage < 1) {
			currentPage = 1;
		}
		this.currentPage = currentPage;
	}

	/**
	 * 得到当前的页码,如果未进行任何操作,则返回默认页码.
	 * @return
	 */
	public long getCurrentPage() {
		if (!isInited()) {
			return INIT_CURRENT_PAGE;
		}
		if (this.currentPage > this.getTotalPage()) {
			this.currentPage = this.getTotalPage();
		}
		return this.currentPage;
	}

	/**
	 * 获得每页显示记录数,如果尚未进行任何操作,则返回默认的页面大小INIT_PAGE_SIZE
	 * @return 每页显示记录数
	 */
	public int getPageSize() {
		if (!isInited()) {
			return INIT_PAGE_SIZE;
		}
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		if (pageSize < 1 || pageSize > 999) {
			pageSize = INIT_PAGE_SIZE;
		}
		this.pageSize = pageSize;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String[] getLabels() {
		if (!isInited()) {
			return null;
		}
		return this.labels;
	}

	private Connection getConnection() {
		return new DbTemplate().getConnection();
	}

	public boolean isInited() {
		if (inited) {
			return true;
		}
		try {
			rend();			
		} catch (Exception ex) {
			log.error(ex.getMessage());
			inited = false;
		}
		return inited;
	}

	public void setInited(boolean isInited) {
		this.inited = isInited;
	}
	
	public ArrayList<HashMap<String, String>> getDataArray() {
		return dataArray;
	}

	public void setDataArray(ArrayList<HashMap<String, String>> dataArray) {
		this.dataArray = dataArray;
	}

	public int getColumnCount() {
		return columnCount;
	}

	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
	}
}
