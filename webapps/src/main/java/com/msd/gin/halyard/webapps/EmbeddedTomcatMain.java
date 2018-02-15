/*
 * Copyright © 2018 Merck Sharp & Dohme Corp., a subsidiary of Merck & Co., Inc.
 * All rights reserved.
 */
package com.msd.gin.halyard.webapps;

import java.io.File;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.Tomcat;

/**
 *
 * @author Adam Sotona (MSD)
 */
public class EmbeddedTomcatMain {
    public static void main(String args[]) throws Exception {
        Tomcat t = new Tomcat();
        StandardHost sh = (StandardHost) t.getHost();
        sh.setAutoDeploy(false);
        sh.setUnpackWARs(false);
        sh.setCreateDirs(false);
        t.addWebapp("/rdf4j-server", new File("rdf4j-server.war").getCanonicalPath());
        t.addWebapp("/rdf4j-workbench", new File("rdf4j-workbench.war").getCanonicalPath());
        t.start();
        t.getServer().await();
    }
}
