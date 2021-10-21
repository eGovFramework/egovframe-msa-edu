import { Page } from '@utils'
import axios, { AxiosError } from 'axios'
import useSWR from 'swr'
import { common, SearchPayload } from './common'

export interface IUser {
  email: string
  lastLoginDate: Date
  loginFailCount: number
  roleId: string
  roleName: string
  userId: string
  userName: string
  userStateCode: string
  userStateCodeName: string
}

/**
 * 저장 시 데이터 타입
 */
export interface UserSavePayload {
  email: string
  password: string
  userName: string
  roleId: string
  userStateCode: string
}

/**
 * request payload너
 */
export interface UserPayload {
  callback: () => any
  errorCallback: (error: AxiosError) => void
  userId?: string
  data?: UserSavePayload
  useAt?: boolean
}

/**
 * 포털 서비스 배너 API URL
 */
const USER_URL = '/user-service/api/v1/users'

/**
 * 배너 관리 서비스
 */
export const userService = {
  search: ({ keywordType, keyword, size, page }: SearchPayload) =>
    // eslint-disable-next-line react-hooks/rules-of-hooks
    useSWR<Page, AxiosError>(
      [`${USER_URL}?size=${size}&page=${page}`, keywordType, keyword],
      url => common.fetcher(url, { keywordType, keyword }),
      { revalidateOnFocus: false, errorRetryCount: 0 },
    ),
  get: async (userId: string) => axios.get(`${USER_URL}/${userId}`),
  delete: async ({ userId, callback, errorCallback }: UserPayload) => {
    try {
      const result = await axios.delete(`${USER_URL}/delete/${userId}`)
      if (result) {
        callback()
      }
    } catch (error) {
      errorCallback(error)
    }
  },
  save: async ({ callback, errorCallback, data }: UserPayload) => {
    try {
      const result = await axios.post(USER_URL, data, {
        headers: common.headers,
      })
      if (result) {
        callback()
      }
    } catch (error) {
      errorCallback(error)
    }
  },
  update: async ({ callback, errorCallback, userId, data }: UserPayload) => {
    try {
      const result = await axios.put(`${USER_URL}/${userId}`, data, {
        headers: common.headers,
      })
      if (result) {
        callback()
      }
    } catch (error) {
      errorCallback(error)
    }
  },
}
