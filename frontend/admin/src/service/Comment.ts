import { Page, Pageable, Sort } from '@utils'
import axios, { AxiosError } from 'axios'
import { common } from './common'

export interface CommentPage {
  empty: boolean
  first: boolean
  last: boolean
  number: number
  numberOfElements: number
  pageable: Pageable
  size: number
  sort: Sort
  totalElements: number
  groupElements: number
  totalPages: number
  content: any[] | []
}

export interface IComment {
  boardNo: number
  postsNo: number
  commentNo?: number
  commentContent: string
  groupNo?: number
  parentCommentNo?: number
  depthSeq: number
  sortSeq?: number
  deleteAt?: number
  createdBy?: string
  createdName?: string
  createdDate?: string
  mode?: 'none' | 'edit' | 'reply'
}

/**
 * 저장 시 데이터 타입
 */
export interface CommentSavePayload {
  boardNo: number
  postsNo: number
  commentNo?: number
  commentContent: string
  groupNo?: number
  parentCommentNo?: number
  depthSeq: number
  sortSeq?: number
  deleteAt?: number
  createdBy?: string
  createdName?: string
  createdDate?: string
}

/**
 * 삭제 시 데이터 타입
 */
export interface CommentDeletePayload {
  boardNo: number
  postsNo: number
}

/**
 * request payload
 */
export interface CommentPayload {
  callback: () => any
  errorCallback: (error: AxiosError) => void
  boardNo?: number
  postsNo?: number
  commentNo?: number
  data?: CommentSavePayload
}

/**
 * 포털 서비스 게시물 API URL
 */
const COMMENT_URL = '/board-service/api/v1/comments'

/**
 * 게시물 관리 서비스
 */
export const commentService = {
  /* list: (boardNo: number, postsNo: number, size: number, page: number) =>
    // eslint-disable-next-line react-hooks/rules-of-hooks
    useSWR<Page, AxiosError>(
      [`${COMMENT_URL}/${boardNo}/${postsNo}?size=${size}&page=${page}`],
      url => common.fetcher(url, {}),
      { revalidateOnFocus: false, errorRetryCount: 0 },
    ), */
  list: (boardNo: number, postsNo: number, size: number, page: number) =>
    new Promise<CommentPage>((resolve, rejects) => {
      try {
        axios
          .get(`${COMMENT_URL}/${boardNo}/${postsNo}?size=${size}&page=${page}`)
          .then(result => {
            resolve(result.data)
          })
      } catch (error) {
        rejects(error)
      }
    }),
  all: (boardNo: number, postsNo: number) =>
    new Promise<Page>((resolve, rejects) => {
      try {
        axios.get(`${COMMENT_URL}/total/${boardNo}/${postsNo}`).then(result => {
          resolve(result.data)
        })
      } catch (error) {
        rejects(error)
      }
    }),
  save: async ({ callback, errorCallback, data }: CommentPayload) => {
    try {
      const result = await axios.post(`${COMMENT_URL}`, data, {
        headers: common.headers,
      })
      if (result) {
        callback()
      }
    } catch (error) {
      errorCallback(error)
    }
  },
  update: async ({ callback, errorCallback, data }: CommentPayload) => {
    try {
      const result = await axios.put(`${COMMENT_URL}`, data, {
        headers: common.headers,
      })
      if (result) {
        callback()
      }
    } catch (error) {
      errorCallback(error)
    }
  },
  delete: async ({
    boardNo,
    postsNo,
    commentNo,
    callback,
    errorCallback,
  }: CommentPayload) => {
    try {
      const result = await axios.delete(
        `${COMMENT_URL}/${boardNo}/${postsNo}/${commentNo}`,
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
