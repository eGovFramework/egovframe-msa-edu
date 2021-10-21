import AttachList from '@components/AttachList'
import { convertStringToDateFormat } from '@libs/date'
import { fileService, IAttachmentResponse, IReserve } from '@service'
import { errorStateSelector } from '@stores'
import React, { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useSetRecoilState } from 'recoil'

interface ReserveInfoProps {
  data: IReserve
}

const ReserveInfo = ({ data }: ReserveInfoProps) => {
  const { t } = useTranslation()
  const setErrorState = useSetRecoilState(errorStateSelector)

  // 첨부파일
  const [attachList, setAttachList] = useState<IAttachmentResponse[]>(undefined)

  useEffect(() => {
    if (data.attachmentCode) {
      const getAttachments = async () => {
        try {
          const result = await fileService.getAttachmentList(
            data.attachmentCode,
          )
          if (result?.data) {
            setAttachList(result.data)
          }
        } catch (error) {
          setErrorState({ error })
        }
      }

      getAttachments()
    }

    return () => setAttachList(null)
  }, [data, setErrorState])

  return (
    <>
      <h4>{`${t('reserve')} ${t('common.information')}`}</h4>
      {data && (
        <div className="view">
          {data.reserveItem.categoryId !== 'education' ? (
            <dl>
              <dt>{`${t('reserve.request')} ${t('reserve.period')}`}</dt>
              <dd>{`${convertStringToDateFormat(
                data.reserveStartDate,
              )}~${convertStringToDateFormat(data.reserveEndDate)}`}</dd>
            </dl>
          ) : null}
          {data.reserveItem.categoryId !== 'space' ? (
            <dl>
              {data.reserveItem.categoryId === 'education' ? (
                <dt>{`${t('reserve.request')} ${t(
                  'reserve.number_of_people',
                )}`}</dt>
              ) : (
                <dt>{`${t('reserve.request')} ${t('reserve.count')}`}</dt>
              )}
              <dd>{data.reserveQty}</dd>
            </dl>
          ) : null}
          <dl>
            <dt>{`${t('reserve.request')} ${t('reserve.purpose')}`}</dt>
            <dd>{data.reservePurposeContent}</dd>
          </dl>
          <dl className="file">
            <dt>{t('common.attachment')}</dt>
            <dd>
              <AttachList data={attachList} setData={setAttachList} readonly />
            </dd>
          </dl>
        </div>
      )}
    </>
  )
}

export { ReserveInfo }
