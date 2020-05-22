package com.example.receiptsbooks.ui.activity;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.receiptsbooks.R;
import com.example.receiptsbooks.base.BaseActivity;
import com.example.receiptsbooks.base.BaseFragment;
import com.example.receiptsbooks.ui.fragment.HistoriesFragment;
import com.example.receiptsbooks.ui.fragment.HomeFragment;
import com.example.receiptsbooks.ui.fragment.ListFragment;
import com.example.receiptsbooks.ui.fragment.SettingFragment;
import com.example.receiptsbooks.ui.fragment.SearchFragment;
import com.example.receiptsbooks.ui.fragment.StoreFragment;
import com.example.receiptsbooks.utils.Exit;
import com.example.receiptsbooks.utils.LogUtils;
import com.example.receiptsbooks.utils.SizeUtils;
import com.example.receiptsbooks.utils.ToastUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import butterknife.BindView;
import butterknife.Unbinder;

public class MainActivity extends BaseActivity implements IMainActivity{

    @BindView(R.id.main_navigation_bar)
    public BottomNavigationView mNavigationView;

    @BindView(R.id.main_page_container)
    public FrameLayout mPageContainer;

    private StoreFragment mStoreFragment;
    private HomeFragment mHomeFragment;
    private SettingFragment mSettingFragment;
    private FragmentManager mFm;
    private ListFragment mListFragment;
    private Unbinder mBind;
    private SearchFragment mSearchFragment;
    private HistoriesFragment mHistoriesFragment;
    private Exit mExit;


    @Override
    protected void initView() {
        mExit = new Exit();
        initFragments();
    }

    @Override
    protected void initEvent() {
        initListener();
    }

    private void initListener() {
        mNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //Log.d(TAG, "onNavigationItemSelected title: ==> " + item.getTitle());
                if (item.getItemId() == R.id.nav_home){
                    LogUtils.d(MainActivity.this,"切换到首页");
                    switchFragment(mHomeFragment);
                }else if (item.getItemId() == R.id.nav_store){
                    LogUtils.d(MainActivity.this,"切换到领卷");
                    switchFragment(mStoreFragment);
                }else if (item.getItemId() == R.id.nav_list){
                    LogUtils.d(MainActivity.this,"切换到清单");
                    switchFragment(mListFragment);
                }else if (item.getItemId() == R.id.nav_setting){
                    LogUtils.d(MainActivity.this,"切换到设置");
                    switchFragment(mSettingFragment);
                }
                return true;
            }
        });
    }

    @Override
    protected void initPresenter() {

    }

    /**
     * 销毁时，解除绑定
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBind != null) {
            mBind.unbind();
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    private void initFragments() {
        mHomeFragment = new HomeFragment();
        mStoreFragment = new StoreFragment();
        mSettingFragment = new SettingFragment();
        mListFragment = new ListFragment();
        mSearchFragment = new SearchFragment();
        mHistoriesFragment = new HistoriesFragment();
        mFm = getSupportFragmentManager();
        //默认首页为起始页面
        switchFragment(mHomeFragment);
    }

    /**
     * 用以保存上次跳转的fragment，方便判断是否重复跳转
     */
    private BaseFragment lastOneFragment = null;

    private void switchFragment(BaseFragment targetFragment) {
        //判断是否和上次的fragment一样
        if (targetFragment == lastOneFragment){
            return;
        }
        //使用add和hide、show的方式修改fragment，用以保存跳转时加载的数据
        FragmentTransaction transaction = mFm.beginTransaction();
        if (!targetFragment.isAdded()){
            transaction.add(R.id.main_page_container,targetFragment);
        }else{
            transaction.show(targetFragment);
        }
        if (lastOneFragment != null){
            transaction.hide(lastOneFragment);
        }
        lastOneFragment = targetFragment;
        //提交事务
        transaction.commit();
    }


    /**
     * 监听返回键
     */
    @Override
    public void onBackPressed() {
        if(lastOneFragment == mHistoriesFragment) {
            switch2Home();
        } else if(lastOneFragment == mSearchFragment) {
            switch2Store();
        }else{
            // handle by activity
            pressAgainExit();
        }
    }

    private void pressAgainExit() {
        if (mExit.isExit()) {
            finish();
        } else {
            ToastUtil.showToast("再按一次退出程序");
            mExit.doExitInOneSecond();
        }
    }

    /**
     * 切换到搜索页面
     */
    @Override
    public void switch2Search() {
        switchFragment(mSearchFragment);
    }

    /**
     * 切换到领卷页面
     */
    @Override
    public void switch2Store() {
        //切换导航栏的选中项
        switchFragment(mStoreFragment);
    }

    private void hideBottomNavigation() {
        ViewGroup.LayoutParams layoutParams = mPageContainer.getLayoutParams();
        layoutParams.height -= SizeUtils.dip2px(this, 50);
        mNavigationView.setVisibility(View.GONE);
    }

    private void restoreBottomNavigation() {
        ViewGroup.LayoutParams layoutParams = mPageContainer.getLayoutParams();
        layoutParams.height += SizeUtils.dip2px(this, 50);
        mNavigationView.setVisibility(View.VISIBLE);
    }

    /**
     * 切换到历史页面
     */
    @Override
    public void switch2Histories() {
        //切换导航栏的选中项
        //hideBottomNavigation();
        mNavigationView.setVisibility(View.GONE);
        switchFragment(mHistoriesFragment);
    }

    @Override
    public void switch2Home() {
        //切换导航栏的选中项
        //restoreBottomNavigation();
        mNavigationView.setVisibility(View.VISIBLE);

        switchFragment(mHomeFragment);
    }

    /**
     * 重写这个方法，并且交给Fragment处理
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mHomeFragment.onActivityResult(requestCode,resultCode,data);
    }
}