package org.exoplatform.social.notification.plugin;

import org.exoplatform.commons.api.notification.NotificationContext;
import org.exoplatform.commons.api.notification.model.NotificationInfo;
import org.exoplatform.commons.api.notification.plugin.BaseNotificationPlugin;
import org.exoplatform.commons.notification.impl.NotificationContextImpl;
import org.exoplatform.commons.persistence.impl.EntityManagerService;
import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.manager.RelationshipManager;
import org.exoplatform.social.notification.Utils;

import java.util.ArrayList;
import java.util.List;

public class ActivityConnectionPlugin extends BaseNotificationPlugin {
    public static final String ID = "ActivityConnectionPlugin";

    public ActivityConnectionPlugin(InitParams initParams) {
        super(initParams);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public NotificationInfo makeNotification(NotificationContext ctx) {
        try {
            ExoSocialActivity activity = ctx.value(SocialNotificationUtils.ACTIVITY);
            IdentityManager identityManager = Utils.getIdentityManager();
            Identity posterIdentity = identityManager.getIdentity(activity.getPosterId(), false);
            return NotificationInfo.instance()
                    .to(getConnections(posterIdentity))
                    .with(SocialNotificationUtils.POSTER.getKey(), posterIdentity.getRemoteId())
                    .with(SocialNotificationUtils.ACTIVITY_ID.getKey(), activity.getId())
                    .key(getId()).end();

        } catch (Exception e) {
            ctx.setException(e);
        }

        return null;
    }

    @Override
    public boolean isValid(NotificationContext ctx) {
        if (!NotificationContextImpl.cloneInstance().getPluginSettingService().isActive(ActivityConnectionPlugin.ID)) {
            return false;
        }

        ExoSocialActivity activity = ctx.value(SocialNotificationUtils.ACTIVITY);
        if (activity.getStreamOwner().equals(Utils.getUserId(activity.getPosterId()))) {
            return true;
        }

        return false;
    }

    public List<String> getConnections(Identity posterIdentity) throws Exception {

        List<String> connections = new ArrayList<String>();
        RelationshipManager re = Utils.getRelationshipManager();
        EntityManagerService service = PortalContainer.getInstance().getComponentInstanceOfType(EntityManagerService.class);
        service.startRequest(PortalContainer.getInstance());
        ListAccess<Identity> listAccess = re.getConnections(posterIdentity);

        int offset = 0, limit = 45, size = listAccess.getSize();

        while(offset < size) {
            Identity[] identities = listAccess.load(offset, limit);
            if(identities.length == 0) { break; }
            for (int i = 0; i < identities.length; ++i) {
                connections.add(identities[i].getRemoteId());
            }
            offset += limit;
        }
        service.endRequest(PortalContainer.getInstance());

        return connections;
    }
}
