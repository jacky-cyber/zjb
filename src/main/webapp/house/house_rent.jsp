<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<jsp:include page="../inc/top.jsp" />
<title>出租房源</title>
<link rel="stylesheet" type="text/css" href="/style/css_ky.css" />
<link rel="stylesheet" type="text/css" href="/style/font/iconfont.css" />
<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/buildHtml.js"></script>
<script type="text/javascript" src="/js/pagination.js"></script>
<script type="text/javascript" src="/js/DatePicker/WdatePicker.js"></script>
<script src="/js/dialog/jquery.artDialog.source.js?skin=win8s" type="text/javascript"></script>
<script src="/js/dialog/plugins/iframeTools.source.js" type="text/javascript"></script>
<script src="/js/jquery.j.tool.v2.js" type="text/javascript"></script>
<script type="text/javascript">
var houseData;
var houseGJbox;
var searching=false;
var chuzu=getParam('chuzu');
function doSearch(callback){
  if(searching){
    return;
  }
  if(event==undefined || $(event.srcElement).attr('action')!='page'){
    $('.pageInput').val(1);
  }
  searching=true;
  $('#searchBtn').attr('disabled','disabled');
  var a=$('form[name=form1]').serialize();
  // pushQueryToHistory($('form[name=form1]').serializeArray());
  YW.ajax({
    type: 'POST',
    url: '/c/house/rent/listAll',
    data:a,
    mysuccess: function(data){
        houseData=JSON.parse(data);
        buildHtmlWithJsonArray("id_House_list",houseData['page']['data']);
        Page.setPageInfo(houseData['page']);
        
        if(callback){
          callback();
        }
    },
    complete:function(){
      searching=false;
      $('#searchBtn').removeAttr('disabled');
    }
  });
}

function doSearchAndSelectFirst(){
  doSearch(function(){
    if(houseData.page.data.length>0){
      $('.TableB').find('tr')[1].click();
    }
  });
}

function getShenHeText(lock){
  if(lock==1){
    return "审";
  }else{
    return "未";
  }
}

function showAction(hcid,cid , seeGX){
  if(cid==1){
    return true;
  }else if(hcid==cid){
    if(seeGX==1){
      return false;
    }else{
      return true;
    }
  }else{
    return false;
  }
}

function getSider(id){
    if(id){
        $('#sideCont').attr('src','/house/rent/view.jsp?id='+id+'&chuzu=1');
    }
}


function houseEdit(id){
  // event.cancelBubble=true;
  art.dialog.open("/house/house_rent_edit.html?id="+id)
}

function ruku(id){
  art.dialog.open("/house/rent/ruku.jsp?id="+id)
}

function shenheFy(id , obj){
  // event.cancelBubble=true;
  YW.ajax({
    type: 'POST',
    url: '/c/house/rent/toggleShenHe?id='+id,
    mysuccess: function(data){
        var json = JSON.parse(data);
        if(json.sh==1){
          $(obj).text('审');
          $(obj).css('color','green');
        }else{
          $(obj).text('未');
          $(obj).css('color','red');
        }
    }
  });
}

function deletehouse(id){
  event.cancelBubble=true;
  art.dialog.confirm('删除后不可恢复，确定要删除吗？', function () {
  YW.ajax({
    type: 'POST',
    url: '/c/house/rent/physicalDelete?houseId='+id,
    mysuccess: function(data){
      $(event.srcElement).attr('action','page');
      doSearch();
      alert('删除成功');
    }
  });
  },function(){},'warning');
}
function setSearchValue(index){
    var ThiA=$('#autoCompleteBox').find('a');
    ThiA.removeClass('hover');
    var Vals=ThiA.eq(index).addClass('hover').attr('area');
    $('#search').val(Vals);
}
function updateRuku(id){
  $('#btn_ruku_'+id).remove();
  $('#shenhe_'+id).text('审').removeClass('shenhe_0');
  //更新未审核为已审核

}
function updateHouse(id,data){
  getSider(id);
  var json = JSON.parse(data);
  var tr = $('tr[data-hid='+json['house']['id']+']');
  var a = tr.children()[2]
  var b = tr.children()[3]
  var c = tr.children()[4]
  var d = tr.children()[5]
  var e = tr.children()[6]
  var f = tr.children()[7]
  var g = tr.children()[8]
  var h = tr.children()[9]
  var i = tr.children()[10]
  var j = tr.children()[11]
  $(a).html(json['house']['ztai']);
  $(b).html(json['house']['quyu']);
  $(c).html(json['house']['area']+' '+json['house']['dhao']+'-'+json['house']['fhao']);
  $(d).html(json['house']['lxing']);
  $(e).html(json['house']['hxf']+'-'+json['house']['hxt']+'-'+json['house']['hxw']);
  $(f).html(json['house']['mji']);
  $(g).html(json['house']['zjia']);
  $(h).html(json['house']['lceng']+'/'+json['house']['zceng']);
  $(i).html(json['house']['zxiu']);
  $(j).html(json['house']['dateadd']);
}

