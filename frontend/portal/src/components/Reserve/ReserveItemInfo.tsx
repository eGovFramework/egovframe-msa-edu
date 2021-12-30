import { convertStringToDateFormat } from '@libs/date'
import { ICode } from '@service'
import React from 'react'
import { useTranslation } from 'react-i18next'
import { ReserveItemProps } from '.'

interface ReserveItemInfoProps extends ReserveItemProps {
  reserveStatus?: ICode
}

const ReserveItemInfo = ({ data, reserveStatus }: ReserveItemInfoProps) => {
  const { t } = useTranslation()
  return (
    <>
      <h4>{`${t('reserve_item')} ${t('common.information')}`}</h4>
      {data && (
        <div className="view">
          <dl>
            <dt>{t('location')}</dt>
            <dd>{data.location.locationName}</dd>
          </dl>
          <dl>
            <dt>{t('reserve_item.type')}</dt>
            <dd>{data.categoryName}</dd>
          </dl>
          <dl>
            <dt>{t('reserve_item.name')}</dt>
            <dd>{data.reserveItemName}</dd>
          </dl>
          <dl>
            <dt>{'예약방법'}</dt>
            <dd>{data.reserveMethodName}</dd>
          </dl>
          <dl>
            <dt>{`${t('reserve.count')}/${t('reserve.number_of_people')}`}</dt>
            <dd>{data.totalQty}</dd>
          </dl>
          <dl>
            <dt>{t('reserve_item.selection_means')}</dt>
            <dd>{data.selectionMeansName}</dd>
          </dl>
          <dl>
            <dt>{`${t('reserve_item.operation')} ${t('reserve.period')}`}</dt>
            <dd>{`${convertStringToDateFormat(
              data.operationStartDate,
              'yyyy-MM-dd',
            )} ~ ${convertStringToDateFormat(
              data.operationEndDate,
              'yyyy-MM-dd',
            )}`}</dd>
          </dl>
          <dl>
            <dt>{`${t('reserve_item.request')} ${t('reserve.period')}`}</dt>
            <dd>{`${convertStringToDateFormat(
              data.requestStartDate,
              'yyyy-MM-dd HH:mm',
            )}
          ~ ${convertStringToDateFormat(
            data.requestEndDate,
            'yyyy-MM-dd HH:mm',
          )}
          `}</dd>
          </dl>
          <dl>
            <dt>{`${t('common.free')}/${t('common.paid')}`}</dt>
            <dd>{data.isPaid ? t('common.paid') : t('common.free')}</dd>
          </dl>
          {reserveStatus && (
            <dl>
              <dt>{`${t('reserve')}/${t('common.status')}`}</dt>
              <dd
                className={
                  reserveStatus.codeId === 'request' ||
                  reserveStatus.codeId === 'cancel'
                    ? 'wait'
                    : ''
                }
              >
                {reserveStatus.codeName}
              </dd>
            </dl>
          )}
        </div>
      )}
    </>
  )
}

export { ReserveItemInfo }
