<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page import="jvc.util.LanguageUtils"%>
<div class="header">
	<div class="header_main">
    	<div class="logo"><a href="#"><img src="../images/logo.jpg" /></a></div>
        <div class="logo_line"></div>
    	<h1>dodopay</h1>
        <a href="javascript:void(0)" onclick="if(confirm('<%=LanguageUtils.get(request, "AreYouSureWantToExit", "你确认要退出吗?") %>'))window.location.href='../service/user/userLoginoutServer.jsp'" class="exit"><%=LanguageUtils.get(request, "Exit", "退出") %></a>
        <div class="clear"></div>
    </div>
</div>