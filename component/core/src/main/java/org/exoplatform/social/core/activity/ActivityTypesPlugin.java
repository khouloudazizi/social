package org.exoplatform.social.core.activity;

import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * EXo activity types plugin centralize the list of all activity types
 */
public class ActivityTypesPlugin extends BaseComponentPlugin {
    private static final String ACTIVITY_TYPES_PARAM ="activity.types";
    private final String SEPARATOR =",";

    private String[]  activityTypes ;

    public ActivityTypesPlugin(InitParams initParams) {
        ValueParam valueParam = initParams.getValueParam(ACTIVITY_TYPES_PARAM);
        if(valueParam != null && !valueParam.getValue().isEmpty()){
            activityTypes =valueParam.getValue().split(SEPARATOR);
        }
    }

    public List<String> getActivityTypes() {
        return new ArrayList(Arrays.asList(activityTypes));
    }
}