$(document).ready(function(){
    ChangeW();
    Page.Init();
    initTopMenu();
    autoComplete($('#search'));
    $.get('/c/config/getQueryOptions', function(data) {
      queryOptions=JSON.parse(data);
      buildHtmlWithJsonArray("id_lxing",queryOptions['lxing'],true);
      buildHtmlWithJsonArray("id_fxing",queryOptions['fxing'],true);
      buildHtmlWithJsonArray("id_zhuangxiu",queryOptions['zhuangxiu'],true);
      buildHtmlWithJsonArray("id_quyu",queryOptions['quyu'],true);
      buildHtmlWithJsonArray("id_zhuangtai",queryOptions['ztai_rent'],true , true);
      buildHtmlWithJsonArray("id_fangshi",queryOptions['rent_type'],true , true);
      doSearchAndSelectFirst();
    });
    $("button.selectMethod").parent().hover(function(){
      $(this).siblings().find("div.ulErMenu").hide();
      $(this).find("div.ulErMenu").show();
    },function(){
         $(this).find("div.ulErMenu").hide(); 
    });
    $('.id_House_list').on('click', 'a', function(event) {
        var Thi=$(this),
        rel=Thi.data('rel'),
        this_hid=Thi.data('hid');
    });
    $('.TableB').on('click', 'tr', function(event) {
        var Thi=$(this),
        ThiHid=Thi.data('hid');
        if(houseGJbox)houseGJbox.close();
        getSider(ThiHid);
        // Thi.toggleClass('selected');
        return false;
    });
    // tableHover();
});

</script>
<style type="text/css">
  .selected{
    background-color: blue;
  }

.KY_TableMain td a.shenhe_0{color:red;}
.ztai_已租{color: red}
.ztai_在租{color: blue}
/*.KY_TableMain td a.shenhe_1{color:green;}*/
</style>
</head>

<body>

