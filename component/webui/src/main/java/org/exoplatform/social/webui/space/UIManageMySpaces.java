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
package org.exoplatform.social.webui.space;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.portal.mop.user.UserNavigation;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.space.SpaceException;
import org.exoplatform.social.core.space.SpaceFilter;
import org.exoplatform.social.core.space.SpaceUtils;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.social.webui.Utils;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

@ComponentConfig(
  template="war:/groovy/social/webui/space/UIManageMySpaces.gtmpl",
  events = {
    @EventConfig(listeners = UIManageMySpaces.DeleteSpaceActionListener.class),
    @EventConfig(listeners = UIManageMySpaces.LeaveSpaceActionListener.class),
    @EventConfig(listeners = UIManageMySpaces.SearchActionListener.class),
    @EventConfig(listeners = UIManageMySpaces.LoadMoreSpaceActionListener.class)
  }
)
public class UIManageMySpaces extends UIContainer {
  private static final Log LOG = ExoLogger.getLogger(UIManageMySpaces.class);
  private static final String MSG_WARNING_LEAVE_SPACE = "UIManageMySpaces.msg.warning_leave_space";
  private static final Integer LEADER = 1, MEMBER = 2;
  private static final String SPACE_DELETED_INFO = "UIManageMySpaces.msg.DeletedInfo";
  private static final String MEMBERSHIP_REMOVED_INFO = "UIManageMySpaces.msg.MemberShipRemovedInfo";
  private static final String CONFIRMED_STATUS = "confirmed";
  
  /**
   * SEARCH ALL.
   */
  private static final String SEARCH_ALL = "All";
  private static final String SPACE_SEARCH = "SpaceSearch";

  private final Integer SPACES_PER_PAGE = 20; 
  private SpaceService spaceService = null;
  private String userId = null;
  private UISpaceSearch uiSpaceSearch = null;

  private boolean enableLoadNext;
  private int loadingCapacity;
  private String spaceNameSearch;
  private List<Space> mySpacesList;
  private ListAccess<Space> mySpacesListAccess;
  private int mySpacesNum;
  private String selectedChar = null;

  private boolean loadMore = false;
  
  
  /**
   * Constructor for initialize UIPopupWindow for adding new space popup.
   *
   * @throws Exception
   */
  public UIManageMySpaces() throws Exception {
    uiSpaceSearch = createUIComponent(UISpaceSearch.class, null, "UISpaceSearch");
    uiSpaceSearch.setTypeOfRelation(CONFIRMED_STATUS);
    addChild(uiSpaceSearch);
    init();
  }

