<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta name="description" content="中介宝房源软件系统">
<meta name="keywords" content="房源软件,房源系统,中介宝">
<link href="/style/css.css" rel="stylesheet">
<link href="/bootstrap/css/bootstrap.css" rel="stylesheet">
<link href="/style/style.css" rel="stylesheet">
<link href="/style/css_ky.css" rel="stylesheet">
<script src="/js/jquery.js" type="text/javascript"></script>
<script src="/bootstrap/js/bootstrap.js" type="text/javascript"></script>
<script src="/js/dialog/jquery.artDialog.source.js?skin=win8s" type="text/javascript"></script>
<script src="/js/dialog/plugins/iframeTools.source.js" type="text/javascript"></script>
<script src="/js/jquery.input.js" type="text/javascript"></script>
<script src="/js/jquery.j.tool.js" type="text/javascript"></script>
<script type="text/javascript" src="/js/buildHtml.js"></script>
<script type="text/javascript">
var houseGJbox;
chuzu=getParam('chuzu');
function replaceAlls(a){
  var aa=a;
  aa=aa.replace(/ /g, '');
  aa=aa.replace(/　/g, '');
  aa=aa.replace(/，/g, ',');
  aa=aa.replace(/、/g, ',');
  aa=aa.replace(/\//g, ',');
  return aa;
}
function reload_genjin(){
  window.location.reload();
}
function selectArea(area){
  $(window.parent.document).find('#search').val(area);
  window.parent.doSearchAndSelectFirst();
}
function sideBtnFun(){
    $('.sideCont').on('click', '.btns', function(event) {
        var Thi=$(this),ThiHid=Thi.data('hid'),ThiType=Thi.data('type');
        if(ThiType=='addGenjin'){
            if(houseGJbox)houseGJbox.close();
            houseGJbox=art.dialog({
                follow: document.getElementById('addGenjinBtn'),
                content: '加载中...',
                title:'添加跟进',
                left:20,
                id:'houseGenjinBox',
                width:'250px',
                padding:'0'
            });
            // jQuery ajax   
            YW.ajax({
                url: '/v/house/house_genjin_add_v2.html',
                mysuccess: function (data) {
                    //blockAlert(data)
                    houseGJbox.content(data);
                },cache: false
            });
            return false;
        }else if(ThiType=='fav'){
            var ThiIsFav=Thi.attr('fav'),
            url,param={
                houseId:$('#dimHouseId').text(),
                chuzu:chuzu
            }
            if(ThiIsFav=='1'){
              //表示当前点击按钮的作用是收藏房源,因此此时房源是未收藏状态
                if(chuzu=='1'){
                  url='addRent';
                }else{
                  url='addSell';
                }
                
                alertStr="收藏成功";
                dimStr='<i class="iconfont">&#xe62e;</i> 已收藏';
                Thi.attr('fav',0).attr('title','已收藏');
            }else{
                if(chuzu=='1'){
                  url='deleteRent';
                }else{
                  url='deleteSell';
                }
                
                alertStr="已取消收藏";
                dimStr='<i class="iconfont">&#xe648;</i> 收藏';
                Thi.attr('fav',1).attr('title','点此收藏');
            }
            YW.ajax({
                type: 'POST',
                url: '/c/house/fav/'+url,
                data:param,
                mysuccess: function(data){
                    //var json = JSON.parse(data);
                    alert(alertStr);
                    Thi.html(dimStr);
                },complete:function(){
                    
                }
            });
            return false;
        }else if(ThiType=='map'){
            ThiArea=Thi.data('area');
            art.dialog.open('/v/house/baidu_map.html?area='+ThiArea,{id:'seemap',width:800,height:600});
            return false;
        }else if(ThiType=='copy'){
            alert('不给复制');
            return false;
        }else{
            return false;
        }
    });
}
$(document).ready(function() {
    sideBtnFun();
    $('[data-toggle=tooltip]').tooltip();
        var lxr='${house.lxr}',
        tel='${house.tel}';
    if($('.telBox').length>0){
        var telArr=new Array(),
        lxrArr=new Array();
        if(tel.indexOf('http')<0){
        	tel=replaceAlls(tel);
        }
        telArr=tel.split(',');
        lxr=replaceAlls(lxr);
        lxrArr=lxr.split(',');
        var ThiBox=$('.telBox');
        // blockAlert(ThiBox.attr('seeHM'));
        ThiBox.html('');
        ThiBox.prepend('<div class="alert alert-success btn-xs"></div>');
        var ThiBoxBtn=ThiBox.find('.telSee'),
        ThiBoxDiv=ThiBox.find('div')
        ThiBoxDiv.css({'display':'block','overflow':'hidden'});
        $('.telBox').find('p:last-child').css({'margin':'0'});
        if(ThiBox.attr('seeHM')=="0"){
          ThiBoxDiv.removeClass('alert-success');
          ThiBoxDiv.addClass('alert-danger');
          ThiBoxDiv.html('未共享，联系业务员');
        }else{
          ThiBoxDiv.html('点此查看房主资料').addClass('onOpen');
          var TelBoxStr='';
          $.each(telArr, function(index, val) {
          var thiTel=telArr[index],thiLxr='';
              if(lxrArr.length>index){
                  thiLxr=lxrArr[index]
              }else{
                  thiLxr=lxrArr[0]
              }
              if(thiTel.indexOf('http')>-1){
            	  TelBoxStr=TelBoxStr+'<p class="onselect"><span class="lxr">'+ thiLxr +'</span> <img src="'+thiTel+'"/> </p>';
              }else{
            	  TelBoxStr=TelBoxStr+'<p class="onselect"><span class="lxr">'+ thiLxr +'</span> <span class="tel">'+ thiTel +'</span> <span class="telFrom"></span> <span class="baidu iconfont" data-toggle="tooltip" style="cursor:pointer" title="百度">&#xe64a;</span></p>';  
              }
              
          });
          TelBoxStr=TelBoxStr+'<i>^</i>'
        };
        ThiBox.on('click', 'i,.onOpen', function(event) {
//          alert(ThiBox.find('p').length)
            if(ThiBox.find('p').length<1){
                ThiBoxDiv.html(TelBoxStr).removeClass('onOpen');
                ThiBoxDiv.find('p').each(function(index, el) {
                    var Thisa=$(this),
                    ThiTel=Thisa.find('.tel').text(),
                    ThiTelFrom=Thisa.find('.telFrom');
                    getTelFrom(ThiTel,Thisa,function(e){
                        ThiTelFrom.html('['+ e +']');
                    });
                });
                $('[data-toggle=tooltip]').tooltip();
            }else{
              //blockAlert($(this).find('p').length)
                ThiBoxDiv.html('点此查看房主资料').addClass('onOpen');
            }
            return false;
        });
        ThiBox.on('click', 'span.baidu', function(event) {
            var ThiTel=$(this).parent().find('.tel').html();
            // art.dialog.open('http://www.ip138.com:8080/search.asp?mobile='+ThiTel+'&action=mobile',{
            //     title:'手机号码归属搜索',
            //     height:230,
            //     width:400
            // });
            window.top.gui.Shell.openExternal('http://www.baidu.com/s?wd='+ThiTel);
            // art.dialog.open('http://www.baidu.com/s?wd='+ThiTel,{
            //   title:'百度搜索',
            //   height:'80%',
            //   width:'80%'
            // });
            return false;
        });
    }
    var GenjinTbody=$('.see_house_genjin').find('tbody'),
    GenjinTbodyHtml=GenjinTbody.html().replace(/(\n)+|(\r\n)+/g, "");
    GenjinTbodyHtml=GenjinTbodyHtml.replace(/ /g,'');

    if(GenjinTbodyHtml==''){
    	$('.see_house_genjin').find('tbody').html('<tr><td style="padding:3px;"><img src="../../style/images/zjb.png" style="width:240px;"/></td></tr>');
      $('#addGenjinBtn').tooltip('show');
    }

});
$('#area').on('click',function(){
  var Thi=$(this),
  ThiVal=Thi.find('b').text();
  $('#search').val(ThiVal);
  $('#searchBtn').click();
})
</script>
</head>
<body >
<div class="sideCont" style="width:100%; display:table; height:100%;-webkit-user-select: text;">

     <div style="display:table-row;">   
        <div class="sideHead TableMainLeftTit" style="display:table-cell;">
            房源详细
        </div>
     </div>
        
        
     <div style="display:table-row;">     
        <div style="display:table-cell; width:100%;">
        <div style="height:100%; overflow:hidden; overflow-y:auto;">
        <table width="100%" class="TableMainL" id="seeHouse ">
        <tbody>
          <tr>
            <td class="Zuo">
              <table width="100%" border="0" cellspacing="0" cellpadding="0" class="zhuyao">
                <tr>
                  <td class="biaoti">编号：</td>
                  <td class="neirong TextColor1" id="dimHouseId">${house.id}</td>
                </tr>
              </table>
            </td>
            <td class="You">
              <table width="100%" border="0" cellspacing="0" cellpadding="0" class="zhuyao">
                <tr>
                  <td class="biaoti">状态：</td>
                  <td class="neirong TextColor1">${ztai} &nbsp;</td>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <td colspan="2" class="Zuo">
              <table width="100%" border="0" cellspacing="0" cellpadding="0" class="zhuyao">
                <tr>
                  <td class="biaoti biaotiMax">发布人员：</td>
                  <td class="neirong TextColor1">${dept.namea}-${fbr.uname}</td>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <td colspan="2" class="Zuo">
              <table width="100%" border="0" cellspacing="0" cellpadding="0" class="zhuyao">
                <tr>
                  <td class="biaoti biaotiMax">发布时间：</td>
                  <td class="neirong TextColor1"><fmt:formatDate value="${house.dateadd}" pattern="yyyy-MM-dd HH:mm"/>&nbsp;</td>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <td colspan="2" class="Zuo">
              <table width="100%" border="0" cellspacing="0" cellpadding="0" class="zhuyao">
                <tr>
                  <td class="biaoti" selectstart="yes">楼盘：</td>
                  <td class="neirong"><span onclick="selectArea('${house.area}')" class="onselect" id="area" style=" cursor: pointer; color:#06C; font-weight:bold; font-size:14px;text-decoration: underline;"><b>${house.area}</b> ${house.dhao}#${house.fhao }</span></td>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <td class="Zuo">
              <table width="100%" border="0" cellspacing="0" cellpadding="0" class="zhuyao">
                <tr>
                  <td class="biaoti">面积：</td>
                  <td class="neirong TextColor1"><span style=" color:#F00; font-weight:bold;">${house.mji}</span>㎡</td>
                </tr>
              </table>
              <table width="100%" border="0" cellspacing="0" cellpadding="0" class="zhuyao">
                <tr extend_id="djia">
                  <td class="biaoti">单价：</td>
                  <td class="neirong TextColor1">
                  ${house.djia} 元
                  </td>
                </tr>
              </table>
              <table width="100%" border="0" cellspacing="0" cellpadding="0" class="zhuyao">
                <tr>
                  <td class="biaoti">楼型：</td>
                  <td class="neirong TextColor1">${house.lxing} &nbsp;</td>
                </tr>
              </table>
              <table width="100%" border="0" cellspacing="0" cellpadding="0" class="zhuyao">
                <tr>
                  <td class="biaoti">装潢：</td>
                  <td class="neirong TextColor1">${house.zxiu} &nbsp;</td>
                </tr>
              </table></td>
            <td class="You">
              <table width="100%" border="0" cellspacing="0" cellpadding="0" class="zhuyao">
                <tr extend_id="zjia">
                  <td class="biaoti">总价：</td>
                  <td class="neirong TextColor1">
                  <span style=" color:#F00; font-weight:bold;">${house.zjia}</span> 万元
                  </td>
                </tr>
              </table>
              <table width="100%" border="0" cellspacing="0" cellpadding="0" class="zhuyao">
                <tr>
                  <td class="biaoti">楼层：</td>
                  <td class="neirong TextColor1">${house.lceng}/${house.zceng} &nbsp;</td>
                </tr>
              </table>
              <table width="100%" border="0" cellspacing="0" cellpadding="0" class="zhuyao">
                <tr>
                  <td class="biaoti">户型：</td>
                  <td class="neirong TextColor1">${house.hxf}室${house.hxt}厅${house.hxw}卫 &nbsp;</td>
                </tr>
              </table>
              <table width="100%" border="0" cellspacing="0" cellpadding="0" class="zhuyao">
                <tr>
                  <td class="biaoti">建于：</td>
                  <td class="neirong TextColor1">${house.dateyear} 年 &nbsp;</td>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <td colspan="2" class="Zuo">
              <table width="100%" border="0" cellspacing="0" cellpadding="0" class="zhuyao">
                <tr>
                  <td class="biaoti">区域：</td>
                  <td class="neirong TextColor1">${house.quyu} &nbsp;</td>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <td colspan="2" class="Zuo">
              <table width="100%" border="0" cellspacing="0" cellpadding="0" class="zhuyao">
                <tr>
                  <td class="biaoti">地址：</td>
                  <td class="neirong TextColor1">${house.address} &nbsp;</td>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <td colspan="2" class="Zuo">
              <table width="100%" border="0" cellspacing="0" cellpadding="0" class="zhuyao">
                <tr>
                  <td class="biaoti">其它：</td>
                  <td class="neirong TextColor1" style="padding-right:10px;">${house.beizhu} &nbsp;</td>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <td colspan="2" class="Zuo">
            
              <table width="100%" border="0" cellspacing="0" cellpadding="0" class="zhuyao">
                <tr>
                  <td>
                    <table width="100%">
                      <tr>
                        <td class="biaoti">房主：</td>
                        <td class="neirong telBox" seeHM="${house.seeHM}"></td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
              <table width="100%" border="0" cellspacing="0" cellpadding="0" class="zhuyao">
                <tr>
                  <td class="biaoti biaotiMax">业务人员：</td>
                  <td class="neirong TextColor1">${house.forlxr} ${house.fortel}&nbsp;</td>
                </tr>
              </table>
            </td>
          </tr>
        </tbody>
        </table>
        
        <table class="TableMainLGj see_house_genjin" width="100%">
          <thead>
            <tr>
              <th><h2>跟进列表</h2></th>
            </tr>
          </thead>
          <tbody>
          <c:forEach items="${gjList }" var="gj">
          	<tr class="list">
              <td style="padding-left:5px;padding-right:5px">
                <div style="margin-bottom:4px;">${gj.conts}</div>
                <p style="display:inline-block;width:110px;white-space:nowrap;text-overflow:ellipsis;overflow:hidden"><span title="${gj.uname}">${gj.dname}-${gj.uname}</span></p> <span style="float:right;color:#999999"><fmt:formatDate value="${gj.addtime }" pattern="yyyy-MM-dd HH:mm"/></span>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
        </div>
        </div>
     </div>
        
        
        
        
     <div style="display:table-row; height:30px; overflow:hidden;">     
        <div class="sideFoot" style="display:table-cell; height:32px; overflow:hidden;">
        
          <ul class="bottomAction" style="margin-bottom:-3px;">
            <li>
              <a href="#" data-toggle="tooltip" title="添加跟进" class="btns" data-type="addGenjin" id="addGenjinBtn"><i class="iconfont">&#xe640;</i> 添加跟进</a>
              <c:if test="${fav eq '0'}">
              <a href="#" data-toggle="tooltip" title="收藏房源" class="btns" data-type="fav" fav="1"><i fav="1" class="iconfont">&#xe648;</i> 收藏</a>
              </c:if>
              <c:if test="${fav eq '1' }">
              <a href="#" data-toggle="tooltip" title="已收藏房源" class="btns" data-type="fav" fav="0"><i fav="0" class="iconfont">&#xe62e;</i> 已收藏</a>
              </c:if>
              <a href="#" data-toggle="tooltip" title="地图查看" class="btns" data-type="map" data-area="${house.area}"><i class="iconfont">&#xe60e;</i> 地图</a>
            </li>
          </ul>
        </div>
     </div>


</div>
</body>
</html>

