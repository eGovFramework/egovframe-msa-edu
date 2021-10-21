import { ControlledTextField } from '@components/ControlledField'
import DialogPopup from '@components/DialogPopup'
import DisableTextField from '@components/DisableTextField'
import Button from '@material-ui/core/Button'
import Card from '@material-ui/core/Card'
import CardActions from '@material-ui/core/CardActions'
import CardContent from '@material-ui/core/CardContent'
import CardHeader from '@material-ui/core/CardHeader'
import Collapse from '@material-ui/core/Collapse'
import Divider from '@material-ui/core/Divider'
import IconButton from '@material-ui/core/IconButton'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import Typography from '@material-ui/core/Typography'
import ExpandLessIcon from '@material-ui/icons/ExpandLess'
import ExpandMoreIcon from '@material-ui/icons/ExpandMore'
import User from '@pages/user'
import { IReserve, IUser, ReserveFormProps } from '@service'
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
    button: {
      marginLeft: theme.spacing(4),
      padding: theme.spacing(0, 1),
    },
    content: {
      width: '100%',
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      marginBottom: '2rem',
    },
    pos: {
      marginTop: theme.spacing(1),
      marginBottom: '3rem',
    },
  }),
)

interface ReserveClientInfoProps extends ReserveFormProps {
  data: IReserve
}

export interface IUserInfo {
  email: string
  userId: string
  userName: string
}

const ReserveClientInfo = (props: ReserveClientInfoProps) => {
  const { control, formState, data, setValue } = props
  const classes = useStyles()
  const { t } = useTranslation()

  const [expanded, setExpanded] = useState<boolean>(true)
  const [dialogOpen, setDialogOpen] = useState<boolean>(false)
  const [user, setUser] = useState<IUserInfo | null>(null)

  useEffect(() => {
    if (data) {
      setUser({
        email: data.userEmail,
        userId: data.userId,
        userName: data.userName,
      })
    }
  }, [data])

  const handleExpandClick = () => {
    setExpanded(!expanded)
  }

  const handlePopup = (userData: IUser) => {
    if (userData) {
      setUser(userData)
      setValue('userEmail', userData.email, {
        shouldValidate: true,
      })
      setValue('userId', userData.userId)
    }

    handleDialogClose()
  }

  const handleDialogOpen = () => {
    setDialogOpen(true)
  }

  const handleDialogClose = () => {
    setDialogOpen(false)
  }

  return (
    <Card className={classes.root}>
      <CardActions className={classes.header}>
        <CardHeader title={`${t('reserve.user')} ${t('common.information')}`} />
        <IconButton onClick={handleExpandClick}>
          {expanded ? <ExpandLessIcon /> : <ExpandMoreIcon />}
        </IconButton>
      </CardActions>
      <Divider />
      <Collapse in={expanded} timeout="auto" unmountOnExit>
        {user ? (
          <CardContent className={classes.container}>
            <DisableTextField
              label={t('label.title.name')}
              value={
                <>
                  {user.userName}
                  <Button
                    className={classes.button}
                    size="small"
                    variant="contained"
                    color="primary"
                    onClick={handleDialogOpen}
                  >
                    {`${t('reserve.user')} ${t('common.search')}`}
                  </Button>
                </>
              }
              labelProps={{
                xs: 4,
                sm: 2,
              }}
              valueProps={{
                xs: 8,
                sm: 10,
              }}
            />
            <ControlledTextField
              control={control}
              formState={formState}
              name="userContactNo"
              label={t('reserve.phone')}
              defaultValue={''}
              textFieldProps={{
                required: true,
              }}
            />
            <ControlledTextField
              control={control}
              formState={formState}
              name="userEmail"
              label={t('user.email')}
              defaultValue={user?.email || ''}
              textFieldProps={{
                required: true,
              }}
            />
          </CardContent>
        ) : (
          <CardContent className={classes.content}>
            <Typography
              className={classes.pos}
              variant="h5"
              color="textSecondary"
            >
              {t('reserve.msg.find_user')}
            </Typography>
            <Button
              variant="contained"
              color="primary"
              onClick={handleDialogOpen}
            >
              {`${t('reserve.user')} ${t('common.search')}`}
            </Button>
          </CardContent>
        )}
        <DialogPopup
          id="find-dialog"
          handleClose={handleDialogClose}
          open={dialogOpen}
          title={`${t('common.user')} ${t('label.button.find')}`}
        >
          <User handlePopup={handlePopup} />
        </DialogPopup>
      </Collapse>
    </Card>
  )
}

export { ReserveClientInfo }
