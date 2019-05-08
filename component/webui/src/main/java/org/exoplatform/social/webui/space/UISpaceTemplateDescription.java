package org.exoplatform.social.webui.space;

import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.social.core.space.spi.SpaceTemplateService;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIComponent;

@ComponentConfig(
    template = "war:/groovy/social/webui/space/UISpaceTemplateDescription.gtmpl"
)
public class UISpaceTemplateDescription extends UIComponent {

  private String templateName;
  private SpaceTemplateService spaceTemplateService;

  /**
   * Constructor
   * @throws Exception
   */
  public UISpaceTemplateDescription() throws Exception {
    super();
    spaceTemplateService = CommonsUtils.getService(SpaceTemplateService.class);
    templateName = spaceTemplateService.getDefaultSpaceTemplate();
  }

  public String getTemplateName() {
    return templateName;
  }

  public void setTemplateName(String name) {
    this.templateName = name;
  }
}
