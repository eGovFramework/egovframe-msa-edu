import { Layout } from '@components/Layout'
import Loader from '@components/Loader'
import LoginLayout from '@components/LoginLayout'
import Wrapper from '@components/Wrapper'
import {
  ACCESS_LOG_ID,
  ACCESS_LOG_TIMEOUT,
  DEFAULT_APP_NAME,
  DEFAULT_ERROR_MESSAGE,
  PUBLIC_PAGES,
} from '@constants'
import { SITE_ID } from '@constants/env'
import useUser from '@hooks/useUser'
import { getCurrentDate } from '@libs/date'
import { common, statisticsService } from '@service'
import {
  currentMenuStateAtom,
  flatMenusSelect,
  ISideMenu,
  menuStateAtom,
} from '@stores'
import axios from 'axios'
import { NextComponentType, NextPageContext } from 'next'
import Head from 'next/head'
import { useRouter } from 'next/router'
import { useSnackbar } from 'notistack'
import React, { useCallback, useEffect } from 'react'
import { useCookies } from 'react-cookie'
import { useRecoilState, useRecoilValue } from 'recoil'
import { SWRConfig } from 'swr'
import { v4 as uuidv4 } from 'uuid'

export type AppProps = {
  component: NextComponentType<any, any, any>
  pathname?: string
  req?: NextPageContext['req']
}

const App = ({ component: Component, ...pageProps }: AppProps) => {
  const router = useRouter()
  const pathname = router.pathname
  const authLayout = pathname.startsWith('/auth/')
  const isUnAuthPage = pathname !== undefined && authLayout

  const { user, loading, isLogin, loggedOut } = useUser()

  const [menus, setMenus] = useRecoilState(menuStateAtom)
  const [currentMenu, setCurrentMenu] = useRecoilState(currentMenuStateAtom)
  const flatMenus = useRecoilValue(flatMenusSelect)

  const { enqueueSnackbar } = useSnackbar()

  const [cookies, setCookie] = useCookies([ACCESS_LOG_ID])

  // access log
  useEffect(() => {
    if (!authLayout) {
      const date = getCurrentDate()
      date.setTime(date.getTime() + ACCESS_LOG_TIMEOUT)
      if (cookies[ACCESS_LOG_ID]) {
        setCookie(ACCESS_LOG_ID, cookies[ACCESS_LOG_ID], {
          path: '/',
          expires: date,
        })
      } else {
        const uuid = uuidv4()
        setCookie(ACCESS_LOG_ID, uuid, { path: '/', expires: date })
        try {
          statisticsService.save(uuid)
        } catch (error) {
          console.error('access log save error', error)
        }
      }
    }
  }, [router])

  useEffect(() => {
    if (loggedOut) {
      router.replace('/auth/login')
    }
  }, [loggedOut])

  useEffect(() => {
    if (!loading && !isUnAuthPage && user === undefined) {
      router.replace('/auth/login')
    }
  }, [user, isUnAuthPage, pathname, loading])

  useEffect(() => {
    if (isLogin) {
      const getMenus = async () => {
        const menu = await axios.get(
          `/portal-service/api/v1/menu-roles/${SITE_ID}`,
          {
            headers: common.headers,
          },
        )
        if (menu) {
          setMenus(menu.data)
        }
      }

      getMenus()
    }
  }, [isLogin])

  //current menu
  const findCurrent = useCallback(
    (path: string) => {
      return flatMenus.find(item => item.urlPath === path)
    },
    [menus, pathname],
  )

  useEffect(() => {
    if (!isUnAuthPage) {
      let current: ISideMenu | undefined = undefined
      let paths = router.asPath
      while (true) {
        current = findCurrent(paths)
        paths = paths.substring(0, paths.lastIndexOf('/'))
        if (current || paths.length < 1) {
          break
        }
      }
      // 권한 없는 페이지 대해 호출이 있으면 404로 redirect
      if (flatMenus.length > 0 && !current) {
        if (!PUBLIC_PAGES.includes(router.asPath)) {
          router.push('/404')
        }
      }

      setCurrentMenu(current)
    }
  }, [pathname, menus])

  if (loading) {
    return <Loader />
  }

  if (!isUnAuthPage && user == null) {
    return null
  }

  if (!isUnAuthPage && !user) {
    return null
  }

  if (!isUnAuthPage && !(currentMenu || PUBLIC_PAGES.includes(router.asPath))) {
    return null
  }

  return (
    <>
      <Head>
        <title>{currentMenu?.korName || DEFAULT_APP_NAME}</title>
      </Head>
      {pathname !== undefined && authLayout ? (
        <LoginLayout>
          <Wrapper>
            <Component pathname={pathname} {...pageProps} />
          </Wrapper>
        </LoginLayout>
      ) : (
        <Layout>
          <SWRConfig
            value={{
              onError: (error, key) => {
                let message: string
                if (error.response) {
                  message = error.response.data.message || DEFAULT_ERROR_MESSAGE
                } else {
                  message = DEFAULT_ERROR_MESSAGE
                }

                enqueueSnackbar(message, {
                  variant: 'error',
                  key,
                })
              },
            }}
          >
            <Wrapper>
              <Component pathname={pathname} {...pageProps} />
            </Wrapper>
          </SWRConfig>
        </Layout>
      )}
    </>
  )
}

export default App
