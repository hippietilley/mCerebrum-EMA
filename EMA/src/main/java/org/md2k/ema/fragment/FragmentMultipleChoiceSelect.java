package org.md2k.ema.fragment;

import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.md2k.ema.Constants;
import org.md2k.ema.R;

import java.util.ArrayList;


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

public class FragmentMultipleChoiceSelect extends FragmentBase {
    private static final String TAG = FragmentMultipleChoiceSelect.class.getSimpleName();
    public static final String ITEM_UNSELECT_OTHER = "<UNSELECT_OTHER>";
    ListView listView;
    TextView textViewPleaseSelect;
    ArrayAdapter<String> adapter;

    public static FragmentMultipleChoiceSelect create(int pageNumber) {
        FragmentMultipleChoiceSelect fragment = new FragmentMultipleChoiceSelect();
        fragment.setArguments(getArgument(pageNumber));
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void setTypeMultipleChoiceSelect() {
        if (question.isType(Constants.MULTIPLE_CHOICE))
            listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        else
            listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        String options[] = new String[question.getResponse_option().size()];
        for (int i = 0; i < question.getResponse_option().size(); i++) {
            options[i] = question.getResponse_option().get(i);
            if (question.getResponse_option().get(i).contains(ITEM_UNSELECT_OTHER)) {
                options[i] = question.getResponse_option().get(i).replaceAll(ITEM_UNSELECT_OTHER, "");
            }
            Log.d(TAG, options[i]);
        }

        if (question.isType(Constants.MULTIPLE_CHOICE))
            adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_single_choice, options);
        else
            adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, options);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        for (int i = 0; i < question.getResponse_option().size(); i++)
            if (question.isResponseExist(question.getResponse_option().get(i)))
                listView.setItemChecked(i, true);
        else listView.setItemChecked(i, false);
        if (question.isValid()) updateNext(true);
        else updateNext(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                ArrayList<String> response = new ArrayList<>();
                int len = listView.getCount();
                SparseBooleanArray checked = listView.getCheckedItemPositions();
                if (question.getResponse_option().get(position).contains(ITEM_UNSELECT_OTHER)) {
                    for (int i = 0; i < len; i++)
                        if (i != position)
                            listView.setItemChecked(i, false);
                    if (listView.isItemChecked(position))
                        response.add((String) listView.getItemAtPosition(position));
                } else {
                    for (int i = 0; i < len; i++)
                        if (checked.get(i)) {
                            if (question.getResponse_option().get(i).contains(ITEM_UNSELECT_OTHER)) {
                                listView.setItemChecked(i, false);
                            } else {
                                String item = (String) listView.getItemAtPosition(i);
                                response.add(item);
                            }
                        }
                }
                question.setResponse(response);
                if (question.isValid())
                    updateNext(true);
                else updateNext(false);

            }
        });
    }

    public boolean isAnswered() {
        return question.getQuestion_type() == null || !(question.getQuestion_type().equals(Constants.MULTIPLE_SELECT) ||
                question.getQuestion_type().equals(Constants.MULTIPLE_CHOICE)) || question.getResponse().size() > 0;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        updateNext(isAnswered());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_choice_select, container, false);
        listView = (ListView) rootView.findViewById(R.id.listView_options);
        textViewPleaseSelect= (TextView) rootView.findViewById(R.id.textView_please_select);
        setQuestionText(rootView, question);
        updateView();
        return rootView;
    }
    public void updateView(){
        Log.d(TAG,"updateView()...");
        if (question.isType(Constants.MULTIPLE_CHOICE) || question.isType(Constants.MULTIPLE_SELECT)) {
            setTypeMultipleChoiceSelect();
        } else {
            textViewPleaseSelect.setVisibility(View.GONE);
        }

    }

}
