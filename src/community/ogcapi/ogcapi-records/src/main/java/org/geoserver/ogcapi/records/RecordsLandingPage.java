/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.ogcapi.records;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.geoserver.catalog.Catalog;
import org.geoserver.csw.CSWInfo;
import org.geoserver.ogcapi.AbstractLandingPageDocument;

/** A Features server landing page */
@JsonPropertyOrder({"title", "description", "links"})
public class RecordsLandingPage extends AbstractLandingPageDocument {

    public RecordsLandingPage(CSWInfo wfs, Catalog catalog, String featuresBase) {
        super(
                (wfs.getTitle() == null) ? "Records 1.0 server" : wfs.getTitle(),
                (wfs.getAbstract() == null) ? "" : wfs.getAbstract(),
                "ogc/records");
        /*
                // collections
                addLinksFor(
                        featuresBase + "/collections",
                        CollectionsDocument.class,
                        "Collections Metadata as ",
                        "collections",
                        null,
                        Link.REL_DATA);
        */
    }
}
