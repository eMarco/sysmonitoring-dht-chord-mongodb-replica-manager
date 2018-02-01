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
import org.unict.ing.pds.dhtdb.utils.model.GenericStat;
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
    public Boolean put(Key key, GenericStat elem) {
        Client client = Client.create();
        
        WebResource webResource = client.resource(nodeRef.getEndpoint() + "/replicamanager-web/webresources/replicamanager/" + key.toString());
        
        String _elem = new Gson().toJson(elem);
        
        ClientResponse clientResponse = webResource.post(ClientResponse.class, _elem);
        _elem = clientResponse.getEntity(String.class);
        
        System.out.println("VALUE:" + _elem);
        System.out.println("NODEREF:" + this.nodeRef.toString());
        
        return true;
    }

    @Override
    public List<GenericStat> get(Key key) {
        Client client = Client.create();
        
        WebResource webResourceGET = client.resource(nodeRef.getEndpoint()+ "/replicamanager-web/webresources/replicamanager/" + key.toString());
        
        ClientResponse clientResponseGET = webResourceGET.get(ClientResponse.class);
        
        String ret = clientResponseGET.getEntity(String.class);
        
        System.out.println("TEST1" + " ADDR "+ nodeRef.toString() + "/replicamanager-web/webresources/replicamanager/" + key.toString() + " RET " + ret);
        
        Type token = new TypeToken<List<GenericStat>>() {}.getType();
        
        return new LinkedList<GenericStat>();
//        return new Gson().fromJson(ret, token);
    }

    @Override
    public void bootstrap(NodeReference nodeRef) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NodeReference findSuccessor(NodeReference nodeRef) {
        Client client = Client.create();
        
        WebResource webResource = client.resource(nodeRef.getEndpoint() + "/replicamanager-web/webresources/replicamanager/successor");
        
        String _nodeRef = new Gson().toJson(nodeRef);
        
        ClientResponse clientResponse = webResource.post(ClientResponse.class, _nodeRef);
        _nodeRef = clientResponse.getEntity(String.class);
        
        System.out.println("NODEREF:" + _nodeRef);
        return new Gson().fromJson(_nodeRef, NodeReference.class);
    }   
}
