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
        String _elem = new Gson().toJson(elem);

        ClientResponse clientResponse = getWebResource("/" + key.toString()).post(ClientResponse.class, _elem);

        if (clientResponse.getStatus() != 200) {
            System.out.println("[ERROR] Error in fetching PUT response [" + clientResponse.getStatus() + " " + clientResponse.getStatusInfo() + "]");
            return false;
        }

        // TODO : return what?
//        _elem = clientResponse.getEntity(String.class);

        return true;
    }

    @Override
    public List<GenericValue> get(Key key) {
        ClientResponse clientResponse = getWebResource("/" + key.toString()).get(ClientResponse.class);

        String res = clientResponse.getEntity(String.class);

        if (clientResponse.getStatus() != 200) {
            System.out.println("[ERROR] Error in fetching GET response [" + clientResponse.getStatus() + " " + clientResponse.getStatusInfo() + "]");
            return new ArrayList<>();
        }

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
        ClientResponse clientResponse = getWebResource("/successor/" + key.toString()).get(ClientResponse.class);

        if (clientResponse.getStatus() != 200) {
            System.out.println("[ERROR] Error in fetching findSuccessor response [" + clientResponse.getStatus() + " " + clientResponse.getStatusInfo() + "]");
            return null;
        }

        String _key = clientResponse.getEntity(String.class);
        return new Gson().fromJson(_key, NodeReference.class);
    }


    @Override
    public NodeReference notify(NodeReference nodeRef) {
        String _nodeRef = new Gson().toJson(nodeRef);

        ClientResponse clientResponse = getWebResource("/notify").post(ClientResponse.class, _nodeRef);

        if (clientResponse.getStatus() != 200) {
            System.out.println("[ERROR] Error in fetching PUT response [" + clientResponse.getStatus() + " " + clientResponse.getStatusInfo() + "]");
            return null;
        }

        _nodeRef = clientResponse.getEntity(String.class);

        System.out.println("VALUE:" + _nodeRef);
        System.out.println("NODEREF:" + this.nodeRef.toString());

        return new Gson().fromJson(_nodeRef, NodeReference.class);
    }


    public WebResource getWebResource(String path) {
        Client client = Client.create();

        return client.resource(this.nodeRef.getEndpoint() + PATH + path);
    }
    
    public NodeReference ping() {
        String clientResponse = getWebResource("/ping").get(String.class);
        return new Gson().fromJson(clientResponse, NodeReference.class);
    }
}
