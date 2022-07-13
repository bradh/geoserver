/* (c) 2019 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.ogcapi.movingfeatures;

import com.jayway.jsonpath.DocumentContext;
import org.geoserver.ogcapi.OGCApiTestSupport;
import org.junit.Test;
import org.springframework.http.MediaType;

public class CollectionsTest extends OGCApiTestSupport {

    @Test
    public void testCollectionsJson() throws Exception {
        DocumentContext json = getAsJSONPath("ogc/movingfeatures/collections", 200);
        testCollectionsJson(json, MediaType.APPLICATION_JSON);
    }

    @Test
    public void testCollectionsYaml() throws Exception {
        String yaml = getAsString("ogc/movingfeatures/collections/?f=application/x-yaml");
        DocumentContext json = convertYamlToJsonPath(yaml);
        testCollectionsJson(json, MediaType.parseMediaType("application/x-yaml"));
    }

    private void testCollectionsJson(DocumentContext json, MediaType defaultFormat)
            throws Exception {
        // TODO: check once we have some collections
    }

    @Test
    public void testCollectionsHTML() throws Exception {
        org.jsoup.nodes.Document document = getAsJSoup("ogc/movingfeatures/collections?f=html");
        // TODO: check once we have some collections
    }
}
