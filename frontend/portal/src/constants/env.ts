export const DEV = process.env.NODE_ENV !== 'production'

export const PORT = process.env.PORT
export const PROXY_HOST = process.env.PROXY_HOST || `http://localhost:${PORT}`

export const TZ = process.env.TZ || 'Asia/Seoul'

export const MODE = process.env.MODE
export const ASSET_PATH = `/styles/${MODE}`

export const SERVER_API_URL = process.env.SERVER_API_URL

export const CLAIM_NAME = process.env.CLAIM_NAME || 'Authorization'
export const AUTH_USER_ID = process.env.AUTH_USER_ID || 'token-id'
export const REFRESH_TOKEN = process.env.REFRESH_TOKEN || 'refresh-token'
export const ACCESS_TOKEN = process.env.ACCESS_TOKEN || 'access-token'

export const SITE_ID = process.env.SITE_ID

export const GOOGLE_CLIENT_ID = process.env.NEXT_PUBLIC_GOOGLE_CLIENT_ID
export const KAKAO_JAVASCRIPT_KEY = process.env.NEXT_PUBLIC_KAKAO_JAVASCRIPT_KEY
export const NAVER_CLIENT_ID = process.env.NEXT_PUBLIC_NAVER_CLIENT_ID
export const NAVER_CALLBACK_URL = process.env.NEXT_PUBLIC_NAVER_CALLBACK_URL

export const SOCIAL_LOGIN_ENABLED = process.env.SOCIAL_LOGIN_ENABLED
