import React, { useEffect, useState } from 'react'

import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import Box from '@material-ui/core/Box'
import Button from '@material-ui/core/Button'
import { Color } from '@material-ui/lab/Alert'
import { useTranslation } from 'react-i18next'
import { ConfirmPopover } from '@components/Confirm'
import { useSnackbar } from 'notistack'
import { detailButtonsSnackAtom } from '@stores'
import { useRecoilState } from 'recoil'

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
    backdrop: {
      zIndex: theme.zIndex.drawer + 1,
      color: '#fff',
    },
  }),
)

export interface ISnackProps {
  severity: Color
  message: string
}

export interface IDetailButtonProps {
  handleList?: () => void
  handleSave?: () => void
  saveMessages?: ISnackProps
}

const DetailButtons: React.FC<IDetailButtonProps> = ({
  handleList,
  handleSave,
  saveMessages,
}) => {
  const classes = useStyles()
  const { t } = useTranslation()
  const { enqueueSnackbar } = useSnackbar()

  const [isSuccessSnackBar, setSuccessSnackBar] = useRecoilState(
    detailButtonsSnackAtom,
  )
  const [anchorEl, setAnchorEl] = useState<HTMLButtonElement | null>(null)

  useEffect(() => {
    if (isSuccessSnackBar === 'success') {
      enqueueSnackbar(saveMessages?.message || t('msg.success.save'), {
        variant: saveMessages?.severity || 'success',
      })
    }

    if (isSuccessSnackBar !== 'loading') {
      setAnchorEl(null)
    }
  }, [isSuccessSnackBar])

  useEffect(() => {
    if (anchorEl === null) {
      setSuccessSnackBar('none')
    }
  }, [anchorEl])

  const handlePopover = (target: HTMLButtonElement | null) => {
    setAnchorEl(target)
  }

  return (
    <>
      <Box className={classes.container}>
        {handleList && (
          <Button variant="contained" onClick={handleList} color="default">
            {t('label.button.list')}
          </Button>
        )}
        {handleSave && (
          <div>
            <Button
              variant="contained"
              color="primary"
              onClick={(event: React.MouseEvent<HTMLButtonElement>) => {
                handlePopover(event.currentTarget)
              }}
            >
              {t('label.button.save')}
            </Button>
            <ConfirmPopover
              anchorEl={anchorEl}
              message={t('msg.confirm.save')}
              handleConfirm={handleSave}
              handlePopover={handlePopover}
            />
          </div>
        )}
      </Box>
    </>
  )
}

export { DetailButtons }
