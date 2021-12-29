import { Page } from '@utils'
import axios, { AxiosError } from 'axios'
import useSWR from 'swr'
import { common, SearchPayload } from './common'

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
  download: (id: string) => {
    // 첨부파일 다운로드 - 삭제 파일 가능
    axios
      .get(`/portal-service/api/v1/download/${id}`, {
        responseType: 'blob',
      })
      .then(response => {
        const downloadFileName = decodeURIComponent(
          response.headers['content-disposition'].replace(
            "attachment; filename*=UTF-8''",
            '',
          ),
        )

        const url = window.URL.createObjectURL(
          new Blob([response.data], { type: response.headers['content-type'] }),
        )
        let link = document.createElement('a')
        link.href = url
        link.setAttribute('download', downloadFileName)
        document.body.appendChild(link)
        link.click()

        const element = { link }
        delete element.link
      })
  },
}
