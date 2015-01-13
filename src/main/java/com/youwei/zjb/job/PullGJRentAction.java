package com.youwei.zjb.job;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.youwei.zjb.util.DataHelper;

public class PullGJRentAction implements PullRentHouseAction{

	@Override
	public String getArea(Element elem) {
		Elements ar = elem.getElementsMatchingOwnText("小区：");
		Element area = ar.first().nextElementSibling().child(0);
		return area.text().trim();
	}

	private Element getElementsByMatchingText(Element elem,String text){
		for(Element e : elem.getElementsMatchingOwnText(text)){
			if(text.equals(e.text())){
				return e;
			}
		}
		return null;
	}

	@Override
	public String getMji(Element elem) {
		Element mj = elem.getElementsMatchingOwnText("户型：").first();
		String text = mj.parent().ownText();
		for(String str : text.split(" - ")){
			if(str.contains("㎡")){
				return str.replace("㎡", "").trim();
			}
		}
		return "";
	}

	@Override
	public String getZujin(Element elem) {
		Elements zj = elem.getElementsMatchingOwnText("租金：");
		Element zjin = zj.first().nextElementSibling();
		return zjin.text().trim();
	}

	@Override
	public String getLceng(Element elem) {
		Element lc = elem.getElementsMatchingOwnText("楼层：").first();
		String text = lc.parent().ownText();
		return text.split("/")[0];
	}

	@Override
	public String getZceng(Element elem) {
		Element lc = elem.getElementsMatchingOwnText("楼层：").first();
		String text = lc.parent().ownText();
		return text.split("/")[1];
	}

	@Override
	public String getLxing(Element elem) {
		return "";
	}

	@Override
	public String getHxing(Element elem) {
		Element hx = elem.getElementsMatchingOwnText("户型：").first();
		String text = hx.parent().ownText();
		String hxing = text.split(" - ")[0].replace("室", "房");
		return hxing.trim();
	}

	@Override
	public String getZxiu(Element elem) {
		Element zx = elem.getElementsMatchingOwnText("概况：").first();
		String text = zx.parent().ownText();
		for(String str : text.split(" - ")){
			if(str.contains("简单装修")){
				return str.replace("简单装修", "简装").trim();
			}else if(str.contains("中等装修")){
				return str.replace("中等装修", "中装").trim();
			}else if(str.contains("精装修")){
				return str.replace("精装修", "精装").trim();
			}else if(str.contains("豪华装修")){
				return str.replace("豪华装修", "豪装").trim();
			}else if(str.contains("毛坯")){
				return str.trim();
			}
		}
		return "";
	}

	@Override
	public String getYear(Element elem) {
		return "";
	}

	@Override
	public String getQuyu(Element elem) {
		Elements qy = elem.getElementsMatchingOwnText("位置：");
		Element quyu = qy.first().parent().child(2);
		return quyu.text().trim();
	}

	@Override
	public String getAddress(Element elem) {
		Element add = elem.getElementsMatchingOwnText("位置：").first();
		Element address = add.parent().nextElementSibling().child(1);
		return address.text().trim();
	}

	@Override
	public String getLxr(Element elem) {
		Element lx = elem.getElementsMatchingOwnText("在线联系：").first();
		Element lxr = lx.child(0);
		return lxr.text().trim();
	}

	@Override
	public String getTelImg(Element elem) {
		String phone = elem.getElementsByClass("talk-btn").attr("data-phone");
		return phone.trim();
	}

	@Override
	public String getWo(Element elem) {
		Element hx = elem.getElementsMatchingOwnText("户型：").first();
		String text = hx.parent().ownText();
		for(String str : text.split(" - ")){
			if(str.contains("卧")){
				return str.trim();
			}
		}
		return "";
	}

	@Override
	public String getXianZhi(Element elem) {
		Element hx = elem.getElementsMatchingOwnText("户型：").first();
		String text = hx.parent().ownText();
		for(String str : text.split(" - ")){
			if(str.contains("限")){
				return str.trim();
			}
		}
		return "";
	}

	@Override
	public String getPeiZhi(Element elem) {
		Elements pz = elem.getElementsMatchingOwnText("配置：");
		if(pz.isEmpty()){
			return "";
		}
		String peizhi = pz.first().nextElementSibling().ownText();
		return peizhi.trim();
	}

	@Override
	public String getTitle(Element doc) {
		return doc.getElementsByClass("title-name").text();
	}

	@Override
	public Element getDetailSumary(String url) throws IOException {
		URL url1 = new URL(url);
		URLConnection conn = url1.openConnection();
		conn.setConnectTimeout(10000);
		conn.setReadTimeout(10000);
		String result = IOUtils.toString(conn.getInputStream(),"utf-8");
		Document doc = Jsoup.parse(result);
		if(doc.getElementsMatchingOwnText("页面可能被删除").isEmpty()==false){
			return null;
		}
		Element sumary = doc.getElementsByClass("leftBox").first();
		return sumary;
	}

	@Override
	public Date getPubTime(Element elem) {
		Element time = getElementsByMatchingText(elem,"发布");
		String text = time.child(0).text().trim();
		text ="2015-"+text;
		try {
			return DataHelper.sdf3.parse(text);
		} catch (ParseException e) {
			e.printStackTrace();
			return new Date();
		}
	}

}