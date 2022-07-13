/* (c) 2019 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.ogcapi.movingfeatures;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.geoserver.ogcapi.AbstractLandingPageDocument;
import org.geoserver.ogcapi.Link;
import org.geoserver.ogcapi.LinksBuilder;

/** A Moving Features server landing page */
@JsonPropertyOrder({"title", "description", "links"})
public class LandingPage extends AbstractLandingPageDocument {

    public static final String SERVICE_BASE = "ogc/movingfeatures";

    public LandingPage(String title, String description) {
        super(title, description, SERVICE_BASE);

        // collections
        new LinksBuilder(CollectionsDocument.class, SERVICE_BASE)
                .segment("/collections")
                .title("Image collections metadata as ")
                .rel(Link.REL_DATA_URI)
                .add(this);
    }
}
