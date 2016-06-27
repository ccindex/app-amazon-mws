package utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Castiel
 * Date: 2015/8/12.
 * Time: 14:32
 */
public class HttpUtils {
    /*
    http post
    name_value_pair：key -value
    headers：设置post 头
     */
    public static String do_post(String url, List<NameValuePair> name_value_pair,Map<String, String> headers) throws IOException {
        String body = "{}";
        DefaultHttpClient httpclient = new DefaultHttpClient();

        try {
            HttpPost httpost = new HttpPost(url);

            if (headers != null) {
                Set<String> keys = headers.keySet();
                for (Iterator<String> i = keys.iterator(); i.hasNext();) {
                    String key = (String) i.next();
                    httpost.addHeader(key, headers.get(key));

                }
            }

            httpost.setEntity(new UrlEncodedFormEntity(name_value_pair));
            HttpResponse response = httpclient.execute(httpost);
            HttpEntity entity = response.getEntity();
            body = EntityUtils.toString(entity);
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return body;
    }

    public static String do_get(String url) throws ClientProtocolException, IOException {
        String body = "{}";
        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            HttpGet httpget = new HttpGet(url);
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            body = EntityUtils.toString(entity);
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return body;
    }

//    /**
//     * http filepost方法
//     * @param url
//     * @param filename1
//     * @param filepath
//     * @param headers
//     * @param action
//     */
//    @SuppressWarnings("deprecation")
//    public static void filePost(String url,String filename1,String filepath,Map<String, String> headers,String action){
//        HttpClient httpclient = new DefaultHttpClient();
//        try {
//
//            HttpPost httppost = new HttpPost(url);
//
//            if (headers != null) {
//                Set<String> keys = headers.keySet();
//                for (Iterator<String> i = keys.iterator(); i.hasNext();) {
//                    String key = (String) i.next();
//                    httppost.addHeader(key, headers.get(key));
//
//                }
//            }
//
//            FileBody bin = new FileBody(new File(filepath + File.separator + filename1));
//
//            StringBody comment = new StringBody(filename1, ContentType.create(
//                    "text/plain", Consts.UTF_8));
//
//            HttpEntity reqEntity = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE).setCharset(CharsetUtils.get("UTF-8"))
//                    .addPart("file", bin)
//                    .addPart("action",new StringBody(action,ContentType.create(
//                            "text/plain", Consts.UTF_8)))
//                    .addPart("filename", comment)
//                    .build();
//
//            httppost.setEntity(reqEntity);
//
//            HttpResponse response = httpclient.execute(httppost);
//
//            int statusCode = response.getStatusLine().getStatusCode();
//            if(statusCode == HttpStatus.SC_OK){
//
//                System.out.println("服务器正常响应.....");
//
//                HttpEntity resEntity = response.getEntity();
//                System.out.println(EntityUtils.toString(resEntity));//httpclient自带的工具类读取返回数据
//                System.out.println(resEntity.getContent());
//
//                EntityUtils.consume(resEntity);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                httpclient.getConnectionManager().shutdown();
//            } catch (Exception ignore) {
//
//            }
//        }
//    }
}
