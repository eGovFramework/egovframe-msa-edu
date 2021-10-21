import React, { useEffect, useState } from 'react'
import { useTranslation } from 'next-i18next'
import { useWatch } from 'react-hook-form'
import { ICode, ReserveItemFormProps } from '@service'
import {
  ControlledDateRangePicker,
  ControlledRadioField,
  ControlledSwitchField,
  ControlledTextField,
} from '@components/ControlledField'

interface ReserveItemMethodProps extends ReserveItemFormProps {
  reserveMeans: ICode[]
}

const ReserveItemMethod = (props: ReserveItemMethodProps) => {
  const { control, formState, getValues, reserveMeans } = props
  const { t } = useTranslation()
  const [isExternal, setIsExternal] = useState<boolean | null>(null)
  const watchReserveMeans = useWatch({
    control,
    name: 'reserveMeansId',
  })

  const [open, setOpen] = useState<boolean>(false)
  const watchPeriod = useWatch({
    control,
    name: 'isPeriod',
  })

  useEffect(() => {
    setOpen(watchPeriod)
  }, [watchPeriod])

  useEffect(() => {
    if (watchReserveMeans === 'external') {
      setIsExternal(true)
    } else {
      setIsExternal(false)
    }
  }, [watchReserveMeans])

  return (
    <>
      <ControlledRadioField
        control={control}
        formState={formState}
        name="reserveMeansId"
        label={'인터넷 예약 구분'}
        defaultValue={''}
        requried
        data={{
          idkey: 'codeId',
          namekey: 'codeName',
          data: reserveMeans,
        }}
      />
      {Boolean(isExternal) === false && (
        <>
          <ControlledDateRangePicker
            getValues={getValues}
            control={control}
            formState={formState}
            required={true}
            format="yyyy-MM-dd HH:mm"
            startProps={{
              label: '예약신청시작일시',
              name: 'requestStartDate',
              contollerProps: {
                rules: {
                  required: true,
                },
              },
              showTimeSelect: true,
            }}
            endProps={{
              label: '예약신청종료일시',
              name: 'requestEndDate',
              contollerProps: {
                rules: {
                  required: true,
                },
              },
              showTimeSelect: true,
            }}
          />

          <ControlledSwitchField
            control={control}
            formState={formState}
            label={'기간 지정 가능여부'}
            name="isPeriod"
            contollerProps={{
              defaultValue: false,
            }}
          />

          {open && (
            <ControlledTextField
              control={control}
              formState={formState}
              name="periodMaxCount"
              label={'최대예약가능일수'}
              defaultValue={0}
              textFieldProps={{
                required: true,
                type: 'number',
              }}
              contollerProps={{
                rules: {
                  required: true,
                  maxLength: 3,
                  pattern: {
                    value: /^[0-9]*$/,
                    message: t('valid.valueAsNumber'),
                  },
                },
              }}
            />
          )}
        </>
      )}

      {isExternal && (
        <ControlledTextField
          control={control}
          formState={formState}
          name="externalUrl"
          label={'외부링크URL'}
          defaultValue={''}
          textFieldProps={{
            required: true,
          }}
          contollerProps={{
            rules: {
              required: true,
              maxLength: 500,
            },
          }}
        />
      )}
    </>
  )
}

export { ReserveItemMethod }
