import { BottomButtons, IButtons } from '@components/Buttons'
import { DLWrapper } from '@components/WriteDLFields'
import { format, isValidPassword } from '@utils'
import React, { createRef, useMemo } from 'react'
import { Controller, UseFormSetFocus } from 'react-hook-form'
import { useTranslation } from 'react-i18next'
import { IUserPasswordForm, PasswordProps } from '.'

interface PasswordChangeProps extends PasswordProps {
  handleChangePassword: () => void
  setFocus: UseFormSetFocus<IUserPasswordForm>
  currentPassword: string
}

const PasswordChange = (props: PasswordChangeProps) => {
  const {
    control,
    handleChangePassword,
    setFocus,
    currentPassword,
    handleList,
  } = props
  const { t } = useTranslation()
  const newPasswordRef = createRef<HTMLInputElement>()

  const handleKeyPress = (event: React.KeyboardEvent<HTMLElement>) => {
    if (event.key === 'Enter') {
      event.preventDefault()
      setFocus('newPasswordConfirm')
    }
  }

  const handleKeyPressConfirm = (event: React.KeyboardEvent<HTMLElement>) => {
    if (event.key === 'Enter') {
      event.preventDefault()
      handleChangePassword()
    }
  }

  const bottomButtons = useMemo(
    (): IButtons[] => [
      {
        id: 'board-edit-save',
        title: t('label.button.change'),
        href: '',
        className: 'blue',
        handleClick: handleChangePassword,
      },
      {
        id: 'board-edit-list',
        title: t('label.button.cancel'),
        href: ``,
        handleClick: handleList,
      },
    ],
    [props, t],
  )
  return (
    <div className="table_write01">
      <span>{t('common.required_fields')}</span>
      <div className="change">
        <Controller
          control={control}
          name="newPassword"
          render={({ field, fieldState }) => (
            <DLWrapper
              title={t('label.title.new_password')}
              className="inputTitle"
              required
              error={fieldState.error}
            >
              <input
                autoFocus
                ref={newPasswordRef}
                type="password"
                value={field.value}
                onChange={field.onChange}
                placeholder={t('label.title.new_password')}
                onKeyPress={handleKeyPress}
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
                (!isValidPassword(value) && (t('valid.password') as string)) ||
                (currentPassword === value &&
                  (t('valid.user.password.notchange') as string)) ||
                true
              )
            },
          }}
        />
        <Controller
          control={control}
          name="newPasswordConfirm"
          render={({ field, fieldState }) => (
            <DLWrapper
              title={t('label.title.new_password_confirm')}
              className="inputTitle"
              required
              error={fieldState.error}
            >
              <input
                ref={field.ref}
                type="password"
                value={field.value}
                onChange={field.onChange}
                placeholder={t('label.title.new_password_confirm')}
                onKeyPress={handleKeyPressConfirm}
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
                  newPasswordRef.current?.value === value) ||
                (t('valid.password.confirm') as string)
              )
            },
          }}
        />
      </div>

      <BottomButtons handleButtons={bottomButtons} />
    </div>
  )
}

export { PasswordChange }
