import { PROXY_HOST } from './env'

export const DEFAULT_APP_NAME = '전자정부 표준프레임워크 포탈'

export const BASE_URL = `${PROXY_HOST}/server`

export const DEFAULT_ERROR_MESSAGE = 'Sorry.. Something Wrong...😱'

export const DEFUALT_GRID_PAGE_SIZE = 10

export const GRID_ROWS_PER_PAGE_OPTION = [10, 20, 50, 100]

export const COMMENTS_MAX_LENGTH = 300

export const COMMENTS_PAGE_SIZE = 5

export const EDITOR_LOAD_IMAGE_URL = '/portal-service/api/v1/images/editor/'
export const LOAD_IMAGE_URL = '/portal-service/api/v1/images/'

export const EDITOR_MAX_LENGTH = 2000

// .htm, .html, .txt, .png/.jpg/etc, .pdf, .xlsx. .xls
export const DEFAULT_ACCEPT_FILE_EXT =
  'text/html, text/plain, image/*, .pdf, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel'

export const DEFAULT_ACCEPT_FILE_EXT_TEXT =
  '.htm, .html, .txt, .png/.jpg/etc, .pdf, .xlsx. .xls'

export const CUSTOM_HEADER_SITE_ID_KEY = 'X-Site-Id'

export const ACCESS_LOG_TIMEOUT = 30 * 60 * 1000

export const ACCESS_LOG_ID = 'accessLogId'

export const PUBLIC_PAGES = [
  '/404',
  '/',
  '',
  '/reload',
  '/_error',
  '/user/leave/bye',
  '#',
  '/auth/login/naver',
]
