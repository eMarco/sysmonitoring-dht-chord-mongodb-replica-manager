/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.datamanager;

import com.google.gson.Gson;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.enterprise.context.RequestScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import org.unict.ing.pds.dhtdb.utils2.model.CPUStat;

/**
 * REST Web Service
 *
 * @author aleskandro
 */
@Path("topics")
@RequestScoped
public class TopicsResource {

    DataManagerSessionBeanRemote dataManagerSessionBean = lookupDataManagerSessionBeanRemote();

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of TopicsResource
     */
    public TopicsResource() {
    }

    /**
     * Retrieves representation of an instance of org.unict.ing.pds.dhtdb.datamanager.TopicsResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path(value="{tsStart : (/[0-9]+)?}{tsEnd : (/[0-9]+)?}")
    public String getAll(            
            @PathParam(value="tsStart") String tsStart,
            @PathParam(value="tsEnd") String tsEnd) {
        
        return dataManagerSessionBean.get(null, null, tsStart, tsEnd);
    }

    /**
     * 
     * @param topic
     * @param tsStart
     * @param tsEnd
     * @return 
     */
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Path(value="/{topic:[a-zA-Z]+}{tsStart : (/[0-9]+)?}{tsEnd : (/[0-9]+)?}")
    public String getByTopicInterval(
            @PathParam(value="topic") String topic,
            @PathParam(value="tsStart") String tsStart,
            @PathParam(value="tsEnd") String tsEnd) {
        
        return dataManagerSessionBean.get(null, topic, tsStart, tsEnd);
    }

    
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Path(value="/{topic:[a-zA-Z]+}/scanners/{scanner:[0-9]+}{tsStart : (/[0-9]+)?}{tsEnd : (/[0-9]+)?}")
    public String getByTopicsScannerInterval(
            @PathParam(value="topic")   String topic,
            @PathParam(value="tsStart") String tsStart,
            @PathParam(value="tsEnd")   String tsEnd,
            @PathParam(value="scanner") String scanner) {
        
        return dataManagerSessionBean.get(scanner, topic, tsStart, tsEnd);
    }
    
    
    /**
     * PUT method for updating or creating an instance of TopicsResource
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    public void putText(String content) {
    }

    private DataManagerSessionBeanRemote lookupDataManagerSessionBeanRemote() {
        try {
            javax.naming.Context c = new InitialContext();
            return (DataManagerSessionBeanRemote) c.lookup("java:global/org.unict.ing.pds.dhtdb_datamanager-ear_ear_1.0-SNAPSHOT/org.unict.ing.pds.dhtdb_datamanager-ejb_ejb_1.0-SNAPSHOT/DataManagerSessionBean!org.unict.ing.pds.dhtdb.datamanager.DataManagerSessionBeanRemote");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

}
