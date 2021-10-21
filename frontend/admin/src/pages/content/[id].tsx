import { DetailButtons } from '@components/Buttons'
import CustomAlert from '@components/CustomAlert'
import ValidationAlert from '@components/EditForm/ValidationAlert'
import Editor from '@components/Editor'
import Box from '@material-ui/core/Box'
import Grid from '@material-ui/core/Grid'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import TextField from '@material-ui/core/TextField'
import { ContentSavePayload, contentService } from '@service'
import { detailButtonsSnackAtom, errorStateSelector } from '@stores'
import { format } from '@utils'
import { AxiosError } from 'axios'
import { GetServerSideProps } from 'next'
import { useTranslation } from 'next-i18next'
import { useRouter } from 'next/router'
import React, { useState } from 'react'
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

interface IContentFormInput {
  contentName: string
  contentRemark: string
  contentValue: string
}

export interface IContentItemsProps {
  contentNo: string
  initData: ContentSavePayload | null
}

const ContentItem = ({ contentNo, initData }: IContentItemsProps) => {
  const classes = useStyles()
  const route = useRouter()
  const { t } = useTranslation()

  // 버튼 component 상태 전이
  const setSuccessSnackBar = useSetRecoilState(detailButtonsSnackAtom)

  // 상태관리 hook
  const setErrorState = useSetRecoilState(errorStateSelector)

  // alert
  const [customAlert, setCustomAlert] = useState<any>({
    open: false,
    message: '',
    handleAlert: () => setCustomAlert({ open: false }),
  })

  // Editor
  const [contentValue, setContentValue] = useState<string>(
    initData?.contentValue || '',
  )

  // form hook
  const methods = useForm<IContentFormInput>({
    defaultValues: {
      contentName: initData?.contentName || '',
      contentRemark: initData?.contentRemark || '',
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
  const handleSave = async (formData: IContentFormInput) => {
    setSuccessSnackBar('loading')

    const saved: ContentSavePayload = {
      contentName: formData.contentName,
      contentRemark: formData.contentRemark,
      contentValue,
    }

    if (!contentValue) {
      setCustomAlert({
        open: true,
        message: format(t('valid.required.format'), [
          t('content.content_value'),
        ]),
        handleAlert: () => {
          setCustomAlert({ open: false })
        },
      })
      return
    }

    if (contentNo === '-1') {
      await contentService.save({
        callback: successCallback,
        errorCallback,
        data: saved,
      })
    } else {
      await contentService.update({
        contentNo,
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
                  name="contentName"
                  control={control}
                  rules={{ required: true, maxLength: 100 }}
                  render={({ field }) => (
                    <TextField
                      autoFocus
                      label={t('content.content_name')}
                      name="contentName"
                      required
                      inputProps={{ maxLength: 100 }}
                      id="outlined-full-width"
                      placeholder={format(t('msg.placeholder.format'), [
                        t('content.content_name'),
                      ])}
                      fullWidth
                      variant="outlined"
                      {...field}
                    />
                  )}
                />
                {errors.contentName && (
                  <ValidationAlert
                    fieldError={errors.contentName}
                    target={[100]}
                    label={t('content.content_name')}
                  />
                )}
              </Box>
            </Grid>
            <Grid item xs={12} sm={12}>
              <Box boxShadow={1}>
                <Controller
                  name="contentRemark"
                  control={control}
                  rules={{ required: true, maxLength: 200 }}
                  render={({ field }) => (
                    <TextField
                      label={t('content.content_remark')}
                      name="contentRemark"
                      inputProps={{ maxLength: 200 }}
                      id="outlined-full-width"
                      placeholder={format(t('msg.placeholder.format'), [
                        t('content.content_remark'),
                      ])}
                      fullWidth
                      variant="outlined"
                      {...field}
                    />
                  )}
                />
                {errors.contentRemark && (
                  <ValidationAlert
                    fieldError={errors.contentRemark}
                    target={[200]}
                    label={t('content.content_remark')}
                  />
                )}
              </Box>
            </Grid>
          </Grid>
          <Editor contents={contentValue} setContents={setContentValue} />
        </form>
      </FormProvider>
      <DetailButtons
        handleList={() => {
          route.back()
        }}
        handleSave={handleSubmit(handleSave)}
      />
      <CustomAlert
        contentText={customAlert.message}
        open={customAlert.open}
        handleAlert={() => setCustomAlert({ open: false })}
      />
    </div>
  )
}

export const getServerSideProps: GetServerSideProps = async ({ query }) => {
  const contentNo = query.id as string

  let data = {}

  try {
    if (contentNo !== '-1') {
      const result = await contentService.get(contentNo)
      if (result) {
        data = (await result.data) as ContentSavePayload
      }
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
      contentNo,
      initData: data,
    },
  }
}

export default ContentItem
