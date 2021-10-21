import axios from 'axios'

export interface IPolicy {
  id: number
  type: string
  title: string
  isUse: boolean
  regDate: Date
  contents: string
}

const POLICY_API = '/portal-service/api/v1/policies'

/**
 * 이용약관 관리 서비스
 */
export const policyService = {
  url: POLICY_API,
  getLatest: (type: string) => {
    return axios.get(`${POLICY_API}/latest/${type}`)
  },
}
