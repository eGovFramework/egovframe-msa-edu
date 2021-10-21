import React from 'react'
import { useTranslation } from 'react-i18next'
import { IErrorMessage } from '.'

interface ErrorPopupProps extends IErrorMessage {
  handlePopupClose: () => void
}

const ErrorPopup = (props: ErrorPopupProps) => {
  const { title, message, handlePopupClose } = props
  const { t } = useTranslation()

  const handleClick = (e: React.MouseEvent<HTMLAnchorElement>) => {
    e.preventDefault()
    handlePopupClose()
  }
  return (
    <>
      <div className="errorPop">
        <div>
          <div>
            <h4>{title}</h4>
            <p>
              {title}
              <br />
              {message}
            </p>
          </div>
          <a href="#" onClick={handleClick}>
            {t('label.button.close')}
          </a>
        </div>
      </div>
    </>
  )
}

export { ErrorPopup }
