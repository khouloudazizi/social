package org.exoplatform.social.webui.composer;

import org.exoplatform.commons.api.settings.ExoFeatureService;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.activity.model.ExoSocialActivityImpl;
import org.exoplatform.social.core.application.PeopleService;
import org.exoplatform.social.core.application.SpaceActivityPublisher;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.identity.provider.SpaceIdentityProvider;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.webui.Utils;
import org.exoplatform.social.webui.activity.UIDefaultActivity;
import org.exoplatform.social.webui.activity.news.UINewsActivity;
import org.exoplatform.social.webui.profile.UIUserActivitiesDisplay;
import org.exoplatform.social.webui.space.UISpaceActivitiesDisplay;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
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

    setReadyForPostingActivity(true);
  }

  @Override
  public boolean isEnabled() {
    return featureService.isActiveFeature(NEWS_FEATURE_NAME);
  }

  @Override
  protected ExoSocialActivity onPostActivity(UIComposer.PostContext postContext, String postedMessage) throws Exception {
    ExoSocialActivity activity = null;
    if (postedMessage.equals("")) {
      WebuiRequestContext requestContext = WebuiRequestContext.getCurrentInstance();
      UIApplication uiApplication = requestContext.getUIApplication();
      uiApplication.addMessage(new ApplicationMessage("UIComposer.msg.error.Empty_Message", null, ApplicationMessage.WARNING));
      return activity;
    }
    if (postContext == UIComposer.PostContext.SPACE) {
      UISpaceActivitiesDisplay uiDisplaySpaceActivities = (UISpaceActivitiesDisplay) getActivityDisplay();
      Space space = uiDisplaySpaceActivities.getSpace();

      Identity spaceIdentity = Utils.getIdentityManager().getOrCreateIdentity(SpaceIdentityProvider.NAME,
              space.getPrettyName(),
              false);
      activity = new ExoSocialActivityImpl(Utils.getViewerIdentity().getId(),
              SpaceActivityPublisher.SPACE_APP_ID,
              postedMessage,
              null);
      activity.setType(UINewsActivity.ACTIVITY_TYPE);
      Utils.getActivityManager().saveActivityNoReturn(spaceIdentity, activity);
    } else if (postContext == UIComposer.PostContext.USER) {
      UIUserActivitiesDisplay uiUserActivitiesDisplay = (UIUserActivitiesDisplay) getActivityDisplay();
      Identity ownerIdentity = Utils.getIdentityManager().getOrCreateIdentity(OrganizationIdentityProvider.NAME,
              uiUserActivitiesDisplay.getOwnerName(),
              false);
      activity = new ExoSocialActivityImpl(Utils.getViewerIdentity().getId(),
              PeopleService.PEOPLE_APP_ID,
              postedMessage,
              null);
      activity.setType(UINewsActivity.ACTIVITY_TYPE);
      //
      Utils.getActivityManager().saveActivityNoReturn(ownerIdentity, activity);

      if (uiUserActivitiesDisplay.getSelectedDisplayMode() == UIUserActivitiesDisplay.DisplayMode.MY_SPACE) {
        uiUserActivitiesDisplay.setSelectedDisplayMode(UIUserActivitiesDisplay.DisplayMode.ALL_ACTIVITIES);
      }
    }
    return activity;
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
    setReadyForPostingActivity(true);
  }
}
