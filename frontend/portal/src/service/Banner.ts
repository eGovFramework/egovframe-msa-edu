import { SITE_ID } from '@constants/env'
import axios from 'axios'

/**
 * 포털 서비스 배너 API URL
 */
const BANNER_URL = `/portal-service/api/v1/${SITE_ID}/banners`

export interface IMainBanner {
  [key: string]: IBanner[]
}

export interface IBanner {
  attachmentCode: string
  bannerContent: string
  bannerNo: number
  bannerTitle: string
  bannerTypeCode: string
  uniqueId: string
  newWindowAt: boolean
  urlAddr: string
}

/**
 * 배너 관리 서비스
 */
export const bannerService = {
  getBanners: (bannerTypeCodes: string[], bannerCount: number) => {
    return axios.get(`${BANNER_URL}/${bannerTypeCodes}/${bannerCount}`)
  },
}
