import { PROXY_HOST } from './env'

export const DRAWER_WIDTH = 220

export const GRID_ROW_HEIGHT = 40

export const GRID_PAGE_SIZE = 10

export const GRID_ROWS_PER_PAGE_OPTION = [10, 20, 50, 100]

export const DEFAULT_ERROR_MESSAGE = 'Sorry.. Something Wrong...😱'

export const DEFAULT_APP_NAME = 'MSA Admin Template'

export const EDITOR_LOAD_IMAGE_URL = '/portal-service/api/v1/images/editor/'

// .htm, .html, .txt, .png/.jpg/etc, .pdf, .xlsx. .xls
export const DEFAULT_ACCEPT_FILE_EXT =
  'text/html, text/plain, image/*, .pdf, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel'

export const BASE_URL = `${PROXY_HOST}/server`

export const ADMIN_LOGO_PATH = '/images/adminLogo.png'

export const ADMIN_LOGO_TEXT = 'MSA Admin'

export const CUSTOM_HEADER_SITE_ID_KEY = 'X-Site-Id'

export const ACCESS_LOG_TIMEOUT = 30 * 60 * 1000

export const ACCESS_LOG_ID = 'accessLogId'

export const PUBLIC_PAGES = ['/404', '/', '/reload', '/_error']
