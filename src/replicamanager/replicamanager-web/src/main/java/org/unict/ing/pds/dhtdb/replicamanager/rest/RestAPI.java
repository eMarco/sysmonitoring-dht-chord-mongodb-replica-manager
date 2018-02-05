/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replicamanager.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
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
import org.unict.ing.pds.dhtdb.utils.common.NodeReference;
import org.unict.ing.pds.dhtdb.utils.common.RemoteNodeProxy;

/**
 * REST Web Service
 *
 * @author aleskandro
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
     * @param k
     * @return an instance of java.lang.String
     */
    @GET
    @Path(value="{key : ([A-Za-z0-9]+)}")
    @Consumes(MediaType.TEXT_PLAIN)
    public String get(@PathParam(value="key") String k) {
        try {
            Key key = new Key(k);
            List<GenericValue> list =  nodeSessionBean.get(key);
            String jsonList = new ObjectMapper().writerFor(new TypeReference<List<GenericValue>>() {}).writeValueAsString(list);
            return jsonList;
        } catch (JsonProcessingException ex) {
            Logger.getLogger(RestAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Put data
     * @param k
     * @param u
     * @return an instance of java.lang.String
     */
    @POST
    @Path(value="{key : ([A-Za-z0-9]+)}")
    @Consumes(MediaType.TEXT_PLAIN)
    public String put(@PathParam(value="key") String k, String u) {
        try {
            System.out.println(u);
            GenericValue genericValue = new ObjectMapper().readValue(u, GenericValue.class);
            Key key = new Key(k);

            nodeSessionBean.put(genericValue);
            // TODO Return JSON
            return key + " " + genericValue;
        } catch (IOException ex) {
            Logger.getLogger(RestAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        // TODO using responseCodes ?
        return "ERROR";
    }
    /**
     * Put data
     * @param k
     * @param u
     * @return an instance of java.lang.String
     */
    @PUT
    @Path(value="{key : ([A-Za-z0-9]+)}")
    @Consumes(MediaType.TEXT_PLAIN)
    public String putList(@PathParam(value="key") String k, String u) {
        try {
            ObjectMapper mapper = new ObjectMapper().enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
            List<GenericValue> ret = mapper.readValue(k,
                    mapper.getTypeFactory().constructCollectionType(List.class, GenericValue.class));
            nodeSessionBean.put(ret);
            // TODO Return JSON
            return "OK";
        } catch (IOException ex) {
            Logger.getLogger(RestAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Get Data
     * @param k
     * @return an instance of java.lang.String
     */
    @DELETE
    @Path(value="{key : ([A-Za-z0-9]+)}")
    @Consumes(MediaType.TEXT_PLAIN)
    public String delete(@PathParam(value="key") String k) {
        try {
            Key key = new Key(k);
            List<GenericValue> list =  nodeSessionBean.delete(key);
            String jsonList = new ObjectMapper().writerFor(new TypeReference<List<GenericValue>>() {}).writeValueAsString(list);
            return jsonList;
        } catch (JsonProcessingException ex) {
            Logger.getLogger(RestAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Get Data
     * @param k
     * @return an instance of java.lang.String
     * @throws com.fasterxml.jackson.core.JsonProcessingException
     */
    @GET
    @Path(value="/moving/{key : ([A-Za-z0-9]+)}")
    @Consumes(MediaType.TEXT_PLAIN)
    public String moving(@PathParam(value="key") String k) throws JsonProcessingException {
        Key key = new Key(k);
        List<GenericValue> list =  nodeSessionBean.getLessThanAndRemove(key);

        String jsonList = new ObjectMapper().writerFor(new TypeReference<List<GenericValue>>() {}).writeValueAsString(list);
        return jsonList;
    }

    /**
     * Retrieves successor of given Key
     * @param k
     * @return an instance of java.lang.String
     * @throws com.fasterxml.jackson.core.JsonProcessingException
     */
    @GET
    @Path(value="/successor/{key : ([A-Za-z0-9]+)}")
    @Consumes(MediaType.TEXT_PLAIN)
    public String findSuccessor(@PathParam(value="key") String k) throws JsonProcessingException {

        Key key = new Key(k);

        return new ObjectMapper().writeValueAsString(nodeSessionBean.findSuccessor(key));// new Gson().toJson(ret);
    }
    /**
     * Retrieves successor of given Key
     * @param k
     * @return an instance of java.lang.String
     * @throws com.fasterxml.jackson.core.JsonProcessingException
     */
    @GET
    @Path(value="/findPredecessor/{key : ([A-Za-z0-9]+)}")
    @Consumes(MediaType.TEXT_PLAIN)
    public String findPredecessor(@PathParam(value="key") String k) throws JsonProcessingException {

        Key key = new Key(k);

        return new ObjectMapper().writeValueAsString(nodeSessionBean.findPredecessor(key));
    }

    /**
     * Retrieves node's predecessor
     * @return an instance of java.lang.String
     */
    @GET
    @Path(value="/predecessor")
    @Consumes(MediaType.TEXT_PLAIN)
    public String getPredecessor() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(nodeSessionBean.getPredecessorNodeRef());
    }

    /**
     *
     * @param u
     * @return an instance of java.lang.String
     */
    @POST
    @Path(value="/notify")
    @Consumes(MediaType.TEXT_PLAIN)
    public String notify(String u) throws JsonProcessingException, IOException {
        NodeReference nodeRef = new ObjectMapper().readValue(u, NodeReference.class);
        return new ObjectMapper().writeValueAsString(nodeSessionBean.notify(nodeRef));
    }

    /**
     *
     * @return
     * @throws JsonProcessingException
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
