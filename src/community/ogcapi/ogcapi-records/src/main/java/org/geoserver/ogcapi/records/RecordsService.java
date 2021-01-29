/* (c) 2019 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.ogcapi.records;

import io.swagger.v3.oas.models.OpenAPI;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.geoserver.catalog.Catalog;
import org.geoserver.config.GeoServer;
import org.geoserver.csw.CSWInfo;
import org.geoserver.ogcapi.APIDispatcher;
import org.geoserver.ogcapi.APIFilterParser;
import org.geoserver.ogcapi.APIService;
import org.geoserver.ogcapi.ConformanceClass;
import org.geoserver.ogcapi.ConformanceDocument;
import org.geoserver.ogcapi.HTMLResponseBody;
import org.geoserver.ogcapi.OpenAPIMessageConverter;
import org.geoserver.ows.kvp.TimeParser;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/** Implementation of OGC Features API service */
@APIService(
    service = "Records",
    version = "1.0",
    landingPage = "ogc/records",
    serviceClass = CSWInfo.class
)
@RequestMapping(path = APIDispatcher.ROOT_PATH + "/records")
public class RecordsService {

    static final Pattern INTEGER = Pattern.compile("\\d+");

    public static final String CORE = "http://www.opengis.net/spec/ogcapi-records-1/1.0/conf/core";

    private final GeoServer geoServer;
    private final APIFilterParser filterParser;

    private TimeParser timeParser = new TimeParser();

    public RecordsService(GeoServer geoServer, APIFilterParser filterParser) {
        this.geoServer = geoServer;
        this.filterParser = filterParser;
    }

    public CSWInfo getService() {
        return geoServer.getService(CSWInfo.class);
    }

    private Catalog getCatalog() {
        return geoServer.getCatalog();
    }

    @GetMapping(name = "getLandingPage")
    @ResponseBody
    @HTMLResponseBody(templateName = "landingPage.ftl", fileName = "landingPage.html")
    public RecordsLandingPage getLandingPage() {
        return new RecordsLandingPage(getService(), getCatalog(), "ogc/records");
    }

    @GetMapping(
        path = "api",
        name = "getApi",
        produces = {
            OpenAPIMessageConverter.OPEN_API_MEDIA_TYPE_VALUE,
            "application/x-yaml",
            MediaType.TEXT_XML_VALUE
        }
    )
    @ResponseBody
    @HTMLResponseBody(templateName = "api.ftl", fileName = "api.html")
    public OpenAPI api() {
        return new RecordsAPIBuilder().build(getService());
    }
    /*
        @GetMapping(path = "collections", name = "getCollections")
        @ResponseBody
        @HTMLResponseBody(templateName = "collections.ftl", fileName = "collections.html")
        public CollectionsDocument getCollections() {
            return new CollectionsDocument(geoServer, getServiceCRSList());
        }

        @GetMapping(path = "filter-capabilities", name = "getFilterCapabilities")
        @ResponseBody
        @HTMLResponseBody(
            templateName = "filter-capabilities.ftl",
            fileName = "filter-capabilities.html"
        )
        public FilterCapabilitiesDocument getFilterCapabilities() {
            return new FilterCapabilitiesDocument();
        }

        @GetMapping(path = "collections/{collectionId}", name = "describeCollection")
        @ResponseBody
        @HTMLResponseBody(templateName = "collection.ftl", fileName = "collection.html")
        public CollectionDocument collection(@PathVariable(name = "collectionId") String collectionId) {
            FeatureTypeInfo ft = getFeatureType(collectionId);
            CollectionDocument collection =
                    new CollectionDocument(geoServer, ft, getFeatureTypeCRS(ft, getServiceCRSList()));

            return collection;
        }

        @GetMapping(path = "collections/{collectionId}/queryables", name = "getQueryables")
        @ResponseBody
        @HTMLResponseBody(templateName = "queryables.ftl", fileName = "queryables.html")
        public QueryablesDocument queryables(@PathVariable(name = "collectionId") String collectionId)
                throws IOException {
            FeatureTypeInfo ft = getFeatureType(collectionId);
            return new QueryablesDocument(ft);
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
    */
    @GetMapping(path = "conformance", name = "getConformanceDeclaration")
    @ResponseBody
    @HTMLResponseBody(templateName = "conformance.ftl", fileName = "conformance.html")
    public ConformanceDocument conformance() {
        List<String> classes =
                Arrays.asList(ConformanceClass.CORE, ConformanceClass.COLLECTIONS, CORE);
        return new ConformanceDocument(classes);
    }

