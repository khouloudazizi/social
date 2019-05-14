package org.exoplatform.social.webui.activity;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;

@ComponentConfig(
        lifecycle = UIFormLifecycle.class,
        template = "war:/groovy/social/webui/activity/UINewsActivity.gtmpl"
)
public class UINewsActivity extends BaseUIActivity {
}
