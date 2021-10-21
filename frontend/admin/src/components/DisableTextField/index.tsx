import Grid, { GridProps } from '@material-ui/core/Grid'
import Paper from '@material-ui/core/Paper'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import Typography from '@material-ui/core/Typography'
import React from 'react'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    label: {
      padding: theme.spacing(1),
      textAlign: 'center',
      backgroundColor: theme.palette.background.default,
    },
    text: {
      padding: theme.spacing(1),
      textAlign: 'left',
    },
  }),
)

interface DisableTextFieldProps {
  label: string
  value: string | number | React.ReactNode
  labelProps?: GridProps
  valueProps?: GridProps
}

const DisableTextField = ({
  label,
  value,
  labelProps,
  valueProps,
}: DisableTextFieldProps) => {
  const classes = useStyles()
  return (
    <Grid container spacing={1}>
      <Grid item xs={4} {...labelProps}>
        <Paper className={classes.label}>
          <Typography variant="body1">{label}</Typography>
        </Paper>
      </Grid>
      <Grid item xs={8} {...valueProps}>
        <Paper className={classes.text}>
          {typeof value === 'string' || typeof value === 'number' ? (
            <Typography variant="body1">{value}</Typography>
          ) : (
            value
          )}
        </Paper>
      </Grid>
    </Grid>
  )
}

export default DisableTextField
