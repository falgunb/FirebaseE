package com.example.firebasee.network;

import com.example.firebasee.model.MyResponse;
import com.example.firebasee.model.NotificationSender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIClass {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAZYt7NCs:APA91bHuK8mEIgDqMbmdc1oD0n4GWFM__E3Lli0ixYIKBQfoaxSIXRcUnvWt7QY2gV7E7VrIM59-na_wCxLMPtamsqHugg-oMMVhAE8oTdRnsQuo4pSApUGmkQ9siJNInDJs_rwyqa17"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body NotificationSender body);
}
