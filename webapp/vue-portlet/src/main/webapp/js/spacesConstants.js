export const spacesConstants = {
  ENV: eXo.env.portal || '',
  PORTAL: eXo.env.portal.context || '',
  PORTAL_NAME: eXo.env.portal.portalName || '',
  PORTAL_REST: eXo.env.portal.rest,
  PROFILE_SPACE_LINK: '/g/:spaces:',
  SOCIAL_USER_API: `${eXo.env.portal.context}/${eXo.env.portal.rest}/v1/social/users/`,
  SOCIAL_SPACE_API: `${eXo.env.portal.context}/${eXo.env.portal.rest}/v1/social/spaces`,
  SPACES_ADMINISTRATION_API: `${eXo.env.portal.context}/${eXo.env.portal.rest}/v1/social/spacesAdministration`,
  USER_API: `${eXo.env.portal.context}/${eXo.env.portal.rest}/v1/users`,
  GROUP_API: `${eXo.env.portal.context}/${eXo.env.portal.rest}/v1/groups`,
  SPACES_PER_PAGE: 30,
  DEFAULT_SPACE_AVATAR: '/eXoSkin/skin/images/system/SpaceAvtDefault.png',
};