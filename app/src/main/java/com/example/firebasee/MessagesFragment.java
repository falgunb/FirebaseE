package com.example.firebasee;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.firebasee.model.Data;
import com.example.firebasee.model.MyResponse;
import com.example.firebasee.model.NotificationSender;
import com.example.firebasee.model.Token;
import com.example.firebasee.network.APIClass;
import com.example.firebasee.network.APIClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagesFragment extends Fragment {

    private static final String TAG = "MessagesFragment";

    private EditText firebaseuid,title,message;
    private Button send;
    private APIClass apiClass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        firebaseuid = view.findViewById(R.id.FirebaseID);
        title = view.findViewById(R.id.title_message);
        message = view.findViewById(R.id.message1);
        send = view.findViewById(R.id.send_btn);

        apiClass = APIClient.getRetrofit("https://fcm.googleapis.com/").create(APIClass.class);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ");
                FirebaseDatabase.getInstance().getReference().child("Tokens").child(firebaseuid.getText().toString()).child("token")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String usertoken = snapshot.getValue(String.class);
                                sendNotification(usertoken, title.getText().toString().trim(), message.getText().toString().trim());
                                Log.d(TAG, "onDataChange: " + title.getText().toString().trim());
                                Log.d(TAG, "onDataChange: " + message.getText().toString().trim());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });
        UpdateToken();

        return view;
    }

    private void UpdateToken(){
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();

        Token token= new Token(refreshToken);
        FirebaseDatabase.getInstance().getReference("Tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
        Log.d(TAG, "UpdateToken: "+ token);
    }

    private void sendNotification(String usertoken, String title, String message) {
        Log.d(TAG, "sendNotification: Invoked");
        Data data = new Data(title, message);
        NotificationSender sender = new NotificationSender(data, usertoken);
        apiClass.sendNotification(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        Log.d(TAG, "onResponse: " + response.body().success);
                        Toast.makeText(getContext(), "Failed ", Toast.LENGTH_LONG);
                    }
                }

            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }
}