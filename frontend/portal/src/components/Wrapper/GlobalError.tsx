import CustomAlert from '@components/CustomAlert'
import { ButtonProps } from '@material-ui/core/Button'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import { errorStateAtom } from '@stores'
import { useSnackbar } from 'notistack'
import React, { useEffect, useState } from 'react'
import { useRecoilState } from 'recoil'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      flexGrow: 1,
    },
    paper: {
      display: 'flex',
      margin: theme.spacing(1),
    },
  }),
)

const customAlertButtonProps: ButtonProps = {
  variant: 'outlined',
  color: 'secondary',
}

const GlobalError = () => {
  const classes = useStyles()
  const [errorState, setErrorState] = useRecoilState(errorStateAtom)
  const { enqueueSnackbar } = useSnackbar()
  const [alertState, setAlertState] = useState<{
    open: boolean
    errors: string[]
  }>({
    open: false,
    errors: [],
  })

  useEffect(() => {
    if (errorState.error) {
      if (errorState.status === 400) {
        const errors = errorState.errors.map(item => {
          return item.defaultMessage
        })

        setAlertState({
          open: true,
          errors,
        })
      } else {
        enqueueSnackbar(errorState.message, {
          variant: 'error',
          onClose: resetError,
        })
      }
    }
  }, [errorState])

  if (!errorState.error) return null

  const resetError = () => {
    setAlertState({
      open: false,
      errors: [],
    })
    setErrorState({
      open: false,
      error: null,
      message: '',
      status: null,
      errors: null,
    })
  }

  return (
    <>
      <CustomAlert
        open={alertState.open}
        handleAlert={resetError}
        title={errorState.message}
        contentText={alertState.errors}
        severity="error"
        classes={classes}
        buttonProps={customAlertButtonProps}
      />
    </>
  )
}

export default GlobalError
