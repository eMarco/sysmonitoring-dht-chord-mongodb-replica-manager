/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.datamanager.lightBeans;

import javax.ejb.Local;
import org.unict.ing.pds.dhtdb.utils.model.GenericStat;
import org.unict.ing.pds.light.utils.Bucket;
import org.unict.ing.pds.light.utils.Label;

/**
 *
 * @author aleskandro
 */
@Local
public interface LightSessionBeanLocal {

    public int getTreeHeight();

    public void setTreeHeight(int treeHeight);

    public void checkTreeHeight(Label label);

    public Bucket splitAndPut(Bucket localBucket, long timestamp, GenericStat elem);
}
