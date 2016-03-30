package com.example.yadong.myapplication;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.yadong.Views.ChangeColorIconWithTextView;
import com.example.yadong.adapter.MainPagerAdapter;
import com.example.yadong.fragment.ConversationListDynamicFragment;
import com.example.yadong.fragment.FriendListFragment;
import com.example.yadong.fragment.PersonalCenterFragment;
import com.example.yadong.fragment.SettingListFragment;
import com.example.yadong.utils.HttpUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;


public class ConversationListActivity extends FragmentActivity implements View.OnClickListener {

    private ViewPager mViewPager;
    private MainPagerAdapter adapter;
    private List<Fragment> fragmentlist;
    private ConversationListFragment mConvListFrag;
    private TextView mTitle;
    private List<ChangeColorIconWithTextView> mTabIndicator = new ArrayList<ChangeColorIconWithTextView>();

    private ViewPager.OnPageChangeListener ViewPageListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            Log.e("TAG", "position = " + position + " , positionOffset = "
//                    + positionOffset);

            if (positionOffset > 0) {
                ChangeColorIconWithTextView left = mTabIndicator.get(position);
                ChangeColorIconWithTextView right = mTabIndicator.get(position + 1);

                left.setIconAlpha(1 - positionOffset);
                right.setIconAlpha(positionOffset);
            }

        }

        @Override
        public void onPageSelected(int arg0) {
            String text = mTabIndicator.get(arg0).getText();
            mTitle.setText(text);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversationlist);

        InitUI();
        FriendListFragment friendFrag = new FriendListFragment();
        ConversationListDynamicFragment convFrag = new ConversationListDynamicFragment();
        SettingListFragment settingFrag = new SettingListFragment();
        PersonalCenterFragment personFrag = new PersonalCenterFragment();
        //配置ViewPager
        fragmentlist = new ArrayList<Fragment>();
        fragmentlist.add(convFrag);
        fragmentlist.add(friendFrag);
        fragmentlist.add(settingFrag);
        fragmentlist.add(personFrag);

        mViewPager = (ViewPager) findViewById(R.id.vp);
        mViewPager.addOnPageChangeListener(ViewPageListener);
        adapter = new MainPagerAdapter(super.getSupportFragmentManager(), fragmentlist);
        mViewPager.setAdapter(adapter);



    }

    public void InitUI() {
//        mTab1 = (TextView) findViewById(R.id.tv_left);
//        mTab2 = (TextView) findViewById(R.id.tv_middle);
//        mTab3 = (TextView) findViewById(R.id.tv_right);
//
//        mTab1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //启动会话界面
//                if (RongIM.getInstance() != null)
//                    RongIM.getInstance().startPrivateChat(ConversationListActivity.this, "26594", "title");
//            }
//        });
//        mTab2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //启动会话列表界面
//                if (RongIM.getInstance() != null)
//                    RongIM.getInstance().startConversationList(ConversationListActivity.this);
//            }
//        });
//        mTab3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //启动聚合会话列表界面
//                if (RongIM.getInstance() != null)
//                    RongIM.getInstance().startSubConversationList(ConversationListActivity.this, Conversation.ConversationType.GROUP);
//            }
//        });
        mTitle = (TextView) findViewById(R.id.tv_title);
        initTabIndicator();
    }

    /**
     * 初始化底部导航
     */
    private void initTabIndicator() {
        ChangeColorIconWithTextView one = (ChangeColorIconWithTextView) findViewById(R.id.id_indicator_one);
        ChangeColorIconWithTextView two = (ChangeColorIconWithTextView) findViewById(R.id.id_indicator_two);
        ChangeColorIconWithTextView three = (ChangeColorIconWithTextView) findViewById(R.id.id_indicator_three);
        ChangeColorIconWithTextView four = (ChangeColorIconWithTextView) findViewById(R.id.id_indicator_four);

        mTabIndicator.add(one);
        mTabIndicator.add(two);
        mTabIndicator.add(three);
        mTabIndicator.add(four);

        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);

        one.setIconAlpha(1.0f);
    }

    /**
     * 点击事件，给底部导航添加点击事件
     *
     * @param v 被点击的view
     */
    @Override
    public void onClick(View v) {

        resetOtherTabs();

        switch (v.getId()) {
            case R.id.id_indicator_one:
                mTabIndicator.get(0).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(0, false);
                break;
            case R.id.id_indicator_two:
                mTabIndicator.get(1).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(1, false);
                break;
            case R.id.id_indicator_three:
                mTabIndicator.get(2).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(2, false);
                break;
            case R.id.id_indicator_four:
                mTabIndicator.get(3).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(3, false);
                break;
        }
    }

    /**
     * 重置其他的Tab
     */
    private void resetOtherTabs() {
        for (int i = 0; i < mTabIndicator.size(); i++) {
            mTabIndicator.get(i).setIconAlpha(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_conversation_list, menu);
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
