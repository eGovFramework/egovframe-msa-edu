import { BottomButtons, IButtons } from '@components/Buttons'
import { ReserveInfo, ReserveItemInfo } from '@components/Reserve'
import { ICode, IReserve, reserveService } from '@service'
import { GetServerSideProps } from 'next'
import { useRouter } from 'next/router'
import React, { useMemo } from 'react'
import { useTranslation } from 'react-i18next'

interface UserReserveDetailProps {
  initData: IReserve
  status?: ICode
}

const UserReserveDetail = ({ initData, status }: UserReserveDetailProps) => {
  const router = useRouter()
  const { t } = useTranslation()

  // 버튼
  const bottomButtons = useMemo((): IButtons[] => {
    const buttons: IButtons[] = [
      {
        id: 'item-list-button',
        title: t('label.button.list'),
        href: `/user/reserve`,
      },
    ]

    if (
      initData?.reserveStatusId === 'approve' ||
      initData?.reserveStatusId === 'request'
    ) {
      buttons.push({
        id: 'item-cancel-button',
        title: `${t('reserve')} ${t('label.button.cancel')}`,
        href: `/user/reserve/cancel/${initData.reserveId}`,
        className: 'blue',
      })
      return buttons.reverse()
    }

    return buttons
  }, [t, router.query, initData])

  return (
    <>
      <div className="table_view02">
        {initData && (
          <>
            <ReserveItemInfo
              data={initData.reserveItem}
              reserveStatus={status}
            />
            <ReserveInfo data={initData} />
          </>
        )}
        <BottomButtons handleButtons={bottomButtons} />
      </div>
    </>
  )
}

export const getServerSideProps: GetServerSideProps = async context => {
  const id = String(context.query.id)

  let initData: IReserve = null
  let status: ICode = null

  try {
    const result = await reserveService.getReserve(id)
    if (result) {
      initData = result.data
    }

    const codeResult = await reserveService.getCode('reserve-status')

    if (codeResult) {
      status = codeResult.data.find(
        item => item.codeId === initData?.reserveStatusId,
      )
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
      status,
    },
  }
}

export default UserReserveDetail
