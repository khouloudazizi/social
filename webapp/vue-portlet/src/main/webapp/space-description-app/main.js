import './components/initComponents.js';
import { spacesConstants } from '../spaces-administration-app/spacesAdministrationConstants.js';
import * as spacesAdministrationDirectives from '../spaces-administration-app/spacesAdministrationDirectives.js';

// getting language of the PLF 
const lang = typeof eXo !== 'undefined' ? eXo.env.portal.language : 'en';

// should expose the locale ressources as REST API 
const url = `${spacesConstants.PORTAL}/${spacesConstants.PORTAL_REST}/i18n/bundle/locale.portlet.social.SpaceDescriptionPortlet-${lang}.json`;

Vue.directive('exo-tooltip', spacesAdministrationDirectives.tooltip);

// get overrided components if exists
if (extensionRegistry) {
  const components = extensionRegistry.loadComponents('SpaceDescription');
  if (components && components.length > 0) {
    components.forEach(cmp => {
      Vue.component(cmp.componentName, cmp.componentOptions);
    });
  }
}

// getting locale ressources
export function init() {
  exoi18n.loadLanguageAsync(lang, url).then(i18n => {
  // init Vue app when locale ressources are ready
    new Vue({
      el: '#spaceDescription',
      template: '<exo-space-description></exo-space-description>',
      i18n
    });
  });
}