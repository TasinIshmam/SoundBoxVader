package com.bignerdranch.beatbox;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

public class ChangeLogDialogFragment extends android.support.v4.app.DialogFragment {

public static ChangeLogDialogFragment newInstance() {
    return new ChangeLogDialogFragment();
}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {



        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_change_log, null);



       AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());


                builder.setView(v)
                .setTitle("Updates for Version 2.0")
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dismiss();
                            }
                        });

                final AlertDialog dialog = builder.create();

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.getWindow().setDimAmount(0.3f);

        return  dialog;


    }


}
