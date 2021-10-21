import axios from 'axios'

/**
 * 컨텐츠 데이터 타입
 */
export interface IContent {
  contentName: string
  contentRemark: string
  contentValue: string
}

/**
 * 포털 서비스 컨텐츠 API URL
 */
const CONTENT_URL = '/portal-service/api/v1/contents'

/**
 * 컨텐츠 관리 서비스
 */
export const contentService = {
  get: async (contentNo: number) => axios.get(`${CONTENT_URL}/${contentNo}`),
}
