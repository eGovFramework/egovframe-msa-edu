import { Page } from '@utils'
import axios, { AxiosError } from 'axios'
import useSWR from 'swr'
import { common, SearchPayload } from './common'

/**
 * 게시판 타입
 */
export interface IBoardProps {
  boardNo: number
  boardName: string
  skinTypeCode: string
  titleDisplayLength: number
  postDisplayCount: number
  pageDisplayCount: number
  newDisplayDayCount: number
  editorUseAt: boolean
  userWriteAt: boolean
  commentUseAt: boolean
  uploadUseAt: boolean
  uploadLimitCount: number
  uploadLimitSize: number
}

/**
 * 저장 시 데이터 타입
 */
export interface BoardSavePayload {
  boardNo?: number
  boardName: string
  skinTypeCode: string
  titleDisplayLength: number
  postDisplayCount: number
  pageDisplayCount: number
  newDisplayDayCount: number
  editorUseAt: boolean
  userWriteAt: boolean
  commentUseAt: boolean
  uploadUseAt: boolean
  uploadLimitCount: number
  uploadLimitSize: number
}

/**
 * request payload
 */
export interface BoardPayload {
  callback: () => any
  errorCallback: (error: AxiosError) => void
  boardNo?: number
  data?: BoardSavePayload
}

/**
 * 게시판 스킨 유형
 */
export const SKINT_TYPE_CODE_NORMAL = 'normal'
export const SKINT_TYPE_CODE_FAQ = 'faq'
export const SKINT_TYPE_CODE_QNA = 'qna'

/**
 * 포털 서비스 게시판 API URL
 */
const BOARD_URL = '/board-service/api/v1/boards'

/**
 * 게시판 관리 서비스
 */
export const boardService = {
  search: ({ keywordType, keyword, size, page }: SearchPayload) =>
    // eslint-disable-next-line react-hooks/rules-of-hooks
    useSWR<Page, AxiosError>(
      [`${BOARD_URL}?size=${size}&page=${page}`, keywordType, keyword],
      url => common.fetcher(url, { keywordType, keyword }),
      { revalidateOnFocus: false, errorRetryCount: 0 },
    ),
  get: async (boardNo: number) => axios.get(`${BOARD_URL}/${boardNo}`),
  delete: async ({ boardNo, callback, errorCallback }: BoardPayload) => {
    try {
      const result = await axios.delete(`${BOARD_URL}/${boardNo}`)
      if (result) {
        callback()
      }
    } catch (error) {
      errorCallback(error)
    }
  },
  save: async ({ callback, errorCallback, data }: BoardPayload) => {
    try {
      const result = await axios.post(BOARD_URL, data, {
        headers: common.headers,
      })
      if (result) {
        callback()
      }
    } catch (error) {
      errorCallback(error)
    }
  },
  update: async ({ callback, errorCallback, boardNo, data }: BoardPayload) => {
    try {
      const result = await axios.put(`${BOARD_URL}/${boardNo}`, data, {
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