  /**
   * Inits at the first loading.
   * @since 1.2.2
   */
  public void init() {
    try {
      enableLoadNext = true;
      loadingCapacity = SPACES_PER_PAGE;
      this.uiSpaceSearch.setSpaceNameSearch(null);
      this.uiSpaceSearch.getUIStringInput(SPACE_SEARCH).setValue("");
      if (this.selectedChar != null){
        setSelectedChar(this.selectedChar);
      } else {
        setSelectedChar(SEARCH_ALL);
      }
      mySpacesList = loadMySpaces(0, loadingCapacity);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }
  
  /**
   * Sets loading capacity.
   * 
   * @param loadingCapacity
   */
  public void setLoadingCapacity(int loadingCapacity) {
    this.loadingCapacity = loadingCapacity;
  }

  /**
   * Gets flag to display LoadNext button or not.
   * 
   * @return the enableLoadNext
   */
  public boolean isEnableLoadNext() {
    return enableLoadNext;
  }

  /**
   * Sets flag to display LoadNext button or not.
   * 
   * @param unableLoadNext
   */
  public void setEnableLoadNext(boolean unableLoadNext) {
    this.enableLoadNext = unableLoadNext;
  }

  /**
   * Gets list of my space.
   * 
   * @return the mySpacesList
   * @throws Exception
   * @since 1.2.2 
   */
  public List<Space> getMySpacesList() throws Exception {
    // reset spaces list, except when loading more spaces (button Show More)
    if (!loadMore) {
      setMySpacesList(loadMySpaces(0, SPACES_PER_PAGE));
    } else {
      loadMore = false;
    }

    return this.mySpacesList;
  }

  /**
   * Sets list of my space.
   * 
   * @param mySpacesList the mySpacesList to set
   */
  public void setMySpacesList(List<Space> mySpacesList) {
    this.mySpacesList = mySpacesList;
  }
  
  /**
   * Gets number of my space.
   * 
   * @return the mySpacesNum
   */
  public int getMySpacesNum() {
    return mySpacesNum;
  }

  /**
   * Sets number of my space.
   * 
   * @param mySpacesNum the mySpacesNum to set
   */
  public void setMySpacesNum(int mySpacesNum) {
    this.mySpacesNum = mySpacesNum;
  }

  /**
   * Gets selected character.
   *
   * @return Character is selected.
   */
  public String getSelectedChar() {
    return selectedChar;
  }

  /**
   * Sets selected character.
   *
   * @param selectedChar A {@code String}
   */
  public void setSelectedChar(String selectedChar) {
    this.selectedChar = selectedChar;
  }
  
  /**
   * Gets name of searched space.
   * 
   * @return the spaceNameSearch
   */
  public String getSpaceNameSearch() {
    return spaceNameSearch;
  }

  /**
   * Sets name of searched space.
   * 
   * @param spaceNameSearch the spaceNameSearch to set
   */
  public void setSpaceNameSearch(String spaceNameSearch) {
    this.spaceNameSearch = spaceNameSearch;
  }
  
  /**
   * Gets spaces with ListAccess type.
   * 
   * @return the mySpacesListAccess
   */
  public ListAccess<Space> getMySpacesListAccess() {
    return mySpacesListAccess;
  }

  /**
   * Sets spaces with ListAccess type.
   * 
   * @param mySpacesListAccess the mySpacesListAccess to set
   */
  public void setMySpacesListAccess(ListAccess<Space> mySpacesListAccess) {
    this.mySpacesListAccess = mySpacesListAccess;
  }

  /**
   * Loads more space.
   * @throws Exception
   * @since 1.2.2
   */
  public void loadNext() throws Exception {
    int _currentIndex = this.mySpacesList.size();
    if (_currentIndex <= getMySpacesNum()) {
      List<Space> loaded = new ArrayList<Space>(Arrays.asList(getMySpacesListAccess().load(_currentIndex, loadingCapacity)));
      this.mySpacesList.addAll(loaded);
      setEnableLoadNext(loaded.size() < SPACES_PER_PAGE ? false : this.mySpacesList.size() < getMySpacesNum());
    }
  }
  
  /**
   * Loads space when searching.
   * @throws Exception
   * @since 1.2.2
   */
  public void loadSearch() throws Exception {
    setMySpacesList(loadMySpaces(0, loadingCapacity));
  }
  
  private List<Space> loadMySpaces(int index, int length) throws Exception {
    String charSearch = getSelectedChar();
    String searchCondition = uiSpaceSearch.getSpaceNameSearch();
    
    if (SEARCH_ALL.equals(charSearch) || (charSearch == null && searchCondition == null)) {
      setMySpacesListAccess(getSpaceService().getMemberSpaces(getUserId()));
    } else if (searchCondition != null) {
      setMySpacesListAccess(getSpaceService().getMemberSpacesByFilter(getUserId(), new SpaceFilter(searchCondition)));
    } else if(charSearch != null) {
      setMySpacesListAccess(getSpaceService().getMemberSpacesByFilter(getUserId(), new SpaceFilter(charSearch.charAt(0))));
    }
    
    setMySpacesNum(getMySpacesListAccess().getSize());
    uiSpaceSearch.setSpaceNum(getMySpacesNum());
    Space[] spaces = getMySpacesListAccess().load(index, length);
    setEnableLoadNext(spaces.length < SPACES_PER_PAGE ? false : getMySpacesNum() > SPACES_PER_PAGE);
    
    return new ArrayList<Space>(Arrays.asList(spaces));
  }
  
  /**
   * Listeners loading more space action.
   * @author <a href="mailto:hanhvq@exoplatform.com">Hanh Vi Quoc</a>
   * @since Aug 18, 2011
   * @since 1.2.2
   */
  static public class LoadMoreSpaceActionListener extends EventListener<UIManageMySpaces> {
    public void execute(Event<UIManageMySpaces> event) throws Exception {
      UIManageMySpaces uiManageMySpaces = event.getSource();
      uiManageMySpaces.loadNext();
      uiManageMySpaces.loadMore = true;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiManageMySpaces);
    }
  }
  
