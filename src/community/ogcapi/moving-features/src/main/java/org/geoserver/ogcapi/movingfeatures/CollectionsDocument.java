/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.ogcapi.movingfeatures;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.Iterator;
import java.util.List;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.util.CloseableIterator;
import org.geoserver.config.GeoServer;
import org.geoserver.ogcapi.AbstractDocument;
import org.geoserver.ogcapi.Link;
import org.opengis.filter.Filter;

/**
 * A class representing the OGC API for Moving Features server "collections" in a way that Jackson
 * can easily translate to JSON/YAML (and can be used as a Freemarker template model)
 */
@JacksonXmlRootElement(localName = "Collections")
@JsonPropertyOrder({"links", "collections"})
public class CollectionsDocument extends AbstractDocument {

    private final GeoServer geoServer;

    public CollectionsDocument(GeoServer geoServer) {
        this.geoServer = geoServer;
        String path = "ogc/movingfeatures/collections/";
        addSelfLinks(path);
    }

    @Override
    @JacksonXmlProperty(localName = "Links")
    public List<Link> getLinks() {
        return links;
    }

    @JacksonXmlProperty(localName = "Collection")
    @SuppressWarnings("PMD.CloseResource") // closed while iterating over it
    public Iterator<CollectionDocument> getCollections() {
        CloseableIterator<FeatureTypeInfo> featureTypes =
                geoServer.getCatalog().list(FeatureTypeInfo.class, Filter.INCLUDE);
        return new Iterator<CollectionDocument>() {
            @Override
            public boolean hasNext() {
                // TODO
                return false;
            }

            @Override
            public CollectionDocument next() {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from
                // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }
        };
    }
}
