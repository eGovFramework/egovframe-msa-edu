import React from 'react'
import FormControl from '@material-ui/core/FormControl'
import FormControlLabel, {
  FormControlLabelProps,
} from '@material-ui/core/FormControlLabel'
import FormLabel from '@material-ui/core/FormLabel'
import Radio from '@material-ui/core/Radio'
import RadioGroup from '@material-ui/core/RadioGroup'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      border: '1px solid rgba(0, 0, 0, 0.23)',
      borderRadius: '4px',
      padding: theme.spacing(0, 1),
      marginBottom: theme.spacing(1),
    },
    label: {
      fontSize: '0.75rem',
    },
  }),
)

export interface IRadioData extends Omit<FormControlLabelProps, 'control'> {}

export interface RadioGroupFieldProps {
  data: IRadioData[]
  label: string
  required?: boolean
  error?: boolean
}

const RadioGroupField = ({
  data,
  label,
  required = false,
  error = false,
}: RadioGroupFieldProps) => {
  const classes = useStyles()
  return (
    <FormControl error={error} component="fieldset" className={classes.root}>
      <FormLabel
        className={classes.label}
        error={error}
        required={required}
        component="legend"
      >
        {label}
      </FormLabel>
      <RadioGroup row aria-label="position" name="position" defaultValue="top">
        {data &&
          data.map((item, index) => (
            <FormControlLabel
              key={`radio-group-${label}-${index}`}
              control={<Radio color="primary" />}
              {...item}
            />
          ))}
      </RadioGroup>
    </FormControl>
  )
}

export default RadioGroupField
