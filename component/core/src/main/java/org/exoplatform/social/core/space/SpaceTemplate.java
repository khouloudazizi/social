/*
 * Copyright (C) 2003-2019 eXo Platform SAS.
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

package org.exoplatform.social.core.space;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Definition of space template model.
 */
public class SpaceTemplate implements Cloneable {
  private String name;
  private String visibility;
  private String registration;
  private String bannerPath;
  private SpaceApplication homePageApplication;
  private List<SpaceApplication> applications;

  /**
   * Sets the template name
   *
   * @param name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the name.
   *
   * @return
   */
  public String getName() {
    return name;
  }

  /**
   * Sets visibility.
   *
   * @param visibility
   */
  public void setVisibility(String visibility) {
    this.visibility = visibility;
  }

  /**
   * Gets visibility.
   *
   * @return
   */
  public String getVisibility() {
    return visibility;
  }

  /**
   * Sets registration.
   *
   * @param registration
   */
  public void setRegistration(String registration) {
    this.registration = registration;
  }

  /**
   * Gets registration.
   *
   * @return
   */
  public String getRegistration() {
    return registration;
  }

  /**
   * Sets space banner path..
   *
   * @param bannerPath
   */
  public void setBannerPath(String bannerPath) {
    this.bannerPath = bannerPath;
  }

  /**
   * Gets space banner path.
   *
   * @return
   */
  public String getBannerPath() {
    return bannerPath;
  }

  /**
   * Adds a space application to space application list.
   *
   * @param spaceApplication
   */
  public void addToSpaceApplicationList(SpaceApplication spaceApplication) {
    if (applications == null) {
      applications = new ArrayList<SpaceApplication>();
    }
    applications.removeIf(o -> (o.getPortletApp().equals(spaceApplication.getPortletApp())
        && o.getPortletName().equals(spaceApplication.getPortletName())));
    applications.add(spaceApplication);
  }

  /**
   * Sets space application list to be installed.
   *
   * @param applicationList
   */
  public void setSpaceApplicationList(List<SpaceApplication> applicationList) {
    applications = applicationList;
  }

  /**
   * Gets space application list to be installed.
   *
   * @return
   */
  public List<SpaceApplication> getSpaceApplicationList() {
    return applications == null ? null : applications.stream().sorted(Comparator.comparing(SpaceApplication::getOrder)).collect(Collectors.toList());
  }

  /**
   * Sets home space applications.
   *
   * @param homeApplication
   */
  public void setHomeApplication(SpaceApplication homeApplication) {
    homePageApplication = homeApplication;
  }

  /**
   * Gets home space application.
   *
   * @return
   */
  public SpaceApplication getSpaceHomeApplication() {
    return homePageApplication;
  }

  @Override
  public SpaceTemplate clone() {
    try {
      SpaceTemplate spaceTemplate = (SpaceTemplate) super.clone();
      spaceTemplate.homePageApplication = homePageApplication.clone();
      return spaceTemplate;
    } catch (CloneNotSupportedException e) {
      return null;
    }
  }
}
