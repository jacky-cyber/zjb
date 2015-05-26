package com.youwei.zjb.house;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.bc.sdak.CommonDaoService;
import org.bc.sdak.GException;
import org.bc.sdak.Page;
import org.bc.sdak.TransactionalServiceHelper;
import org.bc.sdak.utils.JSONHelper;
import org.bc.sdak.utils.LogUtil;
import org.bc.web.ModelAndView;
import org.bc.web.Module;
import org.bc.web.PlatformExceptionType;
import org.bc.web.ThreadSession;
import org.bc.web.WebMethod;
import org.hibernate.exception.SQLGrammarException;

import com.cloopen.rest.sdk.CCPRestSDK;
import com.youwei.zjb.KeyConstants;
import com.youwei.zjb.house.entity.House;
import com.youwei.zjb.house.entity.HouseOwner;
import com.youwei.zjb.house.entity.HouseTel;
import com.youwei.zjb.house.entity.TelVerifyCode;
import com.youwei.zjb.sys.CityService;
import com.youwei.zjb.sys.OperatorService;
import com.youwei.zjb.sys.OperatorType;
import com.youwei.zjb.util.SecurityHelper;

@Module(name="/weixin/houseOwner")
public class HouseOwnerService {

	CommonDaoService dao = TransactionalServiceHelper.getTransactionalService(CommonDaoService.class);
	
	OperatorService operService = TransactionalServiceHelper.getTransactionalService(OperatorService.class);
	
	HouseService houseService = TransactionalServiceHelper.getTransactionalService(HouseService.class);
	
	CityService cityService = new CityService();
	@WebMethod
	public ModelAndView sendVerifyCode(String tel){
		ModelAndView mv = new ModelAndView();
		TelVerifyCode tvc = new TelVerifyCode();
		tvc.tel = tel;
		//send code to tel
		tvc.sendtime =  new Date();
        int max=9999;
        int min=1000;
        Random random = new Random();
        int s = random.nextInt(max)%(max-min+1) + min;
        String code = String.valueOf(s);
        tvc.code = code ;

		HashMap<String, Object> result = null;

		CCPRestSDK restAPI = new CCPRestSDK();
		restAPI.init("sandboxapp.cloopen.com", "8883");// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
		restAPI.setAccount("8a48b5514d0427c1014d0429646a0002", "78a9ff1208304b949309f117a63f1d9b");// 初始化主帐号名称和主帐号令牌
		restAPI.setAppId("aaf98f894d328b13014d65d868e1242a");// 初始化应用ID
		result = restAPI.sendTemplateSMS(tvc.tel,"1" ,new String[]{tvc.code,"5"});

		System.out.println("SDKTestGetSubAccounts result=" + result);
		if("000000".equals(result.get("statusCode"))){
			//正常返回输出data包体信息（map）
			HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
			Set<String> keySet = data.keySet();
			for(String key:keySet){
				Object object = data.get(key);
				System.out.println(key +" = "+object);
			}
		}else{
			//异常返回输出错误码和错误信息
			System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
		}
		dao.saveOrUpdate(tvc);
		HouseOwner owner = dao.getUniqueByKeyValue(HouseOwner.class, "tel", tel);
		if(owner == null){
			owner = new HouseOwner();
			owner.tel = tel;
			owner.sh=0;
		}
		owner.verified = 0;
//		owner.pwd = SecurityHelper.Md5(pwd);
		dao.saveOrUpdate(owner);
		
		return mv;
	}
	
	@WebMethod
	public ModelAndView houses(){
		ModelAndView mv = new ModelAndView();
		//判断session中没有房主信息
		HouseOwner owner = (HouseOwner)ThreadSession.getHttpSession().getAttribute(KeyConstants.Session_House_Owner);
//		if(owner==null){
//			Cookie[] cookies = ThreadSession.HttpServletRequest.get().getCookies();
//    		if(cookies==null){
//    			mv.redirect="login.jsp";
//				return mv;
//    		}
//    		
//    		for(Cookie cookie : cookies){
//    			if("tel".equals(cookie.getName())){
//    				tel = cookie.getValue();
//    			}
//    		}
////			pwd = SecurityHelper.Md5(pwd);
//			HouseOwner po = dao.getUniqueByParams(HouseOwner.class, new String[]{"tel"}, new Object[]{tel});
//			if(po==null){
//				//TODO 信息不正确重新登录
//				mv.redirect="login.jsp";
//				return mv;
//			}else{
//				ThreadSession.getHttpSession().setAttribute(KeyConstants.Session_House_Owner, po);
//			}
//		}else{
//			tel = owner.tel;
//		}
		try{
			List<House> houses = dao.listByParams(House.class, "from House where seeGX=1 and tel like ? and isdel<> 1 ", "%"+owner.tel+"%");
			mv.jspData.put("houses", houses);
			String cityInSession = (String)ThreadSession.getHttpSession().getAttribute("city");
			JSONArray citys = cityService.getCitys();
			for(int i=0;i<citys.size();i++){
				if(citys.getJSONObject(i).getString("py").equals(cityInSession)){
					mv.jspData.put("city", citys.getJSONObject(i).getString("name"));
					break;
				}
			}
		}catch(SQLGrammarException ex){
			if(ex.getCause().getMessage().contains("登录失败")){
				mv.redirect="citys.jsp";
			}
		}
		return mv;
	}
	
