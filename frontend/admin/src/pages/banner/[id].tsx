import AttachList from '@components/AttachList'
import { DetailButtons } from '@components/Buttons'
import CustomAlert from '@components/CustomAlert'
import ValidationAlert from '@components/EditForm/ValidationAlert'
import { Upload, UploadType } from '@components/Upload'
import Box from '@material-ui/core/Box'
import FormControl from '@material-ui/core/FormControl'
import FormControlLabel from '@material-ui/core/FormControlLabel'
import Grid from '@material-ui/core/Grid'
import InputLabel from '@material-ui/core/InputLabel'
import MenuItem from '@material-ui/core/MenuItem'
import Select from '@material-ui/core/Select'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import Switch from '@material-ui/core/Switch'
import TextField from '@material-ui/core/TextField'
import {
  BannerSavePayload,
  bannerService,
  codeService,
  fileService,
  IAttachmentResponse,
  ICode,
  ISite,
  UploadInfoReqeust,
} from '@service'
import { detailButtonsSnackAtom, errorStateSelector } from '@stores'
import { format } from '@utils'
import { AxiosError } from 'axios'
import { GetServerSideProps } from 'next'
import { useTranslation } from 'next-i18next'
import { useRouter } from 'next/router'
import React, { useCallback, useEffect, useRef, useState } from 'react'
import { Controller, FormProvider, useForm, useWatch } from 'react-hook-form'
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
    switchBox: {
      padding: theme.spacing(1, 0),
    },
    textFieldMultiline: {
      padding: '0 !important',
    },
    buttonContainer: {
      display: 'flex',
      margin: theme.spacing(1),
      justifyBanner: 'center',
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

interface IBannerFormInput {
  siteId: number
  bannerTypeCode: string
  bannerTitle: string
  attachmentCode: string
  urlAddr: string
  newWindowAt: boolean
  bannerContent: string
  sortSeq: number
}

export interface IBannerItemsProps {
  bannerNo: string
  initData: BannerSavePayload | null
  bannerTypeCodeList: ICode[]
  sites: ISite[]
}

const BannerItem = ({
  bannerNo,
  initData,
  bannerTypeCodeList,
  sites,
}: IBannerItemsProps) => {
  const classes = useStyles()
  const route = useRouter()
  const { t } = useTranslation()

  // 버튼 component 상태 전이
  const setSuccessSnackBar = useSetRecoilState(detailButtonsSnackAtom)

  // 상태관리 hook
  const setErrorState = useSetRecoilState(errorStateSelector)
  const uploadRef = useRef<UploadType>()

  const [attachData, setAttachData] = useState<
    IAttachmentResponse[] | undefined
  >(undefined)

  // alert
  const [customAlert, setCustomAlert] = useState<any>({
    open: false,
    message: '',
    handleAlert: () => setCustomAlert({ open: false }),
  })

  // form hook
  const methods = useForm<IBannerFormInput>({
    defaultValues: {
      bannerTypeCode: initData?.bannerTypeCode || '0001',
      bannerTitle: initData?.bannerTitle || '',
      urlAddr: initData?.urlAddr || '',
      newWindowAt:
        typeof initData?.newWindowAt !== 'undefined'
          ? initData?.newWindowAt
          : false,
      bannerContent: initData?.bannerContent || '',
      sortSeq: initData?.sortSeq || 0,
    },
  })
  const {
    formState: { errors },
    control,
    handleSubmit,
    setValue,
  } = methods

  const watchSite = useWatch({
    control,
    name: 'siteId',
  })

  useEffect(() => {
    if (watchSite) {
      bannerService
        .getNextSortSeq(watchSite)
        .then(result => {
          if (result) {
            setValue('sortSeq', result.data, {
              shouldValidate: false,
              shouldDirty: true,
            })
          }
        })
        .catch(error => {
          setErrorState({
            error,
          })
        })
    }
  }, [watchSite])

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

  const getAttachments = useCallback(
    async (code: string) => {
      try {
        const result = await fileService.getAttachmentList(code)

        if (result) {
          setAttachData(result.data)
        }
      } catch (error) {
        setErrorState({
          error,
        })
      }
    },
    [setErrorState],
  )

  useEffect(() => {
    if (initData.attachmentCode) {
      getAttachments(initData.attachmentCode)
    }
  }, [getAttachments, initData.attachmentCode])

  // handleSubmit 저장
  const handleSave = async (formData: IBannerFormInput) => {
    setSuccessSnackBar('loading')

    let { attachmentCode } = initData

    const attachCount = await uploadRef.current.count(attachData)
    if (attachCount === 0) {
      setCustomAlert({
        open: true,
        message: format(t('valid.required.format'), [
          t('banner.attachment_code'),
        ]),
        handleAlert: () => {
          setCustomAlert({ open: false })
        },
      })
      setSuccessSnackBar('none')
      return
    }

    const isUpload = await uploadRef.current.isModified(attachData)
    if (isUpload) {
      const info: UploadInfoReqeust = {
        entityName: 'banner',
        entityId: bannerNo,
      }

      // 업로드 및 저장
      const result = await uploadRef.current.upload(info, attachData)
      if (result) {
        if (result !== 'no attachments' && result !== 'no update list') {
          attachmentCode = result
        }
      }
    }

    const saved: BannerSavePayload = {
      siteId: formData.siteId,
      bannerTypeCode: formData.bannerTypeCode,
      bannerTitle: formData.bannerTitle,
      attachmentCode,
      urlAddr: formData.urlAddr,
      newWindowAt: formData.newWindowAt,
      bannerContent: formData.bannerContent,
      sortSeq: formData.sortSeq,
    }

    try {
      let result
      if (bannerNo === '-1') {
        result = await bannerService.save({
          data: saved,
        })
      } else {
        result = await bannerService.update({
          bannerNo,
          data: saved,
        })
      }
      if (result) {
        successCallback()
      }
    } catch (error) {
      errorCallback(error)
      if (bannerNo === '-1') {
        uploadRef.current?.rollback(attachmentCode)
      }
    }
  }

  return (
    <div className={classes.root}>
      <FormProvider {...methods}>
        <form>
          <Grid container spacing={1}>
            <Grid item xs={12} sm={12}>
              <FormControl variant="outlined" className={classes.formControl}>
                <InputLabel id="banner-site-label" required>
                  {t('menu.site')}
                </InputLabel>
                <Controller
                  name="siteId"
                  control={control}
                  defaultValue={initData?.siteId || sites[0]?.id}
                  rules={{ required: true }}
                  render={({ field }) => (
                    <Select
                      variant="outlined"
                      name="siteId"
                      required
                      labelId="site-label"
                      label={t('menu.site')}
                      margin="dense"
                      {...field}
                    >
                      {sites?.map(option => (
                        <MenuItem key={option.id} value={option.id}>
                          {option.name}
                        </MenuItem>
                      ))}
                    </Select>
                  )}
                />
              </FormControl>
            </Grid>

            <Grid item xs={12} sm={12}>
              <FormControl variant="outlined" className={classes.formControl}>
                <InputLabel id="bannerTypeCode-label" required>
                  {t('banner.banner_type_code')}
                </InputLabel>
                <Controller
                  name="bannerTypeCode"
                  control={control}
                  defaultValue={initData?.bannerTypeCode || '0001'}
                  rules={{ required: true }}
                  render={({ field }) => (
                    <Select
                      variant="outlined"
                      name="bannerTypeCode"
                      required
                      labelId="bannerTypeCode-label"
                      label={t('banner.banner_type_code')}
                      margin="dense"
                      {...field}
                    >
                      {bannerTypeCodeList.map(option => (
                        <MenuItem key={option.codeId} value={option.codeId}>
                          {option.codeName}
                        </MenuItem>
                      ))}
                    </Select>
                  )}
                />
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={12}>
              <Box boxShadow={1}>
                <Controller
                  name="bannerTitle"
                  control={control}
                  rules={{ required: true, maxLength: 100 / 2 }}
                  render={({ field }) => (
                    <TextField
                      label={t('banner.banner_title')}
                      name="bannerTitle"
                      required
                      inputProps={{ maxLength: 100 / 2 }}
                      id="outlined-full-width"
                      placeholder={format(t('msg.placeholder.format'), [
                        t('banner.banner_title'),
                      ])}
                      fullWidth
                      variant="outlined"
                      {...field}
                    />
                  )}
                />
                {errors.bannerTitle && (
                  <ValidationAlert
                    fieldError={errors.bannerTitle}
                    target={[100 / 2]}
                    label={t('banner.banner_title')}
                  />
                )}
              </Box>
            </Grid>
            <Grid item xs={12} sm={12}>
              <Box boxShadow={1}>
                <Upload
                  accept={'image/*'}
                  ref={uploadRef}
                  uploadLimitCount={1}
                  attachmentCode={initData.attachmentCode}
                  attachData={attachData}
                />
                {attachData && (
                  <AttachList data={attachData} setData={setAttachData} />
                )}
              </Box>
            </Grid>
            <Grid item xs={12} sm={9}>
              <Box boxShadow={1}>
                <Controller
                  name="urlAddr"
                  control={control}
                  rules={{ required: true, maxLength: 500 }}
                  render={({ field }) => (
                    <TextField
                      label={t('common.url')}
                      name="urlAddr"
                      inputProps={{ maxLength: 500 }}
                      id="outlined-full-width"
                      placeholder={format(t('msg.placeholder.format'), [
                        t('common.url'),
                      ])}
                      fullWidth
                      variant="outlined"
                      {...field}
                    />
                  )}
                />
                {errors.urlAddr && (
                  <ValidationAlert
                    fieldError={errors.urlAddr}
                    target={[500]}
                    label={t('common.url')}
                  />
                )}
              </Box>
            </Grid>
            <Grid item xs={12} sm={3}>
              <Box boxShadow={1} className={classes.switchBox}>
                <FormControlLabel
                  label={t('banner.new_window_at')}
                  labelPlacement="start"
                  control={
                    <Controller
                      name="newWindowAt"
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
            <Grid item xs={12} sm={12}>
              <Box boxShadow={1}>
                <Controller
                  name="bannerContent"
                  control={control}
                  rules={{ maxLength: 2000 }}
                  render={({ field }) => (
                    <TextField
                      label={t('banner.banner_content')}
                      name="bannerContent"
                      inputProps={{
                        maxLength: 2000,
                        className: classes.textFieldMultiline,
                      }}
                      multiline
                      minRows={10}
                      id="outlined-full-width"
                      placeholder={format(t('msg.placeholder.format'), [
                        t('banner.banner_content'),
                      ])}
                      fullWidth
                      variant="outlined"
                      {...field}
                    />
                  )}
                />
                {errors.bannerContent && (
                  <ValidationAlert
                    fieldError={errors.bannerContent}
                    target={[2000]}
                    label={t('banner.banner_content')}
                  />
                )}
              </Box>
            </Grid>
            <Grid item xs={12} sm={12}>
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
      <CustomAlert
        contentText={customAlert.message}
        open={customAlert.open}
        handleAlert={() => setCustomAlert({ open: false })}
      />
    </div>
  )
}

export const getServerSideProps: GetServerSideProps = async ({ query }) => {
  const bannerNo = query.id as string

  let bannerTypeCodeList = []
  let data = {}
  let sites: ISite[] = []

  try {
    sites = await bannerService.getSites()

    const codeList = await codeService.getCodeDetailList('banner_type_code')
    if (codeList) {
      bannerTypeCodeList = (await codeList.data) as ICode[]
    }

    if (bannerNo === '-1') {
      const result = await bannerService.getNextSortSeq(sites[0].id)
      if (result) {
        const nextSortSeq = (await result.data) as number
        data = { sortSeq: nextSortSeq }
      }
    } else {
      const result = await bannerService.get(bannerNo)
      if (result) {
        data = (await result.data) as BannerSavePayload
      }
    }
  } catch (error) {
    console.error(`banner item query error ${error.message}`)
    if (error.response?.data?.code === 'E003') {
      return {
        notFound: true,
      }
    }
  }

  return {
    props: {
      bannerNo,
      initData: data,
      bannerTypeCodeList,
      sites,
    },
  }
}

export default BannerItem
