<#include "common-header.ftl">
       <h2>GeoServer OGC API Records Conformance</h2>
       <p>This document lists the OGC API conformance classes that are implemented by this service.<br/>
       
       <p>Conformance classes:</p>
       <ul>
       <#list model.conformsTo as conformsTo>
       <li>${conformsTo}</li>
       </#list>
       </ul>
<#include "common-footer.ftl">
