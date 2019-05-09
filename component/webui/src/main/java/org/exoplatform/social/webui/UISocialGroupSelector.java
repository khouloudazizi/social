/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */

package org.exoplatform.social.webui;

import java.util.Collection;
import java.util.Set;

import org.exoplatform.portal.config.UserACL;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.MembershipEntry;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIBreadcumbs;
import org.exoplatform.webui.core.UITree;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.organization.account.UIGroupSelector;

@ComponentConfigs({
    @ComponentConfig(template = "war:/groovy/social/webui/space/UISocialGroupSelector.gtmpl", events = {
        @EventConfig(phase = Phase.DECODE, listeners = UIGroupSelector.ChangeNodeActionListener.class),
        @EventConfig(phase = Phase.DECODE, listeners = UIGroupSelector.SelectGroupActionListener.class),
        @EventConfig(phase = Phase.DECODE, listeners = UIGroupSelector.SelectPathActionListener.class) }),
    @ComponentConfig(type = UITree.class, id = "UITreeGroupSelector", template = "system:/groovy/webui/core/UITree.gtmpl", events = @EventConfig(phase = Phase.DECODE, listeners = UITree.ChangeNodeActionListener.class)),
    @ComponentConfig(type = UIBreadcumbs.class, id = "BreadcumbGroupSelector", template = "system:/groovy/webui/core/UIBreadcumbs.gtmpl", events = @EventConfig(phase = Phase.DECODE, listeners = UIBreadcumbs.SelectPathActionListener.class)) })
public class UISocialGroupSelector extends UIGroupSelector {

  private static final Log LOG = ExoLogger.getLogger(UISocialGroupSelector.class);

  public static final String MANAGER = "manager";
  public static final String ANY = "*";
  
  public UISocialGroupSelector() throws Exception {
    super();
  }

  @Override
  protected boolean canUserSeeGroup(Group group) {
    UserACL userACL = getApplicationComponent(UserACL.class);
    Identity userIdentity = ConversationState.getCurrent().getIdentity();
    Collection<MembershipEntry> userMemberships = userIdentity.getMemberships();
    return userACL.getSuperUser().equals(userIdentity.getUserId())
            || isManagerOfGroup(userIdentity.getUserId(), group.getId())
            || userMemberships.stream().anyMatch(userMembership -> userMembership.getGroup().startsWith(group.getId() + "/")
                && (userMembership.getMembershipType().equals(ANY) || userMembership.getMembershipType().equals(MANAGER)));
  }

  private boolean isManagerOfGroup(String remoteUser, String groupId) {
    OrganizationService organizationService = getApplicationComponent(OrganizationService.class);

    Membership membership = null;
    try {
      membership = organizationService.getMembershipHandler().findMembershipByUserGroupAndType(remoteUser, groupId, ANY);

      if (membership == null) {
        membership = organizationService.getMembershipHandler().findMembershipByUserGroupAndType(remoteUser, groupId, MANAGER);
      }
    } catch (Exception e) {
      LOG.error("Error while getting memberships of user " + remoteUser + " for group " + groupId, e);
    }
    
    return membership != null;
  }
}
