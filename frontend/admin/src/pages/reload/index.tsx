import { Button } from '@material-ui/core'
import Card from '@material-ui/core/Card'
import CardContent from '@material-ui/core/CardContent'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import Typography from '@material-ui/core/Typography'
import Alert, { Color } from '@material-ui/lab/Alert'
import React, { useState } from 'react'

const useStyles = makeStyles((_: Theme) =>
  createStyles({
    alert: {
      margin: _.spacing(1),
    },
    content: {
      width: '100%',
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      marginBottom: '2rem',
    },
  }),
)

type Props = {
  initialLoginStatus: string
}

function Home(props: Props) {
  const classes = useStyles(props)
  const [reloadState, setReloadSteate] = useState<{
    message: string
    severity: Color
  }>({
    message: 'reload message!!',
    severity: 'info',
  })

  const onClickReload = async (event: React.MouseEvent<HTMLButtonElement>) => {
    event.preventDefault()
    fetch('/api/v1/messages')
      .then(async response => {
        const result = await response.json()
        if (response.ok) {
          setReloadSteate({
            message: result.message,
            severity: 'success',
          })
        } else {
          setReloadSteate({
            message: result.message,
            severity: 'error',
          })
        }
      })
      .catch(error => {
        setReloadSteate({
          message: error.message,
          severity: 'error',
        })
      })
  }

  return (
    <Card>
      <CardContent className={classes.content}>
        <Typography variant="h5" component="h2">
          Reload Messages
        </Typography>
        <Alert className={classes.alert} severity={reloadState.severity}>
          {reloadState.message}
        </Alert>
        <Button variant="outlined" color="primary" onClick={onClickReload}>
          Reload
        </Button>
      </CardContent>
    </Card>
  )
}

export default Home
