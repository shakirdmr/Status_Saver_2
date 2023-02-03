package com.shawlabs.statussaver.Fragments;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.hbb20.CountryCodePicker;
import com.shawlabs.statussaver.R;


public class DirectChatFragment extends Fragment {


    Menu menu;
    EditText phoneNumber;
    CountryCodePicker ccp;
    Button whatsAppSend,waBusinessSend;

    public DirectChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_direct_chat, container, false);


        whatsAppSend = view.findViewById(R.id.whatsAppSend);
        ccp = view.findViewById(R.id.ccp);
        phoneNumber = view.findViewById(R.id.phoneNumber);
        waBusinessSend = view.findViewById(R.id.waBusinessSend);

        whatsAppSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phone = phoneNumber.getText().toString();

                if (phone.length() >9) {
                    String countryCode_number = ccp.getSelectedCountryCode()+phone;
                    openWhatsApp(countryCode_number,1);
                }
                else
                    Toast.makeText(getActivity(), "Enter Valid Number", Toast.LENGTH_SHORT).show();
            }
        });


        waBusinessSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phone = phoneNumber.getText().toString();

                Intent intent = getContext().getPackageManager().getLaunchIntentForPackage("com.whatsapp.w4b");
                if (intent == null)
                {
                    Toast.makeText(getContext(), "App not installed...", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (phone.length() >9) {
                    String countryCode_number = "+"+ccp.getSelectedCountryCode()+phone;
                    openWhatsApp(countryCode_number,2);
                }
                else
                    Toast.makeText(getActivity(), "Enter Valid Number", Toast.LENGTH_SHORT).show();
            }
        });


        setHasOptionsMenu(true);
        return view;
    }


    private void openWhatsApp(String num,int flag) {

        Intent sendIntent = new Intent("android.intent.action.MAIN");

        if (flag == 1)
            sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
        else
            sendIntent.setComponent(new ComponentName("com.whatsapp.w4b", "com.whatsapp.Conversation"));

        sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(num) + "@s.whatsapp.net");//phone number without "+" prefix

        startActivity(sendIntent);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.stateOfApp).setVisible(false);
    }

    void openWhatsappContact(String number, int flag) {

        Uri uri = Uri.parse("smsto:" + number);
        Intent i = new Intent(Intent.ACTION_SENDTO, uri);


        if (flag == 1)
            i.setPackage("com.whatsapp");
        else
            i.setPackage("com.whatsapp.w4b");



        startActivity(Intent.createChooser(i, ""));
    }
}


