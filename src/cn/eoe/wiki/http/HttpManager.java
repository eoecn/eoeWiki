package cn.eoe.wiki.http;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import cn.eoe.wiki.utils.HttpUtil;
import cn.eoe.wiki.utils.L;
import cn.eoe.wiki.utils.ThreadPoolUtil;

/**
 * a http manager. manager a transaction 
 * @author <a href="mailto:kris1987@qq.com">Kris.lee</a>
 * @version 1.0.0
 */
public class HttpManager {
	public static final int 		GET		= 0;
	public static final int 		POST 	= 1;
	public static final int 		PUT		= 2;
	public static final int 		DELETE 	= 3;
	
	/**
	 * a call back instance
	 */
	private ITransaction			mTransaction;
	private String 					mUrl;
	/**
	 * request data
	 */
	private Map<String,String> 		mRequestData;
	private Map<String,File> 		mUploadFiles;
	/**
	 * http method , get ,post , put ,delete
	 */
	private int 				method;
	
	public HttpManager(String url,Map<String,String> requestData)
	{
		this( url, requestData,null);
	}
	public HttpManager(String url,Map<String,String> requestData,ITransaction transaction)
	{
		this(url, requestData, GET,transaction);
	}
	public HttpManager(String url,Map<String,String> requestData,int method,ITransaction transaction)
	{
		this(url, requestData, null, method, transaction);
	}
	public HttpManager(String url,Map<String,String> requestData,Map<String,File> uploadData,int method,ITransaction transaction)
	{
		this.mTransaction 	= transaction;
		this.mUrl 			= url;
		this.mRequestData 	= requestData;
		this.mUploadFiles= uploadData;
		this.method 		= method;
	}
	
	
	public void setmTransaction(ITransaction mTransaction) {
		this.mTransaction = mTransaction;
	}
	
	public void setRequestData(Map<String, String> requestData) {
		this.mRequestData = requestData;
	}
	
	public Map<String, String> getmRequestData() {
		return mRequestData;
	}
	public void setmRequestData(Map<String, String> mRequestData) {
		this.mRequestData = mRequestData;
	}
	
	
	public Map<String, File> getmUploadFiles() {
		return mUploadFiles;
	}
	public void setmUploadFiles(Map<String, File> mUploadFiles) {
		this.mUploadFiles = mUploadFiles;
	}
	/**
	 * start a transaction in a new thread
	 * @throws TrasactionException
	 */
	public synchronized void start()
	{
		ThreadPoolUtil.execute(new Runnable() {
			private void request() throws IllegalStateException, HttpResponseException, IOException 
			{
				long begin = System.currentTimeMillis();
				String response = null;
				switch (method) {
				case GET:
					response = HttpUtil.get(mUrl, mRequestData);
					break;
				case POST:
					response = HttpUtil.post(mUrl, mRequestData);
					break;
				case PUT:
					response = HttpUtil.put(mUrl, mRequestData);
					break;
				case DELETE:
					response = HttpUtil.delete(mUrl, mRequestData);
					break;
				}
				long end = System.currentTimeMillis();
				L.e("request time:"+(end- begin));
				if(mTransaction!=null)
				{				
					mTransaction.transactionOver(response);
				}
			}
			@Override
			public void run() {
				try {
					if(mRequestData==null)
					{
						mRequestData = new HashMap<String, String>();
					}
					request();
				} catch (IllegalStateException e) {
					L.e("IllegalStateException", e);
					if(mTransaction!=null)
					{
						mTransaction.transactionException(0,null, e);
					}
				} catch (ClientProtocolException e) {
					L.e("ClientProtocolException", e);
					if(mTransaction!=null)
					{
						mTransaction.transactionException(0,null, e);
					}
				} catch (HttpResponseException e) {
					L.e("HttpResponseException", e);
//					if(Constants.DEBUG)
//					{
//						try {
//							HttpUtil.get(WebUrls.SERVER_TEST_URL);
//						}catch (Exception e1) {
//						}
//					}
					if(mTransaction!=null)
					{
						mTransaction.transactionException(e.getState(),e.getResult(), e);
					}
				} catch (IOException e) {
					L.e("IOException", e);
					if(mTransaction!=null)
					{
						mTransaction.transactionException(0,null, e);
					}
				}
			}
		});
	}
	
}
