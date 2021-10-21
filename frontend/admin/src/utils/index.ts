import { BASE_URL } from '@constants'
import { ACCESS_TOKEN } from '@constants/env'
import axios from 'axios'
import Cookies from 'cookies'
import { IncomingMessage, ServerResponse } from 'http'

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

// DataGrid rownum 계산..
export const rownum = (data: Page, index: number, orderby?: 'asc' | 'desc') => {
  if (orderby === 'asc') {
    return data.size * data.number + index + 1
  }
  return data.totalElements - data.size * data.number - index
}

export const formatBytes = (bytes: number, decimals: number = 2) => {
  if (bytes === 0) return '0 Bytes'

  const k = 1024
  const dec = decimals < 0 ? 0 : decimals
  const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(dec)) + ' ' + sizes[i]
}

export const format = (text: string, args: any[]) =>
  text.replace(/{(\d+)}/g, (match, number) =>
    typeof args[number] !== 'undefined' ? args[number] : match,
  )

export const getType = (target: any) => {
  return Object.prototype.toString.call(target).slice(8, -1)
}

export const translateToLang = (
  cur: string,
  data: any,
  korKey: string = 'korName',
  otherKey: string = 'engName',
): string => {
  if (cur === 'ko') {
    return data[korKey]
  }

  return data[otherKey]
}
