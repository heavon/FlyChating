package com.example.yadong.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.yadong.InterfaceClasses.HttpResponse;
import com.example.yadong.dao.FriendDao;
import com.example.yadong.dao.UserDao;
import com.example.yadong.utils.HttpUtils;
import com.example.yadong.vo.Friend;

import org.apache.http.HttpRequest;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;


public class LoginActivity extends Activity implements HttpResponse<Map<String, Object>>{
    private EditText userName, password;
    private CheckBox rem_pw, auto_login;
    private Button btn_login;
    private ImageButton btnQuit;
    private String userNameValue,passwordValue;
    private SharedPreferences sp;

    private RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除标题
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        queue = Volley.newRequestQueue(LoginActivity.this);
        initUI();
        //判断记住密码多选框的状态
        if(sp.getBoolean("ISCHECK", false))
        {
            //设置默认是记录密码状态
            rem_pw.setChecked(true);
            userNameValue = sp.getString("USER_NAME", "");
            passwordValue = sp.getString("PASSWORD", "");
            userName.setText(userNameValue);
            password.setText(passwordValue);
            //判断自动登陆多选框状态
            if(sp.getBoolean("AUTO_ISCHECK", false))
            {
                //设置默认是自动登录状态
                auto_login.setChecked(true);
                //跳转界面
//                UserDao dao = new UserDao();
//                dao.login(userNameValue, passwordValue, queue, LoginActivity.this);

            }
        }
        // 登录监听事件
        btn_login.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                userNameValue = userName.getText().toString();
                passwordValue = password.getText().toString();
                userNameValue.trim();
                passwordValue.trim();
                if ("".equals(userNameValue)) {
                    Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                    return;
                }
                if ("".equals(passwordValue)) {
                    Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                UserDao dao = new UserDao();
                dao.login(userNameValue, passwordValue, queue, LoginActivity.this);

                //设置等待图标

            }
        });
        //监听记住密码多选框按钮事件
        rem_pw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (rem_pw.isChecked()) {
                    System.out.println("记住密码已选中");
                    sp.edit().putBoolean("ISCHECK", true).commit();

                } else {
                    System.out.println("记住密码没有选中");
                    sp.edit().putBoolean("ISCHECK", false).commit();
                }
            }
        });
        //监听自动登录多选框事件
        auto_login.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (auto_login.isChecked()) {
                    System.out.println("自动登录已选中");
                    //选中记住密码多选框
                    rem_pw.setChecked(true);
                    sp.edit().putBoolean("AUTO_ISCHECK", true).commit();

                } else {
                    System.out.println("自动登录没有选中");
                    sp.edit().putBoolean("AUTO_ISCHECK", false).commit();
                }
            }
        });
    }

    /**
     * 建立与融云服务器的连接
     *
     * @param token
     */
    private void connect(String token) {

        if (getApplicationInfo().packageName.equals(App.getCurProcessName(getApplicationContext()))) {
            /**
             * IMKit SDK调用第二步,建立与服务器的连接
             */
            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                /**
                 * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
                 */
                @Override
                public void onTokenIncorrect() {
                    Log.d("LoginActivity", "--onTokenIncorrect");
                }
                /**
                 * 连接融云成功
                 * @param userid 当前 token
                 */
                @Override
                public void onSuccess(String userid) {
                    Log.d("LoginActivity", "--onSuccess" + userid);
                }
                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Log.d("LoginActivity", "--onError" + errorCode);
                }
            });
        }
    }

    @Override
    public void getHttpResponse(Map<String, Object> result) {
        //登录失败
        if((Boolean)result.get("error")){
            String msg = result.get("message").toString();
            if("user not exsist".equals(msg)){
                msg = "用户不存在";
            }else if("pass error".equals(msg)){
                msg = "密码错误";
            }
            Toast.makeText(LoginActivity.this, "登录失败：" + msg, Toast.LENGTH_SHORT).show();
            Log.e("loginError", msg);
        }else{  //登录成功
            int uid = (int) result.get("uid");
            String hashcode = result.get("hashcode").toString();
            Log.i("login", String.valueOf(uid)+" login!");

            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
            //登录成功保存用户信息
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("USER_NAME", userNameValue);
            editor.putString("PASSWORD", passwordValue);
            editor.putInt("USER_ID", uid);
            editor.putString("HASHCODE", hashcode);
            editor.commit();
            //初始化好友列表
            FriendDao dao = new FriendDao();
            dao.initFriends(uid, hashcode, queue);
            //获取用户token
            String token = result.get("token").toString();
            connect(token);
            //启动会话列表界面
            if (RongIM.getInstance() != null)
                RongIM.getInstance().startConversationList(LoginActivity.this);
            LoginActivity.this.finish();
        }
    }

    public void initUI(){
        //获得实例对象
        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        userName = (EditText) findViewById(R.id.et_zh);
        password = (EditText) findViewById(R.id.et_mima);
        rem_pw = (CheckBox) findViewById(R.id.cb_mima);
        auto_login = (CheckBox) findViewById(R.id.cb_auto);
        btn_login = (Button) findViewById(R.id.btn_login);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
