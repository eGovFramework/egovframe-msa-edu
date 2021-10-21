import { DLWrapper } from '@components/WriteDLFields'
import { makeStyles, Theme } from '@material-ui/core/styles'
import Alert from '@material-ui/lab/Alert'
import { userService } from '@service'
import { format, isValidPassword } from '@utils'
import { GetServerSideProps } from 'next'
import { useRouter } from 'next/router'
import React, { createRef, useState } from 'react'
import { Controller, useForm } from 'react-hook-form'
import { useTranslation } from 'react-i18next'

const useStyles = makeStyles((theme: Theme) => ({
  alert: {
    marginTop: theme.spacing(2),
  },
}))

interface IUserPasswordForm {
  tokenValue: string
  password: string
  passwordConfirm: string
}

interface ChangePasswordProps {
  tokenValue: string | null
  valid: boolean
}

const ChangePassword = ({ tokenValue, valid }: ChangePasswordProps) => {
  const router = useRouter()
  const classes = useStyles()
  const { t } = useTranslation()

  const passwordRef = createRef<HTMLInputElement>()

  const [changed, setChanged] = useState<boolean>(false)
  const [errorState, setErrorState] = useState<string | null>(null)

  // form hook
  const methods = useForm<IUserPasswordForm>({
    defaultValues: {
      tokenValue,
      password: '',
      passwordConfirm: '',
    },
  })
  const {
    control,
    handleSubmit,
    formState: { errors },
  } = methods

  // 비밀번호 변경
  const handleChangePassword = async (data: IUserPasswordForm) => {
    userService
      .changePassword(data)
      .then(result => {
        if (result === true) {
          setChanged(true)
        } else {
          setErrorState(t('err.internal.server'))
        }
      })
      .catch(error => {
        setErrorState(error.response.data.message || t('err.internal.server'))
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
      <section className="member">
        <article className="rocation">
          <h2>{t('label.title.change_password')}</h2>
        </article>
        {valid === true && changed === false && (
          <article className="pass">
            <form>
              <div className="table_write01">
                <span>{t('common.required_fields')}</span>
                <div className="write">
                  <Controller
                    control={control}
                    name="password"
                    render={({ field, fieldState }) => (
                      <DLWrapper
                        title={t('label.title.new_password')}
                        className="inputTitle"
                        required
                        error={fieldState.error}
                      >
                        <input
                          ref={passwordRef}
                          type="password"
                          value={field.value}
                          onChange={field.onChange}
                          placeholder={t('label.title.new_password')}
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
                          isValidPassword(value) ||
                          (t('valid.password') as string)
                        )
                      },
                    }}
                  />
                  <Controller
                    control={control}
                    name="passwordConfirm"
                    render={({ field, fieldState }) => (
                      <DLWrapper
                        title={t('label.title.new_password_confirm')}
                        className="inputTitle"
                        required
                        error={fieldState.error}
                      >
                        <input
                          type="password"
                          value={field.value}
                          onChange={field.onChange}
                          placeholder={t('label.title.new_password_confirm')}
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
                </div>
                {errorState && (
                  <Alert className={classes.alert} severity="warning">
                    {errorState}
                  </Alert>
                )}

                <div className="btn_center">
                  <button
                    type="submit"
                    className="blue"
                    onClick={handleSubmit(handleChangePassword)}
                  >
                    {t('label.button.change')}
                  </button>
                  <a href="#" onClick={handleMain}>
                    {t('label.button.first')}
                  </a>
                </div>
              </div>
            </form>
          </article>
        )}
        {valid === true && changed === true && (
          <article>
            <div className="complete">
              <span className="reset">{t('label.text.changed_password')}</span>
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
        )}
        {valid === false && (
          <article>
            <div className="complete">
              <span className="reset">{t('err.user.change.password')}</span>
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
        )}
      </section>
    </>
  )
}

export const getServerSideProps: GetServerSideProps = async context => {
  let tokenValue = context.query.token as string

  let valid = false

  try {
    if (tokenValue) {
      const result = await userService.getFindPassword(tokenValue)
      if (result && result.data) {
        valid = (await result.data) as boolean
      }
    } else {
      tokenValue = null
    }
  } catch (error) {
    console.error(`find-password item query error ${error.message}`)
  }

  return {
    props: {
      tokenValue,
      valid,
    },
  }
}

export default ChangePassword
