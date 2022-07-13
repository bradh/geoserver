package org.geoserver.ogcapi.movingfeatures;

import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.config.GeoServer;
import org.geoserver.ogcapi.AbstractCollectionDocument;
import org.geoserver.ogcapi.CollectionExtents;

public class CollectionDocument extends AbstractCollectionDocument<FeatureTypeInfo> {

    private final GeoServer geoServer;

    public CollectionDocument(GeoServer geoServer, FeatureTypeInfo featureType) {
        super(featureType);
        this.geoServer = geoServer;

        this.id = featureType.prefixedName();
        this.title = featureType.getTitle();
        this.description = featureType.getAbstract();
        this.extent = new CollectionExtents(featureType.getLatLonBoundingBox());
        addSelfLinks("ogc/movingfeatures/collections/" + id);
        // TODO: add links to feature type content
    }
}
