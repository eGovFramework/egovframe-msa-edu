import { Page } from '@utils'
import axios, { AxiosError } from 'axios'
import useSWR from 'swr'
import { common, SearchPayload } from './common'

/**
 * 저장 시 데이터 타입
 */
export interface AuthorizationSavePayload {
  authorizationName: string
  urlPatternValue: string
  httpMethodCode: string
  sortSeq: number
}

/**
 * request payload
 */
export interface AuthorizationPayload {
  callback: () => any
  errorCallback: (error: AxiosError) => void
  authorizationNo?: string
  data?: AuthorizationSavePayload
}

/**
 * 사용자 서비스 인가 API URL
 */
const AUTHORIZATION_URL = '/user-service/api/v1/authorizations'

/**
 * 이용약관 관리 서비스
 */
export const authorizationService = {
  search: ({ keywordType, keyword, size, page }: SearchPayload) =>
    // eslint-disable-next-line react-hooks/rules-of-hooks
    useSWR<Page, AxiosError>(
      [`${AUTHORIZATION_URL}?size=${size}&page=${page}`, keywordType, keyword],
      url => common.fetcher(url, { keywordType, keyword }),
      { revalidateOnFocus: false, errorRetryCount: 0 },
    ),
  get: async (authorizationNo: string) =>
    axios.get(`${AUTHORIZATION_URL}/${authorizationNo}`),
  getNextSortSeq: async () => axios.get(`${AUTHORIZATION_URL}/sort-seq/next`),
  delete: async ({
    authorizationNo,
    callback,
    errorCallback,
  }: AuthorizationPayload) => {
    try {
      const result = await axios.delete(
        `${AUTHORIZATION_URL}/${authorizationNo}`,
      )
      if (result) {
        callback()
      }
    } catch (error) {
      errorCallback(error)
    }
  },
  save: async ({ callback, errorCallback, data }: AuthorizationPayload) => {
    try {
      const result = await axios.post(AUTHORIZATION_URL, data, {
        headers: common.headers,
      })
      if (result) {
        callback()
      }
    } catch (error) {
      errorCallback(error)
    }
  },
  update: async ({
    callback,
    errorCallback,
    authorizationNo,
    data,
  }: AuthorizationPayload) => {
    try {
      const result = await axios.put(
        `${AUTHORIZATION_URL}/${authorizationNo}`,
        data,
        {
          headers: common.headers,
        },
      )
      if (result) {
        callback()
      }
    } catch (error) {
      errorCallback(error)
    }
  },
}
