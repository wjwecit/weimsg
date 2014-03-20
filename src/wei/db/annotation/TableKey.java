/**
 * 
 */
package wei.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ע�ⷽʽע��ʵ����ı�����.
 * @author wei
 * @since 2014-3-12
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TableKey {
	/**
	 * �������ֶ���,Ĭ���ǿհ�
	 */
	public String columnName()default "";
}
