package utils;


import org.apache.http.NameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * erp相关调用utils
 * Created by Castiel
 * Date: 2015/8/13.
 * Time: 13:34
 */
public class ERPUtils {
    private  static  String LOGIN_NAME="test6@test.com";
    private  static  String LOGIN_PWD="test6";
    private  static  String ERP_UPLOAD_URL="http://localhost:8000/api/batch/upload/";
    /**
     * erp登陆
     * @return  map（taken，accept）
     */
    public static Map login(){
        String token="";
        Map map=new HashMap<>();
        NameValuePair username= new NameValuePair() {
            public String getName() {
                return "username";
            }
            public String getValue() {
                return LOGIN_NAME;
            }
        };
        NameValuePair password= new NameValuePair() {
            public String getName() {
                return "password";
            }
            public String getValue() {
                return LOGIN_PWD;
            }
        };
        List<NameValuePair> list=new ArrayList<>();
        list.add(username);
        list.add(password);
        try {
            String body=HttpUtils.do_post("http://localhost:8000/apilogin",list,null);
            System.out.println(body);
            if(body.contains("authToken")){
            	String[] temp=body.split("\":\"");
            	token=temp[1].substring(0,temp[1].length()-2);

                map.put("Accept","application/json, text/plain, */*");
                map.put("X-AUTH-TOKEN",token);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return map;
    }

    public static int  postFile(String action,String fileName,String filePath,Map<String, String> headers) throws IOException {
        String url=ERP_UPLOAD_URL+action;
        //HttpUtils.filePost(url, fileName, filePath, headers,action);
        return 0;
    }
}
