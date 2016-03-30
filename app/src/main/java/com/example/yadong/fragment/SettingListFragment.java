package com.example.yadong.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yadong.myapplication.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yadong on 16/3/10.
 */
public class SettingListFragment extends Fragment {

    private ListView listView;
    private List<Map<String, Object>> mData;
    private List<String> mTexts;
    private int mCurPos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.settinglist, container, false);
        mData = getData();
        MyAdapter adapter = new MyAdapter(getActivity());
        listView = (ListView) view.findViewById(R.id.lv_setting);
        listView.setAdapter(adapter);

        return view;
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

    /**
     * listview中点击按键弹出对话框
     */
    public void showInfo(){
        Toast.makeText(getActivity(), "NIHAO", Toast.LENGTH_SHORT).show();

    }

    public class MyAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public MyAdapter(Context context) {
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

                holder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.vlist2, null);
                holder.img = (ImageView) convertView.findViewById(R.id.img);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.title.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (mTexts.get(mCurPos) == "添加") {
                            mTexts.set(mCurPos, "待验证");
                            ((TextView) v).setText("待验证");
                        }

                    }
                });

                holder.info = (TextView) convertView.findViewById(R.id.info);
                holder.viewBtn = (Button) convertView.findViewById(R.id.view_btn);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.img.setBackgroundResource((Integer) mData.get(position).get("img"));
//            holder.title.setText((String) mData.get(position).get("title"));
//            Log.d("mTexts Postion:", String.valueOf(position));
//            for (String s : mTexts ){
//                Log.d("mTexts", s);
//            }
            holder.title.setText(mTexts.get(position));
            holder.info.setText((String) mData.get(position).get("info"));

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

