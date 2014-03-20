/**
 * 
 */
package wei.test.db;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import wei.db.util.DbTemplate;
import wei.db.util.TransactionExecutor;
import wei.web.mvc.model.AreaChina;

/**
 * @author wei
 *
 */
public class Dbtest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final DbTemplate temp=new DbTemplate();
		try {
			temp.doWithinTransaction(new TransactionExecutor() {

				@Override
				public void execute() throws SQLException {
					AreaChina bean=new AreaChina();
					bean.setAreaCode(755);
					bean.setAreaName("≈Ù≥«…Ó€⁄aaaaaaa");
					bean.setAreaCodeDeprecated(new Random().nextInt(800800)+"");
					temp.update(bean);
					bean=temp.getBean(AreaChina.class, "select * from areachina where areaCode=755");
					System.out.println(bean.toString());
				}
				
			});
			
			System.out.println("endS");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
