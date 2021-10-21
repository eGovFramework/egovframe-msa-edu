import ValidationAlert from '@components/ValidationAlert'
import { defaultlocales } from '@libs/date'
import { useTranslation } from 'next-i18next'
import React, { useState } from 'react'
import DatePicker from 'react-datepicker'
import 'react-datepicker/dist/react-datepicker.css'
import { Controller } from 'react-hook-form'
import { ReserveEditFormProps } from '.'

interface ReserveDateRangeFieldProps extends ReserveEditFormProps {}

const dateFormat = 'yyyy-MM-dd'

const ReserveDateRangeField = (props: ReserveDateRangeFieldProps) => {
  const { control, formState, required = false } = props
  const { i18n } = useTranslation()
  const [startDate, setStartDate] = useState<Date | null>(null)
  const [endDate, setEndDate] = useState<Date | null>(null)

  return (
    <dl>
      <dt className="import">
        신청일자
        <br className="mb" />
        (신청기간)
      </dt>
      <dd>
        <div className="dateRange">
          <Controller
            control={control}
            name="reserveStartDate"
            render={({ field, fieldState }) => (
              <DatePicker
                selected={startDate}
                onChange={(
                  date: Date,
                  event: React.SyntheticEvent<any> | undefined,
                ) => {
                  setStartDate(date)
                  field.onChange(date)
                }}
                selectsStart
                startDate={startDate}
                endDate={endDate}
                minDate={new Date()}
                dateFormat={dateFormat}
                locale={defaultlocales[i18n.language]}
                className="calendar"
              />
            )}
            rules={{ required: required }}
          />
          <span className="span"> ~ </span>
          <Controller
            control={control}
            name="reserveEndDate"
            render={({ field, fieldState }) => (
              <DatePicker
                selected={endDate}
                onChange={(
                  date: Date,
                  event: React.SyntheticEvent<any> | undefined,
                ) => {
                  setEndDate(date)
                  field.onChange(date)
                }}
                selectsEnd
                startDate={startDate}
                endDate={endDate}
                minDate={startDate}
                dateFormat={dateFormat}
                locale={defaultlocales[i18n.language]}
                className="calendar"
              />
            )}
            rules={{ required: required }}
          />
        </div>
        {formState.errors.reserveStartDate && (
          <ValidationAlert
            fieldError={formState.errors.reserveStartDate}
            label={'신청시작일'}
          />
        )}
        {formState.errors.reserveEndDate && (
          <ValidationAlert
            fieldError={formState.errors.reserveEndDate}
            label={'신청종료일'}
          />
        )}
      </dd>
    </dl>
  )
}

export { ReserveDateRangeField }
