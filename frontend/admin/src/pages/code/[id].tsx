import { DetailButtons } from '@components/Buttons'
import ValidationAlert from '@components/EditForm/ValidationAlert'
import Card from '@material-ui/core/Card'
import CardContent from '@material-ui/core/CardContent'
import CardHeader from '@material-ui/core/CardHeader'
import Divider from '@material-ui/core/Divider'
import FormControlLabel from '@material-ui/core/FormControlLabel'
import Grid from '@material-ui/core/Grid'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import Switch from '@material-ui/core/Switch'
import TextField from '@material-ui/core/TextField'
import { CodeSavePayload, codeService } from '@service'
import { detailButtonsSnackAtom, errorStateSelector } from '@stores'
import { AxiosError } from 'axios'
import { GetServerSideProps } from 'next'
import { useRouter } from 'next/router'
import React from 'react'
import { Controller, FormProvider, useForm } from 'react-hook-form'
import { useTranslation } from 'react-i18next'
import { useSetRecoilState } from 'recoil'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      flexGrow: 1,
    },
    content: {
      padding: `${theme.spacing(1)}px ${theme.spacing(2)}px`,
    },
    switch: {
      paddingTop: theme.spacing(1),
      paddingBottom: theme.spacing(1),
      paddingLeft: theme.spacing(2),
      paddingRight: theme.spacing(2),
    },
  }),
)

interface ICodeFormInput {
  codeId: string
  codeName: string
  codeDescription: string
  sortSeq: number
  useAt: boolean
}

export interface ICodeItemsProps {
  id: string
  initData: CodeSavePayload | null
}

const CodeItem = ({ id, initData }: ICodeItemsProps) => {
  const classes = useStyles()
  const route = useRouter()
  const { t } = useTranslation()

  //상태관리 hook
  const setErrorState = useSetRecoilState(errorStateSelector)

  //form hook
  const methods = useForm<ICodeFormInput>({
    defaultValues: {
      codeId: initData?.codeId || '',
      codeName: initData?.codeName || '',
      codeDescription: initData?.codeDescription || '',
      sortSeq: initData?.sortSeq || 0,
      useAt: typeof initData?.useAt !== 'undefined' ? initData?.useAt : true,
    },
  })
  const {
    formState: { errors },
    control,
    handleSubmit,
  } = methods

  // 코드ID disabled
  const disabled = id !== '-1'

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
  const onSubmit = async (formData: ICodeFormInput) => {
    setSuccessSnackBar('loading')
    const saved: CodeSavePayload = {
      codeId: formData.codeId,
      codeName: formData.codeName,
      codeDescription: formData.codeDescription,
      useAt: formData.useAt,
      sortSeq: formData.sortSeq,
    }

    if (id === '-1') {
      codeService.save({
        callback: successCallback,
        errorCallback,
        data: saved,
      })
    } else {
      codeService.update({
        id,
        callback: successCallback,
        errorCallback,
        data: saved,
      })
    }
  }

  return (
    <>
      <FormProvider {...methods}>
        <Grid container spacing={1}>
          <Grid item xs={12} sm={6}>
            <Card className={classes.root}>
              <CardHeader title={t('code.title')} />
              <Divider />
              <CardContent className={classes.content}>
                <Controller
                  name="codeId"
                  control={control}
                  rules={{ required: true, maxLength: 20 }}
                  render={({ field }) => (
                    <TextField
                      fullWidth
                      label={t('code.code_id')}
                      name="codeId"
                      required
                      variant="outlined"
                      disabled={disabled}
                      margin="dense"
                      {...field}
                    />
                  )}
                />
                {errors.codeId && (
                  <ValidationAlert
                    fieldError={errors.codeId}
                    target={[20]}
                    label={t('code.code_id')}
                  />
                )}

                <Controller
                  name="codeName"
                  control={control}
                  rules={{ required: true, maxLength: 500 }}
                  render={({ field }) => (
                    <TextField
                      fullWidth
                      label={t('code.code_name')}
                      name="codeName"
                      required
                      variant="outlined"
                      margin="dense"
                      {...field}
                    />
                  )}
                />
                {errors.codeName && (
                  <ValidationAlert
                    fieldError={errors.codeName}
                    target={[500]}
                    label={t('code.code_name')}
                  />
                )}

                <Controller
                  name="codeDescription"
                  control={control}
                  rules={{ required: false, maxLength: 500 }}
                  render={({ field }) => (
                    <TextField
                      fullWidth
                      label={t('code.code_description')}
                      name="codeDescription"
                      variant="outlined"
                      margin="dense"
                      {...field}
                    />
                  )}
                />
                {errors.codeDescription && (
                  <ValidationAlert
                    fieldError={errors.codeDescription}
                    target={[500]}
                    label={t('code.code_description')}
                  />
                )}

                <Controller
                  name="sortSeq"
                  control={control}
                  rules={{ required: false, maxLength: 3 }}
                  render={({ field }) => (
                    <TextField
                      type="number"
                      fullWidth
                      label={t('common.sort_seq')}
                      name="sortSeq"
                      variant="outlined"
                      margin="dense"
                      {...field}
                    />
                  )}
                />
                {errors.sortSeq && (
                  <ValidationAlert
                    fieldError={errors.sortSeq}
                    target={[3]}
                    label={t('common.sort_seq')}
                  />
                )}

                <FormControlLabel
                  label={t('common.use_at')}
                  labelPlacement="start"
                  control={
                    <Controller
                      name="useAt"
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
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      </FormProvider>
      <Grid container spacing={1}>
        <Grid item xs={12} sm={6}>
          <DetailButtons
            handleList={() => {
              route.push('/code')
            }}
            handleSave={handleSubmit(onSubmit)}
          />
        </Grid>
      </Grid>
    </>
  )
}

export const getServerSideProps: GetServerSideProps = async ({ query }) => {
  const { id } = query

  let data = {}

  try {
    if (id !== '-1') {
      const result = await codeService.getOne(id as string)
      if (result) {
        data = (await result.data) as CodeSavePayload
      }
    }
  } catch (error) {
    console.error(`code item query error ${error.message}`)
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
    },
  }
}

export default CodeItem
