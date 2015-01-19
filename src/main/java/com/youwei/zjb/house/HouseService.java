package com.youwei.zjb.house;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.bc.sdak.CommonDaoService;
import org.bc.sdak.GException;
import org.bc.sdak.Page;
import org.bc.sdak.TransactionalServiceHelper;
import org.bc.sdak.utils.HqlHelper;
import org.bc.sdak.utils.LogUtil;
import org.bc.web.DateSeparator;
import org.bc.web.ModelAndView;
import org.bc.web.Module;
import org.bc.web.PlatformExceptionType;
import org.bc.web.WebMethod;

import com.youwei.zjb.ThreadSessionHelper;
import com.youwei.zjb.house.entity.District;
import com.youwei.zjb.house.entity.House;
import com.youwei.zjb.house.entity.HouseTel;
import com.youwei.zjb.sys.OperatorService;
import com.youwei.zjb.sys.OperatorType;
import com.youwei.zjb.user.UserHelper;
import com.youwei.zjb.user.entity.User;
import com.youwei.zjb.util.DataHelper;
import com.youwei.zjb.util.JSONHelper;

@Module(name="/house/")
public class HouseService {

	CommonDaoService dao = TransactionalServiceHelper.getTransactionalService(CommonDaoService.class);
	
	OperatorService operService = TransactionalServiceHelper.getTransactionalService(OperatorService.class);
	
	@WebMethod
	public ModelAndView exist(String area, String dhao , String fhao , String seeGX){
		ModelAndView mv = new ModelAndView();
		StringBuilder hql = new StringBuilder();
		hql.append("from House where area = ? and dhao = ? and fhao = ? ");
		List<Object> params = new ArrayList<Object>();
		params.add(area);
		params.add(dhao);
		params.add(fhao);
		if(StringUtils.isEmpty(seeGX) || "0".equals(seeGX)){
			hql.append(" and cid= ? ");
			params.add(ThreadSessionHelper.getUser().cid);
		}else{
			hql.append(" and (seeGX=1 or cid=?)");
			params.add(ThreadSessionHelper.getUser().cid);
		}
		List<House> list = dao.listByParams(House.class, hql.toString(), params.toArray());
		if(list==null || list.isEmpty()){
			mv.data.put("exist", "0");
		}else{
			mv.data.put("exist", "1");
			mv.data.put("hid", list.get(0).id);
		}
		return mv;
	}
	@WebMethod
	public ModelAndView add(House house , String hxing){
		ModelAndView mv = new ModelAndView();
		validte(house);
		User user = ThreadSessionHelper.getUser();
		house.isdel = 0;
		house.dateadd = new Date();
		house.uid = user.id;
		house.cid = user.cid;
		house.did = user.did;
		house.sh = 0;
		FangXing fx = FangXing.parse(hxing);
		house.hxf = fx.getHxf();
		house.hxt = fx.getHxt();
		house.hxw = fx.getHxw();
		if(house.mji!=null && house.mji!=0){
			int jiage = (int) (house.zjia*10000/house.mji);
			house.djia = (float) jiage;
		}
		if(house.seeFH==null){
			house.seeFH=0;
		}
		if(house.seeHM==null){
			house.seeHM=0;
		}
		if(house.seeGX==null){
			house.seeGX=0;
		}
		String nbsp = String.valueOf((char)160);
		if(StringUtils.isNotEmpty(house.tel)){
			house.tel = house.tel.trim().replace(nbsp, "");
		}
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
		
		String operConts = "["+user.Department().namea+"-"+user.uname+ "] 添加了房源["+house.area+"],id="+house.id+",seeGX="+house.seeGX;
		operService.add(OperatorType.房源记录, operConts);
		
		try{
			addDistrictIfNotExist(house);
		}catch(Exception ex){
			LogUtil.log(Level.WARN,"add district of house failed,hid= "+house.id,ex);
		}
		return mv;
	}
	
