package com.example.yadong.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yadong.myapplication.LoginActivity;
import com.example.yadong.myapplication.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Yadong on 16/3/10.
 */
public class PersonalCenterFragment extends Fragment {

    private ListView lv_personalcenter;
    private List<HashMap<String, Object>> itemData;
    private List<Map<String, Object>> mData;
    private List<String> mTexts;
    private int mCurPos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.personalcenter, container, false);

        lv_personalcenter = (ListView) view.findViewById(R.id.lv_personalcenter);
        itemData = getItemData();
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), itemData, R.layout.pc_item, new String[]{"iv_icon", "tv_text"}, new int[]{R.id.iv_icon, R.id.tv_text} );
        lv_personalcenter.setAdapter(adapter);
        lv_personalcenter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:logout();
                        default:break;
                }
            }
        });

        return view;
    }

    public List<HashMap<String, Object>> getItemData(){
        List<HashMap<String , Object>> itemdatas = new ArrayList<HashMap<String, Object>>();
        for(int i = 0; i < 1; i++){
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("iv_icon", R.drawable.ic_logout);
            map.put("tv_text", "退出");
            itemdatas.add(map);
        }

        return itemdatas;
    }

    public void logout(){
//        SharedPreferences sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

}

