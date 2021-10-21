import { BottomButtons, IButtons } from '@components/Buttons'
import ValidationAlert from '@components/ValidationAlert'
import useInputs from '@hooks/useInputs'
import { reserveService } from '@service'
import { errorStateSelector } from '@stores'
import { useRouter } from 'next/router'
import { useSnackbar } from 'notistack'
import React, { useEffect, useMemo, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useSetRecoilState } from 'recoil'

const ReserveCancel = () => {
  const router = useRouter()
  const { t } = useTranslation()

  const { enqueueSnackbar } = useSnackbar()

  const setErrorState = useSetRecoilState(errorStateSelector)
  const searchText = useInputs('')
  const [error, setError] = useState<boolean>(false)

  useEffect(() => {
    if (searchText.value !== '') {
      setError(false)
    }
  }, [searchText])

  const handleCancelClick = async () => {
    if (searchText.value === '') {
      setError(true)
      return
    }

    try {
      const result = await reserveService.cancel(
        String(router.query?.id),
        searchText.value,
      )
      if (result) {
        enqueueSnackbar(
          `${t('reserve')} ${t('common.cancel')}${t('common.msg.done.format')}`,
          {
            variant: 'success',
          },
        )
        router.push('/user/reserve')
      }
    } catch (error) {
      setErrorState({ error })
    }
  }

  // 버튼
  const bottomButtons = useMemo(
    (): IButtons[] => [
      {
        id: 'item-confirm-button',
        title: t('label.button.confirm'),
        href: ``,
        handleClick: handleCancelClick,
        className: 'blue',
      },
      {
        id: 'item-list-button',
        title: t('label.button.cancel'),
        href: ``,
        handleClick: () => {
          router.back()
        },
      },
    ],

    [t, router, searchText],
  )

  return (
    <div className="mypage">
      <p>{t('reserve.msg.calcel_reason')}​</p>
      <div className="table_write01">
        <span>{t('common.required_fields')}</span>
        <div className="write">
          <dl>
            <dt className="import">{t('reserve.cancel_reason')}</dt>
            <dd>
              <input
                type="text"
                placeholder={t('reserve.cancel_reason')}
                {...searchText}
              />
              {error && (
                <ValidationAlert message={t('reserve.msg.calcel_reason')} />
              )}
            </dd>
          </dl>
        </div>
      </div>
      <BottomButtons handleButtons={bottomButtons} />
    </div>
  )
}

export default ReserveCancel
