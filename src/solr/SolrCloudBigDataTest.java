package solr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

public class SolrCloudBigDataTest {
	private static List<String> dics = new ArrayList<String>(1<<10);
	public static String[] category = new String[]{"波长","纳米","厘米",
		"分米","机载","舰载","地面","厘米 舰载","厘米 机载","相阵","纳米 相阵 地面","其他"};
	private static String[] department = new String[]{"101室","一部","201室",
		"科技部","test","二部","三部","四部","五部","六部","七步","八部"};
	private static long ITEM_ID = 1;
	
	static {
		
		InputStream in = SolrTest.class.getResourceAsStream("main.dic");
		
		BufferedReader br = null;
		br = new BufferedReader(new InputStreamReader(in));
		
		String str = null;
		try {
			while((str = br.readLine())!= null){
				if(Utils.hasText(str)) {
					dics.add(str);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	private static CloudSolrServer cloudSolrServer;  
	
	private  static synchronized CloudSolrServer getCloudSolrServer(final String zkHost) {  
	    if(cloudSolrServer == null) {  
	        try {  
	            cloudSolrServer = new CloudSolrServer(zkHost);  
	        }catch(Exception e) {  
	            e.printStackTrace();                  
	        }  
	    }  
	      
	    return cloudSolrServer;  
	} 

	
	public static SolrServer getSolrServer(){

		String url = "http://localhost:8180/solr/";
//		String url = "http://localhost:18080/solr/collection1/";
		SolrServer server = new HttpSolrServer(url);
		((HttpSolrServer) server).setSoTimeout(60000); // socket read timeout 
		((HttpSolrServer) server).setConnectionTimeout(60000); 
		((HttpSolrServer) server).setDefaultMaxConnectionsPerHost(100); 
		((HttpSolrServer) server).setMaxTotalConnections(100); 
		((HttpSolrServer) server).setFollowRedirects(false); // defaults to false 
		// allowCompression defaults to false. 
		// Server side must support gzip or deflate for this to have any effect. s
		((HttpSolrServer) server).setAllowCompression(true); 
		((HttpSolrServer) server).setMaxRetries(1); // defaults to 0. > 1 not recommended.
		return server;
	}
	
	public static void addSearchIndex(String id, String title, String content,
			String author) {
		// 构建文档对象,属性同schema.xml配置一致,可以缺少属性
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", id);
		doc.addField("title", title);
		doc.addField("content", content);
		doc.addField("author", author);

		//SolrServer server = SearchEngine.getSolrServer("befitcomponent");
		SolrServer server = getSolrServer();

		try {
			server.add(doc);
			server.commit(false, false);
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	public static SolrInputDocument convertFrom(String id, String title, String content,
//			String author) {
//		SolrInputDocument sd = new SolrInputDocument();
//		sd.addField("id", id);
//		sd.addField("title",title);
//		sd.addField("content",content);
//		sd.addField("author",author);
//		return sd;
//	}
	
	public static String randomString(int num){
		StringBuilder sb = new StringBuilder();
		for(int i=num;i>0;i--) {
			sb.append(randomTerms());
		}
		return sb.toString();
	}
	
	private static String randomTerms(){
		int l = dics.size()-1;
		return dics.get(((Number)(l * Math.random())).intValue());
	}
	
	public static String random(String[] arr) {
		return arr[((Number)((arr.length-1) * Math.random())).intValue()];
	}
	
	public static void main(String[] args){
		
//		SolrServer solrServer = getSolrServer();
		
		 final String zkHost = "localhost:2181";       
//	     final String  defaultCollection = "collection1";  
	     final String  defaultCollection = "newone";  
	     final int  zkClientTimeout = 20000;  
	     final int zkConnectTimeout = 1000;  
	       
	     CloudSolrServer cloudSolrServer = getCloudSolrServer(zkHost);         
	     System.out.println("The Cloud SolrServer Instance has benn created!");  
	       
	     cloudSolrServer.setDefaultCollection(defaultCollection);  
	     cloudSolrServer.setZkClientTimeout(zkClientTimeout);  
	     cloudSolrServer.setZkConnectTimeout(zkConnectTimeout);    
	       
	       
	     cloudSolrServer.connect();  
	     System.out.println("The cloud Server has been connected !!!!");  
		
		String[] authors = new String[]{"test","马, 亮","冬雪",
			"admin","赵焰","张雷","张望","张东","张涛","文超","王霄","王伙志"};
		
		try {
			cloudSolrServer.deleteByQuery("*:*");
			cloudSolrServer.commit();
			
			for (long i = 1; i < 10001L; i++) {
				if (i % 10 == 0) {
					cloudSolrServer.commit();
					System.out.println(i);
				}
				
				String id = String.valueOf(i);
				String title = randomString(15);
				String content = randomString(1000);
				String author = random(authors);
				SolrInputDocument sd = new SolrInputDocument();
				sd.addField("id", id);
				sd.addField("title",title);
				sd.addField("content",content);
				sd.addField("author",author);
				
				cloudSolrServer.add(sd);
			}
			cloudSolrServer.commit();
			
//			solrServer.optimize();
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
