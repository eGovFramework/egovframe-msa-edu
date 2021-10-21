import ActiveLink from '@components/ActiveLink'
import React from 'react'
import { useTranslation } from 'react-i18next'
import { IErrorMessage } from '.'

const ErrorPage = (props: IErrorMessage) => {
  const { title, message } = props
  const { t } = useTranslation()

  return (
    <div id="container">
      <div>
        <section className="error">
          <article>
            <h2>{title} </h2>
            <div>
              <span>
                {title}
                <br />
                {message}
              </span>
            </div>
          </article>
          <div className="btn_center">
            <ActiveLink
              href="prev"
              children={t('label.button.prev')}
              className="blue"
            />
            <ActiveLink href="/" children={t('label.button.go_home')} />
          </div>
        </section>
      </div>
    </div>
  )
}

export { ErrorPage }
