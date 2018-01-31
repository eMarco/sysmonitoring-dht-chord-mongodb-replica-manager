/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.datamanager;

import com.google.gson.Gson;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import org.unict.ing.pds.dhtdb.utils.datamanager.DataManagerSessionBeanRemote;
import org.unict.ing.pds.dhtdb.utils.model.*;

/**
 *
 * @author aleskandro
 */
@Stateless
public class DataManagerSessionBean implements DataManagerSessionBeanRemote {

    @Override
    public void put(String scanner, String topic, String content) {
        try {
            // Convert the request in the proper model object
            Class<? extends GenericStat> t = Class.forName("org.unict.ing.pds.dhtdb.utils.model." + topic).asSubclass(GenericStat.class);
            GenericStat fromJson = new Gson().fromJson(content, t); // is it going to work?
            
            // TODO create the query
            // TODO calculate the dht node to send the query
            // TODO send the query to the proper dht node  
        } catch (ClassNotFoundException ex) {
            // Wrong topic in request
            Logger.getLogger(DataManagerSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String get(String scanner, String topic, String tsStart, String tsEnd) {
        // TODO calculate the dht node, based on f([tsStart, tsEnd], scanner)
        // TODO send the query to the proper nodes
        return "TODO";
    }
}
