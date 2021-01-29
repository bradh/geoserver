/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.ogcapi.records;

import io.swagger.v3.oas.models.OpenAPI;
import org.geoserver.csw.CSWInfo;
import org.geoserver.ogcapi.ConformanceDocument;

/** Builds the OGC Records OpenAPI document */
public class RecordsAPIBuilder extends org.geoserver.ogcapi.OpenAPIBuilder<CSWInfo> {

    public RecordsAPIBuilder() {
        super(RecordsAPIBuilder.class, "openapi.yaml", "Records 1.0 server", "ogc/records");
    }

    /**
     * Build the document based on request, current configuration, and list of available extensions
     *
     * @param cswInfo The Catalog Service for the Web configuration
     */
    @Override
    public OpenAPI build(CSWInfo cswInfo) {
        OpenAPI api = super.build(cswInfo);

        // adjust path output formats
        declareGetResponseFormats(api, "/", OpenAPI.class);
        declareGetResponseFormats(api, "/conformance", ConformanceDocument.class);
        // declareGetResponseFormats(api, "/collections", CollectionsDocument.class);
        // declareGetResponseFormats(api, "/collections/{collectionId}", CollectionsDocument.class);
        // declareGetResponseFormats(api, "/collections/{collectionId}/items",
        // FeaturesResponse.class);
        // declareGetResponseFormats(
        //        api, "/collections/{collectionId}/items/{featureId}", FeaturesResponse.class);

        // provide a list of valid values for collectionId
        /*
        Map<String, Parameter> parameters = api.getComponents().getParameters();
        Parameter collectionId = parameters.get("collectionId");
        Catalog catalog = cswInfo.getGeoServer().getCatalog();
        List<String> validCollectionIds =
                catalog.getFeatureTypes()
                        .stream()
                        .map(ft -> ft.prefixedName())
                        .collect(Collectors.toList());
        collectionId.getSchema().setEnum(validCollectionIds);

        // provide actual values for limit
        Parameter limit = parameters.get("limit");
        BigDecimal limitMax;
        if (cswInfo.getMaxFeatures() > 0) {
            limitMax = BigDecimal.valueOf(cswInfo.getMaxFeatures());
        } else {
            limitMax = BigDecimal.valueOf(Integer.MAX_VALUE);
        }
        limit.getSchema().setMaximum(limitMax);
        // for the moment we don't have a setting for the default, keep it same as max
        limit.getSchema().setDefault(limitMax);
        */
        return api;
    }
}
