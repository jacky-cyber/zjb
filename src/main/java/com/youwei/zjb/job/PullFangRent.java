package com.youwei.zjb.job;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.bc.sdak.CommonDaoService;
import org.bc.sdak.SimpDaoTool;
import org.bc.sdak.utils.LogUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.youwei.zjb.StartUpListener;
import com.youwei.zjb.house.RentType;
import com.youwei.zjb.house.entity.HouseRent;
import com.youwei.zjb.im.IMServer;

public class PullFangRent extends AbstractJob implements HouseRentJob{

	static CommonDaoService dao = SimpDaoTool.getGlobalCommonDaoService();
	private static PullFangRent instance = new PullFangRent();
	private PullFangRentAction action = new PullFangRentAction();
	private final String listPageUrl= "http://zu.hf.fang.com/house/a21/";
	
	public static void main(String[] args){
		StartUpListener.initDataSource();
//		HouseRent hr = PullDataHelper.pullDetail(job.action , "http://hf.ganji.com/fang1/1350251348x.htm" , null ,RentType.整租);
//		dao.saveOrUpdate(hr);
		instance.work();
	}
	
	private Elements getRepeats(Document doc){
		Elements infoList = doc.getElementsByClass("houseList").select("dl");
		return infoList;
	}
	
	public void work(){
		try{
			System.out.print(action.getSiteName()+"正在运行");
			
			CloseableHttpClient client = HttpClientBuilder.create().build(); 
			RequestConfig requestConfig = RequestConfig.custom()  
		    .setConnectionRequestTimeout(10000).setConnectTimeout(10000)
		    .setSocketTimeout(10000).build();
		    HttpGet request = new HttpGet(listPageUrl);
		    request.setConfig(requestConfig);
			CloseableHttpResponse response = client.execute(request);
			String result = EntityUtils.toString(response.getEntity() , "utf-8");
			response.close();
			client.close();
			
			Document doc = Jsoup.parse(result);
			Elements list = getRepeats(doc);
			if(list==null){
				LogUtil.warning("获取房源列表信息失败，等待下次重试.");
				return;
			}
			int count=0;
			for(Element e : list){
				String link = getLink(e);
				HouseRent po = dao.getUniqueByKeyValue(HouseRent.class, "href", link);
				if(po!=null){
					continue;
				}
				HouseRent hr = PullDataHelper.pullDetail(action , link , null ,getRentType(e),getAddress(e));
				if(hr!=null){
					dao.saveOrUpdate(hr);
				}
				count++;
				if(count>5){
					break;
				}
				Thread.sleep(this.getDetailPageInterval());
			}
			System.out.println("共处理"+action.getSiteName()+"房源数:"+count);
		}catch(Exception ex){
			String msg = action.getSiteName()+"扫网任务失败，reason="+ex.getMessage();
			IMServer.sendMsgToUser(PullDataHelper.errorReportUserId, msg);
		}
	}

	public String getAddress(Element elem) {
		String address = elem.getElementsByClass("iconAdress").attr("title");
		if(address.isEmpty()){
			return "";
		}else {
			return address.trim();
		}
	}

	private String getLink(Element elem){
		String href = elem.child(1).child(0).child(0).attr("href");
		return "http://zu.hf.fang.com"+href;
	}
	
	private RentType getRentType(Element elem){
		String title = elem.child(1).child(2).text();
		if(StringUtils.isEmpty(title)){
			return RentType.合租;
		}
		if(title.contains("合租")){
			return RentType.合租;
		}else{
			return RentType.整租;
		}
	}

	@Override
	public String getJobName() {
		return action.getSiteName();
	}

}
