import { common, SearchPayload } from '@service'
import { Page } from '@utils'
import axios, { AxiosError } from 'axios'
import {
  Control,
  FormState,
  UseFormGetValues,
  UseFormRegister,
} from 'react-hook-form'
import useSWR from 'swr'
import { ILocation } from './Location'

const API_URL = '/reserve-item-service/api/v1/reserve-items'
const CODE_API_URL = (code: string) =>
  `/portal-service/api/v1/code-details/${code}/codes`

export interface IReserveItemList {
  reserveItemId: number
  reserveItemName: string
  locationId: number
  categoryId: string
  totalQty: number
  inventoryQty: number
  isUse: boolean
  createDate: Date
  isPossible: boolean
}

export interface IReserveItem {
  reserveItemId: number
  reserveItemName: string
  locationId: number
  categoryId: string
  prevTotalQty: number
  totalQty: number
  inventoryQty: number
  operationStartDate: Date
  operationEndDate: Date
  reserveMethodId: string
  reserveMeansId?: string
  requestStartDate?: Date
  requestEndDate?: Date
  isPeriod?: boolean
  periodMaxCount?: number
  externalUrl?: string
  selectionMeansId: string
  isPaid: boolean
  usageCost?: number
  isUse: boolean
  purpose?: string
  address?: string
  targetId?: string
  excluded?: string
  homepage?: string
  contact?: string
  managerDept?: string
  managerName?: string
  managerContact?: string
}

export interface IReserveItemRelation {
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
}

export interface ReserveItemFormProps {
  control: Control<IReserveItem, object>
  formState: FormState<IReserveItem>
  register?: UseFormRegister<IReserveItem>
  getValues?: UseFormGetValues<IReserveItem>
}

interface ReserveItemSearchPayload extends SearchPayload {
  locationId?: string
  categoryId?: string
  isUse?: boolean
  isPopup?: boolean
}

export const reserveItemService = {
  search: ({
    keywordType,
    keyword,
    size,
    page,
    locationId,
    categoryId,
    isUse = false,
    isPopup = false,
  }: ReserveItemSearchPayload) => {
    return useSWR<Page, AxiosError>(
      [
        `${API_URL}?size=${size}&page=${page}`,
        keywordType,
        keyword,
        locationId,
        categoryId,
        isUse,
        isPopup,
      ],
      url =>
        common.fetcher(url, {
          keywordType,
          keyword,
          locationId,
          categoryId,
          isUse,
          isPopup,
        }),

      { revalidateOnFocus: false, errorRetryCount: 0 },
    )
  },
  get: (id: number) =>
    axios.get(`${API_URL}/${id}`, {
      headers: common.headers,
    }),
  getWithRelation: (id: number) => axios.get(`${API_URL}/relations/${id}`),
  save: (data: IReserveItem) =>
    axios.post(API_URL, data, {
      headers: common.headers,
    }),
  update: (id: number, data: IReserveItem) =>
    axios.put(`${API_URL}/${id}`, data, {
      headers: common.headers,
    }),
  updateUse: (id: number, isUse: boolean) =>
    axios.put(`${API_URL}/${id}/${isUse}`, null, {
      headers: common.headers,
    }),
  getCode: (codeId: string) =>
    axios.get(CODE_API_URL(codeId), {
      headers: common.headers,
    }),
}
