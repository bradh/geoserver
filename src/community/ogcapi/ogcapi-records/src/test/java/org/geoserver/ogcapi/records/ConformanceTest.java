/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.ogcapi.records;

import static org.junit.Assert.assertEquals;

import com.jayway.jsonpath.DocumentContext;
import org.junit.Test;

/**
 * Tests for OGC API Records Core Conformance.
 *
 * <p>Corresponds to Requirement 2 for the draft as of 29 Jan 2021.
 */
public class ConformanceTest extends RecordsTestSupport {

    @Test
    public void testConformanceJson() throws Exception {
        DocumentContext json = getAsJSONPath("ogc/records/conformance", 200);
        checkConformance(json);
    }

    @Test
    public void testConformanceJsonExplicit() throws Exception {
        DocumentContext json = getAsJSONPath("ogc/records/conformance?f=application/json", 200);
        checkConformance(json);
    }

    private void checkConformance(DocumentContext json) {
        assertEquals(
                "http://www.opengis.net/spec/ogcapi-common-1/1.0/conf/core",
                json.read("$.conformsTo[0]", String.class));
        assertEquals(
                "http://www.opengis.net/spec/ogcapi-common-2/1.0/conf/collections",
                json.read("$.conformsTo[1]", String.class));
        assertEquals(
                "http://www.opengis.net/spec/ogcapi-records-1/1.0/conf/core",
                json.read("$.conformsTo[2]", String.class));
    }

    @Test
    public void testConformanceHTML() throws Exception {
        org.jsoup.nodes.Document document = getAsJSoup("ogc/records/conformance?f=text/html");
        assertEquals(
                "http://www.opengis.net/spec/ogcapi-common-1/1.0/conf/core",
                document.select("#content li:eq(0)").text());
        assertEquals(
                "http://www.opengis.net/spec/ogcapi-common-2/1.0/conf/collections",
                document.select("#content li:eq(1)").text());
        assertEquals(
                "http://www.opengis.net/spec/ogcapi-records-1/1.0/conf/core",
                document.select("#content li:eq(2)").text());
    }

    @Test
    public void testCollectionsYaml() throws Exception {
        String yaml = getAsString("ogc/records/conformance?f=application/x-yaml");
        checkConformance(convertYamlToJsonPath(yaml));
    }
}
