package com.emmaguy.gifcast.data.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ImgurGalleryJsonDeserializer implements JsonDeserializer<ImgurGalleryJson> {
    @Override
    public ImgurGalleryJson deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) {
        ImgurGalleryJson obj = new ImgurGalleryJson();
        obj.data = new ArrayList<String>();

        JsonObject data = json.getAsJsonObject().get("data").getAsJsonObject();
        if (data.has("is_album") && data.get("is_album").getAsBoolean()) {
            for (JsonElement e : data.get("images").getAsJsonArray()) {
                obj.data.add(e.getAsJsonObject().get("link").getAsString());
            }
        } else if (data.isJsonObject()) {
            obj.data.add(data.get("link").getAsString());
        } else {
            throw new RuntimeException("Unexpected JSON type: " + json.getClass());
        }
        return obj;
    }
}