	private void addDistrictIfNotExist(House house){
		User u = ThreadSessionHelper.getUser();
		
		//检查楼盘是否在楼盘字典中，如果没有，则添加
		String hql = "from District  where name = ? and address=?";
		List<District> list = dao.listByParams(District.class, hql, house.area,house.address);
		if(list.isEmpty()){
			if(u.cid!=1){
				//只有中介宝用户才可以
				setMessageToMetis("出现新的楼盘: "+house.id+","+house.area+","+house.quyu+","+house.address);
				return;
			}
			District d= new District();
			d.address = house.address;
			d.name = house.area;
			d.addtime = new Date();
			d.quyu = house.quyu;
			d.pinyin=DataHelper.toPinyin(d.name);
			d.pyShort=DataHelper.toPinyinShort(d.name);
			d.sh=0;
			dao.saveOrUpdate(d);
		}
	}
	
	public void setMessageToMetis(final String msg){
		
		Thread t = new Thread(){
			@Override
			public void run() {
				try{
					URL url = new URL("http://60.169.1.32:8888/chat");
//					URL url = new URL("http://localhost:8888/chat");
					HttpURLConnection http = (HttpURLConnection) url.openConnection();
					http.setRequestMethod("POST");
					http.setConnectTimeout(0);
					http.setInstanceFollowRedirects(true);
					http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
					http.setDefaultUseCaches(false);
					http.setDoOutput(true);
					
					PrintWriter out = new PrintWriter(http.getOutputStream());
					out.print("qq=253187898&msg="+msg);//传入参数
					out.close();
					http.connect();//连接
					http.getInputStream();//返回流
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		};
		t.start();
	}
	
	@WebMethod
	public ModelAndView update(House house , String hxing){
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
		po.forlxr = house.forlxr;
		po.fortel = house.fortel;
		if(house.seeFH==null){
			house.seeFH=0;
		}
		if(house.seeGX==null){
			house.seeGX=0;
		}
		if(house.seeHM==null){
			house.seeHM=0;
		}
		po.beizhu = house.beizhu;
		po.seeFH = house.seeFH;
		po.seeGX	= house.seeGX;
		po.seeHM = house.seeHM;
		if(po.mji!=null && po.mji!=0){
			int jiage = (int) (po.zjia*10000/house.mji);
			po.djia = (float) jiage;
		}
		if(house.tel==null){
			dao.execute("delete from HouseTel where hid = ?", house.id);
			po.tel = house.tel;
		}else{
			if(!house.tel.equals(po.tel)){
				//修改了电话号码
				dao.execute("delete from HouseTel where hid = ?", house.id);
				String[] arr = house.tel.split("/");
				for(String tel : arr){
					HouseTel ht = new HouseTel();
					ht.hid = house.id;
					ht.tel = tel;
					dao.saveOrUpdate(ht);
				}
				po.tel = house.tel;
			}
		}
		dao.saveOrUpdate(po);
		User user = ThreadSessionHelper.getUser();
		String operConts = "["+user.Department().namea+"-"+user.uname+ "] 修改了房源["+house.id+"]";
		operService.add(OperatorType.房源记录, operConts);
		mv.data.put("msg", "修改成功");
		mv.data.put("house", JSONHelper.toJSON(po , DataHelper.dateSdf.toPattern()));
		SellState state = SellState.parse(po.ztai);
		if(state!=null){
			mv.data.getJSONObject("house").put("ztai", state.toString());
		}
		mv.data.put("result", 0);
		return mv;
	}
	
	@WebMethod
	public ModelAndView toggleShenHe(Integer id){
		ModelAndView mv = new ModelAndView();
		User user = ThreadSessionHelper.getUser();
		if(id!=null){
			House po = dao.get(House.class, id);
			String state="";
			if(po!=null){
				if(po.sh==1){
					po.sh=0;
					state="已审-->未审";
				}else{
					po.sh=1;
					state="未审-->已审";
				}
				dao.saveOrUpdate(po);
				mv.data.put("sh", po.sh);
			}
			String operConts = "["+user.Department().namea+"-"+user.uname+ "] 审核了房源["+id+"],状态从"+state;
			operService.add(OperatorType.房源记录, operConts);
		}
		return mv;
	}
	
	@WebMethod
	public ModelAndView physicalDeleteBatch(List<Object> ids){
		List<Integer> params = new ArrayList<Integer>();
		ModelAndView mv = new ModelAndView();
		mv.data.put("result", 0);
		if(ids.isEmpty()){
			return mv;
		}
		StringBuilder hql = new StringBuilder("delete from House where id in (-1");
		StringBuilder gjHql = new StringBuilder("delete from GenJin where hid in (-1");
		for(Object id : ids){
			hql.append(",").append("?");
			gjHql.append(",").append("?");
			params.add(Integer.valueOf(id.toString()));
		}
		hql.append(")");
		gjHql.append(")");
		dao.execute(hql.toString(), params.toArray());
		dao.execute(gjHql.toString(), params.toArray());
		return mv;
	}
	
	@WebMethod
	public ModelAndView get(Integer id){
		ModelAndView mv = new ModelAndView();
		House po = dao.get(House.class, id);
		FangXing fxing = FangXing.parse(po.hxf, po.hxt,po.hxw);
		mv.data = JSONHelper.toJSON(po);
		if(fxing!=null){
			mv.data.put("hxing", fxing.getName());
		}else{
			LogUtil.warning("房源的户型信息错误,hid="+id);
		}
		return mv;
	}
	
	@WebMethod
	public ModelAndView physicalDelete(Integer id){
		ModelAndView mv = new ModelAndView();
		//是否需要权限
		if(id!=null){
			House po = dao.get(House.class, id);
			if(po!=null){
				dao.delete(po);
				dao.execute("delete from GenJin where hid=?", po.id);
			}
		}
		mv.data.put("msg", "删除成功");
		return mv;
	}
	
	@WebMethod
	public ModelAndView listMyFav(HouseQuery query ,Page<House> page){
		User user = ThreadSessionHelper.getUser();
		String favStr = "@"+user.id+"|";
		query.favStr = favStr;
		return listAll(query ,page);
	}
	
	@WebMethod
	public ModelAndView listMyAdd(HouseQuery query ,Page<House> page){
		User user = ThreadSessionHelper.getUser();
		query.userid = user.id;
		return listAll(query ,page);
	}
	
	@WebMethod
	public ModelAndView listRecycle(HouseQuery query ,Page<House> page){
		return listAll(query , page);
	}
	
	@WebMethod
	public ModelAndView listAll(HouseQuery query ,Page<House> page){
		List<Object> params = new ArrayList<Object>();
		StringBuilder hql = null;
//		if(StringUtils.isNotEmpty(query.xpath)){
//			hql = new StringBuilder(" select h from  House h  ,User u where h.uid=u.id and u.id is not null and u.orgpath like ? ");
//			params.add(query.xpath+"%");
//		}else{
//			hql = new StringBuilder(" select h  from House  h where 1=1");
//		}
		if(StringUtils.isNotEmpty(query.tel)){
//			hql = new StringBuilder(" select h  from House  h , (select hid from HouseTel where tel=? group by hid,tel) ht where h.id=ht.hid ");
			query.tel = query.tel.trim();
			if(query.useLike){
				hql = new StringBuilder(" select h  from House  h  where h.tel like ? ");
				params.add("%"+query.tel+"%");
			}else{
				hql = new StringBuilder(" select h  from House  h , HouseTel  ht where h.id=ht.hid and ht.tel=? ");
				params.add(query.tel);
			}
			
		}else{
			hql = new StringBuilder(" select h  from House  h where 1=1");
		}
		
		User u = ThreadSessionHelper.getUser();
		if("all".equals(query.scope)){
			hql.append(" and (h.cid=? or h.seeGX=?) ");
			params.add(u.cid);
			params.add(1);
		}else if("seeGX".equals(query.scope)){
			hql.append(" and h.seeGX=1 ");
		}else if("comp".equals(query.scope)){
			hql.append(" and h.cid=? ");
			params.add(u.cid);
		}else if("fav".equals(query.scope)){
			String favStr = "@"+u.id+"|";
			query.favStr = favStr;
		}
		if(StringUtils.isNotEmpty(query.ztai)){
			hql.append(" and h.ztai like ? ");
			params.add(query.ztai);
		}
		
		if(StringUtils.isNotEmpty(query.search)){
			query.search = query.search.replace(" ", "");
			if(StringUtils.isNotEmpty(query.search)){
				hql.append(" and (h.area like ? or h.address like ? or h.tel like ?");
				params.add("%"+query.search+"%");
				params.add("%"+query.search+"%");
				params.add("%"+query.search+"%");
				try{
					int id = Integer.valueOf(query.search);
					hql.append(" or h.id=? ");
					params.add(id);
				}catch(Exception ex){
					
				}
				hql.append(")");
			}
			
//			try{
//				int id = Integer.valueOf(query.search);
//				hql.append(" or h.id=? ");
//				params.add(id);
//			}catch(Exception ex){
//				
//			}
		}
		if(query.id!=null){
			hql.append(" and h.id = ? ");
			params.add(query.id);
		}
		if(StringUtils.isNotEmpty(query.dhao)){
			hql.append(" and h.dhao = ? ");
			params.add(query.dhao);
		}
//		if(StringUtils.isNotEmpty(query.tel)){
//			query.tel = query.tel.replace(" ", "");
//			hql.append(" and h.tel like ? ");
//			params.add("%"+query.tel+"%");
//		}
		if(StringUtils.isNotEmpty(query.area)){
			query.area = query.area.replace(" ", "");
			hql.append(" and h.area like ? ");
			params.add("%"+query.area+"%");
		}
		if(StringUtils.isNotEmpty(query.address)){
			query.address = query.address.replace(" ", "");
			hql.append(" and h.address like ? ");
			params.add("%"+query.address+"%");
		}
		if(StringUtils.isNotEmpty(query.fhao)){
			hql.append(" and h.fhao like ? ");
			params.add(query.fhao+"%");
		}
		if(StringUtils.isNotEmpty(query.favStr)){
			hql.append(" and h.fav like ? ");
			params.add("%"+query.favStr+"%");
		}
//		if(query.id!=null){
//			hql.append(" and h.id = ?");
//			params.add(query.id);
//		}
		
		if(query.quyus!=null){
			hql.append(" and ( ");
			for(int i=0;i<query.quyus.size();i++){
				hql.append(" h.quyu = ? ");
				if(i<query.quyus.size()-1){
					hql.append(" or ");
				}
				params.add(query.quyus.get(i));
			}
			hql.append(" )");
		}
		
		if(query.lxing!=null){
			hql.append(" and ( ");
			for(int i=0;i<query.lxing.size();i++){
				hql.append(" h.lxing = ? ");
				if(i<query.lxing.size()-1){
					hql.append(" or ");
				}
				params.add(query.lxing.get(i));
			}
			hql.append(" )");
		}
		if(query.zxiu!=null){
			hql.append(" and ( ");
			for(int i=0;i<query.zxiu.size();i++){
				hql.append(" h.zxiu = ? ");
				if(i<query.zxiu.size()-1){
					hql.append(" or ");
				}
				params.add(query.zxiu.get(i));
			}
			hql.append(" )");
		}
		
		if(query.fxing!=null){
			hql.append(" and ( ");
			for(int i=0;i<query.fxing.size();i++){
				String fxing = query.fxing.get(i);
				FangXing fx = FangXing.valueOf(fxing);
				hql.append("( h.hxf=? and h.hxt=? and h.hxw=?)");
				if(i<query.fxing.size()-1){
					hql.append(" or ");
				}
				params.add(fx.getHxf());
				params.add(fx.getHxt());
				params.add(fx.getHxw());
			}
			hql.append(" )");
		}

		if(StringUtils.isNotEmpty(query.leibie)){
			hql.append(" and h.leibie = ? ");
			params.add(query.leibie);
		}
		if(query.zjiaStart!=null){
			hql.append(" and h.zjia>= ? ");
			params.add(query.zjiaStart);
		}
		if(query.zjiaEnd!=null){
			hql.append(" and h.zjia<= ? ");
			params.add(query.zjiaEnd);
		}
		if(query.yearStart!=null){
			hql.append(" and h.dateyear>= ? ");
			params.add(query.yearStart);
		}
		if(query.yearEnd!=null){
			hql.append(" and h.dateyear<= ? ");
			params.add(query.yearEnd);
		}
		hql.append(HqlHelper.buildDateSegment("h.dateadd",query.dateStart,DateSeparator.After,params));
		hql.append(HqlHelper.buildDateSegment("h.dateadd",query.dateEnd, DateSeparator.Before , params));
		
		if(query.mjiStart!=null){
			hql.append(" and h.mji>= ? ");
			params.add(query.mjiStart);
		}
		if(query.mjiEnd!=null){
			hql.append(" and h.mji<= ? ");
			params.add(query.mjiEnd);
		}
		if(query.lcengStart!=null){
			hql.append(" and h.lceng>= ? ");
			params.add(query.lcengStart);
		}
		if(query.lcengEnd!=null){
			hql.append(" and h.lceng<= ? ");
			params.add(query.lcengEnd);
		}
		if(query.djiaStart!=null){
			hql.append(" and h.djia>= ? ");
			params.add(query.djiaStart);
		}
		if(query.djiaEnd!=null){
			hql.append(" and h.djia<= ? ");
			params.add(query.djiaEnd);
		}
		
		if(query.userid!=null){
			hql.append(" and h.uid= ? ");
			params.add(query.userid);
		}
		
		if(query.sh!=null){
			hql.append(" and h.sh= ? ");
			params.add(query.sh);
		}

		page.orderBy = "h.dateadd";
		page.order = Page.DESC;
		page.setPageSize(25);
		LogUtil.info("house query hql : "+ hql.toString());
		page = dao.findPage(page, hql.toString(),params.toArray());
		ModelAndView mv = new ModelAndView();
		if(page.getResult().size()==0 && query.useLike==false && StringUtils.isNotEmpty(query.tel)){
			query.useLike = true;
			mv = listAll(query, page);
		}
		JSONObject jpage = JSONHelper.toJSON(page,DataHelper.sdf.toPattern());
		fixEnumValue(jpage);
		mv.data.put("page", jpage);
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
		if(house.seeGX==null || house.seeGX==0){
			if(UserHelper.getUserWithAuthority("fy_sh").isEmpty()){
				throw new GException(PlatformExceptionType.BusinessException,"由于贵公司没有设置房源审核权限，请选择发布至共享房源,由中介宝审核。");
			}
		}
	}
	
	@WebMethod
	public ModelAndView clearHouseWith2SameTel(){
		List<Map> list = dao.listAsMap("select hid as hid ,tel as tel from HouseTel group by hid,tel having COUNT(*)>1");
		int index=0;
		for(Map ht : list){
			Integer hid = (Integer)ht.get("hid");
			String tel = (String)ht.get("tel");
			List<HouseTel> result = dao.listByParams(HouseTel.class, "from HouseTel where hid=? and tel=?", hid , tel);
			for(int i=0;i<result.size()-1;i++){
				dao.delete(result.get(i));
			}
			index++;
			System.out.println(index);
		}
		return new ModelAndView();
	}
	
	
	@WebMethod
	public ModelAndView splitHouseTel(){
		List<HouseTel> list = dao.listByParams(HouseTel.class, "from HouseTel where tel like '%/%' ");
		int index=0;
		for(HouseTel ht : list){
			String[] arr = ht.tel.split("/");
			for(String tel : arr){
				HouseTel vo = new HouseTel();
				vo.hid = ht.hid;
				vo.tel = tel;
				dao.saveOrUpdate(vo);
			}
			dao.delete(ht);
			index++;
			System.out.println(index);
		}
		return new ModelAndView();
	}
	
	private void fixEnumValue(JSONObject jpage) {
		JSONArray results = jpage.getJSONArray("data");
		for(int i=0;i<results.size();i++){
			JSONObject obj = results.getJSONObject(i);
			String key = (String)obj.get("ztai");
			SellState state = SellState.parse(key);
			if(state!=null){
				obj.put("ztai", state.toString());
			}
		}
	}
}
