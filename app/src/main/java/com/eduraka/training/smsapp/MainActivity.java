package com.eduraka.training.smsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.LimitColumn;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int CONTACT_PICKER_REQUEST = 202;

    private EditText txt_message;
    private TextView TvNumber;
    private Button btn_Sms,btn_choose,btn_Clear;

    List < ContactResult > results = new ArrayList <> (  );


    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main );

        txt_message = findViewById(R.id.Message);
        TvNumber = findViewById(R.id.Number);

        btn_Sms = findViewById(R.id.Sms);
        btn_choose = findViewById(R.id.Choose);
        btn_Clear = findViewById(R.id.Clear);

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.READ_CONTACTS
                ).withListener(new MultiplePermissionsListener () {
            @Override public void onPermissionsChecked( MultiplePermissionsReport report) {/* ... */}
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();


        btn_Sms.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick ( View v ) {

                String sTextMessage,sNumber;

                sTextMessage = txt_message.getText().toString().trim();
                sNumber = TvNumber.getText ().toString ().trim ();

                if (sTextMessage.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter Msssage " , Toast.LENGTH_SHORT) .show();
                    txt_message.requestFocus();
                    return;

                }
                else if(sNumber.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Select Numbers " , Toast.LENGTH_SHORT) .show();
                    TvNumber.requestFocus();
                    return;
                }
                else
                {
                    try {

                        //  if(results.isEmpty () ) {

                        for (int j=0;j<results.size ();j++) {

                            SmsManager smsManager = SmsManager.getDefault ( );
                            smsManager.sendTextMessage (results.get ( j ).getPhoneNumbers ().get ( 0 ).getNumber () ,null ,txt_message.getText ( ).toString ( ) ,null ,null );
                            Toast.makeText(MainActivity.this,"Sucess",Toast.LENGTH_SHORT).show();
                        }

                        //    }

                    }
                    catch (Exception e){
                        Toast.makeText(MainActivity.this,"SMS Sent Failed!",Toast.LENGTH_SHORT).show();
                    }
                    ClearFields();
                }


            }
        } );


        btn_choose.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick ( View v ) {
                new MultiContactPicker.Builder(MainActivity.this) //Activity/fragment context

                        .hideScrollbar(false) //Optional - default: false
                        .showTrack(true) //Optional - default: true
                        .searchIconColor( Color.WHITE) //Option - default: White
                        .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE) //Optional - default: CHOICE_MODE_MULTIPLE
                        .handleColor( ContextCompat.getColor(MainActivity.this, R.color.colorPrimary)) //Optional - default: Azure Blue
                        .bubbleColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary)) //Optional - default: Azure Blue
                        .bubbleTextColor(Color.WHITE) //Optional - default: White
                        .setTitleText("Select Contacts") //Optional - default: Select Contacts
                        .setLoadingType(MultiContactPicker.LOAD_ASYNC) //Optional - default LOAD_ASYNC (wait till all loaded vs stream results)
                        .limitToColumn( LimitColumn.NONE) //Optional - default NONE (Include phone + email, limiting to one can improve loading time)
                        .setActivityAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                                android.R.anim.fade_in,
                                android.R.anim.fade_out) //Optional - default: No animation overrides
                        .showPickerForResult(CONTACT_PICKER_REQUEST);
            }
        } );

        btn_Clear.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick ( View v ) {
                txt_message.setText("");
                TvNumber.setText("");

            }
        } );


    }

    private void ClearFields(){
        txt_message.setText("");
        TvNumber.setText("");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult ( requestCode ,resultCode ,data );
        if (requestCode == CONTACT_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                results = MultiContactPicker.obtainResult ( data );
                StringBuilder names = new StringBuilder ( results.get ( 0 ).getDisplayName ( ) );

                for (int j = 0; j < results.size ( ); j++) {

                    if (j != 0)
                        names.append ( "," ).append ( results.get ( j ).getDisplayName ( ) );

                }
                TvNumber.setText ( names );
                Log.d ( "MyTag" ,results.get ( 0 ).getDisplayName ( ) );
            } else if (resultCode == RESULT_CANCELED) {
                System.out.println ( "User closed the picker without selecting items." );
            }
        }
    }

    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Exit Now?");
        alertDialogBuilder
                .setMessage("Click Yes to Exit")
                .setCancelable(false)
                .setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        })

                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


}