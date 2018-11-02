package com.sty.viewpager.tab;

import android.animation.ObjectAnimator;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sty.viewpager.tab.adapter.MyViewPagerAdapter;
import com.sty.viewpager.tab.view.CustomTabSliding;
import com.sty.viewpager.tab.view.LevelData;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LinearLayout llLevelBar;
    private CustomTabSliding ctsLevelBar;
    private LinearLayout llViewPager;
    private ViewPager viewPager;

    private List<LevelData> levelDataList;
    private List<View> views;
    private int listSize;
    private int curGrowthValue;
    private int curLevel;
    private float curPercent = 0f;
    private int curPage; //从0开始，但是总比curLevel小1（0除外）

    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initView();
    }

    private void initData(){
        levelDataList = new LinkedList<>();
        for(int i = 0; i < 10; i++){
            LevelData levelData = new LevelData();
            levelData.setGrade(i);
            levelData.setNeed_growth_value((i + 1) * 1000);
            levelData.setDescription("等级" + i + "级");
            levelDataList.add(levelData);
        }

        curGrowthValue = 3600;
        for(int i = 0; i < levelDataList.size(); i++){
            LevelData levelData = levelDataList.get(i);
            LevelData levelNextData = levelDataList.get(i+1 >= levelDataList.size() ? i : i+1);
            int growthValue = levelData.getNeed_growth_value();
            int nextGrowthValue = levelNextData.getNeed_growth_value();
            if(curGrowthValue == growthValue){
                curLevel = levelData.getGrade();
                break;
            }else if(growthValue < curGrowthValue && curGrowthValue < nextGrowthValue){
                curLevel = i;
                curPercent = (float) (curGrowthValue - growthValue) / (nextGrowthValue - growthValue);
                break;
            }
        }
    }

    private void initView(){
        llLevelBar = findViewById(R.id.ll_level_bar);
        ctsLevelBar = findViewById(R.id.cts_level_bar);
        llViewPager = findViewById(R.id.ll_view_pager);
        viewPager = findViewById(R.id.viewpager);

        initCtsBar();
        initViewPager();
    }

    private void initCtsBar(){
        if(levelDataList != null && levelDataList.size() > 0) {
            listSize = levelDataList.size();
            String[] firstText = new String[listSize];
            String[] secondText = new String[listSize];
            for (int i = 0; i < listSize; i++) {
                LevelData levelData = levelDataList.get(i);
                firstText[i] = "Lv." + levelData.getGrade() + " " + levelData.getDescription();
                secondText[i] = levelData.getNeed_growth_value() + "成长值";
            }

            ctsLevelBar.setNumber(listSize);
            ctsLevelBar.setFirstStrings(firstText);
            ctsLevelBar.setSecondStrings(secondText);
            ctsLevelBar.setCurCircle(curLevel);
            ctsLevelBar.setCurPercent(curPercent);

            ctsLevelBar.setCurPage(curLevel == 0 ? 1 : curLevel);
            curPage = curLevel == 0 ? 0 : curLevel - 1;
            if (listSize > 1) {
                startAnimator(curLevel == 0 ? 0 : curLevel - 1); //向左滑动 curLevel - 1 页（滑动到当前等级对应的圆点）
            }
            llLevelBar.setVisibility(View.VISIBLE);
        }else{
            llLevelBar.setVisibility(View.GONE);
        }

    }

    private void initViewPager(){
        if(levelDataList != null && levelDataList.size() > 0) {
            views = new ArrayList<>();
            for (int i = 1; i < listSize; i++) {
                View itemView = LayoutInflater.from(this).inflate(R.layout.item_view_pager, null);
                TextView tvContent = (TextView) itemView.findViewById(R.id.tv_content);
                tvContent.setText("page" + i);
                views.add(itemView);
            }

            pagerAdapter = new MyViewPagerAdapter(views);
            viewPager.setAdapter(pagerAdapter);
            viewPager.setOffscreenPageLimit(3);
            viewPager.setPageMargin(20);
            viewPager.setOverScrollMode(ViewPager.OVER_SCROLL_NEVER);
            viewPager.setCurrentItem(curLevel == 0 ? 0 : curLevel - 1);
            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    int movePage = position - curPage;
                    curPage = position;
                    startAnimator(movePage);
                    ctsLevelBar.setCurPage(curPage + 1);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            llViewPager.setVisibility(View.VISIBLE);
        }else{
            llViewPager.setVisibility(View.GONE);
        }
    }

    private void startAnimator(int movePage){
        float curTranslationX = ctsLevelBar.getTranslationX();
        final ObjectAnimator animator = ObjectAnimator.ofFloat(ctsLevelBar, "translationX",
                curTranslationX, curTranslationX - movePage * ctsLevelBar.getLineLength());
//        Log.i("sty", "movePage:" + movePage + "(LineLength:" + ctsLevelBar.getLineLength()+ ")");
//        Log.i("sty", "curTranslationX:" + curTranslationX + "(delta:" + (curTranslationX - movePage * ctsLevelBar.getLineLength()) + ")");
        animator.setDuration(150);
        animator.start();
    }
}
