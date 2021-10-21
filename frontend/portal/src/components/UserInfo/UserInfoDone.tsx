import { BottomButtons, IButtons } from '@components/Buttons'
import React, { useMemo } from 'react'
import { useTranslation } from 'react-i18next'

interface UserInfoDoneProps {
  handleList: () => void
}

const UserInfoDone = ({ handleList }: UserInfoDoneProps) => {
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
        <span className="change">{t('label.text.user.info.modified')}</span>
      </div>
      <BottomButtons handleButtons={bottomButtons} />
    </article>
  )
}

export { UserInfoDone }
