package com.example.yadong.dao;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.example.yadong.InterfaceClasses.HttpResponse;
import com.example.yadong.myapplication.LoginActivity;
import com.example.yadong.utils.HttpUtils;
import com.example.yadong.vo.Friend;
import com.example.yadong.vo.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

/**
 * Created by Yadong on 16/3/5.
 */
public class UserDao extends BaseDao{

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @param queue 网络请求队列
     * @param response 回调方法
     */
    public void login(String username, String password, RequestQueue queue, final HttpResponse<Map<String, Object>> response){
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("password", password);
        String loginUrl = "http://old.uigreat.com/api/client_manager/index.php?module=login";
        //发送登录请求
        HttpUtils http = new HttpUtils(queue);
        http.postString(loginUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Map<String, Object> returnMap = new HashMap<String, Object>();
                JSONObject json;
                Boolean responseError;
                try {
                    json = new JSONObject(s);
                    responseError = json.getBoolean("error");
                    returnMap.put("error", responseError);
                    if (responseError) {
                        String msg = json.getString("message");
                        if("module_not_exists".equals(msg)){
                            msg = "请求链接失效";
                        }
                        returnMap.put("msg", msg);
                    } else {
                        JSONObject data = json.getJSONObject("data");
                        int uid = data.getInt("uid");
                        String hashcode = data.getString("hashcode");
                        String token = data.getString("token");
                        returnMap.put("uid", uid);
                        returnMap.put("hashcode", hashcode);
                        returnMap.put("token", token);
                    }
                } catch (Exception e) {
                    e.getStackTrace();
                }
                //将returnMap作为参数回调
                response.getHttpResponse(returnMap);
            }
        }, params);
    }

}
