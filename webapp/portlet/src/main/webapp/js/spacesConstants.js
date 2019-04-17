export const spacesConstants = {
  PORTAL: eXo.env.portal.context || '',
  PORTAL_NAME: eXo.env.portal.portalName || '',
  PORTAL_REST: eXo.env.portal.rest,
  SOCIAL_SPACE_API: `${eXo.env.portal.context}/${eXo.env.portal.rest}/v1/social/spaces/`,
  SPACE_ID: eXo.env.portal.spaceId,
  SOCIAL_USER_API: `${eXo.env.portal.context}/${eXo.env.portal.rest}/v1/social/users`,
  HOST_NAME: window.location.host,
  MANAGERS_ROLE: '/users?role=manager'
};