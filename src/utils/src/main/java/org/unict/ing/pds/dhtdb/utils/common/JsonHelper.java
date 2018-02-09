/* 
 * Copyright (C) 2018 aleskandro - eMarco
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
