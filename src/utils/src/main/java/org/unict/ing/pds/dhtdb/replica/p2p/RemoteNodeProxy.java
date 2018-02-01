/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replica.p2p;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
public class RemoteNodeProxy extends BaseNode implements DHTNode, ChordNode{

    public RemoteNodeProxy(NodeReference nodeRef) {
        super(nodeRef);
    }

    @Override
    public Boolean put(Key key, GenericValue elem) {
        Client client = Client.create();
        
        WebResource webResource = client.resource(nodeRef.getEndpoint() + "/replicamanager-web/webresources/replicamanager/" + key.toString());
        
        String _elem = new Gson().toJson(elem);
        
        ClientResponse clientResponse = webResource.post(ClientResponse.class, _elem);
        
        /*if (clientResponse.getStatusInfo() != ClientResponse.Status.OK) {
            System.out.println("[ERROR] Error in fetching PUT response");
            return false;
        }*/
        
        _elem = clientResponse.getEntity(String.class);
        
        System.out.println("VALUE:" + _elem);
        System.out.println("NODEREF:" + this.nodeRef.toString());
        
        return true;
    }

    @Override
    public List<GenericValue> get(Key key) {
        Client client = Client.create();
        
        WebResource webResourceGET = client.resource(nodeRef.getEndpoint()+ "/replicamanager-web/webresources/replicamanager/" + key.toString());
        
        ClientResponse clientResponse = webResourceGET.get(ClientResponse.class);
        String ret = clientResponse.getEntity(String.class);
        System.out.println("GET RESPONSE: " + ret);
        /*if (clientResponse.getStatusInfo() != ClientResponse.Status.OK) {
            System.out.println("[ERROR] Error in fetching GET response");
            return new ArrayList<>();
        }*/
        
        System.out.println("TEST1" + " ADDR "+ nodeRef.toString() + "/replicamanager-web/webresources/replicamanager/" + key.toString() + " RET " + ret);
        
        // TODO CAST?
        Type token = new TypeToken<List<GenericValue>>() {}.getType();
        List<GenericValue> ret = new LinkedList<>();
        
        for (GenericValue genericValue : (List<GenericValue>) new Gson().fromJson(res, token)) {
            Class<? extends GenericValue> t;
            try {
                t = Class.forName("org.unict.ing.pds.dhtdb.utils.model." + genericValue.getType()).asSubclass(GenericValue.class);
                ret.add(t.cast(genericValue));
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
    public NodeReference findSuccessor(NodeReference nodeRef) {
        Client client = Client.create();
        String requestURI = this.nodeRef.getEndpoint() + "/replicamanager-web/webresources/replicamanager/successor";
        System.out.println("REQUEST URI: " + requestURI);
        WebResource webResource = client.resource(requestURI);
        
        String _nodeRef = new Gson().toJson(nodeRef);
        System.out.println("REQUEST PAYLOAD: " + _nodeRef);
        ClientResponse clientResponse = webResource.post(ClientResponse.class, _nodeRef);
        
        /*if (clientResponse.getStatusInfo() != ClientResponse.Status.OK) {
            System.out.println("[ERROR] Error in fetching findSuccessor response");
            return nodeRef;
        }*/
        _nodeRef = clientResponse.getEntity(String.class);
        System.out.println("REQUEST RESPONSE: " + _nodeRef);
        return new Gson().fromJson(_nodeRef, NodeReference.class);
    }   
}