	@WebMethod
	public ModelAndView add(){
		ModelAndView mv = new ModelAndView();
		mv.jspData.put("zxius", ZhuangXiu.toList());
		mv.jspData.put("lxings", LouXing.toList());
		mv.jspData.put("hxings", FangXing.toList());
		mv.jspData.put("quyus", QuYu.toList());
		return mv;
	}
	
	@WebMethod
	public ModelAndView citys(){
		ModelAndView mv = new ModelAndView();
		cityService.getCitys();
		mv.jspData.put("citys", cityService.getCitys());
		return mv;
	}
	
	@WebMethod
	public ModelAndView edit(Integer id){
		ModelAndView mv = new ModelAndView();
		House po = dao.get(House.class, id);
		if(po!=null){
			mv.jspData.put("house", po);
		}
		FangXing fxing = FangXing.parse(po.hxf, po.hxt,po.hxw);
		mv.data = JSONHelper.toJSON(po);
		if(fxing!=null){
			mv.data.put("fxing", fxing.getName());
		}else{
			LogUtil.warning("房源的户型信息错误,hid="+id);
			mv.data.put("fxing", FangXing.房2厅1卫1.getName());
		}
		mv.jspData.put("zxius", ZhuangXiu.toList());
		mv.jspData.put("lxings", LouXing.toList());
		mv.jspData.put("hxings", FangXing.toList());
		mv.jspData.put("quyus", QuYu.toList());
		return mv;
	}
	
	@WebMethod
	public ModelAndView doLogin(String tel , String pwd){
		ModelAndView mv = new ModelAndView();
		pwd = SecurityHelper.Md5(pwd);
		ThreadSession.getCityPY();
		HouseOwner po = dao.getUniqueByParams(HouseOwner.class, new String[]{"tel" , "pwd"}, new Object[]{tel , pwd});
		if(po==null){
			throw new GException(PlatformExceptionType.BusinessException,"账号或密码不正确");
		}
		ThreadSession.getHttpSession().setAttribute(KeyConstants.Session_House_Owner, po);
		
//		Cookie cookieTel = new Cookie("tel", tel);
//		cookieTel.setMaxAge(-1);
//		response.addCookie(cookieTel);
		return mv;
	}
	
	@WebMethod
	public ModelAndView logout(){
		ModelAndView mv = new ModelAndView();
		ThreadSession.getHttpSession().removeAttribute(KeyConstants.Session_House_Owner);
		mv.redirect="/weixin/houseOwner/login.jsp";
//		mv.jsp = "/weixin/houseOwner/login.jsp";
		return mv;
	}
	
	@WebMethod
	public ModelAndView listData(Page<HouseOwner> page){
		ModelAndView mv = new ModelAndView();
		page = dao.findPage(page, "from HouseOwner ");
		mv.data.put("page", JSONHelper.toJSON(page));
		return mv;
	}
	
	@WebMethod
	public ModelAndView toggleShenHe(Integer id){
		ModelAndView mv = new ModelAndView();
		HouseOwner owner = dao.get(HouseOwner.class, id);
		if(owner==null){
			return mv;
		}
		if(owner.sh== null || owner.sh==0){
			owner.sh=1;
		}else{
			owner.sh=0;
		}
		dao.saveOrUpdate(owner);
		mv.data.put("sh", owner.sh);
		return mv;
	}
	
	@WebMethod
	public ModelAndView verifyCode(String tel , String code , String pwd){
		ModelAndView mv = new ModelAndView();
		TelVerifyCode tvc = dao.getUniqueByParams(TelVerifyCode.class, new String[]{"tel","code" },  new Object[]{tel , code});
		if(tvc==null){
			//验证码不正确
			throw new GException(PlatformExceptionType.BusinessException,"验证码不正确");
		}
		if(System.currentTimeMillis() - tvc.sendtime.getTime()>300*1000){
			//验证码已经过期
			throw new GException(PlatformExceptionType.BusinessException,"验证码已经过期");
		}
		HouseOwner owner  = dao.getUniqueByKeyValue(HouseOwner.class,"tel", tel);
		owner.verified = 1;
		owner.pwd = SecurityHelper.Md5(pwd);
		dao.saveOrUpdate(owner);
		ThreadSession.getHttpSession().setAttribute(KeyConstants.Session_House_Owner, owner);
		return mv;
	}
	
