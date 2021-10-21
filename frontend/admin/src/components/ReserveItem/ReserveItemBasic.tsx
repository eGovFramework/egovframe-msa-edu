import {
  ControlledDateRangePicker,
  ControlledRadioField,
  ControlledSwitchField,
  ControlledTextField,
} from '@components/ControlledField'
import Box from '@material-ui/core/Box'
import Card from '@material-ui/core/Card'
import CardActions from '@material-ui/core/CardActions'
import CardContent from '@material-ui/core/CardContent'
import CardHeader from '@material-ui/core/CardHeader'
import Collapse from '@material-ui/core/Collapse'
import Divider from '@material-ui/core/Divider'
import FormHelperText from '@material-ui/core/FormHelperText'
import Grid from '@material-ui/core/Grid'
import IconButton from '@material-ui/core/IconButton'
import MenuItem from '@material-ui/core/MenuItem'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import Switch from '@material-ui/core/Switch'
import Typography from '@material-ui/core/Typography'
import ExpandMoreIcon from '@material-ui/icons/ExpandMore'
import { ICode, ILocation, IReserveItem, ReserveItemFormProps } from '@service'
import React, { useEffect, useState } from 'react'
import { Controller, useWatch } from 'react-hook-form'
import { useTranslation } from 'react-i18next'
import { ReserveItemMethod } from './ReserveItemMethod'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      flexGrow: 1,
      marginBottom: theme.spacing(2),
    },
    header: {
      justifyContent: 'space-between',
    },
    help: {
      marginLeft: theme.spacing(2),
    },
    container: {
      display: 'flex',
      flexDirection: 'column',
    },
    switch: {
      width: '100%',
      justifyContent: 'start',
      border: '1px solid rgba(0, 0, 0, 0.23)',
      borderRadius: theme.spacing(0.5),
      padding: theme.spacing(1),
      margin: theme.spacing(1, 0),
    },
  }),
)

export interface ReserveItemBasicProps extends ReserveItemFormProps {
  data: IReserveItem
  locations: ILocation[]
  categories: ICode[]
  reserveMethods: ICode[]
  reserveMeans: ICode[]
  selectionMeans: ICode[]
}

const containKeys: string[] = [
  'locationId',
  'categoryId',
  'reserveItemId',
  'totalQty',
  'operationStartDate',
  'operationEndDate',
  'reserveMethodId',
  'reserveMeansId',
  'requestStartDate',
  'requestEndDate',
  'isPeriod',
  'periodMaxCount',
  'externalUrl',
  'selectionMeansId',
  'isFree',
  'usageCost',
  'isUse',
]

