/* (c) 2019 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.ogcapi.movingfeatures;

import static org.junit.Assert.assertEquals;

import com.jayway.jsonpath.DocumentContext;
import org.geoserver.ogcapi.ConformanceClass;
import org.geoserver.ogcapi.OGCApiTestSupport;
import org.junit.Test;

public class ConformanceTest extends OGCApiTestSupport {

    @Test
    public void testConformanceJson() throws Exception {
        DocumentContext json = getAsJSONPath("ogc/movingfeatures/conformance", 200);
        checkConformance(json);
    }

    private void checkConformance(DocumentContext json) {
        assertEquals(2, (int) json.read("$.length()", Integer.class));
        assertEquals(5, (int) json.read("$.conformsTo.length()", Integer.class));
        assertEquals(ConformanceClass.CORE, json.read("$.conformsTo[0]", String.class));
        assertEquals(ConformanceClass.COLLECTIONS, json.read("$.conformsTo[1]", String.class));
        assertEquals(
                "http://www.opengis.net/spec/ogcapi-movingfeatures-1/1.0/conf/common",
                json.read("$.conformsTo[2]", String.class));
        assertEquals(
                "http://www.opengis.net/spec/ogcapi-movingfeatures-1/1.0/conf/mf-collection",
                json.read("$.conformsTo[3]", String.class));
        assertEquals(
                "http://www.opengis.net/spec/ogcapi-movingfeatures-1/1.0/conf/movingfeatures",
                json.read("$.conformsTo[4]", String.class));
    }

    @Test
    public void testCollectionsYaml() throws Exception {
        String yaml = getAsString("ogc/movingfeatures/conformance/?f=application/x-yaml");
        checkConformance(convertYamlToJsonPath(yaml));
    }

    @Test
    public void testConformanceHTML() throws Exception {
        org.jsoup.nodes.Document document =
                getAsJSoup("ogc/movingfeatures/conformance?f=text/html");
        assertEquals(
                "GeoServer OGC API Moving Features Conformance", document.select("#title").text());
        assertEquals(ConformanceClass.CORE, document.select("#content li:eq(0)").text());
        assertEquals(ConformanceClass.COLLECTIONS, document.select("#content li:eq(1)").text());
        assertEquals(
                "http://www.opengis.net/spec/ogcapi-movingfeatures-1/1.0/conf/common",
                document.select("#content li:eq(2)").text());
        assertEquals(
                "http://www.opengis.net/spec/ogcapi-movingfeatures-1/1.0/conf/mf-collection",
                document.select("#content li:eq(3)").text());
        assertEquals(
                "http://www.opengis.net/spec/ogcapi-movingfeatures-1/1.0/conf/movingfeatures",
                document.select("#content li:eq(4)").text());
    }
}