	@WebMethod
	public ModelAndView editHouse(Integer id){
		ModelAndView mv = new ModelAndView();
		House po = dao.get(House.class, id);
		if(po==null){
			//房源信息不存在
		}
		mv.jspData.put("house", po);
		return mv;
	}
	
	@WebMethod
	public ModelAndView deleteHouse(Integer id){
		ModelAndView mv = new ModelAndView();
		House po = dao.get(House.class, id);
		if(po==null){
			//房源信息不存在
		}
		po.isdel = 1;
		dao.saveOrUpdate(po);
		return mv;
	}
	
	@WebMethod
	public ModelAndView addHouse(House house , String hxing){
		HouseOwner owner = (HouseOwner)ThreadSession.getHttpSession().getAttribute(KeyConstants.Session_House_Owner);
		ModelAndView mv = new ModelAndView();
		validte(house);
		house.isdel = 0;
		house.dateadd = new Date();
		house.ztai = SellState.在售.getCodeString();
		//house.uid = user.id;
		house.cid = 1;
		house.did = 0;
		house.sh = 0;
		FangXing fx = FangXing.parse(hxing);
		house.hxf = fx.getHxf();
		house.hxt = fx.getHxt();
		house.hxw = fx.getHxw();
		if(house.mji!=null && house.mji!=0){
			int jiage = (int) (house.zjia*10000/house.mji);
			house.djia = (float) jiage;
		}
		house.seeFH=1;
		house.seeHM=1;
		house.seeGX=1;
		String nbsp = String.valueOf((char)160);
		house.tel = owner.tel;
		dao.saveOrUpdate(house);
		if(StringUtils.isNotEmpty(house.tel)){
			String[] arr = house.tel.split("/");
			for(String tel : arr){
				tel = tel.trim().replace(nbsp, "");
				HouseTel ht = new HouseTel();
				ht.hid = house.id;
				ht.tel = tel;
				dao.saveOrUpdate(ht);
			}
		}
		mv.data.put("msg", "发布成功");
		mv.data.put("result", 0);
		
		String operConts = "[房主添加了房源["+house.area+"],id="+house.id+",seeGX="+house.seeGX;
		operService.add(OperatorType.房源记录, operConts);
		return mv;
	}
	
	@WebMethod
	public ModelAndView updateHouse(House house , String hxing){
		validte(house);
		ModelAndView mv = new ModelAndView();
		House po = dao.get(House.class, house.id);
		po.area = house.area;
		po.address = house.address;
		po.ztai = house.ztai;
		po.dhao = house.dhao;
		po.fhao = house.fhao;
		po.quyu= house.quyu;
		po.lceng = house.lceng;
		po.zceng = house.zceng;
		po.lxing = house.lxing;
		po.mji = house.mji;
		po.zjia =house.zjia;
		FangXing fx = FangXing.parse(hxing);
		po.hxf = fx.getHxf();
		po.hxt = fx.getHxt();
		po.hxw = fx.getHxw();
		po.dateyear = house.dateyear;
		po.zxiu = house.zxiu;
		po.lxr = house.lxr;
		po.beizhu = house.beizhu;
		if(po.mji!=null && po.mji!=0){
			int jiage = (int) (po.zjia*10000/house.mji);
			po.djia = (float) jiage;
		}
		dao.saveOrUpdate(po);
		String operConts = "[房主修改了房源["+house.id+"]";
		operService.add(OperatorType.房源记录, operConts);
		mv.data.put("msg", "修改成功");
		//TODO 是否增加跟进信息
		return mv;
	}
	
	private void validte(House house){
		if(house.fhao==null){
			throw new GException(PlatformExceptionType.ParameterMissingError,"fhao","房号不能为空");
		}
		if(house.dhao==null){
			throw new GException(PlatformExceptionType.ParameterMissingError,"dhao","栋号不能为空");
		}
		if(house.mji==null){
			throw new GException(PlatformExceptionType.ParameterMissingError,"mji","面积不能为空");
		}
		if(house.zceng==null){
			throw new GException(PlatformExceptionType.ParameterMissingError,"zceng","总层不能为空");
		}
		
		if(house.zjia==null){
			throw new GException(PlatformExceptionType.ParameterMissingError,"zjia","总价不能为空");
		}
	}
}
