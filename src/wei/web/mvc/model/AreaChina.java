package wei.web.mvc.model;

import java.io.Serializable;

import org.json.JSONObject;

import wei.db.annotation.TableKey;

/**
 * @author Jerry
 *
 */

public class AreaChina implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7859704013566331716L;
	
	@TableKey(columnName="areaCode")
	private int areaCode;
	private String areaName;
	private String areaCodeDeprecated;	
	
	
	public int getAreaCode() {
		return areaCode;
	}

	
	public void setAreaCode(int areaCode) {
		this.areaCode = areaCode;
	}
	
	public String getAreaCodeDeprecated() {
		return areaCodeDeprecated;
	}

	public void setAreaCodeDeprecated(String areaCodeDeprecated) {
		this.areaCodeDeprecated = areaCodeDeprecated;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	
	@Override
	public String toString() {
		JSONObject json=new JSONObject(this);
		return json.toString();
	}
}
