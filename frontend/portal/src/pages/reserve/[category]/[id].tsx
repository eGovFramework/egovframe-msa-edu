import { BottomButtons, IButtons } from '@components/Buttons'
import {
  ReserveComplete,
  ReserveEdit,
  ReserveItemAdditional,
  ReserveItemInfo,
} from '@components/Reserve'
import { IReserveItem, reserveService } from '@service'
import { GetServerSideProps } from 'next'
import { useRouter } from 'next/router'
import React, { useMemo, useState } from 'react'
import { useTranslation } from 'react-i18next'

interface ReserveDetailProps {
  initData: IReserveItem
}

export interface IReserveComplete {
  done: boolean
  reserveId?: string
}

const ReserveDetail = ({ initData }: ReserveDetailProps) => {
  const router = useRouter()
  const { t } = useTranslation()

  const [edit, setEdit] = useState<boolean>(false)
  const [complete, setComplete] = useState<IReserveComplete>({ done: false })

  // 버튼
  const bottomButtons = useMemo((): IButtons[] => {
    const buttons: IButtons[] = [
      {
        id: 'item-list-button',
        title: t('label.button.list'),
        href: `/reserve/${router.query.category}`,
      },
    ]

    if (initData?.isPossible && initData?.reserveMethodId === 'internet') {
      buttons.push({
        id: 'item-edit-button',
        title: t('reserve_item.request'),
        href: '',
        className: 'blue',
        handleClick: () => {
          if (!edit) {
            setEdit(true)
          }
        },
      })
      return buttons.reverse()
    }

    return buttons
  }, [t, router.query, initData, edit])

  return (
    <>
      {complete.done ? (
        <ReserveComplete reserveId={complete.reserveId} />
      ) : (
        <div className="table_view02">
          {initData && <ReserveItemInfo data={initData} />}
          {initData && !edit && <ReserveItemAdditional data={initData} />}
          {!edit && <BottomButtons handleButtons={bottomButtons} />}
          {edit && (
            <ReserveEdit reserveItem={initData} setComplete={setComplete} />
          )}
        </div>
      )}
    </>
  )
}

export const getServerSideProps: GetServerSideProps = async context => {
  const categoryId = String(context.query.category)
  const id = Number(context.query.id)

  let initData: IReserveItem

  try {
    const result = await reserveService.getItem(id)
    if (result) {
      initData = result.data
    }
  } catch (error) {
    console.error(`reserve detail item query error ${error.message}`)
    if (error.response?.data?.code === 'E003') {
      return {
        notFound: true,
      }
    }
  }

  return {
    props: {
      initData,
    },
  }
}

export default ReserveDetail
