package org.geoserver.ogcapi.movingfeatures;

import org.geoserver.config.GeoServer;
import org.geoserver.config.util.XStreamServiceLoader;
import org.geoserver.platform.GeoServerResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class MovingFeaturesXStreamLoader extends XStreamServiceLoader<MovingFeaturesServiceInfo> {

    public MovingFeaturesXStreamLoader(GeoServerResourceLoader resourceLoader) {
        super(resourceLoader, "tiles");
    }

    @Override
    protected MovingFeaturesServiceInfo createServiceFromScratch(GeoServer gs) {
        MovingFeaturesServiceInfoImpl info = new MovingFeaturesServiceInfoImpl();
        info.setName("movingfeatures");
        info.setTitle("Moving Features 1.0 server");
        return info;
    }

    @Override
    public Class<MovingFeaturesServiceInfo> getServiceClass() {
        return MovingFeaturesServiceInfo.class;
    }
}
