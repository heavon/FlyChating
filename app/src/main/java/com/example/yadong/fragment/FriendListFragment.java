package com.example.yadong.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.yadong.InterfaceClasses.HttpResponse;
import com.example.yadong.dao.FriendDao;
import com.example.yadong.dao.UserDao;
import com.example.yadong.myapplication.R;
import com.example.yadong.utils.HttpUtils;
import com.example.yadong.vo.Friend;
import com.example.yadong.vo.User;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

/**
 * Created by Yadong on 16/3/10.
 */
public class FriendListFragment extends Fragment implements RongIM.UserInfoProvider {

    private ListView listView;
    private List<Friend> mFriends = new ArrayList<Friend>();

    private int mCurPos;
    private int mId = 0;
    private String mHashcode = "";

    private RequestQueue mQueue;

    public FriendListFragment() {
        RongIM.setUserInfoProvider(this, true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.friendlist, container, false);
        mQueue = Volley.newRequestQueue(getActivity());
        HttpUtils http = new HttpUtils(mQueue);

        //获取登录信息
        SharedPreferences sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        mId = sp.getInt("USER_ID", 0);
        mHashcode = sp.getString("HASHCODE", "");

        listView = (ListView) view.findViewById(R.id.lv_friend);
        //获取好友列表
        FriendDao dao = new FriendDao();
        dao.getFriends(mId, mHashcode, mQueue, new HttpResponse<List<Friend>>() {
            @Override
            public void getHttpResponse(List<Friend> friends) {
                mFriends = friends;
                //设置listview显示好友列表
                FriendAdapter adapter = new FriendAdapter(getActivity());
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (RongIM.getInstance() != null) {
                            Friend friend = mFriends.get(position);
                            RongIM.getInstance().startConversation(getActivity(), Conversation.ConversationType.PRIVATE, String.valueOf(friend.getUid()), friend.getName());
                        }

                    }
                });
            }
        });

        return view;
    }

    /**
     * listview中点击按键弹出对话框
     */
    public void showInfo() {
        Toast.makeText(getActivity(), "NIHAO", Toast.LENGTH_SHORT).show();

    }

    //设置内容提供者
    @Override
    public UserInfo getUserInfo(String s) {
        for (Friend friend : mFriends) {

            String id = String.valueOf(friend.getUid());
            if (id.equals(s)) {
                return new UserInfo(s, friend.getName(), null);
            }
        }

        return null;
    }

    class FriendHolder {
        public ImageView avatar;
        public TextView fid;
        public TextView fname;
    }

    public class FriendAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public FriendAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mFriends.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return mFriends.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            FriendHolder holder = null;
            mCurPos = position;
            if (convertView == null) {

                holder = new FriendHolder();

                convertView = mInflater.inflate(R.layout.friend, null);
                holder.fid = (TextView) convertView.findViewById(R.id.friend_id);
                holder.fname = (TextView) convertView.findViewById(R.id.friend_name);

//                holder.avatar = (TextView)convertView.findViewById(R.id.avatar);
                convertView.setTag(holder);

            } else {
                holder = (FriendHolder) convertView.getTag();
            }
//            holder.img.setBackgroundResource((Integer)mFriends.get(position).get("img"));
////            holder.title.setText((String) mData.get(position).get("title"));
////            Log.d("mTexts Postion:", String.valueOf(position));
////            for (String s : mTexts ){
////                Log.d("mTexts", s);
////            }
            holder.fid.setText(String.valueOf(mFriends.get(position).getUid()));
            holder.fname.setText(mFriends.get(position).getName());

//            holder.viewBtn.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    showInfo();
//                }
//            });
            return convertView;
        }
    }
}

