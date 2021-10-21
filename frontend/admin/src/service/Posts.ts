import { Page } from '@utils'
import axios, { AxiosError } from 'axios'
import useSWR from 'swr'
import { common, SearchPayload } from './common'

/**
 * 저장 시 데이터 타입
 */
export interface PostsSavePayload {
  boardNo: number
  postsNo?: number
  postsTitle: string
  noticeAt: boolean
  postsContent: string
  postsAnswerContent?: string
  attachmentCode?: string
  createdBy?: string
  createdName?: string
  createdDate?: string
  readCount?: number
  deleteAt?: number
  commentCount?: number
}

/**
 * request payload
 */
export interface PostsPayload {
  callback: () => any
  errorCallback: (error: AxiosError) => void
  boardNo?: number
  postsNo?: number
  data?: PostsSavePayload | PostsDeletePayload[]
}

/**
 * 삭제/복원/완전삭제 시 데이터 타입
 */
export interface PostsDeletePayload {
  boardNo: number
  postsNo: number
}

/**
 * 포털 서비스 게시물 API URL
 */
const POSTS_URL = '/board-service/api/v1/posts'

/**
 * 게시물 관리 서비스
 */
export const postsService = {
  search: (
    boardNo: number,
    { keywordType, keyword, size, page }: SearchPayload,
  ) =>
    // eslint-disable-next-line react-hooks/rules-of-hooks
    useSWR<Page, AxiosError>(
      [
        `${POSTS_URL}/${boardNo}?size=${size}&page=${page}`,
        keywordType,
        keyword,
      ],
      url => common.fetcher(url, { keywordType, keyword }),
      { revalidateOnFocus: false, errorRetryCount: 0 },
    ),
  get: async (boardNo: number, postsNo: number) =>
    axios.get(`${POSTS_URL}/${boardNo}/${postsNo}`),
  save: async ({ boardNo, callback, errorCallback, data }: PostsPayload) => {
    try {
      const result = await axios.post(`${POSTS_URL}/${boardNo}`, data, {
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
    boardNo,
    postsNo,
    callback,
    errorCallback,
    data,
  }: PostsPayload) => {
    try {
      const result = await axios.put(
        `${POSTS_URL}/${boardNo}/${postsNo}`,
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
  remove: async ({ callback, errorCallback, data }: PostsPayload) => {
    try {
      const result = await axios.put(`${POSTS_URL}/remove`, data, {
        headers: common.headers,
      })
      if (result) {
        callback()
      }
    } catch (error) {
      errorCallback(error)
    }
  },
  restore: async ({ callback, errorCallback, data }: PostsPayload) => {
    try {
      const result = await axios.put(`${POSTS_URL}/restore`, data, {
        headers: common.headers,
      })
      if (result) {
        callback()
      }
    } catch (error) {
      errorCallback(error)
    }
  },
  delete: async ({ callback, errorCallback, data }: PostsPayload) => {
    try {
      const result = await axios.put(`${POSTS_URL}/delete`, data, {
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
