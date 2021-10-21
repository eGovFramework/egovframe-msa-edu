import { BottomButtons, IButtons } from '@components/Buttons'
import React, { useMemo } from 'react'
import { useTranslation } from 'react-i18next'

interface PasswordDoneProps {
  handleList: () => void
}

const PasswordDone = ({ handleList }: PasswordDoneProps) => {
  const { t } = useTranslation()

  const bottomButtons = useMemo(
    (): IButtons[] => [
      {
        id: 'board-edit-list',
        title: t('label.button.first'),
        href: ``,
        handleClick: handleList,
      },
    ],
    [t],
  )
  return (
    <article className="mypage">
      <div className="message">
        <span className="change">{t('label.text.user.password.modified')}</span>
      </div>
      <BottomButtons handleButtons={bottomButtons} />
    </article>
  )
}

export { PasswordDone }
