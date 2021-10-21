import { Page } from '@utils'
import axios, { AxiosError } from 'axios'
import useSWR from 'swr'
import { common, SearchPayload } from './common'

/**
 * 저장 시 데이터 타입
 */
export interface PrivacySavePayload {
  privacyTitle: string
  privacyContent: string
  useAt: boolean
}

/**
 * request payload
 */
export interface PrivacyPayload {
  callback: () => any
  errorCallback: (error: AxiosError) => void
  privacyNo?: string
  data?: PrivacySavePayload
  useAt?: boolean
}

/**
 * 포털 서비스 개인정보처리방침 API URL
 */
const PRIVACY_URL = '/portal-service/api/v1/privacies'

/**
 * 개인정보처리방침 관리 서비스
 */
export const privacyService = {
  search: ({ keywordType, keyword, size, page }: SearchPayload) =>
    // eslint-disable-next-line react-hooks/rules-of-hooks
    useSWR<Page, AxiosError>(
      [`${PRIVACY_URL}?size=${size}&page=${page}`, keywordType, keyword],
      url => common.fetcher(url, { keywordType, keyword }),
      { revalidateOnFocus: false, errorRetryCount: 0 },
    ),
  get: async (privacyNo: string) => axios.get(`${PRIVACY_URL}/${privacyNo}`),
  delete: async ({ privacyNo, callback, errorCallback }: PrivacyPayload) => {
    try {
      const result = await axios.delete(`${PRIVACY_URL}/${privacyNo}`)
      if (result) {
        callback()
      }
    } catch (error) {
      errorCallback(error)
    }
  },
  save: async ({ callback, errorCallback, data }: PrivacyPayload) => {
    try {
      const result = await axios.post(PRIVACY_URL, data, {
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
    privacyNo,
    data,
  }: PrivacyPayload) => {
    try {
      const result = await axios.put(`${PRIVACY_URL}/${privacyNo}`, data, {
        headers: common.headers,
      })
      if (result) {
        callback()
      }
    } catch (error) {
      errorCallback(error)
    }
  },
  updateUseAt: async ({
    callback,
    errorCallback,
    privacyNo,
    useAt,
  }: PrivacyPayload) => {
    try {
      const result = await axios.put(`${PRIVACY_URL}/${privacyNo}/${useAt}`, {
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
