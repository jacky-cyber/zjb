<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" style="height:570px;">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<jsp:include page="../inc/top.jsp" />
<title>无标题文档</title>
<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/buildHtml.js"></script>
<script src="/js/dialog/jquery.artDialog.source.js?skin=win8s" type="text/javascript"></script>
<script src="/js/dialog/plugins/iframeTools.source.js" type="text/javascript"></script>
<script type="text/javascript" src="/js/user.js"></script>
<script src="/js/jquery.j.tool.v2.js" type="text/javascript"></script>
<script src="/js/validate.js" type="text/javascript"></script>
<script type="text/javascript" src="/js/keyEnter.js"></script>
<script type="text/javascript">
var lxingcont=0;
var hxingcont=0;
var zxiucont=0;
var quyucont=0;
var cid=${me.cid};
var dataScope="ky";
function save(){
  if(checkDateyear($('#yearFrom'))==false){
      return ;
    }
  if(checkDateyear($('#yearTo'))==false){
      return ;
    }
  if(checkTel()==false){
    return;
  }
  var a=$('form[name=form1]').serialize();
  if ($('#uid').val()==undefined||$('#uid').val()=='') {
    alert('请先选择业务员');
  }else if ($('#TelAll').val()==undefined||$('#TelAll').val()=='') {
    alert('请填写联系人电话');
    $('#tel').focus();
  }else{
    YW.ajax({
      type: 'POST',
      url: '/c/client/add',
      data:a,
      mysuccess: function(data){
          // $('#saveBtn').removeAttr('disabled');
          art.dialog.close();
          art.dialog.opener.doSearch();
          alert('发布成功');
      }
    });
  }
}

function close(){
  art.dialog.close();
}

function setSearchValue(index ,click){
    var ThiA=$('#autoCompleteBox').find('a');
    ThiA.removeClass('hover');
    var Vals=ThiA.eq(index).addClass('hover').attr('area');
    $('#areas').val(Vals);

    if(click){
		
      houseAddMain();  
      $("#HouseBoxMore div i").click(function(){
				  
		  var telText = "";
		  $("#HouseAreaAll").val("");
		  $(this).parent("div").remove();
		  
		  var divNumChange = $("#HouseBoxMore div").size();
		  $("#HouseBoxMore div").each(function(i) {
			  if(i == (divNumChange-1)){
			   telText += $(this).find("span").text();
			  }else{
			   telText += $(this).find("span").text() + "/";
			  }
		  });
		  $("#HouseAreaAll").val(telText);
		  
		  var pleft = $("#HouseBoxMore").width() + 10 ;
		  var wInput = 688 - pleft;
		
		  $("#areas").css({
			"padding-left":pleft + 5,
			"width":wInput
		  });
		  
		  

      });
    }
    $("#areas").focus();
    
}


$(document).ready(function() {
  autoComplete($('#areas'));
    $.get('/c/config/getQueryOptions', function(data) {
        queryOptions=JSON.parse(data);
        buildHtmlWithJsonArray("id_lxing",queryOptions['lxing'],true);
        buildHtmlWithJsonArray("id_zxiu",queryOptions['zhuangxiu'],true);
        buildHtmlWithJsonArray("id_hxing",queryOptions['fxing'],true);
        buildHtmlWithJsonArray("id_quyu",queryOptions['quyu'],true);
        //doSearchAndSelectFirst();
    });
    $('#did').val('$${did}');
    $('#did').change();
    $('#uid').val('$${userId}');
    $('#name').focus();
});
</script>
<link rel="stylesheet" type="text/css" href="/style/css_ky.css" />
</head>

<body >

<script>

  


