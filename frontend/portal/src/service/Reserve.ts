import { common, Page, SearchPayload } from '@service'
import axios, { AxiosError } from 'axios'
import useSWR from 'swr'

const LIST_API_URL = (categoryId: ValueType) =>
  `/reserve-item-service/api/v1/${categoryId}/reserve-items`
const ITEM_API_URL = `/reserve-item-service/api/v1/reserve-items`
const REQUEST_API_URL = '/reserve-request-service/api/v1/requests'
const CODE_API_URL = `/portal-service/api/v1/code-details`
const LOCATION_API_URL = '/reserve-item-service/api/v1/locations'
const RESERVE_API_URL = '/reserve-check-service/api/v1'

interface ReserveSearchPayload extends SearchPayload {
  categoryId?: ValueType
  locationId?: ValueType
  userId?: string
}

export interface IMainItem {
  [key: string]: IReserveItemMain[]
}

export interface IReserveItemMain {
  reserveItemId: number
  reserveItemName: string
  categoryId: string
  categoryName: string
  startDate: string
  endDate: string
  isPossible: boolean
}

export interface ILocation {
  locationId?: number
  locationName: string
  sortSeq: number
  isUse: boolean
}

export interface ICode {
  codeId: string
  codeName: string
  sortSeq: number
}

export interface IReserveItem {
  reserveItemId: number
  reserveItemName: string
  locationId: number
  location: ILocation
  categoryId: string
  categoryName: string
  totalQty: number
  inventoryQty: number
  operationStartDate: string
  operationEndDate: string
  reserveMethodId: string
  reserveMethodName: string
  reserveMeansId: string
  reserveMeansName: string
  requestStartDate: string
  requestEndDate: string
  isPeriod: true
  periodMaxCount: number
  externalUrl: string
  selectionMeansId: string
  selectionMeansName: string
  isPaid: true
  usageCost: number
  isUse: true
  purpose: string
  address: string
  targetId: string
  targetName: string
  excluded: string
  homepage: string
  contact: string
  managerDept: string
  managerName: string
  managerContact: string
  isPossible: boolean
}

export interface ReserveSavePayload {
  reserveId: string
  reserveItemId: number
  locationId: number
  categoryId: string
  totalQty: number
  reserveMethodId: string
  reserveMeansId: string
  operationStartDate: string
  operationEndDate: string
  requestStartDate: string
  requestEndDate: string
  isPeriod: boolean
  periodMaxCount: number
  reserveQty: number
  reservePurposeContent: string
  attachmentCode: string
  reserveStartDate: string
  reserveEndDate: string
  reserveStatusId: string
  userId: string
  userContactNo: string
  userEmail: string
}

export interface IReserve {
  reserveId: string
  reserveItemId: number
  reserveItem: IReserveItem
  reserveQty: number
  reserveStartDate: string
  reserveEndDate: string
  reservePurposeContent: string
  attachmentCode: string
  reserveStatusId: string
  userId: string
  userName: string
  userContactNo: string
  userEmail: string
}

export const reserveService = {
  requestApiUrl: REQUEST_API_URL,
  search: ({
    keywordType,
    keyword,
    size,
    page,
    categoryId,
    locationId,
  }: ReserveSearchPayload) =>
    useSWR<Page, AxiosError>(
      [
        `${LIST_API_URL(categoryId)}?size=${size}&page=${page}`,
        keywordType,
        keyword,
        locationId,
        categoryId,
      ],
      url =>
        common.fetcher(url, {
          keywordType,
          keyword,
          locationId,
          categoryId,
          isUse: true,
          isPopup: false,
        }),
      { revalidateOnFocus: false, errorRetryCount: 0 },
    ),
  getCode: (codeId: string) =>
    axios.get(`${CODE_API_URL}/${codeId}/codes`, {
      headers: common.headers,
    }),
  getLocation: () =>
    axios.get(`${LOCATION_API_URL}/combo`, {
      headers: common.headers,
    }),
  getItem: (reserveItemId: number) =>
    axios.get(`${ITEM_API_URL}/relations/${reserveItemId}`),
  getCountInventory: (
    reserveItemId: number,
    startDate: string,
    endDate: string,
  ) =>
    axios.get(
      `${RESERVE_API_URL}/reserves/${reserveItemId}/inventories?startDate=${startDate}&endDate=${endDate}`,
      {
        data: {
          startDate,
          endDate,
        },
      },
    ),
  createAudit: (data: ReserveSavePayload) =>
    axios.post(`${REQUEST_API_URL}/evaluates`, data),
  create: (data: ReserveSavePayload) => axios.post(REQUEST_API_URL, data),
  getMainItems: (count: number) => axios.get(`${ITEM_API_URL}/latest/${count}`),
  searchUserReserve: ({
    userId,
    size,
    page,
    keywordType,
    keyword,
    locationId,
    categoryId,
  }: ReserveSearchPayload) =>
    useSWR(
      [
        `${RESERVE_API_URL}/${userId}/reserves?size=${size}&page=${page}`,
        keywordType,
        keyword,
        locationId,
        categoryId,
      ],
      url =>
        common.fetcher(url, {
          keywordType,
          keyword,
          locationId,
          categoryId,
        }),
      { revalidateOnFocus: false, errorRetryCount: 0 },
    ),
  getReserve: (reserveId: string) =>
    axios.get(`${RESERVE_API_URL}/reserves/${reserveId}`),
  cancel: (reserveId: string, reason: ValueType) =>
    axios.put(`${RESERVE_API_URL}/reserves/cancel/${reserveId}`, {
      reasonCancelContent: reason,
    }),
}
