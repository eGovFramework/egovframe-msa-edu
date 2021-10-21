import { DetailButtons } from '@components/Buttons'
import ValidationAlert from '@components/EditForm/ValidationAlert'
import Card from '@material-ui/core/Card'
import CardContent from '@material-ui/core/CardContent'
import CardHeader from '@material-ui/core/CardHeader'
import Divider from '@material-ui/core/Divider'
import FormControl from '@material-ui/core/FormControl'
import FormControlLabel from '@material-ui/core/FormControlLabel'
import Grid from '@material-ui/core/Grid'
import InputLabel from '@material-ui/core/InputLabel'
import MenuItem from '@material-ui/core/MenuItem'
import Select from '@material-ui/core/Select'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import Switch from '@material-ui/core/Switch'
import TextField from '@material-ui/core/TextField'
import { CodeSavePayload, codeService, ICode } from '@service'
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
    formControl: {
      marginTop: theme.spacing(0.5),
      paddingTop: theme.spacing(1),
      paddingBottom: theme.spacing(0.5),
      minWidth: 120,
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
  parentCodeId: string
  codeId: string
  codeName: string
  codeDescription: string
  sortSeq: number
  useAt: boolean
}

export interface ICodeItemsProps {
  id: string
  parentCodes: ICode[]
  initData: CodeSavePayload | null
}

const CodeItem = ({ id, parentCodes, initData }: ICodeItemsProps) => {
  const classes = useStyles()
  const route = useRouter()
  const { t } = useTranslation()

  //상태관리 hook
  const setErrorState = useSetRecoilState(errorStateSelector)

  //form hook
  const methods = useForm<ICodeFormInput>({
    defaultValues: {
      parentCodeId: initData?.parentCodeId || '',
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
  const disabled = Object.keys(initData).length > 0

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
      parentCodeId: formData.parentCodeId,
      codeId: formData.codeId,
      codeName: formData.codeName,
      codeDescription: formData.codeDescription,
      useAt: formData.useAt,
      sortSeq: formData.sortSeq,
    }

    if (id === '-1') {
      codeService.saveDetail({
        callback: successCallback,
        errorCallback,
        data: saved,
      })
    } else {
      codeService.updateDetail({
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
        <form>
          <Grid container spacing={1}>
            <Grid item xs={12} sm={6}>
              <Card className={classes.root}>
                <CardHeader title={t('code.title')} />
                <Divider />
                <CardContent className={classes.content}>
                  <FormControl
                    variant="outlined"
                    className={classes.formControl}
                  >
                    <InputLabel id="parentCodeId-label" required>
                      {t('code.code_id')}
                    </InputLabel>
                    <Controller
                      name="parentCodeId"
                      control={control}
                      defaultValue={initData?.parentCodeId || ''}
                      rules={{ required: true }}
                      render={({ field }) => (
                        <Select
                          variant="outlined"
                          name="parentCodeId"
                          required
                          labelId="parentCodeId-label"
                          label={t('code.code_id')}
                          margin="dense"
                          {...field}
                          disabled={disabled}
                        >
                          <MenuItem value="">
                            <em>{t('code.code_id')}</em>
                          </MenuItem>
                          {parentCodes.map(option => (
                            <MenuItem key={option.codeId} value={option.codeId}>
                              {option.codeName}
                            </MenuItem>
                          ))}
                        </Select>
                      )}
                    />
                  </FormControl>

                  <Controller
                    name="codeId"
                    control={control}
                    rules={{ required: true, maxLength: 20 }}
                    render={({ field }) => (
                      <TextField
                        fullWidth
                        label={t('code.code')}
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
                      label={t('code.code')}
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
        </form>
      </FormProvider>
      <Grid container spacing={1}>
        <Grid item xs={12} sm={6}>
          <DetailButtons
            handleList={() => {
              route.push('/code/detail')
            }}
            handleSave={handleSubmit(onSubmit)}
          />
        </Grid>
      </Grid>
    </>
  )
}

export const getServerSideProps: GetServerSideProps = async ({
  req,
  res,
  query,
}) => {
  const { id } = query

  let data = {}
  let parentCodes = []

  try {
    // 신규시에는 사용여부 true인 상위공통코드를 가져오고, 수정시에는 현재 상위공통코드 하나만 가져온다
    if (id === '-1') {
      const codeList = await codeService.getParentCodeList()
      if (codeList) {
        parentCodes = (await codeList.data) as ICode[]
      }
    } else {
      const parentCode = await codeService.getParentCode(id as string)
      if (parentCode) {
        parentCodes.push((await parentCode.data) as ICode[])
      }

      const result = await codeService.getOneDetail(id as string)
      if (result) {
        data = (await result.data) as CodeSavePayload
      }
    }
  } catch (error) {
    console.error(`codes query error ${error.message}`)
    if (error.response?.data?.code === 'E003') {
      return {
        notFound: true,
      }
    }
  }

  return {
    props: {
      id,
      parentCodes,
      initData: data,
    },
  }
}

export default CodeItem
