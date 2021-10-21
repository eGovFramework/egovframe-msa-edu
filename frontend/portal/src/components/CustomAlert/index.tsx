import { Typography } from '@material-ui/core'
import Button, { ButtonProps } from '@material-ui/core/Button'
import Dialog, { DialogProps } from '@material-ui/core/Dialog'
import DialogActions from '@material-ui/core/DialogActions'
import DialogContent from '@material-ui/core/DialogContent'
import DialogContentText from '@material-ui/core/DialogContentText'
import DialogTitle from '@material-ui/core/DialogTitle'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import CheckCircleOutlineOutlinedIcon from '@material-ui/icons/CheckCircleOutlineOutlined'
import ErrorOutlineOutlinedIcon from '@material-ui/icons/ErrorOutlineOutlined'
import InfoOutlinedIcon from '@material-ui/icons/InfoOutlined'
import ReportProblemOutlinedIcon from '@material-ui/icons/ReportProblemOutlined'
import { Color } from '@material-ui/lab/Alert'
import React, { useCallback } from 'react'
import { useTranslation } from 'react-i18next'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    icon: {
      position: 'relative',
      top: '0.11em',
      width: theme.typography.h5.fontSize,
      height: theme.typography.h5.fontSize,
    },
  }),
)

export interface CustomAlertPrpps extends DialogProps {
  title?: string
  severity?: Color
  contentText?: string | string[]
  handleAlert: () => void
  buttonText?: string
  buttonProps?: ButtonProps
}

const CustomAlert = (props: CustomAlertPrpps) => {
  const {
    open,
    handleAlert,
    title,
    severity,
    contentText,
    buttonText,
    buttonProps,
    ...rest
  } = props

  const classes = useStyles()

  const { t } = useTranslation()

  const icon = useCallback(() => {
    return severity === 'error' ? (
      <ErrorOutlineOutlinedIcon color="error" className={classes.icon} />
    ) : severity === 'success' ? (
      <CheckCircleOutlineOutlinedIcon className={classes.icon} />
    ) : severity === 'warning' ? (
      <ReportProblemOutlinedIcon className={classes.icon} />
    ) : (
      <InfoOutlinedIcon className={classes.icon} />
    )
  }, [severity])

  return (
    <Dialog
      open={open}
      aria-labelledby="alert-dialog-title"
      aria-describedby="alert-dialog-description"
      {...rest}
    >
      <DialogTitle id="alert-dialog-title" disableTypography={true}>
        <Typography variant="h5">
          {icon()} {title || t('common.noti')}
        </Typography>
      </DialogTitle>
      {contentText && (
        <DialogContent>
          {Array.isArray(contentText) ? (
            contentText.map((value, index) => (
              <DialogContentText
                key={`dialog-${index}`}
                id={`alert-dialog-description-${index}`}
              >
                - {value}
              </DialogContentText>
            ))
          ) : (
            <DialogContentText id="alert-dialog-description">
              {contentText}
            </DialogContentText>
          )}
        </DialogContent>
      )}
      <DialogActions>
        <Button
          variant="outlined"
          onClick={handleAlert}
          color="primary"
          autoFocus
          {...buttonProps}
        >
          {buttonText || t('label.button.confirm')}
        </Button>
      </DialogActions>
    </Dialog>
  )
}

export default CustomAlert
