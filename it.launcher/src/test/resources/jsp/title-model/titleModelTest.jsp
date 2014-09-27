<%--@elvariable id="model" type="com.ninedemons.aemtesting.models.simple.TitleModel"--%>
<%--@elvariable id="slingRequest" type="org.apache.sling.api.SlingHttpServletRequest"--%>

<%@ page trimDirectiveWhitespaces="true" %>

<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling" %>
<%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %>
<cq:defineObjects />


<%--
    Minimal JSP to test the Title Model
--%>


<sling:adaptTo adaptable="${slingRequest}" adaptTo="com.ninedemons.aemtesting.models.title.TitleModel" var="model"/>


Element is '${model.element}' <br/>
Title is '${model.text}' <br/>

