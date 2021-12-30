import {
  GoogleLoginButton,
  KakaoLoginButton,
  NaverLoginButton,
} from '@components/Buttons'
import CustomConfirm, { CustomConfirmPrpps } from '@components/CustomConfirm'
import { PasswordConfirm } from '@components/Password'
import { IVerification, userService } from '@service'
import { errorStateSelector, userAtom } from '@stores'
import { useRouter } from 'next/router'
import React, { useState } from 'react'
import { useForm } from 'react-hook-form'
import { useTranslation } from 'react-i18next'
import { useRecoilValue, useSetRecoilState } from 'recoil'

interface IUserForm {
  currentPassword: string
}

interface AlertProps extends CustomConfirmPrpps {
  message: string
}

const UserLeave = () => {
  const router = useRouter()
  const { t } = useTranslation()

  const user = useRecoilValue(userAtom)

  const [customConfirm, setCustomConfirm] = useState<AlertProps | null>(null)
  const setErrorState = useSetRecoilState(errorStateSelector)

  // form hook
  const methods = useForm<IUserForm>({
    defaultValues: {
      currentPassword: '',
    },
  })
  const { control, handleSubmit, formState } = methods

  // 탈퇴 처리
  const leave = async (data: IVerification) => {
    try {
      const result = await userService.leave(data)
      if (result === true) {
        router.push('/auth/logout?redirect=/user/leave/bye')
      } else {
        throw new Error(t('err.internal.server'))
      }
    } catch (error) {
      setErrorState({ error })
    }
  }

  const leaveConfirm = (data: IVerification) => {
    setCustomConfirm({
      open: true,
      message: t('msg.confirm.leave'),
      handleConfirm: () => {
        setCustomConfirm({
          ...customConfirm,
          open: false,
        })

        leave(data)
      },
      handleCancel: () => {
        setCustomConfirm({ ...customConfirm, open: false })
      },
    })
  }

  // 탈퇴 클릭
  const handleLeave = async (data: IUserForm) => {
    const { currentPassword } = data

    try {
      const result = await userService.matchPassword(currentPassword)
      if (result === true) {
        leaveConfirm({
          provider: 'password',
          password: currentPassword,
        })
      } else {
        throw new Error(t('err.user.password.notmatch'))
      }
    } catch (error) {
      setErrorState({ error })
    }
  }

  // 카카오 로그인
  const handleKakaoLogin = async response => {
    if (response.response?.access_token) {
      await leave({
        provider: 'kakao',
        token: response.response.access_token,
      })
    } else {
      setErrorState({ message: t('err.user.login.social') })
    }
  }

  // 네이버 로그인
  const handleNaverLogin = async response => {
    if (response.accessToken) {
      await leave({
        provider: 'naver',
        token: response.accessToken,
      })
    } else {
      setErrorState({ message: t('err.user.login.social') })
    }
  }

  // 구글 로그인
  const handleGoogleLogin = async response => {
    if (response.tokenId) {
      await leave({
        provider: 'google',
        token: response.tokenId,
      })
    } else {
      setErrorState({ message: t('err.user.login.social') })
    }
  }

  const handleList = () => {
    router.push('/')
  }

  return (
    <>
      <form>
        <article className="mypage">
          <p>
            {t('label.text.user.leave1')}
            <br />
            {t('label.text.user.leave2')}
          </p>
          <div className="guide">
            <h4>{t('label.title.guide')}</h4>
            <ul>
              <li>{t('label.text.user.leave.guide1')}</li>
              <li>{t('label.text.user.leave.guide2')}</li>
              <li>{t('label.text.user.leave.guide3')}</li>
            </ul>
          </div>
          {user?.hasPassword === true && (
            <>
              <p>{t('label.text.user.leave.password')}</p>
              <PasswordConfirm
                control={control}
                formState={formState}
                handleCheckPassword={handleSubmit(handleLeave)}
                handleList={handleList}
              />
            </>
          )}
          {user?.isSocialUser === true && (
            <>
              <h3>
                <span>{t('label.title.oauth')}</span>
              </h3>
              <div className="btn_social">
                {user?.kakaoId && (
                  <KakaoLoginButton
                    handleClick={handleKakaoLogin}
                    confirmMessage={t('msg.confirm.leave')}
                  />
                )}
                {user?.naverId && (
                  <NaverLoginButton
                    handleClick={handleNaverLogin}
                    confirmMessage={t('msg.confirm.leave')}
                  />
                )}
                {user?.googleId && (
                  <GoogleLoginButton
                    handleClick={handleGoogleLogin}
                    confirmMessage={t('msg.confirm.leave')}
                  />
                )}
              </div>
            </>
          )}
        </article>
        {customConfirm && (
          <CustomConfirm
            handleConfirm={customConfirm.handleConfirm}
            handleCancel={customConfirm.handleCancel}
            contentText={customConfirm.message}
            open={customConfirm.open}
          />
        )}
      </form>
    </>
  )
}

export default UserLeave
