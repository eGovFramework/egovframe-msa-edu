import { DetailButtons } from '@components/Buttons'
import ValidationAlert from '@components/EditForm/ValidationAlert'
import { Card, CardActions, CardContent } from '@material-ui/core'
import Box from '@material-ui/core/Box'
import CardHeader from '@material-ui/core/CardHeader'
import Divider from '@material-ui/core/Divider'
import FormControlLabel from '@material-ui/core/FormControlLabel'
import Grid from '@material-ui/core/Grid'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import Switch from '@material-ui/core/Switch'
import TextField from '@material-ui/core/TextField'
import { ILocation, locationService } from '@service'
import { detailButtonsSnackAtom, errorStateSelector } from '@stores'
import { GetServerSideProps } from 'next'
import { useTranslation } from 'next-i18next'
import { useRouter } from 'next/router'
import React from 'react'
import { Controller, FormProvider, useForm } from 'react-hook-form'
import { useSetRecoilState } from 'recoil'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      flexGrow: 1,
      marginTop: theme.spacing(1),
      '& .MuiOutlinedInput-input': {
        padding: theme.spacing(2),
      },
    },
    card: {
      width: '100%',
    },
    cardActions: {
      justifyContent: 'center',
    },
    switch: {
      width: '100%',
      justifyContent: 'start',
      border: '1px solid rgba(0, 0, 0, 0.23)',
      borderRadius: theme.spacing(0.5),
      padding: theme.spacing(1),
      marginTop: theme.spacing(1),
    },
  }),
)

interface LocationDetailProps {
  locationId: string
  initData?: ILocation
}

const LocationDetail = ({ locationId, initData }: LocationDetailProps) => {
  const classes = useStyles()
  const route = useRouter()
  const { t } = useTranslation()

  //form hook
  const methods = useForm<ILocation>({
    defaultValues: initData,
  })
  const {
    formState: { errors },
    control,
    handleSubmit,
  } = methods

  //상태관리 hook
  const setErrorState = useSetRecoilState(errorStateSelector)
  // <목록, 저장> 버튼 component 상태 전이
  const setSuccessSnackBar = useSetRecoilState(detailButtonsSnackAtom)

  const handleSave = async (formData: ILocation) => {
    setSuccessSnackBar('loading')
    try {
      let result
      if (locationId === '-1') {
        result = await locationService.save(formData)
      } else {
        result = await locationService.update(parseInt(locationId), formData)
      }

      if (result) {
        setSuccessSnackBar('success')

        route.back()
      }
    } catch (error) {
      setSuccessSnackBar('none')
      setErrorState({ error })
    }
  }

  const handleList = () => {
    route.back()
  }

  return (
    <div className={classes.root}>
      <FormProvider {...methods}>
        <form>
          <Grid container spacing={1}>
            <Card className={classes.card}>
              <CardHeader title={t('location')} />
              <Divider />
              <CardContent>
                <Controller
                  name="locationName"
                  control={control}
                  rules={{ required: true, maxLength: 200 }}
                  render={({ field }) => (
                    <TextField
                      fullWidth
                      label={t('location.name')}
                      name="locationName"
                      required
                      variant="outlined"
                      margin="dense"
                      {...field}
                    />
                  )}
                  defaultValue={''}
                />
                {errors.locationName && (
                  <ValidationAlert
                    fieldError={errors.locationName}
                    target={[200]}
                    label={t('location.name')}
                  />
                )}
                <Controller
                  name="sortSeq"
                  control={control}
                  rules={{
                    required: true,
                    maxLength: 3,
                    pattern: {
                      value: /^[0-9]*$/,
                      message: t('valid.valueAsNumber'),
                    },
                  }}
                  render={({ field }) => (
                    <TextField
                      fullWidth
                      label={t('common.sort_seq')}
                      name="sortSeq"
                      required
                      variant="outlined"
                      margin="dense"
                      {...field}
                    />
                  )}
                  defaultValue={null}
                />
                {errors.sortSeq && (
                  <ValidationAlert
                    fieldError={errors.sortSeq}
                    target={[3]}
                    label={t('common.sort_seq')}
                  />
                )}
                <Box className={classes.switch}>
                  <FormControlLabel
                    label={t('common.use_at')}
                    labelPlacement="start"
                    control={
                      <Controller
                        name="isUse"
                        control={control}
                        rules={{ required: false, maxLength: 3 }}
                        render={({ field: { onChange, ref, value } }) => (
                          <Switch
                            inputProps={{ 'aria-label': 'secondary checkbox' }}
                            onChange={onChange}
                            inputRef={ref}
                            checked={value}
                          />
                        )}
                      />
                    }
                  />
                </Box>
              </CardContent>
              <Divider />
              <CardActions className={classes.cardActions}>
                <DetailButtons
                  handleSave={handleSubmit(handleSave)}
                  handleList={handleList}
                />
              </CardActions>
            </Card>
          </Grid>
        </form>
      </FormProvider>
    </div>
  )
}

export const getServerSideProps: GetServerSideProps = async ({ query }) => {
  const locationId = query.id as string

  if (locationId === '-1') {
    return {
      props: {
        locationId,
      },
    }
  }

  let data = {}

  try {
    const result = await locationService.get(parseInt(locationId))

    if (result) {
      data = (await result.data) as ILocation
    }
  } catch (error) {
    console.error(`content item query error ${error.message}`)
    if (error.response?.data?.code === 'E003') {
      return {
        notFound: true,
      }
    }
  }

  return {
    props: {
      locationId,
      initData: data,
    },
  }
}

export default LocationDetail