</script>
<form name="form1" role="form">
<table width="100%" border="0" cellspacing="0" cellpadding="0" class="layerTable1 TableBgColor1" style=" border-top:4px solid #e9e9e9;">
  <tr>
    <td width="70" align="right" class="TableTdBorBot TableTdBorTop">姓名：</td>
    <td width="300" class="TableTdBgColor"><input type="text" class="w1" name="name" id="name" desc="客户姓名"/></td>
    <td width="70" align="right" class="TableTdBorBot TableTdBorTop">电话：</td>
    <td width="356" class="TableTdBgColor" style="position:relative;"><div id="TelBoxMore" style="margin: 0 0 0 2px; top:10px; position:absolute;"></div><input type="text" id="tel" style="width:315px;"  placeholder="输入号码后按回车键（Enter）确认，仅限3个号码。"  /><input type="hidden" value="" name="tels" id="TelAll"/></td>
  </tr>
  <tr>
    <td align="right" class="TableTdBorBot" style="white-space:nowrap;"><span style="margin-right:12px;">期望</span><br />楼盘：</td>
    <td class="TableTdBgColor" colspan="3" width="726" height="35" style="position:relative;">
          <input type="text" class="w1" id="areas" autocomplete="off" placeholder="添加楼盘后按回车键（Enter）确认，仅限5个楼盘。" style="width:690px; position:absolute; left:8px; top:5px; z-index:1;" /><input type="hidden" name="areas" id="HouseAreaAll" />
          <div style=" top:8px; left:15px; position:absolute; z-index:10; max-width:680px; height:23px; overflow-x:auto;overflow-y:hidden;">
               <div id="HouseBoxMore"  style="height:23px; float:left; display:inline-block;"></div>
          </div>
    </td>
  </tr>
  <tr>
    <td align="right" class="TableTdBorBot">楼型：</td>
    <td class="TableTdBgColor">
        <div class="id_lxing"><input type="checkbox" value="$[name]" name="lxings" onclick="jianCe(this);" />$[name]</div>
    </td>
    <td align="right" class="TableTdBorBot">装潢：</td>
    <td class="TableTdBgColor">
        <div class="id_zxiu"><input type="checkbox" value="$[name]" name="zxius" onclick="jianCe(this);"/>$[name]</div>
    </td>
  </tr>
  <tr>
    <td align="right" class="TableTdBorBot">户型：</td>
    <td class="TableTdBgColor">
        <div class="id_hxing"><input type="checkbox" value="$[name]" name="hxings" onclick="jianCe(this);"/>$[name]</div>
    </td>
    <td align="right" class="TableTdBorBot">区域：</td>
    <td class="TableTdBgColor">
        <div class="id_quyu"><input type="checkbox" value="$[name]" name="quyus" onclick="jianCe(this);"/>$[name]</div>
    </td>
  </tr>
  <tr>
    <td align="right" class="TableTdBorBot">面积：</td>
    <td class="TableTdBgColor"><input type="text" class="w2" desc="面积" name="mjiFrom" onkeyup="checkmji(this)" />-<input type="text" onkeyup="checkmji(this)" desc="面积" class="w2"  name="mjiTo"/> ㎡</td>
    <td align="right" class="TableTdBorBot">总价：</td>
    <td class="TableTdBgColor"><input type="text" class="w2" desc="价格" name="jiageFrom" onkeyup="checkzjia(this)" />-<input type="text" desc="价格" class="w2"  name="jiageTo" onkeyup="checkzjia(this)"/> 万</td>
  </tr>
  <tr>
    <td align="right" class="TableTdBorBot">单价：</td>
    <td class="TableTdBgColor"><input type="text" class="w2" desc="单价" name="djiaFrom" onkeyup="checkdjia(this)" />-<input type="text" desc="单价" class="w2"  name="djiaTo" onkeyup="checkdjia(this)"/> 元</td>
    <td align="right" class="TableTdBorBot">楼层：</td>
    <td class="TableTdBgColor"><input type="text" desc="楼层" class="w2" name="lcengFrom" onkeyup="checklceng(this)" />-<input type="text" desc="楼层" class="w2" name="lcengTo" onkeyup="checklceng(this)" /> 层</td>
  </tr>
  <tr>
    <td align="right" class="TableTdBorBot">年代：</td>
    <td class="TableTdBgColor"><input type="text" class="w2" desc="年代" name="yearFrom" id="yearFrom" onkeyup="checkyear(this)" />-<input type="text" desc="年代" class="w2"  name="yearTo" id="yearTo" onkeyup="checkyear(this)"/></td>
    <td align="right" class="TableTdBorBot" style="white-space:nowrap;">业务员：</td>
    <td colspan="3" class="TableTdBgColor">
        <select name="did" id="did" class="input-sm input-rightB w3 dept_select">
	    </select>
	    <select name="uid" id="uid" class="input-sm input-left w3 user_select">
        </select>
    </td>
  </tr>
  <tr>
    <td align="right" class="TableTdBorBot">备注：</td>
    <td colspan="3" class="TableTdBgColor">
       <textarea class="TA" name="beizhu"></textarea>
    </td>
  </tr>
  <tr>
    <td align="right" class="" style=" background-color:#e9e9e9;"> </td>
    <td colspan="3" class="" height="45" style=" background-color:#e9e9e9;">
       <button onclick="save();return false;" class="save btn btn-primary btn-block" >保存</button>
       <!-- <button class="cancel" onclick="close();">取消</button> -->
    </td>
  </tr>
  
</table>
</form>
</body>
</html>
