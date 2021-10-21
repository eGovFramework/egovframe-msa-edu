import { common } from '@service'
import axios, { AxiosError } from 'axios'
import useSWR from 'swr'

const MENU_ROLE_API = '/portal-service/api/v1/menu-roles'

export interface IMenuRole {
  children?: IMenuRole[]
  icon?: string
  isChecked: boolean
  level?: number
  engName: string
  id: number
  korName: string
  menuRoleId?: number
  parentId?: number
  roleId?: string
  sortSeq?: number
}

export const menuRoleService = {
  search: (roleId: string, siteId: number) => {
    return useSWR<IMenuRole[], AxiosError>(
      `${MENU_ROLE_API}/${roleId.toLowerCase()}/${siteId}`,
      url => common.fetcher(url, {}),
      { revalidateOnFocus: false, errorRetryCount: 0 },
    )
  },
  save: (data: IMenuRole[]) => {
    return axios.post(`${MENU_ROLE_API}`, data, {
      headers: common.headers,
    })
  },
}
