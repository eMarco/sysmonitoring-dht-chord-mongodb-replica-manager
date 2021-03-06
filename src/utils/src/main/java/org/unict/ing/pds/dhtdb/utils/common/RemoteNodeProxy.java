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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.unict.ing.pds.dhtdb.utils.dht.Key;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;

/**
 * A proxy to communicate with remote peers
 */
public class RemoteNodeProxy extends BaseNode {
    public static final String PATH = "/replicamanager";

    public RemoteNodeProxy(NodeReference nodeRef) {
        super(nodeRef);
    }
    
    /**
     * Generic method that generate a request
     * @param uri |
     * @param method |
     * @param payload |
     * @return | 
     */
    private String request(String uri, String method, String payload) {
        ClientResponse clientResponse;
        if (payload == null)
          clientResponse = getWebResource("/" + uri).method(method, ClientResponse.class);
        else
          clientResponse = getWebResource("/" + uri).method(method, ClientResponse.class, payload);

        if (clientResponse.getStatus() != 200) {
            System.out.println("[ERROR] Error in fetching response [" + clientResponse.getStatus() + " " + clientResponse.getStatusInfo() + "]");
            return null;
        }
        
        String res = clientResponse.getEntity(String.class);
        return res;
    }
    
    /**
     * Make a POST request
     * @param uri |
     * @param payload |
     * @return | 
     */    
    private Boolean request(String uri, String payload) {
        return request(uri, "POST", payload) != null;
    }

    /**
     * Make a GET request
     * @param uri |
     * @return | 
     */
    private String request(String uri) {
        return request(uri, "GET", null);
    }
   
    private WebResource getWebResource(String path) {
        Client client = Client.create();
        return client.resource(this.nodeRef.getEndpoint() + PATH + path);
    }

    @Override
    public List<GenericValue> get(Key key) {
        return JsonHelper.readList(request(key.toString()));
    }

    @Override
    public Boolean put(GenericValue elem) {
        // POST
        return request(elem.getKey().toString(), 
                new JsonHelper().write(elem));
    }
    
    @Override
    public List<GenericValue> delete(Key key) {
        return JsonHelper.readList(
                request(key.toString(), "DELETE", null));
    }
    

    @Override
    public NodeReference findSuccessor(Key key) {
        try {
            return new ObjectMapper().readValue(
                    request("successor/" + key.toString()), NodeReference.class);
        } catch (IOException ex) {
            Logger.getLogger(RemoteNodeProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public NodeReference notify(NodeReference nodeRef) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(
                    request("notify/", "POST", mapper.writeValueAsString(nodeRef)), NodeReference.class);
        } catch (IOException ex) {
            Logger.getLogger(RemoteNodeProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public NodeReference getPredecessorNodeRef() {
        try {
            return new ObjectMapper().readValue(request("predecessor/"), NodeReference.class);
        } catch (IOException ex) {
            Logger.getLogger(RemoteNodeProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public NodeReference ping() {
        try {
            return new ObjectMapper().readValue(request("ping"), NodeReference.class);
        } catch (IOException ex) {
            Logger.getLogger(RemoteNodeProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Boolean put(List<GenericValue> elems) {
        // Very hacky (TODO)
        if (elems.size() > 0)
            request(elems.get(0).getKey().toString(), "PUT", JsonHelper.writeList(elems));
        return true;
    }

    @Override
    public List<GenericValue> getLessThanAndRemove(Key key) {
        return JsonHelper.readList(request("moving/" + key.toString()));
    }

    @Override
    public void bootstrap(NodeReference nodeRef) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
