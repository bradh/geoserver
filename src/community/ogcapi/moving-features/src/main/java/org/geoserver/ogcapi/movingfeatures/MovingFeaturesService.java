package org.geoserver.ogcapi.movingfeatures;

import java.util.Arrays;
import java.util.List;
import org.geoserver.config.GeoServer;
import org.geoserver.ogcapi.APIDispatcher;
import org.geoserver.ogcapi.APIService;
import org.geoserver.ogcapi.ConformanceClass;
import org.geoserver.ogcapi.ConformanceDocument;
import org.geoserver.ogcapi.HTMLResponseBody;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.GetMapping;
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
