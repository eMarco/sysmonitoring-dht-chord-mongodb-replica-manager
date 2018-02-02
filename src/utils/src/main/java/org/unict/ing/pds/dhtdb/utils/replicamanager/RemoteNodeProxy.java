/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.utils.replicamanager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
public class RemoteNodeProxy extends BaseNode {
    public static final String PATH = "/replicamanager";

    public RemoteNodeProxy(NodeReference nodeRef) {
        super(nodeRef);
    }

    @Override
    public Boolean put(Key key, GenericValue elem) {
        Client client = Client.create();

        WebResource webResource = client.resource(nodeRef.getEndpoint() + PATH + "/" + key.toString());

        String _elem = new Gson().toJson(elem);
        
        ClientResponse clientResponse = webResource.post(ClientResponse.class, _elem);
        
        if (clientResponse.getStatus() != 200) {
            System.out.println("[ERROR] Error in fetching PUT response [" + clientResponse.getStatus() + " " + clientResponse.getStatusInfo() + "]");
            return false;
        }
        
        _elem = clientResponse.getEntity(String.class);
        
        System.out.println("VALUE:" + _elem);
        System.out.println("NODEREF:" + this.nodeRef.toString());
        
        return true;
    }

    @Override
    public List<GenericValue> get(Key key) {
        Client client = Client.create();

        WebResource webResourceGET = client.resource(nodeRef.getEndpoint() + PATH + "/" + key.toString());

        ClientResponse clientResponse = webResourceGET.get(ClientResponse.class);
        String res = clientResponse.getEntity(String.class);
        System.out.println("GET RESPONSE: " + res);

        if (clientResponse.getStatus() != 200) {
            System.out.println("[ERROR] Error in fetching GET response [" + clientResponse.getStatus() + " " + clientResponse.getStatusInfo() + "]");
            return new ArrayList<>();
        }
        
        System.out.println("TEST1" + " ADDR "+ nodeRef.toString() + "/" + key.toString() + " RET " + res);
        
        // TODO : Improve me!
        Type token = new TypeToken<List<String>>() {}.getType();
        List<GenericValue> ret = new LinkedList<>();
        
        GenericValue genericValue;
        Class<? extends GenericValue> t;
        
        // Unmarshall received JSON to List<String>
        for (String u : (List<String>) new Gson().fromJson(res, token)) {
            try {
                // Unmarshall JSON object to GenericValue
                genericValue = new Gson().fromJson(u, GenericValue.class);
                
                // Use Reflections to obtain the correct SubClass of GenericValue
                t = Class.forName("org.unict.ing.pds.dhtdb.utils.model." + genericValue.getType()).asSubclass(GenericValue.class);
                
                // Unmarshall JSON object to the SubClass and add it to the return list
                ret.add(new Gson().fromJson(u, t));                
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(RemoteNodeProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return ret;
    }

    @Override
    public void bootstrap(NodeReference nodeRef) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NodeReference findSuccessor(Key key) {
        Client client = Client.create();
        String requestURI = this.nodeRef.getEndpoint() + PATH + "/successor/" + key.toString();
        System.out.println("REQUEST URI: " + requestURI);
        WebResource webResource = client.resource(requestURI);
        
        ClientResponse clientResponse = webResource.get(ClientResponse.class);
        
        if (clientResponse.getStatus() != 200) {
            System.out.println("[ERROR] Error in fetching findSuccessor response [" + clientResponse.getStatus() + " " + clientResponse.getStatusInfo() + "]");
            return null;
        }
        
        String _key = clientResponse.getEntity(String.class);
        System.out.println("REQUEST RESPONSE: " + _key);
        return new Gson().fromJson(_key, NodeReference.class);
    }   
}
