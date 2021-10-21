import React from 'react'
import { Controller, ControllerProps } from 'react-hook-form'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import Box from '@material-ui/core/Box'
import FormControlLabel, {
  FormControlLabelProps,
} from '@material-ui/core/FormControlLabel'
import Switch from '@material-ui/core/Switch'
import ValidationAlert from '@components/EditForm/ValidationAlert'
import { ControlledFieldProps } from '.'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      width: '100%',
      justifyContent: 'start',
      border: '1px solid rgba(0, 0, 0, 0.23)',
      borderRadius: theme.spacing(0.5),
      padding: theme.spacing(1),
      margin: theme.spacing(1, 0),
    },
  }),
)

interface ControlledSwitchFieldProps extends ControlledFieldProps {
  label: string
  name: any
  contollerProps?: Omit<
    ControllerProps,
    'control' | 'name' | 'label' | 'render'
  >
  labelProps?: Omit<FormControlLabelProps, 'label' | 'control'>
}
const ControlledSwitchField = (props: ControlledSwitchFieldProps) => {
  const { control, formState, label, name, contollerProps, labelProps } = props
  const classes = useStyles()

  return (
    <Box className={classes.root}>
      <FormControlLabel
        label={label}
        labelPlacement="start"
        control={
          <Controller
            name={name}
            control={control}
            defaultValue={false}
            render={({ field: { onChange, ref, value } }) => (
              <Switch
                inputProps={{ 'aria-label': 'secondary checkbox' }}
                onChange={onChange}
                inputRef={ref}
                checked={value}
              />
            )}
            {...contollerProps}
          />
        }
        {...labelProps}
      />
      {formState.errors[name] && (
        <ValidationAlert fieldError={formState.errors[name]} label={label} />
      )}
    </Box>
  )
}

export { ControlledSwitchField }
