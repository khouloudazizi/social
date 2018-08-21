package org.exoplatform.social.core.jpa.search.listener;

import org.exoplatform.commons.search.index.IndexingService;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.MembershipEventListener;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.jpa.search.ProfileIndexingServiceConnector;
import org.exoplatform.social.core.manager.IdentityManager;

public class MembershipESListenerImpl extends MembershipEventListener {
  private static final Log    LOG                  = ExoLogger.getLogger(UserESListenerImpl.class);

  private static final String PLATFORM_USERS_GROUP = "/platform/users";

  @Override
  public void postSave(Membership membership, boolean isNew) throws Exception {
    String groupId = membership.getGroupId();
    if (groupId.equals(PLATFORM_USERS_GROUP)) {

    }
    reindexProfile(membership);
  }

  @Override
  public void postDelete(Membership membership) throws Exception {
    reindexProfile(membership);
  }

  private void reindexProfile(Membership membership) {
    RequestLifeCycle.begin(PortalContainer.getInstance());
    try {
      IdentityManager idm = CommonsUtils.getService(IdentityManager.class);
      Identity identity = idm.getOrCreateIdentity(OrganizationIdentityProvider.NAME, membership.getUserName(), false);

      LOG.info("Notifying indexing service for user memberships change id={}", identity.getId());

      CommonsUtils.getService(IndexingService.class).unindex(ProfileIndexingServiceConnector.TYPE, identity.getId());
      CommonsUtils.getService(IndexingService.class).index(ProfileIndexingServiceConnector.TYPE, identity.getId());
    } finally {
      RequestLifeCycle.end();
    }
  }
}
