package com.bignerdranch.beatbox;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Tasin Ishmam on 6/6/2018.
 */
public class NamePickerFragment extends android.support.v4.app.DialogFragment {

    AppCompatEditText mNameEditText;

    public static final String Extra_Key_Name = "com.bignerdranch.beatbox.EXTRA_KEY_NAME";

    public static NamePickerFragment newInstance()
    {
        return new NamePickerFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_name_picker,null);

        mNameEditText = (AppCompatEditText)  v.findViewById(R.id.chain_sound_name_edit_text);

      AlertDialog.Builder builder = new AlertDialog.Builder(getContext());


                builder.setView(v)
              .setTitle(R.string.enter_chain_name)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               String name = mNameEditText.getText().toString();


                                   sendResult(Activity.RESULT_OK, name);

                            }
                        })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = "";
                        sendResult(Activity.RESULT_CANCELED, name);
                    }
                });

               final AlertDialog dialog = builder.create();


        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.getWindow().setDimAmount(0.3f);
        dialog.setCancelable(true);


    return dialog;
    }

    private void sendResult(int resultCode, String name) {
        if(getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(Extra_Key_Name, name);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
