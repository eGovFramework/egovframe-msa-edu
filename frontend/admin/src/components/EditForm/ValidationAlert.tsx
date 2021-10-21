import React, { useEffect, useState } from 'react'
import { FieldError } from 'react-hook-form'
import { useTranslation } from 'react-i18next'

import Alert, { AlertProps } from '@material-ui/lab/Alert'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'

import { format } from '@utils'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      padding: `0px ${theme.spacing(1)}px`,
    },
  }),
)

export interface ValidaltionAlertProps extends AlertProps {
  message?: string
  fieldError?: FieldError
  target?: any[]
  label?: string
}

const validMessages = {
  required: {
    code: 'valid.required', // 값은 필수입니다.
    isFormat: false,
  },
  min: {
    code: 'valid.between.format', // {0} ~ {1} 사이의 값을 입력해주세요.
    isFormat: true,
  },
  max: {
    code: 'valid.between.format', // {0} ~ {1} 사이의 값을 입력해주세요.
    isFormat: true,
  },
  maxLength: {
    code: 'valid.maxlength.format', // {0}자 이하로 입력해주세요.
    isFormat: true,
  },
  minLength: {
    code: 'valid.minlength.format', // {0}자 이상으로 입력해주세요.
    isFormat: true,
  },
  valueAsNumber: {
    code: 'valid.valueAsNumber', // 숫자만 입력가능합니다.
    isFormat: false,
  },
  valueAsDate: {
    code: 'valid.valueAsDate', // 날짜 형식으로 입력해주세요.
    isFormat: false,
  },
}

const ValidationAlert = (props: ValidaltionAlertProps) => {
  const { message, fieldError, target, label, ...rest } = props
  const classes = useStyles()
  const { t } = useTranslation()
  const [validMessage, setValidMessage] = useState<string>('')

  useEffect(() => {
    if (message) {
      setValidMessage(message)
      return
    }

    if (fieldError.message) {
      setValidMessage(fieldError.message)
      return
    }

    const valid = validMessages[fieldError.type]
    if (valid.isFormat) {
      setValidMessage(format(t(valid.code), target))
      return
    }
    setValidMessage(`${label} ${t(valid.code)}`)
  }, [message, fieldError])

  return (
    <Alert
      className={classes.root}
      severity="error"
      variant="outlined"
      {...rest}
    >
      {validMessage}
    </Alert>
  )
}

export default ValidationAlert
