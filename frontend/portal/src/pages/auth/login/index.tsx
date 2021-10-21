import ActiveLink from '@components/ActiveLink'
import { LoginForm, loginFormType } from '@components/Auth'
import {
  GoogleLoginButton,
  KakaoLoginButton,
  NaverLoginButton,
} from '@components/Buttons'
import Loader from '@components/Loader'
import useUser from '@hooks/useUser'
import { ILogin, loginSerivce } from '@service'
import { userAtom } from '@stores'
import Router from 'next/router'
import React, { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useRecoilValue } from 'recoil'

const Login = () => {
  const { t } = useTranslation()

  const { isLogin, mutate } = useUser()
  const user = useRecoilValue(userAtom)
  const [errorState, setErrorState] = useState<string | null>(null)

  useEffect(() => {
    if (isLogin && user) {
      Router.replace('/')
    }
  }, [isLogin, user])

  if (isLogin) {
    return <Loader />
  }

  // 로그인 처리
  const login = async (data: ILogin) => {
    try {
      const result = await loginSerivce.login(data)

      if (result === 'success') {
        mutate()
      } else {
        setErrorState(result)
      }
    } catch (error) {
      setErrorState(t('err.user.login'))
    }
  }

  // 이메일 로그인
  const handleLoginSubmit = async (form: loginFormType) => {
    await login({ provider: 'email', ...form })
  }

  // 카카오 로그인
  const handleKakaoLogin = async response => {
    if (response.response?.access_token) {
      setErrorState(null)
      await login({
        provider: 'kakao',
        token: response.response.access_token,
      })
    } else {
      setErrorState('noAuth')
    }
  }

  // 네이버 로그인
  const handleNaverLogin = async response => {
    if (response.accessToken) {
      setErrorState(null)
      await login({
        provider: 'naver',
        token: response.accessToken,
      })
    } else {
      setErrorState('noAuth')
    }
  }

  // 구글 로그인
  const handleGoogleLogin = async response => {
    if (response.tokenId) {
      setErrorState(null)
      await login({
        provider: 'google',
        token: response.tokenId,
      })
    } else {
      setErrorState('noAuth')
    }
  }

  return (
    <section className="login">
      <h2>{t('common.login')}</h2>
      <LoginForm handleLogin={handleLoginSubmit} errorMessage={errorState} />
      <div>
        <ActiveLink href="/auth/join">{t('common.join')}</ActiveLink>
        <ActiveLink href="/auth/find/password">
          {t('login.password_find')}
        </ActiveLink>
      </div>
      <article>
        <h3>
          <span>{t('label.title.login.oauth')}</span>
        </h3>
        <div>
          <KakaoLoginButton handleClick={handleKakaoLogin} />
          <NaverLoginButton handleClick={handleNaverLogin} />
          <GoogleLoginButton handleClick={handleGoogleLogin} />
        </div>
      </article>
    </section>
  )
}

export default Login
