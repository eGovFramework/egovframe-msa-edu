import React, { useEffect } from 'react'
import axios from 'axios'
import Head from 'next/head'
import { AppContext, AppProps } from 'next/app'
import { appWithTranslation, useTranslation } from 'next-i18next'
import '@libs/i18n'
import { RecoilRoot } from 'recoil'
import App from '@components/App'
import GlobalStyles from '@components/App/GlobalStyles'
import {
  BASE_URL,
  CUSTOM_HEADER_SITE_ID_KEY,
  DEFAULT_APP_NAME,
} from '@constants'
import { useLocalStorage } from '@hooks/useLocalStorage'
import { SnackbarProvider } from 'notistack'
import { ThemeProvider } from '@material-ui/core/styles'
import theme from '@styles/theme'
import { SITE_ID } from '@constants/env'
import { CookiesProvider } from 'react-cookie'

// axios 기본 설정
axios.defaults.headers.common[CUSTOM_HEADER_SITE_ID_KEY] = SITE_ID
axios.defaults.baseURL = BASE_URL
axios.defaults.withCredentials = true

const MyApp = ({ Component, pageProps }: AppProps) => {
  /**
   * locales
   */

  const { i18n } = useTranslation()
  const [storedValue, setValue] = useLocalStorage('locale', i18n.language)
  useEffect(() => {
    if (storedValue !== i18n.language) {
      i18n.changeLanguage(storedValue)
    }
  }, [i18n, storedValue])

  useEffect(() => {
    // Remove the server-side injected CSS.
    const jssStyles = document.querySelector('#jss-server-side')
    if (jssStyles) {
      jssStyles.parentElement!.removeChild(jssStyles)
    }
  }, [])

  return (
    <RecoilRoot>
      <ThemeProvider theme={theme}>
        <GlobalStyles>
          <Head>
            <link rel="icon" type="image/x-icon" href="/favicon.ico"></link>
            <meta
              name="viewport"
              content="user-scalable=no, width=device-width, initial-scale=1"
            />
            <meta httpEquiv="X-UA-Compatible" content="IE=edge" />
            <title>{DEFAULT_APP_NAME}</title>
          </Head>
          <SnackbarProvider
            maxSnack={3}
            iconVariant={{
              success: '✅ ',
              error: '✖ ',
              warning: '⚠ ',
              info: 'ℹ️ ',
            }}
            autoHideDuration={2000}
            preventDuplicate={true}
          >
            <CookiesProvider>
              <App component={Component} {...pageProps} />
            </CookiesProvider>
          </SnackbarProvider>
        </GlobalStyles>
      </ThemeProvider>
    </RecoilRoot>
  )
}

MyApp.getInitialProps = async ({ Component, ctx, router }: AppContext) => {
  let pageProps = {}
  const locale = router.locale

  axios.defaults.headers.common[CUSTOM_HEADER_SITE_ID_KEY] = SITE_ID

  if (Component.getInitialProps) {
    const componentInitialProps = await Component.getInitialProps(ctx)
    if (componentInitialProps) {
      pageProps = componentInitialProps
    }
  }

  global.__localeId__ = locale

  return { pageProps }
}

export default appWithTranslation(MyApp)
