import React, { useState } from 'react'
import { useTranslation } from 'react-i18next'
import GoogleLogin from 'react-google-login'
import { GOOGLE_CLIENT_ID } from '@constants/env'
import CustomConfirm, { CustomConfirmPrpps } from '@components/CustomConfirm'

export interface ISocialButton {
  handleClick?: (response: any) => void
  confirmMessage?: string
}

const GoogleLoginButton = (props: ISocialButton) => {
  const { handleClick, confirmMessage } = props
  const { t } = useTranslation()

  const [customConfirm, setCustomConfirm] = useState<CustomConfirmPrpps>({
    open: false,
    handleConfirm: () => {},
    handleCancel: () => {},
  })

  return (
    <>
      <GoogleLogin
        clientId={GOOGLE_CLIENT_ID}
        render={(_props: any) => (
          <a
            href="#"
            className="social google"
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
            {t('label.text.google')}
          </a>
        )}
        onSuccess={handleClick}
        onFailure={handleClick}
        cookiePolicy="single_host_origin"
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

export { GoogleLoginButton }
