package com.youwei.zjb.task;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.youwei.zjb.house.RentType;
import com.youwei.zjb.util.DataHelper;

public class TaskHelper {

	public static Integer getHxtFromText(String text){
		int ting = text.indexOf("厅");
		try{
			if(ting>0){
				return Integer.valueOf(String.valueOf(text.charAt(ting-1)));
			}
		}catch(Exception ex){
			//暂不处理
		}
		
		return 0;
	}
	
	public static Integer getHxsFromText(String text){
		int ting = text.indexOf("室");
		try{
			if(ting>0){
				return Integer.valueOf(String.valueOf(text.charAt(ting-1)));
			}
		}catch(Exception ex){
			//暂不处理
		}
		
		return 0;
	}
	
	public static Integer getHxwFromText(String text){
		int ting = text.indexOf("卫");
		try{
			if(ting>0){
				return Integer.valueOf(String.valueOf(text.charAt(ting-1)));
			}
		}catch(Exception ex){
			//暂不处理
		}
		
		return 0;
	}
	
	public static int getLcengFromText(String text){
		text = text.replace("楼层：", "").replace("楼", "").replace("层", "");
		if(StringUtils.isEmpty(text)){
			return 0;
		}
		try{
			String tmp = text.split("/")[0];
			return Integer.valueOf(tmp);
		}catch(Exception ex){
			//暂不处理
			return 0;
		}
	}
	public static int getZcengFromText(String text){
		text = text.replace("楼", "");
		try{
			String tmp = text.split("/")[1];
			return Integer.valueOf(tmp);
		}catch(Exception ex){
			//暂不处理
			return 0;
		}
	}
	
	public static int getYearFromText(String text){
		String tmp = "";
		if(text.contains("年")){
			tmp =  text.replace("年", "").trim();
		}else{
			tmp = text;
		}
		if("暂无".equals(tmp)){
			return 0;
		}
		try{
			return Integer.valueOf(tmp);
		}catch(Exception ex){
			return 0;
		}
	}
	
	public static Float getMjiFromText(String text){
		if(StringUtils.isEmpty(text)){
			return 0f;
		}
		String tmp = "";
		text  = text.replace("　", " ").replace("面积：", "").replace("平米", "㎡");
		text = text.split("（")[0];
		for(String str : text.split(" ")){
			if(str.contains("㎡") && !str.contains("（")){
				tmp =  str.replace("㎡", "").trim();
				break;
			}
		}
		try{
			return Float.valueOf(tmp);
		}catch(Exception ex){
			return 0f;
		}
	}
	
	public static String getTelFromText(String text){
		if(StringUtils.isEmpty(text)){
			return "";
		}
		if(!text.contains("src=")){
			return text.replace(" ", "").trim();
		}
		String[] arr = text.split("src=");
		if(arr.length>1){
			text = arr[1];
			arr = text.split("'");
			if(arr.length>2){
				text = arr[1];
				return text;
			}
		}
		return "";
	}
	
	public static String getAreaFromText(String text){
		//需要过滤括号里的内容
		if(StringUtils.isEmpty(text)){
			return "";
		}
		text = text.replace(String.valueOf((char)160), "");
		text = text.replace("小区：", "");
		text = text.replace("位置：", "");
		text = text.replace("(出售)", "");
		text = text.replace("（出售 ）", "");
		text = text.replace("出售", "");
		text = text.replace("-", "").trim();
		text = text.split("\\(")[0].trim();
		return text.split(" ")[0];
	}
	
	public static Date getPubtimeFromText(String text){
		text = text.trim();
		try{
			return DataHelper.dateSdf.parse(text);
		}catch(Exception ex){
			return new Date();
		}
		
	}

	public static String getZjiaFromText(String zjia) {
		if(StringUtils.isEmpty(zjia)){
			return "";
		}
		if(zjia.contains("面议")){
			return "";
		}
		if(zjia.contains("急售")){
			return "";
		}
		return zjia.replace("价格：", "").replace("万", "").replace("w", "").replace("W", "").replace("元", "").replace("左右", "");
	}

	public static String getZxiuFromText(String zxiu) {
		if(StringUtils.isEmpty(zxiu)){
			return "";
		}
		if(zxiu.contains("简单装修")){
			return "简装";
		}else if(zxiu.contains("精装修")){
			return "精装";
		}else if(zxiu.contains("中等装修")){
			return "中装";
		}else if(zxiu.contains("豪华装修")){
			return "豪装";
		}else{
			return zxiu.replace("装修情况：", "").replace("房", "");
		}
	}

	public static Float getZujinText(String zujin) {
		try{
			return Float.valueOf(zujin);
		}catch(Exception ex){
			return 0f;
		}
	}

	public static Integer getFangshiText(String fangshi) {
		RentType fs = RentType.valueOf(fangshi);
		if(fs==null){
			return RentType.合租.getCode();
		}
		return fs.getCode();
	}
}