<div class="KY_Main KY_W">

     
     <div class="MainRight">
          
          <div style="display:table; width:100%; height:100%; overflow:hidden;">
            <div id="menuTop" style="display:inline-block;">
              <ul class="MainRightTop KY_W" onselectstart="return false;">
                  <li onclick="window.location='/house/houses.jsp'"><i class="i1"></i>出售</li>
                  <li class="line"></li>
                  <li class="slect" onclick="window.location='/house/house_rent.jsp'"><i class="i2"></i>出租</li>
                  <li class="line"></li>
                  <li class="MenuBox" style="position:relative;">
                       <i class="i3"></i>登记
                       <div class="topMenuChid">
                            <span></span>
                            <a href="javascript:void(0)" onclick="openAddHouse('/v/house/house_add.html?act=add&chuzu=0')">出售登记</a> 
                            <a href="javascript:void(0)" onclick="openAddHouse('/v/house/house_rent_add.html?act=add&chuzu=1')">出租登记</a> 
                       </div>
                  </li>
                  <li class="line"></li>
                  <li class="MenuBox" style="position:relative;">
                       <i class="i4"></i>我的
                       <div class="topMenuChid">
                            <span></span>
                            <a href="javascript:void(0)" onclick="window.location='/v/house/house_my_v2.html?act=my&flag=favShou&chuzus=0'">我收藏的出售</a> 
                            <a href="javascript:void(0)" onclick="window.location='/v/house/house_my_v2.html?act=my&flag=favZu&chuzus=1'">我收藏的出租</a>
                            <a href="javascript:void(0)" onclick="window.location='/v/house/house_my_v2.html?act=my&flag=addShou&chuzus=0'">我发布的出售</a> 
                            <a href="javascript:void(0)" onclick="window.location='/v/house/house_my_v2.html?act=my&flag=addZu&chuzus=1'">我收藏的出租</a>  
                       </div>
                  </li>
                  <li class="line"></li>
              </ul>
              </div>
              <div class="MainRightInputMain KY_W" style="margin-bottom:5px;">
                <form class="form-horizontal form1" onsubmit="doSearchAndSelectFirst();return false;" role="form" name="form1">
                   <input type="hidden" id="sh" name="sh" value="${sh}">
                   <ul class="InputMainLine KY_W" style=" margin-bottom:8px;">
                   
                        <li><input class="input-sm w110" type="text" placeholder="电话" name="tel" /></li>
                        <li>
                            <span class="SpanName">楼盘</span>
                            <input id="search" class="input-sm input-left w208" type="text" placeholder="" autocomplete="off" name="area" />
                        </li>
                        <li>
                            <button class="selectMethod hand"  type="button">区域<i class="caret"></i> </button>
                            <div class="ulErMenu"> 
                                <p onclick="addQueryStr('quyus')" class="id_quyu"><label><input class="hand" text="$[name]" type="checkbox" name="quyus" value="$[name]"> $[name]</label> </p> 
                            </div>
                        </li>
                        <li>
                            <button class="selectMethod hand" type="button">楼型<i class="caret"></i> </button>
                            <div class="ulErMenu"> 
                                <p onclick="addQueryStr('lxing')" class="id_lxing"><label><input class="hand" text="$[name]" type="checkbox" name="lxing" value="$[name]"> $[name]</label> </p> 
                            </div>
                        </li>
                        <li>
                            <button class="selectMethod hand" type="button">房型<i class="caret"></i> </button>
                            <div class="ulErMenu"> 
                                <p onclick="addQueryStr('fxing')" class="id_fxing"><label><input class="hand" text="$[value]" type="checkbox" name="fxing" value="$[value]"> $[name]</label> </p> 
                            </div>
                        </li>
                        <li>
                            <button class="selectMethod hand" type="button">装潢<i class="caret"></i> </button>
                            <div class="ulErMenu"> 
                                <p onclick="addQueryStr('zxiu')" class="id_zhuangxiu"><label><input text="$[name]" class="hand" type="checkbox" name="zxiu" value="$[name]"> $[name]</label> </p> 
                            </div>
                        </li>
                        <li>
                            <button class="selectMethod" type="button">方式<i class="caret"></i> </button>
                            <div class="ulErMenu"> 
                                <p onclick="addQueryStr('fangshi')"><label><input type="radio" name="fangshi" text="" value="" checked="checked"> 不限</label> </p> 
                                <p onclick="addQueryStr('fangshi')" class="id_fangshi"  ><label><input text="$[name]" type="radio" name="fangshi" value="$[code]"> $[name]</p>
                            </div>
                        </li>
                        <li>
                            <button class="selectMethod zonghe" type="button">综合<i class="caret"></i> </button>
                            <div class="ulErMenu"> 
                                <p onclick="addQueryStr('scope')"><label><input type="radio" text="" name="scope" value="all" checked="checked" > 所有</label> </p> 
                                <p onclick="addQueryStr('scope')"><label><input type="radio" text="公司" name="scope" value="comp">公司</label> </p> 
                                <p onclick="addQueryStr('scope')"><label><input type="radio" text="共享" name="scope" value="seeGX" > 共享</label> </p> 
                            </div>
                        </li>
                        <li>
                            <button class="selectMethod" type="button" >状态<i class="caret"></i> </button>
                            <div class="ulErMenu"> 
                                <p onclick="addQueryStr('ztai')"><label><input type="radio" name="ztai" text="" value="" checked="checked" > 所有</label> </p> 
                                <p onclick="addQueryStr('ztai')" class="id_zhuangtai"  ><label><input text="$[name]" type="radio" name="ztai" value="$[code]"> $[name]</p>
                            </div>
                        </li>
                        <c:if test="${authNames.contains('fy_sh') }">
                          <li>
                              <button class="selectMethod" type="button" >审核<i class="caret"></i> </button>
                              <div class="ulErMenu"> 
                                  <p onclick="addQueryStr('shehe')"><label><input text="" name="shehe" type="radio" value="" checked="checked" onclick="$('#sh').val(this.value);">全部</label> </p> 
                                  <p onclick="addQueryStr('shehe')"><label><input text="已审" name="shehe" type="radio" value="1" onclick="$('#sh').val(this.value);">已审</label> </p> 
                                  <p onclick="addQueryStr('shehe')"><label><input text="未审" name="shehe" type="radio" value="0" onclick="$('#sh').val(this.value);">未审</label> </p> 
                              </div>
                          </li>
                        </c:if>
                        <c:if test="${authNames.contains('fy_ruku') }">
                          <li>
                            <button class="selectMethod" type="button" >来源<i class="caret"></i> </button>
                            <div class="ulErMenu"> 
                                <p onclick="addQueryStr('site')"><label><input text="" name="site" type="radio" value="" checked="checked">全部</label> </p> 
                                <p onclick="addQueryStr('site')"><label><input text="58" name="site" type="radio" value="58" >58</label> </p> 
                                <p onclick="addQueryStr('site')"><label><input text="365" name="site" type="radio" value="365" >365</label> </p>
                                <p onclick="addQueryStr('site')"><label><input text="赶集" name="site" type="radio" value="ganji" >赶集</label> </p>
                                <p onclick="addQueryStr('site')"><label><input text="安居客" name="site" type="radio" value="anjuke" >安居客</label> </p>
                                <!-- <p onclick="addQueryStr('site')"><label><input text="百姓" name="site" type="radio" value="baixing" >百姓</label> </p> -->
                                <p onclick="addQueryStr('site')"><label><input text="搜房" name="site" type="radio" value="fang" >搜房</label> </p>
                            </div>
                          </li>
                        </c:if>
                   </ul>
                   
                   <ul class="InputMainLine KY_W">
                   
                        <li style="width:122px;"><input class="input-sm w110" type="text" placeholder="编号" name="id"  desc="编号"/><input class="input-sm w110" type="text" placeholder="路段" name="address" /></li>
                        <li>
                            <span class="SpanNameH2"><em>面积</em></span>
                            <div class="DivBoxW1 Fleft">
                            <input class="input-sm input-left inputBox" type="text" placeholder="" desc="面积" autocomplete="off" name="mjiStart" />
                            <input class="input-sm input-left inputBox" type="text" placeholder="" desc="面积" autocomplete="off" name="mjiEnd" />
                            </div>
                        </li>
                        <li>
                            <span class="SpanNameH2"><em>租金</em></span>
                            <div class="DivBoxW1 Fleft">
                            <input class="input-sm input-left inputBox" type="text" placeholder="" desc="租金" autocomplete="off" name="zjiaStart" />
                            <input class="input-sm input-left inputBox" type="text" placeholder="" desc="租金" autocomplete="off" name="zjiaEnd" />
                            </div>
                        </li>
                        <li>
                            <span class="SpanNameH2"><em>楼层</em></span>
                            <div class="DivBoxW1 Fleft">
                            <input class="input-sm input-left inputBox" type="text" placeholder="" desc="楼层" autocomplete="off" name="lcengStart" />
                            <input class="input-sm input-left inputBox" type="text" placeholder="" desc="楼层" autocomplete="off" name="lcengEnd" />
                            </div>
                        </li>
                        <li>
                            <span class="SpanNameH2"><em>建筑<br />年代</em></span>
                            <div class="DivBoxW1 Fleft">
                            <input class="input-sm input-left inputBox" type="text" placeholder="" autocomplete="off" name="yearStart" />
                            <input class="input-sm input-left inputBox" type="text" placeholder="" autocomplete="off" name="yearEnd" />
                            </div>
                        </li>
                        <li>
                            <span class="SpanNameH2"><em>发布<br />日期</em></span>
                            <div class="DivBoxW1 Fleft">
                            <input type="text" class="form-control input-sm" name="dateStart" id="idTimes" onFocus="var timeend=$dp.$('idTime');WdatePicker({lang:'zh-cn',onpicked:function(){idTime.focus();},maxDate:'#F{$dp.$D(\'idTime\')}'})" style="width:78px">
        <input type="text" class="form-control input-sm" name="dateEnd" id="idTime" onFocus="WdatePicker({lang:'zh-cn',minDate:'#F{$dp.$D(\'idTimes\')}'})" style="width:78px">
                            </div>
                        </li>
                        
                        <li class="LiBoxW1">
                             <button id="searchBtn" class="ButtonW1 ButtonS hand" type="submit">搜索</button>
                             <button class="ButtonW1 ButtonQ hand" type="button" onclick="window.location.reload();">清空</button>
                        </li>
                        
                   </ul>
                   
                </form>
              </div>
              
              
              
              <div class="MainRightConMain KY_W" style="display: table-row; overflow:hidden;">

                <div class="MainRightConL" style=" height:100%; display:table-cell; overflow:hidden; overflow-y:auto;margin-top:0px;">
                  <!-- <div class="sideCont" id="sideCont"></div> -->
                    <iframe id="sideCont" src="" style="width:100%;height:100%;border:none"></iframe>
                </div>

                <div class="MainRightConR" id="FY_RCon" style="margin:0px;overflow:hidden; overflow-y:hidden; display:table-cell; float:left;">
                  <div style="width:100%; display:table; height:100%;">
                                <div style="display:table-row;">
                                  <div class="MR_ConTit KY_W MR_ConTitW2" style=" display:table-cell; position:inherit;">
                                    <ul id="query_str">
                                    <li class="tit">筛选条件：</li>
                                    <span></span>
                                    </ul>
                                  </div>
                                </div>
                                <div style="display:table-row;">  
                                                  
                                      <div style=" width:100%; display:table-cell;">
                                     
                                             <table border="0" cellspacing="0" cellpadding="0" class="KY_TableMain" id="FY_TableTit">
                                                    <tr>
                                                      <th width="60">编号</th>
                                                      <c:if test="${authNames.contains('fy_sh') || authNames.contains('fy_edit') || authNames.contains('fy_del') || authNames.contains('fy_ruku')}">
                                                      <th width="75">操作</th>
                                                      </c:if>
                                                      <th width="50">状态</th>
                                                      <th width="60">区域</th>
                                                      <th style=" width:200px; min-width:50px;">楼盘名称</th>
                                                      <th width="50">来源</th>
                                                      <th width="50">楼型</th>
                                                      <th width="60">户型</th>
                                                      <th width="50">面积</th>
                                                      <th width="50">租金</th>
                                                      <th width="50">楼层</th>
                                                      <th width="50">装潢</th>
                                                      <th width="90">发布时间</th>
                                                    </tr>
                                              </table>
                                             
                                     
                                      </div>
                                  
                                  </div>
                                    
                                 <div style="display:table-row;">           

                                            <div class="FY_RCon" style=" width:100%; display:table-cell;">
                                                <div style="height:100%; float:left; overflow:hidden; overflow-y:auto;">
                                                    <table border="0" cellspacing="0" cellpadding="0" class="KY_TableMain TableB table-hover" id="KY_TableMain">
                                                        <tr data-hid="$[id]" style="display:none;" class="id_House_list" >
                                                          <td width="60"><span class="piliang hidden"><input type="checkbox" name="ids" value="$[id]" style="display:none"> </span>$[id]</td>
                                                          <c:if test="${authNames.contains('fy_sh') || authNames.contains('fy_edit') || authNames.contains('fy_del') || authNames.contains('fy_ruku')}">
                                                            <td  width="75">
                                                              <c:if test="${authNames.contains('fy_sh') }">
                                                              <a id="shenhe_$[id]" href="##" show="showAction($[cid],${cid},$[seeGX])" class="shenhe_$[sh]" data-hid="$[id]" runscript="true" data-rel="del" onclick="shenheFy($[id],this)">getShenHeText($[sh])</a>
                                                              </c:if>
                                                              <c:if test="${authNames.contains('fy_edit') }">
                                                              <a href="#" show="showAction($[cid],${cid},$[seeGX])" class="edit" data-hid="$[id]" onclick="houseEdit($[id])" data-rel="edit">改</a>
                                                              </c:if>
                                                              <c:if test="${authNames.contains('fy_del') }">
                                                              <a href="##" show="showAction($[cid],${cid},$[seeGX])" class="del" data-hid="$[id]" data-rel="del" onclick="deletehouse($[id])">删</a>
                                                              </c:if>
                                                              <c:if test="${authNames.contains('fy_ruku') }">
                                                              <a id="btn_ruku_$[id]" href="#" show="$[ruku]==0" onclick="ruku($[id])">入库</a>
                                                              </c:if>
                                                            </td>
                                                          </c:if>
                                                          <td width="50" class="ztai_$[ztai]">$[ztai]</td>
                                                          <td width="60">$[quyu]</td>
                                                          <!-- <td class="br_area">$[area] <span show="$[seeFH]==1">$[dhao]-$[fhao]</span></td> -->
                                                          <td class="br_area" style=" width:200px; min-width:50px;" align="left"><div style="padding:0 8px;">$[area] <span show="$[seeFH]==1">$[dhao]-$[fhao]</span></div></td>
                                                          <td width="60">$[site]</td>
                                                          <td width="50">$[lxing]</td>
                                                          <td width="60">$[hxf]-$[hxt]-$[hxw]</td>
                                                          <td width="50">$[mji]</td>
                                                          <td width="50">$[zjia]</td>
                                                          <td width="50">$[lceng]/$[zceng]</td>
                                                          <td width="50">$[zxiu]</td>
                                                          <td width="90" class="sy" runscript="true" title="$[dateadd]">'$[dateadd]'.split(' ')[0]</td>
                                                      </tr>
                                                    </table>
                                                </div>
                                            </div>
                                  </div>

                                <div style="display:table-row; height:35px; overflow:hidden;">     
                                     <div class="divPage"  style=" height:35px; margin:0; background:none;">  
                                        <div class="pageMain footer">
                                          <div class="maxHW mainCont ymx_page foot_page_box"></div>
                                        </div>
                                     </div>     
                                </div>
                        </div>
                   </div>

              </div>
              
           </div>
     
     </div>
     
     
     

</div>

</body>
</html>
