import DialogPopup from '@components/DialogPopup'
import Button from '@material-ui/core/Button'
import Card from '@material-ui/core/Card'
import CardContent from '@material-ui/core/CardContent'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import Typography from '@material-ui/core/Typography'
import ReserveItem from '@pages/reserve-item'
import { IReserveItem } from '@service'
import { useRouter } from 'next/router'
import React, { useState } from 'react'
import { useTranslation } from 'react-i18next'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      marginTop: theme.spacing(5),
      backgroundColor: theme.palette.background.default,
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
interface SearchItemProps {}

const SearchItem = (props: SearchItemProps) => {
  const classes = useStyles()
  const { t } = useTranslation()
  const router = useRouter()

  const [dialogOpen, setDialogOpen] = useState<boolean>(false)

  const handlePopup = (data: IReserveItem) => {
    if (data) {
      router.push(`/reserve/-1?reserveItemId=${data.reserveItemId}`)
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
      <CardContent className={classes.content}>
        <Typography className={classes.pos} variant="h5" color="textSecondary">
          {t('reserve.msg.find_item')}
        </Typography>
        <Button variant="contained" color="primary" onClick={handleDialogOpen}>
          {`${t('reserve_item')} ${t('common.search')}`}
        </Button>
        <DialogPopup
          id="find-dialog"
          handleClose={handleDialogClose}
          open={dialogOpen}
          title={`${t('reserve_item')} ${t('label.button.find')}`}
        >
          <ReserveItem handlePopup={handlePopup} />
        </DialogPopup>
      </CardContent>
    </Card>
  )
}

export default SearchItem
