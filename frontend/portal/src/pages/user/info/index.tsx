import {
  GoogleLoginButton,
  KakaoLoginButton,
  NaverLoginButton,
} from '@components/Buttons'
import CustomAlert, { CustomAlertPrpps } from '@components/CustomAlert'
import { PasswordConfirm } from '@components/Password'
import { IUserForm, UserInfoDone, UserInfoModified } from '@components/UserInfo'
import { userService } from '@service'
import { errorStateSelector, userAtom } from '@stores'
import produce from 'immer'
import { useRouter } from 'next/router'
import React, { useEffect, useState } from 'react'
import { useForm } from 'react-hook-form'
import { useTranslation } from 'react-i18next'
import { useRecoilValue, useSetRecoilState } from 'recoil'

interface AlertProps extends CustomAlertPrpps {
  message: string
}

const UserInfo = () => {
  const router = useRouter()
  const { t } = useTranslation()

  const user = useRecoilValue(userAtom)
  const setUser = useSetRecoilState(userAtom)

  const setErrorState = useSetRecoilState(errorStateSelector)

  const [checkedEmail, setCheckedEmail] = useState<boolean>(false)
  const [modified, setModified] = useState<boolean>(false)

  const [customAlert, setCustomAlert] = useState<AlertProps | undefined>({
    open: false,
    message: '',
    handleAlert: () => {},
  })

  // form hook
  const methods = useForm<IUserForm>({
    defaultValues: {
      password: '',
      email: user?.email || '',
      userName: user?.userName || '',
    },
  })
  const { control, handleSubmit, formState, getValues, setFocus } = methods

  useEffect(() => {
    if (user && user.verification) {
      setUser({
        ...user,
        verification: null,
      })
    }
  }, [])

  // 비밀번호 확인
  const handleCheckPassword = async (data: IUserForm) => {
    data = produce(data, draft => {
      draft.password = data.currentPassword
    })
    try {
      const result = await userService.matchPassword(data.currentPassword)
      if (result === true) {
        setUser({
          ...user,
          verification: {
            provider: 'password',
            password: data.currentPassword,
          },
        })
      } else {
        throw new Error(t('err.user.password.notmatch'))
      }
    } catch (error) {
      setErrorState({ error })
    }
  }

  const showMessage = (message: string, callback?: () => void) => {
    setCustomAlert({
      open: true,
      message,
      handleAlert: () => {
        setCustomAlert({
          ...customAlert,
          open: false,
        })
        if (callback) callback()
      },
    })
  }

  // 변경
  const handleUpdate = async (data: IUserForm) => {
    if (user.email === data.email && user.userName === data.userName) {
      showMessage(t('msg.notmodified'))
      return
    }

    if (user.email !== data.email && !checkedEmail) {
      showMessage(t('msg.user.email.check'), () => {
        setFocus('email')
      })
      return
    }

    try {
      const result = await userService.updateInfo(user.userId, {
        ...data,
        ...user.verification,
      })
      if (result) {
        setUser({
          ...user,
          ...data,
        })

        setModified(true)
      } else {
        throw new Error(t('err.internal.server'))
      }
    } catch (error) {
      setErrorState({ error })
    }
  }

  // 메인 이동
  const handleFirst = () => {
    setUser({
      ...user,
      verification: null,
    })
    router.push('/')
  }

  // 카카오 로그인
  const handleKakaoLogin = async response => {
    if (response.profile?.id?.toString() === user.kakaoId) {
      // setVerification({
      //   provider: 'kakao',
      //   token: response.response.access_token,
      // })
      setUser({
        ...user,
        verification: {
          provider: 'kakao',
          token: response.response.access_token,
        },
      })
    } else {
      setErrorState({ message: t('err.user.login.social') })
    }
  }

  // 네이버 로그인
  const handleNaverLogin = async response => {
    if (response.user?.id === user.naverId) {
      // Can't perform a React state update on an unmounted component. This is a no-op, but it indicates a memory leak in your application. To fix, cancel all subscriptions and asynchronous tasks in a useEffect cleanup function.
      // 로그인 페이지에서는 잘되는데 이유를 모르겠다. 구글/카카오는 잘된다. 편법으로 일단 진행
      /* setVerification({
       provider: 'naver',
       token: response.accessToken,
       }) */

      setUser({
        ...user,
        verification: {
          provider: 'naver',
          token: response.accessToken,
        },
      })
    } else {
      setErrorState({ message: t('err.user.login.social') })
    }
  }

  // 구글 로그인
  const handleGoogleLogin = async response => {
    if (response.googleId === user.googleId) {
      // setVerification({
      //   provider: 'google',
      //   token: response.tokenId,
      // })
      setUser({
        ...user,
        verification: {
          provider: 'google',
          token: response.tokenId,
        },
      })
    } else {
      setErrorState({ message: t('err.user.login.social') })
    }
  }

  // 비밀번호 랜더링
  const renderPasswordForm = () => {
    return (
      <article className="mypage">
        <div className="message small">
          <span className="">{t('label.text.required.login')}</span>
        </div>
        {user?.hasPassword === true && (
          <PasswordConfirm
            control={control}
            formState={formState}
            handleCheckPassword={handleSubmit(handleCheckPassword)}
            handleList={handleFirst}
          />
        )}
        {user?.isSocialUser === true && (
          <>
            <h3>
              <span>{t('label.title.oauth')}</span>
            </h3>
            <div className="btn_social">
              {user?.kakaoId && (
                <KakaoLoginButton handleClick={handleKakaoLogin} />
              )}
              {user?.naverId && (
                <NaverLoginButton handleClick={handleNaverLogin} />
              )}
              {user?.googleId && (
                <GoogleLoginButton handleClick={handleGoogleLogin} />
              )}
            </div>
          </>
        )}
      </article>
    )
  }

  return (
    <>
      {modified === false && (
        <form>
          {!user?.verification && renderPasswordForm()}
          {user?.verification && (
            <UserInfoModified
              control={control}
              formState={formState}
              handleUpdate={handleSubmit(handleUpdate)}
              handleList={handleFirst}
              getValues={getValues}
              setFocus={setFocus}
              showMessage={showMessage}
              setCheckedEmail={setCheckedEmail}
            />
          )}
        </form>
      )}
      {modified === true && <UserInfoDone handleList={handleFirst} />}
      <CustomAlert
        contentText={customAlert.message}
        open={customAlert.open}
        handleAlert={customAlert.handleAlert}
      />
    </>
  )
}

export default UserInfo
