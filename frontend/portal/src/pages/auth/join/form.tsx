import CustomAlert, { CustomAlertPrpps } from '@components/CustomAlert'
import { DLWrapper } from '@components/WriteDLFields'
import { makeStyles, Theme } from '@material-ui/core/styles'
import Alert from '@material-ui/lab/Alert'
import { userService } from '@service'
import { format, isValidPassword } from '@utils'
import { useRouter } from 'next/router'
import React, { createRef, useState } from 'react'
import { Controller, useForm } from 'react-hook-form'
import { useTranslation } from 'react-i18next'

const useStyles = makeStyles((theme: Theme) => ({
  alert: {
    marginTop: theme.spacing(2),
  },
}))

interface IUserForm {
  email: string
  password: string
  passwordConfirm: string
  userName: string
}

const Form = () => {
  const router = useRouter()
  const classes = useStyles()
  const { t } = useTranslation()

  const [errorState, setErrorState] = useState<string | null>(null)

  const [checkedEmail, setCheckedEmail] = useState<boolean>(false)
  const emailRef = createRef<HTMLInputElement>()
  const passwordRef = createRef<HTMLInputElement>()

  const [customAlert, setCustomAlert] = useState<any>({
    open: false,
    message: '',
    handleAlert: () => {
      setCustomAlert({ open: false })
    },
  } as CustomAlertPrpps)

  // form hook
  const methods = useForm<IUserForm>({
    defaultValues: {
      email: '',
      password: '',
      passwordConfirm: '',
      userName: '',
    },
  })
  const {
    control,
    handleSubmit,
    formState: { errors },
  } = methods

  const showMessage = (message: string, callback?: () => void) => {
    setCustomAlert({
      open: true,
      message,
      handleAlert: () => {
        setCustomAlert({ open: false })
        if (callback) callback()
      },
    })
  }

  // 이메일중복확인
  const handleCheckEmail = event => {
    event.preventDefault()

    const emailElement = emailRef.current
    const email = emailElement?.value

    if (
      /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i.test(
        email,
      ) === false
    ) {
      showMessage(t('valid.email.pattern'), () => {
        emailElement?.focus()
      })
      return
    }

    userService
      .existsEmail(emailElement?.value)
      .then(result => {
        if (result === true) {
          setCheckedEmail(false)
          showMessage(t('msg.user.email.exists'), () => {
            emailElement?.focus()
          })
        } else {
          setCheckedEmail(true)
          showMessage(t('msg.user.email.notexists'))
        }
      })
      .catch(error => {
        setErrorState(error.response.data.message || t('err.internal.server'))
      })
  }

  // 가입
  const handleJoin = async (data: IUserForm) => {
    if (!checkedEmail) {
      showMessage(t('msg.user.email.check'), () => {
        emailRef.current?.focus()
      })
      return
    }

    userService
      .join(data)
      .then(result => {
        if (result === true) {
          router.push('/auth/join/complete')
        } else {
          setErrorState(t('err.internal.server'))
        }
      })
      .catch(error => {
        setErrorState(error.response.data.message || t('err.internal.server'))
      })
  }

  // 취소
  const handleCancel = event => {
    event.preventDefault()
    router.back()
  }

  return (
    <section className="member">
      <article className="rocation">
        <h2>{t('label.title.join')}</h2>
      </article>

      <article>
        <form>
          <div className="table_write01">
            <span>{t('label.title.required')}</span>
            <div className="write">
              <Controller
                control={control}
                name="email"
                render={({ field, fieldState }) => (
                  <DLWrapper
                    title={t('user.email')}
                    className="inputTitle"
                    required
                    error={fieldState.error}
                  >
                    <input
                      ref={emailRef}
                      type="text"
                      value={field.value}
                      onChange={field.onChange}
                      placeholder={t('user.email')}
                      inputMode="email"
                      maxLength={50}
                      onInput={event => {
                        setCheckedEmail(false)
                      }}
                    />
                    <button type="button" onClick={handleCheckEmail}>
                      {t('label.button.check_email')}
                    </button>
                  </DLWrapper>
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
              <Controller
                control={control}
                name="password"
                render={({ field, fieldState }) => (
                  <DLWrapper
                    title={t('user.password')}
                    className="inputTitle"
                    required
                    error={fieldState.error}
                  >
                    <input
                      ref={passwordRef}
                      type="password"
                      value={field.value}
                      onChange={field.onChange}
                      placeholder={t('user.password')}
                    />
                    <span>{t('label.text.password_format')}</span>
                  </DLWrapper>
                )}
                defaultValue=""
                rules={{
                  required: true,
                  maxLength: {
                    value: 20,
                    message: format(t('valid.maxlength.format'), [20]),
                  },
                  validate: value => {
                    return (
                      isValidPassword(value) || (t('valid.password') as string)
                    )
                  },
                }}
              />
              <Controller
                control={control}
                name="passwordConfirm"
                render={({ field, fieldState }) => (
                  <DLWrapper
                    title={t('label.title.password_confirm')}
                    className="inputTitle"
                    required
                    error={fieldState.error}
                  >
                    <input
                      type="password"
                      value={field.value}
                      onChange={field.onChange}
                      placeholder={t('label.title.password_confirm')}
                    />
                  </DLWrapper>
                )}
                defaultValue=""
                rules={{
                  required: true,
                  maxLength: {
                    value: 20,
                    message: format(t('valid.maxlength.format'), [20]),
                  },
                  validate: value => {
                    return (
                      (isValidPassword(value) &&
                        passwordRef.current?.value === value) ||
                      (t('valid.password.confirm') as string)
                    )
                  },
                }}
              />
              <Controller
                control={control}
                name="userName"
                render={({ field, fieldState }) => (
                  <DLWrapper
                    title={t('label.title.name')}
                    className="inputTitle"
                    required
                    error={fieldState.error}
                  >
                    <input
                      type="text"
                      value={field.value}
                      onChange={field.onChange}
                      placeholder={t('label.title.name')}
                    />
                  </DLWrapper>
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
            </div>
            {errorState && (
              <Alert className={classes.alert} severity="warning">
                {errorState}
              </Alert>
            )}

            <div className="btn_center">
              <a href="#" className="blue" onClick={handleSubmit(handleJoin)}>
                {t('label.button.join')}
              </a>
              <a href="#" onClick={handleCancel}>
                {t('label.button.cancel')}
              </a>
            </div>
          </div>
        </form>
      </article>
      <CustomAlert
        contentText={customAlert.message}
        open={customAlert.open}
        handleAlert={customAlert.handleAlert}
      />
    </section>
  )
}

export default Form
