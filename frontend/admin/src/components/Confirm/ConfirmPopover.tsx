import React from 'react'
import { useTranslation } from 'react-i18next'
import Button from '@material-ui/core/Button'
import Card from '@material-ui/core/Card'
import CardActions from '@material-ui/core/CardActions'
import CardContent from '@material-ui/core/CardContent'
import Popover from '@material-ui/core/Popover'
import Typography from '@material-ui/core/Typography'

export interface ConfirmPopoverProps {
  message: string
  handleConfirm: (event?: React.MouseEvent<HTMLButtonElement>) => void
  handlePopover: (target: Element | null) => void
  anchorEl: Element | null
}

const ConfirmPopover = ({
  message,
  handleConfirm,
  handlePopover,
  anchorEl,
}: ConfirmPopoverProps) => {
  const open = Boolean(anchorEl)
  const popId = open ? 'simple-popover' : undefined

  const { t } = useTranslation()

  return (
    <Popover
      id={popId}
      open={open}
      anchorEl={anchorEl}
      onClose={() => {
        handlePopover(null)
      }}
      anchorOrigin={{
        vertical: 'bottom',
        horizontal: 'center',
      }}
      transformOrigin={{
        vertical: 'top',
        horizontal: 'center',
      }}
    >
      <Card>
        <CardContent>
          <Typography variant="h5">{message}</Typography>
        </CardContent>
        <CardActions>
          <Button
            variant="outlined"
            onClick={() => {
              handlePopover(null)
            }}
          >
            {t('label.button.close')}
          </Button>
          <Button variant="outlined" color="secondary" onClick={handleConfirm}>
            {t('label.button.confirm')}
          </Button>
        </CardActions>
      </Card>
    </Popover>
  )
}

export { ConfirmPopover }