  /**
   * Listens event that broadcast from UISpaceSearch.
   * 
   * @author <a href="mailto:hanhvq@exoplatform.com">Hanh Vi Quoc</a>
   * @since Aug 19, 2011
   */
  static public class SearchActionListener extends EventListener<UIManageMySpaces> {
    @Override
    public void execute(Event<UIManageMySpaces> event) throws Exception {
      UIManageMySpaces uiManageMySpaces = event.getSource();
      WebuiRequestContext ctx = event.getRequestContext();
      String charSearch = ctx.getRequestParameter(OBJECTID);
      
      if (charSearch == null) {
        uiManageMySpaces.setSelectedChar(null);
      } else {
        uiManageMySpaces.uiSpaceSearch.getUIStringInput(SPACE_SEARCH).setValue("");
        uiManageMySpaces.uiSpaceSearch.setSpaceNameSearch(null);
        uiManageMySpaces.setSelectedChar(charSearch);
      }
      
      uiManageMySpaces.loadSearch();
      event.getRequestContext().addUIComponentToUpdateByAjax(uiManageMySpaces);
    }
  }
  
  /**
   * This action trigger when user click on back button from UINavigationManagement.
   *
   * @author hoatle
   */
  static public class BackActionListener extends EventListener<UIPageNodeForm> {

