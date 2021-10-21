import axios from 'axios'
import { common } from './common'

const STATISTICS_API = `/portal-service/api/v1/statistics`

export const statisticsService = {
  save: (uuid: string) => {
    return axios.post(`${STATISTICS_API}/${uuid}`, {
      Headers: common.headers,
    })
  },
}
