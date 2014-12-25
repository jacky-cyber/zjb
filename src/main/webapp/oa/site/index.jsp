<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript">
var saveSiteUrl="";
var siteAreaId = "share";
function showSites(id){
	siteAreaId = id;
	if(id=='personal'){
		$('#'+id).css('display','');
		$('#share').css('display','none');
		saveSiteUrl="/c/oa/site/addMy"
	}else{
		$('#'+id).css('display','');
		$('#personal').css('display','none');
		saveSiteUrl="/c/oa/site/addShare";
	}
	$('.weblineTit span').toggleClass('sel');
}

function addSite(){
	var a=$('form[name=siteForm]').serialize();
	YW.ajax({
	    type: 'POST',
	    url: saveSiteUrl,
	    data:a,
	    mysuccess: function(data){
	    	alert('保存成功');
	    	var len = $('#'+siteAreaId).children().length;
	    	len++;
	    	var html = '<a href="'+$('#site_url').val()+'" class="a green marginLeft15 color_'+len%4+'">'+$('#site_title').val()+'</a>';
	    	$('#'+siteAreaId).append(html);
	    	$('form input').val('');
	    }
	});
}

function editSite(a){
	if (a==0) {
		$('.site_del').css('display', '');
		$('.editSite').css('display', '');
		$('.over').css('display', 'none');
	}else {
		$('.site_del').css('display', 'none');
		$('.over').css('display', '');
		$('.editSite').css('display', 'none');
	}
}

function delSite(site,id){
			event.preventDefault();
	YW.ajax({
	    type: 'POST',
	    url: '/c/oa/site/delete?id='+id,
	    mysuccess: function(data){
			$(site).parent().remove();
	    	alert('删除成功');
	    }
	});
}

</script>
<div class="webline">
    <div class="weblineTit"><span onclick="showSites('share')" class="sel">公共导航网址</span><span onclick="showSites('personal')">我的个人网址</span><a href="#" onclick="editSite(0);" class="marginRight15 over">编辑</a><a href="#" onclick="editSite(1);" class="marginRight15 editSite" style="display:none">完成</a></div>
    <div class="webLineCon" id="share">
    	<c:forEach items="${shareList}"  var="site" varStatus="status">
    		<a href="${site.url }" class="a grey marginLeft15 color_${status.count%4}">${site.title } <i class="site_del" style="display:none" onclick="delSite(this,${site.id});" >×</i></a>
    	</c:forEach>
    </div>
    <div class="webLineCon" id="personal" style="display:none">
         <c:forEach items="${personalList}"  var="site" varStatus="status">
    		<a href="${site.url }" class="a grey marginLeft15 color_${status.count%4}">${site.title } <i class="site_del" style="display:none" onclick="delSite(this,${site.id});" >×</i></a>
    	</c:forEach>
    </div>
    <div class="weblineAdd">
    
    	<form name="siteForm" onsubmit="return false;">
         <span class="txt" style="width:45px;">名称：</span><input type="text" id="site_title" class="inputBox" name="title" style=" width:28%;" />
         <span class="txt" style="width:45px;">网址：</span><input type="text" id="site_url" class="inputBox" name="url" style=" width:28%;" />
         <button class="btn" onclick="addSite()"></button>
    	</form>
    </div>
</div>


