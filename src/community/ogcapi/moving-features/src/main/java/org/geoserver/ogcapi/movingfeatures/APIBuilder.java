package org.geoserver.ogcapi.movingfeatures;

import io.swagger.v3.oas.models.OpenAPI;
import java.io.IOException;
import org.geoserver.config.GeoServer;
import org.geoserver.ogcapi.OpenAPIBuilder;

public class APIBuilder extends OpenAPIBuilder<MovingFeaturesServiceInfo> {

    private final GeoServer geoServer;

    public APIBuilder(GeoServer geoServer) {
        super(
                MovingFeaturesServiceInfo.class,
                "openapi.yml",
                "Moving Features API",
                "ogc/movingfeatures");
        this.geoServer = geoServer;
    }

    @Override
    @SuppressWarnings("unchecked") // getSchema() not generified
    public OpenAPI build(MovingFeaturesServiceInfo service) throws IOException {
        OpenAPI api = super.build(service);

        // adjust path output formats
        declareGetResponseFormats(api, "/collections", CollectionsDocument.class);
        declareGetResponseFormats(api, "/collections/{collectionId}", CollectionDocument.class);

        return api;
    }
}
