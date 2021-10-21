import { MODE } from '@constants/env'
import useSWR from 'swr'
import { common } from './common'

const siteId = MODE === 'sm' ? '3' : '2'

const MENU_API = `/portal-service/api/v1/menu-roles/${siteId}`
const CODE_API = `/portal-service/api/v1/code-details/skin_type_code/codes`

export const menuService = {
  getMenus: () => {
    return useSWR(MENU_API, common.fetcher, {
      revalidateOnFocus: false,
      revalidateOnReconnect: false,
    })
  },
}
