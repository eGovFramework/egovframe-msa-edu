import { ControlledTextField } from '@components/ControlledField'
import Card from '@material-ui/core/Card'
import CardActions from '@material-ui/core/CardActions'
import CardContent from '@material-ui/core/CardContent'
import CardHeader from '@material-ui/core/CardHeader'
import Collapse from '@material-ui/core/Collapse'
import Divider from '@material-ui/core/Divider'
import IconButton from '@material-ui/core/IconButton'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import ExpandMoreIcon from '@material-ui/icons/ExpandMore'
import { ReserveItemFormProps } from '@service'
import { useTranslation } from 'next-i18next'
import React, { useEffect, useState } from 'react'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      flexGrow: 1,
      marginBottom: theme.spacing(2),
      '& .MuiInputLabel-outlined': {
        zIndex: 0,
      },
    },
    header: {
      justifyContent: 'space-between',
    },
    container: {
      display: 'flex',
      flexDirection: 'column',
    },
  }),
)

const containKeys: string[] = ['managerDept', 'managerName', 'managerContact']

interface ReserveItemManagerProps extends ReserveItemFormProps {}

const ReserveItemManager = (props: ReserveItemManagerProps) => {
  const { control, formState } = props
  const classes = useStyles()
  const { t } = useTranslation()
  const [expanded, setExpanded] = useState<boolean>(true)

  const [errorText, setErrorText] = useState<string | undefined>(undefined)

  useEffect(() => {
    if (formState.errors) {
      const keys = Object.keys(formState.errors)
      const found = keys.some(r => containKeys.includes(r))
      if (keys.length > 0 && found) {
        setErrorText('입력값이 잘못 되었습니다.')
      } else {
        setErrorText(undefined)
      }
    }
  }, [formState.errors])

  const handleExpandClick = () => {
    setExpanded(!expanded)
  }
  return (
    <>
      <Card className={classes.root}>
        <CardActions className={classes.header}>
          <CardHeader
            title={`${t('reserve_item.manager')} ${t('common.information')}`}
            subheader={errorText && errorText}
            subheaderTypographyProps={{
              color: 'error',
            }}
          />
          <IconButton onClick={handleExpandClick}>
            <ExpandMoreIcon />
          </IconButton>
        </CardActions>
        <Divider />
        <Collapse in={expanded} timeout="auto" unmountOnExit>
          <CardContent className={classes.container}>
            <ControlledTextField
              control={control}
              formState={formState}
              name="managerDept"
              label={`${t('reserve_item.manager')} ${t('reserve_item.dept')}`}
              defaultValue={''}
              contollerProps={{
                rules: {
                  maxLength: 200,
                },
              }}
              textFieldProps={{
                required: false,
              }}
            />
            <ControlledTextField
              control={control}
              formState={formState}
              name="managerName"
              label={`${t('reserve_item.manager')} ${t('label.title.name')}`}
              defaultValue={''}
              contollerProps={{
                rules: {
                  maxLength: 200,
                },
              }}
              textFieldProps={{
                required: false,
              }}
            />
            <ControlledTextField
              control={control}
              formState={formState}
              name="managerContact"
              label={`${t('reserve_item.manager')} ${t('common.contact')}`}
              defaultValue={''}
              contollerProps={{
                rules: {
                  maxLength: 50,
                },
              }}
              textFieldProps={{
                required: false,
              }}
            />
          </CardContent>
        </Collapse>
      </Card>
    </>
  )
}

export { ReserveItemManager }
