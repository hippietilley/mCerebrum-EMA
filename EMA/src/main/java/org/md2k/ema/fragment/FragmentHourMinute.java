package org.md2k.ema.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import org.md2k.ema.R;

import java.util.ArrayList;
import java.util.Locale;


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

public class FragmentHourMinute extends FragmentBase {
    NumberPicker numberPickerHour, numberPickerMinute;
    public static FragmentHourMinute create(int pageNumber) {
        FragmentHourMinute fragment = new FragmentHourMinute();
        fragment.setArguments(getArgument(pageNumber));
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void setHourMinute(ViewGroup rootView) {
        numberPickerHour= (NumberPicker) rootView.findViewById(R.id.numberPickerHour);
        numberPickerHour.setMaxValue(23);
        numberPickerHour.setMinValue(0);
        numberPickerMinute = (NumberPicker) rootView.findViewById(R.id.numberPickerMinute);
        numberPickerMinute.setMaxValue(59);
        numberPickerMinute.setMinValue(0);
        if(question.getResponse()==null || question.getResponse().size()==0) {
            ArrayList<String> s = new ArrayList<>();
            s.add("00:00:00");
            question.setResponse(s);
        }
        String s=question.getResponse().get(0);
        String split[]=s.split(":");
        int hour=Integer.valueOf(split[0]);
        int min=Integer.valueOf(split[1]);
        numberPickerHour.setValue(hour);
        numberPickerMinute.setValue(min);
        updateNext(true);

        numberPickerHour.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                ArrayList<String> s=new ArrayList<>();
                s.add(String.format(Locale.US, "%02d:%02d:00",numberPickerHour.getValue(), numberPickerMinute.getValue()));
                question.setResponse(s);
                updateNext(true);
            }
        });
        numberPickerMinute.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                ArrayList<String> s=new ArrayList<>();
                s.add(String.format(Locale.US, "%02d:%02d:00",numberPickerHour.getValue(), numberPickerMinute.getValue()));
                question.setResponse(s);
                updateNext(true);
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        updateNext(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_hour_minute, container, false);
        setQuestionText(rootView, question);
        setHourMinute(rootView);
        return rootView;
    }
}