    @Override
    public void execute(Event<UIPageNodeForm> event) throws Exception {
      UIPageNodeForm uiPageNode = event.getSource();
      UserNavigation contextNavigation = uiPageNode.getContextPageNavigation();
      UIManageMySpaces uiMySpaces = uiPageNode.getAncestorOfType(UIManageMySpaces.class);
      UIPopupWindow uiPopup = uiMySpaces.getChild(UIPopupWindow.class);
      UISpaceNavigationManagement navigationManager = uiMySpaces.createUIComponent(UISpaceNavigationManagement.class, null, null);
      navigationManager.setOwner(contextNavigation.getKey().getName());
      navigationManager.setOwnerType(contextNavigation.getKey().getTypeName());
      UISpaceNavigationNodeSelector selector = navigationManager.getChild(UISpaceNavigationNodeSelector.class);
      selector.setEdittedNavigation(contextNavigation);
      selector.initTreeData();
      uiPopup.setUIComponent(navigationManager);
      uiPopup.setWindowSize(400, 400);
      uiPopup.setRendered(true);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMySpaces);
    }

  }

  /**
   * This action is triggered when user click on DeleteSpace a prompt popup is display for
   * confirmation, if yes delete that space; otherwise, do nothing.
   */
  static public class DeleteSpaceActionListener extends EventListener<UIManageMySpaces> {

    @Override
    public void execute(Event<UIManageMySpaces> event) throws Exception {
      UIManageMySpaces uiMySpaces = event.getSource();
      SpaceService spaceService = uiMySpaces.getSpaceService();
      WebuiRequestContext ctx = event.getRequestContext();
      UIApplication uiApp = ctx.getUIApplication();
      String spaceId = ctx.getRequestParameter(OBJECTID);
      Space space = spaceService.getSpaceById(spaceId);
      String userId = uiMySpaces.getUserId();
      
      if (space == null) {
        uiApp.addMessage(new ApplicationMessage(SPACE_DELETED_INFO, null, ApplicationMessage.INFO));
        return;
      }

      if (!spaceService.isMember(space, userId) && !spaceService.hasSettingPermission(space, userId)) {
        uiApp.addMessage(new ApplicationMessage(MEMBERSHIP_REMOVED_INFO, null, ApplicationMessage.INFO));
        return;
      }

      spaceService.deleteSpace(space);
      uiMySpaces.setMySpacesList(null);
      SpaceUtils.updateWorkingWorkSpace();
    }

  }

  /**
   * This action is triggered when user click on LeaveSpace <br> The leaving space will remove
   * that user in the space. <br> If that user is the only leader, then he cannot leave that space
   * <br>
   */
  static public class LeaveSpaceActionListener extends EventListener<UIManageMySpaces> {
    public void execute(Event<UIManageMySpaces> event) throws Exception {
      UIManageMySpaces uiMySpaces = event.getSource();
      SpaceService spaceService = uiMySpaces.getSpaceService();
      WebuiRequestContext ctx = event.getRequestContext();
      UIApplication uiApp = ctx.getUIApplication();
      String spaceId = ctx.getRequestParameter(OBJECTID);
      String userId = uiMySpaces.getUserId();
      Space space = spaceService.getSpaceById(spaceId);
      
      if (space == null) {
        uiApp.addMessage(new ApplicationMessage(SPACE_DELETED_INFO, null, ApplicationMessage.INFO));
        return;
      }

      if (!spaceService.isMember(space, userId) && !spaceService.hasSettingPermission(space, userId)) {
        uiApp.addMessage(new ApplicationMessage(MEMBERSHIP_REMOVED_INFO, null, ApplicationMessage.INFO));
        return;
      }

      if (spaceService.isOnlyManager(space, userId)) {
        uiApp.addMessage(new ApplicationMessage(MSG_WARNING_LEAVE_SPACE, null, ApplicationMessage.WARNING));
        return;
      }

      spaceService.removeMember(space, userId);
      spaceService.setManager(space, userId, false);
      uiMySpaces.setMySpacesList(null);
      SpaceUtils.updateWorkingWorkSpace();
    }
  }
  
  /**
   * Gets role of the user in a specific space for displaying in template.
   *
   * @param spaceId
   * @return UIManageMySpaces.LEADER if the remote user is the space's leader <br>
   *         UIManageMySpaces.MEMBER if the remote user is the space's member
   * @throws SpaceException
   */
  public int getRole(String spaceId) throws SpaceException {
    SpaceService spaceService = getSpaceService();
    Space space = spaceService.getSpaceById(spaceId);
    String userId = getUserId();
    if ((space != null) && spaceService.hasSettingPermission(space, userId)) {
      return LEADER;
    }
    return MEMBER;
  }

  /**
   * Checks in case root has membership with current space.
   *
   * @param spaceId
   * @return true or false
   * @throws SpaceException
   */
  public boolean hasMembership(String spaceId) throws SpaceException {
    SpaceService spaceService = getSpaceService();
    String userId = getUserId();
    Space space = spaceService.getSpaceById(spaceId);
    if (space == null) {
      return false;
    }
    return spaceService.isMember(space, userId);
  }

  /**
   * Gets image source url.
   *
   * @param space
   * @return image source url
   * @throws Exception
   */
  public String getImageSource(Space space) throws Exception {
    return space.getAvatarUrl();
  }

  /**
   * Gets spaceService.
   *
   * @return spaceService
   * @see SpaceService
   */
  private SpaceService getSpaceService() {
    if (spaceService == null) {
      spaceService = getApplicationComponent(SpaceService.class);
    }
    return spaceService;
  }

  /**
   * Gets remote user Id.
   *
   * @return remote userId
   */
  private String getUserId() {
    if (userId == null) {
      userId = Util.getPortalRequestContext().getRemoteUser();
    }
    return userId;
  }
}

