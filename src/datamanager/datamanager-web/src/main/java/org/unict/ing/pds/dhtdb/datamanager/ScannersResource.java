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
 * @author aleskandro
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
     * Retrieves representation of an instance of org.unict.ing.pds.dhtdb.datamanager.ScannersResource
     * @param tsStart
     * @param tsEnd
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path(value="{tsStart : (/[0-9]+)?}{tsEnd : (/[0-9]+)?}")
    public String getAll(
            @PathParam(value="tsStart") String tsStart,
            @PathParam(value="tsEnd") String tsEnd) {
        return dataManagerSessionBean.get(null, null, tsStart.substring(1), tsEnd.substring(1));
    }


     /**
     *
     * @param scanner
     * @param tsStart
     * @param tsEnd
     * @return
     */
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Path(value="/{scanner:[0-9]+}{tsStart : (/[0-9]+)?}{tsEnd : (/[0-9]+)?}")
    public String getByScannerInterval(
            @PathParam(value="scanner") String scanner,
            @PathParam(value="tsStart") String tsStart,
            @PathParam(value="tsEnd") String tsEnd) {

        return dataManagerSessionBean.get(scanner, null, tsStart.substring(1), tsEnd.substring(1));
    }


    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Path(value="/{scanner:[0-9]+}/topics/{topic:[a-zA-Z]+}{tsStart : (/[0-9]+)?}{tsEnd : (/[0-9]+)?}")
    public String getByScannerTopicInterval(
            @PathParam(value="topic")   String topic,
            @PathParam(value="tsStart") String tsStart,
            @PathParam(value="tsEnd")   String tsEnd,
            @PathParam(value="scanner") String scanner) {

        return dataManagerSessionBean.get(scanner, topic, tsStart.substring(1), tsEnd.substring(1));
    }

    @POST
    @Consumes(MediaType.WILDCARD)
    @Path(value="/{scanner:[a-zA-Z0-9_]+}/{topic:[a-zA-Z]+}")
    public void postStat(String content,
            @PathParam(value="topic")   String topic,
            @PathParam(value="scanner") String scanner) {
        dataManagerSessionBean.put(scanner, topic, content);
    }

    /**
     * Retrieves representation of an instance of org.unict.ing.pds.dhtdb.datamanager.ScannersResource
     * @param tsStart
     * @param tsEnd
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path(value="test")
    public String test() {
        String respo = "CIAO";
        String content = "overrided";
        respo += dataManagerSessionBean.test(content);
        return respo;
        //return dataManagerSessionBean.get(null, null, tsStart.substring(1), tsEnd.substring(1));
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
