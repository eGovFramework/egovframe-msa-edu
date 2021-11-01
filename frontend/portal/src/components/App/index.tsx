import Layout from '@components/Layout'
import Loader from '@components/Loader'
import Wrapper from '@components/Wrapper'
import {
  ACCESS_LOG_ID,
  ACCESS_LOG_TIMEOUT,
  DEFAULT_ERROR_MESSAGE,
  PUBLIC_PAGES,
} from '@constants'
import useMounted from '@hooks/useMounted'
import useUser from '@hooks/useUser'
import { menuService, statisticsService } from '@service'
import {
  currentMenuStateAtom,
  flatMenusSelect,
  ISideMenu,
  menuStateAtom,
  userAtom,
} from '@stores'
import { NextComponentType, NextPageContext } from 'next'
import { useRouter } from 'next/router'
import { useSnackbar } from 'notistack'
import React, { useCallback, useEffect } from 'react'
import { useCookies } from 'react-cookie'
import { useRecoilState, useRecoilValue, useSetRecoilState } from 'recoil'
import { SWRConfig } from 'swr'
import { v4 as uuidv4 } from 'uuid'

type AppProps = {
  component: NextComponentType<any, any, any>
  pathname?: string
  req?: NextPageContext['req']
}

const App = ({ component: Component, ...pageProps }: AppProps) => {
  const router = useRouter()
  const pathname = router.pathname
  const authPage = pathname?.startsWith('/auth/')
  const naverLoginCallbackPage = pathname?.startsWith('/auth/login/naver')
  const errorPage = router.pathname === '/404' || router.pathname === '/_error'

  const { enqueueSnackbar } = useSnackbar()

  const { loading } = useUser()

  const user = useRecoilValue(userAtom)
  const setMenus = useSetRecoilState(menuStateAtom)
  const [currentMenu, setCurrentMenus] = useRecoilState(currentMenuStateAtom)
  const flatMenus = useRecoilValue(flatMenusSelect)
  const mounted = useMounted()
  const { data, mutate } = menuService.getMenus()

  const [cookies, setCookie] = useCookies([ACCESS_LOG_ID])

  // access log
  useEffect(() => {
    if (!errorPage) {
      const date = new Date()
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
    if (!loading) {
      mutate()
    }
  }, [user])

  useEffect(() => {
    if (data) {
      setMenus(data)
    }
  }, [data])

  //current menu
  const findCurrent = useCallback(
    (path: string) => {
      return flatMenus.find(item => item.urlPath === path)
    },
    [flatMenus, pathname],
  )

  useEffect(() => {
    if (mounted && data?.length > 0 && flatMenus?.length > 0) {
      let path =
        router.asPath.indexOf('?') === -1
          ? router.asPath
          : router.asPath.substring(0, router.asPath.indexOf('?'))
      let current: ISideMenu | undefined = undefined
      while (true) {
        current = findCurrent(path)
        path = path.substring(0, path.lastIndexOf('/'))
        if (current || path.length < 1) {
          break
        }
      }

      // 권한 없는 페이지 대해 호출이 있으면 404로 redirect
      if (!authPage && !current) {
        if (!PUBLIC_PAGES.includes(router.asPath)) {
          router.push('/404')
        }
      }

      setCurrentMenus(current)
    }
  }, [router, mounted, flatMenus, data])

  if (loading) {
    return <Loader />
  }

  if (!authPage && !(currentMenu || PUBLIC_PAGES.includes(router.asPath))) {
    return null
  }

  if (data?.length <= 0 || flatMenus?.length <= 0) {
    return <Loader />
  }

  return errorPage || naverLoginCallbackPage ? (
    <Wrapper>
      <Component {...pageProps} />
    </Wrapper>
  ) : (
    <Layout main={pathname === '/'} isLeft={!!currentMenu}>
      <SWRConfig
        value={{
          onError: (error, key) => {
            if (key !== '/user-service/api/v1/users') {
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
            }
          },
        }}
      >
        <Wrapper>
          <Component {...pageProps} />
        </Wrapper>
      </SWRConfig>
    </Layout>
  )
}

export default App
