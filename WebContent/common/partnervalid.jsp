<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page import="com.paycenter.entity.InfoPartner" %>
<%@ page import="com.paycenter.entity.LogLoginPartner" %>
<%@ page import="jvc.util.LanguageUtils"%>
<%
	InfoPartner partner = (InfoPartner) session.getAttribute("loginPartner");
	LogLoginPartner logLoginPartner = (LogLoginPartner) session.getAttribute("logLoginPartner");
	if(partner==null){
		response.sendRedirect("../partner/login.jsp?timeout=1");
		return;
	}
%>
