<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page import="com.paycenter.entity.InfoUser" %>
<%@ page import="com.paycenter.entity.LogLoginUser" %>
<%@ page import="jvc.util.LanguageUtils"%>
<%
	InfoUser user = (InfoUser) session.getAttribute("loginUser");
	LogLoginUser logLoginUser = (LogLoginUser) session.getAttribute("logLoginUser");
	if(user==null){
		response.sendRedirect("../login.jsp?timeout=1");
		return;
	}
%>
