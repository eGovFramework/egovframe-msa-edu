import DisableTextField from '@components/DisableTextField'
import { convertStringToDateFormat } from '@libs/date'
import Button from '@material-ui/core/Button'
import Card from '@material-ui/core/Card'
import CardActions from '@material-ui/core/CardActions'
import CardContent from '@material-ui/core/CardContent'
import CardHeader from '@material-ui/core/CardHeader'
import Collapse from '@material-ui/core/Collapse'
import Divider from '@material-ui/core/Divider'
import Grid from '@material-ui/core/Grid'
import IconButton from '@material-ui/core/IconButton'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import Typography from '@material-ui/core/Typography'
import ExpandLessIcon from '@material-ui/icons/ExpandLess'
import ExpandMoreIcon from '@material-ui/icons/ExpandMore'
import { ICode, IReserveItemRelation } from '@service'
import { useTranslation } from 'next-i18next'
import React, { useState } from 'react'

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
  }),
)

interface ReserveItemInfoProps {
  data: IReserveItemRelation
  handleSearchItem: () => void
  reserveStatus?: ICode
}

const ReserveItemInfo = (props: ReserveItemInfoProps) => {
  const { data, handleSearchItem, reserveStatus } = props
  const classes = useStyles()
  const { t } = useTranslation()
  const [expanded, setExpanded] = useState<boolean>(true)

  const handleExpandClick = () => {
    setExpanded(!expanded)
  }

  return (
    <Card className={classes.root}>
      <CardActions className={classes.header}>
        <CardHeader title={`${t('reserve_item')} ${t('common.information')}`} />
        <IconButton onClick={handleExpandClick}>
          {expanded ? <ExpandLessIcon /> : <ExpandMoreIcon />}
        </IconButton>
      </CardActions>
      <Divider />
      <Collapse in={expanded} timeout="auto" unmountOnExit>
        <CardContent className={classes.container}>
          <Grid container spacing={1}>
            <Grid item xs={12} sm={6}>
              <DisableTextField
                label={t('location')}
                value={data.location.locationName}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <DisableTextField
                label={t('reserve_item.type')}
                value={data.categoryName}
              />
            </Grid>
            <Grid item xs={12} sm={12}>
              <DisableTextField
                label={t('reserve_item.name')}
                value={
                  <>
                    {data.reserveItemName}
                    {reserveStatus ? null : (
                      <Button
                        className={classes.button}
                        size="small"
                        variant="contained"
                        color="primary"
                        onClick={handleSearchItem}
                      >
                        {`${t('reserve_item')} ${t('label.button.change')}`}
                      </Button>
                    )}
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
            </Grid>
            <Grid item xs={12} sm={6}>
              <DisableTextField
                label={
                  data.categoryId === 'education'
                    ? t('reserve.number_of_people')
                    : t('reserve.count')
                }
                value={data.totalQty}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <DisableTextField
                label={t('reserve_item.selection_means')}
                value={data.selectionMeansName}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <DisableTextField
                label={`${t('reserve_item.operation')} ${t('reserve.period')}`}
                value={`${convertStringToDateFormat(
                  data.operationStartDate,
                )} ~ ${convertStringToDateFormat(data.operationEndDate)}`}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <DisableTextField
                label={`${t('reserve_item.request')} ${t('reserve.period')}`}
                value={
                  <Typography variant="body1" noWrap>
                    {`${convertStringToDateFormat(
                      data.requestStartDate,
                      'yyyy-MM-dd HH:mm',
                    )}
                    ~ ${convertStringToDateFormat(
                      data.requestEndDate,
                      'yyyy-MM-dd HH:mm',
                    )}
                    `}
                  </Typography>
                }
              />
            </Grid>
            <Grid item xs={12} sm={12}>
              <DisableTextField
                label={`${t('common.free')} ${t('common.paid')}`}
                value={data.isPaid ? t('common.paid') : t('common.free')}
                labelProps={{
                  xs: 4,
                  sm: 2,
                }}
                valueProps={{
                  xs: 8,
                  sm: 10,
                }}
              />
            </Grid>

            {reserveStatus && (
              <Grid item xs={12}>
                <DisableTextField
                  label={t('reserve.status')}
                  value={
                    reserveStatus.codeId === 'request' ? (
                      <Typography variant="body1" color="error">
                        {reserveStatus.codeName}
                      </Typography>
                    ) : (
                      reserveStatus.codeName
                    )
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
              </Grid>
            )}
          </Grid>
        </CardContent>
      </Collapse>
    </Card>
  )
}

export { ReserveItemInfo }
