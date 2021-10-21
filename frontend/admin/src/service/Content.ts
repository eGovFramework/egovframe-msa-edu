import { Page } from '@utils'
import axios, { AxiosError } from 'axios'
import useSWR from 'swr'
import { common, SearchPayload } from './common'

/**
 * 저장 시 데이터 타입
 */
export interface ContentSavePayload {
  contentName: string
  contentRemark: string
  contentValue: string
}

/**
 * request payload
 */
export interface ContentPayload {
  callback: () => any
  errorCallback: (error: AxiosError) => void
  contentNo?: string
  data?: ContentSavePayload
}

/**
 * 포털 서비스 컨텐츠 API URL
 */
const CONTENT_URL = '/portal-service/api/v1/contents'

/**
 * 컨텐츠 관리 서비스
 */
export const contentService = {
  search: ({ keywordType, keyword, size, page }: SearchPayload) =>
    // eslint-disable-next-line react-hooks/rules-of-hooks
    useSWR<Page, AxiosError>(
      [`${CONTENT_URL}?size=${size}&page=${page}`, keywordType, keyword],
      url => common.fetcher(url, { keywordType, keyword }),
      { revalidateOnFocus: false, errorRetryCount: 0 },
    ),
  get: async (contentNo: string) => axios.get(`${CONTENT_URL}/${contentNo}`),
  delete: async ({ contentNo, callback, errorCallback }: ContentPayload) => {
    try {
      const result = await axios.delete(`${CONTENT_URL}/${contentNo}`)
      if (result) {
        callback()
      }
    } catch (error) {
      errorCallback(error)
    }
  },
  save: async ({ callback, errorCallback, data }: ContentPayload) => {
    try {
      const result = await axios.post(CONTENT_URL, data, {
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
    contentNo,
    data,
  }: ContentPayload) => {
    try {
      const result = await axios.put(`${CONTENT_URL}/${contentNo}`, data, {
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
