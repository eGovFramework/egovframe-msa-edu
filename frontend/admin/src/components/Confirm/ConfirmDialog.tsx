import Button from '@material-ui/core/Button'
import Dialog, { DialogProps } from '@material-ui/core/Dialog'
import DialogActions from '@material-ui/core/DialogActions'
import DialogContent from '@material-ui/core/DialogContent'
import DialogContentText from '@material-ui/core/DialogContentText'
import DialogTitle from '@material-ui/core/DialogTitle'
import React from 'react'
import { useTranslation } from 'react-i18next'

export interface ConfirmDialogProps extends DialogProps {
  title?: string
  contentText?: string
  handleConfirm: () => void
  handleClose: () => void
}

const ConfirmDialog = (props: ConfirmDialogProps) => {
  const { open, handleClose, handleConfirm, title, contentText, ...rest } =
    props

  const { t } = useTranslation()

  return (
    <Dialog
      open={open}
      aria-labelledby="confirm-dialog-title"
      aria-describedby="confirm-dialog-description"
      {...rest}
    >
      {title && <DialogTitle id="confirm-dialog-title">{title}</DialogTitle>}
      {contentText && (
        <DialogContent>
          <DialogContentText id="confirm-dialog-description">
            {contentText}
          </DialogContentText>
        </DialogContent>
      )}
      <DialogActions>
        <Button variant="outlined" onClick={handleClose}>
          {t('label.button.close')}
        </Button>
        <Button variant="outlined" color="secondary" onClick={handleConfirm}>
          {t('label.button.confirm')}
        </Button>
      </DialogActions>
    </Dialog>
  )
}

export { ConfirmDialog }
