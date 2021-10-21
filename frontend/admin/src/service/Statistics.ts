import { SITE_ID } from '@constants/env'
import axios, { AxiosError } from 'axios'
import useSWR from 'swr'
import { common } from './common'

const STATISTICS_API = `/portal-service/api/v1/statistics`

export interface IBarChartData {
  year: number
  month: number
  day: number
  x: string
  y: number
}

export interface DailyPayload {
  year: number
  month: number
}

export const statisticsService = {
  getMonthly: (siteId: number) => {
    const { data, mutate } = useSWR<IBarChartData[], AxiosError>(
      `${STATISTICS_API}/monthly/${siteId}`,
      url => common.fetcher(url, {}),
      { revalidateOnFocus: false, errorRetryCount: 0 },
    )

    return {
      monthly: data,
      monthlyMutate: mutate,
    }
  },
  getDaily: (siteId: number, payload: DailyPayload) => {
    const { data, mutate } = useSWR<IBarChartData[], AxiosError>(
      `${STATISTICS_API}/daily/${siteId}?year=${payload.year}&month=${payload.month}`,
      url => common.fetcher(url, {}),
      { revalidateOnFocus: false, errorRetryCount: 0 },
    )

    return {
      daily: data,
      dailyMutate: mutate,
    }
  },
  getSites: () => {
    return axios.get(`/portal-service/api/v1/sites`)
  },
  save: (uuid: string) => {
    return axios.post(`${STATISTICS_API}/${uuid}`, {
      Headers: common.headers,
    })
  },
}
