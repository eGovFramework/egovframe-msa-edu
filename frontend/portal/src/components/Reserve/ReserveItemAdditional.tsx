import React from 'react'
import { useTranslation } from 'react-i18next'
import { ReserveItemProps } from '.'

interface ReserveItemAdditionalPropps extends ReserveItemProps {}

const ReserveItemAdditional = ({ data }: ReserveItemAdditionalPropps) => {
  const { t } = useTranslation()
  return (
    <>
      <h4>{t('reserve_item.add_information')}</h4>
      {data && (
        <div className="view">
          <dl>
            <dt>{t('reserve_item.purpose')}</dt>
            <dd>{data.purpose}</dd>
          </dl>
          <dl>
            <dt>{t('reserve_item.target')}</dt>
            <dd>{data.targetName}</dd>
          </dl>
          <dl>
            <dt>{t('common.home_page_address')}</dt>
            <dd>{data.homepage}</dd>
          </dl>
          <dl>
            <dt>{t('reserve_item.contact')}</dt>
            <dd>{data.contact}</dd>
          </dl>
          <dl>
            <dt>{'주소'}</dt>
            <dd>{data.address}</dd>
          </dl>
        </div>
      )}
      <h4>{t('reserve_item.manager')}</h4>
      {data && (
        <div className="view">
          <dl>
            <dt>{t('reserve_item.dept')}</dt>
            <dd>{data.managerDept}</dd>
          </dl>
          <dl>
            <dt>{t('label.title.name')}</dt>
            <dd>{data.managerName}</dd>
          </dl>
          <dl>
            <dt>{t('common.contact')}</dt>
            <dd>{data.managerContact}</dd>
          </dl>
        </div>
      )}
    </>
  )
}

export { ReserveItemAdditional }
