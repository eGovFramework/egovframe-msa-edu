import React, { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import DatePicker, { ReactDatePickerProps } from 'react-datepicker'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import TextField from '@material-ui/core/TextField'
import { Box } from '@material-ui/core'
import { Controller, ControllerProps } from 'react-hook-form'
import ValidationAlert from '@components/EditForm/ValidationAlert'
import { convertStringToDate, defaultlocales } from '@libs/date'
import { ControlledFieldProps } from '.'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      display: 'flex',
      '& .react-datepicker-wrapper': {
        width: 'fit-content',
      },
      '& .react-datepicker-popper': {
        zIndex: 3,
      },
    },
    to: {
      display: 'inline-flex',
      alignItems: 'center',
      margin: theme.spacing(2),
    },
  }),
)

interface CustomDataPickerProps extends Omit<ReactDatePickerProps, 'onChange'> {
  name: any
  label: string
  contollerProps?: Omit<
    ControllerProps,
    'control' | 'name' | 'label' | 'render' | 'defaultValue'
  >
}

interface ControlledDateRangePickerProps extends ControlledFieldProps {
  startProps: CustomDataPickerProps
  endProps: CustomDataPickerProps
  required?: boolean
  format?: string
}
const ControlledDateRangePicker = (props: ControlledDateRangePickerProps) => {
  const {
    getValues,
    control,
    formState,
    startProps,
    endProps,
    required = false,
    format = 'yyyy-MM-dd',
  } = props
  const classes = useStyles()
  const { i18n } = useTranslation()
  const [startDate, setStartDate] = useState<Date | null>(null)
  const [endDate, setEndDate] = useState<Date | null>(null)

  useEffect(() => {
    if (getValues) {
      if (getValues(startProps.name)) {
        setStartDate(convertStringToDate(getValues(startProps.name)))
      }
      if (getValues(endProps.name)) {
        setEndDate(convertStringToDate(getValues(endProps.name)))
      }
    }
  }, [props])

  return (
    <>
      <Box className={classes.root}>
        <Controller
          control={control}
          name={startProps.name}
          defaultValue={''}
          render={({ field, fieldState }) => (
            <DatePicker
              customInput={
                <TextField
                  id="outlined-basic"
                  label={startProps.label}
                  variant="outlined"
                  margin="dense"
                  required={required}
                  error={!!formState.errors[startProps.name]}
                />
              }
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
              dateFormat={format}
              locale={defaultlocales[i18n.language]}
              {...startProps}
            />
          )}
          {...startProps.contollerProps}
        />

        <span className={classes.to}>~</span>

        <Controller
          control={control}
          name={endProps.name}
          defaultValue={''}
          render={({ field, fieldState }) => (
            <DatePicker
              customInput={
                <TextField
                  id="outlined-basic"
                  label={endProps.label}
                  variant="outlined"
                  margin="dense"
                  required={required}
                  error={!!formState.errors[endProps.name]}
                />
              }
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
              dateFormat={format}
              locale={defaultlocales[i18n.language]}
              {...endProps}
            />
          )}
          {...endProps.contollerProps}
        />
      </Box>
      {formState.errors[startProps.name] && (
        <ValidationAlert
          fieldError={formState.errors[startProps.name]}
          label={startProps.label}
        />
      )}
      {formState.errors[endProps.name] && (
        <ValidationAlert
          fieldError={formState.errors[endProps.name]}
          label={endProps.label}
        />
      )}
    </>
  )
}

export { ControlledDateRangePicker }
