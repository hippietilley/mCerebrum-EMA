package org.md2k.ema;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.md2k.datakitapi.time.DateTime;
import org.md2k.ema.broadcast.BroadcastSend;
import org.md2k.ema.fragment.FragmentBase;
import org.md2k.ema.fragment.FragmentHourMinute;
import org.md2k.ema.fragment.FragmentHourMinuteAMPM;
import org.md2k.ema.fragment.FragmentMinuteSecond;
import org.md2k.ema.fragment.FragmentMultipleChoiceSelect;
import org.md2k.ema.fragment.FragmentTextNumeric;
import org.md2k.ema.fragment.NonSwipeableViewPager;
import org.md2k.ema.notification.Notification;

/**
 * Copyright (c) 2015, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p/>
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * <p/>
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p/>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public class ActivityInterview extends ActivityAbstractInterview {
    private static final String TAG = ActivityInterview.class.getSimpleName();
    FragmentBase fragmentBase;
    private NonSwipeableViewPager mPager = null;
    private PagerAdapter mPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        mPager = (NonSwipeableViewPager) findViewById(R.id.view_pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                invalidateOptionsMenu();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mPager != null) {
            getMenuInflater().inflate(R.menu.menu_previous_next, menu);
            menu.findItem(R.id.action_previous).setEnabled(mPager.getCurrentItem() > 0);
            MenuItem item = menu.findItem(R.id.action_next);
            if (mPager.getCurrentItem() == mPagerAdapter.getCount() - 1)
                item.setTitle("Finish");
            else
                item.setTitle("Next");
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                break;
            case R.id.action_previous:
                BroadcastSend.alive(this);
                mPager.getAdapter().notifyDataSetChanged();
                int curQuestion = ema.findValidQuestionPrevious(mPager.getCurrentItem());
                mPager.setCurrentItem(curQuestion);
                ema.getQuestions().get(curQuestion).setPrompt_time(DateTime.getDateTime());
                break;
            case R.id.action_next:
                BroadcastSend.alive(this);
                if (!ema.getQuestions().get(mPager.getCurrentItem()).isValid())
                    Toast.makeText(getBaseContext(), "Please answer the question first", Toast.LENGTH_SHORT).show();
                else if (mPager.getCurrentItem() >= ema.getQuestions().size() - 1)
                    emaEnd(Constants.EMA_COMPLETED);
                else if (ema.getQuestions().get(mPager.getCurrentItem()).isValid()) {
                    mPager.getAdapter().notifyDataSetChanged();
                    ema.getQuestions().get(mPager.getCurrentItem()).setFinish_time(DateTime.getDateTime());
                    curQuestion = ema.findValidQuestionNext(mPager.getCurrentItem());
                    mPager.setCurrentItem(curQuestion);
                    ema.getQuestions().get(curQuestion).setPrompt_time(DateTime.getDateTime());
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPause() {
        Notification.createNotification(this);
        super.onPause();
    }

    @Override
    public void onResume() {
        Notification.cancelNotification(this);
        super.onResume();
    }


    @Override
    public void onDestroy() {
        Notification.cancelNotification(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        MaterialDialog.Builder md = new MaterialDialog.Builder(this)
                .title("Cancel Survey")
                .content("Do you want to cancel?")
                .cancelable(false)
                .positiveText("Yes")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        emaEnd(Constants.EMA_ABANDONED_BY_USER);
                    }
                }).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                });
        md.show();
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "getItem(): position=" + position);
            fragmentBase = FragmentMultipleChoiceSelect.create(position);
            try {
                switch (ema.getQuestions().get(position).getQuestion_type()) {
                    case Constants.MULTIPLE_CHOICE:
                    case Constants.MULTIPLE_SELECT:
                        fragmentBase = FragmentMultipleChoiceSelect.create(position);
                        break;
                    case Constants.TEXT_NUMERIC:
                        fragmentBase = FragmentTextNumeric.create(position);
                        break;
                    case Constants.HOUR_MINUTE:
                        fragmentBase = FragmentHourMinute.create(position);
                        break;
                    case Constants.HOUR_MINUTE_AMPM:
                        fragmentBase = FragmentHourMinuteAMPM.create(position);
                        break;
                    case Constants.MINUTE_SECOND:
                        fragmentBase = FragmentMinuteSecond.create(position);
                        break;
                    default:
                        fragmentBase = FragmentMultipleChoiceSelect.create(position);
                }
            }catch (Exception e){}
            return fragmentBase;
        }

        @Override
        public int getCount() {
            if (ema != null)
                return ema.getQuestions().size();
            else return 0;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}

