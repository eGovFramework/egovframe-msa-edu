import { CUSTOM_HEADER_SITE_ID_KEY } from '@constants'
import { SITE_ID } from '@constants/env'
import axios from 'axios'

let headers = {
  'Content-Type': 'application/json',
}
headers[CUSTOM_HEADER_SITE_ID_KEY] = SITE_ID

export interface Sort {
  empty: boolean
  sorted: boolean
  unsorted: boolean
}

export interface Pageable {
  offset: number
  pageNumber: number
  pageSize: number
  paged: boolean
  unpaged: boolean
  sort: Sort
}

export interface Page {
  empty: boolean
  first: boolean
  last: boolean
  number: number
  numberOfElements: number
  pageable: Pageable
  size: number
  sort: Sort
  totalElements: number
  totalPages: number
  content: any[] | []
}

export interface SearchPayload {
  keywordType?: ValueType
  keyword?: ValueType
  size?: number
  page?: number
}

const fetcher = async (url: string, param: {}) => {
  const res = await axios.get(url, {
    params: param,
    headers,
  })

  return res.data
}

export const common = {
  headers,
  fetcher,
}
