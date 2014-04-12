package com.emmaguy.gifcast.data.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ImgurGalleryJsonDeserializer implements JsonDeserializer<ImgurGalleryJson> {
    @Override
    public ImgurGalleryJson deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) {
        ImgurGalleryJson obj = new ImgurGalleryJson();
        obj.data = new ArrayList<String>();

        if (json.isJsonArray()) {
            for (JsonElement e : json.getAsJsonArray()) {
                obj.data.add(e.getAsJsonObject().get("data").getAsJsonObject().get("link").getAsString());
            }
        } else if (json.isJsonObject()) {
            obj.data.add(json.getAsJsonObject().get("data").getAsJsonObject().get("link").getAsString());
        } else {
            throw new RuntimeException("Unexpected JSON type: " + json.getClass());
        }
        return obj;
    }
}