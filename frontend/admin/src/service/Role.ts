import axios, { AxiosError } from 'axios'
import useSWR from 'swr'
import { Page } from '@utils'
import { common, SearchPayload } from './common'

/**
 * 사용자 서비스 권한 API URL
 */
const ROLE_URL = '/user-service/api/v1/roles'

export interface IRole {
  createdDate?: Date
  roleContent?: string
  roleId: string
  roleName: string
}

// eslint-disable-next-line import/prefer-default-export
export const roleService = {
  search: ({ keywordType, keyword, size, page }: SearchPayload) =>
    // eslint-disable-next-line react-hooks/rules-of-hooks
    useSWR<Page, AxiosError>(
      [`${ROLE_URL}?size=${size}&page=${page}`, keywordType, keyword],
      url => common.fetcher(url, { keywordType, keyword }),
      { revalidateOnFocus: false, errorRetryCount: 0 },
    ),
  searchAll: () => axios.get(`${ROLE_URL}/all`),
}
