package com.manish.chushylearn.Model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.FloatNode;

import java.io.IOException;

/**
 * Created by rbaisak on 1/6/17.
 */

public class UserLocationDeserializer extends StdDeserializer<UserLocation> {
    public UserLocationDeserializer() {
        this(null);
    }

    public UserLocationDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public UserLocation deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        double latitude = (Double) ((DoubleNode) node.get("latitude")).doubleValue();
        double longitude = (Double) ((DoubleNode) node.get("longitude")).doubleValue();
        float rating = (Float) ((FloatNode) node.get("rating")).floatValue();
        String category = node.get("category").asText();
        String namePlace = node.get("namePlace").asText();
        String feedback = node.get("feedback").asText();
        String placeDetails = node.get("placeDetails").asText();
        String addedByKey = node.get("addedByKey").asText();
        String LocationId = node.get("LocationId").asText();
        String imageURL = node.get("imageURL").asText();
     /****
        List<String> imageURL=new ArrayList<String>();
        //String image=node.get("imageURL").asText();
        final JsonNode arrNode = node.get("imageURL");
        if (arrNode.isArray()) {
            for (final JsonNode objNode : arrNode) {
                imageURL.add(objNode.asText());
            }
        }
        //imageURL.add(image);
*****************************/
        return new UserLocation(latitude, longitude, rating,category,namePlace,feedback,placeDetails,addedByKey, imageURL, LocationId);
    }

}
