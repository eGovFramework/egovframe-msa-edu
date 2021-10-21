import React from 'react'
import { useTranslation } from 'react-i18next'
import { useRouter } from 'next/router'
import { makeStyles, Theme } from '@material-ui/core/styles'

const useStyles = makeStyles((theme: Theme) => ({
  mg0: {
    margin: '0 auto',
  },
}))

const Bye = () => {
  const router = useRouter()
  const classes = useStyles()
  const { t } = useTranslation()

  const handleMain = event => {
    event.preventDefault()
    router.push('/')
  }

  return (
    <section className={classes.mg0}>
      <article className="rocation">
        <h2>{t('label.title.mypage')}</h2>
        <ul>
          <li>{t('label.title.home')}</li>
          <li>{t('label.title.mypage')}</li>
          <li>{t('label.title.leave')}</li>
        </ul>
      </article>
      <article className="mypage">
        <div className="message">
          <span className="end">
            {t('label.text.leave.complete1')}
            <br />
            {t('label.text.leave.complete2')}
          </span>
        </div>
        <div className="btn_center">
          <a href="#" onClick={handleMain}>
            {t('label.button.first')}
          </a>
        </div>
      </article>
    </section>
  )
}

export default Bye
