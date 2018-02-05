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
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.unict.ing.pds.dhtdb.utils.dht.Key;
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
    public List<GenericValue> delete(Key key) {
        ClientResponse clientResponse = getWebResource("/" + key.toString()).delete(ClientResponse.class);
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
        try {
                ClientResponse clientResponse = getWebResource("/successor/" + key.toString()).get(ClientResponse.class);

                if (clientResponse.getStatus() != 200) {
                        System.out.println("[ERROR] Error in fetching findSuccessor response [" + clientResponse.getStatus() + " " + clientResponse.getStatusInfo() + "]");
                        // TODO : handle null value
                        return null;
                }

                String _key = clientResponse.getEntity(String.class);
                return new ObjectMapper().readValue(_key, NodeReference.class);
        } catch (IOException ex) {
                Logger.getLogger(RemoteNodeProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public NodeReference findPredecessor(Key key) {
        try {
                ClientResponse clientResponse = getWebResource("/findPredecessor/" + key.toString()).get(ClientResponse.class);

                if (clientResponse.getStatus() != 200) {
                        System.out.println("[ERROR] Error in fetching findSuccessor response [" + clientResponse.getStatus() + " " + clientResponse.getStatusInfo() + "]");
                        return null;
                }

                String _key = clientResponse.getEntity(String.class);
                return new ObjectMapper().readValue(_key, NodeReference.class);
        } catch (IOException ex) {
                Logger.getLogger(RemoteNodeProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public NodeReference notify(NodeReference nodeRef) {
        try {
                ObjectMapper mapper = new ObjectMapper();
                String _nodeRef = mapper.writeValueAsString(nodeRef);

                ClientResponse clientResponse = getWebResource("/notify").post(ClientResponse.class, _nodeRef);

                if (clientResponse.getStatus() != 200) {
                        System.out.println("[ERROR] Error in fetching PUT response [" + clientResponse.getStatus() + " " + clientResponse.getStatusInfo() + "]");
                        return null;
                }

                _nodeRef = clientResponse.getEntity(String.class);

                return mapper.readValue(_nodeRef, NodeReference.class);
        } catch (IOException ex) {
                Logger.getLogger(RemoteNodeProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public NodeReference getPredecessorNodeRef() {
        try {
                ClientResponse clientResponse = getWebResource("/predecessor/").get(ClientResponse.class);

                if (clientResponse.getStatus() != 200) {
                        System.out.println("[ERROR] Error in fetching getPredecessor response [" + clientResponse.getStatus() + " " + clientResponse.getStatusInfo() + "]");
                        return null;
                }

                String _key = clientResponse.getEntity(String.class);
                return new ObjectMapper().readValue(_key, NodeReference.class);
        } catch (IOException ex) {
                Logger.getLogger(RemoteNodeProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }


    public WebResource getWebResource(String path) {
        Client client = Client.create();

        return client.resource(this.nodeRef.getEndpoint() + PATH + path);
    }

    public NodeReference ping() {
        try {
                String clientResponse = getWebResource("/ping").get(String.class);
                return new ObjectMapper().readValue(clientResponse, NodeReference.class);
        } catch (IOException ex) {
                Logger.getLogger(RemoteNodeProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Boolean put(List<GenericValue> elems) {
        try {

            String jsonList = new ObjectMapper().writerFor(new TypeReference<List<GenericValue>>() {}).writeValueAsString(elems);
            String k = elems.get(0).getKey().toString();
            ClientResponse clientResponse = getWebResource("/" + k).put(ClientResponse.class, jsonList);

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
