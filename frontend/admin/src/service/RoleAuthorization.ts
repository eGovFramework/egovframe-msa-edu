import { Page } from '@utils'
import axios, { AxiosError } from 'axios'
import useSWR from 'swr'
import { common, SearchPayload } from './common'

/**
 * 저장 시 데이터 타입
 */
export interface RoleAuthorizationSavePayload {
  roleId: string
  authorizationNo: number
}

/**
 * request payload
 */
export interface RoleAuthorizationPayload {
  callback: () => any
  errorCallback: (error: AxiosError) => void
  data?: RoleAuthorizationSavePayload[]
}

/**
 * 사용자 서비스 권한 인가 API URL
 */
const ROLE_AUTHORIZATION_URL = '/user-service/api/v1/role-authorizations'

/**
 * 이용약관 관리 서비스
 */
export const roleAuthorizationService = {
  search: (
    roleId: string,
    { keywordType, keyword, size, page }: SearchPayload,
  ) =>
    // eslint-disable-next-line react-hooks/rules-of-hooks
    useSWR<Page, AxiosError>(
      [
        `${ROLE_AUTHORIZATION_URL}?size=${size}&page=${page}`,
        roleId,
        keywordType,
        keyword,
      ],
      url => common.fetcher(url, { roleId, keywordType, keyword }),
      { revalidateOnFocus: false, errorRetryCount: 0 },
    ),
  save: async ({ callback, errorCallback, data }: RoleAuthorizationPayload) => {
    try {
      const result = await axios.post(ROLE_AUTHORIZATION_URL, data, {
        headers: common.headers,
      })
      if (result) {
        callback()
      }
    } catch (error) {
      errorCallback(error)
    }
  },
  delete: async ({
    callback,
    errorCallback,
    data,
  }: RoleAuthorizationPayload) => {
    try {
      const result = await axios.put(ROLE_AUTHORIZATION_URL, data, {
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
