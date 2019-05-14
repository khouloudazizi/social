package org.exoplatform.social.webui.composer;

import org.exoplatform.commons.api.settings.ExoFeatureService;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.form.UIFormStringInput;

import java.util.ResourceBundle;

@ComponentConfig(
        template = "war:/groovy/social/webui/composer/UINewsActivityComposer.gtmpl",
        events = {
                @EventConfig(listeners = UIActivityComposer.CloseActionListener.class),
                @EventConfig(listeners = UIActivityComposer.SubmitContentActionListener.class),
                @EventConfig(listeners = UIActivityComposer.ActivateActionListener.class)
        }
)

public class UINewsActivityComposer extends UIActivityComposer {

  private static final String NEWS_FEATURE_NAME = "news";

  private ExoFeatureService featureService;

  public UINewsActivityComposer() {
    featureService = CommonsUtils.getService(ExoFeatureService.class);
  }

  @Override
  public boolean isEnabled() {
    return featureService.isActiveFeature(NEWS_FEATURE_NAME);
  }

  @Override
  protected void onPostActivity(UIComposer.PostContext postContext, UIComponent source, WebuiRequestContext requestContext, String postedMessage) throws Exception {

  }

  @Override
  protected void onClose(Event<UIActivityComposer> event) {

  }

  @Override
  protected void onSubmit(Event<UIActivityComposer> event) {

  }

  @Override
  protected void onActivate(Event<UIActivityComposer> event) {

  }
}
