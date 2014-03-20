<%@page import="org.apache.catalina.util.RequestUtil"%>
<%@page import="wei.web.util.RequestUtils"%>
<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String error = RequestUtils.getString(request, "error");
	String kaptcha = RequestUtils.getString(request, "kaptcha");
	String login = RequestUtils.getString(request, "login");
	String pass = RequestUtils.getString(request, "pass");
	String timeout = RequestUtils.getString(request, "timeout");
	String enabled = RequestUtils.getString(request, "enabled");
	String relogin = RequestUtils.getString(request, "relogin");
	String reg = RequestUtils.getString(request, "reg");
	String loginout = RequestUtils.getString(request, "loginout");
	String loginName = RequestUtils.getString(request, "loginName");
	String chooseLocale = RequestUtils.getString(request,"lan");//用户选择的语言
	String guessLocale  = request.getLocale().toString();//系统识别的语言
	String lanLabel = "<a href='login.jsp?lan=zh'>简体中文</a>";
	if(guessLocale.equalsIgnoreCase("en")){//英文
		lanLabel = "<a href='login.jsp?lan=en'>English</a>";
	}else if(guessLocale.equalsIgnoreCase("zh_TW")){//繁体中文
		lanLabel = "<a href='login.jsp?lan=zh_TW'>繁体中文</a>";
	}else if(guessLocale.equalsIgnoreCase("th")){//泰文
		lanLabel = "<a href='login.jsp?lan=th'>ภาษาไทย</a>";
	}
	
 %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link type="text/css" rel="stylesheet" href="css/style.css" />
<script type="text/javascript" src="js/jquery-1.7.2.min.js"></script>
<!--1.7.2-->
<title>登录</title>
</head>

<body>
	<div class="header">
		<div class="header_main">
			<div class="logo">
				<a href="#" target="_blank"><img src="images/logo.jpg" />
				</a>
			</div>
			<div class="logo_line"></div>
			<h1>用户登录</h1>
			<div class="clear"></div>
		</div>
	</div>
	<div class="middle">
		<div class="middle_main">
			<div class="main_top"></div>
			<div class="main_center">
				<!-- <div class="mm_qqlogin">
            	<h2>1、使用QQ号快速登陆:</h2>
                <a href="#" class="qqlogin_btn"></a>
            	</div> -->
				<div class="mm_ddnlogin">
					<form action="service/user/userLoginServer.jsp" method="post">
						<h2>
							<span class="register">没有账号
								<a href="fblogin" title="使用Facebook账号登录"><img src="images/facebook_login.png"></img></a>
								<a href="user/register.jsp" >注册</a>
							</span>
						</h2>
						<p id="errormsg">
							<%if(!error.equals("")){%>
								<span class="error">请求异常，已纪录</span>
							<%} %>
						</p>
						<p>
							<span class="label_align">帐号:</span><input type="text" id="loginName" name="loginName" class="user" value="<%=loginName%>"/>
							<span id="loginNameDiv" style="display:none">请输入登录名</span>
						</p>
						<p>
							<span class="label_align">密码:</span><input type="password"  id="loginPass" name="loginPass" class="password" />
							<span id="loginPassDiv" style="display:none">请输入密码</span>
						</p>
						<p>
							<span class="label_align">验证码:</span><input type="text" id="validCode" name="validCode" class="password" /> 
							<img src="kaptcha.jpg" width="120" id="kaptchaImage" />
							<span id="validCodeDiv" style="display:none">请输入验证码</span>
						</p>
						<p class="button">
							<input type="submit" value="登录" class="sub_login" onclick="return check()"/> 
							<!-- <a href="#" target="_blank" class="forget_password">忘记密码？</a> -->
						</p>
					</form>
				</div>
			</div>
			<div id="lang-guess">
				我们检测到您所使用的语言为: <%=lanLabel %>
			</div>
			<div class="main_bottom"></div>
		</div>

	</div>
	<%@ include file="common/bottom.jsp"%>
	<script>
		$(function() {
			$('#kaptchaImage').click(
					function() {
						$(this).attr(
								'src',
								'kaptcha.jpg?'
										+ Math.floor(Math.random() * 100));
					})
		});
		function check(){
			$("#loginNameDiv").hide();
			$("#loginPassDiv").hide();
			$("#validCodeDiv").hide();
			var loginName = $("#loginName").val();
			if(loginName==""){
				$("#loginNameDiv").show();
				return false;
			}
			var loginPass = $("#loginPass").val();
			if(loginPass==""){
				$("#loginPassDiv").show();
				return false;
			}
			var validCode = $("#validCode").val();
			if(validCode==""){
				$("#validCodeDiv").show();
				return false;
			}
			return true;
		}
	</script>
</body>
</html>

