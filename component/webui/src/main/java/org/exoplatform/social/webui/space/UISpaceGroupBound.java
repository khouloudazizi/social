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
package org.exoplatform.social.webui.space;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.form.UIFormStringInput;

@ComponentConfig(
    template = "war:/groovy/social/webui/space/UISpaceGroupBound.gtmpl"
)

public class UISpaceGroupBound extends UIContainer {
  private final String USERS_SPACES = "users-spaces";

  /**
   * constructor
   * @throws Exception
   */
  public UISpaceGroupBound() throws Exception {
    UIFormStringInput uiFormStringInput = new UIFormStringInput(USERS_SPACES, null, null);
    addChild(uiFormStringInput);
  }

  /**
   * gets selected group from group bound
   * @return selected group
   */
  @SuppressWarnings("unchecked")
  public String getSelectedUsersAndSpaces() {
    UIFormStringInput uiFormStringInput = getChild(UIFormStringInput.class);
    return uiFormStringInput.getValue();
  }
}
