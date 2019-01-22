/*
 * Copyright (c) 2019. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

/*
 * Copyright (c) 2019. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.witdraw;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.ParseException;

/**
 * Created by mark on 19. 1. 16.
 */
public class CurrencyTextWatcher implements TextWatcher {

    public static final String TAG = "CurrencyTextWatcher";
    private final DecimalFormat decimalFormat;
    private final DecimalFormat dfnd;
    private final EditText editText;

    private boolean hasFractionalPart;
    private int trailingZeroCount;

    public CurrencyTextWatcher(EditText editText, String pattern) {
        decimalFormat = new DecimalFormat(pattern);
        decimalFormat.setDecimalSeparatorAlwaysShown(true);
        dfnd = new DecimalFormat("#,###.00");
        this.editText = editText;
        hasFractionalPart = false;
    }

    @Override
    public void afterTextChanged(Editable s) {
        editText.removeTextChangedListener(this);

        if (s != null && !s.toString().isEmpty()) {
            try {
                int firstLen, afterLen;
                firstLen = editText.getText().length();
                String v = s.toString().replace(String.valueOf(decimalFormat.getDecimalFormatSymbols().getGroupingSeparator()), "").replace("$", "");
                Number n = decimalFormat.parse(v);
                int cp = editText.getSelectionStart();
                editText.setText(dfnd.format(n));
                editText.setText("$".concat(editText.getText().toString()));
                afterLen = editText.getText().length();
                int sel = (cp + (afterLen - firstLen));
                if (sel > 0 && sel < editText.getText().length()) {
                    editText.setSelection(sel);
                } else if (trailingZeroCount > -1) {
                    editText.setSelection(editText.getText().length() - 3);
                } else {
                    editText.setSelection(editText.getText().length());
                }
            } catch (NumberFormatException | ParseException e) {
                Log.e(TAG, "afterTextChanged: ", e);
            }
        }

        editText.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int index = s.toString().indexOf(String.valueOf(decimalFormat.getDecimalFormatSymbols().getDecimalSeparator()));
        trailingZeroCount = 0;
        if (index > -1) {
            for (index++; index < s.length(); index++) {
                if (s.charAt(index) == '0') {
                    trailingZeroCount++;
                } else {
                    trailingZeroCount = 0;
                }
            }
            hasFractionalPart = true;
        } else {
            hasFractionalPart = false;
        }
    }
}