    /*
        @GetMapping(path = "collections/{collectionId}/items/{itemId:.+}", name = "getFeature")
        @ResponseBody
        @DefaultContentType(RFCGeoJSONFeaturesResponse.MIME)
        public FeaturesResponse item(
                @PathVariable(name = "collectionId") String collectionId,
                @RequestParam(name = "startIndex", required = false, defaultValue = "0")
                        BigInteger startIndex,
                @RequestParam(name = "limit", required = false) BigInteger limit,
                @RequestParam(name = "bbox", required = false) String bbox,
                @RequestParam(name = "bbox-crs", required = false) String bboxCRS,
                @RequestParam(name = "time", required = false) String time,
                @PathVariable(name = "itemId") String itemId,
                @RequestParam(name = "crs", required = false) String crs)
                throws Exception {
            return items(collectionId, startIndex, limit, bbox, bboxCRS, crs, time, null, null, itemId);
        }

        @GetMapping(path = "collections/{collectionId}/items", name = "getFeatures")
        @ResponseBody
        @DefaultContentType(RFCGeoJSONFeaturesResponse.MIME)
        public FeaturesResponse items(
                @PathVariable(name = "collectionId") String collectionId,
                @RequestParam(name = "startIndex", required = false, defaultValue = "0")
                        BigInteger startIndex,
                @RequestParam(name = "limit", required = false) BigInteger limit,
                @RequestParam(name = "bbox", required = false) String bbox,
                @RequestParam(name = "bbox-crs", required = false) String bboxCRS,
                @RequestParam(name = "datetime", required = false) String datetime,
                @RequestParam(name = "filter", required = false) String filter,
                @RequestParam(name = "filter-lang", required = false) String filterLanguage,
                @RequestParam(name = "crs", required = false) String crs,
                String itemId)
                throws Exception {
            // build the request in a way core WFS machinery can understand it
            FeatureTypeInfo ft = getFeatureType(collectionId);
            GetFeatureRequest request =
                    GetFeatureRequest.adapt(Wfs20Factory.eINSTANCE.createGetFeatureType());
            Query query = request.createQuery();
            query.setTypeNames(Arrays.asList(new QName(ft.getNamespace().getURI(), ft.getName())));
            List<Filter> filters = new ArrayList<>();
            if (bbox != null) {
                CoordinateReferenceSystem queryCRS = DefaultGeographicCRS.WGS84;
                if (bboxCRS != null) {
                    queryCRS = CRS.decode(bboxCRS);
                }
                filters.add(APIBBoxParser.toFilter(bbox, queryCRS));
            }
            if (datetime != null) {
                filters.add(buildTimeFilter(ft, datetime));
            }
            if (itemId != null) {
                filters.add(FF.id(FF.featureId(itemId)));
            }
            if (filter != null) {
                Filter parsedFilter = filterParser.parse(filter, filterLanguage);
                filters.add(parsedFilter);
            }
            query.setFilter(mergeFiltersAnd(filters));
            if (crs != null) {
                query.setSrsName(new URI(crs));
            } else {
                query.setSrsName(new URI("EPSG:4326"));
            }
            request.setStartIndex(startIndex);
            request.setMaxFeatures(limit);
            request.setBaseUrl(APIRequestInfo.get().getBaseURL());
            request.getAdaptedQueries().add(query.getAdaptee());

            // run it
            FeaturesGetFeature gf = new FeaturesGetFeature(getService(), getCatalog());
            gf.setFilterFactory(FF);
            gf.setStoredQueryProvider(getStoredQueryProvider());
            FeatureCollectionResponse response = gf.run(request);

            // store information about single vs multi request
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                requestAttributes.setAttribute(ITEM_ID, itemId, RequestAttributes.SCOPE_REQUEST);
            }

            // build a response tracking both results and request to allow reusing the existing WFS
            // output formats
            return new FeaturesResponse(request.getAdaptee(), response);
        }

        private Filter buildTimeFilter(FeatureTypeInfo ft, String time)
                throws ParseException, IOException {
            Collection times = timeParser.parse(time);
            if (times.isEmpty() || times.size() > 1) {
                throw new ServiceException(
                        "Invalid time specification, must be a single time, or a time range",
                        ServiceException.INVALID_PARAMETER_VALUE,
                        "time");
            }

            List<Filter> filters = new ArrayList<>();
            Object timeSpec = times.iterator().next();
            for (String timeProperty : getTimeProperties(ft)) {
                PropertyName property = FF.property(timeProperty);
                Filter filter;
                if (timeSpec instanceof Date) {
                    filter = FF.equals(property, FF.literal(timeSpec));
                } else if (timeSpec instanceof DateRange) {
                    DateRange dateRange = (DateRange) timeSpec;
                    Literal before = FF.literal(dateRange.getMinValue());
                    Literal after = FF.literal(dateRange.getMaxValue());
                    filter = FF.between(property, before, after);
                } else {
                    throw new IllegalArgumentException("Cannot build time filter out of " + timeSpec);
                }

                filters.add(filter);
            }

            return mergeFiltersOr(filters);
        }

        private List<String> getTimeProperties(FeatureTypeInfo ft) throws IOException {
            FeatureType schema = ft.getFeatureType();
            return schema.getDescriptors()
                    .stream()
                    .filter(pd -> Date.class.isAssignableFrom(pd.getType().getBinding()))
                    .map(pd -> pd.getName().getLocalPart())
                    .collect(Collectors.toList());
        }

        private Filter mergeFiltersAnd(List<Filter> filters) {
            if (filters.isEmpty()) {
                return Filter.INCLUDE;
            } else if (filters.size() == 1) {
                return filters.get(0);
            } else {
                return FF.and(filters);
            }
        }

        private Filter mergeFiltersOr(List<Filter> filters) {
            if (filters.isEmpty()) {
                return Filter.EXCLUDE;
            } else if (filters.size() == 1) {
                return filters.get(0);
            } else {
                return FF.or(filters);
            }
        }

        private StoredQueryProvider getStoredQueryProvider() {
            return new StoredQueryProvider(getCatalog());
        }
    */
}
