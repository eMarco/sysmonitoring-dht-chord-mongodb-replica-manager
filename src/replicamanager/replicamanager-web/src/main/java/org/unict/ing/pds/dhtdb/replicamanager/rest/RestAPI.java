/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replicamanager.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.enterprise.context.RequestScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import org.unict.ing.pds.dhtdb.utils.dht.Key;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;
import org.unict.ing.pds.dhtdb.replica.p2p.NodeSessionBeanLocal;
import org.unict.ing.pds.dhtdb.utils.common.JsonHelper;
import org.unict.ing.pds.dhtdb.utils.common.NodeReference;
import org.unict.ing.pds.dhtdb.utils.common.RemoteNodeProxy;

/**
 * REST Web Service
 *
 */
@Path(RemoteNodeProxy.PATH)
@RequestScoped
public class RestAPI {

    NodeSessionBeanLocal nodeSessionBean = lookupNodeSessionBeanLocal();


    @Context
    private UriInfo context;

    /**
     * Creates a new instance of RestAPI
     */
    public RestAPI() {
    }

    /**
     * Get Data
     * @param k |
     * @return | an instance of java.lang.String
     */
    @GET
    @Path(value="{key : ([A-Za-z0-9]+)}")
    @Consumes(MediaType.TEXT_PLAIN)
    public String get(@PathParam(value="key") String k) {
        Key key = new Key(k);
        List<GenericValue> list =  nodeSessionBean.get(key);
        return JsonHelper.writeList(list);
    }

    /**
     * Creates data
     * @param k |
     * @param u |
     * @return | an instance of java.lang.String
     */
    @POST
    @Path(value="{key : ([A-Za-z0-9]+)}")
    @Consumes(MediaType.TEXT_PLAIN)
    public String put(@PathParam(value="key") String k, String u) {
        GenericValue genericValue = JsonHelper.read(u);
        nodeSessionBean.put(genericValue);
        // TODO Return JSON
        return genericValue.toString();
        // TODO using responseCodes ?
    }
    /**
     * Update data
     * @param k |
     * @param u |
     * @return | an instance of java.lang.String
     */
    @PUT
    @Path(value="{key : ([A-Za-z0-9]+)}")
    @Consumes(MediaType.TEXT_PLAIN)
    public String putList(@PathParam(value="key") String k, String u) {
        nodeSessionBean.put(JsonHelper.readList(u));
        // TODO Return JSON
        return "OK";
    }

    /**
     * Delete a key
     * @param k |
     * @return | an instance of java.lang.String
     */
    @DELETE
    @Path(value="{key : ([A-Za-z0-9]+)}")
    @Consumes(MediaType.TEXT_PLAIN)
    public String delete(@PathParam(value="key") String k) {
        return JsonHelper
                .writeList(nodeSessionBean.delete(new Key(k)));
    }

    /**
     * Get Data
     * @param k |
     * @return | an instance of java.lang.String
     * @throws com.fasterxml.jackson.core.JsonProcessingException | 
     */
    @GET
    @Path(value="/moving/{key : ([A-Za-z0-9]+)}")
    @Consumes(MediaType.TEXT_PLAIN)
    public String moving(@PathParam(value="key") String k) throws JsonProcessingException {
        return JsonHelper.writeList(nodeSessionBean.getLessThanAndRemove(new Key(k)));
    }

    /**
     * Retrieves successor for the given Key (findSuccessor)
     * @param k |
     * @return | an instance of java.lang.String
     * @throws com.fasterxml.jackson.core.JsonProcessingException |
     */
    @GET
    @Path(value="/successor/{key : ([A-Za-z0-9]+)}")
    @Consumes(MediaType.TEXT_PLAIN)
    public String findSuccessor(@PathParam(value="key") String k) throws JsonProcessingException {
        return 
                new ObjectMapper().writeValueAsString(nodeSessionBean.findSuccessor(new Key(k)));
    }

    /**
     * Retrieves node's predecessor
     * @return | an instance of java.lang.String
     * @throws com.fasterxml.jackson.core.JsonProcessingException |
     */
    @GET
    @Path(value="/predecessor")
    @Consumes(MediaType.TEXT_PLAIN)
    public String getPredecessor() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(nodeSessionBean.getPredecessorNodeRef());
    }

    /**
     * Call notify
     * @param u |
     * @return | an instance of java.lang.String
     */
    @POST
    @Path(value="/notify")
    @Consumes(MediaType.TEXT_PLAIN)
    public String notify(String u) throws JsonProcessingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(nodeSessionBean.notify(
                mapper.readValue(u, NodeReference.class)));
    }

    /**
     *
     * @return |
     */
    @GET
    @Path(value="/ping")
    @Consumes(MediaType.TEXT_PLAIN)
    public String ping() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(nodeSessionBean.getNodeReference());
    }

    private NodeSessionBeanLocal lookupNodeSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (NodeSessionBeanLocal) c.lookup("java:global/replicamanager-ear-1.0-SNAPSHOT/replicamanager-ejb-1.0-SNAPSHOT/NodeSessionBean!org.unict.ing.pds.dhtdb.replica.p2p.NodeSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
