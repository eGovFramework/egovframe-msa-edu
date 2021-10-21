import { useRouter } from 'next/router'
import React from 'react'
import { useTranslation } from 'react-i18next'

const Complete = () => {
  const router = useRouter()
  const { t } = useTranslation()

  const handleLogin = event => {
    event.preventDefault()
    router.push('/auth/login')
  }

  const handleMain = event => {
    event.preventDefault()
    router.push('/')
  }

  return (
    <section className="member">
      <article className="rocation">
        <h2>{t('label.title.join')}</h2>
      </article>

      <article>
        <div className="complete">
          <span>
            {t('label.text.join.complete1')}
            <br />
            {t('label.text.join.complete2')}
          </span>
        </div>
        <div className="btn_center">
          <a href="#" className="blue" onClick={handleLogin}>
            {t('common.login')}
          </a>
          <a href="#" onClick={handleMain}>
            {t('label.button.first')}
          </a>
        </div>
      </article>
    </section>
  )
}

export default Complete
