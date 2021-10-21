import { BottomButtons, IButtons } from '@components/Buttons'
import { useRouter } from 'next/router'
import React, { useMemo } from 'react'
import { useTranslation } from 'react-i18next'

interface ReserveCompleteProps {
  reserveId: string
}

const ReserveComplete = ({ reserveId }: ReserveCompleteProps) => {
  const router = useRouter()
  const { t } = useTranslation()

  // 버튼
  const bottomButtons = useMemo(
    (): IButtons[] => [
      {
        id: 'item-confirm-button',
        title: `${t('reserve')} ${t('label.button.confirm')}`,
        href: `/user/reserve/${reserveId}`,
        className: 'blue',
      },
      {
        id: 'item-list-button',
        title: t('label.button.list'),
        href: `/reserve/${router.query.category}`,
      },
    ],
    [t, router, reserveId],
  )

  return (
    <>
      <div className="reserv">
        <span>{t('reserve.msg.complete')}</span>
      </div>
      <BottomButtons handleButtons={bottomButtons} />
    </>
  )
}

export { ReserveComplete }
