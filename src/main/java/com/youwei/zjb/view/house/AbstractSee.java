package com.youwei.zjb.view.house;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.bc.sdak.CommonDaoService;
import org.bc.sdak.SimpDaoTool;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.youwei.zjb.ThreadSessionHelper;
import com.youwei.zjb.util.JSONHelper;
import com.youwei.zjb.view.page;

public abstract class AbstractSee extends page{
	public Document initPage(Document doc , HttpServletRequest req){
		String html = doc.html();
		String id = req.getParameter("id");
		CommonDaoService dao = SimpDaoTool.getGlobalCommonDaoService();
		JSONObject json = getData(Integer.valueOf(id));
		if(json==null){
			html="404";
			return Jsoup.parse(html);
		}else{
			Elements nrlist = doc.getElementsByClass("neirong");
			for(Element elem : nrlist){
				String innerHtml = elem.html();
				for(Object key : json.keySet()){
					String val = "";
					if("mji".equals(key)){
						double tmp = json.getDouble(key.toString());
						if(tmp == (int)tmp){
							//没有小数部分
							val = String.valueOf((int)tmp);
						}else{
							val = String.valueOf(tmp);
						}
					}else if ("djia".equals(key)){
						val = String.valueOf(json.getInt(key.toString()));
					}else if ("zjia".equals(key)){
						double tmp = json.getDouble(key.toString());
						if(tmp == (int)tmp){
							//没有小数部分
							val = String.valueOf((int)tmp);
						}else{
							//保留一位小数
							DecimalFormat df = new DecimalFormat("0.0");
							val = df.format(tmp);
						}
					}else {
						val = String.valueOf(json.get(key));
					}
					innerHtml= innerHtml.replace("${"+key+"}", val);
				}
				elem.html(innerHtml);
			}
			String hql = "select gj.conts as conts , d.namea as dname , u.uname as uname , gj.addtime as addtime from GenJin gj , User u , "
					+ " Department d where gj.hid=? and gj.uid=u.id and u.did=d.id and gj.chuzu=  ? and gj.sh=1 order by addtime desc";
			List<Map> gjList = dao.listAsMap(hql, Integer.valueOf(id) , getChuzu());
			Elements temp = doc.getElementsByClass("list");
			buildHtmlWithJsonArray(temp.first() , JSONHelper.toJSONArray(gjList));
			temp.remove();
		}
		Elements fav = doc.getElementsByAttributeValue("fav", json.getString("fav"));
		fav.remove();
		html = doc.html();
		Object seeFH = json.get("seeFH");
		Object seeHM = json.get("seeHM");
		if(Integer.valueOf(1).equals(seeFH)){
			html = html.replace("${fdhao}", json.getString("dhao")+"#"+ json.getString("fhao"));
		}else{
			html = html.replace("${fdhao}","");
		}
		html = html.replace("$${seeHM}",seeHM==null? "" : seeHM.toString());
		html = html.replace("$${lxr}", json.getString("lxr"));
		html = html.replace("$${tel}", json.getString("tel"));
		html = html.replace("$${area}", json.getString("area"));
		html = html.replace("$${city}",ThreadSessionHelper.getCity());
		return Jsoup.parse(html);
	}

	protected abstract JSONObject getData(int id);
	
	protected abstract int getChuzu();
	
}
