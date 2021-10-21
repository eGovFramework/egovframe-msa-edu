import axios, { AxiosError } from 'axios'
import useSWR from 'swr'
import { common, SearchPayload } from './common'
import { Page } from '@utils'

/**
 * 코드 인터페이스
 */
export interface ICode {
  // parentCodeId?: string
  codeId: string
  codeName: string
  sortSeq: number
}

/**
 * 상세코드 조회조건 인터페이스
 */
export interface ICodeDetailSearch extends SearchPayload {
  parentCodeId?: string
}

/**
 * 저장 시 데이터 타입
 */
export interface CodeSavePayload {
  parentCodeId?: string
  codeId: string
  codeName: string
  codeDescription: string
  sortSeq: number
  useAt: boolean
}

/**
 * request payload
 */
export interface CodePayload {
  callback: () => any
  errorCallback: (error: AxiosError) => void
  id?: string
  data?: CodeSavePayload
  useAt?: boolean
}

/**
 * 코드 API URL
 */
const CODE_URL = '/portal-service/api/v1/codes'
const CODE_DETAIL_URL = '/portal-service/api/v1/code-details'

/**
 * 코드 서비스
 */
export const codeService = {
  search: ({ keywordType, keyword, size, page }: SearchPayload) => {
    return useSWR<Page, AxiosError>(
      [`${CODE_URL}?size=${size}&page=${page}`, keywordType, keyword],
      (url, keywordType, keyword) =>
        common.fetcher(url, { keywordType, keyword }),
      { revalidateOnFocus: false, errorRetryCount: 0 },
    )
  },
  searchDetail: ({
    parentCodeId,
    keywordType,
    keyword,
    size,
    page,
  }: ICodeDetailSearch) => {
    return useSWR<Page, AxiosError>(
      [
        `${CODE_DETAIL_URL}?size=${size}&page=${page}`,
        parentCodeId === '-' ? '' : parentCodeId,
        keywordType,
        keyword,
      ],
      (url, parentCodeId, keywordType, keyword) =>
        common.fetcher(url, { parentCodeId, keywordType, keyword }),
      { revalidateOnFocus: false, errorRetryCount: 0 },
    )
  },
  getOne: (id: string) => {
    return axios.get(`${CODE_URL}/${id}`, {
      headers: common.headers,
    })
  },
  getOneDetail: (id: string) => {
    return axios.get(`${CODE_DETAIL_URL}/${id}`, {
      headers: common.headers,
    })
  },
  getParentCode: (id: string) => {
    return axios.get(`${CODE_DETAIL_URL}/${id}/parent`, {
      headers: common.headers,
    })
  },
  delete: async ({ id, callback, errorCallback }: CodePayload) => {
    try {
      const result = await axios.delete(`${CODE_URL}/${id}`)
      if (result) {
        callback()
      }
    } catch (error) {
      errorCallback(error)
    }
  },
  deleteDetail: async ({ id, callback, errorCallback }: CodePayload) => {
    try {
      const result = await axios.delete(`${CODE_DETAIL_URL}/${id}`)
      if (result) {
        callback()
      }
    } catch (error) {
      errorCallback(error)
    }
  },
  save: async ({ callback, errorCallback, data }: CodePayload) => {
    try {
      const result = await axios.post(`${CODE_URL}`, data, {
        headers: common.headers,
      })
      if (result) {
        callback()
      }
    } catch (error) {
      errorCallback(error)
    }
  },
  saveDetail: async ({ callback, errorCallback, data }: CodePayload) => {
    try {
      const result = await axios.post(`${CODE_DETAIL_URL}`, data, {
        headers: common.headers,
      })
      if (result) {
        callback()
      }
    } catch (error) {
      errorCallback(error)
    }
  },
  update: async ({ callback, errorCallback, id, data }: CodePayload) => {
    try {
      const result = await axios.put(`${CODE_URL}/${id}`, data, {
        headers: common.headers,
      })
      if (result) {
        callback()
      }
    } catch (error) {
      errorCallback(error)
    }
  },
  updateDetail: async ({ callback, errorCallback, id, data }: CodePayload) => {
    try {
      const result = await axios.put(`${CODE_DETAIL_URL}/${id}`, data, {
        headers: common.headers,
      })
      if (result) {
        callback()
      }
    } catch (error) {
      errorCallback(error)
    }
  },
  updateUse: async ({ callback, errorCallback, id, useAt }: CodePayload) => {
    try {
      const result = await axios.put(
        `${CODE_URL}/${id}/toggle-use?useAt=${useAt}`,
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
  updateUseDetail: async ({
    callback,
    errorCallback,
    id,
    useAt,
  }: CodePayload) => {
    try {
      const result = await axios.put(
        `${CODE_DETAIL_URL}/${id}/toggle-use?useAt=${useAt}`,
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
  getCodeDetailList: async (parentCodeId: string) =>
    axios.get(`${CODE_DETAIL_URL}/${parentCodeId}/codes`),
  getParentCodeList: async () =>
    axios.get(`${CODE_URL}-parent`, {
      headers: common.headers,
    }),
}
