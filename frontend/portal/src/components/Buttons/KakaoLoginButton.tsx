import React, { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import KakaoLogin from 'react-kakao-login'
import { KAKAO_JAVASCRIPT_KEY } from '@constants/env'
import { ISocialButton } from '@components/Buttons/GoogleLoginButton'
import CustomConfirm, { CustomConfirmPrpps } from '@components/CustomConfirm'

export interface ISocialKakaoButton extends ISocialButton {
  kakaoLoginMode?: string
  setKakaoLoginMode?: any
}

const KakaoLoginButton = (props: ISocialKakaoButton) => {
  const { handleClick, confirmMessage, kakaoLoginMode, setKakaoLoginMode } = props
  const { t } = useTranslation()

  const [customConfirm, setCustomConfirm] = useState<CustomConfirmPrpps>({
    open: false,
    handleConfirm: () => {},
    handleCancel: () => {},
  })

  useEffect(() => {
    // 라이브러리에서 로그인 상태를 유지하고 바꿀 수 없어서 이런 코드를..
    if (kakaoLoginMode !== 'logout' || !document || !document.querySelector('#kakaoIdLogin')) {
      return
    }

    const kakaoLoginButton = document.querySelector('#kakaoIdLogin')

    // @ts-ignore
    kakaoLoginButton.href = 'javascript:void(0);'

    // @ts-ignore
    kakaoLoginButton.click()

    setKakaoLoginMode(null)
  }, [kakaoLoginMode])

  return (
    <>
      <KakaoLogin
        token={KAKAO_JAVASCRIPT_KEY}
        onSuccess={handleClick}
        onFail={handleClick}
        render={(_props: any) => (
          <a
            href="#"
            id="kakaoIdLogin"
            className="social kakao"
            onClick={event => {
              event.preventDefault()

              if (confirmMessage) {
                setCustomConfirm({
                  open: true,
                  contentText: confirmMessage,
                  handleConfirm: () => {
                    setCustomConfirm({
                      open: false,
                      handleConfirm: () => {},
                      handleCancel: () => {},
                    })

                    _props.onClick(event)
                  },
                  handleCancel: () => {
                    setCustomConfirm({
                      open: false,
                      handleConfirm: () => {},
                      handleCancel: () => {},
                    })
                  },
                } as CustomConfirmPrpps)
              } else {
                _props.onClick(event)
              }
            }}
          >
            {t('label.text.kakao')}
          </a>
        )}
      />
      {customConfirm && (
        <CustomConfirm
          handleConfirm={customConfirm.handleConfirm}
          handleCancel={customConfirm.handleCancel}
          contentText={customConfirm.contentText}
          open={customConfirm.open}
        />
      )}
    </>
  )
}

export { KakaoLoginButton }
