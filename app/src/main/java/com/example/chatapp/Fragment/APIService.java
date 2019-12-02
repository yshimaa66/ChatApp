package com.example.chatapp.Fragment;

import com.example.chatapp.Notification.MyResponse;
import com.example.chatapp.Notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
            "Content-Type:application/json",
             "Authorization:key=AAAAUYQMkTM:APA91bGhs27CXTvjp85N5xp7OJvuhQGSavcCC_64YZ5VTOQdmsPVasRo5WHyTbfU945PC7pbyjTta6xR3vxDDbCyqazgzlZ2CZCE-gl2bLV0TAE33ACAw4aYjYK2db-biN_wl4R2dSCh"


    }
    )


    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
