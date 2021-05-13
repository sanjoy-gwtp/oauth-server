package com.surjo.oauth.exception;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * Created by sanjoy on 4/14/20.
 */
public class CustomOauthExceptionSerializer extends StdSerializer<CustomOauthException> {

    public CustomOauthExceptionSerializer() {
        super(CustomOauthException.class);
    }

    @Override
    public void serialize(CustomOauthException value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("code", value.getHttpErrorCode());
        jsonGenerator.writeStringField("error", value.getOAuth2ErrorCode());
        jsonGenerator.writeStringField("message", value.getMessage());

//        jsonGenerator.writeBooleanField("status", false);
//        jsonGenerator.writeObjectField("data", null);
//        jsonGenerator.writeObjectField("errors", Arrays.asList(value.getOAuth2ErrorCode(),value.getMessage()));
//        if (value.getAdditionalInformation()!=null) {
//            for (Map.Entry<String, String> entry : value.getAdditionalInformation().entrySet()) {
//                String key = entry.getKey();
//                String add = entry.getValue();
//                jsonGenerator.writeStringField(key, add);
//            }
//        }
        jsonGenerator.writeEndObject();
    }
}
