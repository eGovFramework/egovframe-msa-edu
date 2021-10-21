import { DetailButtons } from '@components/Buttons'
import CustomAlert from '@components/CustomAlert'
import ValidationAlert from '@components/EditForm/ValidationAlert'
import Editor from '@components/Editor'
import Box from '@material-ui/core/Box'
import FormControlLabel from '@material-ui/core/FormControlLabel'
import Grid from '@material-ui/core/Grid'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import Switch from '@material-ui/core/Switch'
import TextField from '@material-ui/core/TextField'
import { PrivacySavePayload, privacyService } from '@service'
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
    switchBox: {
      padding: theme.spacing(1, 0),
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
      justifyPrivacy: 'center',
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

interface IPrivacyFormInput {
  privacyTitle: string
  privacyContent: string
  useAt: boolean
}

export interface IPrivacyItemsProps {
  privacyNo: string
  initData: PrivacySavePayload | null
}

const PrivacyItem = ({ privacyNo, initData }: IPrivacyItemsProps) => {
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
  const [privacyContent, setPrivacyContent] = useState<string>(
    initData?.privacyContent || '',
  )

  // form hook
  const methods = useForm<IPrivacyFormInput>({
    defaultValues: {
      privacyTitle: initData?.privacyTitle || '',
      useAt: typeof initData?.useAt !== 'undefined' ? initData?.useAt : true,
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
  const handleSave = async (formData: IPrivacyFormInput) => {
    setSuccessSnackBar('loading')

    const saved: PrivacySavePayload = {
      privacyTitle: formData.privacyTitle,
      privacyContent,
      useAt: formData.useAt,
    }

    if (!privacyContent) {
      setCustomAlert({
        open: true,
        message: format(t('valid.required.format'), [
          t('privacy.privacy_content'),
        ]),
        handleAlert: () => {
          setCustomAlert({ open: false })
        },
      })
      return
    }

    if (privacyNo === '-1') {
      await privacyService.save({
        callback: successCallback,
        errorCallback,
        data: saved,
      })
    } else {
      await privacyService.update({
        privacyNo,
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
                  name="privacyTitle"
                  control={control}
                  rules={{ required: true, maxLength: 100 }}
                  render={({ field }) => (
                    <TextField
                      label={t('privacy.privacy_title')}
                      name="privacyTitle"
                      required
                      autoFocus
                      inputProps={{ maxLength: 100 }}
                      id="outlined-full-width"
                      placeholder={format(t('msg.placeholder.format'), [
                        t('privacy.privacy_title'),
                      ])}
                      fullWidth
                      variant="outlined"
                      {...field}
                    />
                  )}
                />
                {errors.privacyTitle && (
                  <ValidationAlert
                    fieldError={errors.privacyTitle}
                    target={[100]}
                    label={t('privacy.privacy_title')}
                  />
                )}
              </Box>
            </Grid>
            <Grid item xs={12} sm={12}>
              <Box boxShadow={1} className={classes.switchBox}>
                <FormControlLabel
                  label={t('common.use_at')}
                  labelPlacement="start"
                  control={
                    <Controller
                      name="useAt"
                      control={control}
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
            </Grid>
          </Grid>
          <Editor contents={privacyContent} setContents={setPrivacyContent} />
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
  const privacyNo = query.id

  let data = {}

  try {
    if (privacyNo !== '-1') {
      const result = await privacyService.get(privacyNo as string)
      if (result) {
        data = (await result.data) as PrivacySavePayload
      }
    }
  } catch (error) {
    console.error(`privacy item query error ${error.message}`)
    if (error.response?.data?.code === 'E003') {
      return {
        notFound: true,
      }
    }
  }

  return {
    props: {
      privacyNo,
      initData: data,
    },
  }
}

export default PrivacyItem
