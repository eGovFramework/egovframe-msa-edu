import React, { useEffect, useRef, useState } from 'react'
import { NextPageContext } from 'next'
import Head from 'next/head'
import { AppContext, AppProps } from 'next/app'
import { ThemeProvider } from '@material-ui/core/styles'
import CssBaseline from '@material-ui/core/CssBaseline'
import { Theme } from '@material-ui/core/styles'
import { RecoilRoot } from 'recoil'
import { SnackbarProvider } from 'notistack'

import theme from '@styles/theme'
import darkTheme from '@styles/darkTheme'
import App from '@components/App/App'
import axios from 'axios'
import '@libs/i18n'
import { appWithTranslation, useTranslation } from 'next-i18next'
import { useLocalStorage } from '@hooks/useLocalStorage'
import { SITE_ID } from '@constants/env'
import { BASE_URL, CUSTOM_HEADER_SITE_ID_KEY } from '@constants'
import { CookiesProvider } from 'react-cookie'

import 'react-datepicker/dist/react-datepicker.css'

export type PageProps = {
  pathname?: string
  query?: NextPageContext['query']
  req?: NextPageContext['req']
}

// axios 기본 설정
axios.defaults.headers.common[CUSTOM_HEADER_SITE_ID_KEY] = SITE_ID
axios.defaults.baseURL = BASE_URL
axios.defaults.withCredentials = true

const MyApp = (props: AppProps) => {
  const { Component, pageProps } = props

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

  /**
   * @TODO
   * 테마 선택시 사용 (언제??)
   */
  const [selectTheme, setSelectTheme] = useState<Theme>(theme)

  useEffect(() => {
    // Remove the server-side injected CSS.
    const jssStyles = document.querySelector('#jss-server-side')
    if (jssStyles) {
      jssStyles.parentElement!.removeChild(jssStyles)
    }
  }, [])

  return (
    <RecoilRoot>
      <ThemeProvider theme={selectTheme}>
        <Head>
          <link rel="icon" type="image/x-icon" href="/favicon.ico"></link>
          <meta
            name="viewport"
            content="minimum-scale=1, initial-scale=1, width=device-width"
          />
          <link
            rel="stylesheet"
            href="https://fonts.googleapis.com/icon?family=Material+Icons"
          />
        </Head>
        {/* CssBaseline kickstart an elegant, consistent, and simple baseline to build upon. */}
        <CssBaseline />

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
      </ThemeProvider>
    </RecoilRoot>
  )
}

MyApp.getInitialProps = async (context: AppContext) => {
  const { Component, ctx, router } = context
  let pageProps: PageProps = {}
  const locale = router.locale

  axios.defaults.headers.common[CUSTOM_HEADER_SITE_ID_KEY] = SITE_ID

  if (Component.getInitialProps) {
    const componentInitialProps = await Component.getInitialProps(ctx)
    if (componentInitialProps) {
      pageProps = componentInitialProps
    }
  }

  global.__localeId__ = locale
  pageProps.pathname = ctx.pathname

  return { pageProps }
}

export default appWithTranslation(MyApp)
