import axios from 'axios'

/**
 * 개인정보처리방침 데이터 타입
 */
export interface IPrivacy {
  privacyNo: number
  privacyTitle: string
  privacyContent: string
}

/**
 * 포털 서비스 개인정보처리방침 API URL
 */
const PRIVACY_URL = '/portal-service/api/v1/privacies'

/**
 * 개인정보처리방침 관리 서비스
 */
export const privacyService = {
  alluse: async () => axios.get(`${PRIVACY_URL}/all/use`),
}
