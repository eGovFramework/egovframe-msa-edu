import { Typography } from '@material-ui/core'
import Dialog, { DialogProps } from '@material-ui/core/Dialog'
import DialogActions, {
  DialogActionsProps,
} from '@material-ui/core/DialogActions'
import DialogContent from '@material-ui/core/DialogContent'
import DialogTitle from '@material-ui/core/DialogTitle'
import IconButton from '@material-ui/core/IconButton'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import CloseIcon from '@material-ui/icons/Close'
import React from 'react'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    close: {
      position: 'absolute',
      color: theme.palette.grey[500],
      right: theme.spacing(1),
      top: theme.spacing(1),
    },
  }),
)

/**
 * 기존의 페이지를 팝업창으로 호출할 경우 사용
 */
export interface PopupProps {
  handlePopup?: (data: any) => void
}

export interface DialogPopupProps extends DialogProps {
  id: string
  children: React.ReactNode
  handleClose: () => void
  title?: string
  action?: {
    props: DialogActionsProps
    children: React.ReactNode
  }
}

const DialogPopup = (props: DialogPopupProps) => {
  const { id, children, handleClose, title, action, ...rest } = props
  const classes = useStyles()

  return (
    <Dialog
      aria-labelledby={id}
      fullWidth
      maxWidth="md"
      onClose={handleClose}
      {...rest}
    >
      <DialogTitle disableTypography id="dialog-title">
        <Typography variant="h3"> {title || 'Popup'}</Typography>
        {handleClose && (
          <IconButton
            className={classes.close}
            aria-label="close"
            onClick={handleClose}
          >
            <CloseIcon />
          </IconButton>
        )}
      </DialogTitle>
      <DialogContent dividers>{children}</DialogContent>
      {action && (
        <DialogActions {...action.props}>{action.children}</DialogActions>
      )}
    </Dialog>
  )
}

export default DialogPopup
