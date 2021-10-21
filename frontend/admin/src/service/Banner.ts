import { ISite } from '@service'
import { Page } from '@utils'
import axios, { AxiosError } from 'axios'
import useSWR from 'swr'
import { common, SearchPayload } from './common'

/**
 * 저장 시 데이터 타입
 */
export interface BannerSavePayload {
  siteId: number
  bannerTypeCode: string
  bannerTitle: string
  attachmentCode: string
  urlAddr: string
  newWindowAt: boolean
  bannerContent: string
  sortSeq: number
}

/**
 * request payload너
 */
export interface BannerPayload {
  callback?: () => any
  errorCallback?: (error: AxiosError) => void
  bannerNo?: string
  data?: BannerSavePayload
  useAt?: boolean
}

/**
 * 포털 서비스 배너 API URL
 */
const BANNER_URL = '/portal-service/api/v1/banners'

interface BannerSearchPayload extends SearchPayload {
  siteId: string | number
}

/**
 * 배너 관리 서비스
 */
export const bannerService = {
  search: ({ keywordType, keyword, size, page, siteId }: BannerSearchPayload) =>
    // eslint-disable-next-line react-hooks/rules-of-hooks
    useSWR<Page, AxiosError>(
      [`${BANNER_URL}?size=${size}&page=${page}`, keywordType, keyword, siteId],
      url => common.fetcher(url, { keywordType, keyword, siteId }),
      { revalidateOnFocus: false, errorRetryCount: 0 },
    ),
  get: async (bannerNo: string) => axios.get(`${BANNER_URL}/${bannerNo}`),
  getNextSortSeq: async (siteId: number) =>
    axios.get(`${BANNER_URL}/${siteId}/sort-seq/next`),
  delete: async ({ bannerNo, callback, errorCallback }: BannerPayload) => {
    try {
      const result = await axios.delete(`${BANNER_URL}/${bannerNo}`)
      if (result) {
        callback()
      }
    } catch (error) {
      errorCallback(error)
    }
  },
  save: ({ data }: BannerPayload) =>
    axios.post(BANNER_URL, data, {
      headers: common.headers,
    }),
  update: ({ bannerNo, data }: BannerPayload) =>
    axios.put(`${BANNER_URL}/${bannerNo}`, data, {
      headers: common.headers,
    }),
  updateUseAt: async ({
    callback,
    errorCallback,
    bannerNo,
    useAt,
  }: BannerPayload) => {
    try {
      const result = await axios.put(`${BANNER_URL}/${bannerNo}/${useAt}`, {
        headers: common.headers,
      })
      if (result) {
        callback()
      }
    } catch (error) {
      errorCallback(error)
    }
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
}
