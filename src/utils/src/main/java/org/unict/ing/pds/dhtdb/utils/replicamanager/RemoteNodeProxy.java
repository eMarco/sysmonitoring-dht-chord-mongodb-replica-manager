/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.utils.replicamanager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
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
    public Boolean put(GenericValue elem) {
        try {
            String jsonElem = new ObjectMapper().writeValueAsString(elem);
            String k = elem.getKey().toString();
            ClientResponse clientResponse = getWebResource("/" + k).post(ClientResponse.class, jsonElem);
            
            if (clientResponse.getStatus() != 200) {
                System.out.println("[ERROR] Error in fetching PUT response [" + clientResponse.getStatus() + " " + clientResponse.getStatusInfo() + "]");
                return false;
            }
            
            return true;
        } catch (JsonProcessingException ex) {
            System.out.println(ex.getOriginalMessage());
            Logger.getLogger(RemoteNodeProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public List<GenericValue> get(Key key) {
        ClientResponse clientResponse = getWebResource("/" + key.toString()).get(ClientResponse.class);
        String res = clientResponse.getEntity(String.class);

        if (clientResponse.getStatus() != 200) {
            System.out.println("[ERROR] Error in fetching GET response [" + clientResponse.getStatus() + " " + clientResponse.getStatusInfo() + "]");
            return new ArrayList<>();
        }

        return unmarshallList(res);
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
            // TODO : handle null value
            return null;
        }

        String _key = clientResponse.getEntity(String.class);
        return new Gson().fromJson(_key, NodeReference.class);
    }

    @Override
    public NodeReference findPredecessor(Key key) {
        ClientResponse clientResponse = getWebResource("/findPredecessor/" + key.toString()).get(ClientResponse.class);

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

        return new Gson().fromJson(_nodeRef, NodeReference.class);
    }

    @Override
    public NodeReference getPredecessor() {
        ClientResponse clientResponse = getWebResource("/predecessor/").get(ClientResponse.class);

        if (clientResponse.getStatus() != 200) {
            System.out.println("[ERROR] Error in fetching getPredecessor response [" + clientResponse.getStatus() + " " + clientResponse.getStatusInfo() + "]");
            return null;
        }

        String _key = clientResponse.getEntity(String.class);
        return new Gson().fromJson(_key, NodeReference.class);
    }


    public WebResource getWebResource(String path) {
        Client client = Client.create();

        return client.resource(this.nodeRef.getEndpoint() + PATH + path);
    }

    public NodeReference ping() {
        String clientResponse = getWebResource("/ping").get(String.class);
        return new Gson().fromJson(clientResponse, NodeReference.class);
    }

    @Override
    public Boolean put(List<GenericValue> elem) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<GenericValue> getLessThanAndRemove(Key key) {
        ClientResponse clientResponse = getWebResource("/moving/" + key.toString()).get(ClientResponse.class);

        String res = clientResponse.getEntity(String.class);

        if (clientResponse.getStatus() != 200) {
            System.out.println("[ERROR] Error in fetching GET response [" + clientResponse.getStatus() + " " + clientResponse.getStatusInfo() + "]");
            return new ArrayList<>();
        }

        return unmarshallList(res);
    }

    public List<GenericValue> unmarshallList(String res) {
        try {
            Type token = new TypeToken<List<GenericValue>>() {}.getType();
            ObjectMapper mapper = new ObjectMapper().enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
            List<GenericValue> ret = mapper.readValue(res,
                    mapper.getTypeFactory().constructCollectionType(List.class, GenericValue.class));
            return ret;
        } catch (IOException ex) {
            Logger.getLogger(RemoteNodeProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
