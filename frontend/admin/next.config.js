const { i18n } = require('./next-i18next.config')

const withBundleAnalyzer = require('@next/bundle-analyzer')({
  enabled: process.env.ANALYZE === 'true',
})
const withPlugins = require('next-compose-plugins')

const plugins = [[withBundleAnalyzer]]
const serverApiUrl = process.env.SERVER_API_URL || 'http://localhost:8000'
const siteId = process.env.SITE_ID || '1'
const port = process.env.PORT || '3000'

const nextConfig = {
  i18n,
  env: {
    SERVER_API_URL: serverApiUrl,
    PORT: port,
    PROXY_HOST: process.env.PROXY_HOST || `http://localhost:${port}`,
    ENV: process.env.ENV || '-',
    SITE_ID: siteId,
  },
  webpack: (config, { webpack }) => {
    const prod = process.env.NODE_ENV === 'production'
    const newConfig = {
      ...config,
      mode: prod ? 'production' : 'development',
    }
    if (prod) {
      newConfig.devtool = 'hidden-source-map'
    }
    return newConfig
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
module.exports = withPlugins(plugins, nextConfig)
