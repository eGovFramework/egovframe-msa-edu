import { common } from '@service'
import axios, { AxiosError } from 'axios'
import useSWR from 'swr'

const MENU_API = '/portal-service/api/v1/menus'

export interface IMenuSavePayload {
  name: string
  parentId?: number
  siteId?: number
  sortSeq: number
  level: number
  isUse: boolean
  isShow: boolean
}

export interface IMenuTree extends IMenuSavePayload {
  menuId: number
  icon: string
  index?: number
  children: IMenuTree[]
}

export interface IMenuInfoForm {
  menuId: number
  menuKorName: string
  menuEngName: string
  menuType: string
  menuTypeName?: string
  urlPath?: string
  connectId?: number
  connectName?: string
  subName?: string
  icon?: string
  description?: string
  isUse: boolean
  isShow: boolean
  isBlank: boolean
}

export interface ISite {
  id: number
  name: string
  isUse: boolean
}

export const menuService = {
  getMenus: () => {
    return new Promise<IMenuTree[]>(async (resolve, rejects) => {
      try {
        const result = await axios.get('/portal-service/api/v1/1/menus')
        if (result) {
          resolve(result.data)
        } else {
          resolve([])
        }
      } catch (error) {
        rejects(error)
      }
    })
  },
  getSites: () => {
    return new Promise<ISite[]>(async (resolve, rejects) => {
      try {
        const result = await axios.get(`/portal-service/api/v1/sites`)
        resolve(result?.data)
      } catch (error) {
        rejects(error)
      }
    })
  },
  getTreeMenus: (siteId: number) => {
    return useSWR<IMenuTree[], AxiosError>(
      `${MENU_API}/${siteId}/tree`,
      url => common.fetcher(url, {}),
      { revalidateOnFocus: false, errorRetryCount: 0 },
    )
  },
  getMenu: (menuId: number) => {
    return new Promise<IMenuInfoForm>(async (resolve, rejects) => {
      try {
        const result = await axios.get(`${MENU_API}/${menuId}`)
        resolve(result?.data)
      } catch (error) {
        rejects(error)
      }
    })
  },
  save: (data: IMenuSavePayload) => {
    return new Promise<IMenuTree>(async (resolve, rejects) => {
      try {
        const result = await axios.post(MENU_API, data, {
          headers: common.headers,
        })
        resolve(result?.data)
      } catch (error) {
        rejects(error)
      }
    })
  },
  updateName: (menuId: number, name: string) => {
    return new Promise<IMenuTree>(async (resolve, rejects) => {
      try {
        const result = await axios.put(`${MENU_API}/${menuId}/${name}`)
        resolve(result?.data)
      } catch (error) {
        rejects(error)
      }
    })
  },
  updateDnD: (siteId: number, data: IMenuTree[]) => {
    return new Promise<IMenuTree[]>(async (resolve, rejects) => {
      try {
        const result = await axios.put(`${MENU_API}/${siteId}/tree`, data, {
          headers: common.headers,
        })
        resolve(result?.data)
      } catch (error) {
        rejects(error)
      }
    })
  },
  delete: (menuId: number) => {
    return new Promise(async (resolve, rejects) => {
      try {
        const result = await axios.delete(`${MENU_API}/${menuId}`)
        resolve(result?.data)
      } catch (error) {
        rejects(error)
      }
    })
  },
  update: (menuId: number, data: IMenuInfoForm) => {
    return new Promise<IMenuInfoForm>(async (resolve, rejects) => {
      try {
        const result = await axios.put(`${MENU_API}/${menuId}`, data, {
          headers: common.headers,
        })
        resolve(result?.data)
      } catch (error) {
        rejects(error)
      }
    })
  },
}
