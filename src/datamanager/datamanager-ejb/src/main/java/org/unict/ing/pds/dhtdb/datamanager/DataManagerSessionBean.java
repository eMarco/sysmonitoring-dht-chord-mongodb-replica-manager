/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.datamanager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.unict.ing.pds.dhtdb.utils.datamanager.DataManagerSessionBeanRemote;
import org.unict.ing.pds.dhtdb.utils.model.*;
import org.unict.ing.pds.dhtdb.utils.replicamanager.Key;

/**
 *
 * @author aleskandro
 */
@Stateless
public class DataManagerSessionBean implements DataManagerSessionBeanRemote {

    @EJB
    private DataManagerChordSessionBeanLocal dataManagerChordSessionBean;

    @Override
    public void put(String scanner, String topic, String content) {
        try {
            // Convert the request in the proper model object
            ObjectMapper mapper = new ObjectMapper().enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
            List<GenericStat> fromJson = mapper.readValue(content,
                    mapper.getTypeFactory().constructCollectionType(List.class, GenericValue.class));
            fromJson.forEach(elem -> {
                elem.setScannerId(scanner);
                dataManagerChordSessionBean.write(new Key("the key to be done" + fromJson.toString(), true), elem);
            });
        }catch (IOException ex) {
            Logger.getLogger(DataManagerSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Wrong topic in request
        
    }

    @Override
    public String get(String scanner, String topic, String tsStart, String tsEnd) {
        // TODO calculate the dht node, based on f([tsStart, tsEnd], scanner)
        // TODO send the query to the proper nodes
        return "TODO";
    }

    @Override
    public String test(String content) {
        try {

            content =  "{ \"MemTotal\":\"12126164\", \"MemFree\":\"230924\"," +
                    " \"MemAvailable\":\"838236\", \"timestamp\": 1517777828, "
                    + "\"className\": \"org.unict.ing.pds.dhtdb.utils.model.RAMStat\" }";
            content = "{\"seconds\": \"17\", \"minutes\":\"7\", \"hours\":\"6\", \"days\": \"0\", "
                    + "\"timestamp\": 1517778670, \"className\": \"org.unict.ing.pds.dhtdb.utils.model.UptimeStat\"}";
            
            content = "{\"usage\":0.5,\"timestamp\":4,\"scannerId\":\"asd\",\"key\":{\"key\":\"1699d6b5508374cf2becc8778548b263271da293\"}}";

            content = "[{\"disk\":\"sda\", \"WritekBps\":\"313.87\", \"ReadkBps\":\"694.29\", \"timestamp\": 1517780002, \"className\": \"org.unict.ing.pds.dhtdb.utils.model.IOStat\" }, {\"disk\":\"sdb\", \"WritekBps\":\"13.16\", \"ReadkBps\":\"44.46\", \"timestamp\": 1517780002, \"className\": \"org.unict.ing.pds.dhtdb.utils.model.IOStat\" }, {\"disk\":\"dm-0\", \"WritekBps\":\"305.58\", \"ReadkBps\":\"637.26\", \"timestamp\": 1517780002, \"className\": \"org.unict.ing.pds.dhtdb.utils.model.IOStat\" }, {\"disk\":\"dm-1\", \"WritekBps\":\"0.23\", \"ReadkBps\":\"0.00\", \"timestamp\": 1517780002, \"className\": \"org.unict.ing.pds.dhtdb.utils.model.IOStat\" }, {\"disk\":\"dm-2\", \"WritekBps\":\"7.92\", \"ReadkBps\":\"57.04\", \"timestamp\": 1517780002, \"className\": \"org.unict.ing.pds.dhtdb.utils.model.IOStat\" }][{\"disk\":\"sda\", \"WritekBps\":\"315.71\", \"ReadkBps\":\"692.67\", \"timestamp\": 1517779859}, {\"disk\":\"sdb\", \"WritekBps\":\"13.24\", \"ReadkBps\":\"43.04\", \"timestamp\": 1517779859}, {\"disk\":\"dm-0\", \"WritekBps\":\"307.38\", \"ReadkBps\":\"635.28\", \"timestamp\": 1517779859}, {\"disk\":\"dm-1\", \"WritekBps\":\"0.23\", \"ReadkBps\":\"0.00\", \"timestamp\": 1517779859}, {\"disk\":\"dm-2\", \"WritekBps\":\"7.96\", \"ReadkBps\":\"57.39\", \"timestamp\": 1517779859}]";
            ObjectMapper mapper = new ObjectMapper().enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
            List<GenericValue> fromJson = mapper.readValue(content,
                    mapper.getTypeFactory().constructCollectionType(List.class, GenericValue.class));

            //nericValue fromJson = new Gson().fromJson(content, t); // is it going to work?
            String toJson = mapper.writeValueAsString(fromJson);
            List<GenericValue> fromJson2 = mapper.readValue(content,
                    mapper.getTypeFactory().constructCollectionType(List.class, GenericValue.class));
           
            return "Ciao mbare " + content + mapper.writeValueAsString(fromJson) + mapper.writeValueAsString(fromJson2) + toJson;
        } catch (IOException ex) {
            Logger.getLogger(DataManagerSessionBean.class.getName()).log(Level.SEVERE, null, ex);
            return "FUCK " + ex.getMessage();
        }
    }
}