const ReserveItemBasic = (props: ReserveItemBasicProps) => {
  const {
    getValues,
    control,
    formState,
    data,
    locations,
    categories,
    reserveMethods,
    reserveMeans,
    selectionMeans,
  } = props
  const classes = useStyles()
  const { t } = useTranslation()
  const [expanded, setExpanded] = useState<boolean>(true)
  const [errorText, setErrorText] = useState<string | undefined>(undefined)
  const [openMethod, setOpenMethod] = useState<boolean>(false)
  const [openCost, setOpenCost] = useState<boolean>(false)

  const watchReserveMethod = useWatch({
    control,
    name: 'reserveMethodId',
  })
  const watchFree = useWatch({
    control,
    name: 'isPaid',
  })
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

  useEffect(() => {
    if (watchReserveMethod === 'internet') {
      setOpenMethod(true)
    } else {
      setOpenMethod(false)
    }
  }, [watchReserveMethod])

  useEffect(() => {
    setOpenCost(watchFree)
  }, [watchFree])

  const handleExpandClick = () => {
    setExpanded(!expanded)
  }

  return (
    <>
      <Card className={classes.root}>
        <CardActions className={classes.header}>
          <CardHeader
            title={`${t('common.basic')} ${t('common.information')}`}
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
              name="locationId"
              label={t('location')}
              isSelect={true}
              defaultValue={1}
              textFieldProps={{
                required: true,
              }}
            >
              {locations.map(value => (
                <MenuItem
                  key={`location-${value.locationId}`}
                  value={value.locationId}
                >
                  {value.locationName}
                </MenuItem>
              ))}
            </ControlledTextField>
            <ControlledTextField
              control={control}
              formState={formState}
              name="categoryId"
              label={t('reserve_item.type')}
              isSelect={true}
              defaultValue={''}
              textFieldProps={{
                required: true,
              }}
            >
              {categories.map(value => (
                <MenuItem key={`category-${value.codeId}`} value={value.codeId}>
                  {value.codeName}
                </MenuItem>
              ))}
            </ControlledTextField>
            <ControlledTextField
              control={control}
              formState={formState}
              name="reserveItemName"
              label={t('reserve_item.name')}
              defaultValue={''}
              textFieldProps={{
                required: true,
              }}
            />
            <ControlledTextField
              control={control}
              formState={formState}
              name="totalQty"
              label={`${t('reserve.count')}/${t('reserve.number_of_people')}`}
              defaultValue={''}
              textFieldProps={{
                fullWidth: false,
                required: true,
              }}
              help={
                <FormHelperText className={classes.help}>
                  * {t('reserve_titem.msg.help_period')}
                </FormHelperText>
              }
            />
            <ControlledDateRangePicker
              getValues={getValues}
              control={control}
              formState={formState}
              required={true}
              startProps={{
                label: `${t('reserve_item.operation')} ${t(
                  'common.start_date',
                )}`,
                name: 'operationStartDate',
                contollerProps: {
                  rules: {
                    required: true,
                  },
                },
              }}
              endProps={{
                label: `${t('reserve_item.operation')} ${t('common.end_date')}`,
                name: 'operationEndDate',
                contollerProps: {
                  rules: {
                    required: true,
                  },
                },
              }}
            />

            <ControlledRadioField
              control={control}
              formState={formState}
              name="reserveMethodId"
              label={t('reserve_item.reserve_method')}
              defaultValue={''}
              requried={true}
              data={{
                idkey: 'codeId',
                namekey: 'codeName',
                data: reserveMethods,
              }}
            />

            {openMethod && (
              <ReserveItemMethod
                control={control}
                formState={formState}
                getValues={getValues}
                reserveMeans={reserveMeans}
              />
            )}
            <ControlledRadioField
              control={control}
              formState={formState}
              name="selectionMeansId"
              label={t('reserve_item.selection_means')}
              defaultValue={''}
              requried
              data={{
                idkey: 'codeId',
                namekey: 'codeName',
                data: selectionMeans,
              }}
            />

            <Box className={classes.switch}>
              <Typography component="div">
                <Grid
                  component="label"
                  container
                  alignItems="center"
                  spacing={1}
                >
                  <Grid item>{t('common.free')}</Grid>
                  <Grid item>
                    <Controller
                      name="isPaid"
                      control={control}
                      defaultValue={false}
                      render={({ field: { onChange, ref, value } }) => (
                        <Switch
                          inputProps={{ 'aria-label': 'secondary checkbox' }}
                          onChange={onChange}
                          inputRef={ref}
                          checked={value}
                        />
                      )}
                    />
                  </Grid>
                  <Grid item>{t('common.paid')}</Grid>
                </Grid>
              </Typography>
            </Box>

            {openCost && (
              <ControlledTextField
                control={control}
                formState={formState}
                name="usageCost"
                label={t('reserve_item.usage_fee')}
                defaultValue={0}
                textFieldProps={{
                  required: true,
                  type: 'number',
                }}
                contollerProps={{
                  rules: {
                    required: true,
                    pattern: {
                      value: /^[0-9]*$/,
                      message: t('valid.valueAsNumber'),
                    },
                  },
                }}
              />
            )}

            <ControlledSwitchField
              control={control}
              formState={formState}
              label={t('common.use_at')}
              name="isUse"
              contollerProps={{
                defaultValue: false,
              }}
            />
          </CardContent>
        </Collapse>
      </Card>
    </>
  )
}

export { ReserveItemBasic }
