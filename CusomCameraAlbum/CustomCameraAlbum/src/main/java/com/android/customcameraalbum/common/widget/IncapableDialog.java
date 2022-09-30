/*
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.customcameraalbum.common.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;

import com.android.customcameraalbum.R;


/**
 * 弹窗
 * @author zhongjh
 */
public class IncapableDialog extends DialogFragment {

    private static final String EXTRA_TITLE = "extra_title";
    private static final String EXTRA_MESSAGE = "extra_message";

    private Context mContext;

    public static IncapableDialog newInstance(String title, String message) {
        IncapableDialog dialog = new IncapableDialog();
        Bundle args = new Bundle();
        args.putString(EXTRA_TITLE, title);
        args.putString(EXTRA_MESSAGE, message);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        String title = null;
        String message = null;
        if (bundle != null) {
            title = getArguments().getString(EXTRA_TITLE);
            message = getArguments().getString(EXTRA_MESSAGE);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        if (!TextUtils.isEmpty(message)) {
            builder.setMessage(message);
        }
//        builder.setPositiveButton(R.string.button_ok, (dialog, which) -> dialog.dismiss());

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

}
