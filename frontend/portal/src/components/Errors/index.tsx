import React, { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { ErrorPage } from './ErrorPage'
import { ErrorPopup } from './ErrorPopup'

interface CustomErrorPageProps {
  title?: string
  message?: string
  statusCode?: number
  isPopup?: boolean
  handlePopupClose?: () => void
}

export interface IErrorMessage {
  title: string
  message: string
}

const CustomErrorPage = (props: CustomErrorPageProps) => {
  const {
    title,
    message,
    statusCode,
    isPopup = false,
    handlePopupClose,
  } = props
  const { t } = useTranslation()

  const [errorMessageState, setErrorMessageState] = useState<IErrorMessage>({
    title: t('err.title'),
    message: t('err.default.message'),
  })

  useEffect(() => {
    if (message) {
      setErrorMessageState({
        title: title || t('error.title'),
        message,
      })
      return
    }

    if (statusCode === 404) {
      setErrorMessageState({
        title: '404 Not Found',
        message: t('err.page.not.found'),
      })
      return
    }

    if (statusCode) {
      setErrorMessageState({
        ...errorMessageState,
        message: t('err.internal.server'),
      })
    }
  }, [statusCode, message])

  return (
    <>
      {isPopup ? (
        <ErrorPopup
          handlePopupClose={handlePopupClose}
          {...errorMessageState}
        />
      ) : (
        <ErrorPage {...errorMessageState} />
      )}
    </>
  )
}

export default CustomErrorPage
