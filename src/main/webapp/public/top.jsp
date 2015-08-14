<%@page import="com.youwei.zjb.KeyConstants"%>
<%@page import="net.sf.json.JSONObject"%>
<%@page import="com.youwei.zjb.sys.entity.City"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.youwei.zjb.sys.CityService"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%
String type = request.getParameter("type");
request.setAttribute("type", type);
CityService cs = new CityService();
JSONArray citys = cs.getCitys();
request.setAttribute("citys", citys);
request.setAttribute(KeyConstants.Session_House_Owner, session.getAttribute(KeyConstants.Session_House_Owner));
%>
<div class="toper">
    <div class="wrap">
        <a href="#" class="logobox">中介宝</a>
        <a  href="#" class="SwitchCityBtn btn_act" data-type="SwitchCity"><span id="currentCity"  py="hefei" >合肥</span> <i class="iconfont">&#xe604;</i></a>

        <ul class="classTab">
            <li <c:if test="${type=='chushou' }">class="active"</c:if> ><a href="chushou.jsp">找二手房</a></li>
            <li <c:if test="${type=='chuzu' }">class="active"</c:if>><a href="chuzu.jsp">找出租房</a></li>
        </ul>

        <ul class="UList fr HA">
            <c:if test="${house_owner eq null }"><li class="HV"><a href="#" class="btn btn_act" data-type="login"><strong>登录管理我的房源</strong></a></li></c:if>
            <c:if test="${house_owner ne null }"><li class="HV"><a href="#" class="btn btn_act" data-type="login"><strong>${house_owner.tel }</strong></a></li></c:if>
            <c:if test="${house_owner ne null }"><li class="HV"><a href="list.jsp?action=my" class="btn " data-type="seeMyHouse"><strong>我的房源</strong></a></li></c:if>
            <c:if test="${house_owner ne null }"><li class="HV"><a href="list.jsp?action=fav" class="btn " data-type="seeMyHouse"><strong>我的收藏</strong></a></li></c:if>
            <c:if test="${house_owner ne null }"><li class="HV"><a href="#" class="btn btn_act" data-type="logout"><strong>退出</strong></a></li></c:if>
            <c:if test="${house_owner eq null }"><li class="HV"><a href="#" class="btn btn_act" data-type="reg"><strong>注册</strong></a></li></c:if>
            <li class="HB "><strong>联系我们</strong>
                <div class="HC ULbox ContactUs">
                    <div class="ewm"><img src="images/ewm_wx.jpg" alt=""><span>关注中介宝微信</span></div>
                    <div class="lx">
                        <ul>
                            <li><a href="#">客服电话：0551-65341555</a></li>
                            <li class="ablogo"><img src="images/logo_blue.png" alt=""></li>
                            <li class="ablogos"><b>二手房信息处理中心</b></li>
                        </ul>
                    </div>
                </div>
            </li>
        </ul>
    </div>
</div>