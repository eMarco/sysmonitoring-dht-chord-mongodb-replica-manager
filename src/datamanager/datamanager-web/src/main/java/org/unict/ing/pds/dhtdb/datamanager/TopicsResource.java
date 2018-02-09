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
package org.unict.ing.pds.dhtdb.datamanager;

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
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 */
@Path("topics")
@RequestScoped
public class TopicsResource {

    DataManagerSessionBeanLocal dataManagerSessionBean = lookupDataManagerSessionBeanLocal();


    @Context
    private UriInfo context;

    /**
     * Creates a new instance of TopicsResource
     */
    public TopicsResource() {
    }
    
    /**
     * /topics/$topic/$tsStart/$tsEnd
     * @param topic  |
     * @param tsStart timestamp in seconds since Epoch (optional) |
     * @param tsEnd timestamp in seconds since Epoch (optional) |
     * @return |
     */
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Path(value="/{topic:[a-zA-Z]+}{tsStart : (/[0-9]+)?}{tsEnd : (/[0-9]+)?}")
    public String getByTopicInterval(
            @PathParam(value="topic") String topic,
            @PathParam(value="tsStart") String tsStart,
            @PathParam(value="tsEnd") String tsEnd) {

        return dataManagerSessionBean.get(null, topic,  RestHelper.ts(tsStart), RestHelper.ts(tsEnd));
    }

    /**
     * /topics/$topic/scanners/$scanner/tsStart/tsEnd
     * @param topic |
     * @param tsStart timestamp in seconds since Epoch (optional) |
     * @param tsEnd timestamp in seconds since Epoch (optional) |
     * @param scanner |
     * @return | 
     */
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Path(value="/{topic:[a-zA-Z]+}/scanners/{scanner:[a-zA-Z_]+_[0-9]+}{tsStart : (/[0-9]+)?}{tsEnd : (/[0-9]+)?}")
    public String getByTopicsScannerInterval(
            @PathParam(value="topic")   String topic,
            @PathParam(value="tsStart") String tsStart,
            @PathParam(value="tsEnd")   String tsEnd,
            @PathParam(value="scanner") String scanner) {

        return dataManagerSessionBean.get(scanner, topic, RestHelper.ts(tsStart), RestHelper.ts(tsEnd));
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
