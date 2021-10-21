import { DetailButtons } from '@components/Buttons'
import ValidationAlert from '@components/EditForm/ValidationAlert'
import { FormControl, InputLabel } from '@material-ui/core'
import Box from '@material-ui/core/Box'
import Grid from '@material-ui/core/Grid'
import MenuItem from '@material-ui/core/MenuItem'
import Select from '@material-ui/core/Select'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import TextField from '@material-ui/core/TextField'
import {
  AuthorizationSavePayload,
  authorizationService,
  codeService,
  ICode,
} from '@service'
import { detailButtonsSnackAtom, errorStateSelector } from '@stores'
import { format } from '@utils'
import { AxiosError } from 'axios'
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
    formControl: {
      width: '100%',
    },
    switch: {
      paddingTop: theme.spacing(1),
      paddingBottom: theme.spacing(1),
      paddingLeft: theme.spacing(2),
      paddingRight: theme.spacing(2),
    },
    buttonContainer: {
      display: 'flex',
      margin: theme.spacing(1),
      justifyContent: 'center',
      '& .MuiButton-root': {
        margin: theme.spacing(1),
      },
    },
    backdrop: {
      zIndex: theme.zIndex.drawer + 1,
      color: '#fff',
    },
  }),
)

interface IAuthorizationFormInput {
  authorizationName: string
  urlPatternValue: string
  httpMethodCode: string
  sortSeq: number
}

export interface IAuthorizationItemsProps {
  authorizationNo: string
  initData: AuthorizationSavePayload | null
  httpMethodCodeList: ICode[]
}

