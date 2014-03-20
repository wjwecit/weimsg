/**
 * 
 */
package wei.web.mvc.model;

/**
 * @author wei
 * @since 2014-3-16
 */
public class Pos {
	
	private int pid;
	private int did;
	private double lon;
	private double lat;
	private double rad;
	private String pname;
	private String ptime;
	private int ptype;
	private int visiable;
	private int flag;
	
	
	
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public int getDid() {
		return did;
	}
	public void setDid(int did) {
		this.did = did;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public String getPname() {
		return pname;
	}
	public void setPname(String pname) {
		this.pname = pname;
	}
	public double getRad() {
		return rad;
	}
	public void setRad(double rad) {
		this.rad = rad;
	}
	public String getPtime() {
		return ptime;
	}
	public void setPtime(String ptime) {
		this.ptime = ptime;
	}
	public int getPtype() {
		return ptype;
	}
	public void setPtype(int ptype) {
		this.ptype = ptype;
	}
	public int getVisiable() {
		return visiable;
	}
	public void setVisiable(int visiable) {
		this.visiable = visiable;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
}
