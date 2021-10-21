import React from 'react'
import { Controller, ControllerProps } from 'react-hook-form'
import { useTranslation } from 'react-i18next'
import ValidationAlert from '@components/EditForm/ValidationAlert'
import TextField, { TextFieldProps } from '@material-ui/core/TextField'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import { ControlledFieldProps } from '.'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      flexGrow: 1,
    },
    content: {
      display: 'flex',
      alignItems: 'center',
    },
  }),
)

interface ControlledTextFieldProps extends ControlledFieldProps {
  name: any
  label: string
  defaultValue: string | number
  isSelect?: boolean
  children?: React.ReactNode
  textFieldProps?: TextFieldProps
  help?: string | React.ReactNode
  contollerProps?: Omit<
    ControllerProps,
    'control' | 'name' | 'label' | 'render' | 'defaultValue'
  >
}

const ControlledTextField = (props: ControlledTextFieldProps) => {
  const {
    control,
    formState,
    name,
    label,
    defaultValue,
    isSelect = false,
    children,
    textFieldProps,
    help,
    contollerProps,
  } = props
  const { t } = useTranslation()
  const classes = useStyles()

  return (
    <div className={classes.root}>
      <div className={classes.content}>
        <Controller
          name={name}
          control={control}
          render={({ field, fieldState }) => (
            <TextField
              fullWidth
              select={isSelect}
              label={label}
              required
              variant="outlined"
              margin="dense"
              inputRef={field.ref}
              value={field.value}
              error={!!fieldState.error}
              {...field}
              {...textFieldProps}
            >
              {isSelect && children}
            </TextField>
          )}
          defaultValue={defaultValue}
          rules={{ required: true, maxLength: 100 }}
          {...contollerProps}
        />
        {help && help}
      </div>
      {formState.errors[name] && (
        <ValidationAlert
          fieldError={formState.errors[name]}
          target={[100]}
          label={label}
        />
      )}
    </div>
  )
}

export { ControlledTextField }
