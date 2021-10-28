import { ISocialButton } from '@components/Buttons/GoogleLoginButton'
import CustomConfirm, { CustomConfirmPrpps } from '@components/CustomConfirm'
import { NAVER_CALLBACK_URL, NAVER_CLIENT_ID } from '@constants/env'
import useMounted from '@hooks/useMounted'
import { useTranslation } from 'next-i18next'
import { useCallback, useEffect, useState } from 'react'
import { ExtendedWindow } from 'react-kakao-login/lib/types'

// declare global {
//   interface window {
//     naver: any
//   }
// }
declare let window: ExtendedWindow

const NaverLoginButton = (loginButtonProps: ISocialButton) => {
  const { handleClick, confirmMessage } = loginButtonProps
  const { t } = useTranslation()

  const mounted = useMounted()

  const [customConfirm, setCustomConfirm] = useState<CustomConfirmPrpps>({
    open: false,
    handleConfirm: () => {},
    handleCancel: () => {},
  })

  const NAVER_ID_SDK_URL =
    'https://static.nid.naver.com/js/naveridlogin_js_sdk_2.0.0.js'

  /**
   * 이 함수는 브라우저 환경에서만 호출이 되야 한다. window 객체에 직접 접근한다.
   * @param props
   */
  const initLoginButton = () => {
    const clientId = NAVER_CLIENT_ID
    const callbackUrl = NAVER_CALLBACK_URL
    const onSuccess = handleClick
    const onFailure = handleClick
    const naver = window['naver']

    const naverLogin = new naver.LoginWithNaverId({
      callbackUrl,
      clientId,
      isPopup: true,
      loginButton: { color: 'green', type: 3, height: 60 },
    })

    naverLogin.init()

    if (!window.opener) {
      naver.successCallback = data => {
        return onSuccess(data)
      }
      naver.failureCallback = onFailure
    } else {
      naverLogin.getLoginStatus(status => {
        if (status) {
          window.opener.naver
            .successCallback({
              ...naverLogin.accessToken,
              user: naverLogin.user,
            })
            .then(() => {
              window.close()
            })
            .catch(() => {
              window.close()
            })
        } else {
          window.opener.naver
            .failureCallback()
            .then(() => {
              window.close()
            })
            .catch(() => {
              window.close()
            })
        }
      })
    }
  }

  const appendNaverButton = () => {
    if (document && document.querySelectorAll('#naverIdLogin').length === 0) {
      let naverId = document.createElement('div')
      naverId.id = 'naverIdLogin'
      naverId.style.position = 'absolute'
      naverId.style.top = '-10000px'
      document.body.appendChild(naverId)
    }
  }

  const loadScript = useCallback(() => {
    if (
      document &&
      document.querySelectorAll('#naver-login-sdk').length === 0
    ) {
      let script = document.createElement('script')
      script.id = 'naver-login-sdk'
      script.src = NAVER_ID_SDK_URL
      script.onload = () => {
        return initLoginButton()
      }
      document.head.appendChild(script)
    } else {
      initLoginButton()
    }
  }, [])

  useEffect(() => {
    if (mounted) {
      appendNaverButton()
      loadScript()
    }
  }, [mounted])

  const handleLogin = () => {
    if (!document || !document.querySelector('#naverIdLogin').firstChild) {
      return
    }
    const naverLoginButton = document.querySelector('#naverIdLogin').firstChild

    // @ts-ignore
    naverLoginButton.href = 'javascript:void(0);'
    // naverLoginButton.click()

    const e = new MouseEvent('click', {
      bubbles: false,
      cancelable: true,
      view: window,
    })

    naverLoginButton.dispatchEvent(e)
  }

  return (
    <>
      <a
        href="#"
        className="social naver"
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

                handleLogin()
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
            handleLogin()
          }
        }}
      >
        {t('label.text.naver')}
      </a>
      <CustomConfirm
        handleConfirm={customConfirm?.handleConfirm}
        handleCancel={customConfirm?.handleCancel}
        contentText={customConfirm?.contentText}
        open={customConfirm?.open}
      />
    </>
  )
}

export { NaverLoginButton }
