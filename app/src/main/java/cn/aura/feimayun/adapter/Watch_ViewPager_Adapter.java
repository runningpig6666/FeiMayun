package cn.aura.feimayun.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * 描述：录播详情界面，播放器下方的ViewPager适配器
 */
public class Watch_ViewPager_Adapter extends FragmentPagerAdapter {
    private static final String titleName[] = new String[]{"简介", "聊天"};
    private List<Fragment> fragments;

    public Watch_ViewPager_Adapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments == null ? 0 : fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleName[position];
    }
}
