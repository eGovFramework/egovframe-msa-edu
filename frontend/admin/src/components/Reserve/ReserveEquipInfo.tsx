import {
  ControlledDateRangePicker,
  ControlledTextField,
} from '@components/ControlledField'
import { convertStringToDate, convertStringToDateFormat } from '@libs/date'
import FormHelperText from '@material-ui/core/FormHelperText'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import {
  IReserve,
  IReserveItemRelation,
  ReserveFormProps,
  reserveService,
} from '@service'
import { errorStateSelector } from '@stores'
import { format } from '@utils'
import isAfter from 'date-fns/isAfter'
import isBefore from 'date-fns/isBefore'
import React, { useEffect, useState } from 'react'
import { UseFormClearErrors, UseFormSetError, useWatch } from 'react-hook-form'
import { useTranslation } from 'react-i18next'
import { useSetRecoilState } from 'recoil'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    help: {
      marginLeft: theme.spacing(2),
      width: '20em',
    },
  }),
)

interface ReserveEquipInfoProps extends ReserveFormProps {
  item: IReserveItemRelation
  setError: UseFormSetError<IReserve>
  clearErrors: UseFormClearErrors<IReserve>
}

const ReserveEquipInfo = (props: ReserveEquipInfoProps) => {
  const { control, formState, getValues, item, setError, clearErrors } = props
  const classes = useStyles()
  const { t } = useTranslation()
  const setErrorState = useSetRecoilState(errorStateSelector)

  const [inventory, setInventory] = useState<number | null>(0)

  const [compareDate, setCompareDate] = useState<{
    startDate: Date
    endDate: Date
  } | null>(null)

  const watchStartDate = useWatch({
    control,
    name: 'reserveStartDate',
  })

  const watchEndDate = useWatch({
    control,
    name: 'reserveEndDate',
  })

  useEffect(() => {
    if (item) {
      let startDate = item.operationStartDate
      let endDate = item.operationEndDate
      if (item.reserveMeansId === 'realtime') {
        startDate = item.requestStartDate
        endDate = item.requestEndDate
      }
      setCompareDate({
        startDate: convertStringToDate(startDate),
        endDate: convertStringToDate(endDate),
      })
    }
  }, [item])

  useEffect(() => {
    if (watchStartDate && compareDate) {
      if (isBefore(watchStartDate, compareDate.startDate)) {
        setError(
          'reserveStartDate',
          {
            message: format(t('valid.to_be_fast.format'), [
              `${t('reserve.request')} ${t('common.start_date')}`,
              `${t('reserve_item.operation')}/${t('reserve_item.request')} ${t(
                'reserve.period',
              )}`,
            ]),
          },
          { shouldFocus: true },
        )
      } else if (isAfter(watchStartDate, compareDate.endDate)) {
        setError(
          'reserveStartDate',
          {
            message: format(t('valid.to_be_slow.format'), [
              `${t('reserve.request')} ${t('common.start_date')}`,
              `${t('reserve_item.operation')}/${t('reserve_item.request')} ${t(
                'reserve.period',
              )}`,
            ]),
          },
          { shouldFocus: true },
        )
      } else {
        clearErrors('reserveStartDate')
      }
    }
  }, [watchStartDate])

  useEffect(() => {
    if (watchEndDate && compareDate) {
      if (isBefore(watchEndDate, compareDate.startDate)) {
        setError(
          'reserveEndDate',
          {
            message: format(t('valid.to_be_fast.format'), [
              `${t('reserve.request')} ${t('common.end_date')}`,
              `${t('reserve_item.operation')}/${t('reserve_item.request')} ${t(
                'reserve.period',
              )}`,
            ]),
          },
          { shouldFocus: true },
        )
      } else if (isAfter(watchEndDate, compareDate.endDate)) {
        setError(
          'reserveEndDate',
          {
            message: format(t('valid.to_be_slow.format'), [
              `${t('reserve.request')} ${t('common.end_date')}`,
              `${t('reserve_item.operation')}/${t('reserve_item.request')} ${t(
                'reserve.period',
              )}`,
            ]),
          },
          { shouldFocus: true },
        )
      } else {
        clearErrors('reserveEndDate')
      }
    }
  }, [watchEndDate])

  useEffect(() => {
    if (watchStartDate && watchEndDate) {
      if (
        !formState.errors.reserveStartDate &&
        !formState.errors.reserveEndDate
      ) {
        reserveService
          .getInventories(
            item.reserveItemId,
            convertStringToDateFormat(watchStartDate),
            convertStringToDateFormat(watchEndDate),
          )
          .then(result => {
            setInventory(result.data)
          })
          .catch(error => {
            setErrorState({ error })
          })
      }
    }
  }, [watchStartDate, watchEndDate])

  return (
    <>
      <ControlledDateRangePicker
        control={control}
        formState={formState}
        getValues={getValues}
        required={true}
        startProps={{
          label: `${t('reserve.request')} ${t('common.start_date')}`,
          name: 'reserveStartDate',
          contollerProps: {
            rules: {
              required: true,
            },
          },
        }}
        endProps={{
          label: `${t('reserve.request')} ${t('common.end_date')}`,
          name: 'reserveEndDate',
          contollerProps: {
            rules: {
              required: true,
            },
          },
        }}
      />
      <ControlledTextField
        control={control}
        formState={formState}
        name="reserveQty"
        label={`${t('reserve.request')} ${t('reserve.count')}`}
        defaultValue={0}
        textFieldProps={{
          required: true,
          type: 'number',
        }}
        contollerProps={{
          rules: {
            required: true,
            pattern: {
              value: /^[0-9]*$/,
              message: t('valid.valueAsNumber'),
            },
          },
        }}
        help={
          <FormHelperText error className={classes.help}>
            ({t('신청기간내 예약가능 수량')}: {inventory} )
          </FormHelperText>
        }
      />
      <ControlledTextField
        control={control}
        formState={formState}
        name="reservePurposeContent"
        label={`${t('reserve.request')} ${t('reserve.purpose')}`}
        defaultValue={''}
        textFieldProps={{
          required: true,
        }}
      />
    </>
  )
}

export { ReserveEquipInfo }
