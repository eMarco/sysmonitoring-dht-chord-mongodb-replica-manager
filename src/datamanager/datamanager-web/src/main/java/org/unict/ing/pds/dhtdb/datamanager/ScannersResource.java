/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.datamanager;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.enterprise.context.RequestScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 */
@Path("scanners")
@RequestScoped
public class ScannersResource {

    DataManagerSessionBeanLocal dataManagerSessionBean = lookupDataManagerSessionBeanLocal();

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of ScannersResource
     */
    public ScannersResource() {
    }

    /**
     * scanners/
     * @return | all the datas in the past 24hours
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path(value="/")
    public String getAll() {
        return dataManagerSessionBean.get(null, null, null, null);
    }
    /**
     * scanners/tsStart/tsEnd
     * @param tsStart |
     * @param tsEnd (optional) |
     * @return | an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path(value="/{tsStart:([0-9]+)?}{tsEnd:(/[0-9]+)?}")
    public String getBetween(
            @PathParam(value="tsStart") String tsStart,
            @PathParam(value="tsEnd") String tsEnd) {
        return dataManagerSessionBean.get(null, null, tsStart, RestHelper.ts(tsEnd));
    }

    /**
     * scanners/$scanner_X/$tsStart/$tsEnd (X is [0-9]+)
     * @param scanner |
     * @param tsStart timestamp in seconds since Epoch (optional) |
     * @param tsEnd timestamp in seconds since Epoch(optional) |
     * @return |
     */
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Path(value="/{scanner:[a-zA-Z_]+_[0-9]+}{tsStart : (/[0-9]+)?}{tsEnd : (/[0-9]+)?}")
    public String getByScannerInterval(
            @PathParam(value="scanner") String scanner,
            @PathParam(value="tsStart") String tsStart,
            @PathParam(value="tsEnd") String tsEnd) {

        return dataManagerSessionBean.get(scanner, null, RestHelper.ts(tsStart), RestHelper.ts(tsEnd));
    }
    
    /**
     * /$scanner_X/topics/$topic/$tsStart/$tsEnd
     * @param topic the topic to query |
     * @param tsStart timestamp in seconds since Epoch (optional) |
     * @param tsEnd timestamp in seconds since Epoch (optional) |
     * @param scanner |
     * @return | 
     */
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Path(value="/{scanner:[a-zA-Z_]+_[0-9]+}/topics/{topic:[a-zA-Z]+}{tsStart : (/[0-9]+)?}{tsEnd : (/[0-9]+)?}")
    public String getByScannerTopicInterval(
            @PathParam(value="topic")   String topic,
            @PathParam(value="tsStart") String tsStart,
            @PathParam(value="tsEnd")   String tsEnd,
            @PathParam(value="scanner") String scanner) {

        return dataManagerSessionBean.get(scanner, topic, RestHelper.ts(tsStart), RestHelper.ts(tsEnd));
    }
    /**
     * The handler for the POST requests from the MessageHandler
     * @param content |
     * @param topic |
     * @param scanner  |
     */
    @POST
    @Consumes(MediaType.WILDCARD)
    @Path(value="/{scanner:[a-zA-Z0-9_]+}/{topic:[a-zA-Z]+}")
    public void postStat(String content,
            @PathParam(value="topic")   String topic,
            @PathParam(value="scanner") String scanner) {
        dataManagerSessionBean.put(scanner, topic, content);
    }

    private DataManagerSessionBeanLocal lookupDataManagerSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (DataManagerSessionBeanLocal) c.lookup("java:global/datamanager-ear-1.0-SNAPSHOT/datamanager-ejb-1.0-SNAPSHOT/DataManagerSessionBean!org.unict.ing.pds.dhtdb.datamanager.DataManagerSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
