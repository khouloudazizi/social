/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.exoplatform.social.core.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.services.organization.UserProfile;
import org.exoplatform.services.organization.UserProfileEventListener;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.storage.api.IdentityStorage;

public class SocialUserProfileEventListenerImpl extends UserProfileEventListener {

  
  @Override
  public void postSave(UserProfile userProfile, boolean isNew) throws Exception {
    RequestLifeCycle.begin(PortalContainer.getInstance());
    try{
      ExoContainer container = ExoContainerContext.getCurrentContainer();
      //
      IdentityManager idm = (IdentityManager) container.getComponentInstanceOfType(IdentityManager.class);
      Identity identity = idm.getOrCreateIdentity(OrganizationIdentityProvider.NAME, userProfile.getUserName(), true);
         
      //
      Profile profile = identity.getProfile();
      
      //
      String uGender = null;
      String uPosition = null;
      String uTelMobHomeNumber = null;
      String uTelHomeNumber = null;
      String uTelMobWorkNumber = null;
      String uTelWorkNumber = null;
      if (userProfile != null) {
        uGender = userProfile.getAttribute(UserProfile.PERSONAL_INFO_KEYS[4]);//"user.gender"
        uPosition = userProfile.getAttribute(UserProfile.PERSONAL_INFO_KEYS[7]);//user.jobtitle
        uTelMobHomeNumber = userProfile.getAttribute(UserProfile.HOME_INFO_KEYS[6]);// user.home-info.telecom.mobile.number
        uTelHomeNumber = userProfile.getAttribute(UserProfile.HOME_INFO_KEYS[7]);// user.home-info.telecom.telephone.number
        uTelMobWorkNumber = userProfile.getAttribute(UserProfile.BUSINESE_INFO_KEYS[5]);// user.business-info.telecom.mobile.number
        uTelWorkNumber = userProfile.getAttribute(UserProfile.BUSINESE_INFO_KEYS[6]);// user.business-info.telecom.telephone.numbers
      }
      
      //
      String pGender = (String) profile.getProperty(Profile.GENDER);
      String pPosition = (String) profile.getProperty(Profile.POSITION); 
      List<Map<String, String>> pContact_Phones = new ArrayList<>();
      //
      boolean hasUpdated = false;
  
      //
      if (uGender != null && !uGender.equals(pGender)) {
        profile.setProperty(Profile.GENDER, uGender);
        List<Profile.UpdateType> list = new ArrayList<Profile.UpdateType>();
        list.add(Profile.UpdateType.CONTACT);
        profile.setListUpdateTypes(list);
        hasUpdated = true;
      }
      
      if (uPosition != null && !uPosition.equals(pPosition)) {
        profile.setProperty(Profile.POSITION, uPosition);
        List<Profile.UpdateType> list = new ArrayList<Profile.UpdateType>();
        list.add(Profile.UpdateType.CONTACT);
        profile.setListUpdateTypes(list);
        hasUpdated = true;
      }
      
      if ((StringUtils.isNotBlank(uTelHomeNumber)) || (StringUtils.isNotBlank(uTelMobHomeNumber))
          || (StringUtils.isNotBlank(uTelWorkNumber)) || (StringUtils.isNotBlank(uTelMobWorkNumber))) {
        if (StringUtils.isNotBlank(uTelHomeNumber))
          profile.setProperty(Profile.CONTACT_PHONES, setContact(pContact_Phones, "Home", uTelHomeNumber));
        if (StringUtils.isNotBlank(uTelMobHomeNumber))
          profile.setProperty(Profile.CONTACT_PHONES, setContact(pContact_Phones, "Home", uTelMobHomeNumber));
        if (StringUtils.isNotBlank(uTelWorkNumber))
          profile.setProperty(Profile.CONTACT_PHONES, setContact(pContact_Phones, "Work", uTelWorkNumber));
        if (StringUtils.isNotBlank(uTelMobWorkNumber))
          profile.setProperty(Profile.CONTACT_PHONES, setContact(pContact_Phones, "Work", uTelMobWorkNumber));
        List<Profile.UpdateType> list = new ArrayList<Profile.UpdateType>();
        list.add(Profile.UpdateType.CONTACT);
        profile.setListUpdateTypes(list);
        hasUpdated = true;
      }
  
      if (hasUpdated && !isNew) {
        IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
        identityManager.updateProfile(profile);
      }
      
      if (isNew) {
        IdentityStorage identityStorage = CommonsUtils.getService(IdentityStorage.class);
        identityStorage.updateProfile(profile);
      }
      
    }finally{
      RequestLifeCycle.end();
    }
  }
  
  private List<Map<String, String>> setContact(List<Map<String, String>> contacts, String key, String value) {
    boolean keyFound = false;
    if (contacts != null) {
      for (Map<String, String> map : contacts) {
        if (!map.containsKey(key)) {
          map.put(key, value);
          keyFound = true;
        }
      }
    }
    if (keyFound == false) {
      Map<String, String> map = new HashMap<>();
      map.put(key, value);
      contacts.add(map);
    }
    return contacts;
  }
}