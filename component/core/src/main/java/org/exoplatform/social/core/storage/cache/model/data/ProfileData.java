/*
 * Copyright (C) 2003-2011 eXo Platform SAS.
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

package org.exoplatform.social.core.storage.cache.model.data;

import org.apache.commons.lang.StringUtils;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Immutable profile data.
 *
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class ProfileData implements CacheData<Profile> {
  public static final ProfileData NULL_OBJECT = new ProfileData(null);

  private static final long serialVersionUID = 1308337180458451775L;

  private String profileId;

  private String identityId;

  private String providerId;

  private String remoteId;

  private boolean hasChanged;

  private Map<String, Object> data;

  private String url;

  private String avatarUrl;

  private String bannerUrl;
  
  private Long avatarLastUpdated;

  private Long bannerLastUpdated;
  
  private Long createdTime;

  public ProfileData(final Profile profile) {
    if(profile == null) {
      this.data = Collections.unmodifiableMap(new HashMap<>());
    } else {
      this.profileId = profile.getId();
      this.identityId = profile.getIdentity().getId();
      this.providerId = profile.getIdentity().getProviderId();
      this.remoteId = profile.getIdentity().getRemoteId();
      this.hasChanged = profile.hasChanged();
      this.data = Collections.unmodifiableMap(profile.getProperties());
      this.url = profile.getUrl();
      this.avatarUrl = profile.getAvatarUrl();
      this.bannerUrl = profile.getBannerUrl();
      this.avatarLastUpdated = profile.getAvatarLastUpdated();
      this.bannerLastUpdated = profile.getBannerLastUpdated();
      this.createdTime = profile.getCreatedTime();
    }
  }

  public String getProfileId() {
    return profileId;
  }

  public String getIdentityId() {
    return identityId;
  }

  public String getProviderId() {
    return providerId;
  }

  public String getRemoteId() {
    return remoteId;
  }

  public Map<String, Object> getData() {
    return data;
  }

  public Profile build() {
    Identity identity = new Identity(identityId);
    identity.setProviderId(providerId);
    identity.setRemoteId(remoteId);

    Profile profile = new Profile(identity);
    profile.setId(profileId);
    profile.setUrl(url);
    profile.setAvatarUrl(avatarUrl);
    profile.setBannerUrl(bannerUrl);
    profile.setAvatarLastUpdated(avatarLastUpdated);
    profile.setBannerLastUpdated(bannerLastUpdated);
    profile.setCreatedTime(createdTime);
    for(String key : data.keySet()) {
      profile.setProperty(key, data.get(key));
    }

    identity.setProfile(profile);
    if (!hasChanged) {
      profile.clearHasChanged();
    }
    return profile;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ProfileData)) return false;

    ProfileData that = (ProfileData) o;

    return StringUtils.equals(profileId, that.profileId) && StringUtils.equals(identityId, that.identityId)
            && StringUtils.equals(providerId, that.profileId) && StringUtils.equals(remoteId, that.remoteId);
  }

  @Override
  public int hashCode() {
    int result = profileId != null ? profileId.hashCode() : 0;
    result = 31 * result + (identityId != null ? identityId.hashCode() : 0);
    result = 31 * result + (providerId != null ? providerId.hashCode() : 0);
    result = 31 * result + (remoteId != null ? remoteId.hashCode() : 0);
    return result;
  }
}
