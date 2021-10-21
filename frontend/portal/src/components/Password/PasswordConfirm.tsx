import { BottomButtons, IButtons } from '@components/Buttons'
import { DLWrapper } from '@components/WriteDLFields'
import { format, isValidPassword } from '@utils'
import React, { useMemo } from 'react'
import { Controller } from 'react-hook-form'
import { useTranslation } from 'react-i18next'
import { PasswordProps } from '.'

interface PasswordConfirmProps extends PasswordProps {
  handleCheckPassword: () => void
}

const PasswordConfirm = (props: PasswordConfirmProps) => {
  const { control, formState, handleCheckPassword, handleList } = props
  const { t } = useTranslation()

  const handleKeyPress = (event: React.KeyboardEvent<HTMLElement>) => {
    if (event.key === 'Enter') {
      event.preventDefault()
      handleCheckPassword()
    }
  }

  const bottomButtons = useMemo(
    (): IButtons[] => [
      {
        id: 'board-edit-save',
        title: t('label.button.confirm'),
        href: '',
        className: 'blue',
        handleClick: handleCheckPassword,
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
      <div className="write">
        <Controller
          control={control}
          name="currentPassword"
          render={({ field, fieldState }) => (
            <DLWrapper
              title={t('user.password')}
              className="inputTitle"
              required
              error={fieldState.error}
            >
              <input
                autoFocus
                type="password"
                value={field.value}
                onChange={field.onChange}
                placeholder={t('user.password')}
                onKeyPress={handleKeyPress}
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
              return isValidPassword(value) || (t('valid.password') as string)
            },
          }}
        />
      </div>

      <BottomButtons handleButtons={bottomButtons} />
    </div>
  )
}

export { PasswordConfirm }
