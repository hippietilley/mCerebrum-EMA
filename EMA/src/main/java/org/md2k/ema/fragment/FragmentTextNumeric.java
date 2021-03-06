package org.md2k.ema.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

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
public class FragmentTextNumeric extends FragmentBase {
    EditText editText;
    public static FragmentTextNumeric create(int pageNumber) {
        FragmentTextNumeric fragment = new FragmentTextNumeric();
        fragment.setArguments(getArgument(pageNumber));
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void setEditTextFocused() {
        if (editText.getText().toString().equals(Constants.TAP)) {
            editText.setText("");
        }
        editText.setTextColor(getResources().getColor(android.R.color.black));
        updateNext(false);
    }

    void setEditTextNotFocused() {
        if (editText.getText().toString().length() == 0) {
            editText.setText(Constants.TAP);
        }
        if (editText.getText().toString().equals(Constants.TAP))
            editText.setTextColor(getResources().getColor(R.color.teal_100));
        else editText.setTextColor(getResources().getColor(android.R.color.black));
    }

    void setEditText(ViewGroup rootView) {
        editText = (EditText) rootView.findViewById(R.id.editTextNumber);
        setEditTextNotFocused();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String response = editText.getText().toString();
                response = response.trim();
                ArrayList<String> responses = new ArrayList<>();
                responses.add(response);
                if (!response.equals(Constants.TAP) && response.length() != 0) {
                    question.setResponse(responses);
                } else question.getResponse().clear();

                updateNext(isAnswered());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    setEditTextFocused();
                else setEditTextNotFocused();

            }
        });
    }

    @Override
    public void onPause() {
        hideKeyboard();
        super.onPause();
    }

    public boolean isAnswered() {
        int lowerLimit = 0, higherLimit = 0;
        boolean lv = false, rv = false;
        if (question.getResponse_option().size() > 0) {
            lowerLimit = Integer.parseInt(question.getResponse_option().get(0));
            lv = true;
        }
        if (question.getResponse_option().size() > 1) {
            higherLimit = Integer.parseInt(question.getResponse_option().get(1));
            rv = true;
        }
        if (question.getResponse().size() > 0) {
            try {
                int num = Integer.parseInt(question.getResponse().get(0));
                if (lv && num < lowerLimit) {
                    Toast.makeText(getActivity(),"Value must be in between "+lowerLimit+" and "+ higherLimit,Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (rv && num > higherLimit){
                    Toast.makeText(getActivity(),"Value must be in between "+lowerLimit+" and "+ higherLimit,Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            }catch(Exception e){
                Toast.makeText(getActivity(),"Value must be in between "+lowerLimit+" and "+ higherLimit,Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
//            Toast.makeText(getActivity(),"Value must be in between "+lowerLimit+" and "+ higherLimit,Toast.LENGTH_SHORT).show();
            return false;
        }
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
                .inflate(R.layout.fragment_text_numeric, container, false);
        setQuestionText(rootView, question);
        setEditText(rootView);
        return rootView;
    }
}
