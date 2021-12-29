import ActiveLink from '@components/ActiveLink'
import { LoginForm, loginFormType } from '@components/Auth'
import {
  GoogleLoginButton,
  KakaoLoginButton,
  NaverLoginButton,
} from '@components/Buttons'
import CustomConfirm, { CustomConfirmPrpps } from '@components/CustomConfirm'
import Loader from '@components/Loader'
import {
  GOOGLE_CLIENT_ID,
  KAKAO_JAVASCRIPT_KEY,
  NAVER_CLIENT_ID,
  SOCIAL_LOGIN_ENABLED,
} from '@constants/env'
import useUser from '@hooks/useUser'
import { ILogin, loginSerivce } from '@service'
import { userAtom } from '@stores'
import Router, { useRouter } from 'next/router'
import React, { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useRecoilValue } from 'recoil'

interface AlertProps extends CustomConfirmPrpps {
  message: string
}

const Login = () => {
  const { t } = useTranslation()
  const router = useRouter()

  const { isLogin, mutate } = useUser()
  const user = useRecoilValue(userAtom)

  const [customConfirm, setCustomConfirm] = useState<AlertProps | null>(null)
  const [errorState, setErrorState] = useState<string | null>(null)
  const [kakaoLoginMode, setKakaoLoginMode] = useState<string | null>(null)

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
      if (error === 'join') {
        setCustomConfirm({
          open: true,
          message: t('msg.confirm.join.social'),
          handleConfirm: () => {
            setCustomConfirm({
              ...customConfirm,
              open: false,
            })

            // recoil 쓰려했는데 회원가입에 스탭이 있어서 진행중에 새로고침하면 상태가 삭제되면서 일반회원으로 가입될 수 있어서 소셜 정보를 파라미터로 넘김
            router.push(
              `/auth/join?provider=${data.provider}&token=${data.token}`,
            )
          },
          handleCancel: () => {
            if (data.provider === 'kakao') {
              setKakaoLoginMode('logout')
            }
            setCustomConfirm({ ...customConfirm, open: false })
          },
        })
      } else {
        setErrorState(t('err.user.login'))
      }
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
      {/** 소셜 로그인을 사용하는 경우 enabled 됨. */}
      {SOCIAL_LOGIN_ENABLED === 'true' && (
        <article>
          <h3>
            <span>{t('label.title.login.oauth')}</span>
          </h3>
          <div>
            {KAKAO_JAVASCRIPT_KEY && (
              <KakaoLoginButton
                handleClick={handleKakaoLogin}
                kakaoLoginMode={kakaoLoginMode}
                setKakaoLoginMode={setKakaoLoginMode}
              />
            )}
            {NAVER_CLIENT_ID && (
              <NaverLoginButton handleClick={handleNaverLogin} />
            )}
            {GOOGLE_CLIENT_ID && (
              <GoogleLoginButton handleClick={handleGoogleLogin} />
            )}
          </div>
        </article>
      )}
      {customConfirm && (
        <CustomConfirm
          handleConfirm={customConfirm.handleConfirm}
          handleCancel={customConfirm.handleCancel}
          contentText={customConfirm.message}
          open={customConfirm.open}
        />
      )}
    </section>
  )
}

export default Login
