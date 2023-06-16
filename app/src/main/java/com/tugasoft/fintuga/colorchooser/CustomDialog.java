package com.tugasoft.fintuga.colorchooser;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatDialog;

public class CustomDialog extends AppCompatDialog {
    private View view;

    public CustomDialog(Context context, View view2) {
        super(context);
        this.view = view2;
        supportRequestWindowFeature(1);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(this.view);
    }
}
