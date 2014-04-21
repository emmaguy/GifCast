package com.emmaguy.gifcast.data.api;

import com.emmaguy.gifcast.data.api.model.ImgurGalleryJson;
import com.emmaguy.gifcast.data.api.model.ImgurJson;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

public interface ImgurService {
    @GET("/{path}")
    void getImgurImagesInGallery(@Path("path") String path, Callback<ImgurGalleryJson> callback);

    @GET("/image/{id}")
    void getImgurImageUrl(@Path("id") String id, Callback<ImgurJson> callback);
}
