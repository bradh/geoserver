/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.ogcapi.records;

import static org.junit.Assert.assertEquals;

import com.jayway.jsonpath.DocumentContext;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.minidev.json.JSONArray;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.config.GeoServer;
import org.geoserver.config.GeoServerInfo;
import org.geoserver.config.SettingsInfo;
import org.geoserver.data.test.MockData;
import org.geoserver.data.test.SystemTestData;
import org.jsoup.Jsoup;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class CollectionsTest extends RecordsTestSupport {

    public static final String BASIC_POLYGONS_TITLE = "Basic polygons";
    public static final String BASIC_POLYGONS_DESCRIPTION = "I love basic polygons!";

    @Override
    protected void onSetUp(SystemTestData testData) throws Exception {
        super.onSetUp(testData);

        // customize metadata and set custom CRS too
        FeatureTypeInfo basicPolygons =
                getCatalog().getFeatureTypeByName(getLayerId(MockData.BASIC_POLYGONS));
        basicPolygons.setTitle(BASIC_POLYGONS_TITLE);
        basicPolygons.setAbstract(BASIC_POLYGONS_DESCRIPTION);
        basicPolygons.setOverridingServiceSRS(true);
        basicPolygons.getResponseSRS().addAll(Arrays.asList("3857", "32632"));
        getCatalog().save(basicPolygons);
    }

    @Test
    public void testCollectionsJson() throws Exception {
        DocumentContext json = getAsJSONPath("ogc/records/collections", 200);
        testCollectionsJson(
                json, "application/json", Arrays.asList("text/html", "application/x-yaml"));
    }

    @Test
    public void testCollectionsJsonWithSlash() throws Exception {
        DocumentContext json = getAsJSONPath("ogc/records/collections/", 200);
        testCollectionsJson(
                json, "application/json", Arrays.asList("text/html", "application/x-yaml"));
    }

    private void testCollectionsJson(
            DocumentContext json, String selfType, List<String> alternateTypes) throws Exception {
        assertEquals((Integer) 3, json.read("links.length()", Integer.class));
        JSONArray selfLinks = json.read("links[?(@.rel=='self')]", JSONArray.class);
        assertEquals(1, selfLinks.size());
        Map selfLink = (Map) selfLinks.get(0);
        assertEquals(4, selfLink.size());
        assertEquals("self", selfLink.get("rel"));
        assertEquals("This document", selfLink.get("title"));
        assertEquals(selfType, selfLink.get("type"));
        assertEquals(
                "http://localhost:8080/geoserver/ogc/records/collections/?f="
                        + selfType.replace("/", "%2F"),
                selfLink.get("href"));
        List alternateLinks = json.read("links[?(@.rel=='alternate')]", List.class);
        assertEquals(2, alternateLinks.size());
        for (String alternateType : alternateTypes) {
            JSONArray alternateLinksForType =
                    json.read("links[?(@.type=='" + alternateType + "')]", JSONArray.class);
            assertEquals(1, alternateLinksForType.size());
            Map alternateLinkForType = (Map) alternateLinksForType.get(0);
            assertEquals(4, alternateLinkForType.size());
            assertEquals("alternate", alternateLinkForType.get("rel"));
            assertEquals("This document as " + alternateType, alternateLinkForType.get("title"));
            assertEquals(alternateType, alternateLinkForType.get("type"));
            assertEquals(
                    "http://localhost:8080/geoserver/ogc/records/collections/?f="
                            + alternateType.replace("/", "%2F"),
                    alternateLinkForType.get("href"));
        }

        int expected = getCatalog().getFeatureTypes().size();
        assertEquals(expected, (int) json.read("collections.length()", Integer.class));

        // check we have the expected number of links and they all use the right "rel" relation
        /* TODO
        Collection<MediaType> formats =
                GeoServerExtensions.bean(
                                APIDispatcher.class, GeoServerSystemTestSupport.applicationContext)
                        .getProducibleMediaTypes(FeaturesResponse.class, true);
        assertThat(
                formats.size(),
                lessThanOrEqualTo((int) json.read("collections[0].links.length()", Integer.class)));
        for (MediaType format : formats) {
            // check title and rel.
            List items = json.read("collections[0].links[?(@.type=='" + format + "')]", List.class);
            Map item = (Map) items.get(0);
            assertEquals("items", item.get("rel"));
        }
        */
    }

    @Test
    public void testCollectionsYaml() throws Exception {
        String yaml = getAsString("ogc/records/collections/?f=application/x-yaml");
        DocumentContext json = convertYamlToJsonPath(yaml);
        testCollectionsJson(
                json, "application/x-yaml", Arrays.asList("text/html", "application/json"));
    }

    @Test
    public void testCollectionsHTML() throws Exception {
        org.jsoup.nodes.Document document = getAsJSoup("ogc/records/collections?f=html");

        // TODO check collection links
    }

    @Test
    public void testCollectionsHTMLWithProxyBase() throws Exception {
        GeoServer gs = getGeoServer();
        GeoServerInfo info = gs.getGlobal();
        SettingsInfo settings = info.getSettings();
        settings.setProxyBaseUrl("http://testHost/geoserver");
        gs.save(info);
        try {
            org.jsoup.nodes.Document document = getAsJSoup("ogc/records/collections?f=html");

            // TODO: check collection links
        } finally {
            info = gs.getGlobal();
            settings = info.getSettings();
            settings.setProxyBaseUrl(null);
            gs.save(info);
        }
    }

    @Test
    public void testCollectionsHTMLWithProxyBaseHeader() throws Exception {
        GeoServer gs = getGeoServer();
        GeoServerInfo info = gs.getGlobal();
        SettingsInfo settings = info.getSettings();
        settings.setProxyBaseUrl("${X-Forwarded-Proto}://test-headers/geoserver/");
        info.setUseHeadersProxyURL(true);
        gs.save(info);
        try {
            MockHttpServletRequest request = createRequest("ogc/records/collections?f=text/html");
            request.setMethod("GET");
            request.setContent(new byte[] {});
            request.addHeader("X-Forwarded-Proto", "http");
            MockHttpServletResponse response = dispatch(request, null);
            assertEquals(200, response.getStatus());
            assertEquals("text/html", response.getContentType());

            // parse the HTML
            org.jsoup.nodes.Document document = Jsoup.parse(response.getContentAsString());

            // TODO: check collection links
        } finally {
            info = gs.getGlobal();
            settings = info.getSettings();
            settings.setProxyBaseUrl(null);
            info.setUseHeadersProxyURL(null);
            gs.save(info);
        }
    }
}
