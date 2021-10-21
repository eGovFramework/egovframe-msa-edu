import ValidationAlert from '@components/ValidationAlert'
import { EmailStorage } from '@libs/Storage/emailStorage'
import Alert from '@material-ui/lab/Alert'
import React, { useState } from 'react'
import { useForm } from 'react-hook-form'
import { useTranslation } from 'react-i18next'
import { makeStyles, Theme } from '@material-ui/core/styles'

const useStyles = makeStyles((theme: Theme) => ({
  alert: {
    marginTop: theme.spacing(2),
    whiteSpace: 'break-spaces',
    wordBreak: 'keep-all',
  },
}))

export type loginFormType = {
  email?: string
  password?: string
  isRemember?: boolean
}

interface LoginFormProps {
  errorMessage?: string
  handleLogin: ({ email, password }: loginFormType) => void
}

const LoginForm = (props: LoginFormProps) => {
  const { errorMessage, handleLogin } = props
  const classes = useStyles()
  const { t } = useTranslation()

  const emails = new EmailStorage('login')

  const [checked, setChecked] = useState<boolean>(emails.get().isRemember)
  const {
    register,
    handleSubmit,
    formState: { errors },
    getValues,
  } = useForm<loginFormType>({
    defaultValues: {
      email: emails.get().email,
    },
  })

  const onSubmit = (formData: loginFormType) => {
    setRemember()
    handleLogin({
      email: formData.email,
      password: formData.password,
    })
  }

  const setRemember = () => {
    if (checked) {
      emails.set({
        email: getValues('email'),
        isRemember: checked,
      })
    } else {
      emails.clear()
    }
  }

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setChecked(event.target.checked)
    setRemember()
  }

  return (
    <fieldset>
      <form noValidate onSubmit={handleSubmit(onSubmit)}>
        <input
          type="text"
          placeholder={t('user.email')}
          {...register('email', {
            required: `${t('user.email')} ${t('valid.required')}`,
            pattern: {
              value:
                /^[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/i,
              message: `${t('user.email')} ${t('valid.format_not_match')}`,
            },
          })}
        />
        {errors.email && (
          <ValidationAlert
            className={classes.alert}
            fieldError={errors.email}
            label={t('user.email')}
          />
        )}
        <input
          type="password"
          placeholder={t('user.password')}
          {...register('password', {
            required: `${t('user.password')} ${t('valid.required')}`,
          })}
        />
        {errors.password && (
          <ValidationAlert
            className={classes.alert}
            fieldError={errors.password}
            label={t('user.password')}
          />
        )}
        <div className="save">
          <input
            type="checkbox"
            id="save"
            onChange={handleChange}
            checked={checked}
          />
          <label htmlFor="save">{t('login.email_save')}</label>
        </div>
        {errorMessage && (
          <Alert className={classes.alert} severity="warning">
            {errorMessage}
          </Alert>
        )}
        <button>{t('common.login')}</button>
      </form>
    </fieldset>
  )
}

export { LoginForm }
