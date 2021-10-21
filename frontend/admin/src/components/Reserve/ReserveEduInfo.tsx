import { ControlledTextField } from '@components/ControlledField'
import { IReserve, ReserveFormProps } from '@service'
import React, { useEffect } from 'react'
import { UseFormSetError, useWatch } from 'react-hook-form'
import { useTranslation } from 'react-i18next'

interface ReserveEduInfoProps extends ReserveFormProps {
  totalQty: number
  setError: UseFormSetError<IReserve>
}

const ReserveEduInfo = (props: ReserveEduInfoProps) => {
  const { control, formState, totalQty, setError } = props
  const { t } = useTranslation()

  const watchReserveQty = useWatch({
    control,
    name: 'reserveQty',
  })

  useEffect(() => {
    if (watchReserveQty) {
      if (watchReserveQty > totalQty) {
        setError(
          'reserveQty',
          { message: t('valid.reserve.number_of_people') },
          { shouldFocus: true },
        )
      }
    }
  }, [watchReserveQty])

  return (
    <>
      <ControlledTextField
        control={control}
        formState={formState}
        name="reserveQty"
        label={`${t('reserve.request')} ${t('reserve.number_of_people')}`}
        defaultValue={''}
        textFieldProps={{
          required: true,
          type: 'number',
        }}
      />
      <ControlledTextField
        control={control}
        formState={formState}
        name="reservePurposeContent"
        label={`${t('reserve')} ${t('reserve.purpose')}`}
        defaultValue={''}
        textFieldProps={{
          required: true,
        }}
      />
    </>
  )
}

export { ReserveEduInfo }
