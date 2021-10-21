import axios, { AxiosError } from 'axios'
import useSWR from 'swr'
import { common, SearchPayload } from './common'
import { Page } from '@utils'

/**
 * request payload
 */
export interface AttachmentPayload {
  callback: () => any
  errorCallback: (error: AxiosError) => void
  id?: string
  isDelete?: boolean
}

const ATTACHMENT_API = '/portal-service/api/v1/attachments'

/**
 * 첨부파일 관리 서비스
 */
export const attachmentService = {
  url: ATTACHMENT_API,
  search: ({ keywordType, keyword, size, page }: SearchPayload) => {
    return useSWR<Page, AxiosError>(
      [`${ATTACHMENT_API}?size=${size}&page=${page}`, keywordType, keyword],
      (url, keywordType, keyword) =>
        common.fetcher(url, { keywordType, keyword }),
      { revalidateOnFocus: false, errorRetryCount: 0 },
    )
  },
  delete: async ({ id, callback, errorCallback }: AttachmentPayload) => {
    try {
      const result = await axios.delete(`${ATTACHMENT_API}/${id}`)
      if (result) {
        callback()
      }
    } catch (error) {
      errorCallback(error)
    }
  },
  updateToggle: async ({
    callback,
    errorCallback,
    id,
    isDelete,
  }: AttachmentPayload) => {
    try {
      const result = await axios.put(`${ATTACHMENT_API}/${id}/${isDelete}`, {
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
