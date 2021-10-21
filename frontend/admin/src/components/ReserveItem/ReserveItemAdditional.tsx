import {
  ControlledRadioField,
  ControlledTextField,
} from '@components/ControlledField'
import Card from '@material-ui/core/Card'
import CardActions from '@material-ui/core/CardActions'
import CardContent from '@material-ui/core/CardContent'
import CardHeader from '@material-ui/core/CardHeader'
import Collapse from '@material-ui/core/Collapse'
import Divider from '@material-ui/core/Divider'
import IconButton from '@material-ui/core/IconButton'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import ExpandMoreIcon from '@material-ui/icons/ExpandMore'
import { ICode, ReserveItemFormProps } from '@service'
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

interface ReserveItemAdditionalProps extends ReserveItemFormProps {
  targets: ICode[]
}

const containKeys: string[] = [
  'purpose',
  'address',
  'targetId',
  'excluded',
  'homepage',
  'contact',
]

const ReserveItemAdditional = (props: ReserveItemAdditionalProps) => {
  const { control, formState, targets } = props
  const classes = useStyles()
  const { t, i18n } = useTranslation()
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
            title={t('reserve_item.add_information')}
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
              name="purpose"
              label={t('reserve_item.purpose')}
              defaultValue={''}
              contollerProps={{
                rules: {
                  maxLength: 4000,
                },
              }}
              textFieldProps={{
                required: false,
              }}
            />
            <ControlledTextField
              control={control}
              formState={formState}
              name="address"
              label={t('common.address')}
              defaultValue={''}
              contollerProps={{
                rules: {
                  maxLength: 500,
                },
              }}
              textFieldProps={{
                required: false,
              }}
            />
            <ControlledRadioField
              control={control}
              formState={formState}
              name="targetId"
              label={t('reserve_item.target')}
              defaultValue={'no-limit'}
              requried={true}
              data={{
                idkey: 'codeId',
                namekey: 'codeName',
                data: targets,
              }}
            />
            <ControlledTextField
              control={control}
              formState={formState}
              name="excluded"
              label={t('reserve_item.excluded')}
              defaultValue={''}
              contollerProps={{
                rules: {
                  maxLength: 2000,
                },
              }}
              textFieldProps={{
                required: false,
              }}
            />
            <ControlledTextField
              control={control}
              formState={formState}
              name="homepage"
              label={t('common.home_page_address')}
              defaultValue={''}
              contollerProps={{
                rules: {
                  maxLength: 500,
                },
              }}
              textFieldProps={{
                required: false,
              }}
            />
            <ControlledTextField
              control={control}
              formState={formState}
              name="contact"
              label={t('reserve_item.contact')}
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

export { ReserveItemAdditional }
