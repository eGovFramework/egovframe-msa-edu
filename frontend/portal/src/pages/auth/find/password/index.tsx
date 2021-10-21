import ValidationAlert from '@components/ValidationAlert'
import CircularProgress from '@material-ui/core/CircularProgress'
import { makeStyles, Theme } from '@material-ui/core/styles'
import Alert from '@material-ui/lab/Alert'
import { userService } from '@service'
import { format } from '@utils'
import { useRouter } from 'next/router'
import React, { useState } from 'react'
import { Controller, useForm } from 'react-hook-form'
import { useTranslation } from 'react-i18next'

const useStyles = makeStyles((theme: Theme) => ({
  alert: {
    marginTop: theme.spacing(2),
  },
  progressWrap: {
    position: 'absolute',
    width: '100%',
    height: '100%',
  },
  progress: {
    // display: 'flex',
    '& > * + *': {
      marginLeft: theme.spacing(2),
    },
    textAlign: 'center',
    marginTop: '320px',
  },
}))

interface IUserPasswordForm {
  emailAddr: string
  userName: string
  mainUrl?: string
  changePasswordUrl?: string
}

const FindPassword = () => {
  const router = useRouter()
  const classes = useStyles()
  const { t } = useTranslation()

  const [requestChange, setRequestChange] = useState<boolean>(false)
  const [emailAddr, setEmailAddr] = useState<string>(null)
  const [errorState, setErrorState] = useState<string | null>(null)

  // form hook
  const methods = useForm<IUserPasswordForm>({
    defaultValues: {
      emailAddr: '',
      userName: '',
    },
  })
  const {
    control,
    handleSubmit,
    formState: { errors },
  } = methods

  // 비밀번호 찾기
  const handleFind = async (data: IUserPasswordForm) => {
    if (requestChange === true) return // 메일 발송 완료까지 시간이 소요되어 비활성화 처리

    data.mainUrl = window.location.origin
    data.changePasswordUrl =
      window.location.origin + '/auth/find/password/change'

    setRequestChange(true)

    userService
      .findPassword(data)
      .then(result => {
        if (result === true) {
          setEmailAddr(data.emailAddr)
        } else {
          setErrorState(t('err.internal.server'))
        }
        setRequestChange(false)
      })
      .catch(error => {
        setErrorState(error.response.data.message)
        setRequestChange(false)
      })
  }

  const handleLogin = event => {
    event.preventDefault()
    router.push('/auth/login')
  }

  const handleMain = event => {
    event.preventDefault()
    router.push('/')
  }

  return (
    <>
      {emailAddr === null && (
        <section className="login">
          <h2>{t('label.title.find_password')}</h2>
          <form>
            <fieldset>
              <Controller
                control={control}
                name="userName"
                render={({ field, fieldState }) => (
                  <input
                    type="text"
                    value={field.value}
                    onChange={field.onChange}
                    placeholder={t('label.title.name')}
                  />
                )}
                defaultValue=""
                rules={{
                  required: true,
                  minLength: {
                    value: 2,
                    message: format(t('valid.minlength.format'), [2]),
                  },
                  maxLength: {
                    value: 25,
                    message: format(t('valid.maxlength.format'), [25]),
                  },
                }}
              />
              {errors.userName && (
                <ValidationAlert
                  fieldError={errors.userName}
                  target={[25]}
                  label={t('label.title.name')}
                />
              )}
              <Controller
                control={control}
                name="emailAddr"
                render={({ field, fieldState }) => (
                  <input
                    type="text"
                    value={field.value}
                    onChange={field.onChange}
                    placeholder={t('user.email')}
                    inputMode="email"
                    maxLength={50}
                  />
                )}
                defaultValue=""
                rules={{
                  required: true,
                  maxLength: {
                    value: 50,
                    message: format(t('valid.maxlength.format'), [50]),
                  },
                  pattern: {
                    value:
                      /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i,
                    message: t('valid.email.pattern'),
                  },
                }}
              />
              {errors.emailAddr && (
                <ValidationAlert
                  fieldError={errors.emailAddr}
                  target={[50]}
                  label={t('user.emailAddr')}
                />
              )}
              {errorState && (
                <Alert className={classes.alert} severity="warning">
                  {errorState}
                </Alert>
              )}
              <button type="submit" onClick={handleSubmit(handleFind)}>
                {t('label.title.find_password')}
              </button>
            </fieldset>
          </form>
        </section>
      )}
      {emailAddr !== null && (
        <section className="member">
          <article className="rocation">
            <h2>{t('label.title.find_password')}</h2>
          </article>

          <article>
            <div className="complete">
              <span className="pass">
                {format(t('msg.user.find.password'), [emailAddr])}
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
      )}
      {errorState && (
        <Alert className={classes.alert} severity="warning">
          {errorState}
        </Alert>
      )}
      {requestChange === true && (
        <div className={classes.progressWrap}>
          <div className={classes.progress}>
            <CircularProgress />
          </div>
        </div>
      )}
    </>
  )
}

export default FindPassword
