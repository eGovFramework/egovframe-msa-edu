import axios, { AxiosError } from 'axios'
import useSWR from 'swr'
import { common, Page, Pageable, SearchPayload, Sort } from './common'

/**
 * 게시판 스킨 유형
 */
export const SKINT_TYPE_CODE_NORMAL = 'normal'
export const SKINT_TYPE_CODE_FAQ = 'faq'
export const SKINT_TYPE_CODE_QNA = 'qna'

export interface IMainBoard {
  [key: number]: IBoard
}

/**
 * 게시판 타입
 */
export interface IBoard {
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
  posts: IPosts[]
}

/**
 * 저장 시 데이터 타입
 */
export interface IPostsForm {
  postsTitle: string
  postsContent: string
  attachmentCode?: string
}
export interface IPosts extends IPostsForm {
  boardNo: number
  postsNo?: number
  noticeAt?: boolean
  postsTitle: string
  postsContent: string
  postsAnswerContent?: string
  attachmentCode?: string
  createdBy?: string
  createdName?: string
  createdDate?: string
  readCount?: number
  deleteAt?: number
  isNew?: boolean
  commentCount?: number
  prevPosts?: IPostsForm[]
  nextPosts?: IPostsForm[]
}

export interface PostsPayload {
  callback: () => any
  errorCallback: (error: AxiosError) => void
  boardNo?: number
  postsNo?: number
  data?: IPostsForm
}

export interface PostsReqPayload extends SearchPayload {
  boardNo: number
  postsNo: number
  headers?: any
  keywordType?: string
  keyword?: string
}

export interface ICommentSearchProps {
  boardNo: number
  postsNo: number
  _page: number
  _mode: 'replace' | 'append' | 'until'
}

export interface ICommentPage {
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
  content: CommentSavePayload[] | []
}

export interface CommentSavePayload {
  boardNo: number
  postsNo: number
  commentNo?: number
  commentContent: string
  commentAnswerContent?: string
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

const BOARD_URL = '/board-service/api/v1/boards'
const POSTS_URL = '/board-service/api/v1/posts'
const COMMENT_URL = '/board-service/api/v1/comments'

export const boardService = {
  getBoardById: (boardNo: number) => {
    return axios.get(`${BOARD_URL}/${boardNo}`)
  },
  getMainPosts: (boardNos: number[], count: number) => {
    return axios.get(`${POSTS_URL}/newest/${boardNos}/${count}`)
  },
  search: (
    boardNo: number,
    { keywordType, keyword, size, page }: SearchPayload,
  ) =>
    useSWR<Page, AxiosError>(
      () =>
        typeof boardNo === 'number' && boardNo > -1
          ? [
              `${POSTS_URL}/list/${boardNo}?size=${size}&page=${page}`,
              keywordType,
              keyword,
            ]
          : null,
      url => common.fetcher(url, { keywordType, keyword }),
      { revalidateOnFocus: false, errorRetryCount: 0 },
    ),
  getPostById: ({
    boardNo,
    postsNo,
    keywordType,
    keyword,
  }: PostsReqPayload) => {
    return axios.get(
      `${POSTS_URL}/view/${boardNo}/${postsNo}?keywordType=${keywordType}&keyword=${keyword}`,
    )
  },
  savePost: async ({
    boardNo,
    callback,
    errorCallback,
    data,
  }: PostsPayload) => {
    try {
      const result = await axios.post(`${POSTS_URL}/save/${boardNo}`, data, {
        headers: common.headers,
      })
      if (result) {
        callback()
      }
    } catch (error) {
      errorCallback(error)
    }
  },
  updatePost: async ({
    boardNo,
    postsNo,
    callback,
    errorCallback,
    data,
  }: PostsPayload) => {
    try {
      const result = await axios.put(
        `${POSTS_URL}/update/${boardNo}/${postsNo}`,
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
  removePost: async ({ callback, errorCallback, data }: PostsPayload) => {
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
  getComments: (boardNo: number, postsNo: number, size: number, page: number) =>
    new Promise<ICommentPage>((resolve, rejects) => {
      axios
        .get(
          `${COMMENT_URL}/list/${boardNo}/${postsNo}?size=${size}&page=${page}`,
        )
        .then(result => {
          resolve(result.data)
        })
        .catch(e => rejects(e))
    }),
  getAllComments: (boardNo: number, postsNo: number) =>
    new Promise<Page>((resolve, rejects) => {
      axios
        .get(`${COMMENT_URL}/all/${boardNo}/${postsNo}`)
        .then(result => {
          resolve(result.data)
        })
        .catch(e => rejects(e))
    }),
  saveComment: (comment: CommentSavePayload) => {
    return new Promise<Page>((resolve, rejects) => {
      axios
        .post(`${COMMENT_URL}`, comment)
        .then(result => {
          resolve(result.data)
        })
        .catch(e => rejects(e))
    })
  },
  updateComment: (comment: CommentSavePayload) => {
    return new Promise<Page>((resolve, rejects) => {
      axios
        .post(`${COMMENT_URL}/update`, comment)
        .then(result => {
          resolve(result.data)
        })
        .catch(e => rejects(e))
    })
  },
  deleteComment: ({ boardNo, postsNo, commentNo }: CommentSavePayload) => {
    return new Promise<Page>((resolve, rejects) => {
      axios
        .delete(`${COMMENT_URL}/delete/${boardNo}/${postsNo}/${commentNo}`)
        .then(result => {
          resolve(result.data)
        })
        .catch(e => rejects(e))
    })
  },
}
