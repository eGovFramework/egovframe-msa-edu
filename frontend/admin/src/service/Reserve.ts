import { common, SearchPayload } from '@service'
import { Page } from '@utils'
import axios, { AxiosError, AxiosResponse } from 'axios'
import {
  Control,
  FormState,
  UseFormGetValues,
  UseFormRegister,
  UseFormSetValue,
} from 'react-hook-form'
import useSWR from 'swr'
import { IReserveItemRelation } from './ReserveItem'

const API_URL = '/reserve-check-service/api/v1/reserves'

export interface IReserve {
  reserveId: string
  reserveItemId: number
  reserveItem: IReserveItemRelation
  reserveQty: number
  reserveStartDate: Date
  reserveEndDate: Date
  reservePurposeContent: string
  attachmentCode: string
  reserveStatusId: string
  userId: string
  userName: string
  userContactNo: string
  userEmail: string
}

export interface ReserveSavePayload {
  reserveItemId: number
  locationId: number
  categoryId: string
  reserveQty: number
  reservePurposeContent: string
  attachmentCode: string
  reserveStartDate: Date
  reserveEndDate: Date
  reserveStatusId: string
  userId: string
  userContactNo: string
  userEmail: string
}

export interface ReserveFormProps {
  control: Control<IReserve, object>
  formState: FormState<IReserve>
  register?: UseFormRegister<IReserve>
  getValues?: UseFormGetValues<IReserve>
  setValue?: UseFormSetValue<IReserve>
}

interface ReserveSearchPayload extends SearchPayload {
  locationId?: string
  categoryId?: string
}

export const reserveService = {
  search: ({
    keywordType,
    keyword,
    size,
    page,
    locationId,
    categoryId,
  }: ReserveSearchPayload) =>
    useSWR<Page, AxiosError>(
      [
        `${API_URL}?size=${size}&page=${page}`,
        keywordType,
        keyword,
        locationId,
        categoryId,
      ],
      url => common.fetcher(url, { keywordType, keyword }),
      { revalidateOnFocus: false, errorRetryCount: 0 },
    ),
  get: (reserveId: string) =>
    axios.get<any, AxiosResponse<IReserve>>(`${API_URL}/${reserveId}`),
  save: (data: ReserveSavePayload) => axios.post(API_URL, data),
  update: (reserveId: string, data: ReserveSavePayload) =>
    axios.put(`${API_URL}/${reserveId}`, data),
  cancel: (reserveId: string, reason: string) =>
    axios.put(`${API_URL}/cancel/${reserveId}`, {
      reasonCancelContent: reason,
    }),
  approve: (reserveId: string) => axios.put(`${API_URL}/approve/${reserveId}`),
  getInventories: (reserveItemId: number, startDate: string, endDate: string) =>
    axios.get(
      `${API_URL}/${reserveItemId}/inventories?startDate=${startDate}&endDate=${endDate}`,
    ),
}