const AuthorizationItem = ({
  authorizationNo,
  initData,
  httpMethodCodeList,
}: IAuthorizationItemsProps) => {
  const classes = useStyles()
  const route = useRouter()
  const { t } = useTranslation()

  // 버튼 component 상태 전이
  const setSuccessSnackBar = useSetRecoilState(detailButtonsSnackAtom)

  // 상태관리 hook
  const setErrorState = useSetRecoilState(errorStateSelector)

  // form hook
  const methods = useForm<IAuthorizationFormInput>({
    defaultValues: {
      authorizationName: initData?.authorizationName || '',
      urlPatternValue: initData?.urlPatternValue || '',
      httpMethodCode: initData?.httpMethodCode || 'GET',
      sortSeq: initData?.sortSeq || 0,
    },
  })
  const {
    formState: { errors },
    control,
    handleSubmit,
  } = methods

  const successCallback = () => {
    setSuccessSnackBar('success')

    route.back()
  }

  const errorCallback = (error: AxiosError) => {
    setSuccessSnackBar('none')

    setErrorState({
      error,
    })
  }

  // handleSubmit 저장
  const handleSave = async (formData: IAuthorizationFormInput) => {
    setSuccessSnackBar('loading')

    const saved: AuthorizationSavePayload = {
      authorizationName: formData.authorizationName,
      urlPatternValue: formData.urlPatternValue,
      httpMethodCode: formData.httpMethodCode,
      sortSeq: formData.sortSeq,
    }

    if (authorizationNo === '-1') {
      await authorizationService.save({
        callback: successCallback,
        errorCallback,
        data: saved,
      })
    } else {
      await authorizationService.update({
        authorizationNo,
        callback: successCallback,
        errorCallback,
        data: saved,
      })
    }
  }

  return (
    <div className={classes.root}>
      <FormProvider {...methods}>
        <form>
          <Grid container spacing={1}>
            <Grid item xs={12} sm={12}>
              <Box boxShadow={1}>
                <Controller
                  name="authorizationName"
                  control={control}
                  rules={{ required: true, maxLength: 50 }}
                  render={({ field }) => (
                    <TextField
                      autoFocus
                      label={t('authorization.authorization_name')}
                      name="authorizationName"
                      required
                      inputProps={{ maxLength: 50 }}
                      id="outlined-full-width"
                      placeholder={format(t('msg.placeholder.format'), [
                        t('authorization.authorization_name'),
                      ])}
                      fullWidth
                      variant="outlined"
                      {...field}
                    />
                  )}
                />
                {errors.authorizationName && (
                  <ValidationAlert
                    fieldError={errors.authorizationName}
                    target={[50]}
                    label={t('authorization.authorization_name')}
                  />
                )}
              </Box>
            </Grid>
            <Grid item xs={12} sm={12}>
              <Box boxShadow={1}>
                <Controller
                  name="urlPatternValue"
                  control={control}
                  rules={{ required: true, maxLength: 200 }}
                  render={({ field }) => (
                    <TextField
                      label={t('authorization.url_pattern_value')}
                      name="urlPatternValue"
                      required
                      inputProps={{ maxLength: 200 }}
                      id="outlined-full-width"
                      placeholder={format(t('msg.placeholder.format'), [
                        t('authorization.url_pattern_value'),
                      ])}
                      fullWidth
                      variant="outlined"
                      {...field}
                    />
                  )}
                />
                {errors.urlPatternValue && (
                  <ValidationAlert
                    fieldError={errors.urlPatternValue}
                    target={[200]}
                    label={t('authorization.url_pattern_value')}
                  />
                )}
              </Box>
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControl variant="outlined" className={classes.formControl}>
                <InputLabel id="httpMethodCode-label" required>
                  {t('authorization.http_method_code')}
                </InputLabel>
                <Controller
                  name="httpMethodCode"
                  control={control}
                  defaultValue={initData?.httpMethodCode || 'GET'}
                  rules={{ required: true }}
                  render={({ field }) => (
                    <Select
                      variant="outlined"
                      name="httpMethodCode"
                      required
                      labelId="httpMethodCode-label"
                      label={t('authorization.http_method_code')}
                      margin="dense"
                      {...field}
                    >
                      {httpMethodCodeList.map(option => (
                        <MenuItem key={option.codeId} value={option.codeId}>
                          {option.codeName}
                        </MenuItem>
                      ))}
                    </Select>
                  )}
                />
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6}>
              <Box boxShadow={1}>
                <Controller
                  name="sortSeq"
                  control={control}
                  rules={{ required: true, min: 1, max: 99999 }}
                  render={({ field }) => (
                    <TextField
                      label={t('common.sort_seq')}
                      name="sortSeq"
                      required
                      type="number"
                      id="outlined-full-width"
                      placeholder={format(t('msg.placeholder.format'), [
                        t('common.sort_seq'),
                      ])}
                      fullWidth
                      variant="outlined"
                      {...field}
                    />
                  )}
                />
                {errors.sortSeq && (
                  <ValidationAlert
                    fieldError={errors.sortSeq}
                    target={[1, 99999]}
                    label={t('common.sort_seq')}
                  />
                )}
              </Box>
            </Grid>
          </Grid>
        </form>
      </FormProvider>
      <DetailButtons
        handleList={() => {
          route.back()
        }}
        handleSave={handleSubmit(handleSave)}
      />
    </div>
  )
}

export const getServerSideProps: GetServerSideProps = async ({ query }) => {
  const authorizationNo = query.id

  let data = {}
  let httpMethodCodeList = []

  try {
    const codeList = await codeService.getCodeDetailList('http_method_code')
    if (codeList) {
      httpMethodCodeList = (await codeList.data) as ICode[]
    }

    if (authorizationNo === '-1') {
      const result = await authorizationService.getNextSortSeq()
      if (result) {
        const nextSortSeq = (await result.data) as number
        data = { sortSeq: nextSortSeq }
      }
    } else {
      const result = await authorizationService.get(authorizationNo as string)
      if (result) {
        data = (await result.data) as AuthorizationSavePayload
      }
    }
  } catch (error) {
    console.error(`authorization item query error ${error.message}`)
    if (error.response?.data?.code === 'E003') {
      return {
        notFound: true,
      }
    }
  }

  return {
    props: {
      authorizationNo,
      initData: data,
      httpMethodCodeList,
    },
  }
}

export default AuthorizationItem
