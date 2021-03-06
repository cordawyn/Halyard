/*
 * Copyright 2018 Merck Sharp & Dohme Corp. a subsidiary of Merck & Co.,
 * Inc., Kenilworth, NJ, USA.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.msd.gin.halyard.tools;

import com.msd.gin.halyard.common.HBaseServerTestInstance;
import com.msd.gin.halyard.sail.HBaseSail;
import java.io.File;
import java.io.PrintStream;
import org.apache.hadoop.util.ToolRunner;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Adam Sotona (MSD)
 */
public class HalyardBulkExportTest {

    @Test
    public void testBulkExport() throws Exception {
        HBaseSail sail = new HBaseSail(HBaseServerTestInstance.getInstanceConfig(), "bulkExportTable", true, 0, true, 0, null, null);
        sail.initialize();
        ValueFactory vf = SimpleValueFactory.getInstance();
        for (int i = 0; i < 1000; i++) {
            sail.addStatement(vf.createIRI("http://whatever/NTsubj"), vf.createIRI("http://whatever/NTpred" + i),  vf.createLiteral("whatever NT value " + i));
        }
        sail.commit();
        sail.close();

        File root = File.createTempFile("test_bulkExport", "");
        root.delete();
        root.mkdirs();

        File q = new File(root, "test_bulkExport.sparql");
        q.deleteOnExit();
        try (PrintStream qs = new PrintStream(q)) {
            qs.println("select * where {?s ?p ?o}");
        }

        assertEquals(0, ToolRunner.run(HBaseServerTestInstance.getInstanceConfig(), new HalyardBulkExport(),
                new String[]{"-s", "bulkExportTable", "-q", q.toURI().toURL().toString(), "-t", root.toURI().toURL().toString() + "{0}.csv"}));

        File f = new File(root, "test_bulkExport.csv");
        assertTrue(f.isFile());
        assertEquals(1001, HalyardExportTest.getLinesCount(f.toURI().toURL().toString(), null));

        q.delete();
        f.delete();
        root.delete();
    }

    @Test
    public void testRunNoArgs() throws Exception {
        assertEquals(-1, new HalyardBulkExport().run(new String[0]));
    }
}
