package com.example.user.carematch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Fragment_Medical extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View feedBackView = inflater.inflate(R.layout.fragment_products_search, container, false);
        //setContentView(R.layout.fragment_personalfile_feedback);

//        final EditText FeedbackEmail =(EditText) feedBackView.findViewById(R.id.editTextFeedbackEmail);
//        final EditText FeedbackEmailSubject =(EditText) feedBackView.findViewById(R.id.editTextFeedbackSubject);
//        final EditText FeedbackEmailText =(EditText) feedBackView.findViewById(R.id.editTextFeedbackEmailText);
//
//        Button sendFeedback = (Button) feedBackView.findViewById(R.id.buttonSendFeedback);
//        sendFeedback.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String feedbackEmail = FeedbackEmail.getText().toString();
//                String feedbackEmailSubject = FeedbackEmailSubject.getText().toString();
//                String feedbackEmailText = FeedbackEmailText.getText().toString();
//
//                Intent intentEmail = new Intent(Intent.ACTION_SEND);
//
//                intentEmail.putExtra(Intent.EXTRA_EMAIL,new String[]{"mrt_guidance@gmail.com"});
//                intentEmail.putExtra(Intent.EXTRA_SUBJECT,feedbackEmailSubject);
//                intentEmail.putExtra(Intent.EXTRA_TEXT,feedbackEmailText);
//
//                intentEmail.setType("message/rfc822");
//                startActivity(Intent.createChooser(intentEmail,"Choose app to sed mail"));
//            }
//        });

        return feedBackView;
    }
}
