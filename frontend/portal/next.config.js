const { i18n } = require('./next-i18next.config')
const { PHASE_DEVELOPMENT_SERVER } = require('next/constants')
const { loadEnvConfig } = require('@next/env')
loadEnvConfig('./', process.env.NODE_ENV !== 'production')

const port = process.env.PORT || 3000
const serverApiUrl = process.env.SERVER_API_URL || 'http://localhost:8000'
const siteId = process.env.SITE_ID || '3'
const mode = siteId === '2' ? 'lg' : siteId === '3' ? 'sm' : 'sm'
const socialLoginEnabled = process.env.SOCIAL_LOGIN_ENABLED || 'false'

module.exports = {
  i18n,
  env: {
    PORT: port,
    PROXY_HOST: process.env.PROXY_HOST || `http://localhost:${port}`,
    MODE: mode,
    SERVER_API_URL: serverApiUrl,
    SITE_ID: siteId,
    SOCIAL_LOGIN_ENABLED: socialLoginEnabled,
  },
  async rewrites() {
    return [
      {
        source: '/server/:path*',
        destination: `${serverApiUrl}/:path*`,
      },
    ]
  },
}
