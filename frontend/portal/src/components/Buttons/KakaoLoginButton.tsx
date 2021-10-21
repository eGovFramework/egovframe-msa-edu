import React, { useState } from 'react'
import { useTranslation } from 'react-i18next'
import KakaoLogin from 'react-kakao-login'
import { KAKAO_JAVASCRIPT_KEY } from '@constants/env'
import { ISocialButton } from '@components/Buttons/GoogleLoginButton'
import CustomConfirm, { CustomConfirmPrpps } from '@components/CustomConfirm'

const KakaoLoginButton = (props: ISocialButton) => {
  const { handleClick, confirmMessage } = props
  const { t } = useTranslation()

  const [customConfirm, setCustomConfirm] = useState<CustomConfirmPrpps>({
    open: false,
    handleConfirm: () => {},
    handleCancel: () => {},
  })

  return (
    <>
      <KakaoLogin
        token={KAKAO_JAVASCRIPT_KEY}
        onSuccess={handleClick}
        onFail={handleClick}
        render={(_props: any) => (
          <a
            href="#"
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
