package com.example.cmpt276group05.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.example.cmpt276group05.R;
//custom BaseDialog
public class BaseDialog extends Dialog {
    private int layoutUri;

    public BaseDialog(Context context, int layoutUri) {
        super(context, R.style.AppDialogTheme);
        this.layoutUri = layoutUri;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(this.layoutUri);
        this.setCanceledOnTouchOutside(false);
    }
}
