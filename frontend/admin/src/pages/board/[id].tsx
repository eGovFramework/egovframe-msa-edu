import { DetailButtons } from '@components/Buttons'
import ValidationAlert from '@components/EditForm/ValidationAlert'
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
  BoardSavePayload,
  boardService,
  codeService,
  ICode,
  SKINT_TYPE_CODE_NORMAL,
} from '@service'
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
    formControl: {
      width: '100%',
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
      justifyBoard: 'center',
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

interface IBoardFormInput {
  boardName: string
  skinTypeCode: string
  titleDisplayLength: number
  postDisplayCount: number
  pageDisplayCount: number
  newDisplayDayCount: number
  editorUseAt: boolean
  userWriteAt: boolean
  commentUseAt: boolean
  uploadUseAt: boolean
  uploadLimitCount: number
  uploadLimitSize: number
}

export interface IBoardItemsProps {
  boardNo: number
  initData: BoardSavePayload | null
  skinTypeCodeList?: ICode[]
}

const BoardItem = ({
  boardNo,
  initData,
  skinTypeCodeList,
}: IBoardItemsProps) => {
  const classes = useStyles()
  const route = useRouter()
  const { t } = useTranslation()

  // 버튼 component 상태 전이
  const setSuccessSnackBar = useSetRecoilState(detailButtonsSnackAtom)

  // 상태관리 hook
  const setErrorState = useSetRecoilState(errorStateSelector)
  const [uploadUseAt, setUploadUseAt] = useState<boolean>(
    initData?.uploadUseAt !== undefined ? initData?.uploadUseAt : false,
  )

  // form hook
  const methods = useForm<IBoardFormInput>({
    defaultValues: {
      boardName: initData?.boardName || '',
      skinTypeCode: initData?.skinTypeCode || SKINT_TYPE_CODE_NORMAL,
      titleDisplayLength: initData?.titleDisplayLength,
      postDisplayCount: initData?.postDisplayCount,
      pageDisplayCount: initData?.pageDisplayCount,
      newDisplayDayCount: initData?.newDisplayDayCount,
      editorUseAt:
        typeof initData?.editorUseAt !== 'undefined'
          ? initData?.editorUseAt
          : false,
      userWriteAt:
        typeof initData?.userWriteAt !== 'undefined'
          ? initData?.userWriteAt
          : false,
      commentUseAt:
        typeof initData?.commentUseAt !== 'undefined'
          ? initData?.commentUseAt
          : false,
      uploadUseAt:
        typeof initData?.uploadUseAt !== 'undefined'
          ? initData?.uploadUseAt
          : false,
      uploadLimitCount: initData?.uploadLimitCount,
      uploadLimitSize: initData?.uploadLimitSize,
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
  const handleSave = async (formData: IBoardFormInput) => {
    setSuccessSnackBar('loading')

    const saved: BoardSavePayload = {
      boardName: formData.boardName,
      skinTypeCode: formData.skinTypeCode,
      titleDisplayLength: formData.titleDisplayLength,
      postDisplayCount: formData.postDisplayCount,
      pageDisplayCount: formData.pageDisplayCount,
      newDisplayDayCount: formData.newDisplayDayCount,
      editorUseAt: formData.editorUseAt,
      userWriteAt: formData.userWriteAt,
      commentUseAt: formData.commentUseAt,
      uploadUseAt: formData.uploadUseAt,
      uploadLimitCount: formData.uploadUseAt ? formData.uploadLimitCount : null,
      uploadLimitSize: formData.uploadUseAt ? formData.uploadLimitSize : null,
    }

    if (boardNo === -1) {
      await boardService.save({
        callback: successCallback,
        errorCallback,
        data: saved,
      })
    } else {
      await boardService.update({
        boardNo,
        callback: successCallback,
        errorCallback,
        data: saved,
      })
    }
  }

  const handleChangeUploadUseAt = event => {
    setUploadUseAt(event.target.checked)
  }

  const getSwitch = (onChange, ref, value) => (
    <Switch
      inputProps={{ 'aria-label': 'secondary checkbox' }}
      onChange={onChange}
      inputRef={ref}
      checked={value}
    />
  )

  return (
    <div className={classes.root}>
      <FormProvider {...methods}>
        <Grid container spacing={1}>
          <Grid item xs={12} sm={6}>
            <Box boxShadow={1}>
              <Controller
                name="boardName"
                control={control}
                rules={{ required: true, maxLength: 100 }}
                render={({ field }) => (
                  <TextField
                    autoFocus
                    label={t('board.board_name')}
                    name="boardName"
                    required
                    inputProps={{ maxLength: 100 }}
                    id="outlined-full-width"
                    placeholder={format(t('msg.placeholder.format'), [
                      t('board.board_name'),
                    ])}
                    fullWidth
                    variant="outlined"
                    {...field}
                  />
                )}
                defaultValue={''}
              />
              {errors.boardName && (
                <ValidationAlert
                  fieldError={errors.boardName}
                  target={[100]}
                  label={t('board.board_name')}
                />
              )}
            </Box>
          </Grid>
          <Grid item xs={12} sm={6}>
            <FormControl variant="outlined" className={classes.formControl}>
              <InputLabel id="skinTypeCode-label" required>
                {t('board.skin_type_code')}
              </InputLabel>
              <Controller
                name="skinTypeCode"
                control={control}
                defaultValue={initData?.skinTypeCode}
                rules={{ required: true }}
                render={({ field }) => (
                  <Select
                    variant="outlined"
                    name="skinTypeCode"
                    required
                    labelId="skinTypeCode-label"
                    label={t('board.skin_type_code')}
                    margin="dense"
                    {...field}
                  >
                    {skinTypeCodeList.map(option => (
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
                name="titleDisplayLength"
                control={control}
                rules={{ required: true, min: 1, max: 99999 }}
                render={({ field }) => (
                  <TextField
                    label={t('board.title_display_length')}
                    name="titleDisplayLength"
                    required
                    type="number"
                    id="outlined-full-width"
                    placeholder={format(t('msg.placeholder.format'), [
                      t('board.title_display_length'),
                    ])}
                    fullWidth
                    variant="outlined"
                    {...field}
                  />
                )}
              />
              {errors.titleDisplayLength && (
                <ValidationAlert
                  fieldError={errors.titleDisplayLength}
                  target={[1, 99999]}
                  label={t('board.title_display_length')}
                />
              )}
            </Box>
          </Grid>
          <Grid item xs={12} sm={6}>
            <Box boxShadow={1}>
              <Controller
                name="postDisplayCount"
                control={control}
                rules={{ required: true, min: 1, max: 99999 }}
                render={({ field }) => (
                  <TextField
                    label={t('board.post_display_count')}
                    name="postDisplayCount"
                    required
                    type="number"
                    id="outlined-full-width"
                    placeholder={format(t('msg.placeholder.format'), [
                      t('board.post_display_count'),
                    ])}
                    fullWidth
                    variant="outlined"
                    {...field}
                  />
                )}
              />
              {errors.postDisplayCount && (
                <ValidationAlert
                  fieldError={errors.postDisplayCount}
                  target={[1, 99999]}
                  label={t('board.post_display_count')}
                />
              )}
            </Box>
          </Grid>

          <Grid item xs={12} sm={6}>
            <Box boxShadow={1}>
              <Controller
                name="pageDisplayCount"
                control={control}
                rules={{ required: true, min: 1, max: 99999 }}
                render={({ field }) => (
                  <TextField
                    label={t('board.page_display_count')}
                    name="pageDisplayCount"
                    required
                    type="number"
                    id="outlined-full-width"
                    placeholder={format(t('msg.placeholder.format'), [
                      t('board.page_display_count'),
                    ])}
                    fullWidth
                    variant="outlined"
                    {...field}
                  />
                )}
              />
              {errors.pageDisplayCount && (
                <ValidationAlert
                  fieldError={errors.pageDisplayCount}
                  target={[1, 99999]}
                  label={t('board.page_display_count')}
                />
              )}
            </Box>
          </Grid>
          <Grid item xs={12} sm={6}>
            <Box boxShadow={1}>
              <Controller
                name="newDisplayDayCount"
                control={control}
                rules={{ required: true, min: 1, max: 99999 }}
                render={({ field }) => (
                  <TextField
                    label={t('board.new_display_day_count')}
                    name="newDisplayDayCount"
                    required
                    type="number"
                    id="outlined-full-width"
                    placeholder={format(t('msg.placeholder.format'), [
                      t('board.new_display_day_count'),
                    ])}
                    fullWidth
                    variant="outlined"
                    {...field}
                  />
                )}
              />
              {errors.newDisplayDayCount && (
                <ValidationAlert
                  fieldError={errors.newDisplayDayCount}
                  target={[1, 99999]}
                  label={t('board.new_display_day_count')}
                />
              )}
            </Box>
          </Grid>

          <Grid item xs={12} sm={6}>
            <Box boxShadow={1} className={classes.switchBox}>
              <FormControlLabel
                label={t('board.editor_use_at')}
                labelPlacement="start"
                control={
                  <Controller
                    name="editorUseAt"
                    control={control}
                    render={({ field: { onChange, ref, value } }) =>
                      getSwitch(onChange, ref, value)
                    }
                  />
                }
              />
            </Box>
          </Grid>
          <Grid item xs={12} sm={6}>
            <Box boxShadow={1} className={classes.switchBox}>
              <FormControlLabel
                label={t('board.user_write_at')}
                labelPlacement="start"
                control={
                  <Controller
                    name="userWriteAt"
                    control={control}
                    render={({ field: { onChange, ref, value } }) =>
                      getSwitch(onChange, ref, value)
                    }
                  />
                }
              />
            </Box>
          </Grid>

          <Grid item xs={12} sm={6}>
            <Box boxShadow={1} className={classes.switchBox}>
              <FormControlLabel
                label={t('board.upload_use_at')}
                labelPlacement="start"
                control={
                  <Controller
                    name="uploadUseAt"
                    control={control}
                    render={({ field: { onChange, ref, value } }) => (
                      <Switch
                        inputProps={{ 'aria-label': 'secondary checkbox' }}
                        onClick={handleChangeUploadUseAt}
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
          <Grid item xs={12} sm={6}>
            <Box boxShadow={1} className={classes.switchBox}>
              <FormControlLabel
                label={t('board.comment_use_at')}
                labelPlacement="start"
                control={
                  <Controller
                    name="commentUseAt"
                    control={control}
                    render={({ field: { onChange, ref, value } }) =>
                      getSwitch(onChange, ref, value)
                    }
                  />
                }
              />
            </Box>
          </Grid>

          <Grid item xs={12} sm={6} hidden={!uploadUseAt}>
            <Box boxShadow={1}>
              <Controller
                name="uploadLimitCount"
                control={control}
                rules={{ required: uploadUseAt, min: 1, max: 99999 }}
                render={({ field }) => (
                  <TextField
                    label={t('board.upload_limit_count')}
                    name="uploadLimitCount"
                    type="number"
                    id="outlined-full-width"
                    placeholder={format(t('msg.placeholder.format'), [
                      t('board.upload_limit_count'),
                    ])}
                    fullWidth
                    variant="outlined"
                    {...field}
                  />
                )}
              />
              {errors.uploadLimitCount && (
                <ValidationAlert
                  fieldError={errors.uploadLimitCount}
                  target={[1, 99999]}
                  label={t('board.upload_limit_count')}
                />
              )}
            </Box>
          </Grid>
          <Grid item xs={12} sm={6} hidden={!uploadUseAt}>
            <Box boxShadow={1}>
              <Controller
                name="uploadLimitSize"
                control={control}
                rules={{
                  required: uploadUseAt,
                  min: 1,
                  max: 99999999999999999999,
                }}
                render={({ field }) => (
                  <TextField
                    label={t('board.upload_limit_size')}
                    name="uploadLimitSize"
                    type="number"
                    id="outlined-full-width"
                    placeholder={format(t('msg.placeholder.format'), [
                      t('board.upload_limit_size'),
                    ])}
                    fullWidth
                    variant="outlined"
                    {...field}
                  />
                )}
              />
              {errors.uploadLimitSize && (
                <ValidationAlert
                  fieldError={errors.uploadLimitSize}
                  target={[1, 99999]}
                  label={t('board.upload_limit_size')}
                />
              )}
            </Box>
          </Grid>
        </Grid>
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
  const boardNo = Number(query.id)

  let data = {}
  let skinTypeCodeList = []

  try {
    const codeList = await codeService.getCodeDetailList('skin_type_code')
    if (codeList) {
      skinTypeCodeList = (await codeList.data) as ICode[]
    }

    if (boardNo !== -1) {
      const result = await boardService.get(boardNo)
      if (result) {
        data = (await result.data) as BoardSavePayload
      }
    }
  } catch (error) {
    console.error(`board item query error ${error.message}`)
    if (error.response?.data?.code === 'E003') {
      return {
        notFound: true,
      }
    }
  }

  return {
    props: {
      boardNo,
      initData: data,
      skinTypeCodeList,
    },
  }
}

export default BoardItem
