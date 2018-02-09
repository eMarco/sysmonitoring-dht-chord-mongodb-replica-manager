/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.utils.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;

/**
 * An helper for GenericValue (un)marshalling
 * @author aleskandro
 * @param <GenericValue>
 */
public class JsonHelper {
    private static final ObjectMapper mapper = new ObjectMapper().enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    private static final ObjectWriter listWriter = mapper.writerFor(new TypeReference<List<GenericValue>>(){});

    public static String writeList(List<GenericValue> objects) {
        try {
            return listWriter.writeValueAsString(objects);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(JsonHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static List<GenericValue> readList(String jsonList) {
        try {
            return mapper.readValue(jsonList,
                    mapper.getTypeFactory().constructCollectionType(List.class, 
                            GenericValue.class));
        } catch (IOException ex) {
            Logger.getLogger(JsonHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static String write(GenericValue object) {
        try { 
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(JsonHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static GenericValue read(String jsonElem) {
        try { 
            return mapper.readValue(jsonElem, GenericValue.class);
        } catch (IOException ex) {
            Logger.getLogger(JsonHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
