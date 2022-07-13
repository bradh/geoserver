package org.geoserver.ogcapi.movingfeatures;

import io.swagger.v3.oas.models.OpenAPI;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.config.GeoServer;
import org.geoserver.ogcapi.APIDispatcher;
import org.geoserver.ogcapi.APIService;
import org.geoserver.ogcapi.ConformanceClass;
import org.geoserver.ogcapi.ConformanceDocument;
import org.geoserver.ogcapi.HTMLResponseBody;
import org.geoserver.ogcapi.OpenAPIMessageConverter;
import org.geoserver.platform.ServiceException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@APIService(
        service = "Moving Features",
        version = "1.0",
        landingPage = "ogc/movingfeatures",
        serviceClass = MovingFeaturesServiceInfo.class)
@RequestMapping(path = APIDispatcher.ROOT_PATH + "/movingfeatures")
public class MovingFeaturesService implements ApplicationContextAware {
    private final GeoServer geoServer;

    private static final String CONFORMANCE_COMMON =
            "http://www.opengis.net/spec/ogcapi-movingfeatures-1/1.0/conf/common";
    private static final String CONFORMANCE_COLLECTION =
            "http://www.opengis.net/spec/ogcapi-movingfeatures-1/1.0/conf/mf-collection";
    private static final String CONFORMANCE_MOVINGFEATURES =
            "http://www.opengis.net/spec/ogcapi-movingfeatures-1/1.0/conf/movingfeatures";

    private static final String DISPLAY_NAME = "OGC API Moving Features";

    public MovingFeaturesService(GeoServer geoServer) {
        this.geoServer = geoServer;
    }

    @GetMapping(name = "getLandingPage")
    @ResponseBody
    @HTMLResponseBody(templateName = "landingPage.ftl", fileName = "landingPage.html")
    public LandingPage getLandingPage() {
        MovingFeaturesServiceInfo service = getService();
        return new LandingPage(
                (service.getTitle() == null) ? "Moving Features server" : service.getTitle(),
                (service.getAbstract() == null) ? "" : service.getAbstract());
    }

    @GetMapping(
            path = "api",
            name = "getApi",
            produces = {
                OpenAPIMessageConverter.OPEN_API_MEDIA_TYPE_VALUE,
                "application/x-yaml",
                MediaType.TEXT_XML_VALUE
            })
    @ResponseBody
    @HTMLResponseBody(templateName = "api.ftl", fileName = "api.html")
    public OpenAPI api() throws IOException {
        return new APIBuilder(geoServer).build(getService());
    }

    @GetMapping(path = "collections", name = "getCollections")
    @ResponseBody
    @HTMLResponseBody(templateName = "collections.ftl", fileName = "collections.html")
    public CollectionsDocument getCollections() {
        return new CollectionsDocument(geoServer);
    }

    @GetMapping(path = "collections/{collectionId}", name = "describeCollection")
    @ResponseBody
    @HTMLResponseBody(templateName = "collection.ftl", fileName = "collection.html")
    public CollectionDocument collection(@PathVariable(name = "collectionId") String collectionId)
            throws IOException {
        FeatureTypeInfo ft = getFeatureType(collectionId);
        CollectionDocument collection = new CollectionDocument(geoServer, ft);

        return collection;
    }

    private FeatureTypeInfo getFeatureType(String collectionId) {
        // single collection
        FeatureTypeInfo featureType = getCatalog().getFeatureTypeByName(collectionId);
        if (featureType == null) {
            throw new ServiceException(
                    "Unknown collection " + collectionId,
                    ServiceException.INVALID_PARAMETER_VALUE,
                    "collectionId");
        }
        return featureType;
    }

    private Catalog getCatalog() {
        return geoServer.getCatalog();
    }

    @GetMapping(path = "conformance", name = "getConformanceDeclaration")
    @ResponseBody
    @HTMLResponseBody(templateName = "conformance.ftl", fileName = "conformance.html")
    public ConformanceDocument conformance() {
        List<String> classes =
                Arrays.asList(
                        ConformanceClass.CORE,
                        ConformanceClass.COLLECTIONS,
                        CONFORMANCE_COMMON,
                        CONFORMANCE_COLLECTION,
                        CONFORMANCE_MOVINGFEATURES);
        return new ConformanceDocument(DISPLAY_NAME, classes);
    }

    public MovingFeaturesServiceInfo getService() {
        return geoServer.getService(MovingFeaturesServiceInfo.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {}
}
