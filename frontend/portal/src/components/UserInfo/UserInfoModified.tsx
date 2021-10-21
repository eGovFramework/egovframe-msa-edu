import ActiveLink from '@components/ActiveLink'
import { BottomButtons, IButtons } from '@components/Buttons'
import { DLWrapper } from '@components/WriteDLFields'
import { userService } from '@service'
import { errorStateSelector, userAtom } from '@stores'
import { format } from '@utils'
import React, { useMemo } from 'react'
import { Controller, UseFormGetValues, UseFormSetFocus } from 'react-hook-form'
import { useTranslation } from 'react-i18next'
import { useRecoilValue, useSetRecoilState } from 'recoil'
import { IUserForm, UserInfoProps } from '.'

interface UserInfoModifiedPrpps extends UserInfoProps {
  handleUpdate: () => void
  getValues: UseFormGetValues<IUserForm>
  setFocus: UseFormSetFocus<IUserForm>
  showMessage: (message: string, callback?: () => void) => void
  setCheckedEmail: React.Dispatch<React.SetStateAction<boolean>>
}

const UserInfoModified = (props: UserInfoModifiedPrpps) => {
  const {
    control,
    formState,
    handleUpdate,
    getValues,
    setFocus,
    showMessage,
    setCheckedEmail,
  } = props
  const { t } = useTranslation()

  const user = useRecoilValue(userAtom)

  const setErrorState = useSetRecoilState(errorStateSelector)

  // 이메일 중복확인
  const handleCheckEmail = async () => {
    const emailValue = getValues('email')

    if (user.email === emailValue) {
      showMessage(t('msg.notmodified'))
      return
    }

    if (
      /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i.test(
        emailValue,
      ) === false
    ) {
      showMessage(t('valid.email.pattern'), () => setFocus('email'))
      return
    }

    try {
      const result = await userService.existsEmail(emailValue, user.userId)
      if (result === true) {
        showMessage(t('msg.user.email.exists'), () => {
          setFocus('email')
        })
      } else {
        showMessage(t('msg.user.email.notexists'))
        setCheckedEmail(true)
      }
    } catch (error) {
      setErrorState({ error })
    }
  }

  const bottomButtons = useMemo(
    (): IButtons[] => [
      {
        id: 'board-edit-save',
        title: t('label.button.confirm'),
        href: '',
        className: 'blue',
        handleClick: handleUpdate,
      },
      {
        id: 'board-edit-list',
        title: t('label.button.cancel'),
        href: `/`,
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
          name="email"
          render={({ field, fieldState }) => (
            <DLWrapper
              title={t('user.email')}
              className="inputTitle"
              required
              error={fieldState.error}
            >
              <input
                autoFocus
                ref={field.ref}
                type="text"
                value={field.value}
                onChange={field.onChange}
                placeholder={t('user.email')}
                inputMode="email"
                maxLength={50}
              />
              <ActiveLink href="" handleActiveLinkClick={handleCheckEmail}>
                {t('label.button.check_email')}
              </ActiveLink>
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
                ref={field.ref}
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

      <BottomButtons handleButtons={bottomButtons} />
    </div>
  )
}

export { UserInfoModified }
