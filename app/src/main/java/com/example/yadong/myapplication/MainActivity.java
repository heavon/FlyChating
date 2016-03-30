package com.example.yadong.myapplication;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.yadong.InterfaceClasses.HttpResponse;
import com.example.yadong.adapter.MainPagerAdapter;
import com.example.yadong.dao.FriendDao;
import com.example.yadong.dao.UserDao;
import com.example.yadong.fragment.ConversationListDynamicFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends FragmentActivity {

    private ListView listView;
    private List<Map<String, Object>> mData;
    private List<String> mTexts;
    private int mCurPos;

    SharedPreferences sp;
    RequestQueue mQueue;
    private  String token = "SxR+68BYqv8WSYb2WQfvwZroheIFeAUDkLDZ/eSXzt69eeq8uN6UAi5w4xaZbPQtG7jYSXznkDw=";

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
                    Log.d("MainActivity", "--onTokenIncorrect");
                }
                /**
                 * 连接融云成功
                 * @param userid 当前 token
                 */
                @Override
                public void onSuccess(String userid) {
                    Log.d("MainActivity", "--onSuccess" + userid);
                }
                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Log.d("MainActivity", "--onError" + errorCode);
                }
            });
        }
    }

    public void initToken(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQueue = Volley.newRequestQueue(MainActivity.this);
        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        final Boolean autoLogin = sp.getBoolean("AUTO_ISCHECK", false);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                if(autoLogin){
                    UserDao dao = new UserDao();
                    String username = sp.getString("USER_NAME", "");
                    String password = sp.getString("PASSWORD", "");
                    dao.login(username, password, mQueue, new HttpResponse<Map<String, Object>>() {
                        @Override
                        public void getHttpResponse(Map<String, Object> result) {
                            if((Boolean)result.get("error")){
                                gotoLogin();
                            }else{
                                int uid = (int) result.get("uid");
                                String hashcode = result.get("hashcode").toString();
                                Log.i("login", String.valueOf(uid)+" login!");
                                //登录成功保存登录信息
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putInt("USER_ID", uid);
                                editor.putString("HASHCODE", hashcode);
                                editor.commit();
                                //初始化好友列表
                                FriendDao dao = new FriendDao();
                                dao.initFriends(uid, hashcode, mQueue);
                                //获取用户token
                                token = result.get("token").toString();
                                connect(token);
                                //启动会话列表界面
                                if (RongIM.getInstance() != null)
                                    RongIM.getInstance().startConversationList(MainActivity.this);
                                MainActivity.this.finish();
                            }
                        }
                    });

                }else{
                    gotoLogin();
                }
//                //启动会话列表界面
//                if (RongIM.getInstance() != null)
//                    RongIM.getInstance().startConversationList(MainActivity.this);

            }
        }, 2000); //2000 for release
//

    }

    public void gotoLogin(){
        //跳转到登录页面
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        MainActivity.this.startActivity(intent);
        MainActivity.this.finish();
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();

        for(int i=0; i<5; i++){

            map.put("title", "G1");
            map.put("info", "google 1");
            map.put("img", R.drawable.abc_btn_switch_to_on_mtrl_00001);
            list.add(map);
        }
        for(int i=0; i<5; i++){
            map = new HashMap<String, Object>();
            map.put("title", "G2");
            map.put("info", "google 2");
            map.put("img", R.drawable.abc_btn_check_to_on_mtrl_000);
            list.add(map);
        }
        for(int i=0; i<5; i++){
            map = new HashMap<String, Object>();
            map.put("title", "G3");
            map.put("info", "google 3");
            map.put("img", R.drawable.abc_btn_check_to_on_mtrl_015);
            list.add(map);
        }

        String str = "添加";
        mTexts = new ArrayList<String>();
        for(int i = 0; i < 15; i++){
            mTexts.add(str);
        }

        return list;
    }


//    // ListView 中某项被选中后的逻辑
//    @Override
//    protected void onListItemClick(ListView l, View v, int position, long id) {
//        Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
//        Log.v("MyListView4-click", (String) mData.get(position).get("title"));
//    }

    /**
     * listview中点击按键弹出对话框
     */
    public void showInfo(){
        Toast.makeText(this, "NIHAO", Toast.LENGTH_SHORT).show();

    }



    public final class ViewHolder{
        public ImageView img;
        public TextView title;
        public TextView info;
        public Button viewBtn;
    }

    public class MyAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public MyAdapter(Context context){
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mData.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            mCurPos = position;
            if (convertView == null) {

                holder=new ViewHolder();

                convertView = mInflater.inflate(R.layout.vlist2, null);
                holder.img = (ImageView)convertView.findViewById(R.id.img);
                holder.title = (TextView)convertView.findViewById(R.id.title);
                holder.title.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        if(mTexts.get(mCurPos) == "添加"){
                            mTexts.set(mCurPos, "待验证");
                            ((TextView)v).setText("待验证");
                        }

                    }
                });

                holder.info = (TextView)convertView.findViewById(R.id.info);
                holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
                convertView.setTag(holder);

            }else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.img.setBackgroundResource((Integer)mData.get(position).get("img"));
//            holder.title.setText((String) mData.get(position).get("title"));
//            Log.d("mTexts Postion:", String.valueOf(position));
//            for (String s : mTexts ){
//                Log.d("mTexts", s);
//            }
            holder.title.setText(mTexts.get(position));
            holder.info.setText((String)mData.get(position).get("info"));

            holder.viewBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    showInfo();
                }
            });
            return convertView;
        }
    }
}
