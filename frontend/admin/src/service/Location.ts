import axios, { AxiosError } from 'axios'
import useSWR from 'swr'
import { common, SearchPayload } from '@service'
import { Page } from '@utils'

const API_URL = '/reserve-item-service/api/v1/locations'

export interface ILocation {
  locationId?: number
  locationName: string
  sortSeq: number
  isUse: boolean
}

export const locationService = {
  search: ({ keywordType, keyword, size, page }: SearchPayload) =>
    useSWR<Page, AxiosError>(
      [`${API_URL}?size=${size}&page=${page}`, keywordType, keyword],
      url => common.fetcher(url, { keywordType, keyword }),
      { revalidateOnFocus: false, errorRetryCount: 0 },
    ),
  get: (id: number) =>
    axios.get(`${API_URL}/${id}`, {
      headers: common.headers,
    }),
  save: (data: ILocation) =>
    axios.post(API_URL, data, {
      headers: common.headers,
    }),
  update: (id: number, data: ILocation) =>
    axios.put(`${API_URL}/${id}`, data, {
      headers: common.headers,
    }),
  delete: (id: number) =>
    axios.delete(`${API_URL}/${id}`, {
      headers: common.headers,
    }),
  updateUse: (id: number, isUse: boolean) =>
    axios.put(`${API_URL}/${id}/${isUse}`, null, {
      headers: common.headers,
    }),
  getList: () =>
    axios.get(`${API_URL}/combo`, {
      headers: common.headers,
    }),
}
