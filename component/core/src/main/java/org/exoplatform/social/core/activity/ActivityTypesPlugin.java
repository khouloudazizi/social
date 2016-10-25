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
    private static final String ACTIVITY_CUSTOM_TYPES_PARAM ="custom.types";
    private final String SEPARATOR =",";
    private List<String> activityTypes ;

    public ActivityTypesPlugin(InitParams initParams) {
        activityTypes = new ArrayList<>();
        ValueParam valueParam = initParams.getValueParam(ACTIVITY_TYPES_PARAM);
        if(valueParam != null && !valueParam.getValue().isEmpty()){
           String[] types= valueParam.getValue().split(SEPARATOR);
            if(types.length > 0) {
                activityTypes.addAll(Arrays.asList(types));
            }
        }
        valueParam = initParams.getValueParam(ACTIVITY_CUSTOM_TYPES_PARAM);
        if(valueParam != null && !valueParam.getValue().isEmpty()){
            String[] types= valueParam.getValue().split(SEPARATOR);
            if(types.length > 0) {
                activityTypes.addAll(Arrays.asList(types));
            }
        }
    }

    public List<String> getActivityTypes() {
        return activityTypes;
    }
}
