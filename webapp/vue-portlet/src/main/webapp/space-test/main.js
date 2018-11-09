import Vue from 'vue';
import app from './components/app.vue';

// getting language of the PLF 
const lang = typeof eXo !== 'undefined' ? eXo.env.portal.language : 'en';
  
// should expose the locale ressources as REST API 
const url = '/social-vue-portlet/space-test/locale_en.json';

// getting locale ressources
exoi18n.loadLanguageAsync(lang, url).then(i18n => {

// init Vue app when locale ressources are ready
  new Vue({
    el: '#spaceTest',
    render: h => h(app),
    i18n
  });
});