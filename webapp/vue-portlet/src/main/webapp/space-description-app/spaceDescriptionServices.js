import { spacesConstants } from '../spaces-administration-app/spacesAdministrationConstants.js';

export function getSpaceDescriptionByPrettyName(id){
  return fetch(`${spacesConstants.SOCIAL_SPACE_API}/${id}/description`, {credentials: 'include'}).then(resp => resp.json());
}

export function getSpaceManagersByPrettyName(id){
  return fetch(`${spacesConstants.SOCIAL_SPACE_API}/${id}/managers`, {credentials: 'include'}).then(resp => resp.json()).catch(e => {console.log(e)});
}

