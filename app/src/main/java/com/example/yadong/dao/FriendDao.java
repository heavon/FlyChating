package com.example.yadong.dao;

import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.example.yadong.InterfaceClasses.HttpResponse;
import com.example.yadong.utils.HttpUtils;
import com.example.yadong.vo.Friend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

/**
 * Created by Yadong on 16/3/5.
 */
public class FriendDao extends BaseDao{

    /**
     * 获取好友列表
     * @param id 用户uid
     * @param hashcode 每次登录都生成一个hashcode
     * @param queue 网络请求队列
     * @param response 回调方法
     */
    public void getFriends(int id, String hashcode, RequestQueue queue, final HttpResponse<List<Friend>> response){
        final List<Friend> friends = new ArrayList<Friend>();

        boolean hasNew = true;
        //判断是否有新好友，如果没有就读取本地数据，如果有就获取新好友信息
        if(hasNew){

            HttpUtils http = new HttpUtils(queue);
            String url = "http://old.uigreat.com/api/client_manager/index.php?module=getfriends&uid=" + id + "&hashcode=" + hashcode;

            http.get(url, new HttpResponse<String>() {
                @Override
                public void getHttpResponse(String result) {
                    //返回json字符串
                    String s = result;
                    Log.e("response", s);
                    JSONObject json;
                    Boolean responseError;
                    try{
                        json = new JSONObject(s);
                        responseError = json.getBoolean("error");
                        //如果返回错误则打印错误信息，否则解析好友数据
                        if(responseError){
                            Log.e("getFriendsError", json.getString("message"));
                        }else{
                            JSONArray data = json.getJSONArray("data");
                            for(int i = 0; i < data.length(); i++){
                                JSONObject friendJson = data.getJSONObject(i);
                                Friend friend = new Friend();
                                friend.setUid(friendJson.getInt("fuid"));
                                friend.setName(friendJson.getString("fusername"));
                                friends.add(friend);
                            }
                        }
                    }catch (Exception e){
                        e.getStackTrace();
                    }

                    //将friends作为参数回调
                    response.getHttpResponse(friends);
                }
            });
        } else{
            //从缓存加载用户信息
        }
    }

    /**
     * 初始化好友列表
     * @param id 用户uid
     * @param hashcode 每次登录都生成一个hashcode
     * @param queue 网络请求队列
     */
    public void initFriends(int id, String hashcode, RequestQueue queue){
        final List<Friend> friends = new ArrayList<Friend>();

        boolean hasNew = true;
        //判断是否有新好友，如果没有就读取本地数据，如果有就获取新好友信息
        if(hasNew){

            HttpUtils http = new HttpUtils(queue);
            String url = "http://old.uigreat.com/api/client_manager/index.php?module=getfriends&uid=" + id + "&hashcode=" + hashcode;

            http.get(url, new HttpResponse<String>() {
                @Override
                public void getHttpResponse(String result) {
                    //返回json字符串
                    String s = result;
                    Log.e("response", s);
                    JSONObject json;
                    Boolean responseError;
                    try{
                        json = new JSONObject(s);
                        responseError = json.getBoolean("error");
                        //如果返回错误则打印错误信息，否则解析好友数据
                        if(responseError){
                            Log.e("getFriendsError", json.getString("message"));
                        }else{
                            JSONArray data = json.getJSONArray("data");
                            for(int i = 0; i < data.length(); i++){
                                JSONObject friendJson = data.getJSONObject(i);
                                Friend friend = new Friend();
                                friend.setUid(friendJson.getInt("fuid"));
                                friend.setName(friendJson.getString("fusername"));
                                friends.add(friend);
                            }
                        }
                    }catch (Exception e){
                        e.getStackTrace();
                    }

                    //给融云提供用户内容
                    RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
                        @Override
                        public UserInfo getUserInfo(String s) {
                            for (Friend friend : friends) {
                                String id = String.valueOf(friend.getUid());
                                if (id.equals(s)) {
                                    return new UserInfo(s, friend.getName(), null);
                                }
                            }
                            return null;
                        }
                    }, true);
                }
            });
        } else{
            //从缓存加载用户信息
        }
    }
}
