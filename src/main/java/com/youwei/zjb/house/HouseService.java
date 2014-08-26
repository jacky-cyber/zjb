package com.youwei.zjb.house;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.bc.sdak.CommonDaoService;
import org.bc.sdak.Page;
import org.bc.sdak.TransactionalServiceHelper;
import org.bc.sdak.utils.BeanUtil;
import org.bc.web.ModelAndView;
import org.bc.web.Module;
import org.bc.web.WebMethod;

import com.youwei.zjb.DateSeparator;
import com.youwei.zjb.ThreadSession;
import com.youwei.zjb.house.entity.Favorite;
import com.youwei.zjb.house.entity.GenJin;
import com.youwei.zjb.house.entity.House;
import com.youwei.zjb.sys.OperatorService;
import com.youwei.zjb.sys.OperatorType;
import com.youwei.zjb.user.entity.User;
import com.youwei.zjb.util.DataHelper;
import com.youwei.zjb.util.HqlHelper;
import com.youwei.zjb.util.JSONHelper;

@Module(name="/house/")
public class HouseService {

	CommonDaoService dao = TransactionalServiceHelper.getTransactionalService(CommonDaoService.class);
	
	OperatorService operService = TransactionalServiceHelper.getTransactionalService(OperatorService.class);
	
	@WebMethod
	public ModelAndView add(House house){
		ModelAndView mv = new ModelAndView();
		User user = ThreadSession.getUser();
		//检查，是否是重复房源.检查条件为,小区名+楼栋号+房号
		House po = dao.getUniqueByParams(House.class, new String[]{"area","dhao","fhao"},new Object[]{house.area,house.dhao,house.fhao});
		if(po!=null){
			mv.data.put("msg", "同一个房源已经存在");
			mv.data.put("result", 2);
		}else{
			house.isdel = 0;
			house.dateadd = new Date();
			house.uid = user.id;
			house.did = user.did;
			if(house.zjia ==null){
				house.zjia=0f;
			}
			if(house.mji!=null && house.mji!=0){
				int jiage = (int) (house.zjia*10000/house.mji);
				house.djia = (float) jiage;
			}
			dao.saveOrUpdate(house);
			mv.data.put("msg", "发布成功");
			mv.data.put("result", 0);
		}
		
		String operConts = "["+user.Department().namea+"-"+user.uname+ "] 添加了房源["+house.area+"]";
		operService.add(OperatorType.房源记录, operConts);
		return mv;
	}
	
	@WebMethod
	public ModelAndView update(House house){
		ModelAndView mv = new ModelAndView();
		House po = dao.get(House.class, house.id);
		house.isdel = po.isdel;
		house.dateadd = po.dateadd;
		house.uid = po.uid;
		house.did = po.did;
		if(house.mji!=null && house.mji!=0){
			int jiage = (int) (house.zjia*10000/house.mji);
			house.djia = (float) jiage;
		}
		dao.saveOrUpdate(house);
		User user = ThreadSession.getUser();
		String operConts = "["+user.Department().namea+"-"+user.uname+ "] 修改了房源["+house.id+"]";
		operService.add(OperatorType.房源记录, operConts);
		mv.data.put("msg", "修改成功");
		mv.data.put("result", 0);
		return mv;
	}
	
	@WebMethod
	public ModelAndView toggleShenHe(Integer id){
		ModelAndView mv = new ModelAndView();
		if(id!=null){
			House po = dao.get(House.class, id);
			if(po!=null){
				if(po.sh==1){
					po.sh=0;
				}else{
					po.sh=1;
				}
				dao.saveOrUpdate(po);
				mv.data.put("sh", po.sh);
			}
		}
		return mv;
	}
	
	@WebMethod
	public ModelAndView softDelete(Integer houseId){
		ModelAndView mv = new ModelAndView();
		if(houseId!=null){
			House po = dao.get(House.class, houseId);
			if(po!=null){
				po.isdel= 1;
				dao.saveOrUpdate(po);
				User user = ThreadSession.getUser();
				String operConts = "["+user.Department().namea+"-"+user.uname+ "] 删除了房源["+po.area+"]";
				operService.add(OperatorType.房源记录, operConts);
			}
		}
		
		mv.data.put("msg", "删除成功");
		return mv;
	}
	
