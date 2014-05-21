package wei.web.wm.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import wei.db.common.DbTemplate;
import wei.web.mvc.model.AreaChina;
import wei.web.mvc.model.Pos;
import wei.web.util.RequestUtils;

/**
 * Servlet implementation class BusinessServlet
 */
public class BusinessServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DbTemplate template;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BusinessServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		template = new DbTemplate();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Content-Type", "text/html;charset=utf-8");
		String action=RequestUtils.getString(request, "action");
		String did=RequestUtils.getString(request, "did");
		
		if(did.length()<1){
			return;
		}
		if(action.equalsIgnoreCase("q")){
			//Pos obj=template.getBean(Pos.class, "select * from pos where did=? order by ptime desc limit 1",new Object[]{did});
			AreaChina obj=template.getBean(AreaChina.class, "select * from areachina where areaCode=? limit 1",new Object[]{did});
			if(obj==null)return;
			String res=new JSONObject(obj).toString();
			response.getWriter().write(res);
			response.flushBuffer();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
