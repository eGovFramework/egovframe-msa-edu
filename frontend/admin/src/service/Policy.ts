import { Page } from '@utils'
import axios, { AxiosError } from 'axios'
import useSWR from 'swr'
import { common, SearchPayload } from './common'

/**
 * 저장 시 데이터 타입
 */
export interface PolicySavePayload {
  type: string
  isUse: boolean
  title: string
  regDate: Date
  contents: string
}

/**
 * request payload
 */
export interface PolicyPayload {
  callback: () => any
  errorCallback: (error: AxiosError) => void
  id?: string
  data?: PolicySavePayload
  isUse?: boolean
}

const POLICY_API = '/portal-service/api/v1/policies'
const POLICY_TYPE_API = '/portal-service/api/v1/code-details/policy/codes'
/**
 * 이용약관 관리 서비스
 */
export const policyService = {
  url: POLICY_API,
  typeUrl: POLICY_TYPE_API,
  getTypeList: () => {
    return axios.get(POLICY_TYPE_API)
  },
  getOne: (id: string) => {
    return axios.get(`${POLICY_API}/${id}`)
  },
  search: ({ keywordType, keyword, size, page }: SearchPayload) => {
    return useSWR<Page, AxiosError>(
      [`${POLICY_API}?size=${size}&page=${page}`, keywordType, keyword],
      (url, keywordType, keyword) =>
        common.fetcher(url, { keywordType, keyword }),
      { revalidateOnFocus: false, errorRetryCount: 0 },
    )
  },
  delete: async ({ id, callback, errorCallback }: PolicyPayload) => {
    try {
      const result = await axios.delete(`${POLICY_API}/${id}`)
      if (result) {
        callback()
      }
    } catch (error) {
      errorCallback(error)
    }
  },
  save: async ({ callback, errorCallback, data }: PolicyPayload) => {
    try {
      const result = await axios.post(`${POLICY_API}`, data, {
        headers: common.headers,
      })
      if (result) {
        callback()
      }
    } catch (error) {
      errorCallback(error)
    }
  },
  update: async ({ callback, errorCallback, id, data }: PolicyPayload) => {
    try {
      const result = await axios.put(`${POLICY_API}/${id}`, data, {
        headers: common.headers,
      })
      if (result) {
        callback()
      }
    } catch (error) {
      errorCallback(error)
    }
  },
  updateUse: async ({ callback, errorCallback, id, isUse }: PolicyPayload) => {
    try {
      const result = await axios.put(`${POLICY_API}/${id}/${isUse}`, {
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
