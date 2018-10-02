package com.bignerdranch.beatbox;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

/**
 * Created by Tasin Ishmam on 6/6/2018.
 */
public class ChainDeleteConfirmFragment extends android.support.v4.app.DialogFragment {

    EditText mNameEditText;

    public static final String EXTRA_KEY_POS = "com.bignerdranch.beatbox.EXTRA_KEY_POS";
    public static final String Extra_Key_STRING = "com.bignerdranch.beatbox.Extra_Key_STRING";


    public static ChainDeleteConfirmFragment newInstance(int position, String name)
    {
        Bundle args = new Bundle();
        args.putInt(EXTRA_KEY_POS, position);
        args.putString(Extra_Key_STRING, name);

        ChainDeleteConfirmFragment fragment = new ChainDeleteConfirmFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final int pos = getArguments().getInt(EXTRA_KEY_POS);
        final String name = getArguments().getString(Extra_Key_STRING);


        Dialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Are you sure you want to delete " + name + "?")
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {



                                sendResult(Activity.RESULT_OK, pos, name);
                            }
                        })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_CANCELED, pos, name);
                    }
                })
                .create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.getWindow().setDimAmount(0);

        return dialog;
    }

    private void sendResult(int resultCode, int pos, String name) {
        if(getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_KEY_POS, pos);
        intent.putExtra(Extra_Key_STRING, name);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
