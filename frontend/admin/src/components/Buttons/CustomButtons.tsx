import React, { useCallback, useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useSnackbar } from 'notistack'
import { useRecoilState } from 'recoil'

import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import Box from '@material-ui/core/Box'
import Button, { ButtonProps } from '@material-ui/core/Button'
import Card from '@material-ui/core/Card'
import CardActions from '@material-ui/core/CardActions'
import CardContent from '@material-ui/core/CardContent'
import Popover from '@material-ui/core/Popover'
import Typography from '@material-ui/core/Typography'

import { detailButtonsSnackAtom } from '@stores'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      flexGrow: 1,
    },
    container: {
      display: 'flex',
      margin: theme.spacing(1),
      justifyContent: 'center',
      '& .MuiButton-root': {
        margin: theme.spacing(1),
      },
    },
    containerLeft: {
      display: 'flex',
      float: 'left',
      margin: theme.spacing(1, 0),
      justifyContent: 'left',
      '& .MuiButton-root': {
        margin: theme.spacing(1),
      },
    },
    containerRight: {
      display: 'flex',
      float: 'right',
      margin: theme.spacing(1, 0),
      justifyContent: 'right',
      '& .MuiButton-root': {
        margin: theme.spacing(1),
      },
    },
    backdrop: {
      zIndex: theme.zIndex.drawer + 1,
      color: '#fff',
    },
    mg0: {
      margin: theme.spacing(0),
    },
  }),
)

export interface IButtonProps extends ButtonProps {
  label: string
  confirmMessage?: string
  validate?: (row?: any) => boolean
  handleButton: (row?: any) => void
  completeMessage?: string
}

export interface ICustomButtonProps {
  buttons: IButtonProps[]
  row?: any
  className?: string
}

const CustomButtons: React.FC<ICustomButtonProps> = ({
  buttons,
  row,
  className,
}) => {
  const classes = useStyles()

  const topBoxClass =
    typeof className !== 'undefined' ? classes[className] : classes.container

  const { t } = useTranslation()
  const { enqueueSnackbar } = useSnackbar()

  const [isSuccessSnackBar, setSuccessSnackBar] = useRecoilState(
    detailButtonsSnackAtom,
  )

  const [buttonId, setButtonId] = useState<string>(null)
  const [message, setMessage] = useState<string>(null)
  const [anchorEl, setAnchorEl] = useState<HTMLButtonElement | null>(null)
  const messageOpen = Boolean(anchorEl)
  const messagePopId = messageOpen ? 'simple-popover' : undefined

  const findButton = useCallback(
    (id: string) => {
      if (id) {
        const buttonIndex = parseInt(id.replace('customButton', ''), 10)
        return buttons[buttonIndex]
      }
      return null
    },
    [buttons],
  )

  useEffect(() => {
    if (isSuccessSnackBar === 'success') {
      const button = findButton(buttonId)
      if (button?.completeMessage) {
        enqueueSnackbar(button.completeMessage || t('msg.success.save'), {
          variant: 'success',
        })
      }
      setSuccessSnackBar('none')
    }
  }, [
    buttonId,
    enqueueSnackbar,
    findButton,
    isSuccessSnackBar,
    setSuccessSnackBar,
    t,
  ])

  const handlePopover = useCallback(
    (target: HTMLButtonElement | null) => {
      if (target?.id) {
        const button = findButton(target?.id)

        if (button) {
          setMessage(button.confirmMessage)
          setAnchorEl(target)
        }
      }
    },
    [findButton],
  )

  const handleClick = (target: HTMLButtonElement | null) => {
    setButtonId(target?.id)

    if (target?.id) {
      const button = findButton(target?.id)

      if (button) {
        if (button.validate && !button.validate(row)) return

        if (button.confirmMessage) {
          handlePopover(target)
        } else {
          button.handleButton(row)
        }
      }
    }
  }

  const handleButton = () => {
    const button = findButton(anchorEl?.id)
    if (button) button.handleButton(row)
    setAnchorEl(null)
  }

  return (
    <>
      <Box className={topBoxClass}>
        {buttons &&
          buttons.map((button, index) => {
            const {
              label,
              confirmMessage,
              validate,
              handleButton,
              completeMessage,
              ...rest
            } = button
            return (
              <Button
                key={`customButton${index.toString()}`}
                id={`customButton${index.toString()}`}
                onClick={(event: React.MouseEvent<HTMLButtonElement>) => {
                  handleClick(event.currentTarget)
                }}
                {...rest}
              >
                {label}
              </Button>
            )
          })}
        <Popover
          id={messagePopId}
          open={messageOpen}
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
                  setAnchorEl(null)
                }}
              >
                {t('label.button.close')}
              </Button>
              <Button
                variant="outlined"
                color="secondary"
                onClick={handleButton}
              >
                {t('label.button.confirm')}
              </Button>
            </CardActions>
          </Card>
        </Popover>
      </Box>
    </>
  )
}

export { CustomButtons }
