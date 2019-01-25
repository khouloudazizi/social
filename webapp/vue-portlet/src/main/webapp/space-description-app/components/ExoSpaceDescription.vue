<template>
  <div id="spaceDescriptionApp" class="uiBox">
    <h5 class="center">{{ $t("social.space.description.title") }}</h5>
    <div id="spaceDescription" class="uiContentBox">
      <p v-html="description"></p>
    </div>
    <div id="spaceManagersList">
      <h5>{{ $t("social.space.description.managers") }}</h5>
      <div v-for="manager in managers" :key="manager" class="spaceManagerEntry">
        <a>
          <img :src="manager.avatar" alt="avatar"/>
        </a>
        {{ manager.fullName }}
      </div>
    </div>
  </div>
</template>

<script>
  import * as spaceDescriptionServices from '../spaceDescriptionServices';

  export default {
    data() {
      return {
        description: '',
        managers: []
      };
    },
    created() {
      this.getDescription();
      this.getManagers();
    },
    methods: {
      getDescription() {
        spaceDescriptionServices.getSpaceDescriptionByPrettyName(eXo.env.portal.spaceName).then(data => {
          if(data) {
            this.description = data.description;
          }
        });
      },
      getManagers() {
        spaceDescriptionServices.getSpaceManagersByPrettyName(eXo.env.portal.spaceName).then(response => {
          if(response) {
            this.managers = response;
          }
        });
      }
    }
  };
</script>