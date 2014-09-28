package com.ninedemons.aemtesting.models.title;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.designer.Style;
import com.day.cq.wcm.commons.WCMUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.Via;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

/**
 * A model for the Title component.
 *
 * This is a drop in replacement for the auto-generated title.js javascript model the maven archetype creates.
 */

@Model(adaptables = SlingHttpServletRequest.class)
public class TitleModel {

    private static final Logger LOG = LoggerFactory.getLogger(TitleModel.class);

    /** TODO This model has knowledge about the HTML elements to use when rendering the title. This violates separation
     *  of concerns between a model and the rendering layer. This part of the model should be embedded in the sightly
     *  markup, not here.
     */

    /**
     * The default HTML element to use when none explicitly set
     */
    private static final String DEFAULT_ELEMENT = "h2";

    /**
     * Holds the mappings of the type of the title to the actual HTML element to use
     */
    private final static Map<String,String> TYPE_TO_ELEMENT;

    static {
        TYPE_TO_ELEMENT = new HashMap<String, String>(2);
        TYPE_TO_ELEMENT.put("small", "h3");
        TYPE_TO_ELEMENT.put("extralarge", "h1");
    }


    @Optional
    @Inject @Via("resource") @Named(JcrConstants.JCR_TITLE)
    private String componentTitle;

    @Optional
    @Inject @Via("resource")
    private String type;


    /**
     * The text for use in the title
     */
    private String text;

    /**
     * The HTML element that the title text will be wrapped in
     */
    private String element;


    private SlingHttpServletRequest request;


    public TitleModel(SlingHttpServletRequest request) {
        this.request = request;
    }

    public String getText() {
        return text;
    }

    public String getElement() {
        return element;
    }

    @PostConstruct
    private void setup() {
        findCurrentStyle();
        findContainingPage();
        findText();
        findElement();
    }

    private Style findCurrentStyle() {
        return WCMUtils.getStyle(request);
    }

    private Page findContainingPage() {
        final Resource resource = request.getResource();
        final ResourceResolver resourceResolver = resource.getResourceResolver();
        final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        return pageManager.getContainingPage(resource);
    }


    private void findText() {
        LOG.trace("Finding text to use");

        LOG.trace("Considering the component jcr:text property");
        text = componentTitle;

        if (StringUtils.isBlank(text)) {

            Page containingPage = findContainingPage();

            LOG.trace("Considering the text of the current page");
            text = containingPage.getTitle();

            if (StringUtils.isBlank(text)) {

                LOG.trace("Defaulting to the name of the current page");
                text = containingPage.getName();
            }

        }
        LOG.trace("Text for the title is '{}'",text);


    }

    private void findElement() {
        String type = findType();
        element = MapUtils.getString(TYPE_TO_ELEMENT,type,DEFAULT_ELEMENT);
        LOG.trace("HTML element for the title is '{}'",element);

    }

    private String findType() {
        LOG.trace("Finding type to use");

        LOG.trace("Considering the component type property");
        String type = this.type;

        if (StringUtils.isBlank(type)) {
            LOG.trace("Defaulting to Design default type");
            findCurrentStyle().get("defaultType",StringUtils.EMPTY);
        }
        return type;
    }

}

