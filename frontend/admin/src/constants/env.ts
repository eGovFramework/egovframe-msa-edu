export const DEV = process.env.NODE_ENV !== 'production'
export const PORT = process.env.PORT || '3000'
export const PROXY_HOST = process.env.PROXY_HOST || `http://localhost:${PORT}`

export const TZ = process.env.TZ || 'Asia/Seoul'

export const SERVER_API_URL = process.env.SERVER_API_URL

export const CLAIM_NAME = process.env.CLAIM_NAME || 'Authorization'
export const AUTH_USER_ID = process.env.AUTH_USER_ID || 'token-id'
export const REFRESH_TOKEN = process.env.REFRESH_TOKEN || 'refresh-token'
export const ACCESS_TOKEN = process.env.ACCESS_TOKEN || 'access-token'

export const SITE_ID = process.env.SITE_ID