	@WebMethod
	public ModelAndView softDeleteBatch(List<Object> ids){
		List<Integer> params = new ArrayList<Integer>();
		ModelAndView mv = new ModelAndView();
		mv.data.put("result", 0);
		if(ids.isEmpty()){
			return mv;
		}
		StringBuilder hql = new StringBuilder("delete from House where id in (-1");
		for(Object id : ids){
			hql.append(",").append("?");
			params.add(Integer.valueOf(id.toString()));
		}
		hql.append(")");
		dao.execute(hql.toString(), params.toArray());
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
		mv.data = JSONHelper.toJSON(po);
		return mv;
	}
	
	@WebMethod
	public ModelAndView recover(Integer houseId){
		ModelAndView mv = new ModelAndView();
		if(houseId!=null){
			House po = dao.get(House.class, houseId);
			if(po!=null){
				po.isdel= 0;
				dao.saveOrUpdate(po);
			}
		}
		mv.data.put("msg", "恢复成功");
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
		User user = ThreadSession.getUser();
		String favStr = "@"+user.id+"|";
		query.favStr = favStr;
		return listAll(query ,page);
	}
	
	@WebMethod
	public ModelAndView listMyAdd(HouseQuery query ,Page<House> page){
		User user = ThreadSession.getUser();
		query.userid = user.id;
		return listAll(query ,page);
	}
	
	@WebMethod
	public ModelAndView listRecycle(HouseQuery query ,Page<House> page){
//		query.isdel=1;
		return listAll(query , page);
	}
	
	@WebMethod
	public ModelAndView listAll(HouseQuery query ,Page<House> page){
		List<Object> params = new ArrayList<Object>();
		StringBuilder hql = null;
		if(StringUtils.isNotEmpty(query.xpath)){
			hql = new StringBuilder(" select h from  House h  ,User u where h.uid=u.id and u.id is not null and u.orgpath like ? ");
			params.add(query.xpath+"%");
		}else{
			hql = new StringBuilder(" select h  from House  h where 1=1");
		}
		
		if(StringUtils.isNotEmpty(query.ztai)){
			hql.append(" and h.ztai like ? ");
			params.add(query.ztai);
		}
		
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
		
		if(StringUtils.isNotEmpty(query.dhao)){
			hql.append(" and h.dhao like ? ");
			params.add("%"+query.dhao+"%");
		}
		if(StringUtils.isNotEmpty(query.fhao)){
			hql.append(" and h.fhao like ? ");
			params.add("%"+query.fhao+"%");
		}
		if(StringUtils.isNotEmpty(query.favStr)){
			hql.append(" and h.fav like ? ");
			params.add("%"+query.favStr+"%");
		}
		if(query.id!=null){
			hql.append(" and h.id = ?");
			params.add(query.id);
		}
		
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
//		if(query.jiaoyis!=null){
//			hql.append(" and ( ");
//			for(int i=0;i<query.jiaoyis.size();i++){
//				hql.append(" h. jiaoyi = ? ");
//				if(i<query.jiaoyis.size()-1){
//					hql.append(" or ");
//				}
//				params.add(JiaoYi.valueOf(query.jiaoyis.get(i)).getCode());
//			}
//			hql.append(" )");
//		}
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
//		if(query.isdel==null){
//			hql.append(" and ( isdel= 0 or isdel is null) ");
//		}
//		if(query.isdel!=null){
//			hql.append(" and h.isdel=?");
//			params.add(query.isdel);
//		}
		page.orderBy = "h.dateadd";
		page.order = Page.DESC;
		page.setPageSize(20);
//		hql.append(" and ( isdel= 0 or isdel is null) ");
		page = dao.findPage(page, hql.toString(),params.toArray());
		ModelAndView mv = new ModelAndView();
		JSONObject jpage = JSONHelper.toJSON(page,DataHelper.dateSdf.toPattern());
		fixEnumValue(jpage);
		mv.data.put("page", jpage);
		return mv;
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
