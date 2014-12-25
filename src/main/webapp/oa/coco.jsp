<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript" src="/oa/js/messagesBox.js"></script>
<script type="text/javascript" src="/oa/js/chat.js"></script>
<script type="text/javascript" charset="utf-8" src="/js/ueditor1_4_3/ueditor.config.js"></script>
<script type="text/javascript" charset="utf-8" src="/js/ueditor1_4_3/ueditor.all.js"> </script>
<script type="text/javascript" charset="utf-8" src="/js/ueditor1_4_3/lang/zh-cn/zh-cn.js"></script>
<script type="text/javascript">

$(function(){
    ue_text_editor = UE.getEditor('editor', {
        toolbars: [
            ['simpleupload','emotion','spechars','forecolor']
        ],
        autoHeightEnabled: false
    });
    ue_text_editor.addListener( 'ready', function( editor ) {
      ue_text_editor.document.onkeyup=function(e){
          msgAreaKeyup(e);
        }
    });
});

</script>
<div class="cocoMain" style="z-index:9999999">
     
    <div class="table w100 h100">
        
         <div class="tr w100">

              <div class="td cocoMainTit oaTitBgCoco">
                <div class=""><img src="/oa/images/avatar/${me.avatar}.jpg" id="avatarId" onclick="openNewWin('changeAvatar','660','修改头像','oa/avatar.jsp');" />
                    <div title="点击修改" class="mainInfo mainName" onclick="startChangeName()" id="user_name_div">${me.uname}</div>
                    <input id="user_name_input" style="display:none;margin-top:5px;" onblur="endChangeName();" />
                    <div class="mainInfo mainabout">${dname}</div>
                </div>
                <!-- <img src="/oa/images/coco.png" /> -->
            </div>
         </div>
         <div class="tr w100">
              <div class="td cocoMainSelect">
                   <span class="sle"><i class="Bg lxr"></i></span>
                   <span><i class="Bg qun"></i></span>
                   <span><i class="Bg ldq"></i></span>
              </div>
         </div>
         <div class="tr w100">
              <div class="td cocoMainCon">
                    
                    <div class="cocoMainConBox" style="height:100%; overflow:hidden; overflow-y:auto;">
                    
                         <ul class="cocoList" id="cocoList" >
                            <c:forEach items="${contacts}" var="contact">
                            	 <li onclick="openChat(${contact.uid},'${contact.uname }',${contact.avatar})">
	                                 <div id="user_avatar_${contact.uid}" class="cocoTx Fleft"><img src="/oa/images/avatar/${contact.avatar}.jpg" /></div>
	                                 <div class="cocoPerInfo Fleft">
	                                     <p class="name">${contact.uname }</p>
	                                     <p class="txt">${contact.dname }</p>
	                                 </div>
	                                 <div class='user_status_${contact.uid} <c:if test="${contact.online}">cocoOnline</c:if> <c:if test="${!contact.online}">cocoLeave</c:if> ' ></div>
                             	</li>
                            </c:forEach>
                            
                         
                         </ul>
                    
                    </div>
                    
              </div>
         </div>
         
         <div class="tr w100">
              <div class="td">
                   
                   <div onclick="recoverChatPanel();" class="cocoNews"><span class="name chat_title"></span><i class="Bg close"></i></div>
                   
              </div>
         </div>
         
         
    </div>

</div>

<div style=" position:absolute; bottom:0; right:0;cursor:pointer;width:50px; height:50px; overflow:hidden; z-index:9999999;">
     <span><img onclick=" $('.cocoMain').toggleClass('hide');" src="/style/images/litFox.png" width="50"></span>
</div>



<div class="cocoWin" id="layerBoxDj" style=" display:none;">

     <div class="cocoWintit" id="cocoWintit"><span class="chat_title"></span><i class="closeBg none" onclick="closeBox()" title="最小化"></i><i class="closeBg closeX" onclick="closeBox(closeAllChat)" title="关闭"></i></div>
     
     <div class="cocoWinContent">
     
          <div class="cocoWinContentLxr">
               
               <ul class="cocoWinLxrList" >
                                    
               </ul>
               
          </div>
          <div style=" width:510px; float:left; height:100%;">
              <div class="cocoWinInfoListShow" style="height:411px;">
              
                   
                   
              
              </div>
                <!-- <div id="facePanel" style="display: none;position:absolute;bottom:125px;border:1px solid #eee">
                    <c:forEach var="i"  begin="1"  end="60"  step="1">
                        <img src="http://forum.csdn.net/PointForum/ui/scripts/csdn/Plugin/001/face/${i}.gif" onclick="appendFaceToMsg(${i});" />
                    </c:forEach>
                </div> -->
              <div class="WinInfoSend">
                    
                    <!-- <div style="height:22px;"><img src="oa/images/face.gif"/ style="cursor:pointer;margin-left:5px;" onclick="showFacePanel()"></div> -->
                    <div class="WinInfoSendWrite">
                         <!-- <textarea id="msg_textarea" onkeyup="msgAreaKeyup();" class="fontStyleNewsWrite"></textarea> -->
                         <span id="editor" type="text/plain" name="conts" style="height:84px;width:100%"></span>
                    </div>
                    
                    <div class="WinInfoSendBtn">
                    
                         <button class="WinInfoSendBtnAddPhoto Fleft" title="选择图片"></button>
                         <button class="WinInfoSendBtnMessage Fleft" onclick="send();">发送</button>
                    
                    </div>
               
               </div>
           </div>
           
           
           
           <div class="qunBox">
               
               <div class="qunBoxTit"><span>群组成员</span></div>
               <div class="qunBoxList">
               
                   <ul>
                   
                       <li><div class="qunTxImg Fleft"><img src="/oa/images/avatar/1.jpg"></div><div class="qunLxrInfo Fleft"><p class="name">冷文卿</p></div></li>
                       <li><div class="qunTxImg Fleft"><img src="/oa/images/avatar/2.jpg"></div><div class="qunLxrInfo Fleft"><p class="name">慕容冲</p></div></li>
                       <li><div class="qunTxImg Fleft"><img src="/oa/images/avatar/3.jpg"></div><div class="qunLxrInfo Fleft"><p class="name">景茵梦</p></div></li>
                       <li><div class="qunTxImg Fleft"><img src="/oa/images/avatar/4.jpg"></div><div class="qunLxrInfo Fleft"><p class="name">柳婵诗</p></div></li>
                       <li><div class="qunTxImg Fleft"><img src="/oa/images/avatar/5.jpg"></div><div class="qunLxrInfo Fleft"><p class="name">夏舒征</p></div></li>
                       <li><div class="qunTxImg Fleft"><img src="/oa/images/avatar/6.jpg"></div><div class="qunLxrInfo Fleft"><p class="name">冷文卿</p></div></li>
                       <li><div class="qunTxImg Fleft"><img src="/oa/images/avatar/7.jpg"></div><div class="qunLxrInfo Fleft"><p class="name">慕容冲</p></div></li>
                       <li><div class="qunTxImg Fleft"><img src="/oa/images/avatar/8.jpg"></div><div class="qunLxrInfo Fleft"><p class="name">景茵梦</p></div></li>
                       <li><div class="qunTxImg Fleft"><img src="/oa/images/avatar/9.jpg"></div><div class="qunLxrInfo Fleft"><p class="name">柳婵诗</p></div></li>
                       <li><div class="qunTxImg Fleft"><img src="/oa/images/avatar/10.jpg"></div><div class="qunLxrInfo Fleft"><p class="name">夏舒征</p></div></li>
                   
                   </ul>
               
               </div>
          
          </div>
           
           
           
           
           
     </div>

</div>



