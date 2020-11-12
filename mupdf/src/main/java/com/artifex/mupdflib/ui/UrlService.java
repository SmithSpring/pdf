package com.artifex.mupdflib.ui;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface UrlService {
    //通用下载
    @Streaming //添加这个注解用来下载大文件
    @GET()
    Call<ResponseBody> downloadFile(@Url String fileUrl);

}
