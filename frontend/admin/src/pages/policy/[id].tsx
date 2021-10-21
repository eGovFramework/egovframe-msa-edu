import { DetailButtons } from '@components/Buttons'
import ValidationAlert from '@components/EditForm/ValidationAlert'
import Editor from '@components/Editor'
import { getCurrentDate } from '@libs/date'
import Box from '@material-ui/core/Box'
import Grid from '@material-ui/core/Grid'
import MenuItem from '@material-ui/core/MenuItem'
import Paper from '@material-ui/core/Paper'
import Select from '@material-ui/core/Select'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import Switch from '@material-ui/core/Switch'
import TextField from '@material-ui/core/TextField'
import { PolicySavePayload, policyService } from '@service'
import { detailButtonsSnackAtom, errorStateSelector } from '@stores'
import { AxiosError } from 'axios'
import { GetServerSideProps } from 'next'
import { useRouter } from 'next/router'
import React, { useState } from 'react'
import { Controller, FormProvider, useForm } from 'react-hook-form'
import { useTranslation } from 'react-i18next'
import { useSetRecoilState } from 'recoil'
import { IPolicyType } from '.'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      flexGrow: 1,
      marginTop: theme.spacing(1),
      '& .MuiOutlinedInput-input': {
        padding: theme.spacing(2),
      },
    },
    label: {
      padding: theme.spacing(2),
      textAlign: 'center',
      backgroundColor: theme.palette.background.default,
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

interface IPolicyFormInput {
  policyType: string
  isUse: boolean
  title: string
  contents: string
}

export interface IPolicyItemsProps {
  id: string
  initData: PolicySavePayload | null
  typeList: IPolicyType[]
}

const PolicyItem = ({ id, initData, typeList }: IPolicyItemsProps) => {
  const classes = useStyles()
  const route = useRouter()
  const { t } = useTranslation()

  //상태관리 hook
  const setErrorState = useSetRecoilState(errorStateSelector)

  //Editor contents
  const [policyContents, setPolicyContents] = useState<string>(
    initData?.contents || '',
  )

  //form hook
  const methods = useForm<IPolicyFormInput>({
    defaultValues: {
      policyType: initData?.type || 'TOS',
      isUse: typeof initData?.isUse !== 'undefined' ? initData?.isUse : true,
      title: initData?.title,
    },
  })
  const {
    formState: { errors },
    control,
    handleSubmit,
  } = methods

  // <목록, 저장> 버튼 component 상태 전이
  const setSuccessSnackBar = useSetRecoilState(detailButtonsSnackAtom)

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

  //onsubmit 저장
  const onSubmit = async (formData: IPolicyFormInput) => {
    setSuccessSnackBar('loading')
    const saved: PolicySavePayload = {
      title: formData.title,
      isUse: formData.isUse,
      type: formData.policyType,
      regDate: id === '-1' ? getCurrentDate() : initData.regDate,
      contents: policyContents,
    }

    if (id === '-1') {
      policyService.save({
        callback: successCallback,
        errorCallback,
        data: saved,
      })
    } else {
      policyService.update({
        id,
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
            <Grid item xs={12} sm={2}>
              <Paper className={classes.label}>{t('common.type')}</Paper>
            </Grid>
            <Grid item xs={12} sm={4}>
              <Controller
                name="policyType"
                render={({ field }) => (
                  <Select variant="outlined" fullWidth {...field}>
                    {typeList?.map(option => (
                      <MenuItem key={option.codeId} value={option.codeId}>
                        {option.codeName}
                      </MenuItem>
                    ))}
                  </Select>
                )}
                control={control}
                defaultValue={initData?.type || 'TOS'}
              />
            </Grid>
            <Grid item xs={12} sm={2}>
              <Paper className={classes.label}>{t('common.use_at')}</Paper>
            </Grid>
            <Grid item xs={12} sm={4}>
              <Paper className={classes.switch}>
                <Controller
                  name="isUse"
                  render={({ field: { onChange, ref, value } }) => (
                    <Switch
                      inputProps={{ 'aria-label': 'secondary checkbox' }}
                      onChange={onChange}
                      inputRef={ref}
                      checked={value}
                    />
                  )}
                  control={control}
                />
              </Paper>
            </Grid>
            <Grid item xs={12} sm={2}>
              <Paper className={classes.label}>{t('policy.title')}</Paper>
            </Grid>
            <Grid item xs={12} sm={10}>
              <Box boxShadow={1}>
                <Controller
                  name="title"
                  render={({ field, fieldState }) => (
                    <TextField
                      id="outlined-full-width"
                      placeholder={`${t('policy.title')} ${t(
                        'msg.placeholder',
                      )}`}
                      fullWidth
                      variant="outlined"
                      error={!!fieldState.error}
                      {...field}
                    />
                  )}
                  control={control}
                  rules={{ required: true }}
                />
                {errors.title && (
                  <ValidationAlert
                    fieldError={errors.title}
                    label={t('policy.title')}
                  />
                )}
              </Box>
            </Grid>
          </Grid>
          <Editor contents={policyContents} setContents={setPolicyContents} />
        </form>
      </FormProvider>
      <DetailButtons
        handleList={() => {
          route.push('/policy')
        }}
        handleSave={handleSubmit(onSubmit)}
      />
    </div>
  )
}

export const getServerSideProps: GetServerSideProps = async ({
  req,
  res,
  query,
}) => {
  const { id } = query

  let data = {}
  let typeList = []

  try {
    const typeResult = await policyService.getTypeList()

    if (typeResult) {
      typeList = (await typeResult.data) as IPolicyType[]
    }

    if (id !== '-1') {
      const result = await policyService.getOne(id as string)
      if (result) {
        data = (await result.data) as PolicySavePayload
      }
    }
  } catch (error) {
    console.error(`policy item query error ${error.message}`)
    if (error.response?.data?.code === 'E003') {
      return {
        notFound: true,
      }
    }
  }

  return {
    props: {
      id,
      initData: data,
      typeList,
    },
  }
}

export default PolicyItem
